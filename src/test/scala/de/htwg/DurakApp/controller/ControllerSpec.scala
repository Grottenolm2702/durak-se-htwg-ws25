package de.htwg.DurakApp.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model._
import de.htwg.DurakApp.util.Observer
import scala.util.Random

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {

    val initialGame = GameState(playerList = Nil, deck = Nil, trump = Suit.Clubs)
    val controller = new Controller(initialGame)
    val FixedRandom: Random = new Random(0)
    val heartAce = Card(Suit.Hearts, Rank.Ace, isTrump = true)
    val spadeSix = Card(Suit.Spades, Rank.Six, isTrump = false)
    val diamondTen = Card(Suit.Diamonds, Rank.Ten, isTrump = false)
    val clubKing = Card(Suit.Clubs, Rank.King, isTrump = false)

    "initialize a game correctly" in {
      given Random = new Random(0)
      val playerNames = List("Player 1", "Player 2")
      val gameState = controller.initGame(36, playerNames)

      gameState.playerList.length shouldBe 2
      gameState.playerList.head.name shouldBe "Player 1"
      gameState.deck.length shouldBe 24 // 36 - 2 * 6
      gameState.playerList.forall(_.hand.length == 6) shouldBe true
    }

    "handle small deck during initialization" in {
      given Random = new Random(0)
      val playerNames = List("Player 1", "Player 2", "Player 3", "Player 4", "Player 5", "Player 6", "Player 7")
      val gameState = controller.initGame(36, playerNames)

      gameState.playerList.length shouldBe 7
      // 36 cards / 7 players = 5 cards each
      gameState.playerList.forall(_.hand.length == 5) shouldBe true
      gameState.deck.length shouldBe 1 // 36 - 7 * 5
    }

    "update finished players" in {
      val player1 = Player("Player 1", List(), isDone = false)
      val player2 = Player("Player 2", List(Card(Suit.Clubs, Rank.Ace)), isDone = false)
      val gameState = GameState(List(player1, player2), Nil, Suit.Clubs)
      
      val updatedGame = controller.updateFinishedPlayers(gameState)
      updatedGame.playerList.find(_.name == "Player 1").get.isDone shouldBe true
      updatedGame.playerList.find(_.name == "Player 2").get.isDone shouldBe false
    }

    "handle game end" in {
      val player1 = Player("Player 1", List(), isDone = true)
      val player2 = Player("Player 2", List(Card(Suit.Clubs, Rank.Ace)), isDone = false)
      val gameState = GameState(List(player1, player2), Nil, Suit.Clubs)

      controller.handleEnd(gameState)
      controller.status shouldBe "Player 2 ist der Durak!"
    }

    "handle game end with no loser (draw)" in {
      val player1 = Player("Player 1", List(), isDone = true)
      val player2 = Player("Player 2", List(), isDone = true)
      val gameState = GameState(List(player1, player2), Nil, Suit.Clubs)

      controller.handleEnd(gameState)
      controller.status shouldBe "Alle fertig — Unentschieden!"
    }

    "attack phase" in {
      val attacker = Player("Attacker", List(Card(Suit.Clubs, Rank.Seven), Card(Suit.Clubs, Rank.Eight)), false)
      val defender = Player("Defender", List(Card(Suit.Hearts, Rank.Ace)), false)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades)
      
      // Mocking user input
      val inputs = List("0", "pass")
      val mockInput = new MockPlayerInput(inputs)

      val finalState = controller.attack(gameState, 0, mockInput)
      finalState.attackingCards.length shouldBe 1
      finalState.attackingCards.head.rank shouldBe Rank.Seven
      finalState.playerList.head.hand.length shouldBe 1
    }

    "should not allow passing when no cards are on the table" in {
      val attacker = Player("Attacker", List(Card(Suit.Clubs, Rank.Seven)), false)
      val defender = Player("Defender", List(Card(Suit.Hearts, Rank.Ace)), false)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades, attackingCards = Nil)

      val inputs = List("pass", "0", "pass")
      val mockInput = new MockPlayerInput(inputs)

      controller.attack(gameState, 0, mockInput)
      // After the invalid "pass", the loop continues and then a valid attack is made.
      // The status will reflect the last successful action.
      controller.game.attackingCards.length shouldBe 1
      controller.game.attackingCards.head.rank shouldBe Rank.Seven
      controller.game.playerList.head.hand.length shouldBe 0
    }

    "defend phase - successful defense" in {
      val attacker = Player("Attacker", List(), false)
      val defender = Player("Defender", List(Card(Suit.Clubs, Rank.Eight)), false)
      val attackingCard = Card(Suit.Clubs, Rank.Seven)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades, attackingCards = List(attackingCard))

      val inputs = List("0")
      val mockInput = new MockPlayerInput(inputs)

      val (finalState, defenderTook) = controller.defend(gameState, 1, mockInput)
      defenderTook shouldBe false
      finalState.defendingCards.length shouldBe 0
      finalState.discardPile.length shouldBe 2
      finalState.attackingCards.isEmpty shouldBe true
    }

    "defend phase - taking cards" in {
      val attacker = Player("Attacker", List(), false)
      val defender = Player("Defender", List(Card(Suit.Clubs, Rank.Six)), false)
      val attackingCard = Card(Suit.Clubs, Rank.Seven)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades, attackingCards = List(attackingCard))

      val inputs = List("take")
      val mockInput = new MockPlayerInput(inputs)

      val (finalState, defenderTook) = controller.defend(gameState, 1, mockInput)
      defenderTook shouldBe true
      finalState.playerList(1).hand.length shouldBe 2 // own card + attacking card
      finalState.attackingCards.isEmpty shouldBe true
    }

    "draw phase" in {
      val player1 = Player("P1", List.fill(4)(Card(Suit.Clubs, Rank.Ace)), false)
      val player2 = Player("P2", List.fill(5)(Card(Suit.Clubs, Rank.Ace)), false)
      val deck = List.fill(5)(Card(Suit.Spades, Rank.King))
      val gameState = GameState(List(player1, player2), deck, Suit.Hearts)

      val finalState = controller.draw(gameState, 0)
      finalState.playerList(0).hand.length shouldBe 6 // needs 2
      finalState.playerList(1).hand.length shouldBe 6 // needs 1
      finalState.deck.length shouldBe 2 // 5 - 2 - 1
    }

    "gameLoop should run a full game until a loser is determined" in {
      given Random = new Random(0)

      // Setup a game state that will end quickly
      val attackerCard = Card(Suit.Clubs, Rank.Seven)
      val defenderCard = Card(Suit.Hearts, Rank.Ace)
      val attacker = Player("Alice", List(attackerCard))
      val defender = Player("Bob", List(defenderCard))
      val initialGameState = GameState(
        playerList = List(attacker, defender),
        deck = Nil, // Empty deck
        trump = Suit.Spades
      )

      // Inputs for the game loop:
      // 1. Alice (attacker) chooses to play her only card (index 0).
      // 2. Alice passes.
      // 3. Bob (defender) chooses to take the card.
      val inputs = List("0", "pass", "take")
      val mockInput = new MockPlayerInput(inputs)

      // Start the game loop
      controller.gameLoop(initialGameState, 0, mockInput)

      // Assert the final state
      controller.status shouldBe "Bob ist der Durak!"
      val finalGame = controller.game
      finalGame.playerList.find(_.name == "Alice").get.isDone shouldBe true
      finalGame.playerList.find(_.name == "Bob").get.isDone shouldBe false
      finalGame.playerList.find(_.name == "Bob").get.hand.length should be > 1
    }

    "gameLoop should skip a player that is done and select the next active player as attacker" in {
      given Random = new Random(0)

      class TestObserver extends de.htwg.DurakApp.util.Observer {
        var messages: List[String] = Nil
        def update: Unit = {
          messages = controller.status :: messages
        }
      }
      val observer = new TestObserver
      controller.add(observer)


      // Setup a game state that will end quickly
      val attackerCard = Card(Suit.Clubs, Rank.Seven)
      val defenderCard = Card(Suit.Hearts, Rank.Ace)
      val player1 = Player("Alice", List(), isDone = true)
      val player2 = Player("Bob", List(attackerCard))
      val player3 = Player("Charlie", List(defenderCard))

      val initialGameState = GameState(
        playerList = List(player1, player2, player3),
        deck = Nil, // Empty deck
        trump = Suit.Spades
      )

      // Inputs for the game loop:
      // 1. Bob (attacker) chooses to play his only card (index 0).
      // 2. Bob passes.
      // 3. Charlie (defender) chooses to take the card.
      val inputs = List("0", "pass", "take")
      val mockInput = new MockPlayerInput(inputs)

      // Start the game loop with player 1 (Alice) as the initial attacker
      controller.gameLoop(initialGameState, 0, mockInput)

      // Assert that the observer was notified with the correct new round message
      observer.messages.reverse should contain ("Neue Runde — Angreifer: Bob, Verteidiger: Charlie")

      // Assert the final state
      controller.status shouldBe "Charlie ist der Durak!"
      val finalGame = controller.game
      finalGame.playerList.find(_.name == "Alice").get.isDone shouldBe true
      finalGame.playerList.find(_.name == "Bob").get.isDone shouldBe true
      finalGame.playerList.find(_.name == "Charlie").get.isDone shouldBe false
      finalGame.playerList.find(_.name == "Charlie").get.hand.length should be > 0
    }

    "setupGameAndStart should run a full game with a small deck" in {
      class TestObserver extends de.htwg.DurakApp.util.Observer {
        var messages: List[String] = Nil
        def update: Unit = {
          messages = controller.status :: messages
        }
      }
      val observer = new TestObserver
      controller.add(observer)

      val playerNames = List("P1", "P2")
      val mockInput = new MockPlayerInput(List("0", "pass", "take"))

      controller.setupGameAndStart(2, playerNames, new Random(0), mockInput)

      val initialStatus = observer.messages.reverse(1)
      initialStatus should include ("Dealer:")
      initialStatus should include ("First attacker:")

      controller.status should endWith ("ist der Durak!")
    }

    "attack phase with various invalid inputs" in {
      val attacker = Player("Attacker", List(Card(Suit.Clubs, Rank.Seven), Card(Suit.Clubs, Rank.Eight)), false)
      val defender = Player("Defender", List(Card(Suit.Hearts, Rank.Ace)), false)
      val gameState = GameState(List(attacker, defender), Nil, Suit.Spades)
      
      val inputs = List("pass", "foo", "10", "0", "pass")
      val mockInput = new MockPlayerInput(inputs)

      val finalState = controller.attack(gameState, 0, mockInput)
      finalState.attackingCards.length shouldBe 1
      finalState.playerList.head.hand.length shouldBe 1
      controller.status shouldBe "Attack phase finished."
    }

    "defend phase when no attack is happening" in {
      val player = Player("Player", List(), false)
      val gameState = GameState(List(player), Nil, Suit.Clubs, attackingCards = Nil)
      val mockInput = new MockPlayerInput(List())

      val (finalState, defenderTook) = controller.defend(gameState, 0, mockInput)
      
      finalState shouldBe gameState
      defenderTook shouldBe false
    }

    "defend phase with invalid card and invalid input" in {
      val defender = Player("Defender", List(Card(Suit.Clubs, Rank.Six)), false) // Can't beat Seven
      val attackingCard = Card(Suit.Clubs, Rank.Seven)
      val gameState = GameState(List(Player("Attacker", Nil), defender), Nil, Suit.Spades, attackingCards = List(attackingCard))

      val inputs = List("foo", "0", "take")
      val mockInput = new MockPlayerInput(inputs)

      val (finalState, defenderTook) = controller.defend(gameState, 1, mockInput)
      defenderTook shouldBe true
      finalState.playerList(1).hand.length shouldBe 2
      controller.status shouldBe "Defender nimmt die Karten."
    }

    "draw phase should not change anything if hands are full" in {
      val player1 = Player("P1", List.fill(6)(Card(Suit.Clubs, Rank.Ace)), false)
      val player2 = Player("P2", List.fill(7)(Card(Suit.Clubs, Rank.Ace)), false)
      val deck = List.fill(5)(Card(Suit.Spades, Rank.King))
      val gameState = GameState(List(player1, player2), deck, Suit.Hearts)

      val finalState = controller.draw(gameState, 0)
      finalState.playerList(0).hand.length shouldBe 6
      finalState.playerList(1).hand.length shouldBe 7
      finalState.deck.length shouldBe 5
    }



    "cardShortString: return short string for card (includes trump tag)" in {
      val s = controller.cardShortString(heartAce)
      s should include("Ace")
      s should include("Hearts")
      s should include("(T)")
    }

    "cardShortString: return short string for card (excludes trump tag)" in {
      val s = controller.cardShortString(spadeSix)
      s should include("Six")
      s should include("Spades")
      s should not include("(T)")
    }

    "moveTrump: rotates first card to end and handles Nil" in {
      controller.moveTrump(Nil) shouldBe Nil
      val lst = List(heartAce, spadeSix, diamondTen)
      val moved = controller.moveTrump(lst)
      moved.last shouldBe heartAce
      moved.head shouldBe spadeSix
    }

    "findNextDefender: finds the next non-done player" in {
      val p1 = Player("A", List(), false)
      val p2 = Player("B", List(), false)
      val p3 = Player("C", List(), true)
      val game = GameState(List(p1, p2, p3), Nil, Suit.Hearts)
      controller.findNextDefender(game, 0) shouldBe 1
    }

    "findNextDefender: skips a done player to find the next active one" in {
      val p1 = Player("A", List(), isDone = false)
      val p2 = Player("B", List(), isDone = true)
      val p3 = Player("C", List(), isDone = false)
      val game = GameState(List(p1, p2, p3), Nil, Suit.Hearts)
      controller.findNextDefender(game, 0) shouldBe 2 // Should skip P2 (done) and find P3
    }

    "createDeck: returns requested size and marks trump; works for small and large sizes" in {
      given Random = FixedRandom // Use FixedRandom
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
      // no trumps
      val p1 = Player("A", List(heartAce.copy(isTrump = false)))
      val p2 = Player("B", List(spadeSix.copy(isTrump = false)))
      val gNoTrumps = GameState(List(p1, p2), Nil, Suit.Hearts)
      controller.selectFirstAttacker(gNoTrumps, 0) shouldBe 1

      // with trumps: give player B a trump six and C a trump king -> lowest trump rank should win
      val pA = Player("A", List(heartAce.copy(isTrump = false)))
      val pB = Player("B", List(spadeSix.copy(isTrump = true)))
      val pC = Player("C", List(clubKing.copy(isTrump = true)))
      val gTrumps = GameState(List(pA, pB, pC), Nil, Suit.Spades)
      val idx = controller.selectFirstAttacker(gTrumps, 0)
      idx should (be >= 0 and be < 3)

      // Test case for multiple players with same lowest trump rank
      val pSameTrump1 = Player("P1", List(Card(Suit.Spades, Rank.Six, isTrump = true)))
      val pSameTrump2 = Player("P2", List(Card(Suit.Clubs, Rank.Six, isTrump = true)))
      val pHigherTrump = Player("P3", List(Card(Suit.Diamonds, Rank.Seven, isTrump = true)))
      val gSameTrump = GameState(List(pSameTrump1, pSameTrump2, pHigherTrump), Nil, Suit.Spades)
      controller.selectFirstAttacker(gSameTrump, 0) shouldBe 0 // P1 has lowest index with lowest trump
    }

    "checkLooser: true when <= 1 active player" in {
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
      val p0 = Player("A", Nil, isDone = true)
      val p1 = Player("B", Nil, isDone = false)
      val p2 = Player("C", Nil, isDone = true)
      val g = GameState(List(p0, p1, p2), Nil, Suit.Hearts)
      controller.findNextActive(g, 0) shouldBe 1
    }

    "findNextActive: returns next active when next player is not done" in {
      val p0 = Player("A", Nil, isDone = false)
      val p1 = Player("B", Nil, isDone = false)
      val g = GameState(List(p0, p1), Nil, Suit.Hearts)
      controller.findNextActive(g, 0) shouldBe 1
    }

    "nextAttackerIndex: handles 1vs1 and >2 players and defenderTook flag" in {
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
      val p1 = Player("A", List(heartAce))
      val p2 = Player("B", List(spadeSix))
      val p3 = Player("C", List(diamondTen))
      val g3 = GameState(List(p1, p2, p3), Nil, Suit.Hearts)
      val idx = controller.nextAttackerIndex(g3, 0, 1, defenderTook = false)
      idx shouldBe 1 // The next active player after the current attacker (P1) is P2 (index 1)
    }

    "canBeat: compares by suit and rank and trump rules" in {
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

    "attack with out of bounds index should show error" in {
      val observer = new TestObserver(controller)
      controller.add(observer)
      val attacker = Player("A", List(heartAce), false)
      val g = GameState(List(attacker), Nil, Suit.Hearts)
      val mockInput = new MockPlayerInput(List("1", "0", "pass")) // index 1 is out of bounds, then valid, then pass
      controller.attack(g, 0, mockInput)
      observer.messages.reverse should contain ("Index out of range.")
      controller.remove(observer)
    }

    "attack with more than 6 cards should show error" in {
      val observer = new TestObserver(controller)
      controller.add(observer)
      val attacker = Player("A", List.fill(7)(heartAce), false)
      val attackingCards = List.fill(6)(spadeSix)
      val g = GameState(List(attacker), Nil, Suit.Hearts, attackingCards = attackingCards)
      val mockInput = new MockPlayerInput(List("0", "pass"))
      controller.attack(g, 0, mockInput)
      observer.messages.reverse should contain ("Maximum 6 attack cards reached.")
      controller.remove(observer)
    }

    "attack with not allowed card should show error" in {
      val observer = new TestObserver(controller)
      controller.add(observer)
      val attacker = Player("A", List(diamondTen), false)
      val attackingCards = List(spadeSix) // rank Six
      val g = GameState(List(attacker), Nil, Suit.Hearts, attackingCards = attackingCards)
      val mockInput = new MockPlayerInput(List("0", "pass")) // diamondTen has rank Ten
      controller.attack(g, 0, mockInput)
      observer.messages.reverse should contain ("You can only play cards whose rank is already on the table.")
      controller.remove(observer)
    }
  }
}

class MockPlayerInput(inputs: List[Int]) extends PlayerInput {
  private var remainingInputs = inputs

  override def chooseAttackCard(attacker: Player, game: GameState): Int = {
    val input = remainingInputs.head
    remainingInputs = remainingInputs.tail
    input
  }

  override def chooseDefenseCard(defender: Player, attackCard: Card, game: GameState): Int = {
    val input = remainingInputs.head
    remainingInputs = remainingInputs.tail
    input
  }
}

class TestObserver extends Observer {
  var messages: List[String] = Nil
  def update: Unit = {
    // This is a bit of a hack to get the status message from the controller
    // It would be better to have the controller pass the message to the observer
    val game = new Controller(GameState(Nil, Nil, Suit.Clubs)).game
    messages = new TUI(new Controller(game)).buildStatusString(game) :: messages
  }
}