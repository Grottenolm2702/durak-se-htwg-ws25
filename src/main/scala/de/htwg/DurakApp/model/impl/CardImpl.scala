package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.model.{Card, Suit, Rank}

private[model] case class CardImpl(
    suit: Suit,
    rank: Rank,
    isTrump: Boolean
) extends Card
