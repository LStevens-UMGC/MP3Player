package group2.mp3player.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SongTest {

    private Song song;
    private List<Song> songList;

    @BeforeEach
    void setUp() {
        // Set up a sample song and song list for testing
        song = new Song("Test Title", "Test Artist", "Test Album", "2023", "/path/to/test.mp3");
        songList = new ArrayList<>();
        songList.add(new Song("Test Title", "Test Artist", "Test Album", "2023", "/path/to/test1.mp3"));
        songList.add(new Song("Another Title", "Another Artist", "Another Album", "2022", "/path/to/test2.mp3"));
        songList.add(new Song("Example Title", "Example Artist", "Example Album", "2021", "/path/to/test3.mp3"));
    }

    @Test
    void testSongConstructorAndGetters() {
        // Test the constructor and getter methods
        assertEquals("Test Title", song.getTitle());
        assertEquals("Test Artist", song.getArtist());
        assertEquals("Test Album", song.getAlbum());
        assertEquals("2023", song.getYear());
        assertEquals("/path/to/test.mp3", song.getFilePath());
    }

    @Test
    void testSearchSongs_TitleMatch() {
        // Test searching for songs by title
        List<Song> result = Song.searchSongs(songList, "Test Title");
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
    }

    @Test
    void testSearchSongs_ArtistMatch() {
        // Test searching for songs by artist
        List<Song> result = Song.searchSongs(songList, "Example Artist");
        assertEquals(1, result.size());
        assertEquals("Example Artist", result.get(0).getArtist());
    }

    @Test
    void testSearchSongs_AlbumMatch() {
        // Test searching for songs by album
        List<Song> result = Song.searchSongs(songList, "Another Album");
        assertEquals(1, result.size());
        assertEquals("Another Album", result.get(0).getAlbum());
    }

    @Test
    void testSearchSongs_YearMatch() {
        // Test searching for songs by year
        List<Song> result = Song.searchSongs(songList, "2023");
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
    }

    @Test
    void testSearchSongs_NoMatch() {
        // Test that no results are found when the input does not match any songs
        List<Song> result = Song.searchSongs(songList, "Nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchSongs_CaseInsensitiveSearch() {
        // Test that search is case-insensitive
        List<Song> result = Song.searchSongs(songList, "test title");
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
    }
}
