package de.htwg.DurakApp.util

import de.htwg.DurakApp.model.GameState
import scala.util.Try

trait FileIOInterface:
  def save(gameState: GameState): Try[Unit]
  def load(): Try[GameState]
