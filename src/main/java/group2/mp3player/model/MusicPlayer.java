package group2.mp3player.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import group2.mp3player.utils.JsonHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.util.prefs.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The `MusicPlayer` class provides functionalities to play, pause,
 * and manage songs and playlists. It also keeps track of the song
 * history and manages UI components related to music playback.
 */
public class MusicPlayer {
	private static MusicPlayer instance = new MusicPlayer();
	private static final String VOLUME_PREF_KEY = "volume";

	private final ObservableList<Song> songHistory = FXCollections.observableArrayList();
	private final ObservableList<Song> allSongs = FXCollections.observableArrayList();
	private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();
	private Playlist currentPlaylist;
	private static final String SONG_HISTORY_FILE = "songHistory.json";
	private static final String ALL_SONGS_FILE = "allSongs.json";
	private MediaPlayer mediaPlayer;
	private boolean randomToggle;

	Label songTitleLabel;
	Label totalTimeLabel;
	Slider progressBar;

	/**
	 * Private constructor to prevent instantiation of the MusicPlayer class.
	 * The MusicPlayer class is designed to follow the Singleton pattern.
	 * To obtain an instance of MusicPlayer, use the getInstance() method.
	 */
	private MusicPlayer() {
		if(instance != null) {
			throw new IllegalStateException("An instance of MusicPlayer is already initialized.");
		}
	}

	/**
	 * Initializes the MusicPlayer by loading the song history and all songs from their respective JSON files.
	 * It also sets the media player volume based on the stored volume preference, if the media player is not null.
	 */
	public void initialize() {
		songHistory.addAll(Objects.requireNonNull(JsonHandler.loadFromJson(SONG_HISTORY_FILE)));
		allSongs.addAll(Objects.requireNonNull(JsonHandler.loadFromJson(ALL_SONGS_FILE)));

		if (mediaPlayer != null) {
			mediaPlayer.setVolume(getSavedVolume());
		}
	}

	/**
	 * Retrieves the singleton instance of the MusicPlayer.
	 *
	 * @return the singleton instance of MusicPlayer
	 */
	public static MusicPlayer getInstance() {
		if (instance == null) {
			instance = new MusicPlayer();
		}
		return instance;
	}
/**
 * Resets the singleton instance to null
 *
 * This method is only for testing purposes and allows the singleton to be reinitialized.
 * Not to be used in the main production code.
 */
	static void resetInstanceForTesting() {
		instance = null;
	}

	/**
	 * Retrieves the song history as an observable list of songs.
	 *
	 * @return an ObservableList containing the history of songs that have been played.
	 */
	public ObservableList<Song> getSongHistory() {
		return songHistory;
	}

	/**
	 * Retrieves the complete list of all songs available in the music player.
	 *
	 * @return an ObservableList containing all the songs.
	 */
	public ObservableList<Song> getAllSongs() {
		return allSongs;
	}

	/**
	 * Retrieves the list of playlists available in the music player.
	 *
	 * @return an ObservableList containing all the playlists.
	 */
	public ObservableList<Playlist> getPlaylists() {
		return playlists;
	}

	/**
	 * Retrieves the media player instance associated with the MusicPlayer.
	 *
	 * @return the MediaPlayer instance used for playing music.
	 */
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	/**
	 * Retrieves the file path for the song history.
	 *
	 * @return a String representing the path to the song history file.
	 */
	public String getSongHistoryFile() {
		return SONG_HISTORY_FILE;
	}

	/**
	 * Retrieves the file path for the all songs file.
	 *
	 * @return a String representing the path to the all songs file.
	 */
	public String getAllSongsFile() {
		return ALL_SONGS_FILE;
	}

	private double getSavedVolume() {
		Preferences preferences = Preferences.userNodeForPackage(MusicPlayer.class);
		return preferences.getDouble("volume", 100.0); // Default to 100% if no value is saved
	}


	/**
	 * Sets the labels and progress bar for the music player interface.
	 *
	 * @param songTitleLabel the label to display the song title
	 * @param totalTimeLabel the label to display the total time of the song
	 * @param progressBar    the progress bar to show the current position of the song
	 */
	public void setLabelsAndProgressBar(Label songTitleLabel, Label totalTimeLabel, Slider progressBar) {
		this.songTitleLabel = songTitleLabel;
		this.totalTimeLabel = totalTimeLabel;
		this.progressBar = progressBar;
	}


