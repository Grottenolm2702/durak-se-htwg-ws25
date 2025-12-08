package de.htwg.DurakApp.aview.tui

import de.htwg.DurakApp.aview.tui.handler._
import de.htwg.DurakApp.controller._
import de.htwg.DurakApp.model.*
import de.htwg.DurakApp.model.state.*
import de.htwg.DurakApp.util.Observer

import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

class TUI(controller: Controller) extends Observer {

  import TUI._

  private val inputHandler: InputHandler = {
    val invalid = new InvalidInputHandler()
    val take = new TakeCardsHandler(Some(invalid))
    val pass = new PassHandler(Some(take))
    val play = new PlayCardHandler(Some(pass))
    val redo = new RedoHandler(controller, Some(play))
    val undo = new UndoHandler(controller, Some(redo))
    undo
  }

  def run(): Unit = {
    println(clearScreen())
    println("Willkommen bei Durak!")
    update
    gameLoop()
    println("Spiel beendet.")
  }

  def parseTuiInput(input: String, game: GameState): PlayerAction =
    game.gamePhase match {
      case SetupPhase | AskPlayerCountPhase =>
        Try(input.trim.toInt)
          .map(SetPlayerCountAction.apply)
          .getOrElse(InvalidAction)
      case AskPlayerNamesPhase =>
        AddPlayerNameAction(input.trim)
      case AskDeckSizePhase =>
        Try(input.trim.toInt)
          .map(SetDeckSizeAction.apply)
          .getOrElse(InvalidAction)
      case AskPlayAgainPhase =>
        input.trim.toLowerCase match {
          case "yes" => PlayAgainAction
          case "no"  => ExitGameAction
          case _     => InvalidAction
        }
      case _ =>
        inputHandler.handleRequest(input, game)
    }

  @scala.annotation.tailrec
  private def gameLoop(): Unit = {
    val input = readLine()
    if (input == "q" || input == "quit") ()
    else {
      val action = parseTuiInput(input, controller.gameState)
      action match {
        case UndoAction | RedoAction =>
        case _                       => controller.processPlayerAction(action)
      }

      // After processing the action, check the current game state for termination or continuation
      controller.gameState.lastEvent match {
        case Some(GameEvent.ExitApplication) => () // Terminate loop
        case _                               => gameLoop() // Continue loop for all other events
      }
    }
  }

  override def update: Unit = {
    println(clearScreen())
    val game = controller.gameState
    val render = game.gamePhase match {
      case SetupPhase | AskPlayerCountPhase | AskPlayerNamesPhase |
          AskDeckSizePhase =>
        buildStatusString(game)
      case _ =>
        renderScreen(game, buildStatusString(game))
    }
    println(render)
    printPrompt(controller.gameState)
  }

  private def description(game: GameState): String = game.gamePhase match {
    case SetupPhase | AskPlayerCountPhase => "Spieleranzahl eingeben (2-6):"
    case AskPlayerNamesPhase =>
      s"Spielername ${game.setupPlayerNames.length + 1}:"
    case AskDeckSizePhase => "Deckgröße eingeben (2-36):"
    case AskPlayAgainPhase => "Möchten Sie eine neue Runde spielen? (yes/no):"
    case _                => game.gamePhase.toString
  }

  private def printPrompt(game: GameState): Unit = {
    game.gamePhase match {
      case SetupPhase | AskPlayerCountPhase | AskPlayerNamesPhase |
          AskDeckSizePhase =>
        println(description(game))
        print("> ")
      case _ =>
        val activePlayer = game.gamePhase match {
          case AttackPhase  => game.players(game.attackerIndex)
          case DefensePhase => game.players(game.defenderIndex)
          case _            => null
        }
        val moves = game.gamePhase match {
          case AttackPhase  => "('play index', 'pass', 'u', 'r')"
          case DefensePhase => "('play index', 'take', 'u', 'r')"
          case _            => ""
        }
        if (activePlayer != null)
          println(s"$GREEN${activePlayer.name}$RESET, dein Zug $moves:")
        else
          println(description(game))
        print("> ")
    }
  }

  def clearScreen(): String = "\u001b[2J\u001b[H"

  def renderCard(card: Card): List[String] = {
    val w = cardWidth
    val inner = w - 2

    val (colorStart, colorEnd) = card.suit match {
      case Suit.Hearts | Suit.Diamonds => (RED, RESET)
      case Suit.Clubs | Suit.Spades    => (GREEN, RESET)
    }

    val symbol = card.suit match {
      case Suit.Hearts   => "\u2665"
      case Suit.Diamonds => "\u2666"
      case Suit.Clubs    => "\u2663"
      case Suit.Spades   => "\u2660"
    }

    val rankStr = card.rank match {
      case Rank.Six   => "6"
      case Rank.Seven => "7"
      case Rank.Eight => "8"
      case Rank.Nine  => "9"
      case Rank.Ten   => "10"
      case Rank.Jack  => "J"
      case Rank.Queen => "Q"
      case Rank.King  => "K"
      case Rank.Ace   => "A"
    }

    val top = "+" + "-" * inner + "+"

    val rankFieldWidth = math.min(2, inner)
    val rankPadded =
      if (rankStr.length >= rankFieldWidth) rankStr.take(rankFieldWidth)
      else rankStr + " " * (rankFieldWidth - rankStr.length)
    val rankRemaining = inner - rankFieldWidth
    val rankField =
      "|" + colorStart + rankPadded + colorEnd + " " * rankRemaining + "|"

    val symbolLeft = (inner - 1) / 2
    val symbolRight = inner - 1 - symbolLeft
    val suitField =
      "|" + " " * symbolLeft + colorStart + symbol + colorEnd + " " * symbolRight + "|"

    val emptyLine = "|" + " " * inner + "|"

    List(top, rankField, suitField, emptyLine, top)
  }

