package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.*;
import javafx.application.Platform;

import java.util.Arrays;
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
        if (VDleft.generatorPoints.get(0).getX() > VDright.generatorPoints.get(0).getX()) { // VDleft換成左邊
            VoronoiDiagram temp = VDleft;
            VDleft = VDright;
            VDright = temp;
        }
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
        Edge edge0 = new Edge(true, 1, 0, 0, 1,
                1, 2, 2, 1);
        Edge edge1 = new Edge(false, 1, 2, 1, 0,
                0, 2, 2, 0);
        Edge edge2 = new Edge(false, 2, 0, 1, 0,
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
        System.out.println("mergeThreePointVD");
        VoronoiDiagram VDmerge = new VoronoiDiagram();
        // 測試
        showMergeInformation(VDleft, VDright);

        GeneratorPoint point0;
        GeneratorPoint point1;
        GeneratorPoint point2;

        if (VDleft.generatorPoints.size() == 2) {
            point0 = VDleft.generatorPoints.get(0);
            point1 = VDleft.generatorPoints.get(1);
            point2 = VDright.generatorPoints.get(0);
        }else {
            point0 = VDleft.generatorPoints.get(0);
            point1 = VDright.generatorPoints.get(0);
            point2 = VDright.generatorPoints.get(1);
        }

        sortThreeGeneratorPoint(point0, point1, point2); // 排序三個點

        // merge generatorPoint
        VDmerge.generatorPoints.add(point0);
        VDmerge.generatorPoints.add(point1);
        VDmerge.generatorPoints.add(point2);

        // 計算三角形面積，如果面積為0，則點共線
        float area = point0.getX() * (point1.getY() - point2.getY()) +
                point1.getX() * (point2.getY() - point0.getY()) +
                point2.getX() * (point0.getY() - point1.getY());

        float delta = 10f; // 距離中點的距離
        // 判斷各種情況
        if (point0.getX() == point1.getX() && point1.getX() == point2.getX()) {
            // case1: 三點垂直共線
            // 三點在垂直線上，y座標有變動，x座標不變
            // 做水平中垂線

            // 求point0, point1的中點
            float midPoint01X = (point0.getX() + point1.getX()) / 2;
            float midPoint01Y = (point0.getY() + point1.getY()) / 2;

            // 中點向左10f
            float v0_X = midPoint01X - delta;
            float v0_Y = midPoint01Y;

            // 中點向右10f
            float v1_X = midPoint01X + delta;
            float v1_Y = midPoint01Y;

            // 求point1, point2的中點
            float midPoint12X = (point1.getX() + point2.getX()) / 2;
            float midPoint12Y = (point1.getY() + point2.getY()) / 2;

            // 中點向左10f
            float v2_X = midPoint12X - delta;
            float v2_Y = midPoint12Y;

            // 中點向右10f
            float v3_X = midPoint12X + delta;
            float v3_Y = midPoint12Y;

            // merge vertex
            Vertex v0 = new Vertex(0, true, v0_X, v0_Y);
            Vertex v1 = new Vertex(0, true, v1_X, v1_Y);
            Vertex v2 = new Vertex(1, true, v2_X, v2_Y);
            Vertex v3 = new Vertex(1, true, v3_X, v3_Y);
            VDmerge.vertexs.add(v0);
            VDmerge.vertexs.add(v1);
            VDmerge.vertexs.add(v2);
            VDmerge.vertexs.add(v3);

            // merge edge
            Edge e0 = new Edge(true, 0, 1, 0, 1, 4, 3, 5, 4);
            Edge e1 = new Edge(true, 1, 2, 2, 3, 3, 2, 2, 5);
            Edge e2 = new Edge(false, 3, 2, 3, 2, 5, 1, 1, 3);
            Edge e3 = new Edge(false, 3, 1, 2, 0, 2, 1, 0, 4);
            Edge e4 = new Edge(false, 0, 3, 1, 0, 0, 5, 3, 0);
            Edge e5 = new Edge(false, 3, 1, 1, 3, 4, 0, 1, 2);
            VDmerge.edges.add(e0);
            VDmerge.edges.add(e1);
            VDmerge.edges.add(e2);
            VDmerge.edges.add(e3);
            VDmerge.edges.add(e4);
            VDmerge.edges.add(e5);

            // merge polygon
            Polygon p0 = new Polygon(4);
            Polygon p1 = new Polygon(0);
            Polygon p2 = new Polygon(1);
            Polygon p3 = new Polygon(5);
            VDmerge.polygons.add(p0);
            VDmerge.polygons.add(p1);
            VDmerge.polygons.add(p2);
            VDmerge.polygons.add(p3);

            // merge convex hull
            ConvexHull convexHull = new ConvexHull();
            convexHull.hull.add(point2);
            convexHull.hull.add(point1);
            convexHull.hull.add(point0);
            convexHull.left = 1;
            convexHull.right = 1;

        } else if (point0.getY() == point1.getY() && point1.getY() == point2.getY()) {
            // case2: 三點水平共線
            // 三點在水平線上，x座標有變動，y座標不變
            // 做垂直中垂線


            // 求point0, point1的中點
            float midPoint01X = (point0.getX() + point1.getX()) / 2;
            float midPoint01Y = (point0.getY() + point1.getY()) / 2;

            // 中點向上delta
            float v0_X = midPoint01X;
            float v0_Y = midPoint01Y + delta;

            // 中點向下delta
            float v1_X = midPoint01X;
            float v1_Y = midPoint01Y - delta;

            // 求point1, point2的中點
            float midPoint12X = (point1.getX() + point2.getX()) / 2;
            float midPoint12Y = (point1.getY() + point2.getY()) / 2;

            // 中點向上delta
            float v2_X = midPoint12X;
            float v2_Y = midPoint12Y + delta;

            // 中點向下delta
            float v3_X = midPoint12X;
            float v3_Y = midPoint12Y - delta;

            // merge vertex
            Vertex v0 = new Vertex(0, true, v0_X, v0_Y);
            Vertex v1 = new Vertex(0, true, v1_X, v1_Y);
            Vertex v2 = new Vertex(1, true, v2_X, v2_Y);
            Vertex v3 = new Vertex(1, true, v3_X, v3_Y);
            VDmerge.vertexs.add(v0);
            VDmerge.vertexs.add(v1);
            VDmerge.vertexs.add(v2);
            VDmerge.vertexs.add(v3);

            // merge edge
            Edge e0 = new Edge(true, 0, 1, 0, 1, 4, 3, 5, 4);
            Edge e1 = new Edge(true, 1, 2, 2, 3, 3, 2, 2, 5);
            Edge e2 = new Edge(false, 3, 2, 3, 2, 5, 1, 1, 3);
            Edge e3 = new Edge(false, 3, 1, 2, 0, 2, 1, 0, 4);
            Edge e4 = new Edge(false, 0, 3, 1, 0, 0, 5, 3, 0);
            Edge e5 = new Edge(false, 3, 1, 1, 3, 4, 0, 1, 2);
            VDmerge.edges.add(e0);
            VDmerge.edges.add(e1);
            VDmerge.edges.add(e2);
            VDmerge.edges.add(e3);
            VDmerge.edges.add(e4);
            VDmerge.edges.add(e5);

            // merge polygon
            Polygon p0 = new Polygon(4);
            Polygon p1 = new Polygon(0);
            Polygon p2 = new Polygon(1);
            Polygon p3 = new Polygon(5);
            VDmerge.polygons.add(p0);
            VDmerge.polygons.add(p1);
            VDmerge.polygons.add(p2);
            VDmerge.polygons.add(p3);

            // merge convex hull
            ConvexHull convexHull = new ConvexHull();
            convexHull.hull.add(point0);
            convexHull.hull.add(point1);
            convexHull.hull.add(point2);
            convexHull.left = 0;
            convexHull.right = 2;


        } else if (Math.abs(area) < 1e-9) { // 使用一個小數值來避免浮點數不精確的問題
            // case3: 三點共線，但不是垂直或水平


            // 計算中點
            float midPoint01X = (point0.getX() + point1.getX()) / 2;
            float midPoint01Y = (point0.getY() + point1.getY()) / 2;

            float midPoint12X = (point1.getX() + point2.getX()) / 2;
            float midPoint12Y = (point1.getY() + point2.getY()) / 2;

            // 計算第一個中垂線的斜率
            float slope1 = (point1.getY() - point0.getY()) / (point1.getX() - point0.getX());
            float perpendicularSlope1 = -1 / slope1; // point0,1 中垂線 斜率
            double angle1 = Math.atan(perpendicularSlope1);

            // 計算新的點
            float v0_X = (float) (midPoint01X + delta * Math.cos(angle1));
            float v0_Y = (float) (midPoint01Y + delta * Math.sin(angle1));

            float v1_X = (float) (midPoint01X - delta * Math.cos(angle1));
            float v1_Y = (float) (midPoint01Y - delta * Math.sin(angle1));

            // 計算第二個中垂線的斜率
            float slope2 = (point2.getY() - point1.getY()) / (point2.getX() - point1.getX());
            float perpendicularSlope2 = -1 / slope2;
            double angle2 = Math.atan(perpendicularSlope2);

            // 計算新的點
            float v2_X = (float) (midPoint12X + delta * Math.cos(angle2));
            float v2_Y = (float) (midPoint12Y + delta * Math.sin(angle2));

            float v3_X = (float) (midPoint12X - delta * Math.cos(angle2));
            float v3_Y = (float) (midPoint12Y - delta * Math.sin(angle2));

            // merge vertex
            Vertex v0 = new Vertex(0, true, v0_X, v0_Y);
            Vertex v1 = new Vertex(0, true, v1_X, v1_Y);
            Vertex v2 = new Vertex(1, true, v2_X, v2_Y);
            Vertex v3 = new Vertex(1, true, v3_X, v3_Y);
            VDmerge.vertexs.add(v0);
            VDmerge.vertexs.add(v1);
            VDmerge.vertexs.add(v2);
            VDmerge.vertexs.add(v3);

            // merge edge
            Edge e0 = new Edge(true, 0, 1, 0, 1, 4, 3, 5, 4);
            Edge e1 = new Edge(true, 1, 2, 2, 3, 3, 2, 2, 5);
            Edge e2 = new Edge(false, 3, 2, 3, 2, 5, 1, 1, 3);
            Edge e3 = new Edge(false, 3, 1, 2, 0, 2, 1, 0, 4);
            Edge e4 = new Edge(false, 0, 3, 1, 0, 0, 5, 3, 0);
            Edge e5 = new Edge(false, 3, 1, 1, 3, 4, 0, 1, 2);
            VDmerge.edges.add(e0);
            VDmerge.edges.add(e1);
            VDmerge.edges.add(e2);
            VDmerge.edges.add(e3);
            VDmerge.edges.add(e4);
            VDmerge.edges.add(e5);

            // merge polygon
            Polygon p0 = new Polygon(4);
            Polygon p1 = new Polygon(0);
            Polygon p2 = new Polygon(1);
            Polygon p3 = new Polygon(5);
            VDmerge.polygons.add(p0);
            VDmerge.polygons.add(p1);
            VDmerge.polygons.add(p2);
            VDmerge.polygons.add(p3);

            // merge convex hull
            ConvexHull convexHull = new ConvexHull();
            convexHull.hull.add(point0);
            convexHull.hull.add(point1);
            convexHull.hull.add(point2);
            convexHull.left = 0;
            convexHull.right = 2;

        }else {
            // case4: 三點不共線

        }



        // generatorPoints

        // merge vertex

        // merge edge

        // merge polygon

        // merge convex hull

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

    private static void sortThreeGeneratorPoint(GeneratorPoint point0, GeneratorPoint point1, GeneratorPoint point2) {
        // 暴力解三個點的VD時使用
        // 由左到右，由下到上排序三個點
        GeneratorPoint[] points = {point0, point1, point2};
        Arrays.sort(points, (p1, p2) -> {
            if (p1.getX() == p2.getX()) {
                return Float.compare(p1.getY(), p2.getY());
            }
            return Float.compare(p1.getX(), p2.getX());
        });

        // 從排序後的陣列取出點
        point0 = points[0];
        point1 = points[1];
        point2 = points[2];
    }

}
