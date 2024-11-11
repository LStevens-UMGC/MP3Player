package group2.mp3player.controller;

import java.io.File;
import java.util.Optional;

import group2.mp3player.model.MusicPlayer;
import group2.mp3player.model.Playlist;
import group2.mp3player.model.Song;
import group2.mp3player.utils.JsonHandler;
import group2.mp3player.utils.MetaDataExtractor;
import group2.mp3player.model.Playlist;
import group2.mp3player.utils.JsonHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class MusicPlayerController {
	private final MusicPlayer model = MusicPlayer.getInstance();
	private static final String PLAYLISTS_FILE = "playlists.json";
	private static final String ALL_SONGS_FILE = "allSongs.json";
	private static final String SONG_HISTORY_FILE = "songHistory.json";

	@FXML
	private ListView<String> playlistListView;

	private List<Song> mainPlaylist = new ArrayList<>();
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
	private TextField searchPlaylistField;

	@FXML
	private TextField searchSongsField;

	@FXML
	private Slider volumeSlider;

	@FXML
	public void initialize() {
		// Set up the columns to display song details
		songNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
		albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
		yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
		// Changes to retrive all songs list.
		songTableView.setItems(model.getAllSongs());

		// Initialize model
		model.setLabelsAndProgressBar(songTitleLabel, totalTimeLabel, progressBar);
		model.initialize();

		songTableView.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				Song selectedSong = songTableView.getSelectionModel().getSelectedItem();
				if (selectedSong != null) {
					model.playSongFromHistory(selectedSong);
					setupCurrentTimeHandler();
					setupAutoPlayHandler();
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


		searchPlaylistField.textProperty().addListener((obs, oldVal, newVal) -> {searchUpdatePlaylistView(newVal);});

		searchSongsField.textProperty().addListener((obs, oldVal, newVal) -> {
			List<Song> currentList;

			if(mainPlaylist == null|| mainPlaylist.isEmpty()){
				currentList=model.getSongHistory();
			}else{
				currentList=mainPlaylist;
			}

			if(newVal.isEmpty()) {
				songTableView.setItems(FXCollections.observableArrayList(currentList));
			}
			List<Song> result = Song.searchSongs(currentList, newVal);
			songTableView.setItems(FXCollections.observableArrayList(result));
		});

		setupProgressBarSeekHandler();

		setupAutoPlayHandler();

	}

	/*
	 * On receiving a close request from the primary stage, will save allSongs,
	 * songHistory, and playlists.
	 */
	@FXML
	private void saveSongData() {
		JsonHandler.saveToJson(model.getAllSongs(), ALL_SONGS_FILE);
		JsonHandler.savePlaylistsToJson(model.getPlaylists(), PLAYLISTS_FILE);
		JsonHandler.saveToJson(model.getSongHistory(), SONG_HISTORY_FILE);
		System.out.println("Song Data Saved");

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

	// added addToAllSongs call
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
				addToAllSongs(song);
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

	// Root function to begin recursively adding songs.
	@FXML
	private void handleMassOpen() {
		DirectoryChooser dChooser = new DirectoryChooser();
		File directory = dChooser.showDialog(null);
		recursiveOpen(directory);

	}

	// Recursive function to add all songs.
	private void recursiveOpen(File directory) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						recursiveOpen(file); // Recursively traverse subdirectories
					} else if (file.getName().toLowerCase().endsWith(".mp3")) {
						Song song = MetaDataExtractor.extractMetadata(file);
						JsonHandler.saveToJson(model.getSongHistory(), model.getSongHistoryFile());
						model.getSongHistory().add(song);
						addToAllSongs(song);

					}
				}
			}
		}

	}

	// Adds to the all songs observable list, used in handle open and recursive
	// open.
	private void addToAllSongs(Song song) {
		model.getAllSongs().add(song);
		JsonHandler.saveToJson(model.getAllSongs(), model.getAllSongsFile());
	}

	// Switches the song view to the all songs list.
	@FXML
	private void viewAllSongs() {
		playlistLabel.setText("Viewing : All Songs");
		songTableView.setItems(model.getAllSongs());

	}

	@FXML
	private void handlePlayPause() {
		Song selectedSong = songTableView.getSelectionModel().getSelectedItem();
		model.handlePlayPause(selectedSong, playPauseButton);
	}

	@FXML
	private void handleNextSong(){
		Song currentSong = songTableView.getSelectionModel().getSelectedItem();
		ArrayList<Song> songList = new ArrayList<>(songTableView.getItems());
		Song nextSong = new Song();
		if (songList.indexOf(currentSong) < songList.size()-1){
			songTableView.getSelectionModel().clearAndSelect(songTableView.getSelectionModel().getSelectedIndex()+1);
			nextSong = songTableView.getSelectionModel().getSelectedItem();

        }
		else if(songList.indexOf(currentSong) == songList.size()-1){
			songTableView.getSelectionModel().clearAndSelect(0);
			nextSong = songTableView.getSelectionModel().getSelectedItem();
		}
		model.handlePrevNext(nextSong);
		resetSetup();
	}

	@FXML
	private void handlePreviousSong(){
		Song currentSong = songTableView.getSelectionModel().getSelectedItem();
		ArrayList<Song> songList = new ArrayList<>(songTableView.getItems());
		Song prevSong = new Song();
		if (songList.indexOf(currentSong) == 0){
			prevSong = currentSong;
		}
		else if(songList.indexOf(currentSong) <= songList.size()){
			songTableView.getSelectionModel().clearAndSelect(songTableView.getSelectionModel().getSelectedIndex()-1);
			prevSong = songTableView.getSelectionModel().getSelectedItem();
		}
		model.handlePrevNext(prevSong);
		resetSetup();
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

	// Clears all songs from song history and resets the playlist.
	@FXML
	private void handleClearSongHistory() {
		songTableView.getItems().clear();
		model.getSongHistory().clear();
		model.getAllSongs().clear();
		JsonHandler.clearPlaylistsFromJson(model.getSongHistoryFile());
		JsonHandler.clearPlaylistsFromJson(model.getAllSongsFile());
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

		List<Song> loadedSongs = model.loadPlaylist(playlistName);

		mainPlaylist.clear();
		mainPlaylist.addAll(loadedSongs);

		songTableView.setItems(FXCollections.observableList(mainPlaylist));

	}

	/*
	 * TODO HERE
	 */
	// Search playlist functionality
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

//-->
	private void addSongFromPlaylist(Song song) {
		ObservableList<Song> songsInHistory = model.getSongHistory();

		for (Song comparingSong : songsInHistory) {

		}
	}

	private void resetSetup(){
		setupCurrentTimeHandler();
		setupAutoPlayHandler();
	}

	private void setupCurrentTimeHandler() {
		if (model.getMediaPlayer() != null) {
			model.getMediaPlayer().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
				currentTimeLabel.setText(formatTime(newValue, model.getMediaPlayer().getTotalDuration()));
			});
		}
	}

	private void setupAutoPlayHandler(){
		if (model.getMediaPlayer() != null){

			model.getMediaPlayer().setOnEndOfMedia(() ->{
				handleNextSong();
				setupCurrentTimeHandler();
				setupAutoPlayHandler();
			});
		}
	}
	/*
	 * TODO: Clear up code duplication that leads to 2 end timers
	 */
	private String formatTime(Duration elapsed, Duration totalDuration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - (elapsedHours * 60 * 60) - (elapsedMinutes * 60);

		if (totalDuration.greaterThan(Duration.ZERO)) {
			int intTotal = (int) Math.floor(totalDuration.toSeconds());
			int totalHours = intTotal / (60 * 60);
			if (totalHours > 0) {
				intTotal -= totalHours * 60 * 60;
			}
			int totalMinutes = intTotal / 60;
			int totalSeconds = intTotal - (totalHours * 60 * 60) - (totalMinutes * 60);
			if (totalHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds,
						totalHours, totalMinutes, totalSeconds);
			} else {
				return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, totalMinutes, totalSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
			}
		}
	}


}