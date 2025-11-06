package de.htwg.DurakApp

import scala.io.StdIn.readLine
import scala.util.{Random, Try}

object DurakApp:

  private def safeToInt(s: String): Option[Int] =
    Try(s.trim.toInt).toOption

  private def cardShortString(card: Card): String =
    s"${card.rank.toString} ${card.suit.toString}${
        if card.isTrump then " (T)" else ""
      }"

  def main(args: Array[String]): Unit =
    val game = init()
    val numPlayers = game.playerList.length
    val dealerIndex = Random.nextInt(numPlayers)
    val firstAttackerIndex = selectFirstAttacker(game, dealerIndex)
    RenderTUI.clearAndRender(
      game,
      s"Dealer: ${game.playerList(dealerIndex).name} — First attacker: ${game.playerList(firstAttackerIndex).name}"
    )
    gameLoop(game, firstAttackerIndex)

  def moveTrump(list: List[Card]): List[Card] =
    list match
      case head :: tail => tail :+ head
      case Nil          => Nil

  def createDeck(deckSizeRequested: Int): (List[Card], Suit) =
    val baseDeck = (for
      suit <- Suit.values
      rank <- Rank.values
    yield Card(suit, rank, isTrump = false)).toList
    val standardDeckSize = baseDeck.size
    val numDecksNeeded =
      Math.ceil(deckSizeRequested.toDouble / standardDeckSize).toInt
    val combinedDeck = List.fill(numDecksNeeded)(baseDeck).flatten
    val shuffled = Random.shuffle(combinedDeck)
    val subset = shuffled.take(deckSizeRequested)
    val trump = subset.head.suit
    val marked = subset.map(c => c.copy(isTrump = c.suit == trump))
    (moveTrump(marked), trump)

  def dealCardsToHand(
      player: Player,
      deck: List[Card],
      n: Int
  ): (Player, List[Card]) =
    val (dealt, rest) = deck.splitAt(n)
    (player.copy(hand = player.hand ++ dealt), rest)

  def getPlayerWithLowestTrump(playerList: List[Player]): Player =
    val withTrump =
      playerList.map(p => (p, p.hand.filter(_.isTrump))).filter(_._2.nonEmpty)
    if withTrump.isEmpty then playerList.head
    else
      withTrump.minBy { case (_, tcs) => tcs.minBy(_.rank.value).rank.value }._1

  def initPlayerList(
      deck: List[Card],
      defaultHandSize: Int = 6
  ): (List[Player], List[Card]) =
    RenderTUI.clearAndRender(
      GameState(Nil, deck, Suit.Clubs),
      "Please answer how many players and names"
    )
    println("How many players?")
    val numPlayers = safeToInt(readLine()).getOrElse(2).max(2)

    val actualHandSize =
      val possible = if numPlayers > 0 then deck.length / numPlayers else 0
      val chosen =
        if deck.length >= numPlayers * defaultHandSize then defaultHandSize
        else possible.max(1)
      if chosen != defaultHandSize then
        RenderTUI.clearAndRender(
          GameState(Nil, deck, Suit.Clubs),
          s"Deck small -> using hand size $chosen (deck ${deck.length} / players $numPlayers)"
        )
      chosen

    val (players, remaining) =
      (1 to numPlayers).foldLeft((List.empty[Player], deck)) {
        case ((acc, curDeck), i) =>
          println(s"Enter name of player $i: ")
          val name = readLine().trim match
            case "" => s"Player$i"
            case n  => n
          val (hand, newDeck) = curDeck.splitAt(actualHandSize)
          (acc :+ Player(name, hand), newDeck)
      }
    (players, remaining)

  def updateFinishedPlayers(game: GameState): GameState =
    val updated = game.playerList.map { p =>
      if p.hand.isEmpty && !p.isDone then
        println(s"${p.name} hat keine Karten mehr und ist fertig!")
        p.copy(isDone = true)
      else p
    }
    game.copy(playerList = updated)

  def selectFirstAttacker(game: GameState, dealerIndex: Int): Int =
    val players = game.playerList
    val trumpsByPlayer: List[(Int, Int)] = players.zipWithIndex.map {
      case (p, idx) =>
        val trumpRanks = p.hand.filter(_.isTrump).map(_.rank.value)
        val maxRank = if trumpRanks.nonEmpty then trumpRanks.min else -1
        (idx, maxRank)
    }

    val playersWithTrumps = trumpsByPlayer.filter(_._2 >= 0)
    if playersWithTrumps.isEmpty then (dealerIndex + 1) % players.length
    else
      val bestRank = playersWithTrumps.map(_._2).min
      val candidates = playersWithTrumps.filter(_._2 == bestRank).map(_._1)
      candidates.min

  def init(): GameState =
    RenderTUI.clearAndRender(GameState(Nil, Nil, Suit.Clubs), "Start options")
    println("Anzahl Karten im Deck (min. 12) [36]: ")
    val deckSizeInput = readLine().trim
    val deckSize =
      safeToInt(deckSizeInput) match
        case Some(n) => n.max(12)
        case None    => 36

    val (deckWithTrump, trump) = createDeck(deckSize)

    val (playerlist, remainingDeck) = initPlayerList(deckWithTrump, 6)
    GameState(
      playerList = playerlist,
      deck = remainingDeck,
      trump = trump,
      attackingCards = Nil,
      defendingCards = Nil,
      discardPile = Nil
    )

  def checkLooser(gameState: GameState): Boolean =
    val activePlayers = gameState.playerList.filterNot(_.isDone)
    activePlayers.length <= 1

  def handleEnd(game: GameState): Unit =
    val loserOpt = game.playerList.find(p => !p.isDone && p.hand.nonEmpty)
    loserOpt match
      case Some(p) =>
        RenderTUI.clearAndRender(game, s"${p.name} ist der Durak!")
      case None =>
        RenderTUI.clearAndRender(game, "Alle fertig — Unentschieden!")
    System.exit(0)

  def findNextActive(game: GameState, startIndex: Int): Int =
    val n = game.playerList.length
    var idx = startIndex
    var found = false
    while
      idx = (idx + 1) % n
      found = !game.playerList(idx).isDone
      !found
    do ()
    idx

  @annotation.tailrec
  def gameLoop(gameState: GameState, attackerIndex: Int): Unit =
    val gameWithDone = updateFinishedPlayers(gameState)
    if checkLooser(gameWithDone) then handleEnd(gameWithDone)

    val nextActiveAttacker = findNextActive(gameWithDone, attackerIndex)
    val attacker = gameWithDone.playerList(nextActiveAttacker)
    val defenderIndex = findNextActive(
      gameWithDone,
      (nextActiveAttacker + 1) % gameWithDone.playerList.length
    )
    val defender = gameWithDone.playerList(defenderIndex)

    var status =
      s"Neue Runde — Angreifer: ${attacker.name}, Verteidiger: ${defender.name}"
    RenderTUI.clearAndRender(gameWithDone, status)

    val afterAttack = attack(gameWithDone, nextActiveAttacker)
    val (afterDefense, defenderTook) = defend(afterAttack, defenderIndex)
    val afterDraw = draw(afterDefense, nextActiveAttacker)
    val updatedGame = updateFinishedPlayers(afterDraw)

    val nextAttackerIndex =
      if defenderTook then
        findNextActive(
          updatedGame,
          defenderIndex
        )
      else findNextActive(updatedGame, defenderIndex)

    gameLoop(updatedGame, nextAttackerIndex)

  def canBeat(attackCard: Card, defendCard: Card, trump: Suit): Boolean =
    if attackCard.suit == defendCard.suit then
      defendCard.rank.value > attackCard.rank.value
    else defendCard.isTrump && !attackCard.isTrump

  def tableCardsContainRank(gameState: GameState, searchCard: Card): Boolean =
    val all = gameState.attackingCards ++ gameState.defendingCards
    all.exists(_.rank == searchCard.rank)

  def attack(gameState: GameState, attackerIndex: Int): GameState =
    var game = gameState
    var continue = true
    var status = s"${game.playerList(attackerIndex).name} is attacking."

    while continue do
      RenderTUI.clearAndRender(game, status)
      val attacker = game.playerList(attackerIndex)
      println(s"${attacker.name}, choose card index to attack or 'pass':")
      val input = readLine().trim

      input match
        case "pass" =>
          if game.attackingCards.isEmpty then
            status = "You can't pass before playing at least one card."
          else continue = false

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
                  status =
                    "You can only play cards whose rank is already on the table."
                else
                  val (newHand, newAttacking) =
                    moveCard(attacker.hand, game.attackingCards, idx)
                  val updatedPlayer = attacker.copy(hand = newHand)
                  val updatedPlayers =
                    game.playerList.updated(attackerIndex, updatedPlayer)
                  game = game.copy(
                    playerList = updatedPlayers,
                    attackingCards = newAttacking
                  )
                  status =
                    s"${attacker.name} played ${cardShortString(candidate)}"

    RenderTUI.clearAndRender(game, "Attack phase finished.")
    game

  def moveCard(
      from: List[Card],
      to: List[Card],
      index: Int
  ): (List[Card], List[Card]) =
    if index < 0 || index >= from.length then (from, to)
    else
      val c = from(index)
      val newFrom = from.patch(index, Nil, 1)
      val newTo = to :+ c
      (newFrom, newTo)

  def defend(gameState: GameState, defenderIndex: Int): (GameState, Boolean) =
    var game = gameState
    var defender = game.playerList(defenderIndex)
    var status = s"${defender.name} must defend."

    if game.attackingCards.isEmpty then
      RenderTUI.clearAndRender(game, "No attacks to defend.")
      return (game, false)

    var i = 0
    while i < game.attackingCards.length do
      status =
        s"${defender.name} defending card ${i + 1} of ${game.attackingCards.length}"
      RenderTUI.clearAndRender(game, status)
      val attackCard = game.attackingCards(i)
      println(
        s"${defender.name}, defend against ${cardShortString(attackCard)}"
      )
      println(
        "Enter index of card to defend with, or 'take' to pick up all cards:"
      )
      val input = readLine().trim

      if input == "take" then
        val newHand =
          defender.hand ++ game.attackingCards ++ game.defendingCards
        val updatedDefender = defender.copy(hand = newHand)
        val updatedPlayers =
          game.playerList.updated(defenderIndex, updatedDefender)
        val clearedGame = game.copy(
          playerList = updatedPlayers,
          attackingCards = Nil,
          defendingCards = Nil
        )
        RenderTUI.clearAndRender(
          clearedGame,
          s"${defender.name} took the table."
        )
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
                val (newHand, newDefending) =
                  moveCard(defender.hand, game.defendingCards, idx)
                defender = defender.copy(hand = newHand)
                val updatedPlayers =
                  game.playerList.updated(defenderIndex, defender)
                game = game.copy(
                  playerList = updatedPlayers,
                  defendingCards = newDefending
                )
                status =
                  s"${defender.name} defended ${cardShortString(attackCard)} with ${cardShortString(defendCard)}"
                i += 1
              else
                status =
                  "That card doesn't beat the attacking card. Try another."

    val cleared = game.attackingCards ++ game.defendingCards
    val updatedDiscard = game.discardPile ++ cleared
    val resultGame = game.copy(
      attackingCards = Nil,
      defendingCards = Nil,
      discardPile = updatedDiscard
    )
    RenderTUI.clearAndRender(
      resultGame,
      s"${defender.name} successfully defended."
    )
    (resultGame, false)

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
        RenderTUI.clearAndRender(
          game.copy(playerList = playersAcc, deck = deckAcc),
          status
        )

    val newGame = game.copy(playerList = playersAcc, deck = deckAcc)
    RenderTUI.clearAndRender(newGame, "Draw phase finished.")
    newGame

end DurakApp
