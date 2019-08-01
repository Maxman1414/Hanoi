import java.util.ArrayList;

public class TowerManager {
	
	private int[][] initialTowers, currentTowers;//towers ordered top(0) to bottom(size)
	private int[] initialTop, topIndex;//top index is the index of the bottom-most empty slot, -1 the tower is full
	private int disks, startT;
	
	public TowerManager(int numDisks, int startIndex) {//startIndex 0-2 inclusive
		disks = numDisks;
		startT = startIndex;
		reset();
	}
	
	public void preformMoves(ArrayList<Move> moves) {
		for(Move move:moves) {
			preformMove(move);
		}
	}
	
	public int preformMove(Move move) {//returns the diskNum for the moved disk
		int from = move.getFrom();
		int to = move.getTo();
		int index1 = topIndex[from]+1;
		int index2 = topIndex[to];
		int diskVal = currentTowers[from][index1];
		
		currentTowers[to][index2] = diskVal;
		currentTowers[from][index1] = 0;
		topIndex[from]++;
		topIndex[to]--;
		
		return diskVal;
	}
	
	public int[][] getCurrentTowers(){
		return currentTowers;
	}
	
	public int[] getTopIndexes() {
		return topIndex;
	}
	
	public int getNumDisks() {
		return disks;
	}
	
	public void reset() {
		initialTop = new int[3];
		initialTop[0] = disks-1;
		initialTop[1] = disks-1;
		initialTop[2] = disks-1;
		initialTop[startT] = -1;
		topIndex = initialTop;
		
		initialTowers = new int[3][disks];
		initialTowers[0] = new int[disks];
		initialTowers[1] = new int[disks];
		initialTowers[2] = new int[disks];
		for(int index = 0;index<disks;index++) {
			initialTowers[startT][index] = index+1;
		}currentTowers = initialTowers;
	}
}