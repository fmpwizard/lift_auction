package com.fmpwizard.code.lib

import net.liftweb.common.{Box, Full, Logger}

/**
 * We use this object to maintain state across multiple sessions.
 * You could replace this by storing and retrieving data from a
 * database, memcached, etc
 *
 */
object ItemPrice extends Logger{

  private var ItemsPrices: Map[String, Double] = Map()

  def getPrice(str: Box[String]): Box[Double] = synchronized {
    val item= str.getOrElse("")
    ItemsPrices.get(item) match {
      case Some(price) => {
        info("Our map is %s".format(ItemsPrices))
        Full(price)
      }
      case None => {
        info("Our map is %s".format(ItemsPrices))
        Full(0.00)
      }
    }
  }

  def setPrice(str: Box[String], price: Double) = synchronized {
    val item= str.getOrElse("")
    str map { x => ItemsPrices += x -> price }
    info("Our items map is %s".format(ItemsPrices))
  }
}
