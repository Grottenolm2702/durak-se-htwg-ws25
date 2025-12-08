package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.GameState
import de.htwg.DurakApp.model.state.GameEvent
import de.htwg.DurakApp.util.{Observable, UndoRedoManager}
import de.htwg.DurakApp.controller.command.{
  GameCommand,
  CommandFactory,
  PhaseChangeCommand
}
import de.htwg.DurakApp.controller.{
  PlayerAction,
  PlayCardAction,
  PassAction,
  TakeCardsAction,
  InvalidAction,
  SetPlayerCountAction,
  AddPlayerNameAction,
  SetDeckSizeAction
}
import de.htwg.DurakApp.model.builder.GameStateBuilder
import de.htwg.DurakApp.model.{Card, Player, Rank, Suit}
import de.htwg.DurakApp.model.state.*
import scala.util.Random

class Controller(var gameState: GameState, var undoRedoManager: UndoRedoManager)
    extends Observable {

  private def initializeGame(
      playerNames: List[String],
      deckSize: Option[Int]
  ): GameState = {
    val finalDeckSize =
      deckSize.getOrElse(36)

    val ranks =
      Rank.values.filter(_.value >= Rank.Six.value).toList.sortBy(_.value)
    val suits = Suit.values.toList

    val allCardsForDurak = for {
      suit <- suits
      rank <- ranks
    } yield Card(suit, rank)

    val shuffledCards = Random.shuffle(allCardsForDurak).take(finalDeckSize)

    val trumpCard = shuffledCards.last

    val players = playerNames.map(name => Player(name, List.empty))

    val (dealtPlayers, remainingDeck) =
      players.foldLeft((List.empty[Player], shuffledCards.dropRight(1))) {
        case ((currentPlayers, currentDeck), player) =>
          val (playerCards, newDeck) = currentDeck.splitAt(6)
          (currentPlayers :+ player.copy(hand = playerCards), newDeck)
      }

    val playersWithTrump = dealtPlayers.map(p =>
      p.copy(hand = p.hand.map(c => c.copy(isTrump = c.suit == trumpCard.suit)))
    )
    val remainingDeckWithTrump =
      remainingDeck.map(c => c.copy(isTrump = c.suit == trumpCard.suit))

    val attackerIndex = Random.nextInt(players.size)
    val defenderIndex = (attackerIndex + 1) % players.size

    GameStateBuilder()
      .withPlayers(playersWithTrump)
      .withDeck(remainingDeckWithTrump)
      .withTable(Map.empty)
      .withDiscardPile(List.empty)
      .withTrumpCard(trumpCard)
      .withAttackerIndex(attackerIndex)
      .withDefenderIndex(defenderIndex)
      .withGamePhase(AttackPhase)
      .withLastEvent(
        Some(GameEvent.GameSetupComplete)
      )
      .withSetupPlayerCount(gameState.setupPlayerCount)
      .withSetupPlayerNames(gameState.setupPlayerNames)
      .withSetupDeckSize(gameState.setupDeckSize)
      .build()
  }

  def processPlayerAction(action: PlayerAction): GameState = {
    gameState.gamePhase match {
      case SetupPhase | AskPlayerCountPhase | AskPlayerNamesPhase |
          AskDeckSizePhase =>
        action match {
          case SetPlayerCountAction(count) =>
            if (count >= 2 && count <= 6) {
              gameState = gameState.copy(
                setupPlayerCount = Some(count),
                gamePhase = AskPlayerNamesPhase,
                lastEvent = Some(GameEvent.AskPlayerNames)
              )
            } else {
              gameState = gameState.copy(lastEvent =
                Some(GameEvent.SetupError)
              )
            }
          case AddPlayerNameAction(name) =>
            val currentNames = gameState.setupPlayerNames
            val expectedCount = gameState.setupPlayerCount.getOrElse(0)
            if (name.trim.nonEmpty) {
              val newNames = currentNames :+ name.trim
              if (newNames.size < expectedCount) {
                gameState = gameState.copy(
                  setupPlayerNames = newNames,
                  lastEvent = Some(GameEvent.AskPlayerNames)
                )
              } else if (newNames.size == expectedCount) {
                gameState = gameState.copy(
                  setupPlayerNames = newNames,
                  gamePhase = AskDeckSizePhase,
                  lastEvent = Some(GameEvent.AskDeckSize)
                )
              } else {
                gameState = gameState.copy(lastEvent =
                  Some(GameEvent.SetupError)
                )
              }
            } else {
              gameState = gameState.copy(lastEvent =
                Some(GameEvent.SetupError)
              )
            }
          case SetDeckSizeAction(size) =>
            if (List(20, 36, 52).contains(size)) {
              gameState = gameState.copy(
                setupDeckSize = Some(size),
                gamePhase = GameStartPhase
              )
              val initializedGameState = initializeGame(
                gameState.setupPlayerNames,
                gameState.setupDeckSize
              )
              gameState = initializedGameState.copy(lastEvent =
                Some(GameEvent.GameSetupComplete)
              )
            } else {
              gameState = gameState.copy(lastEvent =
                Some(GameEvent.SetupError)
              )
            }
          case _ =>
            gameState = gameState.copy(lastEvent =
              Some(GameEvent.SetupError)
            )
        }
        notifyObservers
        this.gameState

      case _ =>
        val oldGameStateBeforeAction = this.gameState
        val result =
          CommandFactory.createCommand(action, oldGameStateBeforeAction)

        result match {
          case Left(event) =>
            this.gameState =
              oldGameStateBeforeAction.copy(lastEvent = Some(event))
            notifyObservers

          case Right(command) =>
            val gameStateAfterCommand =
              command.execute(oldGameStateBeforeAction)
            this.gameState = gameStateAfterCommand
            undoRedoManager =
              undoRedoManager.save(command, oldGameStateBeforeAction)
            notifyObservers

            @scala.annotation.tailrec
            def handlePhaseRecursively(
                currentState: GameState,
                currentUndoRedoManager: UndoRedoManager
            ): GameState = {
              val oldPhaseStateBeforeHandle = currentState
              val nextState = currentState.gamePhase.handle(currentState)

              if (nextState != currentState) {
                this.gameState = nextState
                this.undoRedoManager = currentUndoRedoManager.save(
                  new PhaseChangeCommand(),
                  oldPhaseStateBeforeHandle
                )
                notifyObservers
                handlePhaseRecursively(this.gameState, this.undoRedoManager)
              } else {
                currentState
              }
            }

            val finalStateFromPhaseHandling =
              handlePhaseRecursively(this.gameState, this.undoRedoManager)
            this.gameState = finalStateFromPhaseHandling
        }
        this.gameState
    }
  }

  def undo(): Option[GameState] = {
    undoRedoManager.undo(this.gameState) match {
      case Some((newManager, previousState)) =>
        undoRedoManager = newManager
        this.gameState = previousState
        notifyObservers
        Some(this.gameState)
      case None =>
        this.gameState =
          this.gameState.copy(lastEvent = Some(GameEvent.CannotUndo))
        notifyObservers
        None
    }
  }

  def redo(): Option[GameState] = {
    undoRedoManager.redo(this.gameState) match {
      case Some((newManager, nextState)) =>
        undoRedoManager = newManager
        this.gameState = nextState
        notifyObservers
        Some(this.gameState)
      case None =>
        this.gameState =
          this.gameState.copy(lastEvent = Some(GameEvent.CannotRedo))
        notifyObservers
        None
    }
  }

  def getStatusString(): String = {
    gameState.gamePhase.toString
  }
}
