import java.awt.Point;
import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PGraphics;

public class TowerViewer extends PApplet{
	
	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	
	private int movingDisk;// the moving disk's value = (its initial index in the tower) + 1  
	private int disks; //the number of disks
	private int boxHeight, boxWidth;// the size of the disks
	private int towerY;// the y value of the top of the towers
	private int[] towersX;// contains the x values for each tower
	private HashMap<Integer,Point> points;// key = the disks's value, object = its current point in the animation
	private TowerManager towerManager;// Manages the current layout of the towers after animations
	private PGraphics canvas;// displays the poles of the tower
	private static Move currentMove;// the current move being animated
	
	public TowerViewer(int numDisks, int startIndex) {
		TowerViewer.currentMove = null;
		towerManager = new TowerManager(numDisks,startIndex);
	}
	
	public TowerViewer(TowerManager manager) {
		TowerViewer.currentMove = null;
		towerManager = manager;
	}
	
	public void settings() {
		size(WIDTH,HEIGHT);
		boxWidth = width/7;
		boxHeight = min(height/(disks+2), 15);
		disks = towerManager.getNumDisks();
		points = new HashMap<>();
		towersX = new int[3];
		
		int[][] currentT = towerManager.getCurrentTowers();// sets up the coordinates for each disk/tower
		for(int indexT = 0;indexT<currentT.length;indexT++) {
			int towerX = width/7*((2*indexT)+1);
			towersX[indexT] = towerX;
			for(int index = 0;index<disks;index++) {
				int diskVal = currentT[indexT][index];
				if(diskVal!=0) {
					Point point = new Point(towerX,height-(boxHeight*(disks-index)));
					points.put(diskVal, point);
				}
			}
		}towerY = (int)(height-(boxHeight*((double)disks+1)));
	}
	
	public void setup() {
		frameRate(200);
		surface.setIcon(loadImage("Hanoi Icon.png"));
		
		canvas = createGraphics(width,height);
		canvas.beginDraw();
		canvas.background(255);
		canvas.fill(100);
		canvas.rect(towersX[0]+boxWidth*3/8,towerY,boxWidth/4,height-towerY);
		canvas.rect(towersX[1]+boxWidth*3/8,towerY,boxWidth/4,height-towerY);
		canvas.rect(towersX[2]+boxWidth*3/8,towerY,boxWidth/4,height-towerY);
		canvas.endDraw();
	}
	
	public void draw() {
		surface.setTitle("Tower Viewer");
		image(canvas,0,0);
		for(int diskVal = 1;diskVal<=disks;diskVal++) {//display each disk, with a darker blue for increasing diskValues
			float valR = map(disks-diskVal,0,(float)disks,0,255);
			float valG = map(disks-diskVal,0,(float)disks,85,255);
			float valB = map(disks-diskVal,0,(float)disks,170,255);
			fill(valR,valG,valB);
			Point point = points.get(diskVal);
			if(point!=null)
				rect((float)point.getX(),(float)point.getY(),boxWidth,boxHeight);
		}
		
		if(currentMove!=null) {//moves the moving disk 1 pixel towards its destination
			Point movingPoint = points.get(movingDisk);
			int x = (int)movingPoint.getX();
			int y = (int)movingPoint.getY();
			
			int targetX = towersX[currentMove.getTo()];
			int targetY = height-(boxHeight*(disks-towerManager.getTopIndexes()[currentMove.getTo()]-1));
			
			boolean movingX = x!=targetX;
			boolean movingUp = y>towerY-(boxHeight*1.5) && movingX;
			boolean movingDown = !movingX && y<targetY;
			
			if(movingUp)
				movingPoint.translate(0,-1);
			else if(movingX) {
				if(x<targetX)
					movingPoint.translate(1,0);
				else if(x>targetX)
					movingPoint.translate(-1,0);
			}else if(movingDown)
				movingPoint.translate(0,1);
			else{
				currentMove = null;
				movingDisk = -1;
			}
		}
	}
	
	public Move preformMove(Move move, int moving) {//assumes move is possible/valid (not moving a disk that isnt there or placing a larger disk over a smaller disk) returns true if move preformed
		if(currentMove==null) {
			currentMove = move.copy();
			movingDisk = moving;
			return move;
		}Move lastMove = currentMove;
		currentMove = null;
		resetTowers();
		return lastMove;
	}
	
	public void resetTowers() {
		currentMove = null;
		movingDisk = -1;
		settings();
	}
	
	public boolean getMoving() {
		return currentMove!=null;
	}
	
	public Move getCurrentMove() {
		return currentMove;
	}
	
	@Override
	public void exit() {
		noLoop();
		surface.setVisible(false);
		dispose();
	}
}