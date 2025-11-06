package de.htwg.DurakApp

import scala.io.StdIn.readLine
import scala.util.{Random, Try}

// ---------------------------
// Durak.scala - Main & Game Logic (mit kleinem Test-Deck)
// ---------------------------

object DurakApp:

  // --- Hilfsfunktionen ---
  private def safeToInt(s: String): Option[Int] =
    Try(s.trim.toInt).toOption

  private def cardShortString(card: Card): String =
    s"${card.rank.toString} ${card.suit.toString}${if card.isTrump then " (T)" else ""}"

  private def printIndexedHand(hand: List[Card]): Unit =
    if hand.isEmpty then println("Empty hand")
    else
      println("Index : Card")
      hand.zipWithIndex.foreach { case (c, i) =>
        println(f"$i%2d : ${cardShortString(c)}")
      }
      println() // whitespace
      // show graphical view as well
      println(RenderTUI.renderHand(hand))

  // --- Entry point ---
  def main(args: Array[String]): Unit =
    val game = init()
    val firstPlayer = getPlayerWithLowestTrump(game.playerList)
    println(RenderTUI.renderGame(game))
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
    // Beispiel-Deck mit 12 Karten; Trumpf ist Clubs
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

  /** initPlayerList: legt Spielernamen an und verteilt initial `handSize` Karten */
  def initPlayerList(deck: List[Card], handSize: Int = 6): (List[Player], List[Card]) =
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
    println("Use small test deck? (y/n) [n]: ")
    val smallChoice = readLine().trim.toLowerCase
    val (deckWithTrump, trump) =
      if smallChoice == "y" || smallChoice == "yes" then
        println("Using SMALL deterministic deck for testing. (2 players, quick game)")
        createSmallDeck()
      else initDeck()

    // Hand size: wenn kleines Deck gewählt -> 6 (bei 12 Kartendeck und 2 Spieler werden 6 Karten verteilt)
    // Du kannst per HandSize anpassen, hier belassen wir default 6.
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
      case Some(p) => println(s"${p.name} ist der Durak! 🎉 (oder eher: 😅)")
      case None    => println("Alle sind leer - Unentschieden.")
    System.exit(0)

  // --- Core Loop ---
  @annotation.tailrec
  def gameLoop(gameState: GameState, attackerIndex: Int): Unit =
    if checkLooser(gameState) then handleEnd(gameState)

    val attacker = gameState.playerList(attackerIndex)
    val defenderIndex = (attackerIndex + 1) % gameState.playerList.length
    val defender = gameState.playerList(defenderIndex)

    println("\n--- New Round ---")
    println(s"Trump suit: ${gameState.trump}")
    println(s"Attacker: ${attacker.name}")
    println(s"Defender: ${defender.name}")
    println(RenderTUI.renderGame(gameState))

    val afterAttack = attack(gameState, attackerIndex)
    // defend returns (newGameState, defenderTook: Boolean)
    val (afterDefense, defenderTook) = defend(afterAttack, defenderIndex)

    // draw replenishes starting with attacker (Durak rules: attacker draws first)
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

    while continue do
      val attacker = game.playerList(attackerIndex)
      println(s"\n${attacker.name}, it's your turn to attack.")
      printIndexedHand(attacker.hand)
      println(s"Cards on table: ${game.attackingCards.length} attack / ${game.defendingCards.length} defend")
      print("Choose card index to attack or 'pass': ")
      val input = readLine().trim

      input match
        case "pass" =>
          if game.attackingCards.isEmpty then
            println("You can't pass before playing at least one card.")
          else
            continue = false

        case s =>
          safeToInt(s) match
            case None => println("Invalid input. Use a number or 'pass'.")
            case Some(idx) =>
              if idx < 0 || idx >= attacker.hand.length then
                println("Index out of range.")
              else if game.attackingCards.length >= 6 then
                println("Maximum 6 attack cards reached.")
                continue = false
              else
                val candidate = attacker.hand(idx)
                val allowed =
                  if game.attackingCards.isEmpty then true
                  else tableCardsContainRank(game, candidate)

                if !allowed then
                  println("You can only play cards whose rank is already on the table.")
                else
                  val (newHand, newAttacking) = moveCard(attacker.hand, game.attackingCards, idx)
                  val updatedPlayer = attacker.copy(hand = newHand)
                  val updatedPlayers = game.playerList.updated(attackerIndex, updatedPlayer)
                  game = game.copy(playerList = updatedPlayers, attackingCards = newAttacking)

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

    if game.attackingCards.isEmpty then return (game, false)

    // defend sequentially: defender must respond to each attacking card
    var i = 0
    while i < game.attackingCards.length do
      val attackCard = game.attackingCards(i)
      println(s"\n${defender.name}, defend against: ${cardShortString(attackCard)}")
      printIndexedHand(defender.hand)
      println("Type 'take' to pick up all cards on table, or enter index of card to defend with.")
      val input = readLine().trim

      if input == "take" then
        val newHand = defender.hand ++ game.attackingCards ++ game.defendingCards
        val updatedDefender = defender.copy(hand = newHand)
        val updatedPlayers = game.playerList.updated(defenderIndex, updatedDefender)
        val clearedGame = game.copy(playerList = updatedPlayers, attackingCards = Nil, defendingCards = Nil)
        return (clearedGame, true)
      else
        safeToInt(input) match
          case None =>
            println("Invalid input. Try again.")
            // do not increment i -> repeat defending same attack
          case Some(idx) =>
            if idx < 0 || idx >= defender.hand.length then
              println("Index out of range.")
            else
              val defendCard = defender.hand(idx)
              if canBeat(attackCard, defendCard, game.trump) then
                val (newHand, newDefending) = moveCard(defender.hand, game.defendingCards, idx)
                defender = defender.copy(hand = newHand)
                val updatedPlayers = game.playerList.updated(defenderIndex, defender)
                game = game.copy(playerList = updatedPlayers, defendingCards = newDefending)
                i += 1 // move to next attacking card
              else
                println("That card doesn't beat the attacking card. Try another.")

    // If we reach here, defender successfully beat all attack cards -> move table to discard
    val cleared = game.attackingCards ++ game.defendingCards
    val updatedDiscard = game.discardPile ++ cleared
    val resultGame = game.copy(attackingCards = Nil, defendingCards = Nil, discardPile = updatedDiscard)
    (resultGame, false)

  // --- Draw: replenish hands to 6 starting with attacker, clockwise ---
  def draw(gameState: GameState, attackerIndex: Int): GameState =
    val n = gameState.playerList.length
    // order of drawing: attackerIndex, attackerIndex+1, ...
    val drawOrder = (0 until n).map(i => (attackerIndex + i) % n).toList
    val (newPlayers, remainingDeck) = drawOrder.foldLeft((gameState.playerList, gameState.deck)) {
      case ((playersAcc, deckAcc), playerIdx) =>
        val player = playersAcc(playerIdx)
        if player.hand.size >= 6 || deckAcc.isEmpty then
          (playersAcc, deckAcc)
        else
          val needed = 6 - player.hand.size
          val (drawn, rest) = deckAcc.splitAt(needed)
          val updatedPlayer = player.copy(hand = player.hand ++ drawn)
          val updatedPlayers = playersAcc.updated(playerIdx, updatedPlayer)
          (updatedPlayers, rest)
    }
    gameState.copy(playerList = newPlayers, deck = remainingDeck)

end DurakApp
