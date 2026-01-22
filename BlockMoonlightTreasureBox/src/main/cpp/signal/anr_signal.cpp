//
// Created by tangxiaolu on 2021/11/2.
//

/**
 * @file anr_signal.cpp
 * @brief ANR (Application Not Responding) 信号处理模块的实现文件
 *
 * 该模块实现了ANR信号的捕获和处理机制。
 * 主要功能包括：
 * 1. 查找Android系统中的Signal Catcher线程
 * 2. 注册和注销SIGQUIT信号处理器
 * 3. 向Signal Catcher线程发送SIGQUIT信号以触发ANR信息收集
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <inttypes.h>
#include <errno.h>
#include <signal.h>
#include <sys/syscall.h>
#include <android/log.h>
#include <jni.h>
#include <unistd.h>
#include <pthread.h>
#include <dirent.h>
#include <sys/eventfd.h>
#include <sys/syscall.h>
#include <android/log.h>
#include "xcc_util.h"
#include "anr_signal.h"

// JNI回调方法相关定义
#define XC_TRACE_CALLBACK_METHOD_NAME         "traceCallback"
#define XC_TRACE_CALLBACK_METHOD_SIGNATURE    "(Ljava/lang/String;Ljava/lang/String;)V"

// Signal Catcher线程TID状态定义
#define XC_TRACE_SIGNAL_CATCHER_TID_UNLOAD    (-2)  // 未加载状态
#define XC_TRACE_SIGNAL_CATCHER_TID_UNKNOWN   (-1)  // 未知状态
#define XC_TRACE_SIGNAL_CATCHER_THREAD_NAME   "Signal Catcher"  // Signal Catcher线程名称
#define XC_TRACE_SIGNAL_CATCHER_THREAD_SIGBLK 0x1000  // Signal Catcher线程的信号掩码特征值

/**
 * @brief 标识是否为Android Lollipop版本
 * @note 当前未使用，保留用于后续可能的版本适配
 */
static int                              xc_trace_is_lollipop = 0;

/**
 * @brief Signal Catcher线程的TID（线程ID）
 * @note 初始值为XC_TRACE_SIGNAL_CATCHER_TID_UNLOAD，表示未加载
 */
static pid_t                            xc_trace_signal_catcher_tid = XC_TRACE_SIGNAL_CATCHER_TID_UNLOAD;

/**
 * @brief 保存原始的信号掩码
 * 在注册信号处理器时保存，用于注销时恢复
 */
static sigset_t         xcc_signal_trace_oldset;

/**
 * @brief 保存原始的SIGQUIT信号处理动作
 * 在注册信号处理器时保存，用于注销时恢复
 */
static struct sigaction xcc_signal_trace_oldact;


/**
 * @brief 加载Signal Catcher线程的TID
 *
 * 该函数通过遍历/proc/[pid]/task目录下的所有线程，查找名为"Signal Catcher"的线程。
 * 找到后会进一步验证该线程的SigBlk掩码是否为0x1000，以确保是正确的Signal Catcher线程。
 *
 * @note 查找成功会将TID保存到xc_trace_signal_catcher_tid全局变量中
 * @note 如果查找失败，xc_trace_signal_catcher_tid会被设置为XC_TRACE_SIGNAL_CATCHER_TID_UNKNOWN
 */
static void xc_trace_load_signal_catcher_tid()
{
    char           buf[256];
    DIR           *dir;
    struct dirent *ent;
    FILE          *f;
    pid_t          tid;
    uint64_t       sigblk;

    // 初始化为未知状态
    xc_trace_signal_catcher_tid = XC_TRACE_SIGNAL_CATCHER_TID_UNKNOWN;

    // 打开进程的task目录
    snprintf(buf, sizeof(buf), "/proc/%d/task", xc_common_process_id);
    if(NULL == (dir = opendir(buf))) return;

    // 遍历所有线程
    while(NULL != (ent = readdir(dir)))
    {
        // 获取并检查线程ID
        if(0 != xcc_util_atoi(ent->d_name, &tid)) continue;
        if(tid < 0) continue;

        // 检查线程名称是否为"Signal Catcher"
        xcc_util_get_thread_name(tid, buf, sizeof(buf));
        if(0 != strcmp(buf, XC_TRACE_SIGNAL_CATCHER_THREAD_NAME)) continue;

        // 检查信号阻塞掩码是否为0x1000
        sigblk = 0;
        snprintf(buf, sizeof(buf), "/proc/%d/status", tid);
        if(NULL == (f = fopen(buf, "r"))) break;
        while(fgets(buf, sizeof(buf), f))
        {
            if(1 == sscanf(buf, "SigBlk: %" SCNx64, &sigblk)) break;
        }
        fclose(f);
        if(XC_TRACE_SIGNAL_CATCHER_THREAD_SIGBLK != sigblk) continue;

        // 找到Signal Catcher线程，保存其TID
        xc_trace_signal_catcher_tid = tid;
        break;
    }
    closedir(dir);
}

