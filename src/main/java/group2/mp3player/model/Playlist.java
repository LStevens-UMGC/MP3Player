package group2.mp3player.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a playlist that contains a collection of songs. The playlist has a
 * name and provides various methods to manage its songs.
 *
 * @param <PlayList> The type representing the playlist.
 */
public class Playlist<PlayList> {
	private final String name;
	private final List<Song> songs; // Change from ObservableList to List

	/**
	 * Constructs a new Playlist with the specified name. Initializes an empty list
	 * of songs.
	 *
	 * @param name the name of the playlist.
	 */
	public Playlist(String name) {
		this.name = name;
		songs = new ArrayList<>();
	}

	/**
	 * Retrieves the name of the playlist.
	 *
	 * @return the name of the playlist.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the list of songs in the playlist.
	 *
	 * @return an ObservableList containing the songs in the playlist.
	 */
	public ObservableList<Song> getSongs() {
		return FXCollections.observableArrayList(songs); // Convert to ObservableList when needed
	}

	/**
	 * Adds a song to the playlist.
	 *
	 * @param song the song to be added to the playlist
	 */
	public void addSong(Song song) {
		songs.add(song);
	}

	/**
	 * Removes all songs from the playlist, leaving it empty.
	 */
	public void removeSong() {
		songs.clear();
	}

	/**
	 * Removes a specific song from the playlist.
	 *
	 * @param Song the song to be removed.
	 */
	public void removeSpecifiedSong(Song Song) {
		songs.remove(Song);
	}

	/**
	 * Sets the list of songs in the playlist to the provided list, replacing any
	 * existing songs.
	 *
	 * @param songList the new list of songs to be set in the playlist
	 */
	public void setSongs(List<Song> songList) {
		songs.clear();
		songs.addAll(songList);
	}

	/**
	 * Searches for playlists within a given list based on a specified name.
	 *
	 * @param playlists the list of playlists to search through
	 * @param name      the name to search for within the playlists
	 * @return a list of playlists that contain the specified name
	 */
	public List<Playlist> searchPlaylists(List<Playlist> playlists, String name) {
		List<Playlist> result = new ArrayList<>();
		for (Playlist playlist : playlists) {
			if (playlist.getName().toLowerCase().contains(name.toLowerCase())) {
				result.add(playlist);
			}
		}
		return result;
	}

}
