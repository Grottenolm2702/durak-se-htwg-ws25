package de.htwg.DurakApp.aview.tui

import de.htwg.DurakApp.aview.tui.handler.{
  InputHandler,
  InvalidInputHandler,
  PassHandler,
  PlayCardHandler,
  TakeCardsHandler,
  UndoHandler,
  RedoHandler
}
import de.htwg.DurakApp.controller.Controller
import de.htwg.DurakApp.model.*
import de.htwg.DurakApp.model.state.*
import de.htwg.DurakApp.util.Observer
import de.htwg.DurakApp.controller.{
  PlayerAction,
  PlayCardAction,
  PassAction,
  TakeCardsAction,
  InvalidAction,
  UndoAction,
  RedoAction
}

import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

class TUI(controller: Controller) extends Observer {

  private val cardWidth = 7

  val RED = "\u001b[31m"
  val GREEN = "\u001b[32m"
  val RESET = "\u001b[0m"

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

  def parseTuiInput(input: String, game: GameState): PlayerAction = {
    inputHandler.handleRequest(input, game)
  }

  @scala.annotation.tailrec
  private def gameLoop(): Unit = {
    printPrompt(controller.gameState)
    val input = readLine()
    if (input == "q" || input == "quit") {
      ()
    } else {
      val action = parseTuiInput(input, controller.gameState)
      action match {
        case UndoAction | RedoAction =>
        case _                       => controller.processPlayerAction(action)
      }

      controller.gameState.lastEvent match {
        case Some(GameEvent.GameOver(_, _)) =>
        case _                              => gameLoop()
      }
    }
  }

  override def update: Unit = {
    println(clearScreen())
    val game = controller.gameState
    val render = renderScreen(game, buildStatusString(game))
    println(render)
  }

  def askForDeckSize(inputReader: () => String = readLine): Int = {
    println("Anzahl Karten im Deck (z.B. 36 für Standard): ")
    Try(inputReader().trim.toInt) match {
      case Success(value) => value.max(2)
      case Failure(e) =>
        println(
          s"Ungültige Eingabe (${e.getMessage}). Verwende Standardwert 36."
        )
        36
    }
  }

  def askForPlayerCount(inputReader: () => String = readLine): Int = {
    println("Spieleranzahl?")
    Try(inputReader().trim.toInt) match {
      case Success(value) => value.max(2)
      case Failure(e) =>
        println(
          s"Ungültige Eingabe (${e.getMessage}). Verwende Standardwert 2."
        )
        2
    }
  }

  def askForPlayerNames(
      count: Int,
      inputReader: () => String = readLine
  ): List[String] = {
    (1 to count).map { i =>
      println(s"Enter name of player $i: ")
      inputReader().trim() match {
        case "" => s"Player$i"
        case n  => n
      }
    }.toList
  }

  private def printPrompt(game: GameState): Unit = {
    val activePlayer = game.gamePhase match {
      case AttackPhase  => game.players(game.attackerIndex)
      case DefensePhase => game.players(game.defenderIndex)
      case _            => game.players(game.attackerIndex)
    }
    val moves = game.gamePhase match {
      case AttackPhase  => "('play index', 'pass', 'u', 'r')"
      case DefensePhase => "('play index', 'take', 'u', 'r')"
      case _            => "('play index', 'pass', 'take', 'u', 'r')"
    }
    println(s"$GREEN${activePlayer.name}$RESET, dein Zug ${moves}:")
    print("> ")
  }

  def clearScreen(): String = "\u001b[2J\u001b[H"

  def renderCard(card: Card): List[String] = {
    val w = cardWidth
    val inner = w - 2

    val (colorStart, colorEnd) = card.suit match {
      case Suit.Hearts | Suit.Diamonds => (RED, RESET)
      case Suit.Clubs | Suit.Spades    => (GREEN, RESET)
    }

    val symbol = card.suit match
      case Suit.Hearts   => "\u2665"
      case Suit.Diamonds => "\u2666"
      case Suit.Clubs    => "\u2663"
      case Suit.Spades   => "\u2660"

    val rankStr = card.rank.match
      case Rank.Six   => "6"
      case Rank.Seven => "7"
      case Rank.Eight => "8"
      case Rank.Nine  => "9"
      case Rank.Ten   => "10"
      case Rank.Jack  => "J"
      case Rank.Queen => "Q"
      case Rank.King  => "K"
      case Rank.Ace   => "A"

    val top = "+" + "-".repeat(inner) + "+"

    val rankFieldWidth = math.min(2, inner)
    val rankPadded =
      if (rankStr.length >= rankFieldWidth) rankStr.take(rankFieldWidth)
      else rankStr + " ".repeat(rankFieldWidth - rankStr.length)
    val rankRemaining = inner - rankFieldWidth
    val rankField =
      "|" + colorStart + rankPadded + colorEnd + " ".repeat(rankRemaining) + "|"

    val symbolLeft = (inner - 1) / 2
    val symbolRight = inner - 1 - symbolLeft
    val suitField =
      "|" + " ".repeat(symbolLeft) + colorStart + symbol + colorEnd + " "
        .repeat(symbolRight) + "|"

    val emptyLine = "|" + " ".repeat(inner) + "|"

    List(top, rankField, suitField, emptyLine, top)
  }

  private[aview] def combineCardLines(cards: List[List[String]]): String = {
    if (cards.isEmpty) ""
    else cards.transpose.map(_.mkString(" ")).mkString("\n")
  }

  def renderHandWithIndices(hand: List[Card]): String = {
    if (hand.isEmpty) "Leere Hand"
    else {
      val cardLines = hand.map(renderCard)
      val cardsBlock = combineCardLines(cardLines)
      val indexCells = hand.indices.map { i =>
        val s = i.toString
        val total = cardWidth
        val left = (total - s.length) / 2
        val right = total - s.length - left
        " " * left + s + " " * right
      }
      val indexLine = indexCells.mkString(" ")
      s"$cardsBlock\n$indexLine"
    }
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

  def buildStatusString(game: GameState): String = {
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
      }
      .getOrElse(
        game.gamePhase match {
          case SetupPhase =>
            if (game.players.isEmpty) "Willkommen bei Durak!"
            else "Spieler werden eingerichtet."
          case _ => game.gamePhase.toString
        }
      )
  }
}
