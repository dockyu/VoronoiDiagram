package dev.dockyu.voronoidiagram;

import dev.dockyu.voronoidiagram.datastruct.VoronoiDiagram; // 導入VoronoiDiagram資料結構
import dev.dockyu.voronoidiagram.datastruct.GeneratorPoint;
import java.util.LinkedList; // LinkedList實現Queue
import java.util.Queue; // 導入Queue接口，讓LinkedList實現Queue

public class Model {
    LinkedList<LinkedList<GeneratorPoint>> tasks; // 未來所有任務的 生成點 點集
    LinkedList<GeneratorPoint> taskPoints; // 目前任務的 生成點 點集
    LinkedList<VoronoiDiagram> taskState; // 目前任務狀態，Queue


    public Model() { // 建構函式
        tasks = new LinkedList<>();
        taskPoints = new LinkedList<>();
        taskState = new LinkedList<>();
    }



    // other methods related to your business logic
}
