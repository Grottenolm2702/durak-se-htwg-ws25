package de.htwg.DurakApp.model

/** Case class representing a player in the Durak game. */
case class Player(
    name: String,
    hand: List[Card] = List(),
    isDone: Boolean = false
)
