package com.zavakid.mushroom2

import scala.concurrent.Lock

/**
 * @author zavakid 2013年11月13日 下午10:31:02
 */
object Utils {

  // call by name, not by value
  def syncWith(lock: Lock)(block:  => Unit) {
    try {
      lock.acquire
      block
    } finally {
      lock.release
    }
  }
}