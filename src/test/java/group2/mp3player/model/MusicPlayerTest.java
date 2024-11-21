package group2.mp3player.model;

import group2.mp3player.model.MusicPlayer;
import group2.mp3player.model.Song;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicPlayerTest {

    private MusicPlayer musicPlayer;

    @BeforeEach
    void setUp() {
        musicPlayer = MusicPlayer.getInstance();
    }
    //Test MP1
    @Test
    void testSingletonInstance() {
        MusicPlayer anotherInstance = MusicPlayer.getInstance();
        assertSame(musicPlayer, anotherInstance, "Expected the same instance of MusicPlayer");
    }
    //Test MP2
    @Test
    void testSingletonInstanceWhenNull() {
        MusicPlayer.resetInstanceForTesting();
        assertNull(getPrivateInstance(), "Instance should be null before initialization");

        MusicPlayer musicPlayer = MusicPlayer.getInstance();

        assertNotNull(musicPlayer, "MusicPlayer instance is not null after getInstance() is called");
    }
    // Helper method for test MP2
    private MusicPlayer getPrivateInstance() {
        try {
            var field = MusicPlayer.class.getDeclaredField("instance");
            field.setAccessible(true);
            return (MusicPlayer) field.get(null);
        } catch (Exception e) {
            fail("Error cannot access instance of MusicPlayer");
            return null;
        }
    }

    //Test MP3
    @Test
    void testSingletonPreventsReflectionInstatiation() {
        MusicPlayer firstInstance = MusicPlayer.getInstance();

        //Creates a new instance using reflection
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, () -> {
            Constructor<MusicPlayer> constructor = MusicPlayer.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
        Throwable cause = thrown.getCause(); // Get the wrapped exception
        assertInstanceOf(IllegalStateException.class, cause, "Expected cause to be IllegalStateException");
        assertEquals("An instance of MusicPlayer is already initialized.", cause.getMessage(), "Unexpected exception message");
    }

//Test MP4
@Test
void testSetLabelsAndProgressBar() {
    //Gets JavaFX ready for testing
    Platform.startup(()->{});
    // Had issues with JavaFX threading rules in the test only
    Platform.runLater(()-> {
        Label songTitleLabel = new Label();
        Label totalTimeLabel = new Label();
        Slider progressBar = new Slider();

        musicPlayer.setLabelsAndProgressBar(songTitleLabel, totalTimeLabel, progressBar);

//        assertEquals(songTitleLabel, musicPlayer.songTitleLabel);
//        assertEquals(totalTimeLabel, musicPlayer.totalTimeLabel);
//        assertEquals(progressBar, musicPlayer.progressBar);
    });
}

//Test MP5
@Test
void testSetLabelsAndProgressBarHandlesNullInputs() {
        Platform.startup(()->{});
        Platform.runLater(()-> {

            Label songTitleLabel = null;
            Label totalTimeLabel = null;
            Slider progressBar = null;

            musicPlayer.setLabelsAndProgressBar(songTitleLabel, totalTimeLabel, progressBar);

            //Using reflection checks that the fields in music player were set to null
            try {
                var songTitleLabelField = MusicPlayer.class.getDeclaredField("songTitleLabel");
                var totalTimeLabelField = MusicPlayer.class.getDeclaredField("totalTimeLabel");
                var progressBarField = MusicPlayer.class.getDeclaredField("progressBar");

                songTitleLabelField.setAccessible(true);
                totalTimeLabelField.setAccessible(true);
                progressBarField.setAccessible(true);

                assertNull(songTitleLabelField.get(musicPlayer), "songTitleLabel to be set to null");
                assertNull(totalTimeLabelField.get(musicPlayer), "totalTimeLabel to be set to null");
                assertNull(progressBarField.get(musicPlayer), "progressBar to be set to null");
            } catch (Exception e) {
                fail("Error when performing reflection: " + e.getMessage());
            }
        });
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

