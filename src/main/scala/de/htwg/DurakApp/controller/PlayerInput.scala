package de.htwg.DurakApp
package controller

import model.Player
import model.Card
import model.GameState

trait PlayerInput:
  def choosePassOrAttackCard(attacker: Player, game: GameState): (Boolean, Int)
  def chooseTakeOrDefenseCard(defender: Player, attackCard: Card, game: GameState): (Boolean, Int)
