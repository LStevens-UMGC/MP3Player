package group2.mp3player.model;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static group2.mp3player.model.MusicPlayer.VOLUME_PREF_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicPlayerTest {

    private MusicPlayer musicPlayer;

    @BeforeEach
    void setUp() {
        musicPlayer = MusicPlayer.getInstance();
    }

    //Test case # MP1
    @Test
    void testSingletonInstance() {
        //Purpose: Checks that calls to MusicPlayer.getInstance(); returns the same instance
        MusicPlayer anotherInstance = MusicPlayer.getInstance();
        assertSame(musicPlayer, anotherInstance, "Expected the same instance of MusicPlayer");
    }

    //Test case # MP2
    @Test
    void testSingletonInstanceWhenNull() {
        //Purpose: Checks the singleton instance is null before initialization
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

    //Test case # MP3
    @Test
    void testSingletonPreventsReflectionInstatiation() {
        //Purpose: Checks that the singleton design prevents the creation of a second instance
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

    //Test case # MP4
    @Test
    void testSetLabelsAndProgressBar() {
        //Purpose: Check that the Label and slider are set corectly in MediaPlayer when setLabelsandProgressBar is called
        //Gets JavaFX ready for testing
        Platform.startup(() -> {
        });
        // Had issues with JavaFX threading rules in the test only
        Platform.runLater(() -> {
            Label songTitleLabel = new Label();
            Label totalTimeLabel = new Label();
            Slider progressBar = new Slider();

            musicPlayer.setLabelsAndProgressBar(songTitleLabel, totalTimeLabel, progressBar);

            assertEquals(songTitleLabel, musicPlayer.songTitleLabel);
            assertEquals(totalTimeLabel, musicPlayer.totalTimeLabel);
            assertEquals(progressBar, musicPlayer.progressBar);
        });
    }

    //Test case # MP5
    @Test
    void testSetLabelsAndProgressBarHandlesNullInputs() {
        //Purpose: Checks if the label and slider update when the setLabelAndProgressBar method is called
        Platform.startup(() -> {
        });
        Platform.runLater(() -> {

            Label songTitleLabel = new Label("Old Title");
            Label totalTimeLabel = new Label("00:00");
            Slider progressBar = new Slider(0, 100, 25);

            musicPlayer.setLabelsAndProgressBar(songTitleLabel, totalTimeLabel, progressBar);

            musicPlayer.setLabelsAndProgressBar(null, null, null);

            assertNull(songTitleLabel.getText(), "songTitleLabel text to be null");
            assertNull(totalTimeLabel.getText(), "totalTimeLabel text to be null");
            assertEquals(0, progressBar.getValue(), "progressBar's value to reset to 0");
        });
    }

    //Test case # MP6
    @Test
    void testSetLabelsAndProgressBarUpdatesValues() {
        // Purpose: Verify that previously set Label and Slider objects are updated to new inputs
        Platform.startup(() -> {
        });
        Platform.runLater(() -> {
            //Old values
            Label oldTitleLabel = new Label("Old Title");
            Label oldTimeLabel = new Label("00:00");
            Slider oldProgressBar = new Slider(0, 100, 25);

            musicPlayer.setLabelsAndProgressBar(oldTitleLabel, oldTimeLabel, oldProgressBar);

            //new values
            Label newTitleLabel = new Label("New Title");
            Label newTimeLabel = new Label("03:45");
            Slider newProgressBar = new Slider(0, 100, 75);

            assertEquals(newTitleLabel, musicPlayer.songTitleLabel, "Old title label to be updated to new title label");
            assertEquals(newTimeLabel, musicPlayer.totalTimeLabel, "Old time label to be updated to new time label");
            assertEquals(newProgressBar, musicPlayer.progressBar, "Old progress bar to be updated to new progress bar");

        });
    }

    //Test case # MP7
    @Test
    void testSetVolumeSetsTheVolume() {
        //Purpose: The setVolume method correctly adjusts the volume of the MediaPlayer
        Platform.startup(() -> {
        });
        Platform.runLater(() -> {
            String path = getClass().getResource("/group2/mp3player/AudioTestFiles/1KHz.mp3").toExternalForm();

            Media media = new Media(path);
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            musicPlayer.setMediaPlayer(mediaPlayer);

            //Set the volume to 0.5
            musicPlayer.setVolume(0.5);

            assertEquals(0.5, mediaPlayer.getVolume(), 0.01, "Volume to be set to 0.5");
        });
    }

    //Test case #MP8
    @Test
    void testSetVolumeNoInvalidValues(){
        //Purpose: Checks if an exception is thrown when an invalid value is added
        Platform.startup(() -> {});
        Platform.runLater(() -> {
            String path = getClass().getResource("/group2/mp3player/AudioTestFiles/1KHz.mp3").toExternalForm();
            Media media = new Media(path);
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            musicPlayer.setMediaPlayer(mediaPlayer);

            musicPlayer.setVolume(-1.5);

            assertTrue(mediaPlayer.getVolume() >= 0.0 && mediaPlayer.getVolume() <= 1.0);
        });

    }
//Test case # MP9
    @Test
    void testSaveVolumeAndLoadVolumePreference(){
        //Purpose: Checks that the save volume values are retrieved correctly
        Platform.startup(() -> {});
        Platform.runLater(() -> {
            double savedVolume = 0.5;
            musicPlayer.saveVolumePreference(savedVolume);

            double modifyVolume = 0.54;
            musicPlayer.saveVolumePreference(modifyVolume);


            double retrieveVolume = musicPlayer.loadVolumePreference();

            assertEquals(modifyVolume, retrieveVolume, 0.01);
            musicPlayer.saveVolumePreference(savedVolume);
            retrieveVolume = musicPlayer.loadVolumePreference();
            assertEquals(savedVolume, retrieveVolume, 0.01);

        });
    }

    //Test Case # MP10
    @Test
    void testLoadVolumePreferenceReturnsDefaultValue(){
        //Purpose: Checks if the loadVolumePreference method returns the default value
        //when no preferences are saved.
        Platform.startup(() -> {});
        Platform.runLater(() -> {
            //Clear any saved volumes
            Preferences preferences = Preferences.userNodeForPackage(MusicPlayer.class);
            preferences.remove(VOLUME_PREF_KEY);

            double defaultVolume = musicPlayer.loadVolumePreference();

            assertEquals(1.0, defaultVolume, 0.01);
        });
    }

    //Test cases # MP11
    @Test
    void testSaveVolumePreferencesPreviousSave(){
        //Purpose: Checks that the new save value overwrites the previously saved value
        Platform.startup(() -> {});
        Platform.runLater(() -> {

            double oldVolume = 0.3;
            musicPlayer.saveVolumePreference(oldVolume);

            double newVolume = 0.7;
            musicPlayer.saveVolumePreference(newVolume);

            double retrieveVolume = musicPlayer.loadVolumePreference();
            assertEquals(newVolume, retrieveVolume, 0.01);

        });
    }

    //Test case # MP12
    @Test
    void testLoadPlaylist() {
        //Checks that when the loadPlaylist method is called for a playlist a non-null result is returned
        musicPlayer.createPlaylist("Test Playlist");

        ObservableList<Song> songs = musicPlayer.loadPlaylist("Test Playlist");
        assertNotNull(songs, "Expected non-null result for existing playlist");
    }

    //Test case #MP13
    @Test
    void testLoadPlaylistWhenPlaylistNameDoesNotExist() {
        //Purpose: loadPlaylist method returns null when the playlist name does not exist
        //Clear Playlists
        musicPlayer.getPlaylists().clear();
        //Check the playlist list is empty
        assertTrue(musicPlayer.getPlaylists().isEmpty(), "Expected playlists to be empty before the test.");

        //Input of a playlist name that does not exist
        ObservableList<Song> songs = musicPlayer.loadPlaylist("Test playlist");

        //Assert that the expected result is null
        assertNull(songs, "Expected loadPlaylist to return null for a non-existing playlist name.");
    }

    //Test case #MP14
    @Test
    void testLoadPlaylistWithNullName() {
        //Purpose: Checks that loadPlaylist handles a null playlist name
        //Load a playlist with a null name
        ObservableList<Song> songs = musicPlayer.loadPlaylist(null);

        assertNull(songs, "Expected loadPlaylist to return null for a null playlist name.");
    }

    //Test case # MP15

    @Test
    void testAddSongToHistory() {
        Song song = new Song("Title", "Artist", "Album", "2023", "/path/to/file.mp3");
        musicPlayer.getSongHistory().add(song);

        ObservableList<Song> songHistory = musicPlayer.getSongHistory();
        assertTrue(songHistory.contains(song), "Song should be added to song history");
    }


    @Test
    void testSearchUpdatePlaylistView() {
        musicPlayer.createPlaylist("Rock Classics");
        musicPlayer.createPlaylist("Pop Hits");

        List<String> searchResults = musicPlayer.searchUpdatePlaylistView("Rock");
        assertEquals(1, searchResults.size());
        assertEquals("Rock Classics", searchResults.get(0));
    }

    //Test Case #MP15
    @Test
    void testHandlePlayPauseWhenMediaPlayerIsNull(){
      //Purpose: Checks when media player is null
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {});
        }
        Platform.runLater(() -> {
            Button playPauseButton = new Button();
            playPauseButton.setGraphic(new ImageView());

            Song selectedSong = new Song("Test Title", "Test Artist", "Test Album", "2023", "group2/mp3player/AudioTestFiles/1KHz.mp3");

            assertNull(musicPlayer.getMediaPlayer(), "Expected mediaPlayer to be null initially.");
            musicPlayer.handlePlayPause(selectedSong, playPauseButton);

            assertNotNull(musicPlayer.getMediaPlayer(), "Expected mediaPlayer to be initialized.");
        });
    }

    //Test case #MP16
    @Test
    void testHandlePlayPauseMediaAndUpdate(){
        //Purpose: Checks that handlePlayPause method pauses the media and the button test updates
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {});
        }
        Platform.runLater(() -> {
            Button playPauseButton = new Button("Pause");
            playPauseButton.setGraphic(new ImageView());

            //Mock media player
            MediaPlayer mediaPlayer = mock(MediaPlayer.class);

            //Check that it returns the status as Playing
            when(mediaPlayer.getStatus()).thenReturn(MediaPlayer.Status.PLAYING);

            musicPlayer.handlePlayPause(null, playPauseButton);
            //Check
            verify(mediaPlayer, times(1)).pause();

            assertEquals("Play", playPauseButton.getText(), "Expected playPauseButton text to update to 'Play' after pausing.");


            verify(mediaPlayer, times(1)).pause();


        });
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

