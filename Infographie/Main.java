import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	private static Stage stage;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	static Stage getStage() {
		return stage;
	}
	
	@Override
	public void start(Stage arg0) throws Exception {
		Parent root = FXMLLoader.load(this.getClass().getResource("Main.fxml"));
		Scene scene = new Scene(root);
		arg0.setTitle("Projet Infographie");
		//arg0.setResizable(false);

		arg0.setScene(scene);
		arg0.show();
	}
}
