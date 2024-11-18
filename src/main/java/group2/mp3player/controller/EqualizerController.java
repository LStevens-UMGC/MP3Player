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
    private HBox equalizerContainer; // VBox layout to dynamically hold sliders and labels for each equalizer band

    private Equalizer equalizerModel; // Model containing the equalizer bands and their settings
    private MediaPlayer mediaPlayer; // The MediaPlayer instance to which this equalizer applies

    @FXML
    public void initialize() {
        // Initialize the equalizer model
        equalizerModel = new Equalizer();

        // Loop through each band in the Equalizer model to create corresponding sliders and labels
        for (int i = 0; i < equalizerModel.getBands().size(); i++) {
            EqualizerBand band = equalizerModel.getBands().get(i); // Retrieve the band at the current index

            // Create a slider for adjusting the band's gain
            Slider slider = new Slider(-24.0, 12.0, band.getGain()); // Initialize with current gain
            slider.setShowTickLabels(true); // Show tick labels (e.g., "-24 dB", "0 dB", "12 dB")
            slider.setShowTickMarks(true); // Show tick marks for better visualization
            slider.setMajorTickUnit(6); // Major tick spacing (every 6 dB)
            slider.setBlockIncrement(1); // Adjust the slider in increments of 1 dB
            slider.setOrientation(javafx.geometry.Orientation.VERTICAL); // Set the slider to vertical

            // Bind the slider's value to the band's gain property bidirectionally
            band.gainProperty().bindBidirectional(slider.valueProperty());

            // Create a label that displays the center frequency of the band (e.g., "32 Hz")
            Label label = new Label(String.format("%.0f Hz", band.getCenterFrequency()));
            label.setStyle("-fx-alignment: center;"); // Center align the label below the slider

            // Create a VBox to stack the slider and the label vertically
            VBox bandControl = new VBox(5); // Spacing of 5 pixels between slider and label
            bandControl.getChildren().addAll(slider, label);
            bandControl.setStyle("-fx-alignment: center;"); // Center align the entire VBox

            // Add the VBox to the main container (equalizerContainer)
            equalizerContainer.getChildren().add(bandControl);
        }
    }


    /**
     * Sets the MediaPlayer instance for this controller and applies the equalizer settings to it.
     *
     * @param mediaPlayer The MediaPlayer instance to which the equalizer will be applied.
     */
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

        if (mediaPlayer != null) {
            // Apply the equalizer bands to the MediaPlayer's audio equalizer
            mediaPlayer.getAudioEqualizer().getBands().setAll(equalizerModel.getBands());
            mediaPlayer.getAudioEqualizer().setEnabled(true); // Enable the audio equalizer
        } else {
            // Log a message if the MediaPlayer is not initialized
            System.out.println("MediaPlayer is not initialized.");
        }
    }

    /**
     * Returns the Equalizer model associated with this controller.
     * This is used to access the current equalizer settings (e.g., gain values for each band).
     *
     * @return The Equalizer model.
     */
    public Equalizer getEqualizerModel() {
        return equalizerModel;
    }
}
