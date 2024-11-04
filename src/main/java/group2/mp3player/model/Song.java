package group2.mp3player.model;

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
}

