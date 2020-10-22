module Server {
    requires javafx.controls;
    requires javafx.fxml;
    requires ChatLib;

    opens controller;
    opens ui;
    opens business;
}