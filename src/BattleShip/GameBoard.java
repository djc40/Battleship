package BattleShip;
import java.util.ArrayList;

public class GameBoard
{
	int rowCount = 10;
	int colCount = 10;

	final String LINE_END = System.getProperty("line.separator"); 

	ArrayList< ArrayList< Cell > > cells;
	ArrayList< Ship > myShips = new ArrayList<Ship>();

	public GameBoard( int rowCount, int colCount )
	{
		this.rowCount = rowCount; //y var
		this.colCount = colCount; //x var

		//create the 2D array of cells
		cells = new ArrayList< ArrayList<Cell>>();
		for(int i = 0; i < colCount; i++){
			cells.add(new ArrayList<Cell>());
			for(int j = 0; j < rowCount; j++){
				cells.get(i).add(new Cell());
			}
		}
	}

	public String draw()
	{

		StringBuilder s = new StringBuilder();
		s.append("+");
		for(int i = 0; i < cells.size(); i++){
			s.append("-");
		}
		s.append("+\n");
		for(int i = 0; i < cells.get(0).size(); i++){
			s.append("|");
			for(int j = 0; j <cells.size(); j++){
				s.append(cells.get(j).get(i).draw());
			}
			s.append("|\n");

		}
		s.append("+");
		for(int i = 0; i < cells.size(); i++){
			s.append("-");
		}
		s.append("+\n"); 
		return s.toString();
		//draw the entire board... I'd use a StringBuilder object to improve speed
		//remember - you must draw one entire row at a time, and don't forget the
		//pretty border...
	}

	//add in a ship if it fully 1) fits on the board and 2) doesn't collide w/
	//an existing ship.
	//Returns true on successful addition; false, otherwise
	public boolean addShip( Ship s , Position sternLocation, HEADING bowDirection )
	{
		ArrayList<Cell> position = new ArrayList<Cell>();

		switch (bowDirection) {
		case NORTH: if(sternLocation.y - (s.getLength() - 1)  < 0){
			return false;
		} else{
			for(int i = 0; i < s.getLength(); i++){
				Cell c = cells.get(sternLocation.x).get(sternLocation.y - i);
				if(c.getShip() != null){
					return false;
				}
				position.add(c);
			}
			for(Cell c : position){
				c.setShip(s);
			}
			break;
		}
		case SOUTH: if(sternLocation.y + (s.getLength() - 1) >= rowCount){ 
			return false;
		} else{
			for(int i = 0; i < s.getLength(); i++){
				Cell c = cells.get(sternLocation.x).get(sternLocation.y + i);
				if(c.getShip() != null){
					return false;
				}
				position.add(c);
			}
			for(Cell c : position){
				c.setShip(s);
			}
			break; 
		}

		case EAST: if(sternLocation.x + (s.getLength() - 1) >= colCount){
			return false;
		} else{
			for(int i = 0; i < s.getLength(); i++){
				Cell c = cells.get(sternLocation.x + i).get(sternLocation.y);
				if(c.getShip() != null){
					return false;
				}
				position.add(c);
			}
			for(Cell c : position){
				c.setShip(s);
			}
			break; 
		}

		case WEST: if(sternLocation.x - (s.getLength() - 1) < 0){
			return false;
		} else{
			for(int i = 0; i < s.getLength(); i++){
				Cell c = cells.get(sternLocation.x - i).get(sternLocation.y);
				if(c.getShip() != null){
					return false;
				}
				position.add(c);
			}
			for(Cell c : position){
				c.setShip(s);
			}
			break;
}
		default: return false;
		}
			s.setPosition(position);
			myShips.add(s);
			return true;
}

	//Returns A reference to a ship, if that ship was struck by a missle.
	//The returned ship can then be used to print the name of the ship which
	//was hit to the player who hit it.
	//Ensure you handle missiles that may fly off the grid
	public Ship fireMissle( Position coordinate )
	{
		if(coordinate.x < 0 || coordinate.x > colCount){
			System.out.println("Please only use coordinates on the board");
			return null;
		}
		if(coordinate.y < 0 || coordinate.y > rowCount){
			System.out.println("Please only use coordinates on the board");
			return null;
		}
		
		Cell firedAt = cells.get(coordinate.x).get(coordinate.y);
		firedAt.hasBeenStruckByMissile(true);
		if(firedAt.getShip() != null){
			Ship s = firedAt.getShip();
			return s;
		}else{
			return null;
		}
		
		
	}

	//Here's a simple driver that should work without touching any of the code below this point
	public static void main( String [] args )
	{
		System.out.println( "Hello World" );
		GameBoard b = new GameBoard( 10, 10 );	
		System.out.println("about to draw board");
		System.out.println( b.draw() );

				Ship s = new Cruiser( "Cruiser" );
				if( b.addShip(s, new Position(3,6), HEADING.WEST ) )
					System.out.println( "Added " + s.getName() + "Location is " );
				else
					System.out.println( "Failed to add " + s.getName() );
				
				s = new Destroyer( "Vader" );
				if( b.addShip(s, new Position(3,5), HEADING.NORTH ) )
					System.out.println( "Added " + s.getName() + "Location is " );
				else
					System.out.println( "Failed to add " + s.getName() );
				
				System.out.println( b.draw() );
				
				b.fireMissle( new Position(3,5) );
				System.out.println( b.draw() );
				b.fireMissle( new Position(3,4) );
				System.out.println( b.draw() );
				b.fireMissle( new Position(3,3) );
				System.out.println( b.draw() );
				
				b.fireMissle( new Position(0,6) );
				b.fireMissle( new Position(1,6) );
				b.fireMissle( new Position(2,6) );
				b.fireMissle( new Position(3,6) );
				System.out.println( b.draw() );
				
				b.fireMissle( new Position(6,6) );
				System.out.println( b.draw() );
	}

}
