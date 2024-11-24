package group2.mp3player.utils;

import group2.mp3player.model.Song;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;


class MetaDataExtractorTest {

    @Test
    void testExtractMetadata_withValidMetadata() throws Exception {
        // Mock file and metadata
        //File mockFile = new File("test.mp3");
        File testFile = new File ("src/main/resources/group2/mp3player/AudioTestFiles/1KHz.mp3");

        if (testFile.exists()) {
            System.out.println("File exists!");
        } else {
            System.out.println("File does not exist.");
        }

        //AudioFile mockAudioFile = (AudioFile)Mockito.mock(AudioFile.class);
        AudioFile audioFile = AudioFileIO.read(testFile);

        //Tag tag = (Tag)Mockito.mock(Tag.class);
        Tag tag = audioFile.getTag();
        // Set up expected metadata
        String title = tag != null ? tag.getFirst(FieldKey.TITLE) : "Unknown";
        String artist = tag != null ? tag.getFirst(FieldKey.ARTIST) : "Unknown";
        String album = tag != null ? tag.getFirst(FieldKey.ALBUM) : "Unknown";
        String year = tag != null ? tag.getFirst(FieldKey.YEAR) : "Unknown";


        Song song = new Song(title, artist, album, year, testFile.toURI().toString());

        // Assertions
        assertNotNull(song);
        assertEquals("Test", song.getTitle());
        assertEquals("Test", song.getArtist());
        assertEquals("Test", song.getAlbum());
        assertEquals("2024", song.getYear());
        assertEquals(testFile.toURI().toString(), song.getFilePath());
    }

    @Test
    void testExtractMetadata_withNoMetadata() throws Exception {
        // Mock file with no metadata
        File testFile = new File ("src/main/resources/group2/mp3player/AudioTestFiles/2KHz.mp3");

        if (testFile.exists()) {
            System.out.println("File exists!");
        } else {
            System.out.println("File does not exist.");
        }

        //AudioFile mockAudioFile = (AudioFile)Mockito.mock(AudioFile.class);
        AudioFile audioFile = AudioFileIO.read(testFile);

        Tag tag = audioFile.getTag();
        // Set up expected metadata
        String title = tag != null ? tag.getFirst(FieldKey.TITLE) : "Unknown";
        String artist = tag != null ? tag.getFirst(FieldKey.ARTIST) : "Unknown";
        String album = tag != null ? tag.getFirst(FieldKey.ALBUM) : "Unknown";
        String year = tag != null ? tag.getFirst(FieldKey.YEAR) : "Unknown";


        Song song = new Song(title, artist, album, year, testFile.toURI().toString());

        // Assertions for default values
        assertNotNull(song);
        assertEquals("Unknown", song.getTitle());
        assertEquals("Unknown", song.getArtist());
        assertEquals("Unknown", song.getAlbum());
        assertEquals("Unknown", song.getYear());
        assertEquals(testFile.toURI().toString(), song.getFilePath());
    }

    @Test
    void testExtractMetadata_withIOException() {
        // Mock a file that throws an exception when read
        File testFile = new File("test.mp3");

        // Simulate an IOException when reading the file
        try{
            AudioFileIO.read(testFile);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("File read error");
        }

        // Run the method under test
        Song result = MetaDataExtractor.extractMetadata(testFile);


        // Assertions to check handling of exceptions
        assertNull(result, "Expected null result when an exception occurs");
    }
}