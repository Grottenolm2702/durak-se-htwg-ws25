package de.htwg.DurakApp
import de.htwg.DurakApp.RenderTUI
import de.htwg.DurakApp

def main(args: Array[String]): Unit =
    val deck = (for {
    suit <- Suit.values
    rank <- Rank.values
    isTrump = false
    } yield Card(suit, rank, isTrump)).toList

    val shuffledDeck = scala.util.Random.shuffle(deck)
    val trump = shuffledDeck.head.suit

    val deckWithTrump = shuffledDeck.map(card => card.copy(isTrump = card.suit == trump))

    val players = List(
    Player("Alice", deckWithTrump.take(5)),
    Player("Bob", deckWithTrump.slice(5, 10))
    )

    val game = GameState(players, deckWithTrump.drop(10), deckWithTrump.slice(10, 12), trump)
    println(RenderTUI.renderGame(game))
