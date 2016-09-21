package BattleShip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Client
{
	final String NEWL = System.getProperty("line.separator");

	private String name = "Player";
	PrintWriter out = null;
	BufferedReader in = null;
	GameManager man = null;
	GameBoard board = new GameBoard(10,10);
	GameBoard targets = new GameBoard(10,10);

	Client( BufferedReader in, PrintWriter out, GameManager manager )
	{
		this.in = in;
		this.out = out;
		this.man = manager;
	}

	public void playGame() throws IOException
	{
		this.out.println( NEWL + NEWL + "   Missiles Away! Game has begun" );
		this.out.println( "   To Launch a missle at your enemy:" );
		this.out.println( "F 2 4" );
		this.out.println( "Fires a missile at coordinate x=2, y=4." );

		while(true) // put Code Here to process in game commands, after each command, print the target board and game board w/ updated state )
		{
			if(!processCommand()){
				out.println("Command not recognized");
			}
			out.println( "------------------------" );
			drawBoard();
			out.println( "   Waiting for Next Command...\n\n" );
			out.flush();

			if(allMyShipsAreDestroyed()){
				out.println("You lost");
				break;
			}
			if(allEnemyShipsAreDestroyed()){
				out.println("You Win");
				processChatCmd("You lose");
				break;
			}
			//Perform test here to see if we have won or lost
		}
	}

	//Returns a bool, true iff all of this client's ships are destroyed
	boolean allMyShipsAreDestroyed()
	{
		ArrayList<Ship> myShips = board.myShips;

		for(Ship s : myShips){
			if(s.isAlive()){
				return false;
			}
		}
		return true;
	}

	void drawBoard(){
		out.println( "Target Board:" + this.targets.draw() );
		out.println( "Your Ships: " + this.board.draw() );
	}
	//Returns a bool, true iff all of the opponent's ships are destroyed
	boolean allEnemyShipsAreDestroyed()
	{
		ArrayList<Ship> theirShips = man.getOpponent(this).board.myShips;

		for(Ship s : theirShips){
			if(s.isAlive()){
				return false;
			}
		}
		return true;
	}

	//"F 2 4" = Fire command
	//"C Hello world, i am a chat message"
	//"D" - Redraw the latest game and target boards
	boolean processCommand() throws IOException
	{
		String s = in.readLine();
		String[] line = s.split(" ");
		if(line[0].equals("F")){

			if(!processFireCmd(line)){
				out.println("Fire command not in correct format");
				return false;
			}else{
				return true;
			}
		}else if(line[0].equals("C")){

			if(!processChatCmd(s.substring(2))){
				out.println("Chat command not in correct format");
				return false;
			}else {
				return true;
			}
		}else if(line[0].equals("D")){
			return true;
			//TODO: This part
		}else{
			return false;
		}

	}

	//When a fire command is typed, this method parses the coordinates and launches a missle at the enemy
	boolean processFireCmd( String [] s )
	{
		Position p = new Position(Integer.parseInt(s[1]),Integer.parseInt(s[2]));
		Ship victimShip = null;
		try{
			victimShip = man.getOpponent(this).board.fireMissle(p);
		}catch(Exception e){
			return false;
		}
		
		if(victimShip != null){
			out.println(victimShip.name);
			this.targets.cells.get(Integer.parseInt(s[1])).get(Integer.parseInt(s[2])).setShip(victimShip);
			this.targets.cells.get(Integer.parseInt(s[1])).get(Integer.parseInt(s[2])).hasBeenStruckByMissile(true);
		}else{
			this.targets.cells.get(Integer.parseInt(s[1])).get(Integer.parseInt(s[2])).hasBeenStruckByMissile(true);
		}
		return true;

	}

	//Send a message to the opponent
	boolean processChatCmd( String s )
	{
		Client opponent = man.getOpponent(this);
		try{
			opponent.out.println(s);
		}catch(Exception e){
			return false;
		}
		return true;
	}

	GameBoard getGameBoard() { return this.board; }

	//TODO: THIS
	public void initPlayer() throws IOException
	{
		//1.Get player name
		out.println("Enter your name now");
		out.flush();
		this.name = in.readLine();
		//2.Print out instructions

		//Here's some nice instructions to show a client		
		out.println("   You will now place 2 ships. You may choose between either a Cruiser (C) " );
		out.println("   and Destroyer (D)...");
		out.println("   Enter Ship info. An example input looks like:");
		out.println("\nD 2 4 S USS MyBoat\n");
		out.println("   The above line creates a Destroyer with the stern located at x=2 (col)," );
		out.println("   y=4 (row) and the front of the ship will point to the SOUTH (valid" );
		out.println("   headings are N, E, S, and W.\n\n" );
		out.println("   the name of the ship will be \"USS MyBoat\"");

		int count = 1;
		while(count < 3){
			out.println("Enter Ship " + count + "  information:" );
			out.flush();
			String input = in.readLine();
			try{if(cShipAdd(input)){
				count++;
			}}
			catch(Exception e){
				out.println("Not correct format");
			}
		}

		//Get ship locations from the player for all 2 ships (or more than 2 if you're using more ships)

		drawBoard();
		//After all game state is input, draw the game board to the client



		out.println( "Waiting for other player to finish their setup, then war will ensue!" );
	}

	private boolean cShipAdd(String input) {
		String[] inputSplit = input.split(" ");
		HEADING nHead = null; 

		switch(inputSplit[3]){
		case "N":
			nHead = HEADING.NORTH;
			break;
		case "S":
			nHead = HEADING.SOUTH;
			break;
		case "W":
			nHead = HEADING.WEST;
			break;
		case "E":
			nHead = HEADING.EAST;
			break;
		default:
			return false;

		}

		switch(inputSplit[0]){
		case "D":
			return board.addShip(new Destroyer(input.substring(8)), new Position(Integer.parseInt(inputSplit[1]),Integer.parseInt(inputSplit[2])), nHead);
		case "C":
			return board.addShip(new Cruiser(input.substring(8)), new Position(Integer.parseInt(inputSplit[1]),Integer.parseInt(inputSplit[2])), nHead);
		default:
			return false;
		}
	}

	String getName() { return this.name; }

	public static void main( String [] args )
	{


	}
}
