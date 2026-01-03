package de.htwg.DurakApp.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.DurakApp.model.impl.{CardFactoryImpl, PlayerFactoryImpl, GameStateFactoryImpl}
import de.htwg.DurakApp.model.state.SetupPhase

class GameStateFactorySpec extends AnyWordSpec with Matchers {
  
  "GameStateFactory" should {
    val cardFactory: CardFactory = new CardFactoryImpl()
    val playerFactory: PlayerFactory = new PlayerFactoryImpl()
    val gameStateFactory: GameStateFactory = new GameStateFactoryImpl()
    
    "create a game state with all fields" in {
      val player1 = playerFactory("Alice")
      val player2 = playerFactory("Bob")
      val players = List(player1, player2)
      val trumpCard = cardFactory(Suit.Hearts, Rank.Six, isTrump = true)
      
      val gameState = gameStateFactory(
        players = players,
        deck = List.empty,
        table = Map.empty,
        discardPile = List.empty,
        trumpCard = trumpCard,
        attackerIndex = 0,
        defenderIndex = 1,
        gamePhase = SetupPhase,
        lastEvent = None,
        passedPlayers = Set.empty,
        roundWinner = None,
        setupPlayerCount = Some(2),
        setupPlayerNames = List("Alice", "Bob"),
        setupDeckSize = Some(36),
        currentAttackerIndex = None,
        lastAttackerIndex = None
      )
      
      gameState.players shouldBe players
      gameState.attackerIndex shouldBe 0
      gameState.defenderIndex shouldBe 1
      gameState.trumpCard shouldBe trumpCard
    }
  }
}
