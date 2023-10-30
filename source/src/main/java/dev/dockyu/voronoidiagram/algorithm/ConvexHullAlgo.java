package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.ConvexHull;

public class ConvexHullAlgo {
    public static ConvexHull merge(ConvexHull CHleft, ConvexHull CHright) {
        ConvexHull CHmerge = new ConvexHull();



        return CHmerge;
    }

    // 暴力解
    public static ConvexHull mergeTwoPointCH(ConvexHull CHleft, ConvexHull CHright) {
        ConvexHull CHmerge = new ConvexHull();
        CHmerge.hull.add(CHleft.hull.get(0));
        CHmerge.hull.add(CHright.hull.get(0));
        CHmerge.left = 0;
        CHmerge.right = 1;
        return CHmerge;
    }


}
