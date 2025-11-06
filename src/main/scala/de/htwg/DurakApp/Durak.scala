package de.htwg.DurakApp

import scala.io.StdIn.readLine
import scala.util.{Random, Try}

trait ConsoleIO {
  def readLine(): String
  def println(s: String): Unit
}

object DefaultConsoleIO extends ConsoleIO {
  override def readLine(): String = scala.io.StdIn.readLine()
  override def println(s: String): Unit = Predef.println(s)
}

object DurakApp:

  def safeToInt(s: String): Option[Int] =
    Try(s.trim.toInt).toOption

  def cardShortString(card: Card): String =
    s"${card.rank.toString} ${card.suit.toString}${if card.isTrump then " (T)" else ""}"

  def main(args: Array[String]): Unit =
    given ConsoleIO = DefaultConsoleIO
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

  def findNextDefender(game: GameState, attackerIndex: Int): Int =
    val n = game.playerList.length
    @annotation.tailrec
    def loop(offset: Int): Int = 
      val idx = (attackerIndex + 1 + offset) % n
      if idx != attackerIndex && !game.playerList(idx).isDone then idx
      else loop(offset + 1)
    loop(0)

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



  def initPlayerList(deck: List[Card], defaultHandSize: Int = 6)(using io: ConsoleIO): (List[Player], List[Card]) =
    RenderTUI.clearAndRender(GameState(Nil, deck, Suit.Clubs), "Please answer how many players and names")
    io.println("How many players?")
    val numPlayers = safeToInt(io.readLine()).getOrElse(2).max(2)

    val possibleHandSize = if (numPlayers > 0) deck.length / numPlayers else 0
    val actualHandSize =
      if (deck.length >= numPlayers * defaultHandSize) defaultHandSize
      else possibleHandSize.max(1)

    if (actualHandSize != defaultHandSize) {
      RenderTUI.clearAndRender(
        GameState(Nil, deck, Suit.Clubs),
        s"Deck small -> using hand size $actualHandSize (deck ${deck.length} / players $numPlayers)"
      )
    }

    val (players, remaining) =
      (1 to numPlayers).foldLeft((List.empty[Player], deck)) { case ((acc, curDeck), i) =>
        io.println(s"Enter name of player $i: ")
        val name = io.readLine().trim match
          case "" => s"Player$i"
          case n  => n
        val (hand, newDeck) = curDeck.splitAt(actualHandSize)
        (acc :+ Player(name, hand), newDeck)
      }
    (players, remaining)

  def updateFinishedPlayers(game: GameState)(using io: ConsoleIO): GameState =
    val updated = game.playerList.map { p =>
      if p.hand.isEmpty && !p.isDone then
        io.println(s"${p.name} hat keine Karten mehr und ist fertig!")
        p.copy(isDone = true)
      else p
    }
    game.copy(playerList = updated)

  def selectFirstAttacker(game: GameState, dealerIndex: Int): Int =
    val players = game.playerList
    val trumpsByPlayer: List[(Int, Int)] = players.zipWithIndex.map { case (p, idx) =>
      val trumpRanks = p.hand.filter(_.isTrump).map(_.rank.value)
      val minRank = if trumpRanks.nonEmpty then trumpRanks.min else Int.MaxValue
      (idx, minRank)
    }

    val playersWithTrumps = trumpsByPlayer.filter(_._2 != Int.MaxValue)
    if playersWithTrumps.isEmpty then (dealerIndex + 1) % players.length
    else
      val bestRank = playersWithTrumps.map(_._2).min
      val candidates = playersWithTrumps.filter(_._2 == bestRank).map(_._1)
      candidates.min

  def init()(using io: ConsoleIO): GameState =
    RenderTUI.clearAndRender(GameState(Nil, Nil, Suit.Clubs), "Start options")
    io.println("Anzahl Karten im Deck [36]: ")
    val deckSizeInput = io.readLine().trim
    val deckSize = safeToInt(deckSizeInput).getOrElse(36)

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

  def handleEnd(game: GameState)(using io: ConsoleIO): Unit =
    val loserOpt = game.playerList.find(p => !p.isDone && p.hand.nonEmpty)
    loserOpt match
      case Some(p) => RenderTUI.clearAndRender(game, s"${p.name} ist der Durak!")
      case None    => RenderTUI.clearAndRender(game, "Alle fertig — Unentschieden!")

  def findNextActive(game: GameState, startIndex: Int): Int =
    val n = game.playerList.length
    var idx = (startIndex + 1) % n
    while game.playerList(idx).isDone do
      idx = (idx + 1) % n
    idx


  @annotation.tailrec
  def gameLoop(gameState: GameState, attackerIndex: Int)(using io: ConsoleIO): Unit =
    val gameWithDone = updateFinishedPlayers(gameState)
    if checkLooser(gameWithDone) then
      handleEnd(gameWithDone)
    else
      val nextActiveAttacker =
        if gameWithDone.playerList(attackerIndex).isDone then
          findNextActive(gameWithDone, attackerIndex)
        else attackerIndex

      val attacker = gameWithDone.playerList(nextActiveAttacker)
      val defenderIndex = findNextDefender(gameWithDone, nextActiveAttacker)
      val defender = gameWithDone.playerList(defenderIndex)

      RenderTUI.clearAndRender(
        gameWithDone,
        s"Neue Runde — Angreifer: ${attacker.name}, Verteidiger: ${defender.name}"
      )

      val afterAttack = attack(gameWithDone, nextActiveAttacker)
      val (afterDefense, defenderTook) = defend(afterAttack, defenderIndex)
      val afterDraw = draw(afterDefense, nextActiveAttacker)
      val updatedGame = updateFinishedPlayers(afterDraw)

      val nextAttacker = nextAttackerIndex(updatedGame, nextActiveAttacker, defenderIndex, defenderTook)
      gameLoop(updatedGame, nextAttacker)

  def nextAttackerIndex(game: GameState, currentAttacker: Int, defenderIndex: Int, defenderTook: Boolean): Int = 
    val activePlayers = game.playerList.filterNot(_.isDone)
    if (activePlayers.length == 2) {
      // 1vs1
      findNextActive(game, currentAttacker)
    } else {
      // more then 2 players
      if (defenderTook) {
        findNextActive(game, defenderIndex)
      } else {
        findNextActive(game, currentAttacker)
      }
    }

  def canBeat(attackCard: Card, defendCard: Card, trump: Suit): Boolean =
    if attackCard.suit == defendCard.suit then
      defendCard.rank.value > attackCard.rank.value
    else defendCard.isTrump && !attackCard.isTrump

  def tableCardsContainRank(gameState: GameState, searchCard: Card): Boolean =
    val all = gameState.attackingCards ++ gameState.defendingCards
    all.exists(_.rank == searchCard.rank)

  def attack(gameState: GameState, attackerIndex: Int)(using io: ConsoleIO): GameState = {
    @annotation.tailrec
    def attackLoop(game: GameState, status: String): GameState = {
      RenderTUI.clearAndRender(game, status)
      val attacker = game.playerList(attackerIndex)
      io.println(s"${attacker.name}, choose card index to attack or 'pass':")
      val input = io.readLine().trim

      input match {
        case "pass" =>
          if (game.attackingCards.isEmpty) {
            attackLoop(game, "You can't pass before playing at least one card.")
          } else {
            game
          }

        case s =>
          safeToInt(s) match {
            case None => attackLoop(game, "Invalid input. Use a number or 'pass'.")
            case Some(idx) =>
              if (idx < 0 || idx >= attacker.hand.length) {
                attackLoop(game, "Index out of range.")
              } else if (game.attackingCards.length >= 6) {
                attackLoop(game, "Maximum 6 attack cards reached.")
              } else {
                val candidate = attacker.hand(idx)
                val allowed = if (game.attackingCards.isEmpty) true else tableCardsContainRank(game, candidate)
                if (!allowed) {
                  attackLoop(game, "You can only play cards whose rank is already on the table.")
                } else {
                  val (newHand, newAttacking) = moveCard(attacker.hand, game.attackingCards, idx)
                  val updatedPlayer = attacker.copy(hand = newHand)
                  val updatedPlayers = game.playerList.updated(attackerIndex, updatedPlayer)
                  val newGame = game.copy(playerList = updatedPlayers, attackingCards = newAttacking)
                  attackLoop(newGame, s"${attacker.name} played ${cardShortString(candidate)}")
                }
              }
          }
      }
    }

    val finalState = attackLoop(gameState, s"${gameState.playerList(attackerIndex).name} is attacking.")
    RenderTUI.clearAndRender(finalState, "Attack phase finished.")
    finalState
  }

  def moveCard(from: List[Card], to: List[Card], index: Int): (List[Card], List[Card]) =
    if index < 0 || index >= from.length then (from, to)
    else
      val c = from(index)
      val newFrom = from.patch(index, Nil, 1)
      val newTo = to :+ c
      (newFrom, newTo)

  def defend(gameState: GameState, defenderIndex: Int)(using io: ConsoleIO): (GameState, Boolean) =
    if (gameState.attackingCards.isEmpty) (gameState, false)
    else
      @annotation.tailrec
      def defendLoop(game: GameState, defender: Player, attackCardIndex: Int): (GameState, Boolean) =
        if (attackCardIndex >= game.attackingCards.length) then
          val cleared = game.attackingCards ++ game.defendingCards
          val updatedDiscard = game.discardPile ++ cleared
          val finalGame = game.copy(attackingCards = Nil, defendingCards = Nil, discardPile = updatedDiscard)
          (finalGame, false)
        else
          val attackCard = game.attackingCards(attackCardIndex)
          io.println(s"${defender.name}, defend against ${cardShortString(attackCard)} or 'take':")
          val input = io.readLine().trim

          if (input == "take") then
            val newHand = defender.hand ++ game.attackingCards ++ game.defendingCards
            val updatedDefender = defender.copy(hand = newHand)
            val updatedPlayers = game.playerList.updated(defenderIndex, updatedDefender)
            val newGame = game.copy(playerList = updatedPlayers, attackingCards = Nil, defendingCards = Nil)
            (newGame, true)
          else
            safeToInt(input) match
              case Some(idx) if idx >= 0 && idx < defender.hand.length =>
                val defendCard = defender.hand(idx)
                if (canBeat(attackCard, defendCard, game.trump)) then
                  val (newHand, newDefending) = moveCard(defender.hand, game.defendingCards, idx)
                  val updatedDefender = defender.copy(hand = newHand)
                  val updatedPlayers = game.playerList.updated(defenderIndex, updatedDefender)
                  val newGame = game.copy(playerList = updatedPlayers, defendingCards = newDefending)
                  defendLoop(newGame, updatedDefender, attackCardIndex + 1)
                else
                  io.println("This card doesn't beat the attack. Try again.")
                  defendLoop(game, defender, attackCardIndex)
              case _ =>
                io.println("Invalid input. Try again.")
                defendLoop(game, defender, attackCardIndex)
      defendLoop(gameState, gameState.playerList(defenderIndex), 0)


  def draw(gameState: GameState, attackerIndex: Int)(using io: ConsoleIO): GameState = {
    val n = gameState.playerList.length
    val drawOrder = (0 until n).map(i => (attackerIndex + i) % n).toList

    val (finalPlayers, finalDeck) = drawOrder.foldLeft((gameState.playerList, gameState.deck)) {
      case ((currentPlayers, currentDeck), playerIdx) =>
        val player = currentPlayers(playerIdx)
        if (player.hand.size < 6 && currentDeck.nonEmpty) {
          val needed = 6 - player.hand.size
          val (drawn, rest) = currentDeck.splitAt(needed)
          val updatedPlayer = player.copy(hand = player.hand ++ drawn)
          val newPlayers = currentPlayers.updated(playerIdx, updatedPlayer)
          val status = s"${newPlayers(playerIdx).name} drew ${drawn.length} card(s)"
          RenderTUI.clearAndRender(gameState.copy(playerList = newPlayers, deck = rest), status)
          (newPlayers, rest)
        } else {
          (currentPlayers, currentDeck)
        }
    }

    val newGame = gameState.copy(playerList = finalPlayers, deck = finalDeck)
    RenderTUI.clearAndRender(newGame, "Draw phase finished.")
    newGame
  }

end DurakApp
