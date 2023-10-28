package dev.dockyu.voronoidiagram;

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
    protected void importPoints() {
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
                // 畫出新任務
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
    }

    @FXML
    protected void step() {
        System.out.println("step button click");
        CanvasAction.drawVoronoi(canvas, model.getvoronoiDiagram());
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
    protected  void showMouseCoordinate(MouseEvent event) {
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
}