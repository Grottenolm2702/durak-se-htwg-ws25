package de.htwg.DurakApp.controller.command

import de.htwg.DurakApp.model.{Card, GameState}

/** Command for playing a card in the game.
  *
  * Created through CommandFactory via dependency injection.
  */
trait PlayCardCommand extends GameCommand:
  def card: Card

/** Command for passing the turn.
  *
  * Created through CommandFactory via dependency injection.
  */
trait PassCommand extends GameCommand

/** Command for taking cards from the table.
  *
  * Created through CommandFactory via dependency injection.
  */
trait TakeCardsCommand extends GameCommand

/** Command for phase changes (internal state management).
  *
  * Created through CommandFactory via dependency injection.
  */
trait PhaseChangeCommand extends GameCommand
