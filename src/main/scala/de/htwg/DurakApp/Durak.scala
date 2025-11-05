package de.htwg.DurakApp
import de.htwg.DurakApp.RenderTUI
import de.htwg.DurakApp
import scala.io.StdIn.readLine
import de.htwg.DurakApp.RenderTUI.renderCard
import de.htwg.DurakApp.RenderTUI.renderHand
import scala.compiletime.ops.boolean

def main(args: Array[String]): Unit =
  val game = init()
  val firstPlayer = getPlayerWithLowestTrump(game.playerList)
  println(RenderTUI.renderGame(game))
  gameLoop(game, firstPlayer)

def moveTrump(list: List[Card]): List[Card] =
  list match
    case head :: tail => tail :+ head
    case Nil          => Nil

def initDeck(): List[Card] =
  val deck = (for {
    suit <- Suit.values
    rank <- Rank.values
    isTrump = false
  } yield Card(suit, rank, isTrump)).toList

  val shuffledDeck = scala.util.Random.shuffle(deck)
  val trump = shuffledDeck.head.suit

  val trumpShuffledDeck =
    shuffledDeck.map(card => card.copy(isTrump = card.suit == trump))
  moveTrump(trumpShuffledDeck)

def dealCardsToHand(
    player: Player,
    deck: List[Card],
    n: Int
): (Player, List[Card]) = {
  val (dealtCards, remainingDeck) = deck.splitAt(n)
  val newHand = player.hand ::: dealtCards
  val updatedPlayer = player.copy(hand = newHand)
  (updatedPlayer, remainingDeck)
}

def getPlayerWithLowestTrump(playerList: List[Player]): Player = {
  val playersWithTrumpCards = playerList
    .map { player =>
      val trumpCardsInHand = player.hand.filter(card => card.isTrump)
      (player, trumpCardsInHand)
    }
    .filter(_._2.nonEmpty) // Keep only players who have trump cards

  if (playersWithTrumpCards.isEmpty) {
    playerList.head
  } else {
    playersWithTrumpCards.minBy { case (player, trumpCards) =>
      trumpCards.minBy(_.rank.value).rank.value
    }._1
  }
}

def initPlayerList(deck: List[Card]): (List[Player], List[Card]) =
  println("How many players?")
  val numPlayers = readLine().toInt

  val (players, remainingDeck) =
    (1 to numPlayers).foldLeft((List.empty[Player], deck)) {
      case ((playerList, currentDeck), i) =>
        println(s"Enter name of player $i: ")
        val name = readLine()

        val (playerCards, newDeck) = currentDeck.splitAt(6)
        val player = Player(name, playerCards)
        (playerList :+ player, newDeck)
    }

  (players, remainingDeck)

def init(): GameState =
  val deck = initDeck()
  val trump = deck.last.suit
  val (playerlist, remainingDeck) = initPlayerList(deck)

  GameState(playerlist, remainingDeck, trump)

def checkLooser(gameState: GameState): Boolean =
  gameState.playerList.count(p => p.hand.nonEmpty) <= 1

def gameLoop(gameState: GameState, attacker: Player): GameState = {
  if (checkLooser(gameState)) {
    // handleEnd()
  }

  val defender = gameState.playerList(
    (gameState.playerList.indexOf(attacker) + 1) % gameState.playerList.length
  )
  println(s"Attacker: ${attacker.name}")
  println(s"Defender: ${defender.name}")

  // attack()

  // defend()

  // draw()

  gameState
}

def canBeat(attackCard: Card, defendCard: Card, trump: Suit): Boolean = {
  if (attackCard.suit == defendCard.suit) {
    defendCard.rank.value > attackCard.rank.value
  } else {
    defendCard.isTrump
  }
}

def isPlayable(defender: Card, attacker: Card) = {
  attacker.suit == defender.suit || defender.isTrump
}

def tableCardsContainRank(
    gameState: GameState,
    searchCard: Card
): Boolean = {
  val allCards = gameState.attackingCards ++ gameState.defendingCards
  allCards.exists(_.rank == searchCard.rank)
}

def attack(gameState: GameState, attackerIndex: Int): GameState = {
  print(RenderTUI.renderHand(gameState.playerList(attackerIndex).hand))
  print("Choose Card to attack or pass with 'pass':")
  val chosenCard = readLine

  if (chosenCard == "pass") {
    if (gameState.attackingCards.isEmpty) {
      print("You can't pass you scala-coward")
      attack(gameState, attackerIndex)
    } else {
      gameState
    }
  } else if (
    (gameState.attackingCards.isEmpty || tableCardsContainRank(
      gameState,
      gameState.playerList(attackerIndex).hand(chosenCard.toInt)
    ) && gameState.attackingCards.length < 6)
  ) {
    val (newPlayerHand, newAttacking) =
      moveCard(
        gameState.playerList(attackerIndex).hand,
        gameState.attackingCards,
        chosenCard.toInt
      )

    val updatedPlayer =
      gameState.playerList(attackerIndex).copy(hand = newPlayerHand)

    val updatedPlayerList =
      gameState.playerList.updated(attackerIndex, updatedPlayer)

    gameState.copy(
      playerList = updatedPlayerList,
      attackingCards = newAttacking
    )
  } else {
    gameState
  }
}

def moveCard(
    from: List[Card],
    to: List[Card],
    index: Int
): (List[Card], List[Card]) = {
  if (index < 0 || index >= from.length) {
    (from, to)
  } else {
    val cardToMove = from(index)
    val newFrom = from.patch(index, Nil, 1)
    val newTo =
      cardToMove :: to
    (newFrom, newTo)
  }
}
