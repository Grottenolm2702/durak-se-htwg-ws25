package de.htwg.DurakApp.model

trait Player:
  def name: String
  def hand: List[Card]
  def isDone: Boolean

  def copy(
      name: String = this.name,
      hand: List[Card] = this.hand,
      isDone: Boolean = this.isDone
  ): Player = Player(name, hand, isDone)

object Player:
  def apply(
      name: String,
      hand: List[Card] = List(),
      isDone: Boolean = false
  ): Player =
    impl.PlayerImpl(name, hand, isDone)
