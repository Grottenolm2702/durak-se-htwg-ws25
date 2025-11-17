package de.htwg.DurakApp
package controller

import model.Suit
import model.Rank
import model.Player
import model.Card
import model.GameState

import scala.util.{Random, Try}
import de.htwg.DurakApp.util.Observable

final case class Controller() extends Observable:
  private var _status = ""
  private var _game = GameState(Nil, Nil, Suit.Clubs)

  def game: GameState = _game
  def status: String = _status

  private def setGameAndNotify(gs: GameState, st: String): Unit =
    _game = gs
    _status = st
    notifyObservers
  def safeToInt(s: String): Option[Int] =
    Try(s.trim.toInt).toOption

  def cardShortString(card: Card): String =
    s"${card.rank.toString} ${card.suit.toString}${
        if card.isTrump then " (T)" else ""
      }"

  def setupGameAndStart(
      deckSize: Int,
      playerNames: List[String],
      random: Random,
      inputmethod: PlayerInput
  ): Unit = {
    given Random = random
    val game = initGame(deckSize, playerNames)
    val numPlayers = game.playerList.length
    val dealerIndex = random.nextInt(numPlayers)
    val firstAttackerIndex = selectFirstAttacker(game, dealerIndex)
    this._game = game
    this._status =
      s"Dealer: ${game.playerList(dealerIndex).name} — First attacker: ${game.playerList(firstAttackerIndex).name}"
    notifyObservers
    gameLoop(game, firstAttackerIndex, inputmethod)
  }

  def initGame(
      deckSize: Int,
      playerNames: List[String],
      defaultHandSize: Int = 6
  )(using random: Random): GameState = {
    val (deckWithTrump, trump) = createDeck(deckSize)

    val numPlayers = playerNames.length
    val possibleHandSize = deckWithTrump.length / numPlayers
    val actualHandSize =
      if (deckWithTrump.length >= numPlayers * defaultHandSize) defaultHandSize
      else possibleHandSize.max(1)

    if (actualHandSize != defaultHandSize) {
      val noticeState = GameState(Nil, deckWithTrump, trump)
      setGameAndNotify(
        noticeState,
        s"Deck small -> using hand size $actualHandSize (deck ${deckWithTrump.length} / players $numPlayers)"
      )
    }

    val (players, remainingDeck) =
      playerNames.foldLeft((List.empty[Player], deckWithTrump)) {
        case ((acc, curDeck), name) =>
          val (hand, newDeck) = curDeck.splitAt(actualHandSize)
          (acc :+ Player(name, hand), newDeck)
      }

    GameState(
      playerList = players,
      deck = remainingDeck,
      trump = trump,
      attackingCards = Nil,
      defendingCards = Nil,
      discardPile = Nil
    )
  }

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

  def createDeck(deckSizeRequested: Int)(using
      random: Random
  ): (List[Card], Suit) =
    val baseDeck = (for
      suit <- Suit.values
      rank <- Rank.values
    yield Card(suit, rank, isTrump = false)).toList
    val standardDeckSize = baseDeck.size
    val numDecksNeeded =
      Math.ceil(deckSizeRequested.toDouble / standardDeckSize).toInt
    val combinedDeck = List.fill(numDecksNeeded)(baseDeck).flatten
    val shuffled = random.shuffle(combinedDeck)
    val subset = shuffled.take(deckSizeRequested)
    val trump = subset.head.suit
    val marked = subset.map(c => c.copy(isTrump = c.suit == trump))
    (moveTrump(marked), trump)

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
        val minRank =
          if trumpRanks.nonEmpty then trumpRanks.min else Int.MaxValue
        (idx, minRank)
    }

    val playersWithTrumps = trumpsByPlayer.filter(_._2 != Int.MaxValue)
    if playersWithTrumps.isEmpty then (dealerIndex + 1) % players.length
    else
      val bestRank = playersWithTrumps.map(_._2).min
      val candidates = playersWithTrumps.filter(_._2 == bestRank).map(_._1)
      candidates.min

  def checkLooser(gameState: GameState): Boolean =
    val activePlayers = gameState.playerList.filterNot(_.isDone)
    activePlayers.length <= 1

  def handleEnd(game: GameState): Unit =
    val loserOpt = game.playerList.find(p => !p.isDone && p.hand.nonEmpty)
    loserOpt match
      case Some(p) =>
        setGameAndNotify(game, s"${p.name} ist der Durak!")
      case None =>
        setGameAndNotify(game, "Alle fertig — Unentschieden!")

  def findNextActive(game: GameState, startIndex: Int): Int =
    val n = game.playerList.length
    var idx = (startIndex + 1) % n
    while game.playerList(idx).isDone do idx = (idx + 1) % n
    idx

  @annotation.tailrec
  final def gameLoop(
      gameState: GameState,
      attackerIndex: Int,
      inputmethod: PlayerInput
  )(using
      random: Random
  ): GameState =
    val gameWithDone = updateFinishedPlayers(gameState)
    if checkLooser(gameWithDone) then
      handleEnd(gameWithDone)
      gameWithDone // Return the final state when game ends
    else
      val nextActiveAttacker =
        if gameWithDone.playerList(attackerIndex).isDone then
          findNextActive(gameWithDone, attackerIndex)
        else attackerIndex

      val attacker = gameWithDone.playerList(nextActiveAttacker)
      val defenderIndex = findNextDefender(gameWithDone, nextActiveAttacker)
      val defender = gameWithDone.playerList(defenderIndex)

      setGameAndNotify(
        gameWithDone,
        s"Neue Runde — Angreifer: ${attacker.name}, Verteidiger: ${defender.name}"
      )

      val afterAttack = attack(gameWithDone, nextActiveAttacker, inputmethod)
      val (afterDefense, defenderTook) =
        defend(afterAttack, defenderIndex, inputmethod)
      val afterDraw = draw(afterDefense, nextActiveAttacker)
      val updatedGame = updateFinishedPlayers(afterDraw)

      val nextAttacker = nextAttackerIndex(
        updatedGame,
        nextActiveAttacker,
        defenderIndex,
        defenderTook
      )
      gameLoop(updatedGame, nextAttacker, inputmethod)

  def nextAttackerIndex(
      game: GameState,
      currentAttacker: Int,
      defenderIndex: Int,
      defenderTook: Boolean
  ): Int =
    val activePlayers = game.playerList.filterNot(_.isDone)
    if (defenderTook) {
      findNextActive(game, defenderIndex)
    } else {
      findNextActive(game, currentAttacker)
    }

  def canBeat(attackCard: Card, defendCard: Card, trump: Suit): Boolean =
    if attackCard.suit == defendCard.suit then
      defendCard.rank.value > attackCard.rank.value
    else defendCard.isTrump && !attackCard.isTrump

  def tableCardsContainRank(gameState: GameState, searchCard: Card): Boolean =
    val all = gameState.attackingCards ++ gameState.defendingCards
    all.exists(_.rank == searchCard.rank)

  def attack(
      gameState: GameState,
      attackerIndex: Int,
      input: PlayerInput
  ): GameState = {
    @annotation.tailrec
    def attackLoop(game: GameState, status: String): GameState = {
      setGameAndNotify(game, status)

      val attacker = game.playerList(attackerIndex)
      val raw = input.chooseAttackCard(attacker, game).trim

      raw match {
        case "pass" =>
          if (game.attackingCards.isEmpty) {
            attackLoop(
              game,
              "You can't pass before playing at least one card."
            )
          } else {
            game
          }

        case s =>
          safeToInt(s) match {
            case None =>
              attackLoop(game, "Invalid input. Use a number or 'pass'.")
            case Some(idx) =>
              if (idx < 0 || idx >= attacker.hand.length) {
                attackLoop(game, "Index out of range.")
              } else if (game.attackingCards.length >= 6) {
                attackLoop(game, "Maximum 6 attack cards reached.")
              } else {
                val candidate = attacker.hand(idx)
                val allowed =
                  if (game.attackingCards.isEmpty) true
                  else tableCardsContainRank(game, candidate)

                if (!allowed) {
                  attackLoop(
                    game,
                    "You can only play cards whose rank is already on the table."
                  )
                } else {
                  val (newHand, newAttacking) =
                    moveCard(attacker.hand, game.attackingCards, idx)
                  val updatedPlayer = attacker.copy(hand = newHand)
                  val updatedPlayers =
                    game.playerList.updated(attackerIndex, updatedPlayer)
                  val newGame = game.copy(
                    playerList = updatedPlayers,
                    attackingCards = newAttacking
                  )
                  attackLoop(
                    newGame,
                    s"${attacker.name} played ${cardShortString(candidate)}"
                  )
                }
              }
          }
      }
    }

    val finalState = attackLoop(
      gameState,
      s"${gameState.playerList(attackerIndex).name} is attacking."
    )
    setGameAndNotify(finalState, "Attack phase finished.")
    finalState
  }

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

  def defend(
      gameState: GameState,
      defenderIndex: Int,
      input: PlayerInput
  ): (GameState, Boolean) =
    if (gameState.attackingCards.isEmpty) then (gameState, false)
    else
      @annotation.tailrec
      def defendLoop(
          game: GameState,
          defender: Player,
          attackCardIndex: Int
      ): (GameState, Boolean) =
        if attackCardIndex >= game.attackingCards.length then
          // Alle Angriffe erfolgreich abgewehrt -> Karten kommen auf den Ablagestapel
          val cleared = game.attackingCards ++ game.defendingCards
          val updatedDiscard = game.discardPile ++ cleared
          val finalGame = game.copy(
            attackingCards = Nil,
            defendingCards = Nil,
            discardPile = updatedDiscard
          )
          setGameAndNotify(
            finalGame,
            s"${defender.name} hat alle Karten abgewehrt."
          )
          (finalGame, false)
        else
          val attackCard = game.attackingCards(attackCardIndex)
          val raw = input.chooseDefenseCard(defender, attackCard, game).trim

          if raw == "take" then
            // Verteidiger nimmt alle Karten
            val newHand =
              defender.hand ++ game.attackingCards ++ game.defendingCards
            val updatedDefender = defender.copy(hand = newHand)
            val updatedPlayers =
              game.playerList.updated(defenderIndex, updatedDefender)
            val newGame = game.copy(
              playerList = updatedPlayers,
              attackingCards = Nil,
              defendingCards = Nil
            )
            setGameAndNotify(newGame, s"${defender.name} nimmt die Karten.")
            (newGame, true)
          else
            safeToInt(raw) match
              case Some(idx) if idx >= 0 && idx < defender.hand.length =>
                val defendCard = defender.hand(idx)
                if canBeat(attackCard, defendCard, game.trump) then
                  val (newHand, newDefending) =
                    moveCard(defender.hand, game.defendingCards, idx)
                  val updatedDefender = defender.copy(hand = newHand)
                  val updatedPlayers =
                    game.playerList.updated(defenderIndex, updatedDefender)
                  val newGame = game.copy(
                    playerList = updatedPlayers,
                    defendingCards = newDefending
                  )
                  setGameAndNotify(
                    newGame,
                    s"${defender.name} schlägt ${cardShortString(attackCard)} mit ${cardShortString(defendCard)}"
                  )
                  // nächster Angriff
                  defendLoop(newGame, updatedDefender, attackCardIndex + 1)
                else
                  setGameAndNotify(
                    game,
                    "Diese Karte schlägt den Angriff nicht. Versuche eine andere Karte oder 'take'."
                  )
                  defendLoop(game, defender, attackCardIndex)
              case _ =>
                setGameAndNotify(
                  game,
                  "Ungültige Eingabe. Gib einen Index oder 'take' ein."
                )
                defendLoop(game, defender, attackCardIndex)

      defendLoop(gameState, gameState.playerList(defenderIndex), 0)

  def draw(gameState: GameState, attackerIndex: Int): GameState = {
    val n = gameState.playerList.length
    val drawOrder = (0 until n).map(i => (attackerIndex + i) % n).toList

    val (finalPlayers, finalDeck) =
      drawOrder.foldLeft((gameState.playerList, gameState.deck)) {
        case ((currentPlayers, currentDeck), playerIdx) =>
          val player = currentPlayers(playerIdx)
          if (player.hand.size < 6 && currentDeck.nonEmpty) {
            val needed = 6 - player.hand.size
            val (drawn, rest) = currentDeck.splitAt(needed)
            val updatedPlayer = player.copy(hand = player.hand ++ drawn)
            val newPlayers = currentPlayers.updated(playerIdx, updatedPlayer)
            val status =
              s"${newPlayers(playerIdx).name} drew ${drawn.length} card(s)"
            val interimGame =
              gameState.copy(playerList = newPlayers, deck = rest)
            setGameAndNotify(interimGame, status)
            (newPlayers, rest)
          } else {
            (currentPlayers, currentDeck)
          }
      }

    val newGame = gameState.copy(playerList = finalPlayers, deck = finalDeck)
    setGameAndNotify(newGame, "Draw phase finished.")
    newGame
  }
