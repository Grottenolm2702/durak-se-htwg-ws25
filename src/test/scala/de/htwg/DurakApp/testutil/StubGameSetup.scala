package de.htwg.DurakApp.testutil

import de.htwg.DurakApp.controller.GameSetup
import de.htwg.DurakApp.model.{GameState, Suit, Rank}
import de.htwg.DurakApp.model.state.GameEvent

class StubGameSetup(
    cardFactory: de.htwg.DurakApp.model.CardFactory,
    playerFactory: de.htwg.DurakApp.model.PlayerFactory,
    gameStateFactory: de.htwg.DurakApp.model.GameStateFactory
) extends GameSetup:

  def setupGame(playerNames: List[String], deckSize: Int): Option[GameState] =
    if playerNames.size < 2 || playerNames.size > 6 || deckSize < 2 || deckSize > 36
    then None
    else
      val players = playerNames.map(name => playerFactory(name, List.empty))
      val trumpCard = cardFactory(Suit.Hearts, Rank.Six, isTrump = true)
      Some(
        gameStateFactory(
          players = players,
          deck = List.empty,
          table = Map.empty,
          discardPile = List.empty,
          trumpCard = trumpCard,
          attackerIndex = 0,
          defenderIndex = 1,
          gamePhase = StubGamePhases.setupPhase,
          lastEvent = Some(GameEvent.GameSetupComplete),
          passedPlayers = Set.empty,
          roundWinner = None,
          setupPlayerCount = Some(playerNames.size),
          setupPlayerNames = playerNames,
          setupDeckSize = Some(deckSize),
          currentAttackerIndex = None,
          lastAttackerIndex = None
        )
      )
