package de.htwg.DurakApp.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._

class DefensePhaseSpec extends AnyWordSpec with Matchers {
  "A DefensePhase" should {
    "handle the game state without changing it by default" in {
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Diamonds, Rank.Seven)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(Card(Suit.Spades, Rank.Ace) -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )
      val resultState = DefensePhase.handle(initialGameState)
      resultState.shouldBe(initialGameState)
    }

    "allow a defender to play a card to beat an attack" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val defenseCard = Card(Suit.Clubs, Rank.Seven)
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(defenseCard))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(attackCard -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val resultState = DefensePhase.playCard(defenseCard, 1, initialGameState)

      resultState.players(1).hand.shouldNot(contain(defenseCard))
      resultState.table.get(attackCard).flatten.should(contain(defenseCard))
      resultState.gamePhase.shouldBe(AttackPhase)
      resultState.lastEvent.get.should(be(a[GameEvent.Defend]))
    }

    "not allow a defender to play a non-beating card" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val defenseCard = Card(Suit.Clubs, Rank.Six)
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Eight)))
      val player2 = Player("P2", List(defenseCard))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(attackCard -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val resultState = DefensePhase.playCard(defenseCard, 1, initialGameState)
      resultState.shouldBe(
        initialGameState.copy(lastEvent = Some(GameEvent.InvalidMove))
      )
    }

    "allow a defender to take cards" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Seven)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(attackCard -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val resultState = DefensePhase.takeCards(1, initialGameState)
      resultState.players(1).hand.should(contain(attackCard))
      resultState.table.should(be(empty))
      resultState.gamePhase.shouldBe(DrawPhase)
      resultState.lastEvent.get.shouldBe(GameEvent.Take)
    }

    "not allow an attacker to take cards" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val player1 = Player("P1", List(Card(Suit.Clubs, Rank.Six)))
      val player2 = Player("P2", List(Card(Suit.Hearts, Rank.Seven)))
      val initialGameState = GameState(
        players = List(player1, player2),
        deck = List.empty,
        table = Map(attackCard -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val resultState = DefensePhase.takeCards(0, initialGameState)
      resultState.shouldBe(
        initialGameState.copy(lastEvent = Some(GameEvent.NotYourTurn))
      )
    }

    "not allow a non-defender to play a card (NotYourTurn)" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val defenseCard = Card(Suit.Clubs, Rank.Seven)
      val attacker = Player("A", List(attackCard))
      val nonDefender = Player("B", List(defenseCard))
      val initial = GameState(
        players = List(attacker, nonDefender),
        deck = List.empty,
        table = Map(attackCard -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val res = DefensePhase.playCard(defenseCard, 0, initial)
      res shouldBe initial.copy(lastEvent = Some(GameEvent.NotYourTurn))
    }

    "not allow playCard when defender does not have the card (InvalidMove)" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val defender = Player("D", List.empty)
      val attacker = Player("A", List(attackCard))
      val initial = GameState(
        players = List(attacker, defender),
        deck = List.empty,
        table = Map(attackCard -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val attemptedDefense = Card(Suit.Clubs, Rank.Seven)
      val res = DefensePhase.playCard(attemptedDefense, 1, initial)
      res shouldBe initial.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    "not allow playCard when there is no open attack (attackCardOpt.isEmpty)" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val defenseCard = Card(Suit.Clubs, Rank.Seven)
      val attacker = Player("A", List(attackCard))
      val defender = Player("D", List(defenseCard))
      val initial = GameState(
        players = List(attacker, defender),
        deck = List.empty,
        table = Map(attackCard -> Some(Card(Suit.Clubs, Rank.Seven))),
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val res = DefensePhase.playCard(defenseCard, 1, initial)
      res shouldBe initial.copy(lastEvent = Some(GameEvent.InvalidMove))
    }

    "allow defender to use trump to beat a non-trump attack (defenseCard is trump)" in {
      val attackCard = Card(Suit.Clubs, Rank.Six)
      val trump = Card(Suit.Hearts, Rank.Seven)
      val attacker = Player("A", List(attackCard))
      val defender = Player("D", List(trump))
      val initial = GameState(
        players = List(attacker, defender),
        deck = List.empty,
        table = Map(attackCard -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Hearts, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val res = DefensePhase.playCard(trump, 1, initial)
      res.players(1).hand.shouldNot(contain(trump))
      res.table.get(attackCard).flatten.should(contain(trump))
      res.gamePhase shouldBe AttackPhase
      res.lastEvent.get.should(be(a[GameEvent.Defend]))
    }

    "allow defender to beat a trump attack with higher trump (trump vs trump)" in {
      val attackCard = Card(Suit.Diamonds, Rank.Six)
      val defenseCard = Card(Suit.Diamonds, Rank.Seven)
      val attacker = Player("A", List(attackCard))
      val defender = Player("D", List(defenseCard))
      val initial = GameState(
        players = List(attacker, defender),
        deck = List.empty,
        table = Map(attackCard -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val res = DefensePhase.playCard(defenseCard, 1, initial)
      res.players(1).hand.shouldNot(contain(defenseCard))
      res.table.get(attackCard).flatten.should(contain(defenseCard))
      res.gamePhase shouldBe AttackPhase
      res.lastEvent.get.should(be(a[GameEvent.Defend]))
    }

    "remain in DefensePhase when not all attacks are defended (nextPhase = DefensePhase)" in {
      val attack1 = Card(Suit.Clubs, Rank.Six)
      val attack2 = Card(Suit.Clubs, Rank.Seven)
      val defenseCard = Card(Suit.Clubs, Rank.Eight)
      val attacker = Player("A", List(attack1, attack2))
      val defender = Player("D", List(defenseCard))
      val initial = GameState(
        players = List(attacker, defender),
        deck = List.empty,
        table = Map(attack1 -> None, attack2 -> None),
        discardPile = List.empty,
        trumpCard = Card(Suit.Diamonds, Rank.Ace),
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = DefensePhase
      )

      val res = DefensePhase.playCard(defenseCard, 1, initial)
      res.gamePhase shouldBe DefensePhase
      res.table.values.count(_.isEmpty) shouldBe 1
      res.lastEvent.get.should(be(a[GameEvent.Defend]))
    }
  }
}
