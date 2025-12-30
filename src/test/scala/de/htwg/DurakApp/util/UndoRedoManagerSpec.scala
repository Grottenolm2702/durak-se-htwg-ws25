package de.htwg.DurakApp.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.util.impl.UndoRedoManagerFactoryImpl
import de.htwg.DurakApp.model.{Card, Suit, Rank, GameState, Player}
import de.htwg.DurakApp.model.state.AttackPhase
import de.htwg.DurakApp.controller.command.impl.PhaseChangeCommand

class UndoRedoManagerSpec extends AnyWordSpec with Matchers {

  val factory = UndoRedoManagerFactoryImpl()
  val player1 = Player("Alice", List(Card(Suit.Hearts, Rank.Six)))
  val player2 = Player("Bob", List(Card(Suit.Diamonds, Rank.Seven)))
  val trumpCard = Card(Suit.Clubs, Rank.Ace, isTrump = true)
  
  val gameState = GameState(
    players = List(player1, player2),
    deck = List.empty,
    table = Map.empty,
    discardPile = List.empty,
    trumpCard = trumpCard,
    attackerIndex = 0,
    defenderIndex = 1,
    gamePhase = AttackPhase,
    lastEvent = None,
    passedPlayers = Set.empty,
    roundWinner = None,
    setupPlayerCount = None,
    setupPlayerNames = List.empty,
    setupDeckSize = None,
    currentAttackerIndex = None,
    lastAttackerIndex = None
  )

  "An UndoRedoManager" should {
    "be created with empty stacks" in {
      val manager = factory.create()
      
      manager.undoStack shouldBe empty
      manager.redoStack shouldBe empty
    }
    
    "save a command and state" in {
      val manager = factory.create()
      val command = PhaseChangeCommand()
      
      val updatedManager = manager.save(command, gameState)
      
      updatedManager.undoStack.size shouldBe 1
      updatedManager.redoStack shouldBe empty
    }
    
    "clear redo stack when saving new command" in {
      val manager = factory.create()
      val command1 = PhaseChangeCommand()
      val command2 = PhaseChangeCommand()
      
      val manager1 = manager.save(command1, gameState)
      val manager2 = manager1.save(command2, gameState.copy(attackerIndex = 1))
      val (manager3, _) = manager2.undo(gameState).get
      val manager4 = manager3.save(command1, gameState)
      
      manager4.redoStack shouldBe empty
      manager4.undoStack.size shouldBe 2
    }
    
    "undo a command" in {
      val manager = factory.create()
      val command = PhaseChangeCommand()
      val oldState = gameState
      val newState = gameState.copy(attackerIndex = 1)
      
      val savedManager = manager.save(command, oldState)
      val undoResult = savedManager.undo(newState)
      
      undoResult shouldBe defined
      val (updatedManager, restoredState) = undoResult.get
      updatedManager.undoStack shouldBe empty
      updatedManager.redoStack.size shouldBe 1
    }
    
    "return None when undoing with empty stack" in {
      val manager = factory.create()
      
      val result = manager.undo(gameState)
      
      result shouldBe None
    }
    
    "redo a command" in {
      val manager = factory.create()
      val command = PhaseChangeCommand()
      val oldState = gameState
      val newState = gameState.copy(attackerIndex = 1)
      
      val savedManager = manager.save(command, oldState)
      val (undoneManager, _) = savedManager.undo(newState).get
      val redoResult = undoneManager.redo(oldState)
      
      redoResult shouldBe defined
      val (redoneManager, _) = redoResult.get
      redoneManager.undoStack.size shouldBe 1
      redoneManager.redoStack shouldBe empty
    }
    
    "return None when redoing with empty stack" in {
      val manager = factory.create()
      
      val result = manager.redo(gameState)
      
      result shouldBe None
    }
    
    "maintain proper stack state through multiple operations" in {
      val manager = factory.create()
      val command1 = PhaseChangeCommand()
      val command2 = PhaseChangeCommand()
      val state1 = gameState
      val state2 = gameState.copy(attackerIndex = 1)
      val state3 = gameState.copy(attackerIndex = 2)
      
      val m1 = manager.save(command1, state1)
      val m2 = m1.save(command2, state2)
      m2.undoStack.size shouldBe 2
      m2.redoStack.size shouldBe 0
      
      val (m3, _) = m2.undo(state3).get
      m3.undoStack.size shouldBe 1
      m3.redoStack.size shouldBe 1
      
      val (m4, _) = m3.undo(state2).get
      m4.undoStack.size shouldBe 0
      m4.redoStack.size shouldBe 2
      
      val (m5, _) = m4.redo(state1).get
      m5.undoStack.size shouldBe 1
      m5.redoStack.size shouldBe 1
    }
  }
}
