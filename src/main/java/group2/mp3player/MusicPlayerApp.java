package group2.mp3player;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;


/**
 * MusicPlayerApp is the main entry point for the MP3 Player JavaFX application.
 * This class extends the Application class and is responsible for loading the
 * primary stage with the user interface defined in the MusicPlayer.fxml file.
 */
public class MusicPlayerApp extends Application {
	/**
	 * Initializes and displays the primary stage of the MP3 Player application.
	 *
	 * @param primaryStage the main stage for this application, onto which
	 * the application scene can be set.
	 * @throws Exception if the FXML file or the stylesheet cannot be loaded.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/group2/mp3player/view/MusicPlayer.fxml"));
		Scene scene = new Scene(loader.load());
		scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("MP3 Player");
		primaryStage.show();
	}

	/**
	 * The main method serves as the entry point for the MP3 Player JavaFX application.
	 * It launches the JavaFX application by calling the launch method with the provided
	 * command-line arguments.
	 *
	 * @param args the command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
