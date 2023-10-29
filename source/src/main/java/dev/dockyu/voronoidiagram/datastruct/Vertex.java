package dev.dockyu.voronoidiagram.datastruct;

public class Vertex {
    public int edge_around_vertex; //vertex的任一邊

    public boolean w_vertex; // 是否是無限點(terminal point)
    public float x_vertex; // x座標，或是無限點的延伸向量
    public float y_vertex; // y座標，或是無限點的延伸向量

    public Vertex(int edge_around_vertex, boolean w_vertex, float x_vertex, float y_vertex) {
        this.edge_around_vertex = edge_around_vertex;
        this.w_vertex = w_vertex;
        this.x_vertex = x_vertex;
        this.y_vertex = y_vertex;
    }
}
