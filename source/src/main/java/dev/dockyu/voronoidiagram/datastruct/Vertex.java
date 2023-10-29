package dev.dockyu.voronoidiagram.datastruct;

public class Vertex {
    public int edge_around_vertex; //vertex的任一邊

    public boolean w_vertex; // 是否是無限點(terminal point)
    public float x_vertex; // x座標，或是無限點的延伸向量
    public float y_vertex; // y座標，或是無限點的延伸向量
}
