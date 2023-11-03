package dev.dockyu.voronoidiagram;

import dev.dockyu.voronoidiagram.algorithm.LexicalOrderAlgo;
import dev.dockyu.voronoidiagram.algorithm.TwoDPlaneAlgo;
import dev.dockyu.voronoidiagram.algorithm.VoronoiAlgo;
import dev.dockyu.voronoidiagram.datastruct.Edge;
import dev.dockyu.voronoidiagram.datastruct.GeneratorPoint;

import dev.dockyu.voronoidiagram.datastruct.Vertex;
import dev.dockyu.voronoidiagram.datastruct.VoronoiDiagram;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Controller {
    private Model model;

    public Controller(Model model) {
        this.model = model;
    }
    
    // 以下是視窗的原件，包含button和canvas
    @FXML
    private Button importInput;
    @FXML
    private Button importOutput;
    @FXML
    private Button exportOutput;
    @FXML
    private Button run;
    @FXML
    private Button step;
    @FXML
    private Button clear;
    @FXML
    private Canvas canvas;

    @FXML
    private TextArea cursorCoordinateArea; // 畫鼠座標顯示區
    @FXML
    private TextArea taskPointsArea; // taskPoints顯示區

    // 以下是元件被觸發會做的事
    @FXML
    protected void importTasks() {
//        System.out.println("importInput button click");
        System.out.println("Controller.java importTasks()");

        FileChooser fileChooser = new FileChooser(); // 用來打開檔案選擇對話框
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) { // 如果input file存在
            this.model.tasks.clear(); // 清空tasks
            // 讀檔並按照格式寫入tasks
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 如果該行是空白行或以#開頭，則跳過
                    if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                        continue;
                    }

                    int n = Integer.parseInt(line.trim());
                    if (n == 0) {
                        break;
                    }

                    LinkedList<GeneratorPoint> task = new LinkedList<>();
                    for (int i = 0; i < n; i++) {
                        line = reader.readLine();
                        // 如果該行是空白行或以#開頭，則跳過並不增加i
                        if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                            i--;
                            continue;
                        }

                        String[] coords = line.split(" ");
                        int x = Integer.parseInt(coords[0]);
                        int y = Integer.parseInt(coords[1]);
                        task.add(new GeneratorPoint(x, y));
                    }
                    this.model.tasks.add(task);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!this.model.tasks.isEmpty()) {
                // 清空先前任務(taskPoints, taskState)
                this.model.taskPoints.clear();
                this.model.taskState.clear();
                // 清空canvas
                CanvasAction.clear(this.canvas);
                // 取得新任務
                this.model.taskPoints = this.model.tasks.poll();
                // 畫出新任務的點
                CanvasAction.drawGeneratorPoints(this.canvas, this.model.taskPoints);
                // 更新taskPoints顯示區
                this.updateTaskPointsArea();
            }

        }

    }

    @FXML
    protected void importVoronoi() {
//        System.out.println("importOutput button click");
        System.out.println("Controller.java importVoronoi()");

        ArrayList<float[]> importEdges = new ArrayList<>(); // 存儲輸入的邊
        ArrayList<float[]> importGeneratorPoints = new ArrayList<>(); // 存儲輸入的點

        FileChooser fileChooser = new FileChooser(); // 用來打開檔案選擇對話框
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");

                    if (parts[0].equals("P")) {
                        float x = Float.parseFloat(parts[1]);
                        float y = Float.parseFloat(parts[2]);
                        importGeneratorPoints.add(new float[]{x, y});
                    } else if (parts[0].equals("E")) {
                        float x1 = Float.parseFloat(parts[1]);
                        float y1 = Float.parseFloat(parts[2]);
                        float x2 = Float.parseFloat(parts[3]);
                        float y2 = Float.parseFloat(parts[4]);
                        importEdges.add(new float[]{x1, y1, x2, y2});
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 畫出輸入圖形
            CanvasAction.drawImportFile(this.canvas, importGeneratorPoints, importEdges);
        }
    }

    @FXML
    protected void exportVoronoi() {
//        System.out.println("exportOutput button click");
        System.out.println("Controller.java exportVoronoi()");

        ArrayList<float[]> exportEdges = new ArrayList<>(); // 存儲需要輸出的邊，(在畫布內)
        ArrayList<float[]> exportGeneratorPoints = new ArrayList<>(); // 存儲需要輸出的點，(所有點)

        // 處理生成點
        // 找所有生成點
        for (GeneratorPoint point : this.model.taskPoints) {
            float x = point.getX();
            float y = point.getY();
            exportGeneratorPoints.add(new float[]{x, y});
        }
        // 排序所有生成點
        LexicalOrderAlgo.sortPointsLexically(exportGeneratorPoints);

        // 處理邊
        int canvasHeight = (int) this.canvas.getHeight(); // 畫布長
        int canvasWidth = (int) this.canvas.getWidth(); // 畫布寬
        // 找所有邊
        for (VoronoiDiagram VD : this.model.taskState) {
            // 目前狀態的所有sub Voronoi Diagram
            for (Edge edge : VD.edges) {
                // sub Voronoi Diagram的所有edge
                if (edge.real) {
                    // 真實的邊，才需要輸出
                    Vertex start = VD.vertexs.get(edge.start_vertex);
                    Vertex end = VD.vertexs.get(edge.end_vertex);
                    float start_x = start.x;
                    float start_y = start.y;
                    float end_x = end.x;
                    float end_y = end.y;

                    if (start.terminal) {
                        // start點是無限延伸
                        // 與畫布的最長距離
                        float distance = TwoDPlaneAlgo.maxDistanceWithRectangle(start.x, start.y, 0, canvasWidth, 0, canvasHeight);
                        // start延伸方向的向量
                        float dx = start.x - end.x; // x方向的偏量
                        float dy = start.y - end.y; // y方向的偏量
                        // start點往(dx,dy)方向延伸distance
                        float[] newVertex= TwoDPlaneAlgo.extendWithVector(start.x, start.y, dx, dy, distance);
                        start_x = newVertex[0];
                        start_y = newVertex[1];
                    }
                    if (end.terminal) {
                        // end點是無限延伸
                        // 與畫布的最長距離
                        float distance = TwoDPlaneAlgo.maxDistanceWithRectangle(end.x, end.y, 0, canvasWidth, 0, canvasHeight);
                        // start延伸方向的向量
                        float dx = end.x - start.x; // x方向的偏量
                        float dy = end.y - start.y; // y方向的偏量
                        // start點往(dx,dy)方向延伸distance
                        float[] newVertex= TwoDPlaneAlgo.extendWithVector(end.x, end.y, dx, dy, distance);
                        end_x = newVertex[0];
                        end_y = newVertex[1];
                    }
                    // 已經把所有邊變成封閉邊
                    // 畫布的四個邊
                    float[][] rectangleEdges = new float[][] {
                            {0, 0, canvasWidth, 0},  // 下邊
                            {canvasWidth, 0, canvasWidth, canvasHeight},  // 右邊
                            {canvasWidth, canvasHeight, 0, canvasHeight},  // 上邊
                            {0, canvasHeight, 0, 0}  // 左邊
                    };

                    ArrayList<float[]> intersections = new ArrayList<>(); // 存儲相交點
                    // 判斷跟畫布所有邊相交的點
                    for (float[] canvasEdge : rectangleEdges) {
                        float[] intersection = TwoDPlaneAlgo.intersectionOfTwoClosedLine(canvasEdge[0], canvasEdge[1], canvasEdge[2], canvasEdge[3], start_x, start_y, end_x, end_y);
                        if (intersection[0] != Float.NEGATIVE_INFINITY) {
                            // 有交點
                            // 紀錄相交的點
                            intersections.add(intersection);
                        }
                    }
                    if ( intersections.size()==2 ) {
                        // 此edge穿越畫布
                        System.out.println("exportVoronoi() 狀況1");
                    } else if (intersections.size()==1) {
                        // 此edge一點在畫布內
                        System.out.println("exportVoronoi() 狀況2");
                        if (TwoDPlaneAlgo.isPointInsideRectangle(start_x, start_y, 0, canvasWidth, 0, canvasHeight)) {
                            // start點在矩形內
                            intersections.add(new float[]{start_x, start_y});
                        } else if (TwoDPlaneAlgo.isPointInsideRectangle(end_x, end_y, 0, canvasWidth, 0, canvasHeight)) {
                            // end點在矩形內
                            intersections.add(new float[]{end_x, end_y});
                        } else {
                            // 例外情況，相交在角落
                        }
                    }else {
                        // 此edge沒有在畫布內顯示
                        System.out.println("exportVoronoi() 狀況3");
                    }
                    // 已經將此edge要輸出的部分(兩個點)放入intersections
                    if (intersections.size()==2) {
                        // 排序線的兩點
                        float[] exportEdge = LexicalOrderAlgo.sortTwoPoint(intersections.get(0)[0], intersections.get(0)[1], intersections.get(1)[0], intersections.get(1)[1]);
                        exportEdges.add(exportEdge);
                    }

                }
            }
        }
        // 已經找出所有要輸出的edge，在exportEdges裡
        // 排序所有線
        LexicalOrderAlgo.sortEdgesLexically(exportEdges);

        // 輸出測試
//        for (float[] point : exportGeneratorPoints) {
//            System.out.println("P "+(int) point[0] + " " + (int) point[1]);
//        }
//        for (float[] edge : exportEdges) {
//            System.out.println("E " + (int) edge[0] + " " + (int) edge[1] + " " + (int) edge[2] + " " + (int) edge[3]);
//        }

        // 輸出檔案
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("save");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            try {
                PrintWriter writer = new PrintWriter(selectedFile);

                for (float[] point : exportGeneratorPoints) {
                    writer.println("P " + (int) point[0] + " " + (int) point[1]);
                }

                for (float[] edge : exportEdges) {
                    writer.println("E " + (int) edge[0] + " " + (int) edge[1] + " " + (int) edge[2] + " " + (int) edge[3]);
                }

                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    protected void run() {
//        System.out.println("run button click");
        System.out.println("Controller.java run()");
        // 判斷task狀態，確定有工作要給Algorithm包做才會呼叫
        if ( this.model.taskPoints.isEmpty() && this.model.tasks.isEmpty() ) {
            System.out.println("狀況1");
            // 目前task是空的，還沒點或是被clear
            // 未來task也是空的，已經做完或是還沒import
            // 不用做事
        }else if ( !this.model.taskPoints.isEmpty() && this.model.taskState.size()>1 ) {
            System.out.println("狀況2");
            // 目前task做到一半，還有超過一個的小voronoi diagram可以合併
            // merge直到結束(taskState只剩一個voronoi diagram)
            while ( this.model.taskState.size()>1 ){
                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else if ( !this.model.taskPoints.isEmpty() && this.model.taskState.isEmpty() ) {
            System.out.println("狀況3");
            // 目前task剛在畫布上點完點，所以taskPoints有點，但是舊的狀態被清空了
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState, this.model.taskPoints.size());
            // merge直到結束(taskState只剩一個voronoi diagram)
            while ( this.model.taskState.size()>1 ){
                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else if ( !this.model.taskPoints.isEmpty() && this.model.taskState.size()==1 && this.model.tasks.isEmpty()) {
            System.out.println("狀況4");
            // 剛做完一個task，所以taskPoints有點，且taskState只有一個voronoi diagram，代表已經做完
            // 且沒有下一個任務，所以需要重複做目前任務

            // 清除之前的狀態
            this.model.taskState.clear();
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState, this.model.taskPoints.size());
            // merge直到結束(taskState只剩一個voronoi diagram)
            while ( this.model.taskState.size()>1 ){
                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else if ( !this.model.taskPoints.isEmpty() && this.model.taskState.size()==1 && !this.model.tasks.isEmpty() ) {
            System.out.println("狀況5");
            // 剛做完一個task，所以taskPoints有點，且taskState只有一個voronoi diagram，代表已經做完
            // 且還有未來任務，此時應該要載入下一個task

            // 清除之前的狀態
            this.model.taskState.clear();
            this.model.taskPoints.clear();
            // 載入下一個task
            this.model.taskPoints = this.model.tasks.poll();
            // 更新taskPoints顯示區
            this.updateTaskPointsArea();
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState, this.model.taskPoints.size());
            // merge直到結束(taskState只剩一個voronoi diagram)
            while ( this.model.taskState.size()>1 ){
                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else if ( this.model.taskPoints.isEmpty() && !this.model.tasks.isEmpty() ) {
            System.out.println("狀況6");
            // 目前task是空的，還沒點或是被clear
            // 還有未來task，此時應該要載入下一個task
            // 載入下一個task
            this.model.taskPoints = this.model.tasks.poll();
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState, this.model.taskPoints.size());
            // merge直到結束(taskState只剩一個voronoi diagram)
            while ( this.model.taskState.size()>1 ){
                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else {
            System.out.println("run 有未包含的情況");
        }
    }

    @FXML
    protected void step() {
//        System.out.println("step button click");
        System.out.println("Controller.java step()");
        // 判斷task狀態，確定有工作要給Algorithm包做才會呼叫
        if ( this.model.taskPoints.isEmpty() && this.model.tasks.isEmpty() ) {
            System.out.println("狀況1");
            // 目前task是空的，還沒點或是被clear
            // 未來task也是空的，已經做完或是還沒import
            // 不用做事
        }else if ( !this.model.taskPoints.isEmpty() && this.model.taskState.size()>1 ) {
            System.out.println("狀況2");
            // 目前task做到一半，還有超過一個的小voronoi diagram可以合併
            // 繼續merge一次
            VoronoiAlgo.merge(this.model.taskState);
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else if ( !this.model.taskPoints.isEmpty() && this.model.taskState.isEmpty() ) {
            System.out.println("狀況3");
            // 目前task剛在畫布上點完點，所以taskPoints有點，但是舊的狀態被清空了
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState, this.model.taskPoints.size());
            // 搞不好初始狀態就是最終答案，所以要判斷是否可以merge
            if ( this.model.taskState.size()>1 ) { // 有超過一個的小voronoi diagram可以合併
                // merge一次
//                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else if ( !this.model.taskPoints.isEmpty() && this.model.taskState.size()==1 && this.model.tasks.isEmpty()) {
            System.out.println("狀況4");
            // 剛做完一個task，所以taskPoints有點，且taskState只有一個voronoi diagram，代表已經做完
            // 且沒有下一個任務，所以需要重複做目前任務

            // 清除之前的狀態
            this.model.taskState.clear();
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState, this.model.taskPoints.size());
            // 搞不好初始狀態就是最終答案，所以要判斷是否可以merge
            if ( this.model.taskState.size()>1 ) { // 有超過一個的小voronoi diagram可以合併
                // merge一次
//                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else if ( !this.model.taskPoints.isEmpty() && this.model.taskState.size()==1 && !this.model.tasks.isEmpty() ) {
            System.out.println("狀況5");
            // 剛做完一個task，所以taskPoints有點，且taskState只有一個voronoi diagram，代表已經做完
            // 且還有未來任務，此時應該要載入下一個task

            // 清除之前的狀態
            this.model.taskState.clear();
            this.model.taskPoints.clear();
            // 載入下一個task
            this.model.taskPoints = this.model.tasks.poll();
            // 更新taskPoints顯示區
            this.updateTaskPointsArea();
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState, this.model.taskPoints.size());
            // 搞不好初始狀態就是最終答案，所以要判斷是否可以merge
            if ( this.model.taskState.size()>1 ) { // 有超過一個的小voronoi diagram可以合併
                // merge一次
//                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else if ( this.model.taskPoints.isEmpty() && !this.model.tasks.isEmpty() ) {
            System.out.println("狀況6");
            // 目前task是空的，還沒點或是被clear
            // 還有未來task，此時應該要載入下一個task
            // 載入下一個task
            this.model.taskPoints = this.model.tasks.poll();
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState, this.model.taskPoints.size());
            // 搞不好初始狀態就是最終答案，所以要判斷是否可以merge
            if ( this.model.taskState.size()>1 ) { // 有超過一個的小voronoi diagram可以合併
                // merge一次
//                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else {
            System.out.println("step 有未包含的情況");
        }
    }

    @FXML
    protected void clear() {
//        System.out.println("clear button click");
        System.out.println("Controller.java clear()");
        // 清空taskPoints、taskState
        this.model.taskPoints.clear();
        this.model.taskState.clear();
        // 清空canvas
        CanvasAction.clear(this.canvas);
        // 更新taskPoints顯示區
        this.updateTaskPointsArea();
    }

    @FXML
    // 點擊canvas時觸發的函式
    protected void addPoint(MouseEvent event) {
        // 從事件中獲取點擊的X和Y座標
        int clickedX = (int) event.getX(); // 點擊的x座標
        int clickedY = (int) event.getY(); // 點擊的y座標

//        System.out.println("Clicked position: (" + clickedX + ", " + clickedY + ")"); // 測試用

        // 清除taskState，目前task狀態，因為新增一個點舊的狀態就沒用了
        this.model.taskState.clear();

        // 紀錄點擊的點到taskPoints
        GeneratorPoint clickPoint = new GeneratorPoint(clickedX, clickedY);
        this.model.taskPoints.add(clickPoint);

        // 畫出點擊的點
        CanvasAction.drawGeneratorPoint(this.canvas, clickPoint);

        // 顯示目前taskPoints中的所有點
        this.updateTaskPointsArea();

    }

    @FXML
    // 滑鼠在畫布上移動時顯示滑鼠座標
    protected  void updateCursorCoordinateArea(MouseEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        cursorCoordinateArea.setText("X: " + x + ", Y: " + y);
    }

    // 功能組件

    // taskPoints顯示區，在textarea顯示taskPoints中的所有generator points
    void updateTaskPointsArea() {
        StringBuilder sb = new StringBuilder();
        for (GeneratorPoint generatorPoint : this.model.taskPoints) {
            sb.append("(").append(generatorPoint.getX()).append(",").append(generatorPoint.getY()).append(")\n");
        }
        taskPointsArea.setText(sb.toString());
    }

    // 排序taskPoints
    void sortTaskPoints() {
        Collections.sort(this.model.taskPoints, new Comparator<GeneratorPoint>() {
            @Override
            public int compare(GeneratorPoint p1, GeneratorPoint p2) {
                // 首先比較 x 座標
                int xComparison = Integer.compare((int) p1.getX(), (int) p2.getX());
                if (xComparison != 0) {
                    return xComparison;
                }

                // 如果 x 座標相同，則比較 y 座標
                return Integer.compare((int) p1.getY(), (int) p2.getY());
            }
        });
        deleteRepeatTaskPoint();
    }

    // 刪除重複的點
    void deleteRepeatTaskPoint() {
        if (this.model.taskPoints == null || this.model.taskPoints.size() < 2) {
            return;
        }

        LinkedList<GeneratorPoint> uniquePoints = new LinkedList<>();
        GeneratorPoint prevPoint = this.model.taskPoints.get(0);
        uniquePoints.add(prevPoint);

        for (int i = 1; i < this.model.taskPoints.size(); i++) {
            GeneratorPoint currentPoint = this.model.taskPoints.get(i);

            if (currentPoint.getX() != prevPoint.getX() || currentPoint.getY() != prevPoint.getY()) {
                uniquePoints.add(currentPoint);
                prevPoint = currentPoint;
            }
        }

        this.model.taskPoints = uniquePoints;
    }
}