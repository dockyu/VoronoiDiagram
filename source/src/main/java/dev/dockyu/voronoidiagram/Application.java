package dev.dockyu.voronoidiagram;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. 創建模型和視圖的實例
            Model model = new Model();

            // 2. 創建控制器的實例
            Controller controller = new Controller(model);

            // 3. 加載FXML文件並設置控制器
            FXMLLoader loader = new FXMLLoader(getClass().getResource("voronoi-view.fxml"));
            loader.setController(controller);  // 設置控制器

            Pane root = loader.load();  // 加載FXML

            Scene scene = new Scene(root,1000,800);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(); // 呼叫start()
    }
}