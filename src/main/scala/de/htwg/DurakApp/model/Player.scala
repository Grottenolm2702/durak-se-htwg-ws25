package de.htwg.DurakApp.model

/** Trait representing a player in the Durak game.
  *
  * Use PlayerFactory to create instances via dependency injection. Do not
  * reference impl classes directly.
  */
trait Player:
  def name: String
  def hand: List[Card]
  def isDone: Boolean

  def copy(
      name: String = this.name,
      hand: List[Card] = this.hand,
      isDone: Boolean = this.isDone
  ): Player

/** Factory trait for creating Player instances.
  *
  * Inject this factory via Guice to create Player instances.
  */
trait PlayerFactory:
  def apply(
      name: String,
      hand: List[Card] = List(),
      isDone: Boolean = false
  ): Player
