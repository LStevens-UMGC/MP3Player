package group2.mp3player.model;

import group2.mp3player.model.Playlist;
import group2.mp3player.model.Song;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {

    private Playlist playlist;

    @BeforeEach
    void setUp() {
        playlist = new Playlist("My Playlist");
    }

    @Test
    void testGetName() {
        assertEquals("My Playlist", playlist.getName());
    }

    @Test
    void testAddSong() {
        Song song = new Song("Title", "Artist", "Album", "2023", "/path/to/file.mp3");
        playlist.addSong(song);

        ObservableList<Song> songs = playlist.getSongs();
        assertEquals(1, songs.size());
        assertEquals(song, songs.get(0));
    }

    @Test
    void testRemoveSong() {
        Song song = new Song("Title", "Artist", "Album", "2023", "/path/to/file.mp3");
        playlist.addSong(song);

        playlist.removeSong();
        ObservableList<Song> songs = playlist.getSongs();
        assertTrue(songs.isEmpty(), "Expected songs list to be empty after removeSong is called");
    }

    @Test
    void testSetSongs() {
        Song song1 = new Song("Title1", "Artist1", "Album1", "2023", "/path/to/file1.mp3");
        Song song2 = new Song("Title2", "Artist2", "Album2", "2023", "/path/to/file2.mp3");

        List<Song> songList = new ArrayList<>();
        songList.add(song1);
        songList.add(song2);

        playlist.setSongs(songList);

        ObservableList<Song> songs = playlist.getSongs();
        assertEquals(2, songs.size());
        assertEquals(song1, songs.get(0));
        assertEquals(song2, songs.get(1));
    }

    @Test
    void testSearchPlaylists() {
        Playlist playlist1 = new Playlist("Chill Vibes");
        Playlist playlist2 = new Playlist("Workout");

        List<Playlist> playlists = new ArrayList<>();
        playlists.add(playlist1);
        playlists.add(playlist2);

        // Assuming we expose searchPlaylists as public for testing, or else use reflection.
        List<Playlist> searchResults = playlist1.searchPlaylists(playlists, "Chill");
        assertEquals(1, searchResults.size());
        assertEquals("Chill Vibes", searchResults.get(0).getName());
    }

    @Test
    void testNoResult(){

        Playlist playlist1 = new Playlist("No Search");

        List<Playlist> playlists = new ArrayList<>();

        List<Playlist> searchResults = playlist1.searchPlaylists(playlists, "asdfasdf");
        assertEquals(0, searchResults.size());

    }

    @Test
    void testEmptySearch(){
        Playlist playlist1 = new Playlist("Empty Search");

        List<Playlist> playlists = new ArrayList<>();

        List<Playlist> searchResults = playlist1.searchPlaylists(playlists, "");
        assertEquals(playlist1.getSongs().size(), searchResults.size());
    }

    @Test
    void testPlaylistSongRemoval(){
        Song song1 = new Song("Title1", "Artist1", "Album1", "2023", "/path/to/file1.mp3");
        Song song2 = new Song("Title2", "Artist2", "Album2", "2023", "/path/to/file2.mp3");

        playlist.addSong(song1);
        playlist.addSong(song2);

        playlist.removeSpecifiedSong(song1);
        ObservableList<Song> songs = playlist.getSongs();

        System.out.println(songs);

        assertNotEquals("Title1", songs.getFirst().getTitle());

        assertEquals("Title2", songs.getFirst().getTitle());
    }
}
