package group2.mp3player.controller;
import group2.mp3player.model.MusicPlayer;
import group2.mp3player.model.Song;
import group2.mp3player.utils.MetaDataExtractor;
import group2.mp3player.model.Playlist;
import group2.mp3player.utils.JsonHandler;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;
import java.util.Optional;
import java.util.ArrayList;

public class MusicPlayerController {
	private final MusicPlayer model = MusicPlayer.getInstance();
	private static final String PLAYLISTS_FILE = "playlists.json";

	@FXML
	private ListView<String> playlistListView;

	@FXML
	private TableView<Song> songTableView;

	@FXML
	private TableColumn<Song, String> songNameColumn;

	@FXML
	private TableColumn<Song, String> artistColumn;

	@FXML
	private TableColumn<Song, String> albumColumn;

	@FXML
	private TableColumn<Song, String> yearColumn;

	@FXML
	private Slider progressBar;

	@FXML
	private Label playlistLabel;

	@FXML
	private Label songTitleLabel;

	@FXML
	private Label currentTimeLabel;

	@FXML
	private Label totalTimeLabel;

	@FXML
	private Button playPauseButton;

	@FXML
	private Button showHistoryButton;

	@FXML
	private Button nextButton;

	@FXML
	private Button prevButton;

	@FXML
	private Button clearPlaylistButton;

	@FXML
	private TextField searchTextField;

	@FXML
	private Slider volumeSlider;

