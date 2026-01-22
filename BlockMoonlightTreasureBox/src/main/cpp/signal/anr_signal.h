//
// Created by tangxiaolu on 2021/11/2.
//

/**
 * @file anr_signal.h
 * @brief ANR (Application Not Responding) 信号处理模块的头文件
 *
 * 该模块提供了Android系统中ANR信号的捕获和处理功能。
 * 主要通过拦截SIGQUIT信号来检测系统ANR的发生。
 */

#ifndef MOONLIGHTTREASUREBOX_ANR_SIGNAL_H
#define MOONLIGHTTREASUREBOX_ANR_SIGNAL_H 1

#include <stdint.h>
#include <sys/types.h>
#include <signal.h>
#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * @brief 注册ANR信号追踪处理器
 *
 * 该函数会注册一个自定义的SIGQUIT信号处理器，并解除当前线程的SIGQUIT信号屏蔽。
 * 这样当系统发生ANR时，可以捕获到SIGQUIT信号并执行自定义处理逻辑。
 *
 * @param handler 信号处理函数指针，接收三个参数：
 *                - sig: 信号编号
 *                - si: 信号信息指针
 *                - uc: 用户上下文指针
 * @return 成功返回0，失败返回错误码
 *
 * @note 该函数会修改当前线程的信号掩码和SIGQUIT信号的处理动作
 * @see block_anr_signal_trace_unregister()
 */
int block_anr_signal_trace_register(void (*handler)(int, siginfo_t *, void *));

/**
 * @brief 注销ANR信号追踪处理器
 *
 * 恢复之前的SIGQUIT信号处理器和信号掩码设置。
 * 应该在不再需要ANR监控时调用该函数。
 *
 * @see block_anr_signal_trace_register()
 */
void block_anr_signal_trace_unregister(void);

/**
 * @brief 向Signal Catcher线程发送SIGQUIT信号
 *
 * 该函数会查找Android系统中的"Signal Catcher"线程，并向其发送SIGQUIT信号。
 * Signal Catcher是Android专门用于处理ANR的线程。
 * 通过向该线程发送信号，可以触发Java堆栈的打印和ANR信息的收集。
 *
 * @note 该函数会先查找Signal Catcher线程的TID（线程ID）
 * @note 如果Signal Catcher线程未找到或发送失败，该函数会静默处理
 */
void xc_trace_send_sigquit();

#ifdef __cplusplus
}
#endif

/**
 * @brief 当前进程ID
 *
 * 保存在block_anr_signal_trace_register()中初始化，
 * 用于在xc_trace_send_sigquit()中向正确的线程发送信号。
 */
static pid_t xc_common_process_id;

#endif //MOONLIGHTTREASUREBOX_ANR_SIGNAL_H
