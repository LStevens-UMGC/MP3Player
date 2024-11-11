package group2.mp3player.controller;

import group2.mp3player.model.MusicPlayer;
import group2.mp3player.model.Song;
import group2.mp3player.utils.JsonHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicPlayerControllerTest {

    private MusicPlayerController controller;
    private MusicPlayer model;

    @Mock
    private ListView<String> playlistListView;

    @Mock
    private TableView<Song> songTableView;

    @Mock
    private TextField searchSongsField;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new MusicPlayerController();
        model = MusicPlayer.getInstance();
    }

    @Test
    void testInitialize() {

    }

    @Test
    void testHandleAddSongToPlaylist() {

    }

    @Test
    void testHandleOpen() {

    }

    @Test
    void testHandleClearAllPlayLists() {

    }

    @Test
    void testHandleClearSongHistory() {

    }
}
