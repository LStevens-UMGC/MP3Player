package group2.mp3player.model;

import group2.mp3player.model.MusicPlayer;
import group2.mp3player.model.Song;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicPlayerTest {

    private MusicPlayer musicPlayer;

    @BeforeEach
    void setUp() {
        musicPlayer = MusicPlayer.getInstance();
    }

    @Test
    void testSingletonInstance() {
        MusicPlayer anotherInstance = MusicPlayer.getInstance();
        assertSame(musicPlayer, anotherInstance, "Expected the same instance of MusicPlayer");
    }

    @Test
    void testSetLabelsAndProgressBar() {
        Label songTitleLabel = new Label();
        Label totalTimeLabel = new Label();
        Slider progressBar = new Slider();

        musicPlayer.setLabelsAndProgressBar(songTitleLabel, totalTimeLabel, progressBar);

//        assertEquals(songTitleLabel, musicPlayer.songTitleLabel);
//        assertEquals(totalTimeLabel, musicPlayer.totalTimeLabel);
//        assertEquals(progressBar, musicPlayer.progressBar);
    }

    @Test
    void testAddSongToHistory() {
        Song song = new Song("Title", "Artist", "Album", "2023", "/path/to/file.mp3");
        musicPlayer.getSongHistory().add(song);

        ObservableList<Song> songHistory = musicPlayer.getSongHistory();
        assertTrue(songHistory.contains(song), "Song should be added to song history");
    }

    @Test
    void testLoadPlaylist() {
        // Assuming we have a playlist created and added to the MusicPlayer instance
        musicPlayer.createPlaylist("Test Playlist");

        ObservableList<Song> songs = musicPlayer.loadPlaylist("Test Playlist");
        assertNotNull(songs, "Expected non-null result for existing playlist");
    }

    @Test
    void testSearchUpdatePlaylistView() {
        musicPlayer.createPlaylist("Rock Classics");
        musicPlayer.createPlaylist("Pop Hits");

        List<String> searchResults = musicPlayer.searchUpdatePlaylistView("Rock");
        assertEquals(1, searchResults.size());
        assertEquals("Rock Classics", searchResults.get(0));
    }

    @Test
    void testHandlePlayPauseWithNoMediaPlayer() {
        Song song = new Song("Title", "Artist", "Album", "2023", "/path/to/file.mp3");
        Button playPauseButton = new Button("Play");

        // Mock MediaPlayer instance
        MediaPlayer mediaPlayerMock = Mockito.mock(MediaPlayer.class);
        musicPlayer.setMediaPlayer(mediaPlayerMock);

        musicPlayer.handlePlayPause(song, playPauseButton);

        verify(mediaPlayerMock, times(1)).play();
        assertEquals("Pause", playPauseButton.getText(), "Expected button text to change to 'Pause'");
    }
}

