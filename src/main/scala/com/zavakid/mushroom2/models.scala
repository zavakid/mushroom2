package com.zavakid.mushroom2

/**
 * @author zavakid 2013年11月2日 下午3:27:16
 */
sealed trait Metric[T] {
  def name: String
  def desc: String = "<<no description>>"
  def value: T
}

/** 表示计数的 metric */
case class MetricCounter[T](val name: String, override val desc: String, val value: T) extends Metric[T]
/** 表示差值的 metric */
case class MetricDeltal[T](val name: String, override val desc: String, val value: T) extends Metric[T]

trait MetricProvider {
  def getMetricsRecord(all: Boolean): MetricsRecord
}

/** metric 记录，每个 provider 每次提供一个 */
case class MetricsRecord(val name:String, val desc: String, val metrics: Seq[Metric[_]])

/** metric 消费者 */
trait MetricsSink {
  def putMetrics(record: MetricsRecord)
  def flush
}

trait MetricsSystem {
  /** 注册一个组件。比如 MetricProvider 或者 MetricsSink */
  def register[T](name: String, desc: String, component: T)

  /** 注册一个不存在的组件。比如 MetricProvider 或者 MetricsSink，如果存在的话，会返回原来老的组件 */
  def registerIfAbsent[T](name: String, desc: String, component: T): T

  def start
  def shutdown
}
