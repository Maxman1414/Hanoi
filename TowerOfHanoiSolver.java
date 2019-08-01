import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TowerOfHanoiSolver extends Application{
	
	public void start(Stage primaryStage){
		primaryStage.setTitle("Hanoi Solver");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Hanoi Icon.png")));
		try {
			primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("Default.fxml"))));
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(event->{
			System.exit(0);
		});
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}