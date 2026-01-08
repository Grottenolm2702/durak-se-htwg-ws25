package de.htwg.DurakApp.model

/** Trait representing a playing card in the Durak game.
  *
  * Use CardFactory to create instances via dependency injection. Do not
  * reference impl classes directly.
  */
trait Card:
  def suit: Suit
  def rank: Rank
  def isTrump: Boolean

  def copy(
      suit: Suit = this.suit,
      rank: Rank = this.rank,
      isTrump: Boolean = this.isTrump
  ): Card

/** Factory trait for creating Card instances.
  *
  * Inject this factory via Guice to create Card instances.
  */
trait CardFactory:
  def apply(suit: Suit, rank: Rank, isTrump: Boolean = false): Card
