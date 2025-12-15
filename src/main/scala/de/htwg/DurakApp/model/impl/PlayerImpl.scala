package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.model.{Player, Card}

private[model] case class PlayerImpl(
    name: String,
    hand: List[Card],
    isDone: Boolean
) extends Player
