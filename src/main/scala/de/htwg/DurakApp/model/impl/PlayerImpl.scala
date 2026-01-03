package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.model.{Player, Card}

private[model] case class PlayerImpl(
    name: String,
    hand: List[Card],
    isDone: Boolean
) extends Player:
  override def copy(
      name: String = this.name,
      hand: List[Card] = this.hand,
      isDone: Boolean = this.isDone
  ): Player = PlayerImpl(name, hand, isDone)