  private[aview] def combineCardLines(cards: List[List[String]]): String =
    if (cards.isEmpty) ""
    else cards.transpose.map(_.mkString(" ")).mkString("\n")

  def renderHandWithIndices(hand: List[Card]): String =
    if (hand.isEmpty) "Leere Hand"
    else {
      val cardLines = hand.map(renderCard)
      val cardsBlock = combineCardLines(cardLines)
      val indexLine = hand.indices
        .map { i =>
          val s = i.toString
          val total = cardWidth
          val left = (total - s.length) / 2
          val right = total - s.length - left
          " " * left + s + " " * right
        }
        .mkString(" ")
      s"$cardsBlock\n$indexLine"
    }

  def renderTable(game: GameState): String = {
    val attackingCards = game.table.keys.toList
    val defendingCards = game.table.values.flatten.toList

    val attackHeader = s"Angriff (${attackingCards.length})"
    val attackRender =
      if (attackingCards.isEmpty) "  Leer"
      else combineCardLines(attackingCards.map(renderCard))

    val defenseHeader = s"Verteidigung (${defendingCards.length})"
    val defenseRender =
      if (defendingCards.isEmpty) "  Leer"
      else combineCardLines(defendingCards.map(renderCard))

    s"$attackHeader\n$attackRender\n\n$defenseHeader\n$defenseRender"
  }

  def renderScreen(game: GameState, status: String): String = {
    val header =
      s"Trumpf: ${game.trumpCard.suit}    Deck: ${game.deck.length}    Ablagestapel: ${game.discardPile.length}"

    val table = renderTable(game)

    val activePlayer = game.gamePhase match {
      case AttackPhase  => game.players(game.attackerIndex)
      case DefensePhase => game.players(game.defenderIndex)
      case _            => game.players(game.attackerIndex)
    }

    val playersStr = game.players
      .map { p =>
        val playerName =
          if (p == activePlayer) s"$GREEN${p.name}$RESET" else p.name
        s"$playerName (Karten: ${p.hand.length})\n${renderHandWithIndices(p.hand)}"
      }
      .mkString("\n\n")

    val statusLine = s"Status: $status"

    s"""
$header

$table

$playersStr

$statusLine
""".trim
  }

  def buildStatusString(game: GameState): String =
    game.lastEvent
      .map {
        case GameEvent.InvalidMove  => s"${RED}Ungültiger Zug!$RESET"
        case GameEvent.NotYourTurn  => s"${RED}Du bist nicht am Zug!$RESET"
        case GameEvent.Attack(card) => s"Angriff mit ${card.rank} ${card.suit}."
        case GameEvent.Defend(card) =>
          s"Verteidigung mit ${card.rank} ${card.suit}."
        case GameEvent.Pass => "Passen."
        case GameEvent.Take => "Karten aufgenommen."
        case GameEvent.Draw => "Karten werden gezogen."
        case GameEvent.RoundEnd(cleared) =>
          if (cleared) "Runde vorbei, Tisch geleert."
          else "Runde vorbei, Karten aufgenommen."
        case GameEvent.GameOver(_, Some(loser)) =>
          s"Spiel beendet! ${loser.name} ist der Durak!"
        case GameEvent.GameOver(winner, None) if winner.name == "Quit" =>
          s"Spiel beendet."
        case GameEvent.GameOver(_, None) =>
          s"Spiel beendet! Es gibt keinen Durak (Unentschieden oder alle gewonnen)!"
        case GameEvent.CannotUndo => s"${RED}Nichts zum Rückgängigmachen!$RESET"
        case GameEvent.CannotRedo => s"${RED}Nichts zum Wiederherstellen!$RESET"
        case GameEvent.SetupError =>
          s"${RED}Setup-Fehler: ${description(game)}$RESET"
        case GameEvent.GameSetupComplete =>
          s"${GREEN}Setup abgeschlossen! Starte Spiel...$RESET"
        case GameEvent.AskPlayAgain =>
          s"${GREEN}Möchten Sie eine neue Runde spielen? (yes/no)$RESET"
        case GameEvent.ExitApplication =>
          s"${GREEN}Anwendung wird beendet...$RESET"
        case GameEvent.AskPlayerCount | GameEvent.AskPlayerNames |
            GameEvent.AskDeckSize =>
          ""
      }
      .getOrElse(
        game.gamePhase match {
          case SetupPhase |
              AskPlayerCountPhase | AskPlayerNamesPhase | AskDeckSizePhase =>
            ""
          case _ => game.gamePhase.toString
        }
      )
}

object TUI {
  val cardWidth = 7
  val RED = "\u001b[31m"
  val GREEN = "\u001b[32m"
  val RESET = "\u001b[0m"
}
