package de.htwg.DurakApp.model

case class Player(
    name: String,
    hand: List[Card] = List(),
    isDone: Boolean = false
)
