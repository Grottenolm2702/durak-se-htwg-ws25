package de.htwg.DurakApp.controller

import de.htwg.DurakApp.model.GameState

/** Trait for game setup operations.
  *
  * Handles the initialization of a new game with the given player names and
  * deck size. This abstraction allows for different setup implementations and
  * facilitates dependency injection and testing.
  */
trait GameSetup:
  /** Sets up a new game with the specified players and deck size.
    *
    * @param playerNames
    *   List of player names for the game
    * @param deckSize
    *   Number of cards in the deck
    * @return
    *   Some(GameState) if setup was successful, None if parameters are invalid
    */
  def setupGame(playerNames: List[String], deckSize: Int): Option[GameState]
