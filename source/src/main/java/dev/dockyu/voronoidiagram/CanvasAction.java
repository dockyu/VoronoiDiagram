package dev.dockyu.voronoidiagram;

import dev.dockyu.voronoidiagram.datastruct.Edge;
import dev.dockyu.voronoidiagram.datastruct.GeneratorPoint;
import dev.dockyu.voronoidiagram.datastruct.Vertex;
import dev.dockyu.voronoidiagram.datastruct.VoronoiDiagram;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;

public class CanvasAction {
    // 常數，畫圖的設定
    public static final int GENERATOR_POINT_RADIUS = 3; // generator point 的半徑
    public static final Color GENERATOR_POINT_COLOR = Color.BLUE; // generator point 的顏色
    public static final Color EDGE_COLOR = Color.BLACK; // Voronoi Diagram edge的顏色

    // 靜態方法，不需要實例就可以呼叫


    // 畫出目前task狀態(一堆voronoi diagram)
    public static void drawTaskState(Canvas canvas, LinkedList<VoronoiDiagram> taskState) {
        System.out.println("CanvasAction.java drawTaskState()");
        clear(canvas); // 先清空canvas
        // 遍歷taskState並依次畫出所有Voronoi Diagram
        for (VoronoiDiagram voronoiDiagram : taskState) {
            drawVoronoiDiagram(canvas, voronoiDiagram);
        }

    }

    // 畫出一個voronoi diagram
    public static void drawVoronoiDiagram(Canvas canvas, VoronoiDiagram voronoiDiagram) {

        if (voronoiDiagram.generatorPoints.size() == 1) { // 畫出1個點的VD
            drawOnePointVD(canvas, voronoiDiagram);
        }else if (voronoiDiagram.generatorPoints.size() > 1) { // 一個點以上
            drawMultiointVD(canvas, voronoiDiagram);
        }

    }

    // 畫出1個點的VD
    public static void drawOnePointVD(Canvas canvas, VoronoiDiagram voronoiDiagram) {
        drawGeneratorPoint(canvas, voronoiDiagram.generatorPoints.get(0)); // 畫一個生成點
    }

    // 畫出多個點的VD
    public static void drawMultiointVD(Canvas canvas, VoronoiDiagram voronoiDiagram) {
        // 畫所有生成點
        drawGeneratorPoints(canvas, voronoiDiagram.generatorPoints);
        // 畫所有邊
        drawEdges(canvas, voronoiDiagram.edges, voronoiDiagram.vertexs);

    }


    // 畫一條edge
    public static void drawEdge(Canvas canvas, Edge edge, LinkedList<Vertex> vertexs) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // 設定edge的顏色
        gc.setStroke(EDGE_COLOR);

        if (edge.real) { // 如果是真實存在的邊才要畫
            Vertex start = vertexs.get(edge.start_vertex); // 起始點
            Vertex end = vertexs.get(edge.end_vertex); // 結束點
            float start_x = start.x; // 起始點的x座標
            float start_y = start.y; // 起始點的y座標
            float end_x = end.x; // 結束點的x座標
            float end_y = end.y; // 結束點的y座標
//            System.out.println("資料結構表示edge: "+start_x+" "+start_y+" "+end_x+" "+end_y);

            float canvasWidth = (float) canvas.getWidth();  // 獲取畫布寬度
            float canvasHeight = (float) canvas.getHeight();  // 獲取畫布高度

            // 計算畫布的對角線長度
            double diagonal = Math.sqrt(canvasWidth * canvasWidth + canvasHeight * canvasHeight);
            if (start.terminal) { // start 是無限延伸的點
                // 延伸一個新的 start
                float dx = start_x - end_x; // x方向的差值
                float dy = start_y - end_y; // y方向的差值

                // 計算單位向量
                float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
                float unit_dx = dx / magnitude;
                float unit_dy = dy / magnitude;

                // 沿著單位向量方向延伸 diagonal 的距離
                float new_start_x = start_x + unit_dx * (float) diagonal;
                float new_start_y = start_y + unit_dy * (float) diagonal;

//                System.out.println("Original Start: (" + start_x + ", " + start_y + ")");  // 輸出原始的 start 座標
//                System.out.println("New Start: (" + new_start_x + ", " + new_start_y + ")");  // 輸出新計算的 start 座標

                start_x = new_start_x;
                start_y = new_start_y;
            }
            if (end.terminal) { // end 是無限延伸的點
                // 延伸一個新的end
                float dx = end_x - start_x; // x方向的差值
                float dy = end_y - start_y; // y方向的差值

                // 計算單位向量
                float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
                float unit_dx = dx / magnitude;
                float unit_dy = dy / magnitude;

                // 沿著單位向量方向延伸 diagonal 的距離
                float new_end_x = end_x + unit_dx * (float) diagonal;
                float new_end_y = end_y + unit_dy * (float) diagonal;

//                System.out.println("Original End: (" + end_x + ", " + end_y + ")");  // 輸出原始的 end 座標
//                System.out.println("New End: (" + new_end_x + ", " + new_end_y + ")");  // 輸出新計算的 end 座標

                end_x = new_end_x;
                end_y = new_end_y;
            }

//            System.out.println("準備要畫edge: "+start_x+" "+start_y+" "+end_x+" "+end_y);
            // 畫出start到end
            gc.strokeLine(start_x, start_y, end_x, end_y);
//            gc.strokeLine(50, 700, 250, 460);


        }
    }

    // 畫多條edge
    public static void drawEdges(Canvas canvas, LinkedList<Edge> edges, LinkedList<Vertex> vertexs) {
        for (Edge edge : edges) {
            drawEdge(canvas, edge, vertexs);
        }
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
