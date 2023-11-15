package dev.dockyu.voronoidiagram.datastruct;

import java.util.LinkedList;

public class VoronoiDiagram {
    // generator point
    public LinkedList<GeneratorPoint> generatorPoints;
    // winged-edge data struct
    public LinkedList<Polygon> polygons;
    public LinkedList<Vertex> vertexs;
    public LinkedList<Edge> edges;
    // convex hull
    public ConvexHull convexHull;

    public VoronoiDiagram() {
        this.generatorPoints = new LinkedList<>();
        this.polygons = new LinkedList<>();
        this.vertexs = new LinkedList<>();
        this.edges = new LinkedList<>();
        this.convexHull = new ConvexHull();
    }

    public LinkedList<Integer> edgesAroundPolygon(int polygonIndex) {
        // polygon周圍的edge，按順時針方式排列
//        System.out.println("edgesAroundPolygon start");
        LinkedList<Integer> edges = new LinkedList<Integer>();

        int edgeIndex = this.polygons.get(polygonIndex).edge_around_polygon;
        int edgeStart = edgeIndex;

        do {
            System.out.println("edgesAroundPolygon test");
            edges.add(edgeIndex);
            if (polygonIndex == this.edges.get(edgeIndex).left_polygon) {
                // polygon在edge左邊
                edgeIndex = this.edges.get(edgeIndex).ccw_predecessor;
            } else {
                // polygon在edge右邊
                edgeIndex = this.edges.get(edgeIndex).ccw_successor;
            }
        } while (edgeIndex!=edgeStart);
//        System.out.println("edgesAroundPolygon end");

        return edges;
    }

    public LinkedList<Integer> vertexsAroundPolygon(int polygonIndex) {
        // polygon周圍的vertex，按順時針方式排列
        LinkedList<Integer> vertexs = new LinkedList<Integer>();

        int edgeIndex = this.polygons.get(polygonIndex).edge_around_polygon;
        int edgeStart = edgeIndex;

        do {
            if (polygonIndex == this.edges.get(edgeIndex).left_polygon) {
                // polygon在edge左邊
                 vertexs.add(this.edges.get(edgeIndex).start_vertex);
                edgeIndex = this.edges.get(edgeIndex).ccw_predecessor;
            } else {
                // polygon在edge右邊
                vertexs.add(this.edges.get(edgeIndex).end_vertex);
                edgeIndex = this.edges.get(edgeIndex).ccw_successor;
            }
        } while (edgeIndex!=edgeStart);

        return vertexs;
    }

    public LinkedList<Integer> edgesAroundVertex(int vertexIndex) {
        // vertex周圍的edge，按順時針方式排列
        LinkedList<Integer> edges = new LinkedList<Integer>();

        int edgeIndex = this.vertexs.get(vertexIndex).edge_around_vertex;
        int edgeStart = edgeIndex;

        do {
            edges.add(edgeIndex);
            if (vertexIndex == this.edges.get(edgeIndex).start_vertex) {
                // polygon在edge左邊
                edgeIndex = this.edges.get(edgeIndex).cw_predecessor;
            } else {
                // polygon在edge右邊
                edgeIndex = this.edges.get(edgeIndex).cw_successor;
            }
        } while (edgeIndex!=edgeStart);

        return edges;
    }

}


