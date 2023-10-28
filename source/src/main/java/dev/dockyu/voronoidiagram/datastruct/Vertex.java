package dev.dockyu.voronoidiagram.datastruct;

public class Vertex {
    int edge_around_vertex; //vertex的任一邊

    boolean w_vertex; // 是否是無限點(terminal point)
    float x_vertex; // x座標，或是無限點的延伸向量
    float y_vertex; // y座標，或是無限點的延伸向量
}
