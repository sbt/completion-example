package sbt.example

   import sbt._

   import complete._
   import DefaultParsers._
   import java.io.{File, PrintWriter}

final class Main extends xsbti.AppMain
{
   def run(configuration: xsbti.AppConfiguration): xsbti.MainResult =
      MainLoop.runLogged( initialState(configuration) )

   def initialState(configuration: xsbti.AppConfiguration): State =
   {
      val commandDefinitions = set +: BasicCommands.allBasicCommands
      val commandsToRun = "iflast shell" +: configuration.arguments.map(_.trim)
      State( configuration, commandDefinitions, Set.empty,
         None, commandsToRun, State.newHistory,
         AttributeMap.empty, initialGlobalLogging, State.Continue )
   }

	val setDescription = "Set the foreground or background color of the terminal."
	val setBrief = ("set (fg|bg <color>) | default", setDescription)
	val set = Command("set", setBrief, setDescription)(state => setParser) { (state, code) =>
		print(code)
		state
	}

	lazy val setParser: Parser[String] = 
		token(Space) ~> 
		(
			token(reset) |
			token(select <~ Space).flatMap( fg =>
				token(selectColor(fg))
			)
		)

	lazy val reset: Parser[String] = 
		"default" ^^^ (Console.RESET + "\n")

	lazy val select: Parser[Boolean] =
		("fg" ^^^ true) |
		("bg" ^^^ false)

	def selectColor(fg: Boolean): Parser[String] =
		colorParser( if(fg) foreground else background )

	lazy val letters: Parser[String] = Letter.+.string

	def colorParser(colorMap: Map[String,String]): Parser[String] =
		letters examples colorMap.keySet map(s => colorMap.getOrElse(s, "Invalid color '" + s + "'") )

	def foreground = Map(
		"black" -> Console.BLACK,
		"red" -> Console.RED,
		"blue" -> Console.BLUE,
		"green" -> Console.GREEN
	)
	def background = Map(
		"black" -> Console.BLACK_B,
		"red" -> Console.RED_B,
		"blue" -> Console.BLUE_B
	)

   def initialGlobalLogging: GlobalLogging =
      GlobalLogging.initial(MainLogging.globalDefault _, File.createTempFile("app", "log"))
}