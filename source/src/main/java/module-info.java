module dev.dockyu.voronoidiagram {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // 為JavaFX開放主應用程序類所在的包
    opens dev.dockyu.voronoidiagram to javafx.graphics, javafx.fxml;
//    exports dev.dockyu.voronoidiagram;
//    exports dev.dockyu.voronoidiagram.datastruct;
//    exports dev.dockyu.voronoidiagram.algorithm;
}