module obfuscator.obfuscator_kr {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens obfuscator.obfuscator_kr to javafx.fxml;
    exports obfuscator.obfuscator_kr;
}