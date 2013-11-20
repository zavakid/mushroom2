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

trait Component {
  this:LifeCycle =>
}

trait MetricProvider extends Component with LifeCycle {
  def getMetricsRecord(all: Boolean): MetricsRecord
}

/** metric 消费者 */
trait MetricsSink extends Component with LifeCycle {
  def putMetrics(record: MetricsRecord) : Unit
  def flush : Unit
}

/** 生命周期的管理 */
trait LifeCycle {
  @volatile
  private var running = false
  
  def start {
    synchronized {
      if(!running){
        doStart
        running = true
      }
    }
  }
  
  def stop{
    synchronized{
      if(running){
        doStop
        running = false
      }
    }
  }
  
  def isRunning = running
  
  def doStart:Unit
  def doStop:Unit
}