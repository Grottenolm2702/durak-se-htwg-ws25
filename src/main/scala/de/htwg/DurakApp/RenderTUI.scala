package de.htwg.DurakApp

object RenderTUI:

  private val cardWidth = 7 // "+-----+" == 7 chars
  private val cardHeight = 5 // number of lines per card ASCII art

  /** ANSI clear & cursor home */
  def clearScreen(): Unit =
    print("\u001b[2J\u001b[H")

  /** Render a single card as list of lines (fixed width) */
  def renderCard(card: Card): List[String] =
    val symbol = card.suit match
      case Suit.Hearts   => "\u2665"   // ♥
      case Suit.Diamonds => "\u2666"   // ♦
      case Suit.Clubs    => "\u2663"   // ♣
      case Suit.Spades   => "\u2660"   // ♠
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
    val rankField =
      // left-align rank in a 2-char area (supports "10")
      f"|$rankStr%-2s   |"
    val suitField = s"|  $symbol  |"
    val mid = List("+-----+", rankField, suitField, "|     |", "+-----+")
    // mark trump visually by lowercasing card symbol? We'll add an indicator (T) in a separate status row if needed.
    mid

  /** Render an empty card slot (keeps layout stable when hand shorter) */
  def renderEmptySlot(): List[String] =
    List("       ", "       ", "       ", "       ", "       ")

  /** Combine several cards horizontally, returns full multi-line string */
  private def combineCardLines(cards: List[List[String]]): String =
    if cards.isEmpty then ""
    else
      val transposed = cards.transpose
      val combinedLines = transposed.map(_.mkString(" "))
      combinedLines.mkString("\n")

  /** Render a hand with indices under the cards (stable width) */
  def renderHandWithIndices(hand: List[Card]): String =
    if hand.isEmpty then "Empty hand"
    else
      val cardLines: List[List[String]] = hand.map(renderCard)
      val cardsBlock = combineCardLines(cardLines)
      // build index line with center-aligned indices under each card
      val indexCells = hand.zipWithIndex.map { case (_, i) =>
        val s = i.toString
        val total = cardWidth
        val left = (total - s.length) / 2
        val right = total - s.length - left
        " " * left + s + " " * right
      }
      val indexLine = indexCells.mkString(" ")
      s"$cardsBlock\n$indexLine"

  /** Render table (attacking or defending) - one row of cards */
  def renderTableLine(label: String, table: List[Card]): String =
    val header = s"$label (${table.length})"
    if table.isEmpty then s"$header:\n  Empty"
    else
      val cardLines = table.map(renderCard)
      val combined = combineCardLines(cardLines)
      s"$header:\n$combined"

  /** Render full screen: header (status), table, players */
  def renderScreen(game: GameState, status: String): String =
    val header =
      val deckInfo = s"Deck: ${game.deck.length}"
      val discardInfo = s"Discard: ${game.discardPile.length}"
      val trumpInfo = s"Trump: ${game.trump}"
      s"$trumpInfo    $deckInfo    $discardInfo"

    val attacking = renderTableLine("Attacking", game.attackingCards)
    val defending = renderTableLine("Defending", game.defendingCards)

    // render each player's hand with name and indices; keep ordering stable
    val playersStr = game.playerList.map { p =>
      val nameLine = s"${p.name} (cards: ${p.hand.length})"
      val handBlock = renderHandWithIndices(p.hand)
      s"$nameLine\n$handBlock"
    }.mkString("\n\n")

    // status line at bottom
    val statusLine = if status == null || status.trim.isEmpty then "Status: ready" else s"Status: $status"

    s"""
$header

$attacking

$defending

$playersStr

$statusLine
""".trim

  /** Clear screen and print the rendered UI */
  def clearAndRender(game: GameState, status: String = ""): Unit =
    clearScreen()
    println(renderScreen(game, status))

end RenderTUI
