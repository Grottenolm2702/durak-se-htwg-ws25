package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.model.{Card, CardFactory, Suit, Rank}

case class StubCard(suit: Suit, rank: Rank, isTrump: Boolean) extends Card:
  def copy(suit: Suit = this.suit, rank: Rank = this.rank, isTrump: Boolean = this.isTrump): Card =
    StubCard(suit, rank, isTrump)

class StubCardFactory extends CardFactory:
  def apply(suit: Suit, rank: Rank, isTrump: Boolean = false): Card =
    StubCard(suit, rank, isTrump)
