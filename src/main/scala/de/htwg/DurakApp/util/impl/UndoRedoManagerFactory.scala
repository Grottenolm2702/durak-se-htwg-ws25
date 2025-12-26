package de.htwg.DurakApp.util.impl

import de.htwg.DurakApp.util.{UndoRedoManager, UndoRedoManagerFactory}
import com.google.inject.Inject

/** Factory implementation for creating UndoRedoManager instances.
  * 
  * This factory is bound in Guice and creates fresh manager instances.
  */
class UndoRedoManagerFactoryImpl @Inject() () extends UndoRedoManagerFactory:
  def create(): UndoRedoManager =
    UndoRedoManagerImpl(Nil, Nil)
