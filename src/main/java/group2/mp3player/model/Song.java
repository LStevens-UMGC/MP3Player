package group2.mp3player.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a song with title, artist, album, year, and file path information.
 */
public class Song {
    private String title;
    private String artist;
    private String album;
    private String year;
    private String filePath;

    /**
     * Creates a new Song instance with default values.
     *
     * This constructor initializes a new Song object without setting any of its properties.
     */
    public Song(){};

    /**
     * Constructs a Song instance with specified details.
     *
     * @param title    the title of the song
     * @param artist   the artist of the song
     * @param album    the album in which the song is included
     * @param year     the release year of the song
     * @param filePath the file path to the song
     */
    public Song(String title, String artist, String album, String year, String filePath) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.filePath = filePath;
    }

    /**
     * Retrieves the title of the song.
     *
     * @return the title of the song
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the file path of the song.
     *
     * @return the file path of the song
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Retrieves the artist of the song.
     *
     * @return the artist of the song
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Retrieves the album in which the song is included.
     *
     * @return the album of the song
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Retrieves the release year of the song.
     *
     * @return the release year of the song
     */
    public String getYear() {
        return year;
    }

    /**
     * Searches for songs in the provided list that match the specified input.
     * The search is case-insensitive and checks if the input is contained within
     * the title, artist, album, or year of each song.
     *
     * @param songs the list of songs to search through
     * @param input the search input to match against song fields
     * @return a list of songs that match the search input
     */
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