	@FXML
	public void initialize() {
		// Set up the columns to display song details
		songNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
		albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
		yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

		songTableView.setItems(model.getSongHistory());

		// Initialize model
		model.setLabelsAndProgressBar(songTitleLabel, totalTimeLabel, progressBar);
		model.initialize();

		songTableView.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				Song selectedSong = songTableView.getSelectionModel().getSelectedItem();
				if (selectedSong != null) {
					model.playSongFromHistory(selectedSong);
					setupCurrentTimeHandler();
				}
			}
		});

		// Set up the playlist list view
		playlistListView.setItems(FXCollections.observableArrayList());
		playlistListView.setCellFactory(param -> new ListCell<>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? null : item);
			}
		});

		model.loadPlaylists(PLAYLISTS_FILE, playlistListView.getItems());

		playlistListView.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				String selectedPlaylistName = playlistListView.getSelectionModel().getSelectedItem();
				playlistLabel.setText("Viewing : " + selectedPlaylistName);
				if (selectedPlaylistName != null) {
					loadPlaylist(selectedPlaylistName);
				}
			}
		});

		searchTextField.textProperty().addListener((obs, oldVal, newVal) -> {searchUpdatePlaylistView(newVal);});

		setupProgressBarSeekHandler();
	}

	@FXML
	private void handleAddSongToPlaylist() {
		Song selectedSong = songTableView.getSelectionModel().getSelectedItem();
		String selectedPlaylistName = playlistListView.getSelectionModel().getSelectedItem();
		if ((selectedSong != null) && (selectedPlaylistName != null)) {
			Playlist selectedPlaylist = model.getPlaylists().stream()
					.filter(playlist -> playlist.getName().equals(selectedPlaylistName)).findFirst().orElse(null);
			if (selectedPlaylist != null) {
				selectedPlaylist.addSong(selectedSong);
				JsonHandler.savePlaylistsToJson(model.getPlaylists(), "playlists.json");
				System.out.println("Song added to playlist and saved to JSON.");
			} else {
				System.out.println("Selected playlist not found.");
			}
		} else {
			System.out.println("No song or playlist selected.");
		}
	}

	@FXML
	private void handleOpen() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			try {
				// Extract metadata
				Song song = MetaDataExtractor.extractMetadata(selectedFile);
				// Add the song with detailed metadata
				model.getSongHistory().add(song);
				JsonHandler.saveToJson(model.getSongHistory(), model.getSongHistoryFile());
				// Display metadata in labels (optional)
				songTitleLabel.setText("Title: " + song.getTitle());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error loading the media file.");
			}
		} else {
			System.out.println("No file selected or dialog was canceled.");
		}
	}

	@FXML
	private void handlePlayPause() {
		Song selectedSong = songTableView.getSelectionModel().getSelectedItem();
		model.handlePlayPause(selectedSong, playPauseButton);
	}

	@FXML
	private void handleClearAllPlayLists() {
		if (model.getPlaylists().isEmpty()) {
			System.out.println("No playlist found.");
		} else {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Clear all playlists");
			alert.setHeaderText("Are you sure you want to clear all playlists?");
			alert.setContentText("This action will clear all playlists.");
			ButtonType yesButton = new ButtonType("Yes");
			ButtonType noButton = new ButtonType("No", ButtonType.CANCEL.getButtonData());
			alert.getButtonTypes().setAll(yesButton, noButton);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && (result.get() == yesButton)) {
				model.getPlaylists().clear();
				playlistListView.getItems().clear();
				JsonHandler.clearPlaylistsFromJson("playlists.json");
				System.out.println("Playlists cleared.");
			} else {
				System.out.println("Cancelled. Playlists not cleared");
			}
		}
	}

	@FXML
	private void handleClearSongHistory() {
		songTableView.getItems().clear();
		model.getSongHistory().clear();
		JsonHandler.clearPlaylistsFromJson("songHistory.json");
	}

	@FXML
	private void handleCreatePlaylist() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Create Playlist");
		dialog.setHeaderText("Enter the name of the new playlist:");
		dialog.setContentText("Playlist name:");
		dialog.showAndWait().ifPresent(name -> {
			model.createPlaylist(name);
			playlistListView.getItems().add(name);
		});
	}

	@FXML
	private void handleShowSongHistory() {
		songTableView.setItems(model.getSongHistory());
		playlistLabel.setText("Viewing : Song History");
	}

	private void setupProgressBarSeekHandler() {
		EventHandler<MouseEvent> seekHandler = event -> {
			if (model.getMediaPlayer() != null) {
				double value = progressBar.getValue();
				model.getMediaPlayer().seek(model.getMediaPlayer().getTotalDuration().multiply(value / 100));
			}
		};
		progressBar.setOnMousePressed(seekHandler);
		progressBar.setOnMouseDragged(seekHandler);
	}

	// Load songs from a selected playlist
	private void loadPlaylist(String playlistName) {

		songTableView.setItems(model.loadPlaylist(playlistName));;

	}

	/*
	TODO HERE
	 */
	//Search playlist functionality
	private void searchUpdatePlaylistView(String playlistName) {
//		List<String> playlistNames = new ArrayList<>();
//		if(playlistName == null || playlistName.isEmpty()) {
//
//			for(Playlist playlist : playlists) {
//				playlistNames.add(playlist.getName());
//			}
//		}else {
//			for (Playlist playlist : playlists) {
//				if (playlist.getName().toLowerCase().contains(playlistName.toLowerCase())) {
//					playlistNames.add(playlist.getName());
//				}
//			}
//		}
		playlistListView.setItems(FXCollections.observableArrayList(model.searchUpdatePlaylistView(playlistName)));
	}

	private void setupCurrentTimeHandler() {
		if (model.getMediaPlayer() != null) {
			model.getMediaPlayer().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
				currentTimeLabel.setText(formatTime(newValue, model.getMediaPlayer().getTotalDuration()));
			});
		}
	}
	/*
	TODO: Clear up code duplication that leads to 2 end timers
	 */
	private String formatTime(Duration elapsed, Duration totalDuration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

		if (totalDuration.greaterThan(Duration.ZERO)) {
			int intTotal = (int) Math.floor(totalDuration.toSeconds());
			int totalHours = intTotal / (60 * 60);
			if (totalHours > 0) {
				intTotal -= totalHours * 60 * 60;
			}
			int totalMinutes = intTotal / 60;
			int totalSeconds = intTotal - totalHours * 60 * 60 - totalMinutes * 60;
			if (totalHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d",
						elapsedHours, elapsedMinutes, elapsedSeconds,
						totalHours, totalMinutes, totalSeconds);
			} else {
				return String.format("%02d:%02d/%02d:%02d",
						elapsedMinutes, elapsedSeconds,
						totalMinutes, totalSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d",
						elapsedHours, elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d",
						elapsedMinutes, elapsedSeconds);
			}
		}
	}
}