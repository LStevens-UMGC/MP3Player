package group2.mp3player.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import group2.mp3player.model.Playlist;
import group2.mp3player.model.Song;
import javafx.collections.ObservableList;

public class JsonHandler {
	public static void saveToJson(ObservableList<Song> songs, String filePath) {
		try (FileWriter writer = new FileWriter(filePath)) {
			new Gson().toJson(songs, writer);
			System.out.println("Song history saved successfully.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error saving song history.");
		}
	}

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

	public static void savePlaylistsToJson(List<Playlist> playlists, String filePath) {
		try (FileWriter writer = new FileWriter(filePath)) {
			new Gson().toJson(playlists, writer);
			System.out.println("Playlists saved successfully.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error saving playlists.");
		}
	}

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

	// Added function to clear song history from the JSON, its basically dorielis's
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
