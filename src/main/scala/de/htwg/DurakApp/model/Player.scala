package de.htwg.DurakApp.model

/** Trait representing a player in the Durak game.
  *
  * Use the companion object factory methods to create instances.
  * Do not reference impl classes directly outside this file.
  */
trait Player:
  def name: String
  def hand: List[Card]
  def isDone: Boolean

  def copy(
      name: String = this.name,
      hand: List[Card] = this.hand,
      isDone: Boolean = this.isDone
  ): Player = Player(name, hand, isDone)

/** Factory for creating Player instances.
  * 
  * This companion object is the only place that references impl.PlayerImpl.
  * All other code should use Player(...) to create instances.
  */
object Player:
  def apply(
      name: String,
      hand: List[Card] = List(),
      isDone: Boolean = false
  ): Player =
    impl.PlayerImpl(name, hand, isDone)
