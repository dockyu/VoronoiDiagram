package dev.dockyu.voronoidiagram;

import dev.dockyu.voronoidiagram.algorithm.TwoDPlaneAlgo;
import dev.dockyu.voronoidiagram.datastruct.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class CanvasAction {
    // 常數，畫圖的設定
    public static final int GENERATOR_POINT_RADIUS = 3; // generator point 的半徑
    public static final Color GENERATOR_POINT_COLOR = Color.BLUE; // generator point 的顏色
    public static final Color EDGE_COLOR = Color.BLACK; // Voronoi Diagram edge的顏色
    public static final Color CONVEX_HULL_COLOR = Color.RED; // Convex Hull 的顏色

    // 靜態方法，不需要實例就可以呼叫


    // 畫出目前task狀態(一堆voronoi diagram)
    public static void drawTaskState(Canvas canvas, LinkedList<VoronoiDiagram> taskState) {
//        System.out.println("CanvasAction.java drawTaskState()");
        clear(canvas); // 先清空canvas
        // 遍歷taskState並依次畫出所有Voronoi Diagram
        for (VoronoiDiagram voronoiDiagram : taskState) {
            drawVoronoiDiagram(canvas, voronoiDiagram);
        }

    }

    // 畫出一個voronoi diagram
    public static void drawVoronoiDiagram(Canvas canvas, VoronoiDiagram voronoiDiagram) {
        // 畫所有邊
        drawEdges(canvas, voronoiDiagram.edges, voronoiDiagram.vertexs);
        // 畫convex hull
        drawConvexHull(canvas, voronoiDiagram.convexHull);
        // 畫所有生成點
        drawGeneratorPoints(canvas, voronoiDiagram.generatorPoints);
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
                // 與畫布的最長距離
                float distance = TwoDPlaneAlgo.maxDistanceWithRectangle(start_x, start_y, 0, canvasWidth, 0, canvasHeight);
//                distance += diagonal;

                // 延伸一個新的 start
                float dx = start_x - end_x; // x方向的差值
                float dy = start_y - end_y; // y方向的差值

                // start點往(dx,dy)方向延伸distance
                float[] newVertex= TwoDPlaneAlgo.extendWithVector(start_x, start_y, dx, dy, distance);
                start_x = newVertex[0];
                start_y = newVertex[1];
//                System.out.println("Original Start: (" + start_x + ", " + start_y + ")");  // 輸出原始的 start 座標
//                System.out.println("New Start: (" + new_start_x + ", " + new_start_y + ")");  // 輸出新計算的 start 座標
            }
            if (end.terminal) { // end 是無限延伸的點
                float distance = TwoDPlaneAlgo.maxDistanceWithRectangle(end_x, end_y, 0, canvasWidth, 0, canvasHeight);
//                distance += diagonal;

                // 延伸一個新的end
                float dx = end_x - start_x; // x方向的差值
                float dy = end_y - start_y; // y方向的差值

                // end點往(dx,dy)方向延伸distance
                float[] newVertex= TwoDPlaneAlgo.extendWithVector(end_x, end_y, dx, dy, distance);
                end_x = newVertex[0];
                end_y = newVertex[1];
//                System.out.println("Original End: (" + end_x + ", " + end_y + ")");  // 輸出原始的 end 座標
//                System.out.println("New End: (" + new_end_x + ", " + new_end_y + ")");  // 輸出新計算的 end 座標
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
        gc.fillOval(generatorPoint.getX()-GENERATOR_POINT_RADIUS/2, generatorPoint.getY()-GENERATOR_POINT_RADIUS/2, GENERATOR_POINT_RADIUS, GENERATOR_POINT_RADIUS);  // 畫圓

        // 印出座標
        // 設定文字的顏色
        gc.setFill(Color.BLACK);
        // 設定字體大小
        gc.setFont(new Font("Arial", 10));

        // 準備要印出的座標文字
        String coordinatesText = String.format("(%d, %d)", (int)generatorPoint.getX(), (int)generatorPoint.getY());

        // 印出座標文字
        gc.fillText(coordinatesText, generatorPoint.getX() + GENERATOR_POINT_RADIUS, generatorPoint.getY() - GENERATOR_POINT_RADIUS);

    }

    // 畫出多個generator point
    public static void drawGeneratorPoints(Canvas canvas, LinkedList<GeneratorPoint> generatorPointList) {
        for (GeneratorPoint generatorPoint : generatorPointList) {
            drawGeneratorPoint(canvas, generatorPoint);
        }
    }

    // 畫出convexhull
    public static void drawConvexHull(Canvas canvas, ConvexHull convexHull) {
        Iterator<GeneratorPoint> iterator = convexHull.hull.circularIterator();
        if (!iterator.hasNext()) {
            return; // 空的ConvexHull，直接返回
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        // 設定edge的顏色
        gc.setStroke(CONVEX_HULL_COLOR);

        // 存儲第一個點以便最後使用
        GeneratorPoint firstPoint = iterator.next();
        GeneratorPoint previousPoint = firstPoint;

        for (int i = 0; i < convexHull.hull.size(); i++) {
            GeneratorPoint currentPoint = iterator.next();
            // 使用previousPoint和currentPoint來畫線
            gc.strokeLine(previousPoint.getX(), previousPoint.getY(), currentPoint.getX(), currentPoint.getY());

            // 更新previousPoint為當前點，以供下次迭代使用
            previousPoint = currentPoint;
        }
    }

    // 清空canvas
    public static void clear(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 清空一個與Canvas大小相同的矩形區域
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    // 畫出輸入的檔案
    public static void drawImportFile(Canvas canvas, ArrayList<float[]> importGeneratorPoints, ArrayList<float[]> importEdges) {
        // 畫邊
        drawImportFileEdges(canvas, importEdges);
        // 畫點
        drawImportFilePoints(canvas, importGeneratorPoints);
    }

    // 畫出輸入的檔案的所有點
    public static void drawImportFilePoints(Canvas canvas, ArrayList<float[]> importGeneratorPoints) {
        for (float[] importGeneratorPoint : importGeneratorPoints) {
            drawImportFilePoint(canvas, importGeneratorPoint);
        }
    }

    // 畫出輸入的檔案的點
    public static void drawImportFilePoint(Canvas canvas, float[] importGeneratorPoint) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // 設定點的顏色
        gc.setFill(GENERATOR_POINT_COLOR);
        // 畫出點（這裡以3x3的大小為例）
        gc.fillOval(importGeneratorPoint[0]-GENERATOR_POINT_RADIUS/2, importGeneratorPoint[1]-GENERATOR_POINT_RADIUS/2, GENERATOR_POINT_RADIUS, GENERATOR_POINT_RADIUS);  // 畫圓
    }

    // 畫出輸入的檔案的所有邊
    public static void drawImportFileEdges(Canvas canvas, ArrayList<float[]> importEdges) {
        for (float[] importEdge : importEdges) {
            drawImportFileEdge(canvas, importEdge);
        }
    }

    // 畫出輸入的檔案的邊
    public static void drawImportFileEdge(Canvas canvas, float[] importEdge) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // 設定edge的顏色
        gc.setStroke(EDGE_COLOR);

        gc.strokeLine(importEdge[0], importEdge[1], importEdge[2], importEdge[3]);
    }

}
