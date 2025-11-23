package de.htwg.DurakApp
package controller

import model.Suit
import model.Rank
import model.Player
import model.Card
import model.GameStatus
import model.GameState

import scala.util.{Random, Try}
import de.htwg.DurakApp.util.Observable

final class Controller(var game: GameState) extends Observable:

  private def setGameAndNotify(gs: GameState): GameState =
    game = gs
    notifyObservers
    game

  def setupGameAndStart(
      deckSize: Int,
      playerNames: List[String],
      random: Random,
      inputmethod: PlayerInput
  ): Unit = {
    given Random = random
    val initialGame = initGame(deckSize, playerNames)
    val numPlayers = initialGame.playerList.length
    val dealerIndex = random.nextInt(numPlayers)
    val firstAttackerIndex = selectFirstAttacker(initialGame, dealerIndex)
    val game = initialGame.copy(activePlayerId = firstAttackerIndex)
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
      discardPile = Nil,
      status = GameStatus.PLAYER_SETUP
    )
  }

  def putFirstCardAtEnd(list: List[Card]): List[Card] =
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
    (putFirstCardAtEnd(marked), trump)

  def updateFinishedPlayers(game: GameState): GameState =
    val updated = game.playerList.map { p =>
      if p.hand.isEmpty && !p.isDone then p.copy(isDone = true)
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

  def handleEnd(game: GameState): GameState =
    game.copy(status = GameStatus.GAME_OVER)

  def findNextActive(game: GameState, startIndex: Int): Int =
    val n = game.playerList.length
    @annotation.tailrec
    def findNext(currentIndex: Int): Int = {
      val nextIndex = (currentIndex + 1) % n
      if (!game.playerList(nextIndex).isDone) {
        nextIndex
      } else {
        findNext(nextIndex)
      }
    }
    findNext(startIndex)

  private def handleRoundStartAndEnd(initialState: GameState, currentAttacker: Int)(using random: Random): (GameState, Option[Int]) =
    val gameWithDonePlayers = updateFinishedPlayers(initialState)
    if checkLooser(gameWithDonePlayers) then
      val finalGameState = handleEnd(gameWithDonePlayers) // Now handleEnd is pure
      (finalGameState, None) // Game over, no next attacker
    else
      // Determine the actual attacker for this round, skipping done players
      val actualAttackerForRound =
        if gameWithDonePlayers.playerList(currentAttacker).isDone then
          findNextActive(gameWithDonePlayers, currentAttacker)
        else currentAttacker
      (gameWithDonePlayers, Some(actualAttackerForRound))

  private def executeTurnPhases(
      initialGameState: GameState,
      actualAttacker: Int,
      defenderIndex: Int,
      inputmethod: PlayerInput
  )(using random: Random): (GameState, Boolean) =
    val gameReadyToAttack = initialGameState.copy(
      status = GameStatus.ATTACK,
      activePlayerId = actualAttacker
    )
    setGameAndNotify(gameReadyToAttack) // This notification is part of the turn phase display

    val afterAttack =
      attack(gameReadyToAttack, actualAttacker, inputmethod)
    val (afterDefense, defenderTook) =
      defend(afterAttack, defenderIndex, inputmethod)
    val afterDraw = draw(afterDefense, actualAttacker)

    (afterDraw, defenderTook)


  @annotation.tailrec
  final def gameLoop(
      gameState: GameState,
      attackerIndex: Int,
      inputmethod: PlayerInput
  )(using
      random: Random
  ): GameState =
    val (gameAfterRoundStart, nextAttackerOpt) = handleRoundStartAndEnd(gameState, attackerIndex)

    nextAttackerOpt match
      case None =>
        // Game is over, set the final state and return
        setGameAndNotify(gameAfterRoundStart)
        gameAfterRoundStart
      case Some(actualAttacker) =>
        val defenderIndex = findNextDefender(gameAfterRoundStart, actualAttacker)

        val (gameAfterPhases, defenderTook) = executeTurnPhases(
          gameAfterRoundStart,
          actualAttacker,
          defenderIndex,
          inputmethod
        )
        val updatedGame = updateFinishedPlayers(gameAfterPhases)

        val nextAttacker = nextAttackerIndex(
          updatedGame,
          actualAttacker, // Use actualAttacker for current attacker in nextAttackerIndex
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

  private def isValidAttackMove(
      game: GameState,
      attacker: Player,
      cardIndex: Int
  ): Boolean =
    // Check 1: Valid index
    if (cardIndex < 0 || cardIndex >= attacker.hand.length) then false
    // Check 2: Max attacking cards
    else if (game.attackingCards.length >= 6) then false
    else
      val candidate = attacker.hand(cardIndex)
      // Check 3: Card rank on table, or it's the first attack card
      if (game.attackingCards.isEmpty) then true
      else tableCardsContainRank(game, candidate)

  def attack(
      gameState: GameState,
      attackerIndex: Int,
      input: PlayerInput
  ): GameState = {
    @annotation.tailrec
    def attackLoop(game: GameState): GameState = {
      setGameAndNotify(game)
      val attacker = game.playerList(attackerIndex)
      input.chooseAttackCard(attacker, game) match {
        case -1 => // pass
          if (game.attackingCards.isEmpty) {
            attackLoop(game.copy(status = GameStatus.INVALID_MOVE))
          } else {
            game.copy(status = GameStatus.PASS)
          }
        case -2 => // invalid integer input
          attackLoop(game.copy(status = GameStatus.INVALID_MOVE))
        case idx =>
          if (!isValidAttackMove(game, attacker, idx)) {
            attackLoop(game.copy(status = GameStatus.INVALID_MOVE))
          } else {
            val candidate = attacker.hand(idx) // This is safe now due to isValidAttackMove
            val (newHand, newAttacking) =
              moveCard(attacker.hand, game.attackingCards, idx)
            val updatedPlayer = attacker.copy(hand = newHand)
            val updatedPlayers =
              game.playerList.updated(attackerIndex, updatedPlayer)
            val newGame = game.copy(
              playerList = updatedPlayers,
              attackingCards = newAttacking,
              status = GameStatus.ATTACK
            )
            attackLoop(newGame)
          }
      }
    }
    attackLoop(gameState)
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

  private def isValidDefenseMove(
      game: GameState,
      defender: Player,
      attackCard: Card,
      chosenDefendCardIndex: Int
  ): Boolean =
    if (chosenDefendCardIndex < 0 || chosenDefendCardIndex >= defender.hand.length) then false
    else
      val defendCard = defender.hand(chosenDefendCardIndex)
      canBeat(attackCard, defendCard, game.trump)

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
          val cleared = game.attackingCards ++ game.defendingCards
          val updatedDiscard = game.discardPile ++ cleared
          val finalGame = game.copy(
            attackingCards = Nil,
            defendingCards = Nil,
            discardPile = updatedDiscard,
            status = GameStatus.DEFEND
          )
          setGameAndNotify(finalGame)
          (finalGame, false)
        else
          val attackCard = game.attackingCards(attackCardIndex)
          setGameAndNotify(
            game.copy(
              status = GameStatus.DEFEND,
              activePlayerId = defenderIndex
            )
          )

          input.chooseDefenseCard(defender, attackCard, game) match {
            case -1 => // take
              val newHand =
                defender.hand ++ game.attackingCards ++ game.defendingCards
              val updatedDefender = defender.copy(hand = newHand)
              val updatedPlayers =
                game.playerList.updated(defenderIndex, updatedDefender)
              val newGame = game.copy(
                playerList = updatedPlayers,
                attackingCards = Nil,
                defendingCards = Nil,
                status = GameStatus.TAKE
              )
              setGameAndNotify(newGame)
              (newGame, true)
            case -2 => // invalid integer input
              setGameAndNotify(game.copy(status = GameStatus.INVALID_MOVE))
              defendLoop(game, defender, attackCardIndex)
            case idx =>
              if (isValidDefenseMove(game, defender, attackCard, idx)) then
                val defendCard = defender.hand(idx) // Safe due to isValidDefenseMove
                val (newHand, newDefending) =
                  moveCard(defender.hand, game.defendingCards, idx)
                val updatedDefender = defender.copy(hand = newHand)
                val updatedPlayers =
                  game.playerList.updated(defenderIndex, updatedDefender)
                val newGame = game.copy(
                  playerList = updatedPlayers,
                  defendingCards = newDefending,
                  status = GameStatus.DEFEND
                )
                setGameAndNotify(newGame)
                defendLoop(newGame, updatedDefender, attackCardIndex + 1)
              else
                setGameAndNotify(game.copy(status = GameStatus.INVALID_MOVE))
                defendLoop(game, defender, attackCardIndex)
          }

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
            (newPlayers, rest)
          } else {
            (currentPlayers, currentDeck)
          }
      }

    val newGame = gameState.copy(playerList = finalPlayers, deck = finalDeck)
    setGameAndNotify(newGame)
    newGame
  }
