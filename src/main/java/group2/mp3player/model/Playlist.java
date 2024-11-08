package group2.mp3player.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Playlist<PlayList> {
    private String name;
    private List<Song> songs; // Change from ObservableList to List

    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ObservableList<Song> getSongs() {
        return FXCollections.observableArrayList(songs); // Convert to ObservableList when needed
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public void removeSong() {
        songs.clear();
    }

    public void setSongs(List<Song> songList) {
        this.songs.clear();
        this.songs.addAll(songList);
    }

    //Method to search the lists of playlists
    private List<Playlist> searchPlaylists(List<Playlist> playlists, String name) {
        List<Playlist> result = new ArrayList<>();
        for (Playlist playlist : playlists) {
            if(playlist.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(playlist);
            }
        }
        return result;
    }

}
