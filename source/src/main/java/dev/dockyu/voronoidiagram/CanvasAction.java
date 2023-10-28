package dev.dockyu.voronoidiagram;

import dev.dockyu.voronoidiagram.datastruct.GeneratorPoint;
import dev.dockyu.voronoidiagram.datastruct.VoronoiDiagram;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.LinkedList;

public class CanvasAction {
    // 常數，畫圖的設定
    public static final int GENERATOR_POINT_RADIUS = 3; // generator point 的半徑
    public static final Color GENERATOR_POINT_COLOR = Color.BLUE; // generator point 的顏色

    // 靜態方法，不需要實例就可以呼叫

    // 測試，會刪掉
    public static void drawVoronoi(Canvas canvas, VoronoiDiagram voronoiDiagram) {
        System.out.println("drawVoronoi");

        // 獲取用於繪圖的GraphicsContext
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 設置畫筆顏色
        gc.setFill(Color.RED);

        // 畫一個矩形來模擬Voronoi區域
        gc.fillRect(50, 50, 100, 100);
    }

    // 畫出一個generator point
    public static void drawGeneratorPoint(Canvas canvas, GeneratorPoint generatorPoint) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // 設定點的顏色
        gc.setFill(GENERATOR_POINT_COLOR);
        // 畫出點（這裡以3x3的大小為例）
        gc.fillOval(generatorPoint.getX(), generatorPoint.getY(), GENERATOR_POINT_RADIUS, GENERATOR_POINT_RADIUS);  // 畫圓
    }

    // 畫出多個generator point
    public static void drawGeneratorPoints(Canvas canvas, LinkedList<GeneratorPoint> generatorPointList) {
        for (GeneratorPoint generatorPoint : generatorPointList) {
            drawGeneratorPoint(canvas, generatorPoint);
        }
    }

    // 清空canvas
    public static void clear(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 清空一個與Canvas大小相同的矩形區域
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
