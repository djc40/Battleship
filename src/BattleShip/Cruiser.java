package BattleShip;

public class Cruiser extends Ship{
	final int length = 4;
	public Cruiser(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public char drawShipStatusAtCell( boolean isDamaged ){
		if(isDamaged){
			return 'c';
		}else{
			return 'C';
		}
	}

	public int getLength(){
		return this.length;
	}
}