error id: file://<WORKSPACE>/src/main/scala/tui.worksheet.sc:
file://<WORKSPACE>/src/main/scala/tui.worksheet.sc
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -p.
	 -p#
	 -p().
	 -scala/Predef.p.
	 -scala/Predef.p#
	 -scala/Predef.p().
offset: 233
uri: file://<WORKSPACE>/src/main/scala/tui.worksheet.sc
text:
```scala
def renderCard(card: Card): String =
  s"${card.rank} of ${card.suit}"

def renderHand(hand: List[Card]): String =
  hand.map(renderCard).mkString(", ")

def renderGame(game: GameState): String =
  val playerStrs = game.players.map(p@@ => s"${p.name}: ${renderHand(p.hand)}")
  val tableStr = if game.table.isEmpty then "Table is empty" else renderHand(game.table)
  (playerStrs :+ s"Table: $tableStr").mkString("\n")

  val deck = for
  suit <- Suit.values
  rank <- Rank.values
yield Card(suit, rank)

val players = List(Player("Alice"), Player("Bob"))
val game = GameState(players, deck.toList)

println(renderGame(game))

```


#### Short summary: 

empty definition using pc, found symbol in pc: 