	/**
	 * Sets the MediaPlayer instance used by the MusicPlayer. Mostly used for testing purposes.
	 *
	 * @param mediaPlayer the MediaPlayer instance to be associated with the MusicPlayer.
	 */
	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

	/**
	 * Sets the volume for the media player and saves the volume preference.
	 *
	 * @param volume the desired volume level as a double, where 0.0 represents mute and 1.0 represents the maximum volume.
	 */
	public void setVolume(double volume){
		if(mediaPlayer != null) {
			mediaPlayer.setVolume(volume);
			saveVolumePreference(volume);
		}
	}

	public boolean getRandomStatus(){
		return randomToggle;
	}

	/**
	 * Saves the user's volume preference for the music player.
	 *
	 * @param volume the desired volume level as a double, where 0.0 represents mute and 1.0 represents the maximum volume.
	 */
	public void saveVolumePreference(double volume) {
		Preferences preferences = Preferences.userNodeForPackage(MusicPlayer.class);
		preferences.putDouble(VOLUME_PREF_KEY, volume);  // Save the volume setting
	}

	/**
	 * Loads the user's volume preference for the music player from the stored preferences.
	 *
	 * @return the volume preference as a double, where 1.0 is the default value if no preference is found
	 */
	public double loadVolumePreference() {
		Preferences preferences = Preferences.userNodeForPackage(MusicPlayer.class);
		return preferences.getDouble(VOLUME_PREF_KEY, 1.0);  // Default to 1.0 if no setting is found
	}



	/**
	 * Loads songs from a selected playlist.
	 *
	 * @param playlistName The name of the playlist to load songs from.
	 * @return An ObservableList of songs if the playlist is found, otherwise null.
	 */
	public ObservableList<Song> loadPlaylist(String playlistName) {
		currentPlaylist = playlists.stream().filter(playlist -> playlist.getName().equals(playlistName)).findFirst()
				.orElse(null);

		if (currentPlaylist != null) {
			return currentPlaylist.getSongs();
		}

		return null;
	}

	/**
	 * Loads playlists from a specified JSON file and updates the internal playlist list and provided playlist names list.
	 *
	 * @param playlistsFile the path to the JSON file that contains the playlists data.
	 * @param playlistNames a list where the names of the loaded playlists will be added.
	 */
	public void loadPlaylists(String playlistsFile, List<String> playlistNames) {
//    public void loadPlaylists(String playlistsFile ) {
		List<Playlist> loadedPlaylists = JsonHandler.loadPlaylistsFromJson(playlistsFile);
		if (loadedPlaylists != null) {
			playlists.addAll(loadedPlaylists);
			playlistNames.addAll(playlists.stream().map(Playlist::getName).toList());
		}
	}

	/**
	 * Handles the play and pause functionality for the media player.
	 *
	 * This method manages the play and pause actions based on the current
	 * state of the media player and the provided song selection. If no
	 * media player is active, and a song is selected, it initializes the
	 * media player with the selected song and starts playback. If the
	 * media player is currently playing, it pauses the playback. If the
	 * media player is paused, it resumes playback. Additionally, it updates
	 * the text of the play/pause button to reflect the current state.
	 *
	 * @param selectedSong the song selected for playback or for resuming
	 *                     from a paused state. This can be null if no song
	 *                     is selected.
	 * @param playPauseButton the button that toggles the play/pause state
	 *                        and whose text is updated based on the state
	 *                        of the media player.
	 */
	public void handlePlayPause(Song selectedSong, Button playPauseButton) {
		if (mediaPlayer == null) {
			if (selectedSong != null) {
				songTitleLabel.setText("Playing: " + selectedSong.getTitle());
				initializeMediaPlayer(selectedSong);
				mediaPlayer.play();

				// Update Play/Pause Icon to Pause
				Image pauseIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/pause.png")));
				((ImageView) playPauseButton.getGraphic()).setImage(pauseIcon);
			}
		} else if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
			mediaPlayer.pause();

			// Update Play/Pause Icon to Play
			Image playIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/play.png")));
			((ImageView) playPauseButton.getGraphic()).setImage(playIcon);
		} else {
			mediaPlayer.play();

			// Update Play/Pause Icon to Pause
			Image pauseIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/pause.png")));
			((ImageView) playPauseButton.getGraphic()).setImage(pauseIcon);
		}
	}

    /**
	 * Handles the action of playing the selected song in the media player, either from a paused state or from an uninitialized state.
	 *
	 * If the media player is null and a song is selected, it initializes the media player with the selected song and starts playback.
	 * If the media player is not null, it pauses the current playback, initializes the media player with the selected song, and starts playback again.
	 *
	 * @param selectedSong the song to be played. If null, no action is performed.
	 */
	public void handlePrevNext(Song selectedSong){
        if(mediaPlayer == null){
            if(selectedSong != null) {
                initializeMediaPlayer(selectedSong);
                mediaPlayer.play();
            }
        }
        else{
            mediaPlayer.pause();
            initializeMediaPlayer(selectedSong);
            mediaPlayer.play();
        }
    }


