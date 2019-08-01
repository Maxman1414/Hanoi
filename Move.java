public class Move {
	
	private int lastTower, nextTower;
	
	public Move(int lastT, int newT) {
		lastTower = lastT;
		nextTower = newT;
	}
	
	public Move swap(int num1, int num2) {
		if(lastTower==num1)
			lastTower = num2;
		else if(lastTower==num2)
			lastTower = num1;
		if(nextTower==num1)
			nextTower = num2;
		else if(nextTower==num2)
			nextTower = num1;
		return copy();
	}
	
	public Move reverse() {
		int temp = lastTower;
		lastTower = nextTower;
		nextTower = temp;
		return copy();
	}
	
	public int getFrom() {
		return lastTower;
	}
	
	public int getTo() {
		return nextTower;
	}
	
	public Move copy() {
		return new Move(lastTower,nextTower);
	}
	
	@Override public String toString() {
		return (lastTower+1)+" -> "+(nextTower+1);
	}
	
	public static void main(String[] args) {
		Move move = new Move(1,2);
		System.out.println(move);
		move.swap(2,3);
		System.out.println(move);
		move.reverse();
		System.out.println(move);
	}
}
