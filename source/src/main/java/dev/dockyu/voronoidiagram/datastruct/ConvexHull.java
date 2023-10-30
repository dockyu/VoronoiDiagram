package dev.dockyu.voronoidiagram.datastruct;

import java.util.LinkedList;

public class ConvexHull {
    public LinkedList<GeneratorPoint> hull; // 順時針方向
    public Integer right; // convex hull最右邊的點
    public Integer left; // convex hull最左邊的點

    public ConvexHull() {
        this.hull = new LinkedList<>();
    }

}
