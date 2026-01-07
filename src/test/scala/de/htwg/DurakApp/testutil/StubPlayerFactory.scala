package de.htwg.DurakApp.testutil
import de.htwg.DurakApp.model.{Player, PlayerFactory, Card}
case class StubPlayer(name: String, hand: List[Card], isDone: Boolean)
    extends Player:
  def copy(
      name: String = this.name,
      hand: List[Card] = this.hand,
      isDone: Boolean = this.isDone
  ): Player =
    StubPlayer(name, hand, isDone)
class StubPlayerFactory extends PlayerFactory:
  def apply(
      name: String,
      hand: List[Card] = List.empty,
      isDone: Boolean = false
  ): Player =
    StubPlayer(name, hand, isDone)
