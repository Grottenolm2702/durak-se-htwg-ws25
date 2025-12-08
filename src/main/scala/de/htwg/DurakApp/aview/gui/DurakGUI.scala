package de.htwg.DurakApp.aview.gui

import de.htwg.DurakApp.controller.*
import de.htwg.DurakApp.model.{Card, GameState, Rank}
import de.htwg.DurakApp.model.state.*
import de.htwg.DurakApp.util.Observer
import scalafx.application.Platform
import scalafx.beans.binding.Bindings
import scalafx.beans.property.ObjectProperty
import scalafx.event.EventIncludes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.*
import scalafx.stage.Stage
import scalafx.Includes.jfxGaussianBlur2sfx
import scala.util.{Try, Success, Failure}

class DurakGUI(controller: Controller) extends Observer {

  controller.add(this)

  private val selectedCard = ObjectProperty[Option[Card]](None)

  private val statusLabel = new Label("Welcome to Durak!")

  private val trumpCardView = new ImageView() {
    fitWidth = 56
    fitHeight = 80
    preserveRatio = true
  }

  private val trumpDisplay = new HBox {
    spacing = 10
    alignment = Pos.Center
    children = Seq(new Label("Trump:"), trumpCardView)
  }

  private val playerHandBox = new HBox {
    spacing = 10
    alignment = Pos.Center
    padding = Insets(10)
  }

  private val attackerTable = new HBox {
    spacing = 10
    alignment = Pos.Center
    minHeight = 110
  }

  private val defenderTable = new HBox {
    spacing = 10
    alignment = Pos.Center
    minHeight = 110
  }

  private val tableDisplay = new VBox {
    spacing = 5
    alignment = Pos.Center
    padding = Insets(10)
    children = Seq(
      new Label("Attacking Cards"),
      attackerTable,
      new Separator(),
      new Label("Defending Cards"),
      defenderTable
    )
  }

  private val playCardButton: Button = new Button("Play Card") {
    onAction = _ =>
      selectedCard.value.foreach { card =>
        controller.processPlayerAction(PlayCardAction(card))
        selectedCard.value = None
      }
    prefWidth = 120
  }

  private val passButton: Button = new Button("Pass") {
    onAction = _ => controller.processPlayerAction(PassAction)
    prefWidth = 120
  }

  private val takeCardsButton: Button = new Button("Take Cards") {
    onAction = _ => controller.processPlayerAction(TakeCardsAction)
    prefWidth = 120
  }

  private val actionButtons = new VBox {
    alignment = Pos.Center
    spacing = 10
    padding = Insets(10)
    children = Seq(playCardButton, passButton, takeCardsButton)
  }

  private val playerCountInput = new TextField {
    promptText = "Number of players (2-6)"
  }
  private val submitPlayerCountButton = new Button("Set Player Count") {
    onAction = _ => {
      Try(playerCountInput.text.value.trim.toInt) match {
        case Success(count) =>
          controller.processPlayerAction(SetPlayerCountAction(count))
        case Failure(_) =>
          controller.processPlayerAction(
            InvalidAction
          )
      }
    }
  }

  private val playerNameInput = new TextField {
    promptText = "Player Name"
  }
  private val submitPlayerNameButton = new Button("Add Player") {
    onAction = _ => {
      controller.processPlayerAction(
        AddPlayerNameAction(playerNameInput.text.value)
      )
      if (!controller.gameState.lastEvent.contains(GameEvent.SetupError)) {
        playerNameInput.text = ""
      }
    }
  }

  private val deckSizeChoiceBox = new ComboBox[Int](
    (2 to 36).toVector
  ) {
    value = 36
    editable = true
  }
  private val submitDeckSizeButton = new Button("Set Deck Size") {
    onAction = _ => {
      Try(deckSizeChoiceBox.getEditor.getText.toInt) match {
        case Success(size) =>
          controller.processPlayerAction(SetDeckSizeAction(size))
        case Failure(_) =>
          controller.processPlayerAction(InvalidAction)
      }
    }
  }

  private val setupStatusLabel = new Label("Ready for setup.") {
    style = "-fx-text-fill: black;"
  }

  private val setupInputPane = new VBox(
    10,
    playerCountInput,
    submitPlayerCountButton,
    playerNameInput,
    submitPlayerNameButton,
    deckSizeChoiceBox,
    submitDeckSizeButton,
    setupStatusLabel
  ) {
    alignment = Pos.Center
    padding = Insets(20)
  }

  private val gameDisplayPane = new BorderPane {
    padding = Insets(10)
    top = new VBox {
      alignment = Pos.Center
      spacing = 5
      children = Seq(statusLabel, trumpDisplay)
    }

    center = new VBox {
      alignment = Pos.Center
      spacing = 10
      children = Seq(new Label("Table"), tableDisplay)
    }

    bottom = new VBox {
      alignment = Pos.Center
      spacing = 10
      children = Seq(new Label("Your Hand"), playerHandBox)
    }

    right = actionButtons
  }

  private val winnerLabel = new Label {
    style = "-fx-font-size: 48pt; -fx-font-weight: bold; -fx-text-fill: gold;"
  }

