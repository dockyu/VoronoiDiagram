package dev.dockyu.voronoidiagram;

import dev.dockyu.voronoidiagram.algorithm.VoronoiAlgo;
import dev.dockyu.voronoidiagram.datastruct.GeneratorPoint;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        System.out.println("importInput button click");

        FileChooser fileChooser = new FileChooser(); // 用來打開檔案選擇對話框
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) { // 如果input file存在
            this.model.tasks.clear(); // 清空tasks
            // 讀檔並按照格式寫入tasks
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int n = Integer.parseInt(line.trim());
                    if (n == 0) {
                        break;
                    }
                    LinkedList<GeneratorPoint> task = new LinkedList<>();
                    for (int i = 0; i < n; i++) {
                        line = reader.readLine();
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
        System.out.println("importOutput button click");
    }

    @FXML
    protected void exportVoronoi() {
        System.out.println("exportOutput button click");
    }

    @FXML
    protected void run() {
        System.out.println("run button click");
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
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState);
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
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState);
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
            // 載入下一個task
            this.model.taskPoints = this.model.tasks.poll();
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState);
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
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState);
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
        System.out.println("step button click");
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
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState);
            // 搞不好初始狀態就是最終答案，所以要判斷是否可以merge
            if ( this.model.taskState.size()>1 ) { // 有超過一個的小voronoi diagram可以合併
                // merge一次
                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else if ( !this.model.taskPoints.isEmpty() && this.model.taskState.size()==1 && this.model.tasks.isEmpty()) {
            System.out.println("狀況4");
            // 剛做完一個task，所以taskPoints有點，且taskState只有一個voronoi diagram，代表已經做完
            // 且沒有下一個任務，所以需要重複做目前任務
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState);
            // 搞不好初始狀態就是最終答案，所以要判斷是否可以merge
            if ( this.model.taskState.size()>1 ) { // 有超過一個的小voronoi diagram可以合併
                // merge一次
                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else if ( !this.model.taskPoints.isEmpty() && this.model.taskState.size()==1 && !this.model.tasks.isEmpty() ) {
            System.out.println("狀況5");
            // 剛做完一個task，所以taskPoints有點，且taskState只有一個voronoi diagram，代表已經做完
            // 且還有未來任務，此時應該要載入下一個task
            // 載入下一個task
            this.model.taskPoints = this.model.tasks.poll();
            // 先divide，用taskPoints建立初始狀態
            this.sortTaskPoints();
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState);
            // 搞不好初始狀態就是最終答案，所以要判斷是否可以merge
            if ( this.model.taskState.size()>1 ) { // 有超過一個的小voronoi diagram可以合併
                // merge一次
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
            VoronoiAlgo.divide(this.model.taskPoints, this.model.taskState);
            // 搞不好初始狀態就是最終答案，所以要判斷是否可以merge
            if ( this.model.taskState.size()>1 ) { // 有超過一個的小voronoi diagram可以合併
                // merge一次
                VoronoiAlgo.merge(this.model.taskState);
            }
            // 畫出目前task
            CanvasAction.drawTaskState(this.canvas, this.model.taskState);
        }else {
            System.out.println("step 有未包含的情況");
        }
    }

    @FXML
    protected void clear() {
        System.out.println("clear button click");
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

        System.out.println("Clicked position: (" + clickedX + ", " + clickedY + ")"); // 測試用

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
                int xComparison = Integer.compare(p1.getX(), p2.getX());
                if (xComparison != 0) {
                    return xComparison;
                }

                // 如果 x 座標相同，則比較 y 座標
                return Integer.compare(p1.getY(), p2.getY());
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