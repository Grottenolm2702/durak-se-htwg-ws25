package de.htwg.DurakApp
package aview

import controller.Controller
import model.Card
import model.Suit
import model.Rank
import model.GameState
import model.Player
import model.GameStatus

import de.htwg.DurakApp.util.Observer

import scala.io.StdIn.readLine
import scala.util.Try
import de.htwg.DurakApp.controller.PlayerInput

class TUI(controller: Controller) extends Observer with PlayerInput:

  private val cardWidth = 7
  private val cardHeight = 5

  val RED = "\u001b[31m"
  val GREEN = "\u001b[32m"
  val RESET = "\u001b[0m"

  def askForDeckSize(inputReader: () => String = readLine): Int = {
    println("Anzahl Karten im Deck [36]: ")
    Try(inputReader().trim.toInt).getOrElse(36)
  }

  def askForPlayerCount(inputReader: () => String = readLine): Int = {
    println("How many players?")
    Try(inputReader().trim.toInt).getOrElse(2).max(2)
  }

  def askForPlayerNames(
      count: Int,
      inputReader: () => String = readLine
  ): List[String] = {
    (1 to count).map { i =>
      println(s"Enter name of player $i: ")
      inputReader().trim match {
        case "" => s"Player$i"
        case n  => n
      }
    }.toList
  }

  def clearScreen(): String =
    "\u001b[2J\u001b[H"

  def renderCard(card: Card): List[String] =
    val (colorStart, colorEnd) = card.suit match
      case Suit.Hearts | Suit.Diamonds => (RED, RESET)
      case Suit.Clubs | Suit.Spades    => (GREEN, RESET)

    val symbol = card.suit match
      case Suit.Hearts   => "\u2665" // ♥
      case Suit.Diamonds => "\u2666" // ♦
      case Suit.Clubs    => "\u2663" // ♣
      case Suit.Spades   => "\u2660" // ♠

    val rankStr = card.rank match
      case Rank.Six   => "6"
      case Rank.Seven => "7"
      case Rank.Eight => "8"
      case Rank.Nine  => "9"
      case Rank.Ten   => "10"
      case Rank.Jack  => "J"
      case Rank.Queen => "Q"
      case Rank.King  => "K"
      case Rank.Ace   => "A"

    val rankField = f"|$colorStart$rankStr%-2s$colorEnd   |"
    val suitField = s"|  $colorStart$symbol$colorEnd  |"
    List("+-----+", rankField, suitField, "|     |", "+-----+")

  private def combineCardLines(cards: List[List[String]]): String =
    if cards.isEmpty then ""
    else
      val transposed = cards.transpose
      val combinedLines = transposed.map(_.mkString(" "))
      combinedLines.mkString("\n")

  def renderHandWithIndices(hand: List[Card]): String =
    if hand.isEmpty then "Empty hand"
    else
      val cardLines: List[List[String]] = hand.map(renderCard)
      val cardsBlock = combineCardLines(cardLines)
      val indexCells = hand.zipWithIndex.map { case (_, i) =>
        val s = i.toString
        val total = cardWidth
        val left = (total - s.length) / 2
        val right = total - s.length - left
        " " * left + s + " " * right
      }
      val indexLine = indexCells.mkString(" ")
      s"$cardsBlock\n$indexLine"

  def renderTableLine(label: String, table: List[Card]): String =
    val header = s"$label (${table.length})"
    if table.isEmpty then s"$header:\n  Empty"
    else
      val cardLines = table.map(renderCard)
      val combined = combineCardLines(cardLines)
      s"$header:\n$combined"

  def renderScreen(game: GameState, status: String): String =
    val header =
      val deckInfo = s"Deck: ${game.deck.length}"
      val discardInfo = s"Discard: ${game.discardPile.length}"
      val trumpInfo = s"Trump: ${game.trump}"
      s"$trumpInfo    $deckInfo    $discardInfo"

    val attacking = renderTableLine("Attacking", game.attackingCards)
    val defending = renderTableLine("Defending", game.defendingCards)

    val playersStr = game.playerList
      .map { p =>
        val nameLine =
          if p.isDone then s"\u001b[2m${p.name} (fertig)\u001b[0m" // grau
          else s"${p.name} (cards: ${p.hand.length})"
        val handBlock =
          if p.isDone then "----" else renderHandWithIndices(p.hand)
        s"$nameLine\n$handBlock"
      }
      .mkString("\n\n")

    val statusLine =
      if status == null || status.trim.isEmpty then "Status: ready"
      else s"Status: $status"

    s"""
$header

$attacking

$defending

$playersStr

$statusLine
""".trim

  def buildStatusString(game: GameState): String =
    game.status match {
      case GameStatus.WELCOME      => "Willkommen bei Durak!"
      case GameStatus.PLAYER_SETUP => "Spieler werden eingerichtet."
      case GameStatus.ATTACK =>
        val attacker = game.playerList(game.activePlayerId)
        s"Angreifer ${attacker.name} ist am Zug."
      case GameStatus.DEFEND =>
        val defender = game.playerList(game.activePlayerId)
        s"Verteidiger ${defender.name} ist am Zug."
      case GameStatus.TAKE =>
        val player = game.playerList(game.activePlayerId)
        s"${player.name} nimmt die Karten."
      case GameStatus.PASS =>
        val player = game.playerList(game.activePlayerId)
        s"${player.name} hat gepasst."
      case GameStatus.INVALID_MOVE => "Ungültiger Zug!"
      case GameStatus.GAME_OVER =>
        val loserOpt = game.playerList.find(p => !p.isDone && p.hand.nonEmpty)
        loserOpt match {
          case Some(p) => s"Spiel beendet! ${p.name} ist der Durak!"
          case None    => "Spiel beendet! Unentschieden."
        }
      case GameStatus.QUIT => "Spiel beendet."
    }

  def cardShortString(card: Card): String =
    s"${card.rank.toString} ${card.suit.toString}${
        if card.isTrump then " (T)" else ""
      }"

  override def update: Unit = {
    val clear = clearScreen()
    println(clear)
    val game = controller.game
    val status = buildStatusString(game)
    val render = renderScreen(game, status)
    println(render)
  }

  override def chooseAttackCard(attacker: Player, game: GameState): Int =
    println(s"${attacker.name}, wähle Karte-Index zum Angreifen oder 'pass':")
    readLine().trim match {
      case "pass" => -1
      case s => Try(s.toInt).getOrElse(-2) // Using -2 for invalid integer input
    }

  override def chooseDefenseCard(
      defender: Player,
      attackCard: Card,
      game: GameState
  ): Int =
    println(
      s"${defender.name}, verteidige gegen ${cardShortString(attackCard)} oder 'take':"
    )
    readLine().trim match {
      case "take" => -1
      case s => Try(s.toInt).getOrElse(-2) // Using -2 for invalid integer input
    }
