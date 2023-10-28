package dev.dockyu.voronoidiagram.datastruct;

import java.util.LinkedList;
import java.util.ListIterator;

public class ConvexHull {
    LinkedList<Integer> list = new LinkedList<>();
    ListIterator<Integer> right = list.listIterator(); // convex hull最右邊的點
    ListIterator<Integer> left = list.listIterator(); // convex hull最左邊的點
}
