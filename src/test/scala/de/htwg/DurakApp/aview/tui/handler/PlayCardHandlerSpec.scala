package de.htwg.DurakApp.aview.tui.handler

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.model.state._
import de.htwg.DurakApp.controller._

class PlayCardHandlerSpec extends AnyWordSpec with Matchers {

  val heartAce = Card(Suit.Hearts, Rank.Ace)
  val spadeSix = Card(Suit.Spades, Rank.Six)
  val attacker = Player("Alice", List(spadeSix))
  val defender = Player("Bob", List(heartAce))
  val gameState = GameState(
    players = List(attacker, defender),
    deck = List.empty,
    table = Map.empty,
    discardPile = List.empty,
    trumpCard = Card(Suit.Clubs, Rank.Seven, isTrump = true),
    attackerIndex = 0,
    defenderIndex = 1,
    gamePhase = AttackPhase,
    lastEvent = None,
    passedPlayers = Set.empty,
    roundWinner = None
  )

  "A PlayCardHandler" should {
    val passHandler = new PassHandler()
    val playHandler = new PlayCardHandler(Some(passHandler))

    "handle 'play 0' command" in {
      playHandler.handleRequest("play 0", gameState) should be(
        PlayCardAction(spadeSix)
      )
    }

    "handle 'play 0' command during defense phase" in {
      val defenseState = gameState.copy(gamePhase = DefensePhase)
      playHandler.handleRequest("play 0", defenseState) should be(
        PlayCardAction(heartAce)
      )
    }

    "return InvalidAction for 'play' with out of bounds index" in {
      playHandler.handleRequest("play 1", gameState) should be(InvalidAction)
    }

    "return InvalidAction for 'play' with non-integer index" in {
      playHandler.handleRequest("play a", gameState) should be(InvalidAction)
    }

    "return InvalidAction for 'play' during defense phase with out of bounds index" in {
      val defenseState = gameState.copy(gamePhase = DefensePhase)
      playHandler.handleRequest("play 1", defenseState) should be(InvalidAction)
    }

    "pass to next handler for non-play command" in {
      playHandler.handleRequest("pass", gameState) should be(PassAction)
    }

    "return InvalidAction for unhandled command" in {
      val playHandlerOnly = new PlayCardHandler()
      playHandlerOnly.handleRequest("foo", gameState) should be(InvalidAction)
    }
  }
}
