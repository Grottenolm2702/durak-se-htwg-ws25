package de.htwg.DurakApp

enum Suit:
  case Hearts, Diamonds, Clubs, Spades

enum Rank(val value: Int):
  case Six extends Rank(6)
  case Seven extends Rank(7)
  case Eight extends Rank(8)
  case Nine extends Rank(9)
  case Ten extends Rank(10)
  case Jack extends Rank(11)
  case Queen extends Rank(12)
  case King extends Rank(13)
  case Ace extends Rank(14)

case class Card(suit: Suit, rank: Rank, isTrump: Boolean)
case class Player(
    name: String,
    hand: List[Card] = List(),
    isDone: Boolean = false,
    lastDefenseSuccessful: Boolean = true
)
case class GameState(
    playerList: List[Player],
    deck: List[Card],
    trump: Suit,
    attackingCards: List[Card] = Nil,
    defendingCards: List[Card] = Nil,
    discardPile: List[Card] = Nil
)
