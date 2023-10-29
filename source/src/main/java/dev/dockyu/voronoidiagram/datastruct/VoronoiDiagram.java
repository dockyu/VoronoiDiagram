package dev.dockyu.voronoidiagram.datastruct;

import java.util.ArrayList;

public class VoronoiDiagram {
    // generator point
    public ArrayList<GeneratorPoint> generatorPoints;
    // winged-edge data struct
    public ArrayList<Polygon> polygons;
    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    // convex hull
    public ConvexHull convexHull;

    public VoronoiDiagram() {
        this.generatorPoints = new ArrayList<>();
        this.polygons = new ArrayList<>();
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.convexHull = new ConvexHull();
    }

}


