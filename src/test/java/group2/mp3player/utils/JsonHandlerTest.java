package group2.mp3player.utils;

import com.google.gson.Gson;
import group2.mp3player.model.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonHandlerTest {

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test", ".json");
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testSaveToJsonAndLoadFromJson() {
        ObservableList<Song> songs = FXCollections.observableArrayList(
                new Song("Song1", "Artist1", "Album1", "2020", "path/to/song1.mp3"),
                new Song("Song2", "Artist2", "Album2", "2021", "path/to/song2.mp3")
        );

        // Save songs to JSON
        JsonHandler.saveToJson(songs, tempFile.getAbsolutePath());

        // Load songs back from JSON
        List<Song> loadedSongs = JsonHandler.loadFromJson(tempFile.getAbsolutePath());

        assertNotNull(loadedSongs);
        assertEquals(2, loadedSongs.size());
        assertEquals("Song1", loadedSongs.get(0).getTitle());
        assertEquals("Song2", loadedSongs.get(1).getTitle());
    }

    @Test
    void testLoadFromJson_FileNotFound() {
        // Attempt to load from a non-existing file
        List<Song> result = JsonHandler.loadFromJson("nonexistent.json");
        assertNull(result, "Expected result to be null for a nonexistent file");
    }

    @Test
    void testClearSongHistoryFromJson() {
        ObservableList<Song> songs = FXCollections.observableArrayList(
                new Song("Song1", "Artist1", "Album1", "2020", "path/to/song1.mp3")
        );

        // Save songs to JSON
        JsonHandler.saveToJson(songs, tempFile.getAbsolutePath());

        // Clear song history
        JsonHandler.clearSongHistoryFromJson(tempFile.getAbsolutePath());

        // Verify the file is cleared
        try (FileReader reader = new FileReader(tempFile)) {
            Gson gson = new Gson();
            Song[] emptySongs = gson.fromJson(reader, Song[].class);
            assertEquals(0, emptySongs.length, "Expected the JSON to be cleared");
        } catch (IOException e) {
            fail("Failed to read cleared JSON file");
        }
    }
}
