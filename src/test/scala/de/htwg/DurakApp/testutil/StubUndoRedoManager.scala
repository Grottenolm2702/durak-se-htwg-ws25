package de.htwg.DurakApp.testutil
import de.htwg.DurakApp.util.UndoRedoManager
import de.htwg.DurakApp.controller.command.GameCommand
import de.htwg.DurakApp.model.GameState
class StubUndoRedoManager(
    val undoStack: List[(GameCommand, GameState)] = List.empty,
    val redoStack: List[(GameCommand, GameState, GameState)] = List.empty
) extends UndoRedoManager:
  private val dummyCommand: GameCommand = new GameCommand:
    def execute(state: GameState): GameState = state
    def undo(state: GameState): GameState = state
  def save(command: GameCommand, currentState: GameState): UndoRedoManager =
    new StubUndoRedoManager(
      undoStack = (command, currentState) :: undoStack,
      redoStack = List.empty
    )
  def undo(currentState: GameState): Option[(UndoRedoManager, GameState)] =
    undoStack match
      case (_, prevState) :: tail =>
        val newManager = new StubUndoRedoManager(
          undoStack = tail,
          redoStack = (dummyCommand, prevState, currentState) :: redoStack
        )
        Some((newManager, prevState))
      case Nil => None
  def redo(currentState: GameState): Option[(UndoRedoManager, GameState)] =
    redoStack match
      case (_, stateBeforeCommand, stateAfterCommand) :: tail =>
        val newManager = new StubUndoRedoManager(
          undoStack = (dummyCommand, stateBeforeCommand) :: undoStack,
          redoStack = tail
        )
        Some((newManager, stateAfterCommand))
      case Nil => None
