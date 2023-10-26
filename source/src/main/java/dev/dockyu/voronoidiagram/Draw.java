package dev.dockyu.voronoidiagram;

import dev.dockyu.voronoidiagram.datastruct.VoronoiDiagram;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Draw {
    // 靜態方法，不需要實例就可以呼叫
    public static void drawVoronoi(Canvas canvas, VoronoiDiagram voronoiDiagram) {
        System.out.println("Drawing a circle");

        // 獲取用於繪圖的GraphicsContext
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 設置畫筆顏色
        gc.setFill(Color.RED);

        // 畫一個矩形來模擬Voronoi區域
        gc.fillRect(50, 50, 100, 100);
    }
}
