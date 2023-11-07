package dev.dockyu.voronoidiagram.datastruct;

public class Polygon {
    public int edge_around_polygon; // polygon的任一邊

    public Polygon(int edge_around_polygon) {
        this.edge_around_polygon = edge_around_polygon;
    }

    // 複製構造器
    public Polygon(Polygon other) {
        this.edge_around_polygon = other.edge_around_polygon;
    }
}
