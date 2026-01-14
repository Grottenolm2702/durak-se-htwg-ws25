package de.htwg.DurakApp.util.impl

import de.htwg.DurakApp.model.*
import de.htwg.DurakApp.model.state.{GameEvent, GamePhase}
import de.htwg.DurakApp.util.FileIOInterface
import scala.util.{Try, Success, Failure}
import scala.xml.{Node, Elem, PrettyPrinter}
import java.io.{File, PrintWriter}
import com.google.inject.Inject

class FileIOXml @Inject() (filePath: String) extends FileIOInterface:

  override def save(gameState: GameState): Try[Unit] = Try {
    val xml = gameStateToXml(gameState)
    val prettyPrinter = new PrettyPrinter(80, 2)
    val prettyXml = prettyPrinter.format(xml)

    val pw = new PrintWriter(new File(filePath))
    try {
      pw.write(prettyXml)
    } finally {
      pw.close()
    }
  }

  override def load(): Try[GameState] = Try {
    val xml = scala.xml.XML.loadFile(filePath)
    xmlToGameState(xml)
  }

  private def playerIndexToXml(idx: Int): Elem =
    <playerIndex>{idx}</playerIndex>

  private def nameToXml(name: String): Elem =
    <name>{name}</name>

  private def gameStateToXml(gs: GameState): Elem =
    val playersXml = gs.players.map(playerToXml)
    val passedPlayersXml = gs.passedPlayers.toList.map(playerIndexToXml)
    val deckXml = gs.deck.map(cardToXml)
    val tableXml = gs.table.toList.map { case (attackCard, defenseCard) =>
      <entry>
        <attackCard>{cardToXml(attackCard)}</attackCard>
        <defenseCard>{optionToXml(defenseCard, cardToXml)}</defenseCard>
      </entry>
    }
    val discardPileXml = gs.discardPile.map(cardToXml)
    val setupPlayerNamesXml = gs.setupPlayerNames.map(nameToXml)

    <gameState>
      <players>
        {playersXml}
      </players>
      <mainAttackerIndex>{gs.mainAttackerIndex}</mainAttackerIndex>
      <defenderIndex>{gs.defenderIndex}</defenderIndex>
      <currentAttackerIndex>{
      optionToXml(gs.currentAttackerIndex)
    }</currentAttackerIndex>
      <lastAttackerIndex>{optionToXml(gs.lastAttackerIndex)}</lastAttackerIndex>
      <passedPlayers>
        {passedPlayersXml}
      </passedPlayers>
      <roundWinner>{optionToXml(gs.roundWinner)}</roundWinner>
      <deck>
        {deckXml}
      </deck>
      <table>
        {tableXml}
      </table>
      <discardPile>
        {discardPileXml}
      </discardPile>
      <trumpCard>{cardToXml(gs.trumpCard)}</trumpCard>
      <gamePhase>{gamePhaseToString(gs.gamePhase)}</gamePhase>
      <lastEvent>{optionToXml(gs.lastEvent, gameEventToXml)}</lastEvent>
      <setupPlayerCount>{optionToXml(gs.setupPlayerCount)}</setupPlayerCount>
      <setupPlayerNames>
        {setupPlayerNamesXml}
      </setupPlayerNames>
      <setupDeckSize>{optionToXml(gs.setupDeckSize)}</setupDeckSize>
    </gameState>

  private def playerToXml(player: Player): Elem =
    <player>
      <name>{player.name}</name>
      <hand>
        {player.hand.map(cardToXml)}
      </hand>
      <isDone>{player.isDone}</isDone>
    </player>

  private def cardToXml(card: Card): Elem =
    <card>
      <suit>{card.suit.toString}</suit>
      <rank>{card.rank.toString}</rank>
      <isTrump>{card.isTrump}</isTrump>
    </card>

  private def gameEventToXml(event: GameEvent): Elem = event match
    case GameEvent.InvalidMove => <event>InvalidMove</event>
    case GameEvent.NotYourTurn => <event>NotYourTurn</event>
    case GameEvent.Attack(card) =>
      <event><type>Attack</type>{cardToXml(card)}</event>
    case GameEvent.Defend(card) =>
      <event><type>Defend</type>{cardToXml(card)}</event>
    case GameEvent.Pass => <event>Pass</event>
    case GameEvent.Take => <event>Take</event>
    case GameEvent.Draw => <event>Draw</event>
    case GameEvent.RoundEnd(cleared) =>
      <event><type>RoundEnd</type><cleared>{cleared}</cleared></event>
    case GameEvent.GameOver(winner, loser) =>
      <event>
        <type>GameOver</type>
        {playerToXml(winner)}
        <loser>{optionToXml(loser, playerToXml)}</loser>
      </event>
    case GameEvent.CannotUndo        => <event>CannotUndo</event>
    case GameEvent.CannotRedo        => <event>CannotRedo</event>
    case GameEvent.AskPlayerCount    => <event>AskPlayerCount</event>
    case GameEvent.AskPlayerNames    => <event>AskPlayerNames</event>
    case GameEvent.AskDeckSize       => <event>AskDeckSize</event>
    case GameEvent.GameSetupComplete => <event>GameSetupComplete</event>
    case GameEvent.SetupError        => <event>SetupError</event>
    case GameEvent.AskPlayAgain      => <event>AskPlayAgain</event>
    case GameEvent.ExitApplication   => <event>ExitApplication</event>

  private def gamePhaseToString(phase: GamePhase): String =
    phase.getClass.getSimpleName.replace("Impl", "").replace("$", "")

  private def optionToXml[T](opt: Option[T], converter: T => Elem): Elem =
    opt match
      case Some(value) => <some>{converter(value)}</some>
      case None        => <none/>

  private def optionToXml(opt: Option[Int]): String = opt match
    case Some(value) => value.toString
    case None        => ""

  private def nodeTextToInt(node: Node): Int = node.text.toInt

  private def nodeText(node: Node): String = node.text

  private def xmlToGameState(node: Node): GameState =
    val players = (node \ "players" \ "player").map(xmlToPlayer).toList
    val mainAttackerIndex = (node \ "mainAttackerIndex").text.toInt
    val defenderIndex = (node \ "defenderIndex").text.toInt
    val currentAttackerIndex = xmlToOptionInt(
      (node \ "currentAttackerIndex").text
    )
    val lastAttackerIndex = xmlToOptionInt((node \ "lastAttackerIndex").text)

    val passedPlayersNodes = (node \ "passedPlayers" \ "playerIndex")
    val passedPlayers = passedPlayersNodes.map(nodeTextToInt).toSet

    val roundWinner = xmlToOptionInt((node \ "roundWinner").text)
    val deck = (node \ "deck" \ "card").map(xmlToCard).toList

    val tableEntries = (node \ "table" \ "entry")
    val table = tableEntries.map { entry =>
      val attackCard = xmlToCard((entry \ "attackCard" \ "card").head)
      val defenseCard = xmlToOptionCard((entry \ "defenseCard").head)
      (attackCard, defenseCard)
    }.toMap

    val discardPileNodes = (node \ "discardPile" \ "card")
    val discardPile = discardPileNodes.map(xmlToCard).toList
    val trumpCard = xmlToCard((node \ "trumpCard" \ "card").head)
    val gamePhase = stringToGamePhase((node \ "gamePhase").text)
    val lastEvent = xmlToOptionGameEvent((node \ "lastEvent").head)
    val setupPlayerCount = xmlToOptionInt((node \ "setupPlayerCount").text)
    val setupPlayerNamesNodes = (node \ "setupPlayerNames" \ "name")
    val setupPlayerNames = setupPlayerNamesNodes.map(nodeText).toList
    val setupDeckSize = xmlToOptionInt((node \ "setupDeckSize").text)

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
      setupDeckSize
    )

  private def xmlToPlayer(node: Node): Player =
    Player(
      (node \ "name").text,
      (node \ "hand" \ "card").map(xmlToCard).toList,
      (node \ "isDone").text.toBoolean
    )

  private def xmlToCard(node: Node): Card =
    Card(
      Suit.valueOf((node \ "suit").text),
      Rank.valueOf((node \ "rank").text),
      (node \ "isTrump").text.toBoolean
    )

  private def xmlToOptionInt(text: String): Option[Int] =
    if text.isEmpty then None else Some(text.toInt)

  private def xmlToOptionCard(node: Node): Option[Card] =
    if (node \ "none").nonEmpty then None
    else Some(xmlToCard((node \ "some" \ "card").head))

  private def xmlToOptionGameEvent(node: Node): Option[GameEvent] =
    if (node \ "none").nonEmpty then None
    else Some(xmlToGameEvent((node \ "some" \ "event").head))

  private def xmlToGameEvent(node: Node): GameEvent =
    val eventType = (node \ "type").text
    if eventType.isEmpty then
      node.text match
        case "InvalidMove"       => GameEvent.InvalidMove
        case "NotYourTurn"       => GameEvent.NotYourTurn
        case "Pass"              => GameEvent.Pass
        case "Take"              => GameEvent.Take
        case "Draw"              => GameEvent.Draw
        case "CannotUndo"        => GameEvent.CannotUndo
        case "CannotRedo"        => GameEvent.CannotRedo
        case "AskPlayerCount"    => GameEvent.AskPlayerCount
        case "AskPlayerNames"    => GameEvent.AskPlayerNames
        case "AskDeckSize"       => GameEvent.AskDeckSize
        case "GameSetupComplete" => GameEvent.GameSetupComplete
        case "SetupError"        => GameEvent.SetupError
        case "AskPlayAgain"      => GameEvent.AskPlayAgain
        case "ExitApplication"   => GameEvent.ExitApplication
    else
      eventType match
        case "Attack"   => GameEvent.Attack(xmlToCard((node \ "card").head))
        case "Defend"   => GameEvent.Defend(xmlToCard((node \ "card").head))
        case "RoundEnd" => GameEvent.RoundEnd((node \ "cleared").text.toBoolean)
        case "GameOver" =>
          val winner = xmlToPlayer((node \ "player").head)
          val loser = xmlToOptionPlayer((node \ "loser").head)
          GameEvent.GameOver(winner, loser)

  private def xmlToOptionPlayer(node: Node): Option[Player] =
    if (node \ "none").nonEmpty then None
    else Some(xmlToPlayer((node \ "some" \ "player").head))

  private def stringToGamePhase(phaseName: String): GamePhase =
    new GamePhase {
      override def handle(gameState: GameState): GameState = gameState
    }