/**
 * @brief 向Signal Catcher线程发送SIGQUIT信号
 *
 * 如果Signal Catcher线程的TID还未加载，会先调用xc_trace_load_signal_catcher_tid()进行加载。
 * 然后使用tgkill系统调用向Signal Catcher线程发送SIGQUIT信号。
 *
 * @note 该函数会触发Signal Catcher线程执行ANR处理流程，包括打印Java堆栈等
 * @note 如果Signal Catcher线程未找到或TID无效，该函数不会执行任何操作
 */
void xc_trace_send_sigquit()
{
    // 如果TID未加载，先加载
    if(XC_TRACE_SIGNAL_CATCHER_TID_UNLOAD == xc_trace_signal_catcher_tid)
        xc_trace_load_signal_catcher_tid();

    // 如果TID有效，发送SIGQUIT信号
    if(xc_trace_signal_catcher_tid >= 0)
        syscall(SYS_tgkill, xc_common_process_id, xc_trace_signal_catcher_tid, SIGQUIT);
}

/**
 * @brief 注册ANR信号追踪处理器
 *
 * 该函数执行以下操作：
 * 1. 保存当前进程ID到xc_common_process_id全局变量
 * 2. 解除当前线程的SIGQUIT信号屏蔽（确保能接收到SIGQUIT信号）
 * 3. 注册自定义的SIGQUIT信号处理器
 * 4. 保存原始的信号掩码和信号处理动作，以便后续恢复
 *
 * @param handler 自定义的信号处理函数指针
 * @return 成功返回0，失败返回错误码
 *
 * @note 该函数假设当前线程为主线程
 * @note 如果注册失败，会恢复原始的信号掩码
 */
int block_anr_signal_trace_register(void (*handler)(int, siginfo_t *, void *)){

    int              r;
    sigset_t         set;
    struct sigaction act;

    // 获取并保存当前进程ID
    xc_common_process_id = getpid();

    // 解除当前线程的SIGQUIT信号屏蔽（假设当前线程为主线程）
    sigemptyset(&set);
    sigaddset(&set, SIGQUIT);
    if(0 != (r = pthread_sigmask(SIG_UNBLOCK, &set, &xcc_signal_trace_oldset))) return r;

    // 注册新的SIGQUIT信号处理器
    memset(&act, 0, sizeof(act));
    sigfillset(&act.sa_mask);  // 在处理器执行期间阻塞所有信号
    act.sa_sigaction = handler;  // 设置信号处理函数
    act.sa_flags = SA_RESTART | SA_SIGINFO;  // 设置标志：自动重启系统调用 + 传递详细信息
    if(0 != sigaction(SIGQUIT, &act, &xcc_signal_trace_oldact))
    {
        // 如果注册失败，恢复原始的信号掩码
        pthread_sigmask(SIG_SETMASK, &xcc_signal_trace_oldset, NULL);
        return -1;
    }

    return 0;
}

/**
 * @brief 注销ANR信号追踪处理器
 *
 * 该函数恢复之前的信号设置：
 * 1. 恢复原始的信号掩码
 * 2. 恢复原始的SIGQUIT信号处理器
 *
 * @note 应该在不再需要ANR监控时调用该函数
 * @see block_anr_signal_trace_register()
 */
void block_anr_signal_trace_unregister(void){
    // 恢复原始的信号掩码
    pthread_sigmask(SIG_SETMASK, &xcc_signal_trace_oldset, NULL);
    // 恢复原始的SIGQUIT信号处理器
    sigaction(SIGQUIT, &xcc_signal_trace_oldact, NULL);
}





