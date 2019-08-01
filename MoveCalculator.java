import java.util.ArrayList;
import javafx.concurrent.Task;

public class MoveCalculator extends Task<ArrayList<Move>>{
	
	private double percentDone;
	private int disks, startT, midT, targetT, totalMoves;
	private ArrayList<Move> moves;
	private ArrayList<ProgressListener> listeners;
	
	public MoveCalculator(int numDisks, int start, int mid, int target) {
		super();
		disks = numDisks;
		totalMoves = (int)Math.pow(2, disks)-1;
		startT = start;
		midT = mid;
		targetT = target;
		listeners = new ArrayList<>();
		moves = new ArrayList<>();
	}
	
	@Override
	public ArrayList<Move> call() throws Exception {
		ArrayList<Move> moveArr = new ArrayList<Move>();
		updateProgressListeners(moves.size());
		moves = getMoves(moveArr,disks);
		percentDone = 1;
		updateProgressListeners(moves.size());
		return moves;
	}
	
	private ArrayList<Move> getMoves(ArrayList<Move> lastMoves,int numDisks) throws Exception{
		if((numDisks==0)||(startT==targetT)) {
			moves = lastMoves;
			return lastMoves;
		}

		ArrayList<Move> moveArr = new ArrayList<Move>();
		moveArr.addAll(swapMoves(lastMoves,midT,targetT));
		updateProgressListeners(moveArr.size());
		moveArr.add(new Move(startT,targetT));
		updateProgressListeners(moveArr.size());
		moveArr.addAll(swapMoves(lastMoves,startT,midT));
		updateProgressListeners(moveArr.size());
		
		if(numDisks%2==disks%2) {
			moves = moveArr;
		}
		
		return getMoves(moveArr,numDisks-1);
	}
	
	private ArrayList<Move> swapMoves(ArrayList<Move> moves, int tower1, int tower2) throws Exception{
		ArrayList<Move> moveArr = new ArrayList<>();
		for(Move move:moves) {
			if(isCancelled())
				throw new Exception("Thread Cancelled");
			moveArr.add(move.copy().swap(tower1,tower2));
		}return moveArr;
	}
	
	public void addListener(ProgressListener listener) {
		listeners.add(listener);
	}
	
	public double getPercentDone() {
		return percentDone;
	}
	
	public ArrayList<Move> getMoves(){
		return moves;
	}
	
	public int getDisks() {
		return disks;
	}
	
	public int getStartT() {
		return startT;
	}
	
	public void updateProgressListeners(int num) {
		percentDone = 0;
		if(moves!=null)
			percentDone = num/(double)totalMoves;
		for(ProgressListener listener:listeners) {
			listener.updateProgress(percentDone);
		}
	}
}