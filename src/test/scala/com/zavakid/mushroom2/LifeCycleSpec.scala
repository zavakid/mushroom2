package com.zavakid.mushroom2

import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import org.scalatest.concurrent.ConductorMethods
import org.scalatest.matchers.ShouldMatchers
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author zavakid 2013年11月3日 下午7:26:58
 */
class LifeCycleSpec extends FunSpec with ShouldMatchers with ConductorMethods with GivenWhenThen{
  describe("LifeCycle will just start/stop only once") {
    it("can just start only once") {
        given("a new lifecycle and count the foreach number with atomicInt")
        var atomicInt = new AtomicInteger
        val lifecycle = new LifeCycle{
          var count = 0
          def doStart { count = count + 1}
          def doStop {}
        }
        
        def loopStart = {
          1 to 100 foreach { _ =>
            atomicInt.addAndGet(1)
            lifecycle start
          }
        }
        
        when("3 threads starting the lifecycle")
        thread("thread 1 will start 100 times"){
          waitForBeat(1)
          loopStart
        }
        
        thread("thread 2 will start 100 times"){
          waitForBeat(1)
          loopStart
        }
        
        thread("thread 3 will start 100 times"){
          waitForBeat(1)
          loopStart
        }
        
        then("the lifecycle's count should be 1, atomicInt.get() should be 300")
        whenFinished{
            atomicInt.get() should be(300)
            lifecycle.count should be(1)
        }
    }
    
     it("can just stop only once") {
        given("a new lifecycle and count the foreach number with atomicInt")
        var atomicInt = new AtomicInteger
        val lifecycle = new LifeCycle{
          var count = 0
          def doStart {}
          def doStop { count = count + 1}
        }
        
        lifecycle start
        
        def loopStop = {
          1 to 100 foreach { _ =>
            atomicInt.addAndGet(1)
            lifecycle stop
          }
        }
        
        when("3 threads starting the lifecycle")
        thread("thread 1 will start 100 times"){
          waitForBeat(1)
          loopStop
        }
        
        thread("thread 2 will start 100 times"){
          waitForBeat(1)
          loopStop
        }
        
        thread("thread 3 will start 100 times"){
          waitForBeat(1)
          loopStop
        }
        
        then("the lifecycle's count should be 1, atomicInt.get() should be 300")
        whenFinished{
            atomicInt.get() should be(300)
            lifecycle.count should be(1)
        }
    }
  }
}