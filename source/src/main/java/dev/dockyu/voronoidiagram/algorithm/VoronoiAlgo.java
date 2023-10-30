package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.*;

import java.util.LinkedList;

public class VoronoiAlgo {

    // 用多個生成點生成初始狀態(queue)
    public static void divide(LinkedList<GeneratorPoint> Points, LinkedList<VoronoiDiagram> voronoiTaskState) {
        int pointNum = Points.size();

        // Base case，直接做出來
        if (pointNum == 1) { // 一個生成點
            VoronoiDiagram onePointVD = createOnePointVD(Points.get(0)); // 生成VD
            voronoiTaskState.add(onePointVD); // 加入初始狀態
        }
        else if (pointNum > 1) { // 要繼續切
            int midIndex = pointNum / 2;
            LinkedList<GeneratorPoint> leftPoints = new LinkedList<>(Points.subList(0, midIndex));
            LinkedList<GeneratorPoint> rightPoints = new LinkedList<>(Points.subList(midIndex, pointNum));

            divide(leftPoints, voronoiTaskState);
            divide(rightPoints, voronoiTaskState);
        }else { // pointNum<1，有問題
            System.out.println("divide 出錯");
        }

    }

    public static void merge(LinkedList<VoronoiDiagram> voronoiTaskState) {
        VoronoiDiagram VDleft = voronoiTaskState.poll();
        VoronoiDiagram VDright = voronoiTaskState.poll();
        VoronoiDiagram VDmerge = null;
        // merge開始
        // 暴力解，merge總共2個點的voronoi diagram
        if ( VDleft.generatorPoints.size()+VDright.generatorPoints.size() == 2 ) {
            VDmerge = mergeTwoPointVD(VDleft, VDright);
        }
        // 暴力解，merge總共3個點的voronoi diagram
        if ( VDleft.generatorPoints.size()+VDright.generatorPoints.size() == 3 ) {
            VDmerge = mergeThreePointVD(VDleft, VDright);
        }
        // merge總共4個點或以上
        if ( VDleft.generatorPoints.size()+VDright.generatorPoints.size() > 3 ) {
            VDmerge = mergeMultiPointVD(VDleft, VDright);
        }
        
        // merge完成
        voronoiTaskState.add(VDmerge);
    }


    // merge 總共4個點或以上的voronoi diagram
    private static VoronoiDiagram mergeMultiPointVD(VoronoiDiagram VDleft, VoronoiDiagram VDright) {
        VoronoiDiagram VDmerge = new VoronoiDiagram();
        // 測試
        showMergeInformation(VDleft, VDright);

        return VDmerge;
    }

