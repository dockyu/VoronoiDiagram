package dev.dockyu.voronoidiagram.datastruct;

import java.util.ArrayList;
import java.util.LinkedList;

public class VoronoiDiagram {
    // generator point
    public LinkedList<GeneratorPoint> generatorPoints;
    // winged-edge data struct
    public ArrayList<Polygon> polygons;
    public ArrayList<Vertex> vertexs;
    public ArrayList<Edge> edges;
    // convex hull
    public ConvexHull convexHull;

    public VoronoiDiagram() {
        this.generatorPoints = new LinkedList<>();
        this.polygons = new ArrayList<>();
        this.vertexs = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.convexHull = new ConvexHull();
    }

}


