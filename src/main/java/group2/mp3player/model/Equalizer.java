package group2.mp3player.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.EqualizerBand;

public class Equalizer {
    private final ObservableList<EqualizerBand> bands;

    public Equalizer() {
        // Initialize bands with predefined center frequencies and bandwidths
        bands = FXCollections.observableArrayList(
                new EqualizerBand(32, 19, 0.0),
                new EqualizerBand(64, 39, 0.0),
                new EqualizerBand(125, 78, 0.0),
                new EqualizerBand(250, 156, 0.0),
                new EqualizerBand(500, 312, 0.0),
                new EqualizerBand(1000, 625, 0.0),
                new EqualizerBand(2000, 1250, 0.0),
                new EqualizerBand(4000, 2500, 0.0),
                new EqualizerBand(8000, 5000, 0.0),
                new EqualizerBand(16000, 10000, 0.0)
        );
    }

    public ObservableList<EqualizerBand> getBands() {
        return bands;
    }

    public void setGainValues(double[] gainValues) {
        for (int i = 0; i < bands.size() && i < gainValues.length; i++) {
            bands.get(i).setGain(gainValues[i]);
        }
    }

    public double[] getGainValues() {
        return bands.stream().mapToDouble(EqualizerBand::getGain).toArray();
    }
}
