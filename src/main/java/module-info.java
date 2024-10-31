module group2.mp3player {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
//    requires eu.hansolo.tilesfx;

    opens group2.mp3player to javafx.fxml;
    exports group2.mp3player;
}