package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.testutil.TestHelper
import de.htwg.DurakApp.model.{Card, Player, Suit, Rank}

class GameEventSpec extends AnyWordSpec with Matchers {
  "GameEvent" should {
    "have InvalidMove event" in {
      val event: GameEvent = GameEvent.InvalidMove
      event shouldBe a[GameEvent]
    }

    "have NotYourTurn event" in {
      val event: GameEvent = GameEvent.NotYourTurn
      event shouldBe a[GameEvent]
    }

    "have Attack event with card" in {
      val card = TestHelper.Card(Suit.Hearts, Rank.Ace)
      val event: GameEvent = GameEvent.Attack(card)
      event shouldBe a[GameEvent]
    }

    "have Defend event with card" in {
      val card = TestHelper.Card(Suit.Spades, Rank.King)
      val event: GameEvent = GameEvent.Defend(card)
      event shouldBe a[GameEvent]
    }

    "have Pass event" in {
      val event: GameEvent = GameEvent.Pass
      event shouldBe a[GameEvent]
    }

    "have Take event" in {
      val event: GameEvent = GameEvent.Take
      event shouldBe a[GameEvent]
    }

    "have Draw event" in {
      val event: GameEvent = GameEvent.Draw
      event shouldBe a[GameEvent]
    }

    "have RoundEnd event with cleared boolean" in {
      val event1: GameEvent = GameEvent.RoundEnd(cleared = true)
      val event2: GameEvent = GameEvent.RoundEnd(cleared = false)
      event1 shouldBe a[GameEvent]
      event2 shouldBe a[GameEvent]
    }

    "have GameOver event with winner and optional loser" in {
      val winner = TestHelper.Player("Winner")
      val loser = TestHelper.Player("Loser")
      val event1: GameEvent = GameEvent.GameOver(winner, Some(loser))
      val event2: GameEvent = GameEvent.GameOver(winner, None)
      event1 shouldBe a[GameEvent]
      event2 shouldBe a[GameEvent]
    }

    "have CannotUndo event" in {
      val event: GameEvent = GameEvent.CannotUndo
      event shouldBe a[GameEvent]
    }

    "have CannotRedo event" in {
      val event: GameEvent = GameEvent.CannotRedo
      event shouldBe a[GameEvent]
    }

    "have AskPlayerCount event" in {
      val event: GameEvent = GameEvent.AskPlayerCount
      event shouldBe a[GameEvent]
    }

    "have AskPlayerNames event" in {
      val event: GameEvent = GameEvent.AskPlayerNames
      event shouldBe a[GameEvent]
    }

    "have AskDeckSize event" in {
      val event: GameEvent = GameEvent.AskDeckSize
      event shouldBe a[GameEvent]
    }

    "have GameSetupComplete event" in {
      val event: GameEvent = GameEvent.GameSetupComplete
      event shouldBe a[GameEvent]
    }

    "have SetupError event" in {
      val event: GameEvent = GameEvent.SetupError
      event shouldBe a[GameEvent]
    }

    "have AskPlayAgain event" in {
      val event: GameEvent = GameEvent.AskPlayAgain
      event shouldBe a[GameEvent]
    }

    "have ExitApplication event" in {
      val event: GameEvent = GameEvent.ExitApplication
      event shouldBe a[GameEvent]
    }

    "have GameSaved event" in {
      val event: GameEvent = GameEvent.GameSaved
      event shouldBe a[GameEvent]
    }

    "have GameLoaded event" in {
      val event: GameEvent = GameEvent.GameLoaded
      event shouldBe a[GameEvent]
    }

    "have SaveError event" in {
      val event: GameEvent = GameEvent.SaveError
      event shouldBe a[GameEvent]
    }

    "have LoadError event" in {
      val event: GameEvent = GameEvent.LoadError
      event shouldBe a[GameEvent]
    }

    "distinguish between different singleton events" in {
      GameEvent.InvalidMove should not be GameEvent.NotYourTurn
      GameEvent.Pass should not be GameEvent.Take
      GameEvent.CannotUndo should not be GameEvent.CannotRedo
      GameEvent.GameSaved should not be GameEvent.GameLoaded
      GameEvent.SaveError should not be GameEvent.LoadError
    }

    "maintain consistency across FileIO events" in {
      GameEvent.GameSaved shouldBe a[GameEvent]
      GameEvent.GameLoaded shouldBe a[GameEvent]
      GameEvent.SaveError shouldBe a[GameEvent]
      GameEvent.LoadError shouldBe a[GameEvent]

      GameEvent.GameSaved should not be GameEvent.SaveError
      GameEvent.GameLoaded should not be GameEvent.LoadError
    }
  }
}
