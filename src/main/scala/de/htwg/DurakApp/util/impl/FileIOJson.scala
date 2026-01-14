package de.htwg.DurakApp.util.impl

import de.htwg.DurakApp.model.*
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase, GamePhases}
import de.htwg.DurakApp.util.FileIOInterface
import scala.util.{Try, Success, Failure}
import play.api.libs.json.*
import scala.io.Source
import java.io.{File, PrintWriter}
import com.google.inject.Inject

class FileIOJson @Inject() (filePath: String, gamePhases: GamePhases)
    extends FileIOInterface:

  override def save(gameState: GameState): Try[Unit] = Try {
    val json = gameStateToJson(gameState)
    val prettyJson = Json.prettyPrint(json)

    val pw = new PrintWriter(new File(filePath))
    try {
      pw.write(prettyJson)
    } finally {
      pw.close()
    }
  }

  override def load(): Try[GameState] = Try {
    val source = Source.fromFile(filePath)
    try {
      val content = source.getLines.mkString
      val json = Json.parse(content)
      jsonToGameState(json)
    } finally {
      source.close()
    }
  }

  private def gameStateToJson(gs: GameState): JsValue = {
    Json.obj(
      "players" -> JsArray(gs.players.map(playerToJson)),
      "mainAttackerIndex" -> gs.mainAttackerIndex,
      "defenderIndex" -> gs.defenderIndex,
      "currentAttackerIndex" -> optionToJson(gs.currentAttackerIndex),
      "lastAttackerIndex" -> optionToJson(gs.lastAttackerIndex),
      "passedPlayers" -> JsArray(gs.passedPlayers.map(JsNumber(_)).toSeq),
      "roundWinner" -> optionToJson(gs.roundWinner),
      "deck" -> JsArray(gs.deck.map(cardToJson)),
      "table" -> tableToJson(gs.table),
      "discardPile" -> JsArray(gs.discardPile.map(cardToJson)),
      "trumpCard" -> cardToJson(gs.trumpCard),
      "gamePhase" -> JsString(
        gs.gamePhase.getClass.getSimpleName.replace("Impl", "").replace("$", "")
      ),
      "lastEvent" -> optionEventToJson(gs.lastEvent),
      "setupPlayerCount" -> optionToJson(gs.setupPlayerCount),
      "setupPlayerNames" -> JsArray(gs.setupPlayerNames.map(JsString(_))),
      "setupDeckSize" -> optionToJson(gs.setupDeckSize),
      "undoStack" -> JsArray(gs.undoStack.map(gameStateToJson)),
      "redoStack" -> JsArray(gs.redoStack.map(gameStateToJson))
    )
  }

  private def jsonToGameState(json: JsValue): GameState = {
    val players = (json \ "players").as[JsArray].value.map(jsonToPlayer).toList
    val mainAttackerIndex = (json \ "mainAttackerIndex").as[Int]
    val defenderIndex = (json \ "defenderIndex").as[Int]
    val currentAttackerIndex = jsonToOptionInt(json \ "currentAttackerIndex")
    val lastAttackerIndex = jsonToOptionInt(json \ "lastAttackerIndex")
    val passedPlayers =
      (json \ "passedPlayers").as[JsArray].value.map(_.as[Int]).toSet
    val roundWinner = jsonToOptionInt(json \ "roundWinner")
    val deck = (json \ "deck").as[JsArray].value.map(jsonToCard).toList
    val table = jsonToTable(json \ "table")
    val discardPile =
      (json \ "discardPile").as[JsArray].value.map(jsonToCard).toList
    val trumpCard = jsonToCard((json \ "trumpCard").get)
    val gamePhase = stringToGamePhase((json \ "gamePhase").as[String])
    val lastEvent = jsonToOptionEvent(json \ "lastEvent")
    val setupPlayerCount = jsonToOptionInt(json \ "setupPlayerCount")
    val setupPlayerNames =
      (json \ "setupPlayerNames").as[JsArray].value.map(_.as[String]).toList
    val setupDeckSize = jsonToOptionInt(json \ "setupDeckSize")

    val undoStack = (json \ "undoStack").asOpt[JsArray] match {
      case Some(arr) => arr.value.map(jsonToGameState).toList
      case None      => List.empty
    }
    val redoStack = (json \ "redoStack").asOpt[JsArray] match {
      case Some(arr) => arr.value.map(jsonToGameState).toList
      case None      => List.empty
    }

    GameState(
      players,
      mainAttackerIndex,
      defenderIndex,
      currentAttackerIndex,
      lastAttackerIndex,
      passedPlayers,
      roundWinner,
      deck,
      table,
      discardPile,
      trumpCard,
      gamePhase,
      lastEvent,
      setupPlayerCount,
      setupPlayerNames,
      setupDeckSize,
      undoStack,
      redoStack
    )
  }

  private def stringToGamePhase(phaseName: String): GamePhase = {
    phaseName match {
      case "SetupPhase" | "SetupPhaseImpl" => gamePhases.setupPhase
      case "AskPlayerCountPhase" | "AskPlayerCountPhaseImpl" =>
        gamePhases.askPlayerCountPhase
      case "AskPlayerNamesPhase" | "AskPlayerNamesPhaseImpl" =>
        gamePhases.askPlayerNamesPhase
      case "AskDeckSizePhase" | "AskDeckSizePhaseImpl" =>
        gamePhases.askDeckSizePhase
      case "GameStartPhase" | "GameStartPhaseImpl" => gamePhases.gameStartPhase
      case "AttackPhase" | "AttackPhaseImpl"       => gamePhases.attackPhase
      case "DefensePhase" | "DefensePhaseImpl"     => gamePhases.defensePhase
      case "DrawPhase" | "DrawPhaseImpl"           => gamePhases.drawPhase
      case "RoundPhase" | "RoundPhaseImpl"         => gamePhases.roundPhase
      case "EndPhase" | "EndPhaseImpl"             => gamePhases.endPhase
      case "AskPlayAgainPhase" | "AskPlayAgainPhaseImpl" =>
        gamePhases.askPlayAgainPhase
      case _ => gamePhases.setupPhase
    }
  }

  private def playerToJson(player: Player): JsValue = {
    Json.obj(
      "name" -> player.name,
      "hand" -> JsArray(player.hand.map(cardToJson)),
      "isDone" -> player.isDone
    )
  }

  private def jsonToPlayer(json: JsValue): Player = {
    Player(
      (json \ "name").as[String],
      (json \ "hand").as[JsArray].value.map(jsonToCard).toList,
      (json \ "isDone").as[Boolean]
    )
  }

  private def cardToJson(card: Card): JsValue = {
    Json.obj(
      "suit" -> card.suit.toString,
      "rank" -> card.rank.toString,
      "isTrump" -> card.isTrump
    )
  }

  private def jsonToCard(json: JsValue): Card = {
    Card(
      Suit.valueOf((json \ "suit").as[String]),
      Rank.valueOf((json \ "rank").as[String]),
      (json \ "isTrump").as[Boolean]
    )
  }

  private def tableToJson(table: Map[Card, Option[Card]]): JsValue = {
    val entries = table.map { case (attackCard, defenseCard) =>
      Json.obj(
        "attackCard" -> cardToJson(attackCard),
        "defenseCard" -> (defenseCard match {
          case Some(card) => cardToJson(card)
          case None       => JsNull
        })
      )
    }
    JsArray(entries.toSeq)
  }

  private def jsonToTable(lookup: JsLookupResult): Map[Card, Option[Card]] = {
    lookup
      .as[JsArray]
      .value
      .map { entry =>
        val attackCard = jsonToCard((entry \ "attackCard").get)
        val defenseCard = (entry \ "defenseCard").asOpt[JsValue] match {
          case Some(JsNull) | None => None
          case Some(cardJson)      => Some(jsonToCard(cardJson))
        }
        (attackCard, defenseCard)
      }
      .toMap
  }

  private def optionToJson(opt: Option[Int]): JsValue = {
    opt match {
      case Some(value) => JsNumber(value)
      case None        => JsNull
    }
  }

  private def jsonToOptionInt(lookup: JsLookupResult): Option[Int] = {
    lookup.asOpt[Int]
  }

  private def optionEventToJson(opt: Option[GameEvent]): JsValue = {
    opt match {
      case Some(event) => eventToJson(event)
      case None        => JsNull
    }
  }

  private def jsonToOptionEvent(lookup: JsLookupResult): Option[GameEvent] = {
    lookup.asOpt[JsObject].map(jsonToEvent)
  }

  private def eventToJson(event: GameEvent): JsValue = event match {
    case GameEvent.InvalidMove => Json.obj("type" -> "InvalidMove")
    case GameEvent.NotYourTurn => Json.obj("type" -> "NotYourTurn")
    case GameEvent.Attack(card) =>
      Json.obj("type" -> "Attack", "card" -> cardToJson(card))
    case GameEvent.Defend(card) =>
      Json.obj("type" -> "Defend", "card" -> cardToJson(card))
    case GameEvent.Pass => Json.obj("type" -> "Pass")
    case GameEvent.Take => Json.obj("type" -> "Take")
    case GameEvent.Draw => Json.obj("type" -> "Draw")
    case GameEvent.RoundEnd(cleared) =>
      Json.obj("type" -> "RoundEnd", "cleared" -> cleared)
    case GameEvent.GameOver(winner, loser) =>
      Json.obj(
        "type" -> "GameOver",
        "winner" -> playerToJson(winner),
        "loser" -> (loser match {
          case Some(p) => playerToJson(p)
          case None    => JsNull
        })
      )
    case GameEvent.CannotUndo        => Json.obj("type" -> "CannotUndo")
    case GameEvent.CannotRedo        => Json.obj("type" -> "CannotRedo")
    case GameEvent.AskPlayerCount    => Json.obj("type" -> "AskPlayerCount")
    case GameEvent.AskPlayerNames    => Json.obj("type" -> "AskPlayerNames")
    case GameEvent.AskDeckSize       => Json.obj("type" -> "AskDeckSize")
    case GameEvent.GameSetupComplete => Json.obj("type" -> "GameSetupComplete")
    case GameEvent.SetupError        => Json.obj("type" -> "SetupError")
    case GameEvent.AskPlayAgain      => Json.obj("type" -> "AskPlayAgain")
    case GameEvent.ExitApplication   => Json.obj("type" -> "ExitApplication")
    case GameEvent.GameSaved         => Json.obj("type" -> "GameSaved")
    case GameEvent.GameLoaded        => Json.obj("type" -> "GameLoaded")
    case GameEvent.SaveError         => Json.obj("type" -> "SaveError")
    case GameEvent.LoadError         => Json.obj("type" -> "LoadError")
  }

  private def jsonToEvent(json: JsValue): GameEvent = {
    val eventType = (json \ "type").as[String]
    eventType match {
      case "InvalidMove" => GameEvent.InvalidMove
      case "NotYourTurn" => GameEvent.NotYourTurn
      case "Attack"      => GameEvent.Attack(jsonToCard((json \ "card").get))
      case "Defend"      => GameEvent.Defend(jsonToCard((json \ "card").get))
      case "Pass"        => GameEvent.Pass
      case "Take"        => GameEvent.Take
      case "Draw"        => GameEvent.Draw
      case "RoundEnd"    => GameEvent.RoundEnd((json \ "cleared").as[Boolean])
      case "GameOver" =>
        val winner = jsonToPlayer((json \ "winner").get)
        val loser = (json \ "loser").asOpt[JsObject].map(jsonToPlayer)
        GameEvent.GameOver(winner, loser)
      case "CannotUndo"        => GameEvent.CannotUndo
      case "CannotRedo"        => GameEvent.CannotRedo
      case "AskPlayerCount"    => GameEvent.AskPlayerCount
      case "AskPlayerNames"    => GameEvent.AskPlayerNames
      case "AskDeckSize"       => GameEvent.AskDeckSize
      case "GameSetupComplete" => GameEvent.GameSetupComplete
      case "SetupError"        => GameEvent.SetupError
      case "AskPlayAgain"      => GameEvent.AskPlayAgain
      case "ExitApplication"   => GameEvent.ExitApplication
      case "GameSaved"         => GameEvent.GameSaved
      case "GameLoaded"        => GameEvent.GameLoaded
      case "SaveError"         => GameEvent.SaveError
      case "LoadError"         => GameEvent.LoadError
      case _ => throw new Exception(s"Unknown event type: $eventType")
    }
  }
