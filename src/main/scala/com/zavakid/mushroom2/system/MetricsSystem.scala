package com.zavakid.mushroom2.system

import com.zavakid.mushroom2.MetricProvider
import com.zavakid.mushroom2.MetricsSink
import com.zavakid.mushroom2.Compentent
import scala.concurrent.Lock
import com.zavakid.mushroom2.LifeCycle
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.Props
import com.zavakid.mushroom2.MetricsRecord
import com.zavakid.mushroom2.MetricsRecord
import scala.collection.immutable.Nil
import java.net.URLClassLoader

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
  val config = ConfigFactory.load.getConfig("metrics")
  val actorSystem = ActorSystem("metricsAkkaSystem", config)

  override def register[T <: Compentent](name: String, desc: String, component: T): T = {

    def tryPut[T](xs: MutableList[T], e: T) {
      if (!xs.contains(e)) xs += e
    }

    import com.zavakid.mushroom2.Utils.syncWith
    component match {
      case provider: MetricProvider =>
        syncWith(providersLock) {
          tryPut(providers, provider)
        }
      case sink: MetricsSink =>
        syncWith(providersLock) {
          tryPut(sinks, sink)
        }
      case _ => throw new UnsupportedOperationException(s"${name}'s component type is not support yet")
    }
    component
  }

  override def doStart {
    providers.foreach(_ start)
    sinks.foreach(_ start)

    import scala.concurrent.duration._
    val interval: Long = config.getMilliseconds("interval")

    import actorSystem.dispatcher
    actorSystem.scheduler.schedule(interval millisecond, interval millisecond) {
      val records = providers.toList.map(_.getMetricsRecord(true)) 
      records match {
        case Nil => ()
        case xs: List[MetricsRecord] => sinks.toList.foreach { s =>
          xs.foreach(s putMetrics _)
          s.flush
        }
      }
    }
  }

  override def doStop {
    providers.foreach(_ stop)
    sinks.foreach(_ stop)
    actorSystem.shutdown
  }
}