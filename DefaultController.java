import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSlider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DefaultController implements ProgressListener{
	@FXML private JFXProgressBar progressBar;
	@FXML private JFXComboBox<String> startCB;
	@FXML private JFXComboBox<String> targetCB;
	@FXML private JFXSlider slider;
	@FXML private Label diskLabel;
	@FXML private JFXButton startBtn;
	private boolean running;
	private MoveCalculator calcTask;
	private static Stage viewerStage;
	
	@FXML private void onStartPressed() throws Exception{
		if(!running)
			startCalculation();
		else
			stopCalculation();
	}
	
	private void startCalculation() throws Exception{
		closeViewer();
		double value = slider.getValue();
		int numDisks = (int)value+((value-(int)value>0.5)?1:0);
		if((startCB.getSelectionModel().isEmpty())||(targetCB.getSelectionModel().isEmpty()))
			return;
		
		running = true;
		startBtn.setText("Cancel");
		int startT = startCB.getSelectionModel().getSelectedIndex();
		int targetT = targetCB.getSelectionModel().getSelectedIndex();
		ArrayList<Integer> nums = new ArrayList<>(Arrays.asList(0,1,2));
		nums.remove((Integer)startT);
		nums.remove((Integer)targetT);
		int midT = nums.get(0);
		
		calcTask = new MoveCalculator(numDisks,startT,midT,targetT);
		calcTask.addListener(this);
		calcTask.setOnSucceeded(event->{
			running = false;
			startBtn.setText("Start");
			openMoveViewer();
		});
		calcTask.setOnCancelled(event->{
			running = false;
			startBtn.setText("Start");
		});
		
		new Thread(calcTask).start();
	}
	
	private void stopCalculation() {
		calcTask.cancel();
		running = false;
	}
	
	public void openMoveViewer(){
		viewerStage = new Stage();
		viewerStage.setOnCloseRequest(event->{
			ViewerController.closeAnimation();
		});
		viewerStage.setResizable(false);
		viewerStage.setTitle("Move Selector");
		ViewerController viewerC = new ViewerController(calcTask.getMoves(),calcTask.getDisks(),calcTask.getStartT());
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MoveViewer.fxml"));
		loader.setController(viewerC);
		try {
			viewerStage.setScene(new Scene(loader.load()));
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}viewerStage.getIcons().add(new Image(getClass().getResourceAsStream("Hanoi Icon.png")));
		viewerStage.show();
	}
	
	@FXML public void initialize() {
		ObservableList<String> items = FXCollections.observableArrayList("Left Tower", "Middle Tower", "Right Tower");
		startCB.setItems(items);
		targetCB.setItems(items);
	}

	@Override
	public void updateProgress(double percent) {
		progressBar.progressProperty().set(percent);
	}
	
	public static void closeViewer(){
		if(viewerStage!=null)
			viewerStage.fireEvent(new WindowEvent(viewerStage,WindowEvent.WINDOW_CLOSE_REQUEST));
	}
}