import java.util.ArrayList;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import processing.core.PApplet;

public class ViewerController{
	@FXML private Label moveLabel;
	@FXML private JFXButton backBtn;
	@FXML private JFXButton playBtn;
	@FXML private JFXButton nextBtn;
	@FXML private JFXListView<Move> moveView;
	
	private boolean playing, reset, nextJustPressed, backJustPressed;
	private int moveIndex;
	private ObservableList<Move> moves;
	private TowerManager towerManager;
	private static TowerViewer viewer;
	
	public ViewerController(ArrayList<Move> moveArr, int numDisks, int startT) {
		moveIndex = -1;
		moves = FXCollections.observableArrayList();
		moves.addAll(moveArr);
		towerManager = new TowerManager(numDisks,startT);
		
		String[] title = {"Tower Of Hanoi"};
		viewer = new TowerViewer(towerManager);
		PApplet.runSketch(title,viewer);
		reset = true;
	}
	
	@FXML public void initialize() {
		setComponentValues();
	}
	
	@FXML public void selectionMade() {
		if(!playing) {
			moveIndex = moveView.getSelectionModel().getSelectedIndex();
			if(!viewer.getSurface().isStopped()) {
				towerManager.reset();
				towerManager.preformMoves(new ArrayList<Move>(moves.subList(0, moveIndex+1)));
				viewer.resetTowers();
			}
		}setComponentValues();
	}
	
	@FXML public void nextPressed() {
		Move move = moves.get(Math.min(moves.size()-1,moveIndex+1)).copy();
		int movingDisk = -1;
		if(viewer.getSurface().isStopped()) {
			moveIndex++;
			setComponentValues();
			return;
		}
		
		if(!nextJustPressed) {
			movingDisk = towerManager.preformMove(move);
			moveIndex++;
		}if((backJustPressed)||(nextJustPressed)) {
			backJustPressed = false;
			nextJustPressed = false;
			viewer.resetTowers();
		}else{
			viewer.preformMove(move,movingDisk);
			nextJustPressed = true;
			runFinishChecker();
		}setComponentValues();
	}
	
	@FXML public void backPressed() {
		Move move = moves.get(Math.max(0,moveIndex)).copy().reverse();
		int movingDisk = -1;
		if(viewer.getSurface().isStopped()) {
			moveIndex--;
			setComponentValues();
			return;
		}
		
		if(!backJustPressed) {
			movingDisk = towerManager.preformMove(move);
			moveIndex--;
		}if((backJustPressed)||(nextJustPressed)) {
			backJustPressed = false;
			nextJustPressed = false;
			viewer.resetTowers();
		}else{
			viewer.preformMove(move,movingDisk);
			backJustPressed = true;
			runFinishChecker();
		}setComponentValues();
	}
	
	@FXML public void playPressed() {
		playing = !playing;
		if(playing && !viewer.getMoving())
			nextPressed();
		setComponentValues();
	}
	
	private void setComponentValues() {
		if(reset) {
			moveView.setItems(moves);
			reset = false;
		}
		
		boolean moving = viewer.getMoving();
		boolean playDisabled = moveIndex>=moves.size()-1 || viewer.getSurface().isStopped();
		playing = playing && !playDisabled && !viewer.getSurface().isStopped();
		boolean nextDisabled = playing || (moveIndex>moves.size()-2 && !moving);
		boolean backDisabled = playing || (moveIndex<0 && !moving) || (moving&&moveIndex>=moves.size()-1);
		
		playBtn.setDisable(playDisabled);
		nextBtn.setDisable(nextDisabled);
		backBtn.setDisable(backDisabled);
		
		if(playing) {
			playBtn.setText("Stop");
			moveView.scrollTo(Math.max(0,moveIndex-4));
		}else
			playBtn.setText("Play");
		
		if(moveIndex<0)
			moveLabel.setText("Start");
		else {
			moveLabel.setText("Move "+(moveIndex+1)+": "+moves.get(moveIndex));
			moveView.getSelectionModel().select(moveIndex);
		}
	}
	
	public void runFinishChecker() {
		if(viewer.getSurface().isStopped())
			return;
		Task<Void> checkFinished = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				boolean wait = true;
				while(wait) {
					if(viewer.getSurface().isStopped())
						break;
					wait = viewer.getMoving();
				}return null;
			}
		};
		checkFinished.setOnSucceeded(event->{
			if(!viewer.getSurface().isStopped()) {
				nextJustPressed = false;
				backJustPressed = false;
				setComponentValues();
				if(playing)
					nextPressed();
			}
		});
		new Thread(checkFinished).start();
	}
	
	public static void closeAnimation() {
		viewer.exit();
	}
}