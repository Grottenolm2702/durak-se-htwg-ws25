package de.htwg.DurakApp.model

trait Card:
  def suit: Suit
  def rank: Rank
  def isTrump: Boolean

  def copy(
      suit: Suit = this.suit,
      rank: Rank = this.rank,
      isTrump: Boolean = this.isTrump
  ): Card = Card(suit, rank, isTrump)

object Card:
  def apply(suit: Suit, rank: Rank, isTrump: Boolean = false): Card =
    impl.CardImpl(suit, rank, isTrump)
