package de.htwg.DurakApp.controller.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.controller.{InvalidAction, PassAction, PlayCardAction, TakeCardsAction}
import de.htwg.DurakApp.model.state.GameEvent
import de.htwg.DurakApp.model.{Card, GameState, Player, Rank, Suit}
import de.htwg.DurakApp.model.state._

class CommandFactorySpec extends AnyWordSpec with Matchers {

  val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
  val game = GameState(
    players = List(player1),
    deck = List.empty,
    table = Map.empty,
    discardPile = List.empty,
    trumpCard = Card(Suit.Diamonds, Rank.Ace),
    attackerIndex = 0,
    defenderIndex = 1,
    gamePhase = AttackPhase
  )

  "A CommandFactory" should {
    "create a PlayCardCommand for PlayCardAction" in {
      val action = PlayCardAction(player1.hand.head)
      val command = CommandFactory.createCommand(action, game)
      command shouldBe Right(PlayCardCommand(player1.hand.head))
    }

    "create a PassCommand for PassAction" in {
      val action = PassAction
      val command = CommandFactory.createCommand(action, game)
      command shouldBe Right(PassCommand())
    }

    "create a TakeCardsCommand for TakeCardsAction" in {
      val action = TakeCardsAction
      val command = CommandFactory.createCommand(action, game)
      command shouldBe Right(TakeCardsCommand())
    }

    "create an InvalidMove GameEvent for InvalidAction" in {
      val action = InvalidAction
      val event = CommandFactory.createCommand(action, game)
      event shouldBe Left(GameEvent.InvalidMove)
    }
  }
}
