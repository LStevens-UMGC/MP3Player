package group2.mp3player.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import group2.mp3player.model.Playlist;
import group2.mp3player.model.Song;
import javafx.collections.ObservableList;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * JsonHandler provides methods to save and load songs and playlists to and from JSON files.
 */
public class JsonHandler {
	/**
	 * Saves the provided list of songs to a JSON file specified by the file path.
	 *
	 * @param songs the list of songs to save in JSON format
	 * @param filePath the path of the file where the songs will be saved
	 */
	public static void saveToJson(ObservableList<Song> songs, String filePath) {
		try (FileWriter writer = new FileWriter(filePath)) {
			new Gson().toJson(songs, writer);
			System.out.println("Song data saved successfully.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error saving song history.");
		}
	}

	/**
	 * Loads a list of Song objects from a specified JSON file.
	 *
	 * @param filePath the path of the JSON file containing the song data
	 * @return a list of Song objects loaded from the JSON file, or null if an error occurs
	 */
	public static List<Song> loadFromJson(String filePath) {
		try (FileReader reader = new FileReader(filePath)) {
			Type listType = new TypeToken<List<Song>>() {
			}.getType();
			return new Gson().fromJson(reader, listType);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error loading song history.");
			return null;
		}
	}

	/**
	 * Saves the provided list of playlists to a JSON file specified by the file path.
	 *
	 * @param playlists the list of playlists to save in JSON format
	 * @param filePath the path of the file where the playlists will be saved
	 */
	public static void savePlaylistsToJson(List<Playlist> playlists, String filePath) {
		try (FileWriter writer = new FileWriter(filePath)) {
			new Gson().toJson(playlists, writer);
			System.out.println("Playlists saved successfully.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error saving playlists.");
		}
	}

	/**
	 * Loads a list of Playlist objects from a specified JSON file.
	 *
	 * @param filePath the path of the JSON file containing the playlist data
	 * @return a list of Playlist objects loaded from the JSON file, or null if an error occurs
	 */
	public static List<Playlist> loadPlaylistsFromJson(String filePath) {
		try (FileReader reader = new FileReader(filePath)) {
			Type listType = new TypeToken<List<Playlist>>() {
			}.getType();
			return new Gson().fromJson(reader, listType);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error loading playlists.");
			return null;
		}
	}

	/**
	 * Clears the song history by writing an empty list to the specified JSON file.
	 *
	 * @param filePath the path of the JSON file from which the song history will be cleared
	 */
	// Added function to clear song history from the JSON.
	// function and its added for clarity.
	// Can be removed if function call is set to clearPlaylistFromJson.
	public static void clearSongHistoryFromJson(String filePath) {
		try (FileWriter writer = new FileWriter(filePath)) {
			Type listType = new TypeToken<List<Playlist>>() {
			}.getType();
			new Gson().toJson(List.of(), listType, writer);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error clearing playlists.");
		}
	}

	/**
	 * Clears all playlists by writing an empty list to the specified JSON file.
	 *
	 * @param filePath the path of the JSON file from which the playlists will be cleared
	 */
	public static void clearPlaylistsFromJson(String filePath) {
		try (FileWriter writer = new FileWriter(filePath)) {
			Type listType = new TypeToken<List<Playlist>>() {
			}.getType();
			new Gson().toJson(List.of(), listType, writer);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error clearing playlists.");
		}
	}

}
