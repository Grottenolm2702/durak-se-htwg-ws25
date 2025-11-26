package de.htwg.DurakApp.controller

sealed trait PlayerAction
case class PlayCardAction(cardString: String) extends PlayerAction
case object PassAction extends PlayerAction
case object TakeCardsAction extends PlayerAction
case object InvalidAction extends PlayerAction