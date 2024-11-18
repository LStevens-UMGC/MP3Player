package group2.mp3player.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.EqualizerBand;

/**
 * Represents an Equalizer model that manages a list of frequency bands.
 * Each band has a center frequency, bandwidth, and gain that can be adjusted
 * to modify the audio output of a MediaPlayer.
 */
public class Equalizer {
    // A list of equalizer bands, each representing a frequency range
    private final ObservableList<EqualizerBand> bands;

    /**
     * Constructor to initialize the equalizer with predefined frequency bands.
     * Each band is configured with:
     * - Center frequency (in Hz): The frequency range the band adjusts.
     * - Bandwidth (in Hz): The range of frequencies around the center frequency affected by the band.
     * - Gain (in dB): The amount of amplification or attenuation applied (default is 0.0 dB).
     */
    public Equalizer() {
        bands = FXCollections.observableArrayList(
                // Predefined frequency bands with default gain of 0.0 dB
                new EqualizerBand(32, 19, 0.0),    // Low bass
                new EqualizerBand(64, 39, 0.0),   // Bass
                new EqualizerBand(125, 78, 0.0),  // Lower midrange
                new EqualizerBand(250, 156, 0.0), // Midrange
                new EqualizerBand(500, 312, 0.0), // Upper midrange
                new EqualizerBand(1000, 625, 0.0),// Treble (1 kHz)
                new EqualizerBand(2000, 1250, 0.0),// High treble
                new EqualizerBand(4000, 2500, 0.0),// Very high treble
                new EqualizerBand(8000, 5000, 0.0),// Presence
                new EqualizerBand(16000, 10000, 0.0)// Brilliance
        );
    }

    /**
     * Returns the list of equalizer bands.
     * This list can be used to dynamically bind sliders or update the bands programmatically.
     *
     * @return An ObservableList of EqualizerBand objects.
     */
    public ObservableList<EqualizerBand> getBands() {
        return bands;
    }

    /**
     * Sets the gain values for each band in the equalizer.
     * Each gain value corresponds to the gain of a specific band. If the provided array
     * has fewer values than the number of bands, only the corresponding bands are updated.
     *
     * @param gainValues An array of gain values (in dB) to apply to the bands.
     */
    public void setGainValues(double[] gainValues) {
        // Iterate over the bands and update the gain for each band
        for (int i = 0; i < bands.size() && i < gainValues.length; i++) {
            bands.get(i).setGain(gainValues[i]);
        }
    }

    /**
     * Retrieves the current gain values of all bands in the equalizer.
     *
     * @return An array of gain values (in dB) representing the current settings for each band.
     */
    public double[] getGainValues() {
        // Stream through the bands, map their gain values, and collect them into an array
        return bands.stream().mapToDouble(EqualizerBand::getGain).toArray();
    }
}
