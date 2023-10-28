package dev.dockyu.voronoidiagram.datastruct;

import java.util.ArrayList;

public class VoronoiDiagram {
    // generator point
    ArrayList<GeneratorPoint> generatorPoints = new ArrayList<>();
    // winged-edge data struct
    ArrayList<Polygon> polygons = new ArrayList<>();
    ArrayList<Vertex> vertices = new ArrayList<>();
    ArrayList<Edge> edges = new ArrayList<>();
    // convex hull
    ConvexHull convexHull = new ConvexHull();

}


