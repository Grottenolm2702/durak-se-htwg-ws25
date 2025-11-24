package de.htwg.DurakApp.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._

import scala.util.Random

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {

    val FixedRandom: Random = new Random(0)
    val heartAce = Card(Suit.Hearts, Rank.Ace, isTrump = true)
    val spadeSix = Card(Suit.Spades, Rank.Six, isTrump = false)
    val diamondTen = Card(Suit.Diamonds, Rank.Ten, isTrump = false)
    val clubKing = Card(Suit.Clubs, Rank.King, isTrump = false)

    "initialize a game correctly" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      given Random = new Random(0)
      val playerNames = List("Player 1", "Player 2")
      val gameState = controller.initGame(36, playerNames)

      gameState.playerList.length shouldBe 2
      gameState.playerList.head.name shouldBe "Player 1"
      gameState.deck.length shouldBe 24
      gameState.playerList.forall(_.hand.length == 6) shouldBe true
    }

    "handle small deck during initialization" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      given Random = new Random(0)
      val playerNames = List(
        "Player 1",
        "Player 2",
        "Player 3",
        "Player 4",
        "Player 5",
        "Player 6",
        "Player 7"
      )
      val gameState = controller.initGame(36, playerNames)

      gameState.playerList.length shouldBe 7

      gameState.playerList.forall(_.hand.length == 5) shouldBe true
      gameState.deck.length shouldBe 1
    }

    "update finished players" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val player1 = Player("Player 1", List(), isDone = false)
      val player2 =
        Player("Player 2", List(Card(Suit.Clubs, Rank.Ace)), isDone = false)
      val gameState = GameState(List(player1, player2), Nil, Suit.Clubs)

      val updatedGame = controller.updateFinishedPlayers(gameState)
      updatedGame.playerList.find(_.name == "Player 1").get.isDone shouldBe true
      updatedGame.playerList
        .find(_.name == "Player 2")
        .get
        .isDone shouldBe false
    }

    "putFirstCardAtEnd should handle empty list (Nil)" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val input = Nil
      val result = controller.putFirstCardAtEnd(input)
      result shouldBe Nil
    }

    "handle game end" in {
      val player1 = Player("Player 1", List(), isDone = true)
      val player2 =
        Player("Player 2", List(Card(Suit.Clubs, Rank.Ace)), isDone = false)
      val initialGameState = GameState(
        List(player1, player2),
        Nil,
        Suit.Clubs,
        status = GameStatus.ATTACK
      )
      val controller = Controller(initialGameState)

      val newState = controller.handleEnd(controller.game)
      newState.status shouldBe GameStatus.GAME_OVER
    }

    "handle game end with no loser (draw)" in {
      val player1 = Player("Player 1", List(), isDone = true)
      val player2 = Player("Player 2", List(), isDone = true)
      val initialGameState = GameState(
        List(player1, player2),
        Nil,
        Suit.Clubs,
        status = GameStatus.ATTACK
      )
      val controller = new Controller(initialGameState)

      val newState = controller.handleEnd(controller.game)
      newState.status shouldBe GameStatus.GAME_OVER
    }

    "attack phase" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val attacker = Player(
        "Attacker",
        List(Card(Suit.Clubs, Rank.Seven), Card(Suit.Clubs, Rank.Eight)),
        false
      )
      val defender =
        Player("Defender", List(Card(Suit.Hearts, Rank.Ace)), false)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades)

      val inputs = List("0", "pass")
      val mockInput = new MockPlayerInput(inputs)

      val finalState = controller.attack(gameState, 0, mockInput)
      finalState.attackingCards.length shouldBe 1
      finalState.attackingCards.head.rank shouldBe Rank.Seven
      finalState.playerList.head.hand.length shouldBe 1
    }

    "should not allow passing when no cards are on the table" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val attacker =
        Player("Attacker", List(Card(Suit.Clubs, Rank.Seven)), false)
      val defender =
        Player("Defender", List(Card(Suit.Hearts, Rank.Ace)), false)
      val gameState = GameState(
        List(attacker, defender),
        Nil,
        Suit.Spades,
        attackingCards = Nil
      )

      val inputs = List("pass", "0", "pass")
      val mockInput = new MockPlayerInput(inputs)

      controller.attack(gameState, 0, mockInput)

      controller.game.attackingCards.length shouldBe 1
      controller.game.attackingCards.head.rank shouldBe Rank.Seven
      controller.game.playerList.head.hand.length shouldBe 0
    }

    "defend phase - successful defense" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val attacker = Player("Attacker", List(), false)
      val defender =
        Player("Defender", List(Card(Suit.Clubs, Rank.Eight)), false)
      val attackingCard = Card(Suit.Clubs, Rank.Seven)
      val gameState = GameState(
        List(attacker, defender),
        Nil,
        Suit.Spades,
        attackingCards = List(attackingCard)
      )

      val inputs = List("0")
      val mockInput = new MockPlayerInput(inputs)

      val (finalState, defenderTook) =
        controller.defend(gameState, 1, mockInput)
      defenderTook shouldBe false
      finalState.defendingCards.length shouldBe 0
      finalState.discardPile.length shouldBe 2
      finalState.attackingCards.isEmpty shouldBe true
    }

    "defend phase - taking cards" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val attacker = Player("Attacker", List(), false)
      val defender = Player("Defender", List(Card(Suit.Clubs, Rank.Six)), false)
      val attackingCard = Card(Suit.Clubs, Rank.Seven)
      val gameState = GameState(
        List(attacker, defender),
        Nil,
        Suit.Spades,
        attackingCards = List(attackingCard)
      )

      val inputs = List("100", "take")
      val mockInput = new MockPlayerInput(inputs)

      val (finalState, defenderTook) =
        controller.defend(gameState, 1, mockInput)
      defenderTook shouldBe true
      finalState
        .playerList(1)
        .hand
        .length shouldBe 2
      finalState.attackingCards.isEmpty shouldBe true
    }

    "draw phase" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val player1 =
        Player("P1", List.fill(4)(Card(Suit.Clubs, Rank.Ace)), false)
      val player2 =
        Player("P2", List.fill(5)(Card(Suit.Clubs, Rank.Ace)), false)
      val deck = List.fill(5)(Card(Suit.Spades, Rank.King))
      val gameState = GameState(List(player1, player2), deck, Suit.Hearts)

      val finalState = controller.draw(gameState, 0)
      finalState.playerList(0).hand.length shouldBe 6
      finalState.playerList(1).hand.length shouldBe 6
      finalState.deck.length shouldBe 2
    }

    "gameLoop should run a full game until a loser is determined" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      given Random = new Random(0)

      val attackerCard = Card(Suit.Clubs, Rank.Seven)
      val defenderCard = Card(Suit.Hearts, Rank.Ace)
      val attacker = Player("Alice", List(attackerCard))
      val defender = Player("Bob", List(defenderCard))
      val initialGameState = GameState(
        playerList = List(attacker, defender),
        deck = Nil,
        trump = Suit.Spades
      )

      val inputs = List("0", "pass", "take")
      val mockInput = new MockPlayerInput(inputs)

      controller.gameLoop(initialGameState, 0, mockInput)

      controller.game.status shouldBe GameStatus.GAME_OVER
      val finalGame = controller.game
      finalGame.playerList.find(_.name == "Alice").get.isDone shouldBe true
      finalGame.playerList.find(_.name == "Bob").get.isDone shouldBe false
      finalGame.playerList.find(_.name == "Bob").get.hand.length should be > 1
    }

    "gameLoop should skip a player that is done and select the next active player as attacker" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      given Random = new Random(0)

      val attackerCard = Card(Suit.Clubs, Rank.Seven)
      val defenderCard = Card(Suit.Hearts, Rank.Ace)
      val player1 = Player("Alice", List(), isDone = true)
      val player2 = Player("Bob", List(attackerCard))
      val player3 = Player("Charlie", List(defenderCard))

      val initialGameState = GameState(
        playerList = List(player1, player2, player3),
        deck = Nil,
        trump = Suit.Spades
      )

      val inputs = List("0", "pass", "take")
      val mockInput = new MockPlayerInput(inputs)

      controller.gameLoop(initialGameState, 0, mockInput)

      controller.game.status shouldBe GameStatus.GAME_OVER
      val finalGame = controller.game
      finalGame.playerList.find(_.name == "Alice").get.isDone shouldBe true
      finalGame.playerList.find(_.name == "Bob").get.isDone shouldBe true
      finalGame.playerList.find(_.name == "Charlie").get.isDone shouldBe false
      finalGame.playerList
        .find(_.name == "Charlie")
        .get
        .hand
        .length should be > 0
    }

    "setupGameAndStart should run a full game with a small deck" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val playerNames = List("P1", "P2")
      val mockInput = new MockPlayerInput(List("0", "pass", "take"))

      controller.setupGameAndStart(2, playerNames, new Random(0), mockInput)

      controller.game.status shouldBe GameStatus.GAME_OVER
      val finalGame = controller.game
      finalGame.playerList.count(
        _.isDone
      ) shouldBe (finalGame.playerList.length - 1)
      finalGame.playerList
        .find(!_.isDone)
        .get
        .name should not be empty
    }

    "attack phase with various invalid inputs" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val attacker = Player(
        "Attacker",
        List(Card(Suit.Clubs, Rank.Seven), Card(Suit.Clubs, Rank.Eight)),
        false
      )
      val defender =
        Player("Defender", List(Card(Suit.Hearts, Rank.Ace)), false)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades)

      val inputs = List("pass", "foo", "10", "0", "pass")
      val mockInput = new MockPlayerInput(inputs)

      val finalState = controller.attack(gameState, 0, mockInput)
      finalState.attackingCards.length shouldBe 1
      finalState.playerList.head.hand.length shouldBe 1
      finalState.status shouldBe GameStatus.PASS
    }

    "defend phase when no attack is happening" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val player = Player("Player", List(), false)
      val gameState =
        GameState(List(player), Nil, Suit.Clubs, attackingCards = Nil)
      val mockInput = new MockPlayerInput(List())

      val (finalState, defenderTook) =
        controller.defend(gameState, 0, mockInput)

      finalState shouldBe gameState
      defenderTook shouldBe false
    }

    "defend should eventually take cards after invalid inputs" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val defender = Player(
        "Defender",
        List(Card(Suit.Clubs, Rank.Six)),
        false
      )
      val attackingCard = Card(Suit.Clubs, Rank.Seven)
      val gameState = GameState(
        List(Player("Attacker", Nil), defender),
        Nil,
        Suit.Spades,
        attackingCards = List(attackingCard)
      )

      val inputs = List("foo", "0", "take")
      val mockInput = new MockPlayerInput(inputs)

      val (finalState, defenderTook) =
        controller.defend(gameState, 1, mockInput)
      defenderTook shouldBe true
      finalState
        .playerList(1)
        .hand
        .length shouldBe 2
      finalState.status shouldBe GameStatus.TAKE
    }

    "draw phase should not change anything if hands are full" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val player1 =
        Player("P1", List.fill(6)(Card(Suit.Clubs, Rank.Ace)), false)
      val player2 =
        Player("P2", List.fill(7)(Card(Suit.Clubs, Rank.Ace)), false)
      val deck = List.fill(5)(Card(Suit.Spades, Rank.King))
      val gameState = GameState(List(player1, player2), deck, Suit.Hearts)

      val finalState = controller.draw(gameState, 0)
      finalState.playerList(0).hand.length shouldBe 6
      finalState.playerList(1).hand.length shouldBe 7
      finalState.deck.length shouldBe 5
    }

    "findNextDefender: finds the next non-done player" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val p1 = Player("A", List(), false)
      val p2 = Player("B", List(), false)
      val p3 = Player("C", List(), true)
      val game = GameState(List(p1, p2, p3), Nil, Suit.Hearts)
      controller.findNextDefender(game, 0) shouldBe 1
    }
    "findNextDefender: skips a done player to find the next active one" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val p1 = Player("A", List(), isDone = false)
      val p2 = Player("B", List(), isDone = true)
      val p3 = Player("C", List(), isDone = false)
      val game = GameState(List(p1, p2, p3), Nil, Suit.Hearts)
      controller.findNextDefender(
        game,
        0
      ) shouldBe 2
    }
    "createDeck: returns requested size and marks trump; works for small and large sizes" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      given Random = FixedRandom
      val (deck36, trump36) = controller.createDeck(36)
      deck36.length shouldBe 36
      deck36.count(_.isTrump) should be > 0
      deck36.exists(_.suit == trump36) shouldBe true

      val (deck10, trump10) = controller.createDeck(10)
      deck10.length shouldBe 10
      deck10.count(_.isTrump) should be >= 1
      deck10.exists(_.suit == trump10) shouldBe true
    }
    "selectFirstAttacker: chooses (dealer+1) when no trumps, otherwise player with lowest trump rank" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )

      val p1 = Player("A", List(heartAce.copy(isTrump = false)))
      val p2 = Player("B", List(spadeSix.copy(isTrump = false)))
      val gNoTrumps = GameState(List(p1, p2), Nil, Suit.Hearts)
      controller.selectFirstAttacker(gNoTrumps, 0) shouldBe 1

      val pA = Player("A", List(heartAce.copy(isTrump = false)))
      val pB = Player("B", List(spadeSix.copy(isTrump = true)))
      val pC = Player("C", List(clubKing.copy(isTrump = true)))
      val gTrumps = GameState(List(pA, pB, pC), Nil, Suit.Spades)
      val idx = controller.selectFirstAttacker(gTrumps, 0)
      idx should (be >= 0 and be < 3)

      val pSameTrump1 =
        Player("P1", List(Card(Suit.Spades, Rank.Six, isTrump = true)))
      val pSameTrump2 =
        Player("P2", List(Card(Suit.Clubs, Rank.Six, isTrump = true)))
      val pHigherTrump =
        Player("P3", List(Card(Suit.Diamonds, Rank.Seven, isTrump = true)))
      val gSameTrump = GameState(
        List(pSameTrump1, pSameTrump2, pHigherTrump),
        Nil,
        Suit.Spades
      )
      controller.selectFirstAttacker(
        gSameTrump,
        0
      ) shouldBe 0
    }
    "checkLooser: true when <= 1 active player" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val game1 = GameState(
        List(Player("A", Nil, true), Player("B", Nil, true)),
        Nil,
        Suit.Hearts
      )
      controller.checkLooser(game1) shouldBe true

      val game2 = GameState(
        List(Player("A", List(heartAce)), Player("B", Nil, true)),
        Nil,
        Suit.Hearts
      )
      controller.checkLooser(game2) shouldBe true

      val game3 = GameState(
        List(Player("A", List(heartAce)), Player("B", List(spadeSix))),
        Nil,
        Suit.Hearts
      )
      controller.checkLooser(game3) shouldBe false
    }
    "findNextActive: returns next active skipping done players" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val p0 = Player("A", Nil, isDone = true)
      val p1 = Player("B", Nil, isDone = false)
      val p2 = Player("C", Nil, isDone = true)
      val g = GameState(List(p0, p1, p2), Nil, Suit.Hearts)
      controller.findNextActive(g, 0) shouldBe 1
    }
    "findNextActive: returns next active when next player is not done" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val p0 = Player("A", Nil, isDone = false)
      val p1 = Player("B", Nil, isDone = false)
      val g = GameState(List(p0, p1), Nil, Suit.Hearts)
      controller.findNextActive(g, 0) shouldBe 1
    }
    "nextAttackerIndex: handles 1vs1 and >2 players and defenderTook flag" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val p1 = Player("A", List(heartAce))
      val p2 = Player("B", List(spadeSix))
      val g21 = GameState(List(p1, p2), Nil, Suit.Hearts)
      controller.nextAttackerIndex(
        g21,
        0,
        1,
        defenderTook = false
      ) should (be >= 0 and be < 2)

      val p3 = Player("C", List(diamondTen))
      val g3 = GameState(List(p1, p2, p3), Nil, Suit.Hearts)
      val idx = controller.nextAttackerIndex(g3, 0, 1, defenderTook = true)
      idx should (be >= 0 and be < 3)
    }
    "nextAttackerIndex: handles >2 players and defenderTook = false" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val p1 = Player("A", List(heartAce))
      val p2 = Player("B", List(spadeSix))
      val p3 = Player("C", List(diamondTen))
      val g3 = GameState(List(p1, p2, p3), Nil, Suit.Hearts)
      val idx = controller.nextAttackerIndex(g3, 0, 1, defenderTook = false)
      idx shouldBe 1
    }
    "canBeat: compares by suit and rank and trump rules" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      controller.canBeat(
        Card(Suit.Clubs, Rank.Six),
        Card(Suit.Clubs, Rank.Ace),
        Suit.Hearts
      ) shouldBe true
      controller.canBeat(
        heartAce,
        Card(Suit.Hearts, Rank.King),
        Suit.Hearts
      ) shouldBe false
      controller.canBeat(
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Hearts, Rank.Six, true),
        Suit.Hearts
      ) shouldBe true
      controller.canBeat(
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Spades, Rank.King),
        Suit.Hearts
      ) shouldBe false
    }
    "tableCardsContainRank: finds ranks across attacking and defending cards" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val g = GameState(
        Nil,
        Nil,
        Suit.Hearts,
        attackingCards = List(heartAce),
        defendingCards = List(spadeSix)
      )
      controller.tableCardsContainRank(g, heartAce) shouldBe true
      controller.tableCardsContainRank(g, diamondTen) shouldBe false
    }
    "moveCard: moves card for valid index and returns unchanged for invalid index" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      controller.moveCard(
        List(heartAce, spadeSix),
        List(diamondTen),
        -1
      ) shouldBe (List(heartAce, spadeSix), List(diamondTen))
      val (from, to) =
        controller.moveCard(List(heartAce, spadeSix), List(diamondTen), 1)
      from shouldBe List(heartAce)
      to should contain(spadeSix)
    }
    "attack should eventually perform valid action after invalid inputs" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val attacker = Player("A", List(heartAce), false)
      val g = GameState(List(attacker), Nil, Suit.Hearts)
      val mockInput =
        new MockPlayerInput(
          List("1", "0", "pass")
        )
      val finalState = controller.attack(g, 0, mockInput)
      finalState.attackingCards.length shouldBe 1
      finalState.attackingCards.head shouldBe heartAce
      finalState.playerList.head.hand.length shouldBe 0
      finalState.status shouldBe GameStatus.PASS
    }
    "attack should prevent playing more than allowed cards on table" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val attacker = Player("A", List.fill(7)(heartAce), false)
      val attackingCards = List.fill(6)(spadeSix)
      val g = GameState(
        List(attacker),
        Nil,
        Suit.Hearts,
        attackingCards = attackingCards
      )
      val mockInput =
        new MockPlayerInput(
          List("0", "pass")
        )
      val initialAttackerHandSize = g.playerList.head.hand.length
      val initialTableCardsCount =
        g.attackingCards.length + g.defendingCards.length

      val finalState = controller.attack(g, 0, mockInput)
      finalState.attackingCards.length shouldBe 6
      finalState.playerList.head.hand.length shouldBe initialAttackerHandSize
      finalState.status shouldBe GameStatus.PASS
    }
    "attack should prevent playing non-matching rank cards" in {
      val controller = new Controller(
        GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
      )
      val attacker = Player("A", List(diamondTen), false)
      val attackingCards = List(spadeSix)
      val g = GameState(
        List(attacker),
        Nil,
        Suit.Hearts,
        attackingCards = attackingCards
      )
      val mockInput =
        new MockPlayerInput(
          List("0", "pass")
        )
      val initialAttackerHandSize = g.playerList.head.hand.length

      val finalState = controller.attack(g, 0, mockInput)
      finalState.attackingCards.length shouldBe 1
      finalState.playerList.head.hand.length shouldBe initialAttackerHandSize
      finalState.status shouldBe GameStatus.PASS
    }
  }
}

class MockPlayerInput(inputs: List[String]) extends PlayerInput {
  private var remainingInputs = inputs

  override def choosePassOrAttackCard(
      attacker: Player,
      game: GameState
  ): (Boolean, Int) = {
    val input = remainingInputs.head
    remainingInputs = remainingInputs.tail
    input match {
      case "pass" => (true, 0)
      case s      => util.Try(s.toInt).map(idx => (false, idx)).getOrElse((false, -1))
    }
  }

  override def chooseTakeOrDefenseCard(
      defender: Player,
      attackCard: Card,
      game: GameState
  ): (Boolean, Int) = {
    val input = remainingInputs.head
    remainingInputs = remainingInputs.tail
    input match {
      case "take" => (true, 0)
      case s      => util.Try(s.toInt).map(idx => (false, idx)).getOrElse((false, -1))
    }
  }
}
