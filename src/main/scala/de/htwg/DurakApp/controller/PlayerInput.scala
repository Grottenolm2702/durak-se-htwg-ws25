package de.htwg.DurakApp
package controller

import model.Player
import model.Card
import model.GameState

trait PlayerInput:
  def chooseAttackCard(attacker: Player, game: GameState): Int
  def chooseDefenseCard(defender: Player, attackCard: Card, game: GameState): Int
