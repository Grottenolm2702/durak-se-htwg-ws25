package de.htwg.DurakApp.util.impl

import de.htwg.DurakApp.model.*
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import de.htwg.DurakApp.util.FileIOInterface
import scala.util.{Try, Success, Failure}
import play.api.libs.json.*
import scala.io.Source
import java.io.{File, PrintWriter}
import com.google.inject.Inject

class FileIOJson @Inject() (filePath: String) extends FileIOInterface:

  import FileIOJson.given

  override def save(gameState: GameState): Try[Unit] = Try {
    val json = Json.toJson(gameState)
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
      json.validate[GameState] match
        case JsSuccess(gameState, _) => gameState
        case JsError(errors) =>
          throw new Exception(s"JSON parsing failed: $errors")
    } finally {
      source.close()
    }
  }

object FileIOJson:

  given suitFormat: Format[Suit] = new Format[Suit] {
    def reads(json: JsValue): JsResult[Suit] =
      json.validate[String].map(Suit.valueOf)
    def writes(suit: Suit): JsValue = JsString(suit.toString)
  }

  given rankFormat: Format[Rank] = new Format[Rank] {
    def reads(json: JsValue): JsResult[Rank] =
      json.validate[String].map(Rank.valueOf)
    def writes(rank: Rank): JsValue = JsString(rank.toString)
  }

  given cardFormat: Format[Card] = new Format[Card] {
    def reads(json: JsValue): JsResult[Card] = {
      for {
        suit <- (json \ "suit").validate[Suit]
        rank <- (json \ "rank").validate[Rank]
        isTrump <- (json \ "isTrump").validate[Boolean]
      } yield Card(suit, rank, isTrump)
    }
    def writes(card: Card): JsValue = {
      Json.obj(
        "suit" -> card.suit,
        "rank" -> card.rank,
        "isTrump" -> card.isTrump
      )
    }
  }

  given playerFormat: Format[Player] = new Format[Player] {
    def reads(json: JsValue): JsResult[Player] = {
      for {
        name <- (json \ "name").validate[String]
        hand <- (json \ "hand").validate[List[Card]]
        isDone <- (json \ "isDone").validate[Boolean]
      } yield Player(name, hand, isDone)
    }
    def writes(player: Player): JsValue = {
      Json.obj(
        "name" -> player.name,
        "hand" -> player.hand,
        "isDone" -> player.isDone
      )
    }
  }

  given gameEventFormat: Format[GameEvent] = new Format[GameEvent] {
    def reads(json: JsValue): JsResult[GameEvent] = {
      val eventType = (json \ "type").as[String]
      eventType match
        case "InvalidMove" => JsSuccess(GameEvent.InvalidMove)
        case "NotYourTurn" => JsSuccess(GameEvent.NotYourTurn)
        case "Attack" =>
          (json \ "card").validate[Card].map(GameEvent.Attack(_))
        case "Defend" =>
          (json \ "card").validate[Card].map(GameEvent.Defend(_))
        case "Pass" => JsSuccess(GameEvent.Pass)
        case "Take" => JsSuccess(GameEvent.Take)
        case "Draw" => JsSuccess(GameEvent.Draw)
        case "RoundEnd" =>
          (json \ "cleared").validate[Boolean].map(GameEvent.RoundEnd(_))
        case "GameOver" =>
          for {
            winner <- (json \ "winner").validate[Player]
            loser <- (json \ "loser").validateOpt[Player]
          } yield GameEvent.GameOver(winner, loser)
        case "CannotUndo"        => JsSuccess(GameEvent.CannotUndo)
        case "CannotRedo"        => JsSuccess(GameEvent.CannotRedo)
        case "AskPlayerCount"    => JsSuccess(GameEvent.AskPlayerCount)
        case "AskPlayerNames"    => JsSuccess(GameEvent.AskPlayerNames)
        case "AskDeckSize"       => JsSuccess(GameEvent.AskDeckSize)
        case "GameSetupComplete" => JsSuccess(GameEvent.GameSetupComplete)
        case "SetupError"        => JsSuccess(GameEvent.SetupError)
        case "AskPlayAgain"      => JsSuccess(GameEvent.AskPlayAgain)
        case "ExitApplication"   => JsSuccess(GameEvent.ExitApplication)
        case _                   => JsError(s"Unknown event type: $eventType")
    }

    def writes(event: GameEvent): JsValue = event match
      case GameEvent.InvalidMove => Json.obj("type" -> "InvalidMove")
      case GameEvent.NotYourTurn => Json.obj("type" -> "NotYourTurn")
      case GameEvent.Attack(card) =>
        Json.obj("type" -> "Attack", "card" -> card)
      case GameEvent.Defend(card) =>
        Json.obj("type" -> "Defend", "card" -> card)
      case GameEvent.Pass => Json.obj("type" -> "Pass")
      case GameEvent.Take => Json.obj("type" -> "Take")
      case GameEvent.Draw => Json.obj("type" -> "Draw")
      case GameEvent.RoundEnd(cleared) =>
        Json.obj("type" -> "RoundEnd", "cleared" -> cleared)
      case GameEvent.GameOver(winner, loser) =>
        Json.obj("type" -> "GameOver", "winner" -> winner, "loser" -> loser)
      case GameEvent.CannotUndo     => Json.obj("type" -> "CannotUndo")
      case GameEvent.CannotRedo     => Json.obj("type" -> "CannotRedo")
      case GameEvent.AskPlayerCount => Json.obj("type" -> "AskPlayerCount")
      case GameEvent.AskPlayerNames => Json.obj("type" -> "AskPlayerNames")
      case GameEvent.AskDeckSize    => Json.obj("type" -> "AskDeckSize")
      case GameEvent.GameSetupComplete =>
        Json.obj("type" -> "GameSetupComplete")
      case GameEvent.SetupError      => Json.obj("type" -> "SetupError")
      case GameEvent.AskPlayAgain    => Json.obj("type" -> "AskPlayAgain")
      case GameEvent.ExitApplication => Json.obj("type" -> "ExitApplication")
  }

  given gamePhaseFormat: Format[GamePhase] = new Format[GamePhase] {
    def reads(json: JsValue): JsResult[GamePhase] = {
      val phaseName = json.as[String]
      JsSuccess(new GamePhase {
        override def handle(gameState: GameState): GameState = gameState
      })
    }

    def writes(phase: GamePhase): JsValue = {
      val phaseName =
        phase.getClass.getSimpleName.replace("Impl", "").replace("$", "")
      JsString(phaseName)
    }
  }

  given tableFormat: Format[Map[Card, Option[Card]]] =
    new Format[Map[Card, Option[Card]]] {
      def reads(json: JsValue): JsResult[Map[Card, Option[Card]]] = {
        json.validate[Seq[JsObject]].flatMap { entries =>
          val pairs = entries.map { entry =>
            for {
              attackCard <- (entry \ "attackCard").validate[Card]
              defenseCard <- (entry \ "defenseCard").validateOpt[Card]
            } yield (attackCard, defenseCard)
          }
          JsSuccess(pairs.collect { case JsSuccess(pair, _) => pair }.toMap)
        }
      }

      def writes(table: Map[Card, Option[Card]]): JsValue = {
        val entries = table.map { case (attackCard, defenseCard) =>
          Json.obj(
            "attackCard" -> attackCard,
            "defenseCard" -> defenseCard
          )
        }
        JsArray(entries.toSeq)
      }
    }

  given gameStateFormat: Format[GameState] = new Format[GameState] {
    def reads(json: JsValue): JsResult[GameState] = {
      for {
        players <- (json \ "players").validate[List[Player]]
        mainAttackerIndex <- (json \ "mainAttackerIndex").validate[Int]
        defenderIndex <- (json \ "defenderIndex").validate[Int]
        currentAttackerIndex <- (json \ "currentAttackerIndex").validateOpt[Int]
        lastAttackerIndex <- (json \ "lastAttackerIndex").validateOpt[Int]
        passedPlayers <- (json \ "passedPlayers").validate[Set[Int]]
        roundWinner <- (json \ "roundWinner").validateOpt[Int]
        deck <- (json \ "deck").validate[List[Card]]
        table <- (json \ "table").validate[Map[Card, Option[Card]]]
        discardPile <- (json \ "discardPile").validate[List[Card]]
        trumpCard <- (json \ "trumpCard").validate[Card]
        gamePhase <- (json \ "gamePhase").validate[GamePhase]
        lastEvent <- (json \ "lastEvent").validateOpt[GameEvent]
        setupPlayerCount <- (json \ "setupPlayerCount").validateOpt[Int]
        setupPlayerNames <- (json \ "setupPlayerNames").validate[List[String]]
        setupDeckSize <- (json \ "setupDeckSize").validateOpt[Int]
      } yield GameState(
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
        setupDeckSize
      )
    }

    def writes(gs: GameState): JsValue = {
      Json.obj(
        "players" -> gs.players,
        "mainAttackerIndex" -> gs.mainAttackerIndex,
        "defenderIndex" -> gs.defenderIndex,
        "currentAttackerIndex" -> gs.currentAttackerIndex,
        "lastAttackerIndex" -> gs.lastAttackerIndex,
        "passedPlayers" -> gs.passedPlayers,
        "roundWinner" -> gs.roundWinner,
        "deck" -> gs.deck,
        "table" -> gs.table,
        "discardPile" -> gs.discardPile,
        "trumpCard" -> gs.trumpCard,
        "gamePhase" -> gs.gamePhase,
        "lastEvent" -> gs.lastEvent,
        "setupPlayerCount" -> gs.setupPlayerCount,
        "setupPlayerNames" -> gs.setupPlayerNames,
        "setupDeckSize" -> gs.setupDeckSize
      )
    }
  }
