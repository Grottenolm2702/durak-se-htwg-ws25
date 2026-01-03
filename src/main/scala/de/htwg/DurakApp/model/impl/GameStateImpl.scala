package de.htwg.DurakApp.model.impl

import de.htwg.DurakApp.model.{GameState, Player, Card, GameStateFactory, CardFactory, Suit, Rank}
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import de.htwg.DurakApp.model.builder.GameStateBuilder

private[model] case class GameStateImpl(
    players: List[Player],
    deck: List[Card],
    table: Map[Card, Option[Card]],
    discardPile: List[Card],
    trumpCard: Card,
    attackerIndex: Int,
    defenderIndex: Int,
    gamePhase: GamePhase,
    lastEvent: Option[GameEvent],
    passedPlayers: Set[Int],
    roundWinner: Option[Int],
    setupPlayerCount: Option[Int],
    setupPlayerNames: List[String],
    setupDeckSize: Option[Int],
    currentAttackerIndex: Option[Int],
    lastAttackerIndex: Option[Int]
) extends GameState {

  override def copy(
      players: List[Player] = this.players,
      deck: List[Card] = this.deck,
      table: Map[Card, Option[Card]] = this.table,
      discardPile: List[Card] = this.discardPile,
      trumpCard: Card = this.trumpCard,
      attackerIndex: Int = this.attackerIndex,
      defenderIndex: Int = this.defenderIndex,
      gamePhase: GamePhase = this.gamePhase,
      lastEvent: Option[GameEvent] = this.lastEvent,
      passedPlayers: Set[Int] = this.passedPlayers,
      roundWinner: Option[Int] = this.roundWinner,
      setupPlayerCount: Option[Int] = this.setupPlayerCount,
      setupPlayerNames: List[String] = this.setupPlayerNames,
      setupDeckSize: Option[Int] = this.setupDeckSize,
      currentAttackerIndex: Option[Int] = this.currentAttackerIndex,
      lastAttackerIndex: Option[Int] = this.lastAttackerIndex
  ): GameState = GameStateImpl(
    players,
    deck,
    table,
    discardPile,
    trumpCard,
    attackerIndex,
    defenderIndex,
    gamePhase,
    lastEvent,
    passedPlayers,
    roundWinner,
    setupPlayerCount,
    setupPlayerNames,
    setupDeckSize,
    currentAttackerIndex,
    lastAttackerIndex
  )

  def toBuilder: GameStateBuilder = {
    // Use inline factories that creates GameStateImpl and CardImpl directly
    val inlineGameStateFactory = new GameStateFactory {
      def apply(
          players: List[Player],
          deck: List[Card],
          table: Map[Card, Option[Card]],
          discardPile: List[Card],
          trumpCard: Card,
          attackerIndex: Int,
          defenderIndex: Int,
          gamePhase: GamePhase,
          lastEvent: Option[GameEvent],
          passedPlayers: Set[Int],
          roundWinner: Option[Int],
          setupPlayerCount: Option[Int],
          setupPlayerNames: List[String],
          setupDeckSize: Option[Int],
          currentAttackerIndex: Option[Int],
          lastAttackerIndex: Option[Int]
      ): GameState = GameStateImpl(
        players,
        deck,
        table,
        discardPile,
        trumpCard,
        attackerIndex,
        defenderIndex,
        gamePhase,
        lastEvent,
        passedPlayers,
        roundWinner,
        setupPlayerCount,
        setupPlayerNames,
        setupDeckSize,
        currentAttackerIndex,
        lastAttackerIndex
      )
    }
    
    val inlineCardFactory = new CardFactory {
      def apply(suit: Suit, rank: Rank, isTrump: Boolean): Card =
        CardImpl(suit, rank, isTrump)
    }
    
    de.htwg.DurakApp.model.builder.impl.GameStateBuilder(inlineGameStateFactory, inlineCardFactory)
      .withPlayers(players)
      .withDeck(deck)
      .withTable(table)
      .withDiscardPile(discardPile)
      .withTrumpCard(trumpCard)
      .withAttackerIndex(attackerIndex)
      .withDefenderIndex(defenderIndex)
      .withGamePhase(gamePhase)
      .withLastEvent(lastEvent)
      .withPassedPlayers(passedPlayers)
      .withRoundWinner(roundWinner)
      .withSetupPlayerCount(setupPlayerCount)
      .withSetupPlayerNames(setupPlayerNames)
      .withSetupDeckSize(setupDeckSize)
      .withCurrentAttackerIndex(currentAttackerIndex)
      .withLastAttackerIndex(lastAttackerIndex)
  }
}
