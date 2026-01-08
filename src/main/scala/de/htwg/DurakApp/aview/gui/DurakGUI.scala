package de.htwg.DurakApp.aview.gui

import de.htwg.DurakApp.util.Observer

import de.htwg.DurakApp.controller.Controller
import de.htwg.DurakApp.controller.*

import de.htwg.DurakApp.model.{Card, Player, GameState, Rank, Suit}
import de.htwg.DurakApp.model.state.{GameEvent, GamePhases}

import com.google.inject.Inject

import scalafx.application.Platform
import scalafx.beans.binding.Bindings
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.*
import scalafx.scene.effect.GaussianBlur
import scalafx.stage.Stage
import scalafx.Includes.*
import scalafx.event.ActionEvent
import scalafx.collections.ObservableBuffer
import scala.util.{Try, Success, Failure}

class DurakGUI @Inject() (controller: Controller, val gamePhases: GamePhases)
    extends Observer {
  controller.add(this)

  private val selectedCard = ObjectProperty[Option[Card]](None)
  private val statusLabel = new Label("Welcome to Durak!")
  private val trumpCardView = new ImageView {
    fitWidth = 56
    fitHeight = 80
    preserveRatio = true
  }
  private val deckSizeLabel = new Label("Deck: 0") {}
  private val trumpDisplay = new HBox {
    spacing = 10
    alignment = Pos.Center
    children = Seq(new Label("Trump:"), trumpCardView, deckSizeLabel)
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
  private val playCardButton = new Button("Play Card") {
    onAction = (e: ActionEvent) => {
      selectedCard.value.foreach { card =>
        controller.processPlayerAction(PlayCardAction(card))
        selectedCard.value = None
      }
    }
    prefWidth = 120
  }
  private val passButton = new Button("Pass") {
    onAction = (e: ActionEvent) => {
      controller.processPlayerAction(PassAction)
    }
    prefWidth = 120
  }
  private val takeCardsButton = new Button("Take Cards") {
    onAction = (e: ActionEvent) => {
      controller.processPlayerAction(TakeCardsAction)
    }
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
    onAction = (e: ActionEvent) => {
      Try(playerCountInput.text.value.trim.toInt) match {
        case Success(count) =>
          controller.processPlayerAction(SetPlayerCountAction(count))
        case Failure(_) =>
          controller.processPlayerAction(InvalidAction)
      }
    }
  }
  private val playerNameInput = new TextField {
    promptText = "Player Name"
  }
  private val submitPlayerNameButton = new Button("Add Player") {
    onAction = (e: ActionEvent) => {
      controller.processPlayerAction(
        AddPlayerNameAction(playerNameInput.text.value)
      )
      if (!controller.gameState.lastEvent.contains(GameEvent.SetupError)) {
        playerNameInput.text = ""
      }
    }
  }
  private val deckSizeChoiceBox = new ComboBox[Int]((2 to 36).toVector) {
    value = 36
    editable = true
  }
  private val submitDeckSizeButton = new Button("Set Deck Size") {
    onAction = (e: ActionEvent) => {
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
  private val setupInputPane = new VBox {
    spacing = 10
    alignment = Pos.Center
    padding = Insets(20)
    children = Seq(
      playerCountInput,
      submitPlayerCountButton,
      playerNameInput,
      submitPlayerNameButton,
      deckSizeChoiceBox,
      submitDeckSizeButton,
      setupStatusLabel
    )
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
    children = Seq(
      winnerLabel,
      new HBox(10) {
        alignment = Pos.Center
        children = Seq(
          new Button("Play Again") {
            onAction = (e: ActionEvent) =>
              controller.processPlayerAction(PlayAgainAction)
          },
          new Button("Exit Game") {
            onAction =
              (e: ActionEvent) => controller.processPlayerAction(ExitGameAction)
          }
        )
      }
    )
    alignment = Pos.Center
    style = "-fx-background-color: rgba(0, 0, 0, 0.75);"
    visible = false
  }

  playerCountInput.onKeyReleased = event => {
    if (event.code == KeyCode.Enter) submitPlayerCountButton.fire()
  }
  playerNameInput.onKeyReleased = event => {
    if (event.code == KeyCode.Enter) submitPlayerNameButton.fire()
  }
  deckSizeChoiceBox.editor.value.onKeyReleased = event => {
    if (event.code == KeyCode.Enter) submitDeckSizeButton.fire()
  }

  def start(): Unit = {
    val stage = new Stage {
      title = "Durak - GUI"
      scene = new Scene(900, 600) {
        root = new StackPane {
          children = Seq(gameDisplayPane, setupInputPane, winnerDisplayPane)
        }
      }
    }
    stage.show()
    update
  }

  private def description(gameState: GameState): String = {
    val p = gameState.gamePhase
    if (p == gamePhases.setupPhase || p == gamePhases.askPlayerCountPhase) {
      "Enter number of players (2-6):"
    } else if (p == gamePhases.askPlayerNamesPhase) {
      s"Player name ${gameState.setupPlayerNames.length + 1}:"
    } else if (p == gamePhases.askDeckSizePhase) {
      val minSize = gameState.setupPlayerNames.size
      s"Select deck size ($minSize-36):"
    } else {
      gameState.gamePhase.toString
    }
  }

  override def update: Unit = Platform.runLater {
    val gameState = controller.gameState
    updateWinnerDisplay(gameState)
    val isSetupPhase = gamePhases.isAnySetupPhase(gameState.gamePhase)
    setupInputPane.visible = isSetupPhase
    gameDisplayPane.visible = !isSetupPhase
    if (isSetupPhase) {
      updateSetupInputs(gameState)
    } else {
      updateActivePlayer(gameState)
      updateTrump(gameState)
      updateHand(gameState)
      updateTable(gameState)
      updateActionButtons(gameState)
    }
  }

  private def updateSetupInputs(gameState: GameState): Unit = {
    val setupError = gameState.lastEvent.contains(GameEvent.SetupError)
    setupStatusLabel.text = description(gameState)
    setupStatusLabel.style =
      if (setupError) "-fx-text-fill: red;" else "-fx-text-fill: black;"
    playerCountInput.visible = false
    submitPlayerCountButton.visible = false
    playerNameInput.visible = false
    submitPlayerNameButton.visible = false
    deckSizeChoiceBox.visible = false
    submitDeckSizeButton.visible = false
    val p = gameState.gamePhase
    if (p == gamePhases.setupPhase || p == gamePhases.askPlayerCountPhase) {
      playerCountInput.visible = true
      submitPlayerCountButton.visible = true
      playerCountInput.text =
        gameState.setupPlayerCount.map(_.toString).getOrElse("")
      setupStatusLabel.text =
        if (setupError) description(gameState)
        else "Enter number of players (2-6):"
    } else if (p == gamePhases.askPlayerNamesPhase) {
      val expectedCount = gameState.setupPlayerCount.getOrElse(0)
      val currentNames = gameState.setupPlayerNames.size
      playerNameInput.visible = true
      submitPlayerNameButton.visible = true
      setupStatusLabel.text =
        if (setupError) description(gameState)
        else s"Enter name for player ${currentNames + 1} of $expectedCount:"
    } else if (p == gamePhases.askDeckSizePhase) {
      val minSize = gameState.setupPlayerNames.size
      deckSizeChoiceBox.items = ObservableBuffer.from((minSize to 36))
      deckSizeChoiceBox.visible = true
      submitDeckSizeButton.visible = true
      val defaultSize =
        math.max(minSize, gameState.setupDeckSize.getOrElse(36))
      deckSizeChoiceBox.value = defaultSize
      setupStatusLabel.text =
        if (setupError) description(gameState)
        else s"Select deck size ($minSize-36):"
    } else if (p == gamePhases.gameStartPhase) {
      setupStatusLabel.text = "Initializing game..."
    }
  }

  private def updateWinnerDisplay(gameState: GameState): Unit = {
    winnerDisplayPane.visible = false
    gameDisplayPane.effect = null
    if (gamePhases.isAskPlayAgainPhase(gameState.gamePhase)) {
      gameState.lastEvent match {
        case Some(GameEvent.GameOver(winner, loserOpt)) =>
          val loserText =
            loserOpt.fold("")(l => s" (${l.name} is the Durak!)")
          winnerLabel.text = s"${winner.name} Wins!$loserText"
          winnerDisplayPane.visible = true
          gameDisplayPane.effect = new GaussianBlur(10)
        case Some(GameEvent.ExitApplication) =>
          Platform.exit()
        case _ =>
      }
    }
  }

  private def updateActionButtons(gameState: GameState): Unit = {
    val (playVisible, passVisible, takeVisible) =
      if (gamePhases.isAttackPhase(gameState.gamePhase)) {
        (true, true, false)
      } else if (gamePhases.isDefensePhase(gameState.gamePhase)) {
        (true, false, true)
      } else {
        (false, false, false)
      }
    playCardButton.visible = playVisible
    passButton.visible = passVisible
    takeCardsButton.visible = takeVisible
  }

  private def activePlayerIndex(gameState: GameState): Option[Int] =
    if (gamePhases.isAttackPhase(gameState.gamePhase)) {
      Some(gameState.attackerIndex)
    } else if (gamePhases.isDefensePhase(gameState.gamePhase)) {
      Some(gameState.defenderIndex)
    } else {
      None
    }

  private def updateActivePlayer(gameState: GameState): Unit = {
    statusLabel.text =
      if (gameState.players.nonEmpty)
        activePlayerIndex(gameState)
          .map(i =>
            s"${gameState.players(i).name}'s turn (${description(gameState)})"
          )
          .getOrElse(description(gameState))
      else
        description(gameState)
  }

  private def updateTrump(gameState: GameState): Unit = {
    trumpCardView.image = (
      if (gameState.deck.nonEmpty || gameState.players.nonEmpty)
        Option(loadCardImage(gameState.trumpCard))
      else
        None
    ).orNull
    deckSizeLabel.text.value = s"Deck: ${gameState.deck.size}"
  }

  private def loadCardImage(card: Card): Image = {
    val imagePath = cardToImagePath(card)
    Option(getClass.getResourceAsStream(s"/$imagePath"))
      .map(new Image(_, 70, 100, true, true))
      .orNull
  }

  private def updateHand(gameState: GameState): Unit = {
    playerHandBox.children = (
      for {
        i <- activePlayerIndex(gameState)
        player <- gameState.players.lift(i)
      } yield player.hand.map(createCardButton)
    ).getOrElse(Seq.empty)
  }

  private def updateTable(gameState: GameState): Unit = {
    if (gameState.players.nonEmpty) {
      attackerTable.children = gameState.table.map { case (attack, _) =>
        createCardView(attack)
      }
      defenderTable.children = gameState.table.flatMap { case (_, defense) =>
        defense.map(createCardView)
      }
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
    Option(loadCardImage(card))
      .map(new ImageView(_))
      .getOrElse(
        new ImageView(
          new Image(
            getClass.getResourceAsStream("/cards/placeholder.png"),
            70,
            100,
            false,
            false
          )
        )
      )
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
      onAction = (e: ActionEvent) => {
        selectedCard.value = Some(card)
      }
    }
  }
}
