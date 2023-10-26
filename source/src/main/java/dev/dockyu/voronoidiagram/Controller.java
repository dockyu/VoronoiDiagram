package dev.dockyu.voronoidiagram;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
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
    protected void addPoint(MouseEvent event) {
        // 從事件中獲取點擊的X和Y座標
        double clickedX = event.getX();
        double clickedY = event.getY();

        // 轉換Y座標，因為Canvas的原點在左上角，而我們希望它在左下角
        double canvasHeight = canvas.getHeight();
        double convertedY = canvasHeight - clickedY;

        System.out.println("Clicked position: (" + clickedX + ", " + convertedY + ")");
    }
}