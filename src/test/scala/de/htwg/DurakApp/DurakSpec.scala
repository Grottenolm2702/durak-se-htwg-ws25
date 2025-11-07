package de.htwg.DurakApp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.{ByteArrayInputStream, InputStream}

class DurakSpec extends AnyWordSpec with Matchers {

  object DummyIO extends ConsoleIO {
    private val inputs = scala.collection.mutable.Queue[String]()
    def enqueue(inputsToAdd: String*): Unit = inputs ++= inputsToAdd.toList
    def reset(): Unit = inputs.clear()
    override def readLine(): String =
      if inputs.nonEmpty then inputs.dequeue() else "pass"
    override def println(s: String): Unit = () // ignore output
  }

  val heartAce = Card(Suit.Hearts, Rank.Ace, isTrump = true)
  val spadeSix = Card(Suit.Spades, Rank.Six, isTrump = false)
  val diamondTen = Card(Suit.Diamonds, Rank.Ten, isTrump = false)
  val clubKing = Card(Suit.Clubs, Rank.King, isTrump = false)
  val clubsAce = Card(Suit.Clubs, Rank.Ace, isTrump = false)

  "DurakApp" should {

    "safeToInt: convert string to int safely" in {
      DurakApp.safeToInt("42") shouldBe Some(42)
      DurakApp.safeToInt("abc") shouldBe None
      DurakApp.safeToInt(" 7 ") shouldBe Some(7)
    }

    "cardShortString: return short string for card (includes trump tag)" in {
      val s = DurakApp.cardShortString(heartAce)
      s should include("Ace")
      s should include("Hearts")
      s should include("(T)")
    }

    "cardShortString: return short string for card (excludes trump tag)" in {
      val s = DurakApp.cardShortString(spadeSix)
      s should include("Six")
      s should include("Spades")
      s should not include("(T)")
    }

    "DefaultConsoleIO println is callable (covers println line)" in {
      // Nur println testen — readLine blockiert in sbt's terminal proxy, deshalb vermeiden wir es.
      DefaultConsoleIO.println("hello-default-console")
      succeed // nur sicherstellen, dass kein Fehler geworfen wurde
    }

    "moveTrump: rotates first card to end and handles Nil" in {
      DurakApp.moveTrump(Nil) shouldBe Nil
      val lst = List(heartAce, spadeSix, diamondTen)
      val moved = DurakApp.moveTrump(lst)
      moved.last shouldBe heartAce
      moved.head shouldBe spadeSix
    }

    "findNextDefender: finds the next non-done player" in {
      val p1 = Player("A")
      val p2 = Player("B")
      val p3 = Player("C", isDone = true)
      val game = GameState(List(p1, p2, p3), Nil, Suit.Hearts)
      DurakApp.findNextDefender(game, 0) shouldBe 1
    }

    "createDeck: returns requested size and marks trump; works for small and large sizes" in {
      val (deck36, trump36) = DurakApp.createDeck(36)
      deck36.length shouldBe 36
      deck36.count(_.isTrump) should be > 0
      deck36.exists(_.suit == trump36) shouldBe true

      val (deck10, trump10) = DurakApp.createDeck(10)
      deck10.length shouldBe 10
      deck10.count(_.isTrump) should be >= 1
      deck10.exists(_.suit == trump10) shouldBe true
    }

    "initPlayerList: handles input, default names and small deck hand-size message branch" in {
      DummyIO.reset()
      // smaller deck to trigger actualHandSize != defaultHandSize branch
      val (smallDeck, _) = DurakApp.createDeck(4)
      // simulate: How many players? -> 3 (max(2) ensures >=2), then names (one empty to get default)
      DummyIO.enqueue("3", "", "Bob", "Charlie")
      val (players, remaining) =
        DurakApp.initPlayerList(smallDeck, 6)(using DummyIO)
      players.length shouldBe 3
      // first name empty -> default Player1
      players.head.name shouldBe "Player1"
      players.tail.map(_.name) should contain("Bob")
      remaining.length should be >= 0
    }

    "initPlayerList: uses defaultHandSize when deck is large enough" in {
      DummyIO.reset()
      val (largeDeck, _) = DurakApp.createDeck(20) // 20 cards
      DummyIO.enqueue("2", "Alice", "Bob") // 2 players
      val (players, _) = DurakApp.initPlayerList(largeDeck, 6)(using DummyIO) // defaultHandSize = 6
      players.head.hand.length shouldBe 6
    }

    "updateFinishedPlayers: marks players with empty hands as done and prints message" in {
      DummyIO.reset()
      val p1 = Player("A", Nil, isDone = false)
      val p2 = Player("B", List(spadeSix), isDone = false)
      val game = GameState(List(p1, p2), Nil, Suit.Hearts)
      val updated = DurakApp.updateFinishedPlayers(game)(using DummyIO)
      updated.playerList.head.isDone shouldBe true
      updated.playerList(1).isDone shouldBe false
    }

    "selectFirstAttacker: chooses (dealer+1) when no trumps, otherwise player with lowest trump rank" in {
      // no trumps
      val p1 = Player("A", List(heartAce.copy(isTrump = false)))
      val p2 = Player("B", List(spadeSix.copy(isTrump = false)))
      val gNoTrumps = GameState(List(p1, p2), Nil, Suit.Hearts)
      DurakApp.selectFirstAttacker(gNoTrumps, 0) shouldBe 1

      // with trumps: give player B a trump six and C a trump king -> lowest trump rank should win
      val pA = Player("A", List(heartAce.copy(isTrump = false)))
      val pB = Player("B", List(spadeSix.copy(isTrump = true)))
      val pC = Player("C", List(clubKing.copy(isTrump = true)))
      val gTrumps = GameState(List(pA, pB, pC), Nil, Suit.Spades)
      val idx = DurakApp.selectFirstAttacker(gTrumps, 0)
      idx should (be >= 0 and be < 3)

      // Test case for multiple players with same lowest trump rank
      val pSameTrump1 = Player("P1", List(Card(Suit.Spades, Rank.Six, isTrump = true)))
      val pSameTrump2 = Player("P2", List(Card(Suit.Clubs, Rank.Six, isTrump = true)))
      val pHigherTrump = Player("P3", List(Card(Suit.Diamonds, Rank.Seven, isTrump = true)))
      val gSameTrump = GameState(List(pSameTrump1, pSameTrump2, pHigherTrump), Nil, Suit.Spades)
      DurakApp.selectFirstAttacker(gSameTrump, 0) shouldBe 0 // P1 has lowest index with lowest trump
    }

    "checkLooser: true when <= 1 active player" in {
      val game1 = GameState(
        List(Player("A", Nil, true), Player("B", Nil, true)),
        Nil,
        Suit.Hearts
      )
      DurakApp.checkLooser(game1) shouldBe true

      val game2 = GameState(
        List(Player("A", List(heartAce)), Player("B", Nil, true)),
        Nil,
        Suit.Hearts
      )
      DurakApp.checkLooser(game2) shouldBe true

      val game3 = GameState(
        List(Player("A", List(heartAce)), Player("B", List(spadeSix))),
        Nil,
        Suit.Hearts
      )
      DurakApp.checkLooser(game3) shouldBe false
    }

    "handleEnd: runs both loser and tie branches without throwing" in {
      DummyIO.reset()
      val pLoser = Player("Loser", List(heartAce), isDone = false)
      val pDone = Player("Done", Nil, isDone = true)
      val gameLoser = GameState(List(pLoser, pDone), Nil, Suit.Hearts)
      noException shouldBe thrownBy(
        DurakApp.handleEnd(gameLoser)(using DummyIO)
      )

      val gTie = GameState(
        List(pDone.copy(isDone = true), Player("AlsoDone", Nil, true)),
        Nil,
        Suit.Hearts
      )
      noException shouldBe thrownBy(DurakApp.handleEnd(gTie)(using DummyIO))
    }

    "findNextActive: returns next active skipping done players" in {
      val p0 = Player("A", Nil, isDone = true)
      val p1 = Player("B", Nil, isDone = false)
      val p2 = Player("C", Nil, isDone = true)
      val g = GameState(List(p0, p1, p2), Nil, Suit.Hearts)
      DurakApp.findNextActive(g, 0) shouldBe 1
    }

    "findNextActive: returns next active when next player is not done" in {
      val p0 = Player("A", Nil, isDone = false)
      val p1 = Player("B", Nil, isDone = false)
      val g = GameState(List(p0, p1), Nil, Suit.Hearts)
      DurakApp.findNextActive(g, 0) shouldBe 1
    }

    "nextAttackerIndex: handles 1vs1 and >2 players and defenderTook flag" in {
      val p1 = Player("A", List(heartAce))
      val p2 = Player("B", List(spadeSix))
      val g21 = GameState(List(p1, p2), Nil, Suit.Hearts)
      DurakApp.nextAttackerIndex(
        g21,
        0,
        1,
        defenderTook = false
      ) should (be >= 0 and be < 2)

      val p3 = Player("C", List(diamondTen))
      val g3 = GameState(List(p1, p2, p3), Nil, Suit.Hearts)
      val idx = DurakApp.nextAttackerIndex(g3, 0, 1, defenderTook = true)
      idx should (be >= 0 and be < 3)
    }

    "nextAttackerIndex: handles >2 players and defenderTook = false" in {
      val p1 = Player("A", List(heartAce))
      val p2 = Player("B", List(spadeSix))
      val p3 = Player("C", List(diamondTen))
      val g3 = GameState(List(p1, p2, p3), Nil, Suit.Hearts)
      val idx = DurakApp.nextAttackerIndex(g3, 0, 1, defenderTook = false)
      idx shouldBe 1 // The next active player after the current attacker (P1) is P2 (index 1)
    }

    "canBeat: compares by suit and rank and trump rules" in {
      DurakApp.canBeat(
        Card(Suit.Clubs, Rank.Six),
        Card(Suit.Clubs, Rank.Ace),
        Suit.Hearts
      ) shouldBe true
      DurakApp.canBeat(
        heartAce,
        Card(Suit.Hearts, Rank.King),
        Suit.Hearts
      ) shouldBe false
      DurakApp.canBeat(
        Card(Suit.Clubs, Rank.King),
        Card(Suit.Hearts, Rank.Six, true),
        Suit.Hearts
      ) shouldBe true
      DurakApp.canBeat(
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
      DurakApp.tableCardsContainRank(g, heartAce) shouldBe true
      DurakApp.tableCardsContainRank(g, diamondTen) shouldBe false
    }

    "moveCard: moves card for valid index and returns unchanged for invalid index" in {
      DurakApp.moveCard(
        List(heartAce, spadeSix),
        List(diamondTen),
        -1
      ) shouldBe (List(heartAce, spadeSix), List(diamondTen))
      val (from, to) =
        DurakApp.moveCard(List(heartAce, spadeSix), List(diamondTen), 1)
      from shouldBe List(heartAce)
      to should contain(spadeSix)
    }

    "defend: when no attacking cards returns (game, false)" in {
      val g = GameState(List(Player("A")), Nil, Suit.Hearts)
      val (res, took) = DurakApp.defend(g, 0)(using DummyIO)
      res shouldBe g
      took shouldBe false
    }

    "defend: valid defend and then take branch (including invalid then take)" in {
      DummyIO.reset()
      val attackerCard = Card(Suit.Clubs, Rank.Six)
      val attacker = Player("Att", List(attackerCard))
      val defender = Player("Def", List(Card(Suit.Clubs, Rank.Ace)))
      val g1 = GameState(
        List(attacker, defender),
        Nil,
        Suit.Hearts,
        attackingCards = List(attackerCard)
      )
      DummyIO.enqueue("0")
      val (afterDef, took1) = DurakApp.defend(g1, 1)(using DummyIO)
      took1 shouldBe false
      afterDef.attackingCards shouldBe Nil
      afterDef.defendingCards shouldBe Nil
      afterDef.discardPile.nonEmpty shouldBe true

      DummyIO.reset()
      val defender2 = Player("Def2", List(Card(Suit.Hearts, Rank.Six)))
      val g2 = GameState(
        List(attacker, defender2),
        Nil,
        Suit.Hearts,
        attackingCards = List(attackerCard)
      )
      DummyIO.enqueue("0", "take")
      val (afterTake, took2) = DurakApp.defend(g2, 1)(using DummyIO)
      took2 shouldBe true
      afterTake.playerList(1).hand should contain(attackerCard)
    }

    "attack: invalid input, index out of range, allowed play then pass, and max 6 branch" in {
      DummyIO.reset()
      val p = Player("Att", List(heartAce))
      val g = GameState(List(p), Nil, Suit.Hearts)
      DummyIO.enqueue("x", "0", "pass")
      val finalG = DurakApp.attack(g, 0)(using DummyIO)
      finalG.playerList.head.hand.isEmpty shouldBe true

      val already6 = GameState(
        List(p),
        Nil,
        Suit.Hearts,
        attackingCards = List.fill(6)(spadeSix)
      )
      DummyIO.reset()
      DummyIO.enqueue("0", "pass")
      val after = DurakApp.attack(already6, 0)(using DummyIO)
      after shouldBe a[GameState]
    }

    "attack: negative index yields 'Index out of range' message" in {
      DummyIO.reset()
      val p = Player("Att", List(heartAce))
      val g = GameState(List(p), Nil, Suit.Hearts)
      DummyIO.enqueue("-1", "0", "pass") // -1 is invalid, then valid 0, then pass
      val finalG = DurakApp.attack(g, 0)(using DummyIO)
      finalG.playerList.head.hand.isEmpty shouldBe true
    }

    "draw: players draw up to 6 and deck is consumed; skip players with full hands" in {
      DummyIO.reset()
      val p1 = Player(
        "A",
        List(heartAce, spadeSix, diamondTen, clubKing, heartAce, spadeSix)
      )
      val p2 = Player("B", Nil)
      val deck = List(diamondTen, clubKing)
      val g = GameState(List(p1, p2), deck, Suit.Hearts)
      val after = DurakApp.draw(g, 0)(using DummyIO)
      after.playerList(0).hand.size shouldBe 6
      after.playerList(1).hand.size shouldBe 2
      after.deck.size should be >= 0
    }

    "draw: handles empty deck, players don't draw if deck is empty" in {
      DummyIO.reset()
      val p1 = Player("P1", Nil) // Hand size 0, needs 6 cards
      val emptyDeck = Nil
      val g = GameState(List(p1), emptyDeck, Suit.Hearts)
      val after = DurakApp.draw(g, 0)(using DummyIO)
      after.playerList.head.hand shouldBe empty
      after.deck shouldBe empty
    }

    "initPlayerList + init (safe path via initPlayerList) produce a GameState-like structure" in {
      DummyIO.reset()
      val (deckWithTrump, trump) = DurakApp.createDeck(12)
      DummyIO.enqueue("2", "Alice", "Bob")
      val (players, remaining) =
        DurakApp.initPlayerList(deckWithTrump, 6)(using DummyIO)
      players.length shouldBe 2
      players.map(_.name) should contain allElementsOf List("Alice", "Bob")
      val gs = GameState(playerList = players, deck = remaining, trump = trump)
      gs.playerList.length shouldBe 2
      gs.trump shouldBe trump
    }

    "gameLoop: handleEnd branch when everyone is done (avoids infinite loop)" in {
      DummyIO.reset()
      val pDone1 = Player("P1", Nil, isDone = true)
      val pDone2 = Player("P2", Nil, isDone = true)
      val finishedGame = GameState(List(pDone1, pDone2), Nil, Suit.Hearts)
      // Should call handleEnd and return without throwing
      noException shouldBe thrownBy(
        DurakApp.gameLoop(finishedGame, 0)(using DummyIO)
      )
    }

    "gameLoop: initial attacker is done, but other players are active" in {
      DummyIO.reset()
      val pDone = Player("P1", Nil, isDone = true)
      val pActive1 = Player("P2", List(heartAce), isDone = false)
      val pActive2 = Player("P3", List(spadeSix), isDone = false)
      val game = GameState(List(pDone, pActive1, pActive2), Nil, Suit.Hearts)
      // We need to provide enough input for the attack and defend phases to complete one loop
      DummyIO.enqueue("0", "0", "pass", "take") // P2 attacks with heartAce, P3 defends with spadeSix, P2 passes, P3 takes
      noException shouldBe thrownBy(
        DurakApp.gameLoop(game, 0)(using DummyIO)
      )
    }

          } // end should
        }
