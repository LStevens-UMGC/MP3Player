package group2.mp3player.controller;

import group2.mp3player.model.Equalizer;
import group2.mp3player.model.MusicPlayer;
import group2.mp3player.model.Playlist;
import group2.mp3player.model.Song;
import group2.mp3player.utils.JsonHandler;
import group2.mp3player.utils.MetaDataExtractor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;


/**
 * The `MusicPlayerController` class is responsible for handling user interactions
 * with the music player interface, managing playlists, songs, and playback control.
 */
public class MusicPlayerController {
    private final MusicPlayer model = MusicPlayer.getInstance();
    private static final String PLAYLISTS_FILE = "playlists.json";
    private static final String ALL_SONGS_FILE = "allSongs.json";
    private static final String SONG_HISTORY_FILE = "songHistory.json";
    private Equalizer savedEqualizer;
    private String currentPlaylist;
    private static final String VOLUME_KEY = "volume";
    private static final String EQUALIZER_GAINS_KEY = "equalizerGains";
    private Preferences preferences;

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
    private Button randomizeButton;

    @FXML
    private Button clearPlaylistButton;

    @FXML
    private TextField searchPlaylistField;

    @FXML
    private TextField searchSongsField;

    @FXML
    private ImageView speakerIcon;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Label volumePercentageLabel;

    /**
     * Initializes the music player controller, setting up the necessary
     * elements and listeners for the user interface.
     *
     * It configures the TableView columns and playlist ListView,
     * initializes the model, and sets up event handlers for interacting
     * with the UI elements.
     *
     * - Configures columns to display song details such as title, artist, album, and year.
     * - Sets up context menu for adding and removing songs from playlists.
     * - Handles double-click events on songs to play them.
     * - Sets up the playlist ListView and loads playlists from a file.
     * - Adds listeners for search fields to filter playlists and songs.
     * - Loads previously saved volume settings and initializes UI elements
     *   such as volume slider and progress bar.
     * - Sets up various image icons for control buttons.
     */
    @FXML
    public void initialize() {
        preferences = Preferences.userNodeForPackage(MusicPlayerController.class);
        // Set up the columns to display song details
        songTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(songTableView, Priority.ALWAYS);
        VBox.setVgrow(progressBar, Priority.ALWAYS);
        songNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        // Changes to retrive all songs list.
        songTableView.setItems(model.getAllSongs());
        currentPlaylist = "AllSongs";


		// Initialize model
		model.setLabelsAndProgressBar(songTitleLabel, totalTimeLabel, progressBar);
		model.initialize();
		ContextMenu action = new ContextMenu();
		MenuItem addToPlaylist = new MenuItem("Add To Playlist");
		MenuItem removeFromPlaylist = new MenuItem("Remove From Playlist");
		action.getItems().add(addToPlaylist);
		action.getItems().add(removeFromPlaylist);

		songTableView.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				Song selectedSong = songTableView.getSelectionModel().getSelectedItem();
				if (selectedSong != null) {
					playlistLabel.setText("");
					songTitleLabel.setText("Viewing : " + selectedSong.getTitle());
					model.playSongFromHistory(selectedSong);
                    loadEqualzierWithoutDisplay();
                    Image pauseIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/pause2.png")));
                    ((ImageView) playPauseButton.getGraphic()).setImage(pauseIcon);
					setupCurrentTimeHandler();
					setupAutoPlayHandler();
				}
			}
			// context menu for added and removing songs from playlist.
			if (event.getButton() == MouseButton.SECONDARY) {
				action.show(songTableView, event.getScreenX(), event.getScreenY());

			}

