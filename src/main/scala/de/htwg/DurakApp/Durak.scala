package de.htwg.DurakApp

import scala.io.StdIn.readLine
import scala.util.{Random, Try}

// ---------------------------
// Durak.scala - Main & Game Logic (TUI integriert)
// ---------------------------

object DurakApp:

  // --- Hilfsfunktionen ---
  private def safeToInt(s: String): Option[Int] =
    Try(s.trim.toInt).toOption

  private def cardShortString(card: Card): String =
    s"${card.rank.toString} ${card.suit.toString}${if card.isTrump then " (T)" else ""}"

  private def printIndexedHandInPrompt(hand: List[Card]): Unit =
    // Für Prompt-Zeilen verwenden wir die RenderTUI; diese Funktion ist nur Fallback
    println(RenderTUI.renderHandWithIndices(hand))

  // --- Entry point ---
  def main(args: Array[String]): Unit =
    val game = init()
    val firstPlayer = getPlayerWithLowestTrump(game.playerList)
    RenderTUI.clearAndRender(game, s"Game started. First attacker: ${firstPlayer.name}")
    gameLoop(game, game.playerList.indexOf(firstPlayer))

  // --- Deck / Init ---
  def moveTrump(list: List[Card]): List[Card] =
    list match
      case head :: tail => tail :+ head
      case Nil          => Nil

  /** returns (deckWithTrumpMarked, trumpSuit) */
  def initDeck(): (List[Card], Suit) =
    val deck = (for
      suit <- Suit.values
      rank <- Rank.values
    yield Card(suit, rank, isTrump = false)).toList

    val shuffled = Random.shuffle(deck)
    val trump = shuffled.head.suit
    val deckWithTrump = shuffled.map(c => c.copy(isTrump = c.suit == trump))
    (moveTrump(deckWithTrump), trump)

  /** creates a small deterministic deck for testing (useful for fast runs) */
  def createSmallDeck(): (List[Card], Suit) =
    val trump = Suit.Clubs
    val small = List(
      Card(Suit.Clubs, Rank.King, isTrump = false),
      Card(Suit.Spades, Rank.Seven, isTrump = false),
      Card(Suit.Spades, Rank.Ten, isTrump = false),
      Card(Suit.Diamonds, Rank.Seven, isTrump = false),
      Card(Suit.Diamonds, Rank.Ace, isTrump = false),
      Card(Suit.Hearts, Rank.Jack, isTrump = false),
      Card(Suit.Clubs, Rank.Nine, isTrump = false),
      Card(Suit.Spades, Rank.Queen, isTrump = false),
      Card(Suit.Hearts, Rank.Nine, isTrump = false),
      Card(Suit.Diamonds, Rank.Nine, isTrump = false),
      Card(Suit.Clubs, Rank.Ten, isTrump = false),
      Card(Suit.Hearts, Rank.Eight, isTrump = false)
    )
    val marked = small.map(c => c.copy(isTrump = c.suit == trump))
    (moveTrump(marked), trump)

  def dealCardsToHand(player: Player, deck: List[Card], n: Int): (Player, List[Card]) =
    val (dealt, rest) = deck.splitAt(n)
    (player.copy(hand = player.hand ++ dealt), rest)

  def getPlayerWithLowestTrump(playerList: List[Player]): Player =
    val withTrump = playerList.map(p => (p, p.hand.filter(_.isTrump))).filter(_._2.nonEmpty)
    if withTrump.isEmpty then playerList.head
    else withTrump.minBy { case (_, tcs) => tcs.minBy(_.rank.value).rank.value }._1

  def initPlayerList(deck: List[Card], handSize: Int = 6): (List[Player], List[Card]) =
    // für stabilen Test: wir fragen weiterhin nach Spielern
    RenderTUI.clearAndRender(GameState(Nil, deck, Suit.Clubs), "Please answer how many players and names")
    println("How many players?")
    val numPlayers = safeToInt(readLine()).getOrElse(2).max(2)
    val (players, remaining) = (1 to numPlayers).foldLeft((List.empty[Player], deck)) {
      case ((acc, curDeck), i) =>
        println(s"Enter name of player $i: ")
        val name = readLine().trim match
          case "" => s"Player$i"
          case n  => n
        val (hand, newDeck) = curDeck.splitAt(handSize)
        (acc :+ Player(name, hand), newDeck)
    }
    (players, remaining)

  def init(): GameState =
    // start UI prompt to choose small deck
    RenderTUI.clearAndRender(GameState(Nil, Nil, Suit.Clubs), "Start options")
    println("Use small test deck? (y/n) [n]: ")
    val smallChoice = readLine().trim.toLowerCase
    val (deckWithTrump, trump) =
      if smallChoice == "y" || smallChoice == "yes" then
        (createSmallDeck())
      else initDeck()

    val handSize = 6
    val (playerlist, remainingDeck) = initPlayerList(deckWithTrump, handSize)
    GameState(
      playerList = playerlist,
      deck = remainingDeck,
      trump = trump,
      attackingCards = Nil,
      defendingCards = Nil,
      discardPile = Nil
    )

  // --- End / Check ---
  def checkLooser(gameState: GameState): Boolean =
    gameState.playerList.count(_.hand.nonEmpty) <= 1

  def handleEnd(game: GameState): Unit =
    val loser = game.playerList.find(_.hand.nonEmpty)
    loser match
      case Some(p) => RenderTUI.clearAndRender(game, s"${p.name} ist der Durak!"); System.exit(0)
      case None    => RenderTUI.clearAndRender(game, "Unentschieden"); System.exit(0)

  // --- Core Loop ---
  @annotation.tailrec
  def gameLoop(gameState: GameState, attackerIndex: Int): Unit =
    if checkLooser(gameState) then handleEnd(gameState)

    val attacker = gameState.playerList(attackerIndex)
    val defenderIndex = (attackerIndex + 1) % gameState.playerList.length
    val defender = gameState.playerList(defenderIndex)

    var status = s"Neue Runde — Angreifer: ${attacker.name}, Verteidiger: ${defender.name}"
    RenderTUI.clearAndRender(gameState, status)

    val afterAttack = attack(gameState, attackerIndex)
    val (afterDefense, defenderTook) = defend(afterAttack, defenderIndex)
    val afterDraw = draw(afterDefense, attackerIndex)

    val nextAttackerIndex =
      if defenderTook then (attackerIndex + 1) % afterDraw.playerList.length
      else defenderIndex % afterDraw.playerList.length

    gameLoop(afterDraw, nextAttackerIndex)

  // --- Rules: can beat? ---
  def canBeat(attackCard: Card, defendCard: Card, trump: Suit): Boolean =
    if attackCard.suit == defendCard.suit then
      defendCard.rank.value > attackCard.rank.value
    else
      defendCard.isTrump && !attackCard.isTrump

  def tableCardsContainRank(gameState: GameState, searchCard: Card): Boolean =
    val all = gameState.attackingCards ++ gameState.defendingCards
    all.exists(_.rank == searchCard.rank)

  // --- Attack: attacker may play multiple cards until 'pass' or limit reached ---
  def attack(gameState: GameState, attackerIndex: Int): GameState =
    var game = gameState
    var continue = true
    var status = s"${game.playerList(attackerIndex).name} is attacking."

    while continue do
      RenderTUI.clearAndRender(game, status)
      val attacker = game.playerList(attackerIndex)
      // Prompt
      println(s"${attacker.name}, choose card index to attack or 'pass':")
      val input = readLine().trim

      input match
        case "pass" =>
          if game.attackingCards.isEmpty then
            status = "You can't pass before playing at least one card."
          else
            continue = false

        case s =>
          safeToInt(s) match
            case None =>
              status = "Invalid input. Use a number or 'pass'."
            case Some(idx) =>
              if idx < 0 || idx >= attacker.hand.length then
                status = "Index out of range."
              else if game.attackingCards.length >= 6 then
                status = "Maximum 6 attack cards reached."
                continue = false
              else
                val candidate = attacker.hand(idx)
                val allowed =
                  if game.attackingCards.isEmpty then true
                  else tableCardsContainRank(game, candidate)

                if !allowed then
                  status = "You can only play cards whose rank is already on the table."
                else
                  val (newHand, newAttacking) = moveCard(attacker.hand, game.attackingCards, idx)
                  val updatedPlayer = attacker.copy(hand = newHand)
                  val updatedPlayers = game.playerList.updated(attackerIndex, updatedPlayer)
                  game = game.copy(playerList = updatedPlayers, attackingCards = newAttacking)
                  status = s"${attacker.name} played ${cardShortString(candidate)}"

    // final render after attack
    RenderTUI.clearAndRender(game, "Attack phase finished.")
    game

  // --- moveCard helper: append to 'to' for intuitive order on table ---
  def moveCard(from: List[Card], to: List[Card], index: Int): (List[Card], List[Card]) =
    if index < 0 || index >= from.length then (from, to)
    else
      val c = from(index)
      val newFrom = from.patch(index, Nil, 1)
      val newTo = to :+ c
      (newFrom, newTo)

  // --- Defend: returns (newGameState, defenderTookFlag) ---
  def defend(gameState: GameState, defenderIndex: Int): (GameState, Boolean) =
    var game = gameState
    var defender = game.playerList(defenderIndex)
    var status = s"${defender.name} must defend."

    if game.attackingCards.isEmpty then
      RenderTUI.clearAndRender(game, "No attacks to defend.")
      return (game, false)

    var i = 0
    while i < game.attackingCards.length do
      status = s"${defender.name} defending card ${i + 1} of ${game.attackingCards.length}"
      RenderTUI.clearAndRender(game, status)
      val attackCard = game.attackingCards(i)
      println(s"${defender.name}, defend against ${cardShortString(attackCard)}")
      println("Enter index of card to defend with, or 'take' to pick up all cards:")
      val input = readLine().trim

      if input == "take" then
        val newHand = defender.hand ++ game.attackingCards ++ game.defendingCards
        val updatedDefender = defender.copy(hand = newHand)
        val updatedPlayers = game.playerList.updated(defenderIndex, updatedDefender)
        val clearedGame = game.copy(playerList = updatedPlayers, attackingCards = Nil, defendingCards = Nil)
        RenderTUI.clearAndRender(clearedGame, s"${defender.name} took the table.")
        return (clearedGame, true)
      else
        safeToInt(input) match
          case None =>
            status = "Invalid input. Try again."
          case Some(idx) =>
            if idx < 0 || idx >= defender.hand.length then
              status = "Index out of range."
            else
              val defendCard = defender.hand(idx)
              if canBeat(attackCard, defendCard, game.trump) then
                val (newHand, newDefending) = moveCard(defender.hand, game.defendingCards, idx)
                defender = defender.copy(hand = newHand)
                val updatedPlayers = game.playerList.updated(defenderIndex, defender)
                game = game.copy(playerList = updatedPlayers, defendingCards = newDefending)
                status = s"${defender.name} defended ${cardShortString(attackCard)} with ${cardShortString(defendCard)}"
                i += 1
              else
                status = "That card doesn't beat the attacking card. Try another."

    // defender succeeded -> move table to discard
    val cleared = game.attackingCards ++ game.defendingCards
    val updatedDiscard = game.discardPile ++ cleared
    val resultGame = game.copy(attackingCards = Nil, defendingCards = Nil, discardPile = updatedDiscard)
    RenderTUI.clearAndRender(resultGame, s"${defender.name} successfully defended.")
    (resultGame, false)

  // --- Draw: replenish hands to 6 starting with attacker, clockwise ---
  def draw(gameState: GameState, attackerIndex: Int): GameState =
    var status = "Drawing cards..."
    var game = gameState
    val n = game.playerList.length
    val drawOrder = (0 until n).map(i => (attackerIndex + i) % n).toList
    var deckAcc = game.deck
    var playersAcc = game.playerList

    for playerIdx <- drawOrder do
      val player = playersAcc(playerIdx)
      if player.hand.size < 6 && deckAcc.nonEmpty then
        val needed = 6 - player.hand.size
        val (drawn, rest) = deckAcc.splitAt(needed)
        val updatedPlayer = player.copy(hand = player.hand ++ drawn)
        playersAcc = playersAcc.updated(playerIdx, updatedPlayer)
        deckAcc = rest
        status = s"${playersAcc(playerIdx).name} drew ${drawn.length} card(s)"
        RenderTUI.clearAndRender(game.copy(playerList = playersAcc, deck = deckAcc), status)

    val newGame = game.copy(playerList = playersAcc, deck = deckAcc)
    RenderTUI.clearAndRender(newGame, "Draw phase finished.")
    newGame

end DurakApp
