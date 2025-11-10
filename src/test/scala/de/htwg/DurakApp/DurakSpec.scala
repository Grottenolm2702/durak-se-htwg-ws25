package de.htwg.DurakApp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.{ByteArrayInputStream, InputStream}
import scala.util.Random // Added import
import de.htwg.DurakApp.model.*
import de.htwg.DurakApp.controller.Controller

class DurakSpec extends AnyWordSpec with Matchers {

  val controller = Controller()

  val FixedRandom: Random = new Random(0) // Use a fixed seed for deterministic behavior

  val heartAce = Card(Suit.Hearts, Rank.Ace, isTrump = true)
  val spadeSix = Card(Suit.Spades, Rank.Six, isTrump = false)
  val diamondTen = Card(Suit.Diamonds, Rank.Ten, isTrump = false)
  val clubKing = Card(Suit.Clubs, Rank.King, isTrump = false)
  val clubsAce = Card(Suit.Clubs, Rank.Ace, isTrump = false)

  "DurakApp" should {

    "safeToInt: convert string to int safely" in {
      controller.safeToInt("42") shouldBe Some(42)
      controller.safeToInt("abc") shouldBe None
      controller.safeToInt(" 7 ") shouldBe Some(7)
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
  } // end should
}
