package de.htwg.DurakApp.model

case class GameState(
    playerList: List[Player],
    deck: List[Card],
    trump: Suit,
    attackingCards: List[Card] = Nil,
    defendingCards: List[Card] = Nil,
    discardPile: List[Card] = Nil
)
