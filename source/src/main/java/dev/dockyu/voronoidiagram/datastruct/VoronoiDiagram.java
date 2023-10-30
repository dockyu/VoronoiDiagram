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

}


