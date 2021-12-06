module com.uet.subscriber {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.uet.subscriber to javafx.fxml;
    exports com.uet.subscriber;
}