/**
 * Initializes the media player with the selected song and starts playback.
 *
 * This method creates a new Media instance using the file path of the selected song.
 * The song is added to the song history and a MediaPlayer is created for the media.
 * The media player is set to automatically play the song. The progress bar is also updated
 * in real-time based on the current playback time.
 *
 * @param selectedSong the song that is selected for playback.
 */
//Modified to add new the currently playing song to song history.
//Commented out code was causing minor visual glitch with song duration label.
	private void initializeMediaPlayer(Song selectedSong) {
		Media media = new Media(selectedSong.getFilePath());
		songHistory.add(selectedSong);
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setVolume(getSavedVolume());
		// mediaPlayer.setOnReady(() ->
		// totalTimeLabel.setText(formatTime(media.getDuration())));
		mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> progressBar
				.setValue((newTime.toSeconds() / media.getDuration().toSeconds()) * 100));
        mediaPlayer.setAutoPlay(true);
    }


	/**
	 * Plays a song from the song history using the media player.
	 *
	 * If the media player is not null, it stops the current playback.
	 * Then, it initializes the media player with the provided song and starts playback.
	 *
	 * @param song the song to be played from the history.
	 */
	public void playSongFromHistory(Song song) {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
		initializeMediaPlayer(song);
		mediaPlayer.play();
	}

	/**
	 * Formats a Duration object into a string representation with the format "m:ss".
	 *
	 * @param duration the duration to be formatted, represented as a Duration object.
	 * @return a string representing the formatted time in minutes and seconds.
	 */
	private String formatTime(Duration duration) {
		int minutes = (int) duration.toMinutes();
		int seconds = (int) (duration.toSeconds() % 60);
		return String.format("%d:%02d", minutes, seconds);
	}

	/**
	 * Creates a new playlist with the specified name and adds it to the list of playlists.
	 * The updated list of playlists is then saved to a JSON file named "playlists.json".
	 *
	 * @param name the name of the new playlist to be created.
	 */
	public void createPlaylist(String name) {
		Playlist newPlaylist = new Playlist(name);
		playlists.add(newPlaylist);
		JsonHandler.savePlaylistsToJson(playlists, "playlists.json");
	}

	/**
	 * Searches for playlists whose names match or contain the provided playlist name.
	 * If no playlist name is provided, it returns all playlist names.
	 *
	 * @param playlistName the name or partial name of the playlist to search for.
	 * @return a list of playlist names that match the search criteria. If the playlistName
	 *         is null or empty, returns all available playlist names.
	 */
	/*
	 * TODO rename?
	 */
	public List<String> searchUpdatePlaylistView(String playlistName) {
		List<String> playlistNames = new ArrayList<>();
		if ((playlistName == null) || playlistName.isEmpty()) {

			for (Playlist playlist : playlists) {
				playlistNames.add(playlist.getName());
			}
		} else {
			for (Playlist playlist : playlists) {
				if (playlist.getName().toLowerCase().contains(playlistName.toLowerCase())) {
					playlistNames.add(playlist.getName());
				}
			}
		}

		return playlistNames;
	}

	/**
	 * Toggles the randomize boolean which affects if the next song is the next
	 * in the list or a random selection.
	 */
	public void toggleRandomize() {
//		System.out.println("Before toggle: " + randomToggle);
		randomToggle = !randomToggle;
//		System.out.println("After toggle: " + randomToggle);
	}

}

