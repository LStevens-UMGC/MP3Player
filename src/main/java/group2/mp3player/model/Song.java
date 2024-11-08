package group2.mp3player.model;

import java.util.ArrayList;
import java.util.List;

public class Song {
    private String title;
    private String artist;
    private String album;
    private String year;
    private String filePath;

    public Song(String title, String artist, String album, String year, String filePath) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getYear() {
        return year;
    }

    public static List<Song> searchSongs(List<Song> songs, String input){
        List<Song> result = new ArrayList<Song>();
        for(Song song : songs){
            if(song.getTitle().toLowerCase().contains(input.toLowerCase()) ||
                    song.getArtist().toLowerCase().contains(input.toLowerCase())||
                    song.getAlbum().toLowerCase().contains(input.toLowerCase())||
                    song.getYear().contains(input)
            ){result.add(song);
            }
        }
        return result;
    }

}

