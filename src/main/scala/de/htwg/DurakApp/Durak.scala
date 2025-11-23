package de.htwg.DurakApp

import aview.TUI
import controller.Controller
import model.{GameState, GameStatus, Suit}
import scala.util.Random

@main def run: Unit =

  val initialGameState = GameState(Nil, Nil, Suit.Clubs, status = GameStatus.WELCOME)
  val controller = new Controller(initialGameState)
  val tui = new TUI(controller)
  controller.add(tui)

  val deckSize = tui.askForDeckSize()
  val playerCount = tui.askForPlayerCount()
  val playerNames = tui.askForPlayerNames(playerCount)

  controller.setupGameAndStart(deckSize, playerNames, new Random, tui)

