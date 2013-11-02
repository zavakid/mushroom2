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

/** metric 记录，每个 provider 每次提供一个 */
case class MetricsRecord(val name: String, val desc: String, val metrics: Seq[Metric[_]])

trait Compentent

trait MetricProvider extends Compentent {
  def getMetricsRecord(all: Boolean): MetricsRecord
}

/** metric 消费者 */
trait MetricsSink extends Compentent {
  def putMetrics(record: MetricsRecord)
  def flush
}