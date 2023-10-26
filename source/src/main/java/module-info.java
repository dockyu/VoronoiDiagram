module dev.dockyu.voronoidiagram {
    requires javafx.controls;
    requires javafx.fxml;


    opens dev.dockyu.voronoidiagram to javafx.fxml;
    exports dev.dockyu.voronoidiagram;
    exports dev.dockyu.voronoidiagram.datastruct;
    opens dev.dockyu.voronoidiagram.datastruct to javafx.fxml;
}