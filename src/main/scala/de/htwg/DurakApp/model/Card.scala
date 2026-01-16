package de.htwg.DurakApp.model

/** Case class representing a playing card in the Durak game. */
case class Card(
    suit: Suit,
    rank: Rank,
    isTrump: Boolean = false
)
