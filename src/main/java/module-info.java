module group2.mp3player {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;
    requires jaudiotagger;
//    requires eu.hansolo.tilesfx;

    opens group2.mp3player.controller to javafx.fxml;
    exports group2.mp3player.controller;
    exports group2.mp3player.model;
    opens group2.mp3player.model to com.google.gson;
    exports group2.mp3player;
}