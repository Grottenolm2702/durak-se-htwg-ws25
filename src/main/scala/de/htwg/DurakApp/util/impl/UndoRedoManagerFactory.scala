package de.htwg.DurakApp.util.impl

import de.htwg.DurakApp.util.{UndoRedoManager, UndoRedoManagerFactory}
import com.google.inject.Inject

class UndoRedoManagerFactoryImpl @Inject() () extends UndoRedoManagerFactory:
  def create(): UndoRedoManager =
    UndoRedoManagerImpl(Nil, Nil)
