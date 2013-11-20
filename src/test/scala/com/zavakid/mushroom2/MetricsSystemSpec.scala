package com.zavakid.mushroom2

import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers

import com.zavakid.mushroom2.system.MetricsSystem

/**
 * @author zavakid 2013年11月2日 下午8:34:25
 */
class MetricsSystemSpec extends FunSpec with ShouldMatchers with GivenWhenThen {

  describe("the MetricsSystem") {
    java.lang.System.setProperty("config.trace", "loads")
    it("can register new metricProvider") {
      Given("a new provider")
      val provider = new MetricProvider {
        override def getMetricsRecord(all: Boolean): MetricsRecord = MetricsRecord("name", "desc", Nil)
        override def doStart = Unit
        override def doStop = Unit
      }

      When("we register the provider")
      val returnProvider = MetricsSystem.register("provider", "provider desc ", provider)

      Then("the returnProvider should be the provider")
      returnProvider should be(provider)

      And("the providers' size should be 1")
      MetricsSystem.providers.size should be(1)

      And("and the content is the provider")
      MetricsSystem.providers.head should be(provider)
    }

    it("can register a new metricProvider again") {
      Given("a new provider")
      val newProvider = new MetricProvider {
        override def getMetricsRecord(all: Boolean): MetricsRecord = MetricsRecord("name", "desc", Nil)
        override def doStart = Unit
        override def doStop = Unit
      }

      When("we register the newProvider")
      val returnProvider = MetricsSystem.register("newProvider", "newProvider desc", newProvider)

      Then("the returnProvider should be the newProvider")
      returnProvider should be(newProvider)

      And("the providers' size shoube be 2 this time")
      MetricsSystem.providers.size should be(2)

      And("and the last is the newProvider")
      MetricsSystem.providers.last should be(newProvider)
    }

    it("can  register the same provider more than once with register") {
      Given("a provider_1")
      val provider_1 = new MetricProvider {
        override def getMetricsRecord(all: Boolean): MetricsRecord = MetricsRecord("name", "desc", Nil)
        override def doStart = Unit
        override def doStop = Unit
      }

      When("we register provider_1 first twice")
      val returnProvider_1 = MetricsSystem.register("provider_1", "provider_1 desc", provider_1)
      val returnProvider_2 = MetricsSystem.register("provider_1_1", "provider_1 desc_1", provider_1)

      Then("the providers' size should just add 1, means 3")
      MetricsSystem.providers.size should be(3)

      And("returnProvider_1 should be returnProvider_2")
      returnProvider_1 should be(returnProvider_2)
    }

    it("can start with a scheduler") {
      Given("a provider which can exhale metricsRecord, and a sink which can receive the MetricsRecord")
      val provider = new MetricProvider {
        override def getMetricsRecord(all: Boolean): MetricsRecord = MetricsRecord("hello", "hello_desc", Nil)
        override def doStart = Unit
        override def doStop = Unit
      }

      val sinker = new MetricsSink {
        var metricsRecords = 0;
        var flushed = 0;
        
        override def putMetrics(record: MetricsRecord):Unit = record match {
          case  MetricsRecord("hello", "hello_desc", Nil) => 
            metricsRecords += 1
          case _ => 
        }
        
        override def flush:Unit = {
          flushed += 1
        }
        override def doStart = Unit
        override def doStop = Unit
      }

      When("we register the provider, and start the MetricsSystem, metricsRecords should be 2,  flushed should be 2, ")
      try {
        MetricsSystem.register("provider", "provider desc", provider)
        MetricsSystem.register("sink", "sink desc", sinker)
        MetricsSystem.start
        Thread sleep 10000
        sinker.metricsRecords should be(2)
        sinker.flushed should be(2)
      } finally {
        MetricsSystem.stop
      }

    }

  }

}