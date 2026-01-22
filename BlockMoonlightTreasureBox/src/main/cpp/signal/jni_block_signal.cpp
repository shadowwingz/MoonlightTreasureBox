//
// Created by tangxiaolu on 2021/11/2.
//

/**
 * @file jni_block_signal.cpp
 * @brief ANR监控的JNI接口实现
 *
 * 该文件实现了Java层和C++层之间的JNI接口，用于ANR信号的监控和处理。
 * 主要功能：
 * 1. 注册和注销信号处理器
 * 2. 接收SIGQUIT信号并通知Java层
 * 3. 触发Signal Catcher线程的ANR处理流程
 */

#include <jni.h>
#include <string>
#include <jni.h>
#include "anr_signal.h"
//#include "anr_signal.cpp"

/**
 * @brief 标识信号处理器是否已注册
 * @note 用于防止重复注册
 */
bool is_registered;

/**
 * @brief Java层ANR观察者的全局引用
 * @note 当发生ANR时，会调用该对象的onSystemAnr()方法
 */
jobject system_anr_observed;

/**
 * @brief JNI环境指针
 * @note 保存在注册时，用于后续的JNI回调
 */
JNIEnv *glb_env;

/**
 * @brief 通知Java层发生系统ANR
 *
 * 该函数通过JNI调用Java层观察者的onSystemAnr()方法，
 * 通知应用发生了系统ANR事件。
 *
 * @note 如果glb_env或system_anr_observed为nullptr，该函数会直接返回
 */
static void notify_system_anr(){
    // 检查JNI环境和观察者对象是否有效
    if(glb_env == nullptr || system_anr_observed == nullptr){
        return;
    }

    // 获取观察者对象的类
    jclass obj_class = glb_env->GetObjectClass(system_anr_observed);

    // 获取onSystemAnr方法的ID
    jmethodID getName_method = glb_env->GetMethodID(obj_class, "onSystemAnr", "()V");

    // 调用Java层的onSystemAnr方法
    glb_env->CallVoidMethod(system_anr_observed,getName_method);
}

/**
 * @brief SIGQUIT信号处理函数
 *
 * 当接收到SIGQUIT信号时（表示发生了ANR），该函数会被调用。
 * 执行以下操作：
 * 1. 通知Java层发生了系统ANR
 * 2. 向Signal Catcher线程发送SIGQUIT信号，触发ANR信息的收集
 *
 * @param sig 信号编号（SIGQUIT）
 * @param si 信号信息指针
 * @param uc 用户上下文指针
 *
 * @note 该函数会被注册为SIGQUIT信号的处理器
 */
static void xc_trace_handler(int sig, siginfo_t *si, void *uc)
{
    uint64_t data;

    // 避免未使用参数的编译警告
    (void)sig;
    (void)si;
    (void)uc;

    // 通知Java层发生ANR
    notify_system_anr();

    // 向Signal Catcher线程发送SIGQUIT信号，触发系统ANR处理流程
    xc_trace_send_sigquit();
}



/**
 * @brief JNI方法：注册ANR信号监控
 *
 * 该方法是Java层SystemAnrMonitor.hookSignalCatcher()的本地实现。
 * 功能：
 * 1. 保存JNI环境指针
 * 2. 创建观察者对象的全局引用
 * 3. 注册SIGQUIT信号处理器
 *
 * @param env JNI环境指针
 * @param thiz Java对象的引用
 * @param observed ANR观察者对象，需要实现onSystemAnr()方法
 *
 * @note 该方法只会注册一次，重复调用会被忽略
 * @note 对应的Java方法：com.txl.blockmoonlighttreasurebox.block.SystemAnrMonitor.hookSignalCatcher()
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_txl_blockmoonlighttreasurebox_block_SystemAnrMonitor_hookSignalCatcher(JNIEnv *env,
                                                                                jobject thiz,
                                                                                jobject observed) {
    // 防止重复注册
    if(!is_registered){
        // 保存JNI环境指针
        glb_env = env;
        // 标记为已注册
        is_registered = true;
        // 创建观察者对象的全局引用（确保在后续回调时对象不会被GC回收）
        system_anr_observed = env->NewGlobalRef(observed);
        // 注册SIGQUIT信号处理器
        block_anr_signal_trace_register(xc_trace_handler);
    }
}









/**
 * @brief JNI方法：注销ANR信号监控
 *
 * 该方法是Java层SystemAnrMonitor.unHookSignalCatcher()的本地实现。
 * 功能：
 * 1. 释放观察者对象的全局引用
 * 2. 注销SIGQUIT信号处理器
 * 3. 清理相关状态
 *
 * @param env JNI环境指针
 * @param thiz Java对象的引用
 *
 * @note 对应的Java方法：com.txl.blockmoonlighttreasurebox.block.SystemAnrMonitor.unHookSignalCatcher()
 * @see Java_com_txl_blockmoonlighttreasurebox_block_SystemAnrMonitor_hookSignalCatcher()
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_txl_blockmoonlighttreasurebox_block_SystemAnrMonitor_unHookSignalCatcher(JNIEnv *env,
                                                                                  jobject thiz) {
    // 检查是否已经注册
    if(system_anr_observed != nullptr){
        // 释放观察者对象的全局引用
        env->DeleteGlobalRef(system_anr_observed);
        // 标记为未注册
        is_registered = false;
        // 注销SIGQUIT信号处理器
        block_anr_signal_trace_unregister();
    }
}