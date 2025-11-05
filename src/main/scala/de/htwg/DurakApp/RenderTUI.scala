package de.htwg.DurakApp

object RenderTUI:

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
    val playersStr = game.playerList
      .map(p => s"${p.name}'s hand:\n${renderHand(p.hand)}")
      .mkString("\n\n")
    val tableStr = s"Table:\n${renderTable(game.table)}"
    val trumpStr = s"Trump suit: ${game.trump}"
    s"$trumpStr\n\n$tableStr\n\n$playersStr"

  // def main(args: Array[String]): Unit =
    // val deck = (for {
    //   suit <- Suit.values
    //   rank <- Rank.values
    //   isTrump = false
    // } yield Card(suit, rank, isTrump)).toList

    // val shuffledDeck = scala.util.Random.shuffle(deck)
    // val trump = shuffledDeck.head.suit

    // val deckWithTrump = shuffledDeck.map(card => card.copy(isTrump = card.suit == trump))

    // val players = List(
    //   Player("Alice", deckWithTrump.take(5)),
    //   Player("Bob", deckWithTrump.slice(5, 10))
    // )

    // val game = GameState(players, deckWithTrump.drop(10), deckWithTrump.slice(10, 12), trump)
    // println(renderGame(game))
