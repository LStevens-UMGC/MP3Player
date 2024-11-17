//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package group2.mp3player.utils;

import com.google.gson.Gson;
import group2.mp3player.model.Song;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class JsonHandlerTest {
    private File tempFile;

    JsonHandlerTest() {
    }

    @BeforeEach
    void setUp() throws IOException {
        this.tempFile = File.createTempFile("test", ".json");
        if (this.tempFile.exists()){
            System.out.println("file found");
        }
    }

    @AfterEach
    void tearDown() {
        if (this.tempFile.exists()) {
            this.tempFile.delete();
        }
        if(!this.tempFile.exists()){
            System.out.println("file deleted");
        }
    }

    @Test
    void testSaveToJsonAndLoadFromJson() {
        ObservableList<Song> songs = FXCollections.observableArrayList(new Song("Song1", "Artist1", "Album1", "2020", "path/to/song1.mp3"), new Song("Song2", "Artist2", "Album2", "2021", "path/to/song2.mp3"));

        System.out.println(songs);

        JsonHandler.saveToJson(songs, this.tempFile.getAbsolutePath());
        List<Song> loadedSongs = JsonHandler.loadFromJson(this.tempFile.getAbsolutePath());

        System.out.println(loadedSongs + " " + loadedSongs.size());

        Assertions.assertNotNull(loadedSongs);
        Assertions.assertEquals(2, loadedSongs.size());
        Assertions.assertEquals("Song1", loadedSongs.get(0).getTitle());
        Assertions.assertEquals("Song2", loadedSongs.get(1).getTitle());
        if (this.tempFile.exists()){
            System.out.println(loadedSongs);
        }
    }

    @Test
    void testLoadFromJson_FileNotFound() throws FileNotFoundException {

        List<Song> result = JsonHandler.loadFromJson("nonexistent.json");

        Assertions.assertNull(result, "Expected result to be null for a nonexistent file");

    }

    @Test
    void testClearSongHistoryFromJson() {
        ObservableList<Song> songs = FXCollections.observableArrayList(new Song("Song1", "Artist1", "Album1", "2020", "path/to/song1.mp3"));
        JsonHandler.saveToJson(songs, this.tempFile.getAbsolutePath());
        JsonHandler.clearSongHistoryFromJson(this.tempFile.getAbsolutePath());

        try {
            FileReader reader = new FileReader(this.tempFile);

            try {
                Gson gson = new Gson();
                Song[] emptySongs = gson.fromJson(reader, Song[].class);
                Assertions.assertEquals(0, emptySongs.length, "Expected the JSON to be cleared");
            } catch (Throwable var6) {
                try {
                    reader.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }

                throw var6;
            }

            reader.close();
        } catch (IOException var7) {
            Assertions.fail("Failed to read cleared JSON file");
        }

        List<Song> loadedSong = JsonHandler.loadFromJson(tempFile.getAbsolutePath());
        System.out.println(loadedSong);

    }
}