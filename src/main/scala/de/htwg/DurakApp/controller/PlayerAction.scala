package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.ModelInterface.Card

/** Sealed trait representing all possible player actions in the game.
  *
  * Player actions are processed by the Controller and transformed into
  * game state changes. The sealed nature ensures all action types are
  * known at compile time for exhaustive pattern matching.
  */
sealed trait PlayerAction

// Gameplay Actions

/** Action to play a card from the player's hand.
  *
  * @param card The card to play
  */
case class PlayCardAction(card: Card) extends PlayerAction

/** Action to pass the current turn. */
case object PassAction extends PlayerAction

/** Action to take all cards from the table (defender only). */
case object TakeCardsAction extends PlayerAction

/** Action representing an invalid or unrecognized input. */
case object InvalidAction extends PlayerAction

// Undo/Redo Actions

/** Action to undo the last move. */
case object UndoAction extends PlayerAction

/** Action to redo the last undone move. */
case object RedoAction extends PlayerAction

// Setup Actions

/** Action to set the number of players during game setup.
  *
  * @param count The number of players (typically 2-6)
  */
case class SetPlayerCountAction(count: Int) extends PlayerAction

/** Action to add a player name during game setup.
  *
  * @param name The name of the player to add
  */
case class AddPlayerNameAction(name: String) extends PlayerAction

/** Action to set the deck size during game setup.
  *
  * @param size The number of cards in the deck (typically up to 36)
  */
case class SetDeckSizeAction(size: Int) extends PlayerAction

// End Game Actions

/** Action to start a new game after the current game ends. */
case object PlayAgainAction extends PlayerAction

/** Action to exit the application after the current game ends. */
case object ExitGameAction extends PlayerAction
