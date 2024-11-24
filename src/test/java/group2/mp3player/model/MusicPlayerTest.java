package group2.mp3player.model;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.prefs.Preferences;

import static group2.mp3player.model.MusicPlayer.VOLUME_PREF_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicPlayerTest {

    private MusicPlayer musicPlayer;

    static {
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {
            });
        }
    }

    @BeforeEach
    void setUp() {
        musicPlayer = MusicPlayer.getInstance();
    }

    @AfterEach
    void cleanUp() {
        musicPlayer.getPlaylists().clear();
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

    //Test Case #MP15
    @Test
    void testHandlePlayPauseWhenMediaPlayerIsNull(){
      //Purpose: Checks when media player is null
        Platform.runLater(() -> {
            Button playPauseButton = new Button();
            playPauseButton.setGraphic(new ImageView());

            Song selectedSong = new Song("Test Title", "Test Artist", "Test Album", "2023", "group2/mp3player/AudioTestFiles/1KHz.mp3");

            assertNull(musicPlayer.getMediaPlayer(), "Expected mediaPlayer to be null initially.");
            musicPlayer.handlePlayPause(selectedSong, playPauseButton);

            assertNotNull(musicPlayer.getMediaPlayer(), "Expected mediaPlayer to be initialized.");
        });
    }

    @Test
    void testHandlePlayPauseWithNoMediaPlayer() {
        Platform.runLater(() -> {
            Song song = new Song("Title", "Artist", "Album", "2023", "/path/to/file.mp3");
            Button playPauseButton = new Button("Play");

            // Mock MediaPlayer instance
            MediaPlayer mediaPlayerMock = Mockito.mock(MediaPlayer.class);
            musicPlayer.setMediaPlayer(mediaPlayerMock);

            musicPlayer.handlePlayPause(song, playPauseButton);

            verify(mediaPlayerMock, times(1)).play();
            assertEquals("Pause", playPauseButton.getText(), "Expected button text to change to 'Pause'");
        });
    }

    //Test case #MP16
    @Test
    void testHandlePlayPauseMediaAndUpdate(){
        //Purpose: Checks that handlePlayPause method pauses the media and the button text updates to Play
        Platform.runLater(() -> {
            Button playPauseButton = new Button("Pause");
            playPauseButton.setGraphic(new ImageView());

            //Mock media player
            MediaPlayer mediaPlayer = mock(MediaPlayer.class);

            //Check that it returns the status as Playing
            when(mediaPlayer.getStatus()).thenReturn(MediaPlayer.Status.PLAYING);

            musicPlayer.handlePlayPause(null, playPauseButton);

            //Check that mediaPlayer.pause was called
            verify(mediaPlayer, times(1)).pause();

            assertEquals("Play", playPauseButton.getText(), "Expected playPauseButton text to update to 'Play' after pausing.");
            verify(mediaPlayer, times(1)).pause();

        });
    }

    //Test case #MP17
    @Test
    void testHandlePlayPausePlayMediaAndUpdate(){
        //Purpose: Checks that playback resumes and the button text updates to pause
        Platform.runLater(() -> {
            Button playPauseButton = new Button("Play");
            playPauseButton.setGraphic(new ImageView());
            //Mock the media player
            MediaPlayer mediaPlayer = mock(MediaPlayer.class);
            when(mediaPlayer.getStatus()).thenReturn(MediaPlayer.Status.PAUSED);

            musicPlayer.handlePlayPause(null, playPauseButton);
            verify(mediaPlayer, times(1)).play();
            assertEquals("Pause", playPauseButton.getText(), "Expected playPauseButton text to update to 'Pause' after resuming playback.");
        });

    }

    //Test case #MP18
    @Test
    void testHandlePlayPauseNullButton(){
        //Purpose: Check that the method handles a null playPauseButton
        Platform.runLater(() -> {
            Label songTitleLabel = new Label();
            Label totalTimeLabel = new Label();
            Slider progressBar = new Slider();

            musicPlayer.setLabelsAndProgressBar(songTitleLabel, totalTimeLabel, progressBar);
            Song selectedSong = new Song("Test Song", "Test Artist", "Test Album", "2023", "group2/mp3player/AudioTestFiles/1KHz.mp3");

            assertNull(musicPlayer.getMediaPlayer(), "Expected mediaPlayer to be null initially.");
            musicPlayer.handlePlayPause(selectedSong, null);
            assertNotNull(musicPlayer.getMediaPlayer(), "Expected mediaPlayer to be initialized.");
            assertEquals("Playing: Test Song", songTitleLabel.getText(), "Expected songTitleLabel to be updated with the song title.");
        });
    }

    //Test Case #MP19
    @Test
    void testHandlePrevNext(){
        //Purpose: Check that Previous button changes songs correctly when a song is playing
        Platform.runLater(() -> {
        Song currentSong = new Song("Test Song", "Test Artist", "Test Album", "2023", "group2/mp3player/AudioTestFiles/1KHz.mp3");
        Song newSong = new Song("New Test Song", "New Artist", "New Album", "2024", "group2/mp3player/AudioTestFiles/2KHz.mp3");

            MediaPlayer mockMediaPlayer = mock(MediaPlayer.class);
            musicPlayer.setMediaPlayer(mockMediaPlayer);

            //simulate a song playing
            musicPlayer.handlePrevNext(currentSong);
            verify(mockMediaPlayer, times(0)).pause();
            assertNotNull(musicPlayer.getMediaPlayer(), "Expected mediaPlayer to be initialized.");
            verify(musicPlayer.getMediaPlayer(), times(1)).play();

            musicPlayer.handlePrevNext(newSong);
            verify(mockMediaPlayer, times(1)).pause();
            assertNotNull(musicPlayer.getMediaPlayer(), "Expected new mediaPlayer to be initialized.");
            verify(musicPlayer.getMediaPlayer(), times(2)).play();
    });
    }

    //Test case #MP20
    @Test
    void testCreatePlaylist() {
        //Purpose: Checks that playlists are created correctly
        musicPlayer.createPlaylist("Rock Classics");
        musicPlayer.createPlaylist("Pop Hits");

        List<String> playlistNames = musicPlayer.getPlaylists().stream()
                .map(Playlist::getName)
                .toList();

        assertEquals(2, playlistNames.size());
        assertEquals("Rock Classics", playlistNames.get(0));
        assertEquals("Pop Hits", playlistNames.get(1));
    }

    //Test case #MP21
    @Test
    void testCreateDuplicatePlaylist() {
        //Purpose: Checks if the createPlaylist method creates duplicate playlists
        musicPlayer.getPlaylists().clear();
        musicPlayer.createPlaylist("Rock Classics");
        musicPlayer.createPlaylist("Rock Classics");

        List<String> playlistNames = musicPlayer.getPlaylists().stream()
                .map(Playlist::getName)
                .toList();
        assertEquals(2, playlistNames.size());
        assertEquals("Rock Classics", playlistNames.get(0), "Expected the first playlist to be 'Rock Classics'.");
        assertEquals("Rock Classics", playlistNames.get(1), "Expected the second playlist to be 'Rock Classics'.");
    }

    //Test case #MP22
    @Test
    void testCreatePlaylistWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> musicPlayer.createPlaylist(null));
    }

    //Test case #MP23
    @Test
    void testAddSongToHistory() {
        //Purpose: Checks that the song is successfully added to song history
        Song song = new Song("Test Song", "Test Artist", "Test Album", "2023", "group2/mp3player/AudioTestFiles/1KHz.mp3");
        musicPlayer.getSongHistory().add(song);

        ObservableList<Song> songHistory = musicPlayer.getSongHistory();
        assertTrue(songHistory.contains(song), "Song should be added to song history");
    }

    //Test case #MP24
    @Test
    void testPlaySongFromHistoryInitializesWhenNull(){
        //Purpose: Verify that the MediaPlayer initializes when null and plays a song from history
        Platform.runLater(() -> {
            Song song = new Song("Test Song", "Test Artist", "Test Album", "2023",
                    getClass().getResource("/group2/mp3player/AudioTestFiles/1KHz.mp3").toExternalForm());
           //MediaPlayer is null
            musicPlayer.setMediaPlayer(null);
            assertNull(musicPlayer.getMediaPlayer(), "Expected mediaPlayer to be null initially.");

            musicPlayer.playSongFromHistory(song);
            assertNotNull(musicPlayer.getMediaPlayer(), "Expected mediaPlayer to be initialized.");
            verify(musicPlayer.getMediaPlayer(), times(1)).play();
        });
    }

    //Test Case #MP25
    @Test
    void testPlaySongFromHistoryInvalidSong(){
        //Purpose: playSongFromHistory throws an exception when playing a song with an invalid file path
        Platform.runLater(() -> {
            Song invalidSong = new Song("Invalid Title","Invalid Artist","Invalid Album","Invalid Year","invalid/path/to/nonexistent-file.mp3");
            musicPlayer.setMediaPlayer(null);
            assertNull(musicPlayer.getMediaPlayer(), "Expected mediaPlayer to be null initially.");

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                musicPlayer.playSongFromHistory(invalidSong);
            });

            String expectedMessage = "Error initializing media player file: invalid/path/to/nonexistent-file.mp3";
            assertTrue(exception.getMessage().contains(expectedMessage),
                    "Expected exception message to contain: " + expectedMessage + ", but got: " + exception.getMessage());

        });
    }

    //Test Case #MP26
    @Test
    void testPlaySongFromHistoryWithCorruptedSong(){
       //Purpose: Verify that playSongFromHistory handles errors when the file is corrupted
        Platform.runLater(() -> {
            Song corruptedSong = new Song("Corrupted Song","Test Artist","Test Album","2023","group2/mp3player/AudioTestFiles/corrupted.mp3");

            Exception exception = assertThrows(RuntimeException.class, () -> {
                musicPlayer.playSongFromHistory(corruptedSong);
            });

            String expectedMessage = "Error initializing media player file: group2/mp3player/AudioTestFiles/corrupted.mp3";
            assertTrue(exception.getMessage().contains(expectedMessage),
                    expectedMessage + ", but got: " + exception.getMessage());
        });
    }

    //Test case #MP27
    @Test
    void testSearchUpdatePlaylistView() {
        //Purpose: Verify that playlists are searched correctly based on search terms
        musicPlayer.createPlaylist("Rock Classics");
        musicPlayer.createPlaylist("Pop Hits");

        List<String> searchResults = musicPlayer.searchPlaylistByName("Rock");
        assertEquals(1, searchResults.size());
        assertEquals("Rock Classics", searchResults.get(0));
    }

    //Test Case #MP28
    @Test
    public void testSearchPlaylistsByNameEmptyQuery() throws Exception {
        //Purpose: Verify that all playlists are returned when the search query is empty
        musicPlayer.createPlaylist("Rock Classics");
        musicPlayer.createPlaylist("Pop Hits");
        musicPlayer.createPlaylist("Country Hits");

        List<String> searchResults = musicPlayer.searchPlaylistByName("");

        assertEquals(3, searchResults.size());
        assertEquals(List.of("Rock Classics", "Pop Hits", "Country Hits"), searchResults);
    }

    //Test Case #MP29
    @Test
    void testSearchPlaylistsByNameDuplicateNames() {
        //Purpose: Checks that duplicate playlist are retuned when searched
        musicPlayer.getPlaylists().clear();
        musicPlayer.createPlaylist("Rock Classics");
        musicPlayer.createPlaylist("Rock Classics");
        musicPlayer.createPlaylist("Pop Hits");

        List<String> searchResults = musicPlayer.searchPlaylistByName("Rock");

        assertEquals(2, searchResults.size());
        musicPlayer.getPlaylists().clear();
    }

    //Test Case #MP30
    @Test
    void testSearchPlaylistsByNameWhenCaseSensitive() {
        //Purpose: Checks that playlists are returned if they are case sensitive
        musicPlayer.createPlaylist("Rock Classics");
        musicPlayer.createPlaylist("Pop Hits");

        List<String> searchResults = musicPlayer.searchPlaylistByName("rock");

        assertEquals(1, searchResults.size());
        assertEquals("Rock Classics", searchResults.get(0));
    }




}

