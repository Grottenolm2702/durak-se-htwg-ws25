package de.htwg.DurakApp.model

/** Trait representing a playing card in the Durak game.
  *
  * Use the companion object factory methods to create instances.
  * Do not reference impl classes directly outside this file.
  */
trait Card:
  def suit: Suit
  def rank: Rank
  def isTrump: Boolean

  def copy(
      suit: Suit = this.suit,
      rank: Rank = this.rank,
      isTrump: Boolean = this.isTrump
  ): Card = Card(suit, rank, isTrump)

/** Factory for creating Card instances.
  * 
  * This companion object is the only place that references impl.CardImpl.
  * All other code should use Card(...) to create instances.
  */
object Card:
  def apply(suit: Suit, rank: Rank, isTrump: Boolean = false): Card =
    impl.CardImpl(suit, rank, isTrump)
