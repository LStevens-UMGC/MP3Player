package group2.mp3player;

import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MainController {
	@FXML
	private Label welcomeText;

	private MediaPlayer mediaPlayer;

	@FXML
	protected void onPlayButtonClick() {
//        String mp3FilePath = "src/main/resources/group2/mp3player/sample.mp3";
		String mp3FilePath = "soulsweeper.mp3";
		Media media = new Media(Paths.get(mp3FilePath).toUri().toString());

		if (mediaPlayer != null) {
			mediaPlayer.stop(); // Stop any currently playing media
		}

		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play(); // Play the MP3
	}
}
