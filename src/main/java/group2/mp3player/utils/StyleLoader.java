package group2.mp3player.utils;

import javafx.scene.Scene;
import java.util.Objects;

public class StyleLoader {

    /**
     * Loads the specified CSS file and applies it to the given scene.
     *
     * @param scene       The JavaFX scene to apply the stylesheet to.
     * @param stylesheet  The name of the CSS file to load (e.g., "Blue.css").
     */
    public static void loadStylesheet(Scene scene, String stylesheet) {
        // Clear all existing stylesheets
        scene.getStylesheets().clear();

        // Construct the stylesheet path
        String cssPath = StyleLoader.class.getResource(stylesheet).toExternalForm();

        // Apply the new stylesheet
        scene.getStylesheets().add(cssPath);
    }

    /**
     * Loads the specified stylesheets into the given scene.
     *
     * @param scene The Scene where stylesheets will be applied.
     * @param stylesheets Array of stylesheet paths to load.
     */
    public static void loadStyles(Scene scene, String... stylesheets) {
        for (String stylesheet : stylesheets) {
            try {
                String stylesheetPath = Objects.requireNonNull(StyleLoader.class.getResource(stylesheet)).toExternalForm();
                scene.getStylesheets().add(stylesheetPath);
            } catch (NullPointerException e) {
                System.err.println("Stylesheet not found: " + stylesheet);
            }
        }
    }
}
