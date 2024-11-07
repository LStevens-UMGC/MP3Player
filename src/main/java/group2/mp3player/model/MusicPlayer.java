package group2.mp3player.model;

import group2.mp3player.utils.JsonHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;

public class MusicPlayer {
    private static final MusicPlayer instance = new MusicPlayer();

    private final ObservableList<Song> songHistory = FXCollections.observableArrayList();
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();
    private Playlist currentPlaylist;
    private static final String SONG_HISTORY_FILE = "songHistory.json";
    private MediaPlayer mediaPlayer;

    private Label songTitleLabel;
    private Label totalTimeLabel;
    private Slider progressBar;

    private MusicPlayer() {}

    public static MusicPlayer getInstance() {
        return instance;
    }

    public ObservableList<Song> getSongHistory() {
        return songHistory;
    }

    public ObservableList<Playlist> getPlaylists() {
        return playlists;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public String getSongHistoryFile() {
        return SONG_HISTORY_FILE;
    }

    public void setLabelsAndProgressBar(Label songTitleLabel, Label totalTimeLabel, Slider progressBar) {
        this.songTitleLabel = songTitleLabel;
        this.totalTimeLabel = totalTimeLabel;
        this.progressBar = progressBar;
    }

    public void initialize() {
        songHistory.addAll(Objects.requireNonNull(JsonHandler.loadFromJson(SONG_HISTORY_FILE)));
    }

    // Load songs from a selected playlist
    public ObservableList<Song> loadPlaylist(String playlistName) {
        currentPlaylist = playlists.stream().filter(playlist -> playlist.getName().equals(playlistName)).findFirst()
                .orElse(null);

        if (currentPlaylist != null) {
            return currentPlaylist.getSongs();
        }

        return null;
    }


    public void loadPlaylists(String playlistsFile, List<String> playlistNames) {
//    public void loadPlaylists(String playlistsFile ) {
        List<Playlist> loadedPlaylists = JsonHandler.loadPlaylistsFromJson(playlistsFile);
        if (loadedPlaylists != null) {
            playlists.addAll(loadedPlaylists);
            playlistNames.addAll(playlists.stream().map(Playlist::getName).toList());
        }
    }

    public void handlePlayPause(Song selectedSong, Button playPauseButton) {
        if (mediaPlayer == null) {
            if (selectedSong != null) {
                songTitleLabel.setText("Playing: " + selectedSong.getTitle());
                initializeMediaPlayer(selectedSong);
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

    private void initializeMediaPlayer(Song selectedSong) {
        Media media = new Media(selectedSong.getFilePath());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> totalTimeLabel.setText(formatTime(media.getDuration())));
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) ->
                progressBar.setValue((newTime.toSeconds() / media.getDuration().toSeconds()) * 100)
        );
    }

    public void playSongFromHistory(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        initializeMediaPlayer(song);
        mediaPlayer.play();
    }

    private String formatTime(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    public void createPlaylist(String name) {
        Playlist newPlaylist = new Playlist(name);
        playlists.add(newPlaylist);
        JsonHandler.savePlaylistsToJson(playlists, "playlists.json");
    }
}