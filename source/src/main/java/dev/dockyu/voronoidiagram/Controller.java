package dev.dockyu.voronoidiagram;

import dev.dockyu.voronoidiagram.datastruct.GeneratorPoint;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

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
    private TextArea mouse_coordinate;
    @FXML
    private TextArea taskPoints_coordinate;

    // 以下是元件被觸發會做的事
    @FXML
    protected void importPoints() {
        System.out.println("importInput button click");
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
        Draw.drawVoronoi(canvas, model.getvoronoiDiagram());
    }

    @FXML
    protected void clear() {
        System.out.println("clear button click");
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

        // 顯示taskPoints中的所有點
        StringBuilder sb = new StringBuilder();
        for (GeneratorPoint generatorPoint : this.model.taskPoints) {
            sb.append("(").append(generatorPoint.getX()).append(",").append(generatorPoint.getY()).append(")\n");
        }
        taskPoints_coordinate.setText(sb.toString());

    }

    @FXML
    // 滑鼠在畫布上移動時顯示滑鼠座標
    protected  void showMouseCoordinate(MouseEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        mouse_coordinate.setText("X: " + x + ", Y: " + y);
    }
}