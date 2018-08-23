package com.ns.greg.socket.internal

import com.ns.greg.library.fasthook.BaseRunnable
import com.ns.greg.library.fasthook.BaseThreadManager
import com.ns.greg.library.fasthook.BaseThreadTask
import com.ns.greg.library.fasthook.ThreadExecutorFactory
import java.util.concurrent.ThreadPoolExecutor

/**
 * @author gregho
 * @since 2018/8/10
 */
internal class SocketHook private constructor() : BaseThreadManager<ThreadPoolExecutor>() {

  companion object {

    // Singleton instance
    val instance: SocketHook by lazy {
      SocketHook()
    }
  }

  init {
    setLog(false)
  }

  override fun createBaseThreadTask(job: BaseRunnable<*>?): BaseThreadTask {
    return BaseThreadTask(job)
  }

  override fun createThreadPool(): ThreadPoolExecutor {
    return ThreadExecutorFactory.newCoreSizeThreadPool()
  }
}