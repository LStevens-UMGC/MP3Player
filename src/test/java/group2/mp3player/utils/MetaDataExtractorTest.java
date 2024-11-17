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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MetaDataExtractorTest {

    @Test
    void testExtractMetadata_withValidMetadata() throws Exception {
        // Mock file and metadata
        File mockFile = new File("test.mp3");

        AudioFile mockAudioFile = (AudioFile)Mockito.mock(AudioFile.class);
        Tag mockTag = (Tag)Mockito.mock(Tag.class);

        // Set up expected metadata
        when(mockTag.getFirst(FieldKey.TITLE)).thenReturn("Test Title");
        when(mockTag.getFirst(FieldKey.ARTIST)).thenReturn("Test Artist");
        when(mockTag.getFirst(FieldKey.ALBUM)).thenReturn("Test Album");
        when(mockTag.getFirst(FieldKey.YEAR)).thenReturn("2024");

        when(mockAudioFile.getTag()).thenReturn(mockTag);

        // Mock AudioFileIO to return our mock AudioFile
        Mockito.mockStatic(AudioFileIO.class).when(() -> AudioFileIO.read(mockFile)).thenReturn(mockAudioFile);

        // Run the method under test
        Song result = MetaDataExtractor.extractMetadata(mockFile);

        // Assertions
        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Artist", result.getArtist());
        assertEquals("Test Album", result.getAlbum());
        assertEquals("2024", result.getYear());
        assertEquals(mockFile.toURI().toString(), result.getFilePath());
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
