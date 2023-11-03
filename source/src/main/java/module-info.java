module dev.dockyu.voronoidiagram {
    requires javafx.controls;
    requires javafx.fxml;


    opens dev.dockyu.voronoidiagram to javafx.fxml;
    opens dev.dockyu.voronoidiagram.datastruct to javafx.fxml;
    opens dev.dockyu.voronoidiagram.algorithm to javafx.fxml;
    exports dev.dockyu.voronoidiagram;
    exports dev.dockyu.voronoidiagram.datastruct;
    exports dev.dockyu.voronoidiagram.algorithm;
}