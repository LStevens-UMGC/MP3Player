package group2.mp3player.utils;

import group2.mp3player.model.Song;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class MetaDataExtractor {
    public static Song extractMetadata(File file) {
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            String title = tag != null ? tag.getFirst(FieldKey.TITLE) : "Unknown";
            String artist = tag != null ? tag.getFirst(FieldKey.ARTIST) : "Unknown";
            String album = tag != null ? tag.getFirst(FieldKey.ALBUM) : "Unknown";
            String year = tag != null ? tag.getFirst(FieldKey.YEAR) : "Unknown";

            return new Song(title, artist, album, year, file.toURI().toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error extracting metadata.");
            return null;
        }
    }
}