    // merge 總共2個點的voronoi diagram
    private static VoronoiDiagram mergeTwoPointVD(VoronoiDiagram VDleft, VoronoiDiagram VDright) {
        System.out.println("mergeTwoPointVD");
        VoronoiDiagram VDmerge = new VoronoiDiagram();
        // 測試
        showMergeInformation(VDleft, VDright);

        GeneratorPoint leftPoint = VDleft.generatorPoints.get(0); // VDleft的唯一一個生成點
        GeneratorPoint rightPoint = VDright.generatorPoints.get(0); // VDright的唯一一個生成點
        // 取得 leftPoint 和 rightPoint 的座標
        float p1_x = (float)leftPoint.getX();
        float p1_y = (float)leftPoint.getY();
        float p2_x = (float)rightPoint.getX();
        float p2_y = (float)rightPoint.getY();
        // 計算中點座標
        float midpoint_x = (p1_x + p2_x) / 2;
        float midpoint_y = (p1_y + p2_y) / 2;
        // 計算斜率
        float slope;
        if (p2_x != p1_x) {
            slope = (p2_y - p1_y) / (p2_x - p1_x);
        } else {
            slope = Float.POSITIVE_INFINITY; // 垂直線
        }

        // 計算中垂線上的點
        float deltaY = 10f; // 距離中點的距離

        float vertex0_x = 0;
        float vertex0_y = 0;
        float vertex1_x = 0;
        float vertex1_y = 0;

        if (slope == 0) {
            // 如果原始線段是水平的，那麼中垂線就是垂直的
            vertex0_x = midpoint_x;
            vertex0_y = midpoint_y - deltaY;  // 較小的 y 值
            vertex1_x = midpoint_x;
            vertex1_y = midpoint_y + deltaY;

        } else if (slope == Float.POSITIVE_INFINITY) {
            // 如果原始線段是垂直的，那麼中垂線就是水平的
            vertex0_x = midpoint_x - deltaY;  // 較小的 x 值
            vertex0_y = midpoint_y;
            vertex1_x = midpoint_x + deltaY;
            vertex1_y = midpoint_y;

        } else if (slope != 0) {
            float perpSlope = -1 / slope;

            float temp_x1 = midpoint_x + deltaY / (float) Math.sqrt(1 + perpSlope * perpSlope);
            float temp_y1 = midpoint_y + perpSlope * deltaY / (float) Math.sqrt(1 + perpSlope * perpSlope);

            float temp_x2 = midpoint_x - deltaY / (float) Math.sqrt(1 + perpSlope * perpSlope);
            float temp_y2 = midpoint_y - perpSlope * deltaY / (float) Math.sqrt(1 + perpSlope * perpSlope);

            // 確保 vertex0 是 y 較小的點
            if (temp_y1 < temp_y2) {
                vertex0_x = temp_x1;
                vertex0_y = temp_y1;
                vertex1_x = temp_x2;
                vertex1_y = temp_y2;
            } else {
                vertex0_x = temp_x2;
                vertex0_y = temp_y2;
                vertex1_x = temp_x1;
                vertex1_y = temp_y1;
            }
        }

        // 兩個 generatorPoints
        GeneratorPoint point0 = VDleft.generatorPoints.get(0); // VDleft的唯一一個生成點
        GeneratorPoint point1 = VDright.generatorPoints.get(0); // VDright的唯一一個生成點
        VDmerge.generatorPoints.add(point0);
        VDmerge.generatorPoints.add(point1);

        // 2個vertex
        Vertex vertex0 = new Vertex(0, true, vertex0_x, vertex0_y);
        Vertex vertex1 = new Vertex(0, true, vertex1_x, vertex1_y);
        VDmerge.vertexs.add(vertex0);
        VDmerge.vertexs.add(vertex1);

        // 3條edge
        Edge edge0 = new Edge(true, 0, 1, 0, 1,
                1, 2, 2, 1);
        Edge edge1 = new Edge(false, 0, 3, 1, 0,
                0, 2, 2, 0);
        Edge edge2 = new Edge(false, 3, 1, 1, 0,
                1, 0, 0, 1);
        VDmerge.edges.add(edge0);
        VDmerge.edges.add(edge1);
        VDmerge.edges.add(edge2);

        // 3個polygon
        Polygon polygon0 = new Polygon(0);
        Polygon polygon1 = new Polygon(0);
        Polygon polygon2 = new Polygon(1);
        VDmerge.polygons.add(polygon0);
        VDmerge.polygons.add(polygon1);
        VDmerge.polygons.add(polygon2);
        // convex hull
        ConvexHull CHmerge = ConvexHullAlgo.mergeTwoPointCH(VDleft.convexHull, VDright.convexHull);
        VDmerge.convexHull = CHmerge;

        return VDmerge;
    }

    // merge 總共3個點的voronoi diagram
    private static VoronoiDiagram mergeThreePointVD(VoronoiDiagram VDleft, VoronoiDiagram VDright) {
        VoronoiDiagram VDmerge = new VoronoiDiagram();
        // 測試
        showMergeInformation(VDleft, VDright);

        return VDmerge;
    }

    // base case: 直接建構1個點的voronoi diagram
    private static VoronoiDiagram createOnePointVD(GeneratorPoint p0) {
        VoronoiDiagram VD = new VoronoiDiagram();
        // generator point
        VD.generatorPoints.add(p0);
        // 沒有vertex
        // 沒有edge
        // 沒有polygon
        // convex hull
        VD.convexHull.hull.add(p0);
        VD.convexHull.left = 0;
        VD.convexHull.right = 0;

        return VD;
    }

    // 測試用
    private static void showMergeInformation(VoronoiDiagram VDleft, VoronoiDiagram VDright) {
        // 印出數量
        System.out.print("merge 左: " + VDleft.generatorPoints.size() + " 右: " + VDright.generatorPoints.size() + "\n");
        // 印出左邊的 generator points
        for (GeneratorPoint point : VDleft.generatorPoints) {
            System.out.println("左:(" + point.getX() + "," + point.getY() + ")");
        }
        // 印出右邊的 generator points
        for (GeneratorPoint point : VDright.generatorPoints) {
            System.out.println("右:(" + point.getX() + "," + point.getY() + ")");
        }
    }


}
