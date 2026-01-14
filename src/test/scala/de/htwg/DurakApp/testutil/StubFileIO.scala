package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.util.FileIOInterface
import de.htwg.DurakApp.model.GameState
import scala.util.{Try, Success}

class StubFileIO extends FileIOInterface {
  override def save(gameState: GameState): Try[Unit] = Success(())
  override def load(): Try[GameState] = Success(
    TestHelper.createTestGameState()
  )
}
