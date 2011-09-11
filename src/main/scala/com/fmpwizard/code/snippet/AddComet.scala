package com.fmpwizard.code.snippet

import com.fmpwizard.cometactor.pertab.namedactor.InsertNamedComet
import net.liftweb._
import common._
import http._
import S._

/**
 * This snippet is in charged of adding a named comet
 * actor based on the query url parameter   "q"
 */
class AddComet extends InsertNamedComet with Logger{
  override lazy val name= param("q") openOr("")
  override lazy val cometClass= "AuctionComet"
}
