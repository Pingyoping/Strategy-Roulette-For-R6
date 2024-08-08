module com.roulette {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.roulette to javafx.fxml;
    exports com.roulette;
}
