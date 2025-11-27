package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.Card

sealed trait PlayerAction
case class PlayCardAction(card: Card) extends PlayerAction
case object PassAction extends PlayerAction
case object TakeCardsAction extends PlayerAction
case object InvalidAction extends PlayerAction
