package de.htwg.DurakApp.aview.gui

import de.htwg.DurakApp.controller.*
import de.htwg.DurakApp.model.Card
import de.htwg.DurakApp.model.state.{AttackPhase, DefensePhase}
import de.htwg.DurakApp.util.Observer

import scalafx.application.Platform
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.layout.*
import scalafx.stage.Stage
import scalafx.event.ActionEvent
import scalafx.event.EventIncludes.*

import scalafx.beans.property.ObjectProperty
import de.htwg.DurakApp.model.GameState
import scalafx.beans.binding.Bindings

class DurakGUI(controller: Controller) extends Observer {

  controller.add(this)

  // ======== STATE ========

  private val selectedCard = ObjectProperty[Option[Card]](None)

  // ======== UI NODES ========

  private val statusLabel = new Label("Welcome to Durak!")
  private val trumpLabel = new Label("Trump: ?")

  private val playerHandBox = new HBox {
    spacing = 10
    alignment = Pos.Center
  }

  private val tableBox = new HBox {
    spacing = 10
    alignment = Pos.Center
  }

  // ======== INIT ========

  def start(): Unit =
    val stage = new Stage {
      title = "Durak - GUI"
      scene = new Scene(900, 600) {
        root = createRootPane()
      }
    }

    stage.show()
    update

  // ======== LAYOUT ========

  private def createRootPane(): BorderPane = new BorderPane {

    top = new VBox {
      alignment = Pos.Center
      spacing = 5
      children = Seq(statusLabel, trumpLabel)
    }

    center = new VBox {
      alignment = Pos.Center
      spacing = 10
      children = Seq(new Label("Table"), tableBox)
    }

    bottom = new VBox {
      alignment = Pos.Center
      spacing = 10
      children = Seq(new Label("Your Hand"), playerHandBox)
    }

    right = createActionButtons()
  }

  private def createActionButtons(): VBox = new VBox {
    alignment = Pos.Center
    spacing = 10

    children = Seq(
      new Button("Play Card") {
        onAction = _ =>
          selectedCard.value.foreach { card =>
            controller.processPlayerAction(PlayCardAction(card))
            selectedCard.value = None
          }
      },
      new Button("Pass") {
        onAction = _ => controller.processPlayerAction(PassAction)
      },
      new Button("Take Cards") {
        onAction = _ => controller.processPlayerAction(TakeCardsAction)
      }
    )
  }

  // ======== RENDERING ========

  override def update: Unit =
    Platform.runLater {
      val gameState = controller.gameState

      updateActivePlayer(gameState)
      updateTrump(gameState)
      updateHand(gameState)
      updateTable(gameState)
    }

  private def updateActivePlayer(gameState: GameState): Unit = {
    val activePlayerIndex: Option[Int] =
      gameState.gamePhase match
        case AttackPhase  => Some(gameState.attackerIndex)
        case DefensePhase => Some(gameState.defenderIndex)
        case _            => None

    activePlayerIndex match
      case Some(i) =>
        val player = gameState.players(i)
        statusLabel.text = s"${player.name}'s turn (${gameState.description})"
      case None =>
        statusLabel.text = gameState.description
  }

  private def updateTrump(gameState: GameState): Unit =
    trumpLabel.text = s"Trump: ${gameState.trumpCard}"

  private def updateHand(gameState: GameState): Unit = {
    val activePlayerIndex: Option[Int] =
      gameState.gamePhase match
        case AttackPhase  => Some(gameState.attackerIndex)
        case DefensePhase => Some(gameState.defenderIndex)
        case _            => None

    playerHandBox.children = activePlayerIndex
      .map(i => gameState.players(i).hand.map(createCardButton))
      .getOrElse(Seq.empty)
  }

  private def updateTable(gameState: GameState): Unit =
    tableBox.children = gameState.table.flatMap { case (attack, defense) =>
      Seq(createTableCard(attack)) ++ defense.map(createTableCard)
    }.toList

  // ======== CARD BUTTONS ========

  private def createCardButton(card: Card): Button =
    new Button(card.toString) {

      style <== Bindings.createStringBinding(
        () =>
          if (selectedCard.value.contains(card))
            "-fx-background-color: lightblue;"
          else
            "",
        selectedCard
      )

      onAction = _ => selectedCard.value = Some(card)
    }

  private def createTableCard(card: Card): Button =
    new Button(card.toString) {
      disable = true
    }
}
