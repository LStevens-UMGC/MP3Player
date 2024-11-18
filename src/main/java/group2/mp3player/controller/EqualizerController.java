package group2.mp3player.controller;

import group2.mp3player.model.Equalizer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.MediaPlayer;

public class EqualizerController {
    @FXML
    private VBox equalizerContainer; // A VBox to hold the sliders dynamically

    private Equalizer equalizerModel;
    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        // Initialize the equalizer model
        equalizerModel = new Equalizer();

        // Set up sliders for each band
        for (int i = 0; i < equalizerModel.getBands().size(); i++) {
            EqualizerBand band = equalizerModel.getBands().get(i);

            // Create a label for the band frequency
            Label label = new Label(String.format("%.0f Hz", band.getCenterFrequency()));

            // Create a slider for adjusting the band's gain
            Slider slider = new Slider(-24.0, 12.0, band.getGain()); // Initialize with current gain
            slider.setShowTickLabels(true);
            slider.setShowTickMarks(true);
            slider.setMajorTickUnit(6);
            slider.setBlockIncrement(1);

            // Bind each slider value to the gain of each band
            band.gainProperty().bindBidirectional(slider.valueProperty());

            // Add the label and slider to an HBox
            HBox bandControl = new HBox(10); // Spacing of 10 between label and slider
            bandControl.getChildren().addAll(label, slider);

            // Add the HBox to the VBox container
            equalizerContainer.getChildren().add(bandControl);
        }
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

        if (mediaPlayer != null) {
            // Set the equalizer bands in the MediaPlayer's audio equalizer
            mediaPlayer.getAudioEqualizer().getBands().setAll(equalizerModel.getBands());
            mediaPlayer.getAudioEqualizer().setEnabled(true);
        } else {
            System.out.println("MediaPlayer is not initialized.");
        }
    }

    public Equalizer getEqualizerModel() {
        return equalizerModel;
    }
}
