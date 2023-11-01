package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.CircularLinkedList;
import dev.dockyu.voronoidiagram.datastruct.ConvexHull;
import dev.dockyu.voronoidiagram.datastruct.GeneratorPoint;

import java.util.ArrayList;
import java.util.Iterator;

public class ConvexHullAlgo {

    // 找上切線
    public static int[] getUpperTangent(ConvexHull CHleft, ConvexHull CHright) {

        return new int[]{};
    }

    public static int[] getLowerTangent(ConvexHull CHleft, ConvexHull CHright) {

        return new int[]{};
    }

    public static ConvexHull merge(ConvexHull CHleft, ConvexHull CHright, int[] upperTangent, int[] lowerTangent) {
        ConvexHull CHmerge = new ConvexHull();

        // 從右圖上切線的點開始，按順時針方向遍歷點，直到下切線的點
        CircularLinkedList.CircularIterator<GeneratorPoint> itRight = CHright.hull.circularIterator(upperTangent[1]);
        int count = 0; // 計數，紀錄目前是CHmerge第幾個點
        GeneratorPoint gpRight = itRight.peek();  // 獲取初始點，而不是移動到下一個點

        do {
            CHmerge.hull.addLast(gpRight);  // 將點加入新的凸包
            if (itRight.getCurrentIndex()==CHright.right) {
                // 找到right
                CHmerge.right = count;
            }
            count++;

            if (itRight.hasNext()) {
                gpRight = itRight.next();
            }
        } while (itRight.hasNext() && itRight.getCurrentIndex() != lowerTangent[1]);

        // 從左圖下切線的點開始，按順時針方向遍歷點，直到上切線的點
        CircularLinkedList.CircularIterator<GeneratorPoint> itLeft = CHleft.hull.circularIterator(lowerTangent[0]);
        GeneratorPoint gpLeft = itLeft.peek();  // 獲取初始點，而不是移動到下一個點

        do {
            CHmerge.hull.addLast(gpLeft);  // 將點加入新的凸包
            if (itLeft.getCurrentIndex()==CHleft.left) {
                // 找到left
                CHmerge.left = count;
            }
            count++;

            if (itLeft.hasNext()) {
                gpLeft = itLeft.next();
            }
        } while (itLeft.hasNext() && itLeft.getCurrentIndex() != upperTangent[0]);

        //計算CHmerge的最左和最右點


        return CHmerge;
    }
}
