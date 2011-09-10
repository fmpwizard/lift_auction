package com.fmpwizard.code.snippet

import com.fmpwizard.cometactor.pertab.namedactor.InsertNamedComet
import net.liftweb._
import common._
import http._
import S._

class AddComet extends InsertNamedComet with Logger{
  override lazy val name= param("q") openOr("")
  override lazy val cometClass= "AuctionComet"
}
