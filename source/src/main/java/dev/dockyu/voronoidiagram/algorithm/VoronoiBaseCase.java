package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.*;

public class VoronoiBaseCase {
    // base case: 直接建構3個點的voronoi diagram
    protected static VoronoiDiagram createThreePointVD(GeneratorPoint gp0, GeneratorPoint gp1, GeneratorPoint gp2) {
        // gp0,gp1,gp2已經由左到右，由下到上排列好了
        VoronoiDiagram VD = new VoronoiDiagram();

        float delta = 10f; // 無限vertex距離中點的距離

        // 判斷是否三點共線
        if ( ((gp1.getY()-gp0.getY())*(gp2.getX()-gp1.getX())) == ((gp2.getY()-gp1.getY())*(gp1.getX()-gp0.getX())) ) {
            // (p1_y-p0_y)/(p1_x-p0_x) = (p2_y-p1_y)/(p2_x-p1_x)
            // 三點共線

            // 三點共線一定會產生4個vertex (terminal vertex)
            float v0_x, v0_y;
            float v1_x, v1_y;
            float v2_x, v2_y;
            float v3_x, v3_y;

            // gp0,gp1的中點，gp1,gp2的中點
            float midPoint01_x = (gp1.getX()+gp0.getX())/2;
            float midPoint01_y = (gp1.getY()+gp0.getY())/2;
            float midPoint12_x = (gp2.getX()+gp1.getX())/2;
            float midPoint12_y = (gp2.getY()+gp1.getY())/2;

            // 共線有3種情況，算v0,v1,v2,v3座標
            if (gp0.getX() == gp1.getX() && gp1.getX() == gp2.getX()) {
                // TODO: case1: 三點垂直共線
                // 三點在垂直線上，y座標有變動，x座標不變
                // 做水平中垂線

                // 算出v0,v1,v2,v3座標
                v0_x = midPoint01_x - delta;
                v0_y = midPoint01_y;
                v1_x = midPoint01_x + delta;
                v1_y = midPoint01_y;
                v2_x = midPoint12_x - delta;
                v2_y = midPoint12_y;
                v3_x = midPoint12_x + delta;
                v3_y = midPoint12_y;
            } else if (gp0.getY() == gp1.getY() && gp1.getY() == gp2.getY()) {
                // TODO: case2: 三點水平共線
                // 三點在水平線上，x座標有變動，y座標不變
                // 做垂直中垂線

                // 算出v0,v1,v2,v3座標
                v0_x = midPoint01_x;
                v0_y = midPoint01_y + delta;
                v1_x = midPoint01_x;
                v1_y = midPoint01_y - delta;
                v2_x = midPoint12_x;
                v2_y = midPoint12_y + delta;
                v3_x = midPoint12_x;
                v3_y = midPoint12_y - delta;
            } else {
                // TODO: case3: 三點共線，但不是垂直或水平
//                System.out.println("三點共斜線");
                float slope = (gp1.getY()-gp0.getY())/(gp1.getX()- gp0.getX()); // gp0, gp1的斜率，等同gp1,gp2的斜率
                float perpendicularSlope = -1 / slope; // gp0, gp1 中垂線的斜率
                float angle = (float) Math.atan(perpendicularSlope); // 中垂線的角度

                // 算出v0,v1,v2,v3座標
                v0_x = (float) (midPoint01_x + delta * Math.cos(angle));
                v0_y = (float) (midPoint01_y + delta * Math.sin(angle));
                v1_x = (float) (midPoint01_x - delta * Math.cos(angle));
                v1_y = (float) (midPoint01_y - delta * Math.sin(angle));
                v2_x = (float) (midPoint12_x + delta * Math.cos(angle));
                v2_y = (float) (midPoint12_y + delta * Math.sin(angle));
                v3_x = (float) (midPoint12_x - delta * Math.cos(angle));
                v3_y = (float) (midPoint12_y - delta * Math.sin(angle));
            }
            
            // TODO: 建構三點共線的Voronoi Diagram

            // generator point
            VD.generatorPoints.add(gp0);
            VD.generatorPoints.add(gp1);
            VD.generatorPoints.add(gp2);

            // vertex
            Vertex v0 = new Vertex(0, true, v0_x, v0_y);
            Vertex v1 = new Vertex(0, true, v1_x, v1_y);
            Vertex v2 = new Vertex(1, true, v2_x, v2_y);
            Vertex v3 = new Vertex(1, true, v3_x, v3_y);
            VD.vertexs.add(v0);
            VD.vertexs.add(v1);
            VD.vertexs.add(v2);
            VD.vertexs.add(v3);

            // polygon
            Polygon p0 = new Polygon(4);
            Polygon p1 = new Polygon(0);
            Polygon p2 = new Polygon(1);
            Polygon p3 = new Polygon(5);
            VD.polygons.add(p0);
            VD.polygons.add(p1);
            VD.polygons.add(p2);
            VD.polygons.add(p3);

            //edge
            Edge e0 = new Edge(true, 0, 1, 0, 1, 4, 3, 5, 4);
            Edge e1 = new Edge(true, 1, 2, 2, 3, 3, 2, 2, 5);
            Edge e2 = new Edge(false, 3, 2, 3, 2, 5, 1, 1, 3);
            Edge e3 = new Edge(false, 3, 1, 2, 0, 2, 1, 0, 4);
            Edge e4 = new Edge(false, 0, 3, 1, 0, 0, 5, 3, 0);
            Edge e5 = new Edge(false, 3, 1, 1, 3, 4, 0, 1, 2);
            VD.edges.add(e0);
            VD.edges.add(e1);
            VD.edges.add(e2);
            VD.edges.add(e3);
            VD.edges.add(e4);
            VD.edges.add(e5);

            // convex hull
            VD.convexHull.hull.add(gp0);
            VD.convexHull.hull.add(gp1);
            VD.convexHull.hull.add(gp2);
            VD.convexHull.left = 0;
            VD.convexHull.right = 2;

            VD.convexHull.setCollinear();

        } else {
            // TODO: 三點不共線
//            System.out.println("三點不共線");

            // 按照順時針排序 gp0,gp1,gp2
            GeneratorPoint[] cwThreePoints = TwoDPlaneAlgo.sortThreePointClockwise(gp0, gp1, gp2);
            gp0 = cwThreePoints[0];
            gp1 = cwThreePoints[1];
            gp2 = cwThreePoints[2];
//            System.out.println("順時鐘的gp0: ("+gp0.getX()+","+gp0.getY()+")");
//            System.out.println("順時鐘的gp2: ("+gp2.getX()+","+gp2.getY()+")");

            // 算出gp0,gp1,gp2的外心
            float[] circumcenter = TwoDPlaneAlgo.getThreePointCircumcenter(gp0, gp1, gp2);
            float circumcenter_x = circumcenter[0]; // 外心的x座標
            float circumcenter_y = circumcenter[1]; // 外心的y座標

            // 計算gp0->gp1向量的法向量
            float[] normal01 = TwoDPlaneAlgo.getNormalVector(gp0, gp1);
            float normal01_x = normal01[0];
            float normal01_y = normal01[1];

            float[] v1_coordinate = TwoDPlaneAlgo.extendWithVector(circumcenter_x, circumcenter_y, normal01_x, normal01_y, delta);

            // 計算gp1->gp2向量的法向量
            float[] normal12 = TwoDPlaneAlgo.getNormalVector(gp1, gp2);
            float normal12_x = normal12[0];
            float normal12_y = normal12[1];

            float[] v2_coordinate = TwoDPlaneAlgo.extendWithVector(circumcenter_x, circumcenter_y, normal12_x, normal12_y, delta);

            // 計算gp2->gp0向量的法向量
            float[] normal20 = TwoDPlaneAlgo.getNormalVector(gp2, gp0);
            float normal20_x = normal20[0];
            float normal20_y = normal20[1];

            float[] v3_coordinate = TwoDPlaneAlgo.extendWithVector(circumcenter_x, circumcenter_y, normal20_x, normal20_y, delta);

            // 建構三點不共線的Voronoi Diagram

            // generator point
            VD.generatorPoints.add(gp0);
            VD.generatorPoints.add(gp1);
            VD.generatorPoints.add(gp2);

            // vertex
            Vertex v0 = new Vertex(0, false, circumcenter_x, circumcenter_y);
            Vertex v1 = new Vertex(3, true, v1_coordinate[0], v1_coordinate[1]);
            Vertex v2 = new Vertex(1, true, v2_coordinate[0], v2_coordinate[1]);
            Vertex v3 = new Vertex(2, true, v3_coordinate[0], v3_coordinate[1]);
            VD.vertexs.add(v0);
            VD.vertexs.add(v1);
            VD.vertexs.add(v2);
            VD.vertexs.add(v3);

            // polygon
            Polygon p0 = new Polygon(0);
            Polygon p1 = new Polygon(1);
            Polygon p2 = new Polygon(2);
            Polygon p3 = new Polygon(3);
            VD.polygons.add(p0);
            VD.polygons.add(p1);
            VD.polygons.add(p2);
            VD.polygons.add(p3);

            // edge
            Edge e0 = new Edge(true, 1, 0, 0, 1, 1, 2, 5, 3);
            Edge e1 = new Edge(true, 2, 1, 0, 2, 2, 0, 3, 4);
            Edge e2 = new Edge(true, 0, 2, 0, 3, 0, 1, 4, 5);
            Edge e3 = new Edge(false, 1, 3, 1, 2, 0, 5, 4, 1);
            Edge e4 = new Edge(false, 2, 3, 2, 3, 1, 3, 5, 2);
            Edge e5 = new Edge(false, 0, 3, 3, 1, 2, 4, 3, 0);
            VD.edges.add(e0);
            VD.edges.add(e1);
            VD.edges.add(e2);
            VD.edges.add(e3);
            VD.edges.add(e4);
            VD.edges.add(e5);

            // convex hull
            VD.convexHull.hull.add(gp0);
            VD.convexHull.hull.add(gp1);
            VD.convexHull.hull.add(gp2);
            VD.convexHull.left = 0;
            if (gp1.getX() > gp2.getX()) {
                // 如果gp1比較右邊
                VD.convexHull.right = 1;
            } else {
                VD.convexHull.right = 2;
            }

        }
//        System.out.println("convex hull left: "+VD.convexHull.get(VD.convexHull.left).getX()+","+VD.convexHull.get(VD.convexHull.left).getY());
//        System.out.println("convex hull right: "+VD.convexHull.get(VD.convexHull.right).getX()+","+VD.convexHull.get(VD.convexHull.right).getY());

        return VD;
    }

