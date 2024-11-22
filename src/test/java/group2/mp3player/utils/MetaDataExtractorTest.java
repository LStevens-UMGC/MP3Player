package group2.mp3player.utils;

import group2.mp3player.model.Song;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        //when(tag.getFirst(FieldKey.TITLE)).thenReturn("Test Title");
        //when(tag.getFirst(FieldKey.ARTIST)).thenReturn("Test Artist");
        //when(tag.getFirst(FieldKey.ALBUM)).thenReturn("Test Album");
        //when(tag.getFirst(FieldKey.YEAR)).thenReturn("2024");
        String title = tag != null ? tag.getFirst(FieldKey.TITLE) : "Unknown";
        String artist = tag != null ? tag.getFirst(FieldKey.ARTIST) : "Unknown";
        String album = tag != null ? tag.getFirst(FieldKey.ALBUM) : "Unknown";
        String year = tag != null ? tag.getFirst(FieldKey.YEAR) : "Unknown";


        Song song = new Song(title, artist, album, year, testFile.toURI().toString());


        //when(audioFile.getTag()).thenReturn(tag);

        // Mock AudioFileIO to return our mock AudioFile
        //Mockito.mockStatic(AudioFileIO.class).when(() -> AudioFileIO.read(testFile)).thenReturn(audioFile);

        // Run the method under test
        //Song result = MetaDataExtractor.extractMetadata(testFile);

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
        File mockFile = new File("test.mp3");

        AudioFile mockAudioFile = mock(AudioFile.class);

        // No tag associated with the file
        when(mockAudioFile.getTag()).thenReturn(null);

        // Mock AudioFileIO to return our mock AudioFile
        Mockito.mockStatic(AudioFileIO.class).when(() -> AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Run the method under test
        Song result = MetaDataExtractor.extractMetadata(mockFile);

        // Assertions for default values
        assertNotNull(result);
        assertEquals("Unknown", result.getTitle());
        assertEquals("Unknown", result.getArtist());
        assertEquals("Unknown", result.getAlbum());
        assertEquals("Unknown", result.getYear());
        assertEquals(mockFile.toURI().toString(), result.getFilePath());
    }

    @Test
    void testExtractMetadata_withIOException() {
        // Mock a file that throws an exception when read
        File mockFile = new File("test.mp3");

        // Simulate an IOException when reading the file
        Mockito.mockStatic(AudioFileIO.class).when(() -> AudioFileIO.read(mockFile)).thenThrow(new RuntimeException("File read error"));

        // Run the method under test
        Song result = MetaDataExtractor.extractMetadata(mockFile);

        // Assertions to check handling of exceptions
        assertNull(result, "Expected null result when an exception occurs");
    }
}