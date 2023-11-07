package dev.dockyu.voronoidiagram.datastruct;

public class Vertex {
    public int edge_around_vertex; //vertex的任一邊

    public boolean terminal; // 是否是無限點(terminal point)
    public float x; // x座標，或是無限點的延伸向量
    public float y; // y座標，或是無限點的延伸向量

    public boolean deleted;

    public Vertex(int edge_around_vertex, boolean terminal, float x, float y) {
        this.edge_around_vertex = edge_around_vertex;
        this.terminal = terminal;
        this.x = x;
        this.y = y;
        this.deleted = false;
    }

    // 複製構造器
    public Vertex(Vertex other) {
        this.edge_around_vertex = other.edge_around_vertex;
        this.terminal = other.terminal;
        this.x = other.x;
        this.y = other.y;
        this.deleted = other.deleted;
    }
}