			// context action to add song to playlist;
			addToPlaylist.setOnAction(a -> {
				handleAddSongToPlaylist();
			});
			// context action to remove song from playlist.
			removeFromPlaylist.setOnAction(a -> {
				handleRemoveSongFromPlaylist();
			});
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
                currentPlaylist = selectedPlaylistName;
                playlistLabel.setText("Viewing : " + selectedPlaylistName);
                if (selectedPlaylistName != null) {
                    loadPlaylist(selectedPlaylistName);
                }
            }
        });


        searchPlaylistField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchPlaylistByName(newVal);
        });

        searchSongsField.textProperty().addListener((obs, oldVal, newVal) -> {
            List<Song> currentList;

            if (mainPlaylist == null || mainPlaylist.isEmpty()) {
                currentList = model.getSongHistory();
            } else {
                currentList = mainPlaylist;
            }

            if (newVal.isEmpty()) {
                songTableView.setItems(FXCollections.observableArrayList(currentList));
            }
            List<Song> result = Song.searchSongs(currentList, newVal);
            songTableView.setItems(FXCollections.observableArrayList(result));
        });

        double savedVolume = getSavedVolume();
        volumeSlider.setValue(savedVolume);
        updateVolumePercentageLabel(savedVolume);
        loadEqualzierWithoutDisplay();

        setupProgressBarSeekHandler();
        setupVolumeBarHandler();
        setupAutoPlayHandler();

        Image prevIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/previous2.png")));
        ((ImageView) prevButton.getGraphic()).setImage(prevIcon);

        Image playIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/play2.png")));
        ((ImageView) playPauseButton.getGraphic()).setImage(playIcon);

        Image nextIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/next2.png")));
        ((ImageView) nextButton.getGraphic()).setImage(nextIcon);

        Image randomIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/repeat.png")));
        ((ImageView) randomizeButton.getGraphic()).setImage(randomIcon);

        Image speakerImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/speaker.png")));
        speakerIcon.setImage(speakerImage);
    }

    /**
     * Gets saved volume data. Used to ensure music player volume initialized properly
     * @return saved value for volume
     */
    private double getSavedVolume() {
        Preferences preferences = Preferences.userNodeForPackage(MusicPlayerController.class);
        return preferences.getDouble("volume", 100.0); // Default to 100% if no value is saved
    }

    /**
     * Saves the current song data to JSON files. This includes all songs, song history, and playlists.
     *
     * This method is typically invoked when a close request is received from the primary stage.
     * It ensures that the application state related to songs is persisted, allowing for data
     * recovery in future sessions.
     *
     * The following data is saved:
     * - All songs
     * - Song history
     * - Playlists
     *
     * The data is saved to predefined JSON files.
     */
    @FXML
    private void saveSongData() {
        JsonHandler.saveToJson(model.getAllSongs(), ALL_SONGS_FILE);
        JsonHandler.savePlaylistsToJson(model.getPlaylists(), PLAYLISTS_FILE);
        JsonHandler.saveToJson(model.getSongHistory(), SONG_HISTORY_FILE);
        System.out.println("Song Data Saved");

    }

    /**
     * Handles the action of adding a selected song to a selected playlist.
     * <p>
     * This method performs the following steps:
     * 1. Retrieves the selected song from the song table view.
     * 2. Retrieves the selected playlist name from the playlist list view.
     * 3. Finds the playlist object corresponding to the selected playlist name.
     * 4. Adds the selected song to the found playlist.
     * 5. Saves the updated playlist list to a JSON file.
     * 6. Prints appropriate messages to the console based on success or failure of these operations.
     */
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

	/**
     * Handles the removal of a selected song from a specified playlist.
     *
     * This method first checks if a song and a playlist are selected. If the selected playlist is "AllSongs",
     * the song is removed from the collection of all songs, and the view is updated accordingly. If the selected
     * playlist is a user-defined playlist, it identifies the playlist, removes the song from it, saves the updated
     * playlist data to a JSON file, and reloads the playlist.
     *
     * The method prints appropriate messages to the console if the operation is successful or if any issues arise
     * (such as no song or playlist being selected, or the specified playlist not being found).
     */
	private void handleRemoveSongFromPlaylist() {
		Song selectedSong = songTableView.getSelectionModel().getSelectedItem();
		String selectedPlaylistName = currentPlaylist;//playlistListView.getSelectionModel().getSelectedItem();
		if ((selectedSong != null) && (selectedPlaylistName != null)) {
            if(selectedPlaylistName == "AllSongs"){
                removeSongFromAll(selectedSong);
                viewAllSongs();
            }
            else{
                Playlist selectedPlaylist = model.getPlaylists().stream()
                        .filter(playlist -> playlist.getName().equals(selectedPlaylistName)).findFirst().orElse(null);
                if (selectedPlaylist != null) {
                    selectedPlaylist.removeSpecifiedSong(selectedSong);

                    JsonHandler.savePlaylistsToJson(model.getPlaylists(), "playlists.json");
                    System.out.println("Song removed from playlist and saved to JSON.");
                    loadPlaylist(selectedPlaylistName);
                } else {
                    System.out.println("Selected playlist not found.");
                }
            }
		} else {
			System.out.println("No song or playlist selected.");
		}
	}

    /**
     * Handles the action of opening an MP3 file, extracting its metadata, and updating the song history.
     * <p>
     * This method performs the following operations:
     * 1. Opens a file chooser dialog allowing the user to select an MP3 file.
     * 2. Extracts metadata from the selected MP3 file using the MetaDataExtractor class.
     * 3. Adds the song with the extracted metadata to the song history and all songs list.
     * 4. Saves the updated song history to a JSON file.
     * 5. Updates song metadata information in the UI labels.
     * <p>
     * If an error occurs during file selection or metadata extraction, it prints an error message to the console.
     */
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

    /**
     * Handles the action of opening multiple MP3 files from a directory and its subdirectories.
     * <p>
     * This method allows the user to select a root directory using a DirectoryChooser dialog.
     * Once a directory is selected, it recursively processes all MP3 files within that directory
     * and its subdirectories by invoking the recursiveOpen method.
     * <p>
     * The MP3 files are processed to extract metadata, which is subsequently added to the song history
     * and the all songs list.
     * <p>
     * If the user cancels the selection, no action is taken.
     */
    @FXML
    private void handleMassOpen() {
        DirectoryChooser dChooser = new DirectoryChooser();
        File directory = dChooser.showDialog(null);
        recursiveOpen(directory);

    }

    /**
     * Recursively traverses a directory and its subdirectories to find and process
     * all MP3 files. For each MP3 file found, its metadata is extracted and the
     * song is added to the song history and the overall song list.
     *
     * @param directory the root directory to start the search from. If the directory
     *                  contains any MP3 files, their metadata will be extracted and added to
     *                  the song history and song list. Subdirectories will be traversed
     *                  recursively.
     */
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

    /**
     * Adds a song to the list of all songs in the model and saves the updated list to a JSON file.
     *
     * @param song the Song object to be added to the list of all songs
     */
    private void addToAllSongs(Song song) {
        model.getAllSongs().add(song);
        JsonHandler.saveToJson(model.getAllSongs(), model.getAllSongsFile());
    }

    /**
     * Removes a specified song from the list of all songs and updates the storage.
     *
     * @param song the Song object to be removed from the list
     */
    private void removeSongFromAll(Song song) {
        model.getAllSongs().remove(song);
        JsonHandler.saveToJson(model.getAllSongs(), model.getAllSongsFile());
    }

    /**
     * Switches the song view to display the entire list of songs.
     * <p>
     * This method updates the playlist label to indicate that all songs are
     * currently being viewed. It also sets the items of the song table view
     * to the list of all songs retrieved from the model.
     */
    @FXML
    private void viewAllSongs() {
        currentPlaylist = "AllSongs";
        playlistLabel.setText("");
        playlistLabel.setText("Viewing : All Songs");
        songTableView.setItems(model.getAllSongs());

    }

    /**
     * Handles the play/pause functionality for the media player.
     * <p>
     * This method retrieves the selected song from the song table view
     * and invokes the model's handlePlayPause method to manage the
     * play/pause state of the media player. It also updates the
     * play/pause button text accordingly.
     */
    @FXML
    private void handlePlayPause() {
        Song selectedSong = songTableView.getSelectionModel().getSelectedItem();
        model.handlePlayPause(selectedSong, playPauseButton);
        setupCurrentTimeHandler();
    }

    /**
     * Handles the action of selecting and playing the next song in the song table view.
     * <p>
     * This method performs the following operations:
     * 1. Retrieves the current selected song from the song table view.
     * 2. Determines the next song to be played based on the current song's position in the song list.
     * 3. If not at the end of the list, selects the next song; otherwise, loops back to the first song.
     * 4. Invokes the model's handlePrevNext method to handle media player state and play the next song.
     * 5. Resets the media player setup by invoking the resetSetup method.
     */
    @FXML
    private void handleNextSong() {
        Song currentSong = songTableView.getSelectionModel().getSelectedItem();
        ArrayList<Song> songList = new ArrayList<>(songTableView.getItems());
        Song nextSong = new Song();
        if(!model.getRandomStatus()){
            if (songList.indexOf(currentSong) < songList.size() - 1) {
                songTableView.getSelectionModel().clearAndSelect(songTableView.getSelectionModel().getSelectedIndex() + 1);
                nextSong = songTableView.getSelectionModel().getSelectedItem();

            } else if (songList.indexOf(currentSong) == songList.size() - 1) {
                songTableView.getSelectionModel().clearAndSelect(0);
                nextSong = songTableView.getSelectionModel().getSelectedItem();
            }
        }
        else{
            int randomIndex = (int) (Math.random() * songList.size());
            nextSong = songList.get(randomIndex);
            songTableView.getSelectionModel().clearAndSelect(randomIndex);
        }
        playlistLabel.setText(""); // Setting song title display
        songTitleLabel.setText("Viewing : " + nextSong.getTitle()); //setting song title display
        model.handlePrevNext(nextSong);
        loadEqualzierWithoutDisplay();
        resetSetup();
    }

    /**
     * Handles the action of selecting and playing the previous song in the song table view.
     * <p>
     * This method performs the following steps:
     * 1. Retrieves the currently selected song from the song table view.
     * 2. Creates a list of all songs from the song table view.
     * 3. Determines the previous song based on the current song's position within the list.
     * - If the current song is the first in the list, the previous song remains the same.
     * - If the current song is not the first, it selects the previous song in the list.
     * 4. Invokes the model's handlePrevNext method to handle media player state and play the previous song.
     * 5. Resets the media player setup by invoking the resetSetup method.
     */
    @FXML
    private void handlePreviousSong() {
        Song currentSong = songTableView.getSelectionModel().getSelectedItem();
        ArrayList<Song> songList = new ArrayList<>(songTableView.getItems());
        Song prevSong = new Song();
        if (songList.indexOf(currentSong) == 0) {
            prevSong = currentSong;
        } else if (songList.indexOf(currentSong) <= songList.size()) {
            songTableView.getSelectionModel().clearAndSelect(songTableView.getSelectionModel().getSelectedIndex() - 1);
            prevSong = songTableView.getSelectionModel().getSelectedItem();
        }
        playlistLabel.setText(""); // Setting song title display
        songTitleLabel.setText("Viewing : " + prevSong.getTitle()); //setting song title display
        model.handlePrevNext(prevSong);
        loadEqualzierWithoutDisplay();
        resetSetup();
    }


    /**
     * Handles the action of clearing all playlists in the music player.
     * <p>
     * This method performs the following steps:
     * 1. Checks if there are any playlists. If there are none, it prints "No playlist found." to the console.
     * 2. If playlists exist, it creates and shows a confirmation alert dialog asking the user if they really want to clear all playlists.
     * 3. If the user confirms the action by clicking "Yes", it:
     * - Clears the playlists from the model.
     * - Clears the playlists from the playlistListView.
     * - Clears the playlists stored in the JSON file "playlists.json".
     * - Prints "Playlists cleared." to the console.
     * 4. If the user cancels the action by clicking "No", it prints "Cancelled. Playlists not cleared." to the console.
     */
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

    /**
     * Clears all songs from song history and resets the playlist.
     * <p>
     * This method performs the following operations:
     * 1. Clears all items from the song table view.
     * 2. Clears the song history list in the model.
     * 3. Clears the all songs list in the model.
     * 4. Clears the saved playlists from the song history JSON file.
     * 5. Clears the saved playlists from the all songs JSON file.
     * <p>
     * It effectively resets the state by removing all saved and displayed songs and playlists.
     */
    @FXML
    private void handleClearSongHistory() {
        songTableView.getItems().clear();
        model.getSongHistory().clear();
        model.getAllSongs().clear();
        JsonHandler.clearPlaylistsFromJson(model.getSongHistoryFile());
        JsonHandler.clearPlaylistsFromJson(model.getAllSongsFile());
    }

    /**
     * Handles the creation of a new playlist in the music player application.
     * <p>
     * This method displays a TextInputDialog to prompt the user to enter the name of the new playlist.
     * If the user provides a name, it creates the playlist using the application's model.
     * The new playlist name is then added to the playlistListView.
     */
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

    /**
     * Event handler that displays the song history in the song table view.
     * Updates the playlist label to indicate that the song history is being viewed.
     */
    @FXML
    private void handleShowSongHistory() {
        songTableView.setItems(model.getSongHistory());
        playlistLabel.setText("Viewing : Song History");
    }

    /**
     * Handles the opening of the Equalizer window.
     *
     * This method loads the Equalizer FXML layout and initializes the EqualizerController with the current
     * media player. If previously saved equalizer settings are available, they are restored. The method
     * also sets up an event handler to save equalizer settings when the Equalizer window is closed.
     *
     * In case of exceptions during the loading or initialization of the Equalizer window,
     * an error message is printed to the console.
     */
    @FXML
    private void handleOpenEqualizer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group2/mp3player/view/Equalizer.fxml"));
            Parent root = loader.load();

            EqualizerController equalizerController = loader.getController();
            equalizerController.setMediaPlayer(model.getMediaPlayer());

            // Restore saved equalizer settings if available
            double[] savedGainValues = loadEqualizerSettings();
            if (savedGainValues != null) {
                equalizerController.getEqualizerModel().setGainValues(savedGainValues);
            }


            Stage equalizerStage = new Stage();
            equalizerStage.setTitle("Equalizer");
            equalizerStage.setScene(new Scene(root));

            // Save equalizer settings on close
            equalizerStage.setOnCloseRequest(event -> {
                savedEqualizer = equalizerController.getEqualizerModel();
                saveEqualizerSettings(savedEqualizer.getGainValues());
            });

            equalizerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error opening the equalizer.");
        }
    }

    /**
     * Loads the equalizer without displaying its UI.
     *
     * This method initializes the equalizer by loading its FXML file, setting the
     * media player to the controller, and restoring any saved equalizer settings.
     * In case of any exceptions during loading, the error is printed to the stack trace.
     */
    private void loadEqualzierWithoutDisplay(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/group2/mp3player/view/Equalizer.fxml"));
            Parent root = loader.load();

            EqualizerController equalizerController = loader.getController();
            equalizerController.setMediaPlayer(model.getMediaPlayer());

            // Restore saved equalizer settings if available
            double[] savedGainValues = loadEqualizerSettings();
            if (savedGainValues != null) {
                equalizerController.getEqualizerModel().setGainValues(savedGainValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error opening the equalizer.");
        }
    }

    /**
     * Saves the equalizer gain settings to the user's preferences.
     *
     * @param gainValues an array of gain values for the equalizer bands
     */
    private void saveEqualizerSettings(double[] gainValues) {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        prefs.put("equalizerGains", Arrays.toString(gainValues)); // Save as a comma-separated string
    }

    /**
     * Resets the equalizer settings to their default values.
     *
     * This method initializes an array of double values representing the
     * gain for each of the 10 equalizer bands to zero. It then saves these
     * settings and reloads the equalizer without updating the display.
     */
    public void resetEqualizerSettings() {
        double[] gainValues = new double[10];
        Arrays.fill(gainValues, 0.0);
        saveEqualizerSettings(gainValues);
        loadEqualzierWithoutDisplay();
    }

    /**
     * Loads equalizer settings from user preferences. The settings are stored as a
     * comma-separated string of gain values associated with the "equalizerGains" key.
     *
     * @return a double array of equalizer gains if settings are found and parsed
     * successfully, or null if no settings are found or an error occurs.
     */
    private double[] loadEqualizerSettings() {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        String gainsString = prefs.get("equalizerGains", null); // Retrieve the stored string
        if (gainsString != null) {
            try {
                String[] gainStrings = gainsString.replaceAll("[\\[\\]\\s]", "").split(",");
                return Arrays.stream(gainStrings)
                        .mapToDouble(Double::parseDouble)
                        .toArray(); // Convert to double array
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error parsing saved equalizer gains.");
            }
        }
        return null; // No saved settings found
    }

    /**
     * Initializes the handler for the volume slider. This method adds a change listener
     * to the volume slider's value property. When the slider's value changes, the volume
     * of the media player in the model is adjusted accordingly.
     * <p>
     * The volume is set by converting the slider value to a double representing a percentage
     * of the maximum volume.
     */
    private void setupVolumeBarHandler() {
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (model.getMediaPlayer() != null) {
                model.setVolume(newVal.doubleValue() / 100);
            }
            updateVolumePercentageLabel(newVal.doubleValue());
            preferences.putDouble(VOLUME_KEY, newVal.doubleValue());
        });
    }

    /**
     * Updates the volume percentage label with the given volume as a percentage.
     *
     * @param volume the volume level to be displayed as a percentage
     */
    private void updateVolumePercentageLabel(double volume) {
        volumePercentageLabel.setText(String.format("%.0f%%", volume));
    }

    /**
     * Initializes the seek handler for the progress bar.
     *
     * This method sets up an event handler for the progress bar that responds to mouse
     * press and drag events. When triggered, the handler seeks the media player to
     * a position corresponding to the value of the progress bar.
     */
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

    /**
     * Loads songs from the selected playlist into the main playlist.
     *
     * @param playlistName the name of the playlist to load
     */
    private void loadPlaylist(String playlistName) {

        List<Song> loadedSongs = model.loadPlaylist(playlistName);

        mainPlaylist.clear();
        mainPlaylist.addAll(loadedSongs);

        songTableView.setItems(FXCollections.observableList(mainPlaylist));

    }

    /**
     * Updates the playlist view based on the provided playlist name.
     * This method searches for playlists that contain the specified
     * name (case-insensitive) and updates the view to show the matching playlists.
     *
     * @param playlistName the name or partial name of the playlist(s) to search for.
     *                     If null or empty, all playlists will be displayed.
     */
    private void searchPlaylistByName(String playlistName) {
        playlistListView.setItems(FXCollections.observableArrayList(model.searchPlaylistByName(playlistName)));
    }

    /**
     * Adds a song from the playlist to the song history.
     *
     * @param song the song to be added from the playlist.
     */
    private void addSongFromPlaylist(Song song) {
        ObservableList<Song> songsInHistory = model.getSongHistory();

        for (Song comparingSong : songsInHistory) {

        }
    }

    /**
     * Resets the setup by initializing the current time handler and the auto-play handler.
     * This method is responsible for setting up the necessary handlers to manage the
     * current playback time and handle auto-play functionality.
     */
    private void resetSetup() {
        setupCurrentTimeHandler();
        setupAutoPlayHandler();
    }

    /**
     * Sets up a listener on the current time property of the media player.
     * The listener updates the current time label with the formatted current time
     * of the media being played.
     * <p>
     * If the media player is not null, this method will add a listener to the
     * media player's current time property. The listener will react to changes
     * in the media player's current time and will update the current time label
     * with a formatted time string based on the new value of the current time
     * and the total duration of the media.
     */
    private void setupCurrentTimeHandler() {
        if (model.getMediaPlayer() != null) {
            model.getMediaPlayer().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                currentTimeLabel.setText(formatTime(newValue, model.getMediaPlayer().getTotalDuration()));
            });
        }
    }

    /**
     * Initializes the auto-play handler for the media player.
     * This method sets an event listener on the media player
     * that triggers when the media reaches its end. Upon
     * reaching the end of the media, the following actions
     * are performed:
     * 1. Proceeds to handle the next song.
     * 2. Sets up the current time handler.
     * 3. Reinitializes the auto-play handler to ensure
     * it remains active for the next media.
     * <p>
     * Note: If the media player is null, this method performs
     * no action.
     */
    private void setupAutoPlayHandler() {
        if (model.getMediaPlayer() != null) {

            model.getMediaPlayer().setOnEndOfMedia(() -> {
                handleNextSong();
                setupCurrentTimeHandler();
                setupAutoPlayHandler();
            });
        }
    }

    /**
     * Formats the elapsed and total duration into a human-readable string.
     *
     * @param elapsed       The duration that has elapsed.
     * @param totalDuration The total duration of the event.
     * @return A string representation of the elapsed and total duration formatted as "HH:MM:SS/HH:MM:SS"
     * if the total duration has hours, or "MM:SS/MM:SS" if it does not. If the total duration is zero,
     * it returns only the elapsed time in the appropriate format.
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
                return String.format(
                        "%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds,
                        totalHours, totalMinutes, totalSeconds
                );
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

    /**
     * Toggles the randomization state in the model and updates the UI icon accordingly.
     *
     * The method checks the current randomization status from the model and sets the
     * appropriate icon on the randomizeButton based on the status. If randomization is
     * turned off, it sets the icon to "repeat.png". If randomization is turned on, it
     * sets the icon to "shuffle.png".
     */
    public void toggleRandomize() {
        model.toggleRandomize();
        if(model.getRandomStatus() == false){
            Image shuffleIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/repeat.png")));
            ((ImageView) randomizeButton.getGraphic()).setImage(shuffleIcon);
        }
        else{
            Image shuffleIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/shuffle.png")));
            ((ImageView) randomizeButton.getGraphic()).setImage(shuffleIcon);

        }
    }


}