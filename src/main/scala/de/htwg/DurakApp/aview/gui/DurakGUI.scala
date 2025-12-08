package de.htwg.DurakApp.aview.gui

import de.htwg.DurakApp.controller.*
import de.htwg.DurakApp.model.{Card, GameState, Rank}
import de.htwg.DurakApp.model.state.{AttackPhase, DefensePhase}
import de.htwg.DurakApp.util.Observer
import scalafx.application.Platform
import scalafx.beans.binding.Bindings
import scalafx.beans.property.ObjectProperty
import scalafx.event.EventIncludes.*
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.*
import scalafx.stage.Stage

class DurakGUI(controller: Controller) extends Observer {

  controller.add(this)

  private val selectedCard = ObjectProperty[Option[Card]](None)

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

  def start(): Unit = {
    val stage = new Stage {
      title = "Durak - GUI"
      scene = new Scene(900, 600) {
        root = createRootPane()
      }
    }

    stage.show()
    update
  }

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

  override def update: Unit = {
    Platform.runLater {
      val gameState = controller.gameState

      updateActivePlayer(gameState)
      updateTrump(gameState)
      updateHand(gameState)
      updateTable(gameState)
    }
  }

  private def updateActivePlayer(gameState: GameState): Unit = {
    val activePlayerIndex: Option[Int] =
      gameState.gamePhase match {
        case AttackPhase  => Some(gameState.attackerIndex)
        case DefensePhase => Some(gameState.defenderIndex)
        case _            => None
      }

    activePlayerIndex match {
      case Some(i) =>
        val player = gameState.players(i)
        statusLabel.text = s"${player.name}'s turn (${gameState.description})"
      case None =>
        statusLabel.text = gameState.description
    }
  }

  private def updateTrump(gameState: GameState): Unit = {
    trumpLabel.text = s"Trump: ${gameState.trumpCard}"
  }

  private def updateHand(gameState: GameState): Unit = {
    val activePlayerIndex: Option[Int] =
      gameState.gamePhase match {
        case AttackPhase  => Some(gameState.attackerIndex)
        case DefensePhase => Some(gameState.defenderIndex)
        case _            => None
      }

    playerHandBox.children = activePlayerIndex
      .map(i => gameState.players(i).hand.map(createCardButton))
      .getOrElse(Seq.empty)
  }

  private def updateTable(gameState: GameState): Unit = {
    tableBox.children = gameState.table.flatMap { case (attack, defense) =>
      Seq(createCardView(attack)) ++ defense.map(createCardView)
    }.toList
  }

  private def cardToImagePath(card: Card): String = {
    val suitStr = card.suit.toString.toLowerCase.stripSuffix("s")
    val rankStr = card.rank match {
      case Rank.Ace   => "1"
      case Rank.King  => "king"
      case Rank.Queen => "queen"
      case Rank.Jack  => "jack"
      case Rank.Ten   => "10"
      case Rank.Nine  => "9"
      case Rank.Eight => "8"
      case Rank.Seven => "7"
      case Rank.Six   => "6"
    }
    s"cards/${suitStr}_${rankStr}.png"
  }

  private def createCardView(card: Card): ImageView = {
    val imagePath = cardToImagePath(card)
    val resourceStream = getClass.getResourceAsStream(s"/$imagePath")
    if (resourceStream == null) {
      new ImageView(
        new Image(
          "https://via.placeholder.com/70x100.png?text=Not+Found",
          70,
          100,
          false,
          false
        )
      )
    } else {
      val image = new Image(resourceStream, 70, 100, true, true)
      new ImageView(image)
    }
  }

  private def createCardButton(card: Card): Button = {
    new Button {
      graphic = createCardView(card)
      style = "-fx-background-color: transparent; -fx-padding: 0;"

      style <== Bindings.createStringBinding(
        () =>
          if (selectedCard.value.contains(card))
            "-fx-border-color: lightblue; -fx-border-width: 3; -fx-background-color: transparent; -fx-padding: 0;"
          else
            "-fx-border-color: transparent; -fx-border-width: 3; -fx-background-color: transparent; -fx-padding: 0;",
        selectedCard
      )

      onAction = _ => selectedCard.value = Some(card)
    }
  }
}
