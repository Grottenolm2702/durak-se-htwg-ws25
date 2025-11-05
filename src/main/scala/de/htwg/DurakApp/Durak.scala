package de.htwg.DurakApp
import de.htwg.DurakApp.RenderTUI
import de.htwg.DurakApp
import scala.io.StdIn.readLine
import de.htwg.DurakApp.RenderTUI.renderCard
import de.htwg.DurakApp.RenderTUI.renderHand
def main(args: Array[String]): Unit =
  // val game = GameState(playerList, deckWithTrump.drop(10), deckWithTrump.slice(10, 12), trump)
  val game = init()
  println(RenderTUI.renderGame(game))

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

def dealCardsToHand(player: Player, deck: List[Card], n: Int): (Player, List[Card]) = {
  val (dealtCards, remainingDeck) = deck.splitAt(n)
  val newHand = player.hand ::: dealtCards
  val updatedPlayer = player.copy(hand = newHand)
  (updatedPlayer, remainingDeck)
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

  GameState(playerlist, remainingDeck, List[Card](), trump)
