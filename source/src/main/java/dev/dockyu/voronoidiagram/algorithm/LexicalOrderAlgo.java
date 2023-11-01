package dev.dockyu.voronoidiagram.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LexicalOrderAlgo {

    // 排序線的兩端點
    public static float[] sortTwoPoint(float x1, float y1, float x2, float y2) {
        if (x1 < x2 || (x1 == x2 && y1 <= y2)) {
            // 已按字典序排序
            return new float[]{x1, y1, x2, y2};
        } else {
            // 需要交換兩個點的位置
            return new float[]{x2, y2, x1, y1};
        }
    }

    // 排序多個點
    public static void sortPointsLexically(ArrayList<float[]> exportGeneratorPoints) {
        Collections.sort(exportGeneratorPoints, new Comparator<float[]>() {
            @Override
            public int compare(float[] point1, float[] point2) {
                if (point1[0] < point2[0]) return -1;
                if (point1[0] > point2[0]) return 1;
                if (point1[1] < point2[1]) return -1;
                if (point1[1] > point2[1]) return 1;
                return 0;
            }
        });
    }

    // 排序多條線
    public static void sortEdgesLexically(ArrayList<float[]> exportEdges) {
        Collections.sort(exportEdges, new Comparator<float[]>() {
            @Override
            public int compare(float[] edge1, float[] edge2) {
                for (int i = 0; i < 4; ++i) {
                    if (edge1[i] < edge2[i]) {
                        return -1;
                    }
                    if (edge1[i] > edge2[i]) {
                        return 1;
                    }
                }
                return 0;
            }
        });
    }
}
