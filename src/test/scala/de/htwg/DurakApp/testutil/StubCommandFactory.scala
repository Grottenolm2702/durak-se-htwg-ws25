package de.htwg.DurakApp.testutil
import de.htwg.DurakApp.controller.command._
import de.htwg.DurakApp.controller.PlayerAction
import de.htwg.DurakApp.model.{GameState, Card}
class StubCommandFactory extends CommandFactory:
  def playCard(cardValue: Card): PlayCardCommand =
    new PlayCardCommand:
      val card: Card = cardValue
      def execute(state: GameState): GameState = state
      def undo(state: GameState): GameState = state
  def pass(): PassCommand =
    new PassCommand:
      def execute(state: GameState): GameState = state
      def undo(state: GameState): GameState = state
  def takeCards(): TakeCardsCommand =
    new TakeCardsCommand:
      def execute(state: GameState): GameState = state
      def undo(state: GameState): GameState = state
  def phaseChange(): PhaseChangeCommand =
    new PhaseChangeCommand:
      def execute(state: GameState): GameState = state
      def undo(state: GameState): GameState = state
