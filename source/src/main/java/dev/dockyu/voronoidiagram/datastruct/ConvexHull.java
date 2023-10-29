package dev.dockyu.voronoidiagram.datastruct;

import java.util.LinkedList;

public class ConvexHull {
    LinkedList<GeneratorPoint> hull;
    Integer right; // convex hull最右邊的點
    Integer left; // convex hull最左邊的點

    public ConvexHull() {
        this.hull = new LinkedList<>();
    }


}
