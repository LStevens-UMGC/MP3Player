package group2.mp3player.utils;

import group2.mp3player.model.Song;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MetaDataExtractorTest {

    @Test
    void testExtractMetadata_withValidMetadata() throws Exception {
        File testFile = new File ("src/main/resources/group2/mp3player/AudioTestFiles/1KHz.mp3");

        if (testFile.exists()) {
            System.out.println("File exists!");
        } else {
            System.out.println("File does not exist.");
        }

        try {
            AudioFile audioFile = AudioFileIO.read(testFile);
            Tag tag = audioFile.getTag();

            String title = tag != null ? tag.getFirst(FieldKey.TITLE) : "Unknown";
            String artist = tag != null ? tag.getFirst(FieldKey.ARTIST) : "Unknown";
            String album = tag != null ? tag.getFirst(FieldKey.ALBUM) : "Unknown";
            String year = tag != null ? tag.getFirst(FieldKey.YEAR) : "Unknown";

            Song newSong = new Song(title, artist, album, year, testFile.toURI().toString());

            System.out.println(newSong.getTitle() + " " + newSong.getArtist() + " " + newSong.getAlbum() + " " + newSong.getYear());

            assertNotNull(newSong);
            assertEquals("Test", newSong.getTitle());
            assertEquals("Test", newSong.getArtist());
            assertEquals("Test", newSong.getAlbum());
            assertEquals("2024", newSong.getYear());
            assertEquals(testFile.toURI().toString(), newSong.getFilePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error extracting metadata.");

        }
    }

    @Test
    void testExtractMetadata_withNoMetadata() throws Exception {
        File testFile = new File ("src/main/resources/group2/mp3player/AudioTestFiles/2KHz.mp3");

        if (testFile.exists()) {
            System.out.println("File exists!");
        } else {
            System.out.println("File does not exist.");
        }

        try {
            AudioFile audioFile = AudioFileIO.read(testFile);
            Tag tag = audioFile.getTag();

            String title = tag != null ? tag.getFirst(FieldKey.TITLE) : "Unknown";
            String artist = tag != null ? tag.getFirst(FieldKey.ARTIST) : "Unknown";
            String album = tag != null ? tag.getFirst(FieldKey.ALBUM) : "Unknown";
            String year = tag != null ? tag.getFirst(FieldKey.YEAR) : "Unknown";

            Song newSong = new Song(title, artist, album, year, testFile.toURI().toString());

            System.out.println(newSong.getTitle() + " " + newSong.getArtist() + " " + newSong.getAlbum() + " " + newSong.getYear());

            assertNotNull(newSong);
            assertEquals("Unknown", newSong.getTitle());
            assertEquals("Unknown", newSong.getArtist());
            assertEquals("Unknown", newSong.getAlbum());
            assertEquals("Unknown", newSong.getYear());
            assertEquals(testFile.toURI().toString(), newSong.getFilePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error extracting metadata.");

        }

    }

    @Test
    void testExtractMetadata_withIOException() {
        // Mock a file that throws an exception when read
        //File mockFile = new File("test.mp3");
        try {
            File testFile = new File("test.mp3");
            if (testFile.createNewFile()) {
                System.out.println("File created: " + testFile.getName());
            } else {
                System.out.println("File already exists.");
            }
            try {
                Song result = MetaDataExtractor.extractMetadata(testFile);
                assertNull(result, "Expected null result when an exception occurs");
                AudioFileIO.read(testFile);
                testFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("File read Error");
            }

        }catch(Exception e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }
}
