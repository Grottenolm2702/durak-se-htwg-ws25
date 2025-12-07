package de.htwg.DurakApp.aview.gui

import de.htwg.DurakApp.controller.{Controller, PlayCardAction, PassAction, TakeCardsAction}
import de.htwg.DurakApp.util.Observer
import de.htwg.DurakApp.model.Card
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import javafx.event.{ActionEvent, EventHandler} // Added import

import de.htwg.DurakApp.model.state.{AttackPhase, DefensePhase}
import scalafx.stage.Stage

class DurakGUI(controller: Controller) extends Observer {
  controller.add(this)

  private lazy val statusLabel = new Label("Welcome to Durak!")
  private lazy val playerHandBox = new HBox()
  private lazy val tableBox = new HBox()
  private lazy val trumpLabel = new Label("Trump: ?")
  private var selectedPlayerCard: Option[Card] = None

  def start(): Unit = {
    val stage = new Stage {
      title = "Durak - GUI"
      scene = new Scene(800, 600) {
        root = createRootPane()
      }
    }
    stage.show()
    update
  }

  private def createRootPane() = new BorderPane {
    top = new VBox(statusLabel, trumpLabel) {
      alignment = Pos.Center
    }
    center = new VBox(new Label("Table:"), tableBox) {
      alignment = Pos.Center
    }
    bottom = new VBox(new Label("Your Hand:"), playerHandBox) {
      alignment = Pos.Center
    }
    right = createActionButtons()
  }

  private def createActionButtons() = new VBox(
    new Button("Play Card") {
      onAction = new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          println(s"Play Card button clicked. Selected card: $selectedPlayerCard")
          selectedPlayerCard.foreach { card =>
            println(s"Processing play action for card: $card")
            controller.processPlayerAction(PlayCardAction(card))
            selectedPlayerCard = None
          }
        }
      }
    },
    new Button("Pass") {
      onAction = new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          controller.processPlayerAction(PassAction)
        }
      }
    },
    new Button("Take Cards") {
      onAction = new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          controller.processPlayerAction(TakeCardsAction)
        }
      }
    }
  ) {
    alignment = Pos.Center
    spacing = 10
  }

  override def update: Unit = {
    Platform.runLater {
      val gameState = controller.gameState

      val activePlayerIndex = gameState.gamePhase match {
        case AttackPhase => gameState.attackerIndex
        case DefensePhase => gameState.defenderIndex
        case _ => -1 // No single active player in other phases
      }

      if (activePlayerIndex != -1) {
        val activePlayer = gameState.players(activePlayerIndex)
        statusLabel.text = s"${activePlayer.name}'s turn (${gameState.description})"
        playerHandBox.children = activePlayer.hand.map(createCardButton)
      } else {
        statusLabel.text = gameState.description
        playerHandBox.children = Seq() // Clear hand display if no active player
      }

      trumpLabel.text = "Trump: " + gameState.trumpCard.toString
      tableBox.children = gameState.table.flatMap { case (attack, defense) =>
        Seq(new Button(attack.toString)) ++ defense.map(d => new Button(d.toString))
      }.toList
    }
  }

  private def createCardButton(card: Card) = new Button(card.toString) {
    onAction = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        println(s"Card selected: $card")
        selectedPlayerCard = Some(card)
      }
    }
  }
}
