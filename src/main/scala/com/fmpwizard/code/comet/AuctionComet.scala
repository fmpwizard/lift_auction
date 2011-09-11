package com.fmpwizard.code
package comet

/**
 * This is the named comet library found at
 * https://github.com/fmpwizard/LiftNamedComet
 */
import com.fmpwizard.cometactor.pertab.namedactor.{NamedCometActor, CometListerner}

import net.liftweb._
import common.{Box, Full, Logger}
import http._
import js.JsCmds.SetHtml
import js.JE._
import js.{JsCmd, JsCmds}
import actor.LiftActor
//import util.DynoVar

import scala.xml.Text
import lib.ItemPrice

/**
 * This is the message we pass to our Auction Actor
 */
case class Message(item: String, price: Double)



class AuctionComet extends NamedCometActor with Logger{

  /**
   * We store the item and price on our global object
   * and then retrieve it and use it on our partialUpdate
   */
  override def lowPriority: PartialFunction[Any, Unit] ={
    case Message(item, price) => {
      ItemPrice.setPrice(Full(item), price )
      ItemPrice.getPrice(Full(item)) map  {
        x => partialUpdate(SetHtml("price", Text(x.toString)))
      }

    }
  }

  /**
   * I use the value name because S.param("q") seems Empty at the time the comet calls render
   */
  def render= {
    "#item *" #> Text(name.openOr("N/A")) &
    "#price *" #> Text(ItemPrice.getPrice(name).openOr(0.00).toString) &
    "#bid [onclick]" #> SHtml.jsonCall(JsRaw("""{item : """" + name.openOr("3") + """", bid : $('#price').text() }""")
      , bid _)._2
  }

  /**
   * We parse the Json data we get from jsonCall()
   * and we pass this info to our Actor dispatcher
   */
  def bid(x: Any) : JsCmd = {
    val (item: String, bid) = x match {
      case m: Map[String, String] => (
        m.get("item").getOrElse("No comet Name"),
        m.get("bid").getOrElse("1")
      )
      case _ => ("No Comet Name", "1")
    }
    info("item is: %s".format(item))
    info("Bid is: %s".format(bid))

    /**
     * listenerFor() returns a DispatcherActor that in turn
     * will send the Message case class to the correct comet actor9s) that
     * we got json data for
     */
    CometListerner.listenerFor(Full(item)) match {
      case a: LiftActor => info(bid); a !  Message(item, (bid.toDouble + 1.00))
      case _            => info("No actor to send an update")
    }
    JsCmds.Noop
  }

}
