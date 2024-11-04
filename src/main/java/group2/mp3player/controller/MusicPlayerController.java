package group2.mp3player.controller;

import group2.mp3player.model.Playlist;
import group2.mp3player.model.Song;
import group2.mp3player.utils.JsonHandler;
import group2.mp3player.utils.MetaDataExtractor;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MusicPlayerController {
    private final ObservableList<Song> songHistory = FXCollections.observableArrayList();
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();
    private final String SONG_HISTORY_FILE = "songHistory.json";
    private MediaPlayer mediaPlayer;
    private Playlist currentPlaylist;

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
    private Slider volumeSlider;

    @FXML
    public void initialize() {
        // Set up the column to display song titles
        songNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        songTableView.setItems(songHistory);

        // Load the song history from the JSON file on startup
        songHistory.addAll(Objects.requireNonNull(JsonHandler.loadFromJson(SONG_HISTORY_FILE)));

        // Add a double-click event handler to play songs from the history
        songTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Song selectedSong = songTableView.getSelectionModel().getSelectedItem();
                if (selectedSong != null) {
                    playSongFromHistory(selectedSong);
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

        // Load playlists from JSON
        List<Playlist> loadedPlaylists = JsonHandler.loadPlaylistsFromJson("playlists.json");
        if (loadedPlaylists != null) {
            playlists.addAll(loadedPlaylists);
            playlistListView.getItems().addAll(playlists.stream().map(Playlist::getName).toList());
        }

        // Add a click handler for selecting a playlist
        playlistListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String selectedPlaylistName = playlistListView.getSelectionModel().getSelectedItem();
                if (selectedPlaylistName != null) {
                    loadPlaylist(selectedPlaylistName);
                }
            }
        });

        // Handle user input on the progress bar (seeking)
        progressBar.setOnMousePressed(event -> {
            if (mediaPlayer != null) {
                double value = progressBar.getValue();
                mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(value / 100));
            }
        });

        progressBar.setOnMouseDragged(event -> {
            if (mediaPlayer != null) {
                double value = progressBar.getValue();
                mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(value / 100));
            }
        });

        // Set up volume control
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue() / 100);
            }
        });
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
                songHistory.add(song);
                JsonHandler.saveToJson(songHistory, SONG_HISTORY_FILE);

                // Display metadata in labels (optional)
                songTitleLabel.setText("Title: " + song.getTitle());

                // You can create more labels for artist, album, etc., and display them similarly

                Media media = new Media(selectedFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                setupMediaPlayerListeners(mediaPlayer);
                mediaPlayer.play();


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
        if (mediaPlayer == null) {
            Song selectedSong = songTableView.getSelectionModel().getSelectedItem();
            if (selectedSong != null) {
                Media media = new Media(selectedSong.getFilePath());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnReady(() -> totalTimeLabel.setText(formatTime(media.getDuration())));
                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) ->
                                                                      progressBar.setValue(newTime.toSeconds() / media.getDuration().toSeconds() * 100));
                mediaPlayer.play();
                playPauseButton.setText("Pause");
            }
        } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseButton.setText("Play");
        } else {
            mediaPlayer.play();
            playPauseButton.setText("Pause");
        }
    }

    private String formatTime(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    private void playSongFromHistory(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop(); // Stop current playback if a song is already playing
        }

        try {
            Media media = new Media(song.getFilePath());
            mediaPlayer = new MediaPlayer(media);
            setupMediaPlayerListeners(mediaPlayer); // Set up listeners for new mediaPlayer

            songTitleLabel.setText("Playing: " + song.getTitle());
            mediaPlayer.play();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading the media file.");
        }
    }

    @FXML
    private void handleShowSongHistory() {
        songTableView.setItems(songHistory);
    }

    // Method to create a new playlist
    @FXML
    private void handleCreatePlaylist() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Playlist");
        dialog.setHeaderText("Enter the name of the new playlist:");
        dialog.setContentText("Playlist name:");

        dialog.showAndWait().ifPresent(name -> {
            Playlist newPlaylist = new Playlist(name);
            playlists.add(newPlaylist);
            playlistListView.getItems().add(name);
            JsonHandler.savePlaylistsToJson(playlists, "playlists.json");
        });
    }

    // Load songs from a selected playlist
    private void loadPlaylist(String playlistName) {
        currentPlaylist = playlists.stream()
                .filter(playlist -> playlist.getName().equals(playlistName))
                .findFirst()
                .orElse(null);

        if (currentPlaylist != null) {
            songTableView.setItems(currentPlaylist.getSongs());
        }
    }

    @FXML
    private void handleAddSongToPlaylist() {
        Song selectedSong = songTableView.getSelectionModel().getSelectedItem();
        String selectedPlaylistName = playlistListView.getSelectionModel().getSelectedItem();

        if (selectedSong != null && selectedPlaylistName != null) {
            Playlist selectedPlaylist = playlists.stream()
                    .filter(playlist -> playlist.getName().equals(selectedPlaylistName))
                    .findFirst()
                    .orElse(null);

            if (selectedPlaylist != null) {
                selectedPlaylist.addSong(selectedSong);
                JsonHandler.savePlaylistsToJson(playlists, "playlists.json");
                System.out.println("Song added to playlist and saved to JSON.");
            } else {
                System.out.println("Selected playlist not found.");
            }
        } else {
            System.out.println("No song or playlist selected.");
        }
    }

    // Clear the playlists and Listview
    @FXML
    private void handleClearAllPlayLists(){
        if(playlists.isEmpty()) {
            System.out.println("No playlist found.");
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Clear all playlists");
            alert.setHeaderText("Are you sure you want to clear all playlists?");
            alert.setContentText("This action will clear all playlists.");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No", ButtonType.CANCEL.getButtonData());
            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == yesButton){
                playlists.clear();
                playlistListView.getItems().clear();
                JsonHandler.clearPlaylistsFromJson("playlists.json");
                System.out.println("Playlists cleared.");
            }else{
                System.out.println("Cancelled. Playlists not cleared");
            }


        }

    }

    private void setupMediaPlayerListeners(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnReady(() -> {
            progressBar.setValue(0); // Reset progress bar
            totalTimeLabel.setText(formatTime(mediaPlayer.getTotalDuration()));
        });

        // Bind the progress bar to the currentTimeProperty of the MediaPlayer
        progressBar.valueProperty().bind(
                Bindings.createDoubleBinding(
                        () -> {
                            if (mediaPlayer.getTotalDuration().toSeconds() == 0) {
                                return 0.0;
                            } else {
                                return (mediaPlayer.getCurrentTime().toSeconds() / mediaPlayer.getTotalDuration().toSeconds()) * 100;
                            }
                        },
                        mediaPlayer.currentTimeProperty()
                )
        );

        // Update the time label as the song progresses
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer.getTotalDuration().greaterThan(Duration.ZERO)) {
                currentTimeLabel.setText(formatTime(newValue));
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            progressBar.setValue(100); // Set progress to 100% when the song ends
            playPauseButton.setText("Play");
        });

        mediaPlayer.setOnPaused(() -> playPauseButton.setText("Play"));
        mediaPlayer.setOnPlaying(() -> playPauseButton.setText("Pause"));

        mediaPlayer.setOnError(() -> {
            System.out.println("Playback error: " + mediaPlayer.getError().getMessage());
        });
    }
}