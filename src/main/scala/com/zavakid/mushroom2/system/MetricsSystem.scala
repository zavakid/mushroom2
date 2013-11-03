package com.zavakid.mushroom2.system

import com.zavakid.mushroom2.MetricProvider
import com.zavakid.mushroom2.MetricsSink
import com.zavakid.mushroom2.Compentent
import scala.concurrent.Lock
import com.zavakid.mushroom2.LifeCycle

/**
 * @author zavakid 2013年11月2日 下午7:57:50
 */
trait MetricsSystem {
  /**
   * 注册一个组件。比如 MetricProvider 或者 MetricsSink
   * 如果存在的话，会返回原来老的组件
   */
  def register[T <: Compentent](name: String, desc: String, component: T): T
}

object MetricsSystem extends MetricsSystem with LifeCycle {
  import scala.collection.mutable.MutableList
  val providers = MutableList[MetricProvider]()
  val sinks = MutableList[MetricsSink]()

  val providersLock = new Lock
  val sinksLock = new Lock

  override def register[T <: Compentent](name: String, desc: String, component: T): T = {

    def tryPut[T](xs: MutableList[T], e: T, lock: Lock) {
      try {
        lock.acquire
        if (!xs.contains(e)) xs += e
      } finally {
        lock.release
      }
    }

    component match {
      case provider: MetricProvider => {
        tryPut(providers, provider, providersLock)
        provider.asInstanceOf[T]
      }
      case sink: MetricsSink => {
        tryPut(sinks, sink, sinksLock)
        sink.asInstanceOf[T]
      }
      case _ => throw new UnsupportedOperationException(s"${name}'s component type is not support yet")
    }
  }

  override def doStart{
    providers.foreach( _ start)
    sinks.foreach(_ start)
  }
  
  override def doStop {
    providers.foreach( _ stop)
    sinks.foreach(_ stop)
  }

}