  private val winnerDisplayPane = new VBox {
    spacing = 20
    children = Seq(winnerLabel)
    alignment = Pos.Center
    style = "-fx-background-color: rgba(0, 0, 0, 0.75);"
    visible = false
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

  private def createRootPane(): StackPane = new StackPane {
    children = Seq(gameDisplayPane, setupInputPane, winnerDisplayPane)
  }

  override def update: Unit = {
    Platform.runLater {
      val gameState = controller.gameState
      updateWinnerDisplay(gameState)

      val isSetupPhase = gameState.gamePhase match {
        case SetupPhase | AskPlayerCountPhase | AskPlayerNamesPhase |
            AskDeckSizePhase | GameStartPhase =>
          true
        case _ => false
      }
      setupInputPane.visible = isSetupPhase
      gameDisplayPane.visible = !isSetupPhase

      if (isSetupPhase) {
        setupStatusLabel.text = gameState.description
        if (gameState.lastEvent.contains(GameEvent.SetupError)) {
          setupStatusLabel.style = "-fx-text-fill: red;"
        } else {
          setupStatusLabel.style = "-fx-text-fill: black;"
        }

        playerCountInput.visible = false
        submitPlayerCountButton.visible = false
        playerNameInput.visible = false
        submitPlayerNameButton.visible = false
        deckSizeChoiceBox.visible = false
        submitDeckSizeButton.visible = false

        gameState.gamePhase match {
          case SetupPhase | AskPlayerCountPhase =>
            playerCountInput.visible = true
            submitPlayerCountButton.visible = true
            playerCountInput.text =
              gameState.setupPlayerCount.map(_.toString).getOrElse("")
            if (gameState.lastEvent.contains(GameEvent.SetupError)) {
              setupStatusLabel.text = gameState.description
            } else {
              setupStatusLabel.text = "Enter number of players (2-6):"
            }

          case AskPlayerNamesPhase =>
            val expectedCount = gameState.setupPlayerCount.getOrElse(0)
            val currentNames = gameState.setupPlayerNames.size
            playerNameInput.visible = true
            submitPlayerNameButton.visible = true
            if (gameState.lastEvent.contains(GameEvent.SetupError)) {
              setupStatusLabel.text = gameState.description
            } else {
              setupStatusLabel.text =
                s"Enter name for player ${currentNames + 1} of ${expectedCount}:"
            }

          case AskDeckSizePhase =>
            deckSizeChoiceBox.visible = true
            submitDeckSizeButton.visible = true
            deckSizeChoiceBox.value = gameState.setupDeckSize.getOrElse(36)
            if (gameState.lastEvent.contains(GameEvent.SetupError)) {
              setupStatusLabel.text = gameState.description
            } else {
              setupStatusLabel.text = "Select deck size (2-36):"
            }

          case GameStartPhase =>
            setupStatusLabel.text = "Initializing game..."
          case _ =>
        }

      } else {
        updateActivePlayer(gameState)
        updateTrump(gameState)
        updateHand(gameState)
        updateTable(gameState)
        updateActionButtons(gameState)
      }
    }
  }

  private def updateWinnerDisplay(gameState: GameState): Unit = {
    import javafx.scene.effect.GaussianBlur

    winnerDisplayPane.visible = false
    gameDisplayPane.effect = null

    gameState.gamePhase match {
      case EndPhase =>
        gameState.lastEvent match {
          case Some(GameEvent.GameOver(winner, loserOpt)) =>
            val loserText = loserOpt.map(l => s" (${l.name} is the Durak!)").getOrElse("")
            winnerLabel.text = s"${winner.name} Wins!$loserText"
            winnerDisplayPane.visible = true
            gameDisplayPane.effect = new GaussianBlur(10)
          case _ =>
        }
      case _ =>
    }
  }

  private def updateActionButtons(gameState: GameState): Unit = {
    playCardButton.visible = false
    passButton.visible = false
    takeCardsButton.visible = false

    gameState.gamePhase match {
      case AttackPhase =>
        playCardButton.visible = true
        passButton.visible = true
      case DefensePhase =>
        playCardButton.visible = true
        takeCardsButton.visible = true
      case _ =>
    }
  }

  private def updateActivePlayer(gameState: GameState): Unit = {
    if (gameState.players.nonEmpty) {
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
    } else {
      statusLabel.text = gameState.description
    }
  }

  private def updateTrump(gameState: GameState): Unit = {
    if (gameState.deck.nonEmpty || gameState.players.nonEmpty) {
      val imagePath = cardToImagePath(gameState.trumpCard)
      val resourceStream = getClass.getResourceAsStream(s"/$imagePath")
      if (resourceStream != null) {
        trumpCardView.image = new Image(resourceStream, 70, 100, true, true)
      } else {
        trumpCardView.image = null
      }
    } else {
      trumpCardView.image = null
    }
  }

  private def updateHand(gameState: GameState): Unit = {
    if (gameState.players.nonEmpty) {
      val activePlayerIndex: Option[Int] =
        gameState.gamePhase match {
          case AttackPhase  => Some(gameState.attackerIndex)
          case DefensePhase => Some(gameState.defenderIndex)
          case _            => None
        }

      playerHandBox.children = activePlayerIndex
        .map(i => gameState.players(i).hand.map(createCardButton))
        .getOrElse(Seq.empty)
    } else {
      playerHandBox.children = Seq.empty
    }
  }

  private def updateTable(gameState: GameState): Unit = {
    if (gameState.players.nonEmpty) {
      val attackCards = gameState.table.map { case (attack, _) =>
        createCardView(attack)
      }
      val defenseCards = gameState.table.flatMap { case (_, defense) =>
        defense.map(createCardView)
      }
      attackerTable.children = attackCards
      defenderTable.children = defenseCards
    } else {
      attackerTable.children = Seq.empty
      defenderTable.children = Seq.empty
    }
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
