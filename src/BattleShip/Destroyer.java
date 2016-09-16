package BattleShip;
public class Destroyer extends Ship{
	final int length = 3;
	public Destroyer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public char drawShipStatusAtCell( boolean isDamaged ){
		if(isDamaged){
			return 'd';
		}else{
			return 'D';
		}
	}

	public int getLength(){
		return this.length;
	}
}