    // base case: 直接建構2個點的voronoi diagram
    protected static VoronoiDiagram createTwoPointVD(GeneratorPoint gp0, GeneratorPoint gp1) {
        // gp0,gp1已經由左到右，由下到上排列好了

        // 兩點的VD有三種情況，1.中垂線垂直2.中垂線水平3.中垂線斜斜的
        // 三種情況只有v0,v1的座標計算方法不一樣，generator point, vertex, polygon, edge, convex hull都是一樣的

        float delta = 10f; // v0, v1要和中點差10f

        float v0_x;
        float v0_y;
        float v1_x;
        float v1_y;

        // 中點座標
        float midPoint_x = ( gp0.getX()+gp1.getX() ) / 2;
        float midPoint_y = ( gp0.getY()+gp1.getY() ) / 2;

        // 計算三種情況的v0,v1座標
        if ( gp0.getY()==gp1.getY() ) {
            // TODO: 兩點水平
            // 中垂線垂直
            v0_x = midPoint_x;
            v0_y = midPoint_y - delta;
            v1_x = midPoint_x;
            v1_y = midPoint_y + delta;

        } else if ( gp0.getX()==gp1.getX() ) {
            // TODO: 兩點垂直
            // 中垂線水平
            v0_x = midPoint_x + delta;
            v0_y = midPoint_y;
            v1_x = midPoint_x - delta;
            v1_y = midPoint_y;

        } else {
            // TODO: 中垂線斜斜的
            float slope = (gp1.getY() - gp0.getY()) / (gp1.getX() - gp0.getX());
            float perpSlope = -1 / slope; // 中垂線斜率

            // 利用三角函數計算v0和v1的座標
            float angle = (float)Math.atan(perpSlope);  // 斜率轉換為角度

            v0_x = midPoint_x - delta * (float)Math.cos(angle);
            v0_y = midPoint_y - delta * (float)Math.sin(angle);

            v1_x = midPoint_x + delta * (float)Math.cos(angle);
            v1_y = midPoint_y + delta * (float)Math.sin(angle);
        }

        VoronoiDiagram VD = new VoronoiDiagram();

        // generator point
        VD.generatorPoints.add(gp0);
        System.out.println("gp0:("+gp0.getX()+","+gp0.getY()+")");
        VD.generatorPoints.add(gp1);

        // vertex
        Vertex v0 = new Vertex(0, true, v0_x, v0_y);
        Vertex v1 = new Vertex(0, true, v1_x, v1_y);
        VD.vertexs.add(v0);
        VD.vertexs.add(v1);

        // polygon
        Polygon p0 = new Polygon(1);
        Polygon p1 = new Polygon(0);
        Polygon p2 = new Polygon(2);
        VD.polygons.add(p0);
        VD.polygons.add(p1);
        VD.polygons.add(p2);

        // edge
        Edge e0 = new Edge(true, 1, 0, 0, 1, 2, 1, 1, 2);
        Edge e1 = new Edge(false, 2, 0, 1, 0, 2, 0, 0, 2);
        Edge e2 = new Edge(false, 2, 1, 0, 1, 1, 0, 0, 1);
        VD.edges.add(e0);
        VD.edges.add(e1);
        VD.edges.add(e2);

        // convex hull
        VD.convexHull.hull.add(gp0);
        VD.convexHull.hull.add(gp1);
        VD.convexHull.left = 0;
        VD.convexHull.right = 1;

        VD.convexHull.setCollinear();

        for (int i=0; i<VD.edges.size(); i++) {
            Edge edge = VD.edges.get(i);
            if (edge.deleted) {
                continue;
            }
            Vertex startVertex = VD.vertexs.get(edge.start_vertex);
            Vertex endVertex = VD.vertexs.get(edge.end_vertex);
            System.out.println("edge "+i);
            System.out.println("從("+startVertex.x+","+startVertex.y+")到("+endVertex.x+","+endVertex.y+")");
            System.out.println("左polygon "+edge.left_polygon);
            System.out.println("右polygon "+edge.right_polygon);
            System.out.println("-------");
        }

//        System.out.println("convex hull left: "+VD.convexHull.get(VD.convexHull.left).getX()+","+VD.convexHull.get(VD.convexHull.left).getY());
//        System.out.println("convex hull right: "+VD.convexHull.get(VD.convexHull.right).getX()+","+VD.convexHull.get(VD.convexHull.right).getY());

        return VD;
    }

    // base case: 直接建構1個點的voronoi diagram
    protected static VoronoiDiagram createOnePointVD(GeneratorPoint gp0) {
        VoronoiDiagram VD = new VoronoiDiagram();
        // generator point
        VD.generatorPoints.add(gp0);
        // 沒有vertex
        // 沒有edge
        // 沒有polygon
        // convex hull
        VD.convexHull.hull.add(gp0);
        VD.convexHull.left = 0;
        VD.convexHull.right = 0;
        return VD;
    }

}
