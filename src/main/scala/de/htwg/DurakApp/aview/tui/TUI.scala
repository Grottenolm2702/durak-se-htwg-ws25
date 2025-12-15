package de.htwg.DurakApp.aview.tui

import de.htwg.DurakApp.aview.ViewInterface
import de.htwg.DurakApp.aview.tui.handler._
import de.htwg.DurakApp.controller.ControllerInterface.*
import de.htwg.DurakApp.model.ModelInterface.*
import de.htwg.DurakApp.model.ModelInterface.StateInterface.*

import scala.io.StdIn.readLine

class TUI(controller: Controller) extends ViewInterface {

  import TUI._

  val inputHandler: InputHandler = {
    val invalid = new InvalidInputHandler(None)
    val take = new TakeCardsHandler(Some(invalid))
    val pass = new PassHandler(Some(take))
    val play = new PlayCardHandler(Some(pass))
    val redo = new RedoHandler(controller, Some(play))
    val undo = new UndoHandler(controller, Some(redo))
    val gamePhaseHandler = new GamePhaseInputHandler(Some(undo))
    gamePhaseHandler
  }

  def run(): Unit = {
    controller.add(this)
    println(clearScreen())
    println("Willkommen bei Durak!")
    update
    gameLoop()
    println("Spiel beendet.")
  }

  @scala.annotation.tailrec
  private def gameLoop(): Unit = {
    val input = readLine()
    if (input == "q" || input == "quit") return
    val action = inputHandler.handleRequest(input, controller.gameState)
    action match {
      case UndoAction | RedoAction =>
      case _                       => controller.processPlayerAction(action)
    }
    controller.gameState.lastEvent match {
      case Some(GameEvent.ExitApplication) => ()
      case _                               => gameLoop()
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
    printPrompt(game)
  }

  def description(game: GameState): String = game.gamePhase match {
    case SetupPhase | AskPlayerCountPhase => "Spieleranzahl eingeben (2-6):"
    case AskPlayerNamesPhase =>
      s"Spielername ${game.setupPlayerNames.length + 1}:"
    case AskDeckSizePhase =>
      val minSize = game.setupPlayerNames.size
      s"Deckgröße eingeben ($minSize-36):"
    case AskPlayAgainPhase => "Möchten Sie eine neue Runde spielen? (yes/no):"
    case _                 => game.gamePhase.toString
  }

  private def printPrompt(game: GameState): Unit = {
    game.gamePhase match {
      case SetupPhase | AskPlayerCountPhase | AskPlayerNamesPhase |
          AskDeckSizePhase =>
        println(description(game))
        print("> ")
      case AttackPhase | DefensePhase | DrawPhase =>
        val activePlayer = game.gamePhase match {
          case AttackPhase =>
            val idx = game.currentAttackerIndex.getOrElse(game.attackerIndex)
            Some(game.players(idx))
          case DefensePhase => Some(game.players(game.defenderIndex))
          case _            => None
        }
        val moves = game.gamePhase match {
          case AttackPhase  => "('play index', 'pass', 'u', 'r')"
          case DefensePhase => "('play index', 'take', 'u', 'r')"
          case _            => ""
        }
        activePlayer match {
          case Some(player) =>
            println(s"$GREEN${player.name}$RESET, dein Zug $moves:")
          case None => println("Error: No active player. " + description(game))
        }
        print("> ")
      case _ =>
        println(description(game))
        print("> ")
    }
  }

  def clearScreen(): String = "\u001b[2J\u001b[H"

  private def cardColor(suit: Suit): String = suit match {
    case Suit.Hearts | Suit.Diamonds => RED
    case Suit.Clubs | Suit.Spades    => GREEN
  }
  private def cardSymbol(suit: Suit): String = suit match {
    case Suit.Hearts   => "\u2665"
    case Suit.Diamonds => "\u2666"
    case Suit.Clubs    => "\u2663"
    case Suit.Spades   => "\u2660"
  }
  private def cardRankStr(rank: Rank): String = rank match {
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

  def renderCard(card: Card): List[String] = {
    val w = cardWidth
    val inner = w - 2
    val color = cardColor(card.suit)
    val symbol = cardSymbol(card.suit)
    val rankStr = cardRankStr(card.rank)
    val top = "+" + "-" * inner + "+"

    val rankFieldWidth = math.min(2, inner)
    val rankPadded = rankStr.padTo(rankFieldWidth, ' ').take(rankFieldWidth)
    val rankRemaining = inner - rankFieldWidth
    val rankField = "|" + color + rankPadded + RESET + " " * rankRemaining + "|"

    val symbolLeft = (inner - 1) / 2
    val symbolRight = inner - 1 - symbolLeft
    val suitField =
      "|" + " " * symbolLeft + color + symbol + RESET + " " * symbolRight + "|"

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
      case AttackPhase =>
        val idx = game.currentAttackerIndex.getOrElse(game.attackerIndex)
        game.players(idx)
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
