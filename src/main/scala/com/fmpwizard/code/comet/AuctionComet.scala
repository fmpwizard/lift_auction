package com.fmpwizard.code.comet

/**
 * Created by IntelliJ IDEA.
 * User: Diego Medina
 * Date: 9/7/11
 * Time: 11:15 PM
 */

import com.fmpwizard.cometactor.pertab.namedactor.{NamedCometActor, CometListerner}

import net.liftweb._
import common.{Box, Full, Logger}
import http._
import js.JsCmds.SetHtml
import SHtml._
import js.JE._
import js.{JsCmd, JsCmds}
import S._
import actor.LiftActor
import util.DynoVar

import scala.xml.Text


case class Message(item: String, price: Double)


object ItemPrice extends Logger{

  private var ItemsPrices: Map[String, Double] = Map()

  def getPrice(str: Box[String]): Box[Double] = synchronized {
    val item= str.getOrElse("")
    ItemsPrices.get(item ) match {
      case Some(price) => info("Our map is %s".format(ItemsPrices));  Full(price)
      case None => {
        info("Our map is %s".format(ItemsPrices))
        Full(0.00)
      }
    }
  }

  def setPrice(str: Box[String], price: Double) = synchronized {
    val item= str.getOrElse("")
    ItemsPrices += item -> price
    info("Our items map is %s".format(ItemsPrices))
  }
}

class AuctionComet extends NamedCometActor with Logger{

  override def lowPriority: PartialFunction[Any, Unit] ={
    case Message(item, price) => {
      ItemPrice.setPrice(Full(item), price )
      ItemPrice.getPrice(Full(item)) map  {
        x => partialUpdate(SetHtml("price", Text(x.toString)))
      }

    }
  }

  def render= {
    "#price *" #> Text(ItemPrice.getPrice(name).openOr(0.00).toString) &
    //I use the val name because S.param("q") seems Empty at the time the comet calls render
    "#bid [onclick]" #> SHtml.jsonCall(JsRaw("""{item : """" + name.openOr("3") + """", bid : $('#price').text() }""")
      , bid _)._2
  }

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
       * listenerFor(cometName) returns a DispatcherActor2 that in turn
       * will send the CityStateUpdate case class to the correct comet actors that
       * we got json data for
       */
    CometListerner.listenerFor(Full(item)) match {
      case a: LiftActor => info(bid); a !  Message(item, (bid.toDouble + 1.00))
      case _            => info("No actor to send an update")
    }
    JsCmds.Noop
  }

}
