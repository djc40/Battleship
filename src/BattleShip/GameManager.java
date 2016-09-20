package BattleShip;
import BattleShip.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameManager
{
	private ArrayList<Client> clients = new ArrayList<Client>();
	private ServerSocket listener = null;
	
	public GameManager()
	{		
	}
	
	//Returns a client reference to the opponent. This way, we can inspect attributes
	//and send messages between clients... Each client has a reference to the GameManager
	//so a client is able to use this method to get a reference to his opponent
	public Client getOpponent( Client me )
	{
		for(Client c: clients){
			if(!c.equals(me)){
				return c;
			}
			
		}
		return null;
	}
	
	//In a asychronous nature, begin playing the game. This should only occur after 
	//the players have been fully initialized.
	public void playGame()
	{
		//Each player may begin firing missiles at the other player. First player to lose all ships is the loser.
		//Asynchronously process missile fire commands from each player		
		clients.parallelStream().forEach( client -> 
		{
			try{ client.playGame(); }
			catch( IOException e ) { e.printStackTrace(); } 
		} );
		
	}
	
	//Create a server listener socket and wait for two clients to connect.
	//Use the new client socket to create a PrintWriter and BufferedReader
	//so you can pass these two streams into the constructor of a new client.
	//Don't forget about try/finally blocks, if needed
	boolean waitFor2PlayersToConnect() throws IOException
	{
		ServerSocket serverSocket = null;
		Socket clientSocket1 = null;
		Socket clientSocket2 = null;
		try{
			serverSocket = new ServerSocket(10000);
			System.out.println("Server Socket started");
		}catch (IOException e){
		return false;	
		}
		try{
			clientSocket1 = serverSocket.accept();

		}catch (IOException e){
			System.err.println("Accept Failed");
		}
		System.out.println("Connected");
		
		PrintWriter out = new PrintWriter(clientSocket1.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
		Client client1 = new Client(in, out, this);
		clients.add(client1);
		
		try{
			clientSocket2 = serverSocket.accept();
		}catch (IOException e){
			System.err.println("Accept Failed");
		}
		System.out.println("Connected");

		out = new PrintWriter(clientSocket2.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
		Client client2 = new Client(in, out, this);
		clients.add(client2);
		
		return true;
	}
	
	//let players initialize their name, and gameboard here. This should be done asynchronously
	void initPlayers() throws IOException
	{
		clients.parallelStream().forEach( client ->{
			try{client.initPlayer();}
			catch(Exception e){e.printStackTrace();
		}}
			);
	}
	
	
	//Main driver for the program... Hit Crtl-F11 in eclipse to launch the server...
	//Of course, it has to compile first...
	public static void main( String [] args ) throws IOException
	{
		GameManager m = new GameManager();
		
		System.out.println( "<<<---BattleShip--->>>" );
		System.out.println( "Waiting for two players to connect to TCP:10000" );
		m.waitFor2PlayersToConnect();
		System.out.println( "Clients have joined!!!");		
		m.initPlayers();
		System.out.println( m.clients.get(0).getName() + " vs " + m.clients.get(1).getName() + " Let's Rumble..." );
		m.playGame();		
		System.out.println( "Shutting down server now... Disconnecting Clients..." );
	}

}
