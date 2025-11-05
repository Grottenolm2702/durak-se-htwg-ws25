package de.htwg.DurakApp
import de.htwg.DurakApp.RenderTUI
import de.htwg.DurakApp
import scala.io.StdIn.readLine
def main(args: Array[String]): Unit =
    //val game = GameState(playerList, deckWithTrump.drop(10), deckWithTrump.slice(10, 12), trump)
    val game = init()
    println(RenderTUI.renderGame(game))

def moveTrump(list : List[Card]) : List[Card] = 
    list match
        case head :: tail => tail :+ head
        case Nil => Nil
    
def initDeck() : List[Card] =


    val deck = (for {
    suit <- Suit.values
    rank <- Rank.values
    isTrump = false
    } yield Card(suit, rank, isTrump)).toList

    val shuffledDeck = scala.util.Random.shuffle(deck)
    val trump = shuffledDeck.head.suit

    shuffledDeck.map(card => card.copy(isTrump = card.suit == trump))
    moveTrump(shuffledDeck)

def initPlayerList(deck : List[Card]): List[Player] = 
    println("How many players?")
    val numPlayers = readLine().toInt

    val players = for (i <- 1 to numPlayers) yield {
        println(s"Enter name of player $i: ")
        val name = readLine()
        Player(name, deck.take(6))
    }
    players.toList

def init(): GameState =

    val deck = initDeck()
    val trump = deck.last.suit
    // val playerList = List(
    // Player("Alice", deck.take(5)),
    // Player("Bob", deck.slice(5, 10))
    // )

    val playerlist = initPlayerList(deck)

    GameState(playerlist, deck, deck, trump)

