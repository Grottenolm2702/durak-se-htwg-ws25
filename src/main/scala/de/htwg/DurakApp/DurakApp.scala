package de.htwg.DurakApp
object DurakApp:

  def main(args: Array[String]): Unit =

    enum Suit:
      case Hearts, Diamonds, Clubs, Spades

    enum Rank(val value: Int):
      case Six extends Rank(6)
      case Seven extends Rank(7)
      case Eight extends Rank(8)
      case Nine extends Rank(9)
      case Ten extends Rank(10)
      case Jack extends Rank(11)
      case Queen extends Rank(12)
      case King extends Rank(13)
      case Ace extends Rank(14)

    case class Card(suit: Suit, rank: Rank)
    case class Player(name: String, hand: List[Card] = List())
    case class GameState(
        players: List[Player],
        deck: List[Card],
        table: List[Card] = List(),
        trump: Suit
    )

    def renderCard(card: Card): List[String] =
      val symbol = card.suit match
        case Suit.Hearts   => "\u001b[31m♥\u001b[0m"
        case Suit.Diamonds => "\u001b[31m♦\u001b[0m"
        case Suit.Clubs    => "\u001b[32m♣\u001b[0m"
        case Suit.Spades   => "\u001b[32m♠\u001b[0m"
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
      List(
        "+-----+",
        f"|$rankStr%-2s   |",
        s"|  $symbol  |",
        "|     |",
        "+-----+"
      )

    def renderHand(hand: List[Card]): String =
      if hand.isEmpty then "Empty hand"
      else
        val lines = hand.map(renderCard)
        val combined = lines.transpose.map(_.mkString(" "))
        combined.mkString("\n")

    def renderTable(table: List[Card]): String =
      if table.isEmpty then "Table is empty"
      else
        val lines = table.map(renderCard)
        val combined = lines.transpose.map(_.mkString(" "))
        combined.mkString("\n")

    def renderGame(game: GameState): String =
      val playersStr = game.players
        .map(p => s"${p.name}'s hand:\n${renderHand(p.hand)}")
        .mkString("\n\n")
      val tableStr = s"Table:\n${renderTable(game.table)}"
      val trumpStr = s"Trump suit: ${game.trump}"
      s"$trumpStr\n\n$tableStr\n\n$playersStr"

    // --- Spiel initialisieren ---
    val deck = (for {
      suit <- Suit.values
      rank <- Rank.values
    } yield Card(suit, rank)).take(52).toList

    val shuffledDeck = scala.util.Random.shuffle(deck.toList)
    val trump = shuffledDeck.head.suit

    // Spieler mit Beispielkarten füllen (je 5 Karten)
    val aliceHand = shuffledDeck.take(5)
    val bobHand = shuffledDeck.slice(5, 10)
    val remainingDeck = shuffledDeck.drop(10)

    val players = List(
      Player("Alice", aliceHand),
      Player("Bob", bobHand)
    )

    val tableCards = List(
      shuffledDeck(10),
      shuffledDeck(11)
    ) // Beispielhafte Karten auf dem Tisch

    val game = GameState(players, remainingDeck, tableCards, trump)

    // --- Spiel anzeigen ---
    println(renderGame(game))
