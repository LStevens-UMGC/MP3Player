package group2.mp3player;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MusicPlayerApp extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/group2/mp3player/view/MusicPlayer.fxml"));
		Scene scene = new Scene(loader.load());
		primaryStage.setScene(scene);
		primaryStage.setTitle("MP3 Player");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
