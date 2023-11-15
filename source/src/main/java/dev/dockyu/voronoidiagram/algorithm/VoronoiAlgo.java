package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.*;

import java.util.LinkedList;

public class VoronoiAlgo {

    // 用多個生成點生成初始狀態(queue)
    public static void divide(LinkedList<GeneratorPoint> Points, LinkedList<VoronoiDiagram> voronoiTaskState, int taskPointsNum) {
//        System.out.println("VoronoiAlgo.java divide()");
        int pointNum = Points.size();

        // Base case，直接做出來
        if (pointNum == 3) {
            VoronoiDiagram threePointVD = VoronoiBaseCase.createThreePointVD(Points.get(0), Points.get(1), Points.get(2));
            voronoiTaskState.add(threePointVD);
            if (!canBuildFullBinaryTree(taskPointsNum)) {
                voronoiTaskState.add(null); // 補成complete tree
            }
        } else if (pointNum == 2) {
            VoronoiDiagram twoPointVD = VoronoiBaseCase.createTwoPointVD(Points.get(0), Points.get(1));
            voronoiTaskState.add(twoPointVD);
        } else if (pointNum == 1) { // 一個生成點
            VoronoiDiagram onePointVD = VoronoiBaseCase.createOnePointVD(Points.get(0)); // 生成VD
            voronoiTaskState.add(onePointVD); // 加入初始狀態
        }
        else if (pointNum > 3) { // 要繼續切
            int midIndex = pointNum / 2;
            LinkedList<GeneratorPoint> leftPoints = new LinkedList<>(Points.subList(0, midIndex));
            LinkedList<GeneratorPoint> rightPoints = new LinkedList<>(Points.subList(midIndex, pointNum));

            divide(leftPoints, voronoiTaskState, taskPointsNum);
            divide(rightPoints, voronoiTaskState, taskPointsNum);
        }//else { // pointNum<1，有問題
//            System.out.println("divide 出錯");
//        }

    }

    public static void merge(LinkedList<VoronoiDiagram> voronoiTaskState) {
//        System.out.println("merge");
        // TODO: 從taskState取出要merge的兩個VD
        VoronoiDiagram VDleft = voronoiTaskState.poll();
        VoronoiDiagram VDright = voronoiTaskState.poll();
        while (VDright==null) {  // 遇到null要跳過
            voronoiTaskState.add(VDleft);
            VDleft = voronoiTaskState.poll();
            VDright = voronoiTaskState.poll();
        }

//        for (Vertex vertex : VDright.vertexs) {
//            System.out.println("("+vertex.x+","+vertex.y+")");
//        }

        VoronoiDiagram VDmerge = new VoronoiDiagram();
        // TODO: merge開始

        // TODO: 合併並換Index
        // TODO: 合併 generatorPoints
        {
            // 直接合併就好
            VDmerge.generatorPoints.addAll(VDleft.generatorPoints);
            VDmerge.generatorPoints.addAll(VDright.generatorPoints);
        }

        // TODO: 合併 polygon
        {
            for (Polygon polygon : VDleft.polygons) { // 左
                VDmerge.polygons.add(new Polygon(polygon));
            }
            VDmerge.polygons.removeLast(); // 刪掉左邊最後一個
            for (Polygon polygon : VDright.polygons) { // 右
                Polygon rightPolygon = new Polygon(polygon);
                rightPolygon.edge_around_polygon += VDleft.edges.size();
                VDmerge.polygons.add(rightPolygon);
            }
        }
//        System.out.println("polygon數量:"+VDmerge.polygons.size());
        // TODO: 合併 vertex
        {
            for (Vertex vertex : VDleft.vertexs) {
                VDmerge.vertexs.add(new Vertex(vertex));
            }
            for (Vertex vertex : VDright.vertexs) {
                Vertex rightVertex = new Vertex(vertex);
                rightVertex.edge_around_vertex += VDleft.edges.size();
                VDmerge.vertexs.add(rightVertex);
            }
        }

        // TODO: 合併 edge
        {
            for (Edge edge : VDleft.edges) {
                Edge leftEdge = new Edge(edge);
                // P無限要換
                if (leftEdge.left_polygon==VDleft.polygons.size()-1) { // 左圖的無限多邊形
                    leftEdge.left_polygon = VDmerge.polygons.size()-1; // 換成merge後的無限多邊形
                } else if (leftEdge.right_polygon==VDleft.polygons.size()-1) {
                    leftEdge.right_polygon = VDmerge.polygons.size()-1;
                }
                VDmerge.edges.add(leftEdge);
                Vertex startVertex = VDmerge.vertexs.get(leftEdge.start_vertex);
                Vertex endVertex = VDmerge.vertexs.get(leftEdge.end_vertex);
                System.out.println("從("+startVertex.x+","+startVertex.y+")到("+endVertex.x+","+endVertex.y+")");
                System.out.println("左polygon "+edge.left_polygon);
                System.out.println("右polygon "+edge.right_polygon);
                System.out.println("-------");

            }
            for (Edge edge : VDright.edges) {
                Edge rightEdge = new Edge(edge);
                rightEdge.right_polygon += (VDleft.generatorPoints.size());
                rightEdge.left_polygon += (VDleft.generatorPoints.size());
                rightEdge.start_vertex += VDleft.vertexs.size();
                rightEdge.end_vertex += VDleft.vertexs.size();
                rightEdge.ccw_predecessor += VDleft.edges.size();
                rightEdge.cw_predecessor += VDleft.edges.size();
                rightEdge.ccw_successor += VDleft.edges.size();
                rightEdge.cw_successor += VDleft.edges.size();
                VDmerge.edges.add(rightEdge);
            }
        }

//        System.out.println("求上下切線開始");
        // TODO: 求上下切線

        int upperTangentInLeftConvexHull, upperTangentInRightConvexHull;
        int lowerTangentInLeftConvexHull, lowerTangentInRightConvexHull;
        {
//            System.out.println("find Tangent");
//            System.out.println("右邊convex hull left:("+VDright.convexHull.hull.get(VDright.convexHull.left).getX()+","+VDright.convexHull.hull.get(VDright.convexHull.left).getY()+")");
            int[] upperTangent = ConvexHullAlgo.getUpperTangent(VDleft.convexHull, VDright.convexHull);
            upperTangentInLeftConvexHull = upperTangent[0];
            upperTangentInRightConvexHull = upperTangent[1];
            int[] lowerTangent = ConvexHullAlgo.getLowerTangent(VDleft.convexHull, VDright.convexHull);
            lowerTangentInLeftConvexHull = lowerTangent[0];
            lowerTangentInRightConvexHull = lowerTangent[1];
        }

        // TODO: merge convex hull
        {
            // 合併convex hull
            int[] upperTangent = new int[]{upperTangentInLeftConvexHull, upperTangentInRightConvexHull};
            int[] lowerTangent = new int[]{lowerTangentInLeftConvexHull, lowerTangentInRightConvexHull};
            ConvexHullAlgo.merge(VDmerge.convexHull, VDleft.convexHull, VDright.convexHull, upperTangent, lowerTangent);
//            System.out.println("after merge points");
//            for (GeneratorPoint gp : VDmerge.convexHull.hull) {
//                System.out.println(gp.getX()+","+gp.getY());
//            }
        }
        System.out.println("上下切線找完");
        int upperTangentLeftGPIndexInLeftVD=-1, upperTangentRightGPIndexInRightVD=-1;
        int lowerTangentLeftGPIndexInLeftVD=-1, lowerTangentRightGPIndexInRightVD=-1;


        GeneratorPoint GP = null;
        GP = VDleft.convexHull.hull.get(upperTangentInLeftConvexHull);
//        System.out.println("左上test:("+GP.getX()+","+GP.getY()+")");
        for (int i=0; i<VDleft.generatorPoints.size(); i++) {
            GeneratorPoint GPnow = VDleft.generatorPoints.get(i);
            if (GP.getX()==GPnow.getX()&&GP.getY()==GPnow.getY()) {
                upperTangentLeftGPIndexInLeftVD = i;
//                System.out.println("找到左上");
            }
        }

        GP = VDright.convexHull.hull.get(upperTangentInRightConvexHull);
//        System.out.println("右上test:("+GP.getX()+","+GP.getY()+")");
        for (int i=0; i<VDright.generatorPoints.size(); i++) {
            GeneratorPoint GPnow = VDright.generatorPoints.get(i);
//            System.out.println("嘗試找右上");
            if (GP.getX()==GPnow.getX()&&GP.getY()==GPnow.getY()) {
                upperTangentRightGPIndexInRightVD = i;
//                System.out.println("找到右上");
            }
        }


        GP = VDleft.convexHull.hull.get(lowerTangentInLeftConvexHull);
//        System.out.println("左下:("+GP.getX()+","+GP.getY()+")");
        for (int i=0; i<VDleft.generatorPoints.size(); i++) {
            GeneratorPoint GPnow = VDleft.generatorPoints.get(i);
            if (GP.getX()==GPnow.getX()&&GP.getY()==GPnow.getY()) {
                lowerTangentLeftGPIndexInLeftVD = i;
            }
        }

        GP = VDright.convexHull.hull.get(lowerTangentInRightConvexHull);
//        System.out.println("右下test:("+GP.getX()+","+GP.getY()+")");
        for (int i=0; i<VDright.generatorPoints.size(); i++) {
            GeneratorPoint GPnow = VDright.generatorPoints.get(i);
            if (GP.getX()==GPnow.getX()&&GP.getY()==GPnow.getY()) {
                lowerTangentRightGPIndexInRightVD = i;
            }
        }

        // TODO: 生成上下切線的4的點
        GeneratorPoint upperTangentLeftGP = new GeneratorPoint(
                VDleft.generatorPoints.get(upperTangentLeftGPIndexInLeftVD)
        );
        GeneratorPoint upperTangentRightGP = new GeneratorPoint(
                VDright.generatorPoints.get(upperTangentRightGPIndexInRightVD)
        );
        GeneratorPoint lowerTangentLeftGP = new GeneratorPoint(
                VDleft.generatorPoints.get(lowerTangentLeftGPIndexInLeftVD)
        );
        GeneratorPoint lowerTangentRightGP = new GeneratorPoint(
                VDright.generatorPoints.get(lowerTangentRightGPIndexInRightVD)
        );

//        System.out.println("左convexhull left("+VDleft.convexHull.hull.get(VDleft.convexHull.left).getX()+","+VDleft.convexHull.hull.get(VDleft.convexHull.left).getY()+")");
//        System.out.println("左convexhull right("+VDleft.convexHull.hull.get(VDleft.convexHull.right).getX()+","+VDleft.convexHull.hull.get(VDleft.convexHull.right).getY()+")");
//        System.out.println("右convexhull left("+VDright.convexHull.hull.get(VDright.convexHull.left).getX()+","+VDright.convexHull.hull.get(VDright.convexHull.left).getY()+")");
//        System.out.println("右convexhull right("+VDright.convexHull.hull.get(VDright.convexHull.right).getX()+","+VDright.convexHull.hull.get(VDright.convexHull.right).getY()+")");
//
        System.out.println("切線");
        System.out.println("左上("+upperTangentLeftGP.getX()+","+upperTangentLeftGP.getY()+")");
        System.out.println("右上GP index:"+upperTangentRightGPIndexInRightVD);
        System.out.println("右上("+upperTangentRightGP.getX()+","+upperTangentRightGP.getY()+")");
        System.out.println("左下("+lowerTangentLeftGP.getX()+","+lowerTangentLeftGP.getY()+")");
        System.out.println("右下("+lowerTangentRightGP.getX()+","+lowerTangentRightGP.getY()+")");


        // TODO: 找上切線的中垂線
        float[] midpointOfUpperTangent = new float[]{ // 上切線中點，也是中垂線上一點
                (upperTangentLeftGP.getX()+upperTangentRightGP.getX())/2,
                (upperTangentLeftGP.getY()+upperTangentRightGP.getY())/2
        };
        float[] HPVectorUp = TwoDPlaneAlgo.getNormalVector(upperTangentLeftGP, upperTangentRightGP); // 左點到右點求出向上的法向量
        float[] HPVectorDown = TwoDPlaneAlgo.getNormalVector(upperTangentRightGP, upperTangentLeftGP); // 右點到左點求出向上的法向量

        // TODO: 找外圍的所有點
//        System.out.println("find terminal vertex");
//        LinkedList<Integer> terminalVertexsInLeftVDIndex = VDleft.vertexsAroundPolygon(VDleft.generatorPoints.size()); // 左圖外圍的所有點
//        LinkedList<Integer> terminalVertexsInRightVDIndex = VDright.vertexsAroundPolygon(VDright.generatorPoints.size()); // 右圖外圍的所有點
//        System.out.println("右圖外圍的所有點的數量:"+terminalVertexsInRightVDIndex.size());

        // TODO: 找所有無限延伸的邊
        LinkedList<Integer> infinityEdgesInLeftVDIndex = new LinkedList<>(); // 左圖無限延伸的邊
        LinkedList<Integer> infinityEdgesInRightVDIndex = new LinkedList<>(); // 右圖無限延伸的邊

//        {
//            LinkedList<Integer> edgesAroundVertexIndex = new LinkedList<>();
//            // 找左圖的無限edge
//            for (int terminalVertexIndex : terminalVertexsInLeftVDIndex) {
//                edgesAroundVertexIndex = VDleft.edgesAroundVertex(terminalVertexIndex); // 無限點旁的所有邊
//                for (int infinityEdgeIndex : edgesAroundVertexIndex) {
//                    Edge infinityEdge = VDleft.edges.get(infinityEdgeIndex);
//                    if (infinityEdge.deleted == true) {
//                        continue;
//                    }
//                    if (infinityEdge.real == true) {
//                        infinityEdgesInLeftVDIndex.add(infinityEdgeIndex);
//                    }
//                }
//            }
//            // 找右圖的無限edge
//            for (int terminalVertexIndex : terminalVertexsInRightVDIndex) {
//                edgesAroundVertexIndex = VDright.edgesAroundVertex(terminalVertexIndex);
//                for ( int infinityEdgeIndex : edgesAroundVertexIndex) {
//                    Edge infinityEdge = VDright.edges.get(infinityEdgeIndex);
//                    if (infinityEdge.deleted == true) {
//                        continue;
//                    }
//                    if (infinityEdge.real == true) {
//                        infinityEdgesInRightVDIndex.add(infinityEdgeIndex);
//                    }
//
//                }
//            }
//
//        }

        System.out.println("左上切線的index: "+upperTangentLeftGPIndexInLeftVD);
        infinityEdgesInLeftVDIndex = VDleft.edgesAroundPolygon(upperTangentLeftGPIndexInLeftVD);
        System.out.println("左邊找完");

        System.out.println("右上切線的index: "+upperTangentRightGPIndexInRightVD);
        infinityEdgesInRightVDIndex = VDright.edgesAroundPolygon(upperTangentRightGPIndexInRightVD);

        System.out.println("左邊無限edge數量:"+infinityEdgesInLeftVDIndex.size());
        System.out.println("右邊無限edge數量:"+infinityEdgesInRightVDIndex.size());

        System.out.println("第一次有可能的邊找完");

        // TODO: 找HP相交點
        int leftFirstIntersection = -1;
        int rightFirstIntersection = -1;
        LinkedList<Intersection> HyperPlane = new LinkedList<>();
        int nowTangentLeftGPIndex = upperTangentLeftGPIndexInLeftVD; // 目前中垂線的左圖生成點
        int nowTangentRightGPIndex = upperTangentRightGPIndexInRightVD; // 目前中垂線的右圖生成點
        System.out.println("左圖Polygon編號:"+nowTangentLeftGPIndex);
        System.out.println("右圖Polygon編號:"+nowTangentRightGPIndex);
        {
            int leftIntersectEdgeIndex = -1; // 左圖有最高交點的邊
            float[] leftIntersection = new float[]{0, Float.NEGATIVE_INFINITY}; // 左圖最高的交點
            int rightIntersectEdgeIndex = -1; // 右圖有最高交點的邊
            float[] rightIntersection = new float[]{0, Float.NEGATIVE_INFINITY}; // 右圖最高的交點

            float[] tempIntersection; // 暫存交點

            // TODO: 找左邊無限edge和上切線中垂線交點
            for (int edgeIndex : infinityEdgesInLeftVDIndex) { // 左邊所有無限延伸的edge
                Edge infinityEdge = VDleft.edges.get(edgeIndex); // 其中一個無限edge
                Vertex startVertex = VDleft.vertexs.get(infinityEdge.start_vertex);
                Vertex endVertex = VDleft.vertexs.get(infinityEdge.end_vertex);
                // TODO: 判斷是不是真的邊
                if (infinityEdge.deleted == true) { // 被刪掉的邊
                    continue;
                }
                if (infinityEdge.real == false) { // 是假的邊
                    continue;
                }
                // TODO: 判斷有沒有交點
                // 把HP分成兩次求交點
                tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(midpointOfUpperTangent, HPVectorUp, startVertex, endVertex);
                if (tempIntersection == null) { // 與HP向上延伸沒有交點
                    tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(midpointOfUpperTangent, HPVectorDown, startVertex, endVertex);
                }
                if (tempIntersection == null) { // 與HP向上跟向下都沒有交點
                    continue;
                }
                // TODO: 判斷此交點有沒有比較高
                if (tempIntersection[1] > leftIntersection[1]) {
                    // TODO: 比較高，更新最高交點
//                System.out.println("左圖找到更高的點 ("+tempIntersection[0]+","+tempIntersection[0]+")");
                    leftIntersectEdgeIndex = edgeIndex;
                    leftIntersection = tempIntersection;
                }
            }

            // TODO: 找右邊無限edge和上切線中垂線交點
            for (int edgeIndex : infinityEdgesInRightVDIndex) { // 右邊所有無限延伸的edge
                Edge infinityEdge = VDright.edges.get(edgeIndex); // 目前要判斷的邊
                Vertex startVertex = VDright.vertexs.get(infinityEdge.start_vertex);
                Vertex endVertex = VDright.vertexs.get(infinityEdge.end_vertex);
                // TODO: 判斷是不是真的邊
                if (infinityEdge.deleted == true) { // 被刪掉的邊
                    continue;
                }
                if (infinityEdge.real == false) { // 是假的邊
                    continue;
                }
                System.out.println("右圖找到一條edge");
                System.out.println("從("+startVertex.x+","+startVertex.y+")到("+endVertex.x+","+endVertex.y+")");
                // TODO: 判斷有沒有交點
                // 把HP分成兩次求交點
                tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(midpointOfUpperTangent, HPVectorUp, startVertex, endVertex);
                if (tempIntersection == null) { // 與HP向上延伸沒有交點
                    tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(midpointOfUpperTangent, HPVectorDown, startVertex, endVertex);
                }
                if (tempIntersection == null) { // 與HP沒有交點
                    continue;
                }
                // TODO: 判斷此交點有沒有比較高
                if (tempIntersection[1] > rightIntersection[1]) {
                    // TODO: 比較高，更新最高交點
//                System.out.println("右圖找到交點");
                    rightIntersectEdgeIndex = edgeIndex;
                    rightIntersection = tempIntersection;
                }
            }

            // TODO: 找第一個交點
            Intersection intersectionFirst = null;

            // tangent[0] 切線左邊生成點的index
            // tangent[1] 切線右邊生成點的index

            // TODO: 找出左圖+右圖最高的交點
            // TODO: 4種情況
            if (leftIntersectEdgeIndex==-1 && rightIntersectEdgeIndex==-1) {
                // TODO: case1 左右圖都沒有交點
                {
                    // TODO: 直接建一條無限邊
//                    nowTangentLeftGPIndex = 0;
//                    nowTangentRightGPIndex = 0;
//                    for (int i=0; i< VDleft.generatorPoints.size(); i++) {
//                        if (VDleft.generatorPoints.get(i) > VDleft.generatorPoints.get(nowTangentLeftGPIndex))
//                    }
                    GeneratorPoint leftGP = VDleft.generatorPoints.get(nowTangentLeftGPIndex);
                    GeneratorPoint rightGP = VDright.generatorPoints.get(nowTangentRightGPIndex);
                    // TODO: 建兩個vertex
                    float midpointX = ( leftGP.getX()+rightGP.getX() ) /2;
                    float midpointY = ( leftGP.getY()+rightGP.getY() ) /2;

                    float[] upperVertexXY = TwoDPlaneAlgo.extendWithVector(midpointX, midpointY, HPVectorUp[0], HPVectorUp[1], 10f);
                    Vertex upperVertex = new Vertex(VDmerge.edges.size(), true, upperVertexXY[0], upperVertexXY[1]);

                    float[] lowerVertexXY = TwoDPlaneAlgo.extendWithVector(midpointX, midpointY, HPVectorDown[0], HPVectorDown[1], 10f);
                    Vertex lowerVertex = new Vertex(VDmerge.edges.size(), true, lowerVertexXY[0], lowerVertexXY[1]);

                    VDmerge.vertexs.add(upperVertex);
                    VDmerge.vertexs.add(lowerVertex);

                    Edge leftUpEdge = null;
                    Edge leftDownEdge = null;
                    Edge rightUpEdge = null;
                    Edge rightDownEdge = null;

                    // TODO: 找到原始的左虛邊
                    {
                        LinkedList<Integer> edgeIndexs = null;

                        edgeIndexs= VDleft.edgesAroundPolygon(nowTangentLeftGPIndex); // 左圖上切線的多邊形的所有邊
                        for (int edgeIndex : edgeIndexs) {
                            Edge edge = VDmerge.edges.get(edgeIndex);
                            if (edge.deleted) {
                                continue;
                            }
                            if (edge.real == false) {
                                // TODO: 目標虛邊
                                edge.deleted = true;
                                Vertex startVertex = VDmerge.vertexs.get(edge.start_vertex);
                                Vertex endVertex = VDmerge.vertexs.get(edge.end_vertex);
                                float[] midpointToStartVertexVector = new float[]{startVertex.x-midpointX, startVertex.y-midpointY};
                                float dotProduct = TwoDPlaneAlgo.dotProduct(HPVectorUp, midpointToStartVertexVector);
                                if (dotProduct > 0) {
                                    // startvertex 在上(HPVectorUp方向)
                                    leftUpEdge = new Edge(false, nowTangentLeftGPIndex, VDmerge.polygons.size()-1, edge.start_vertex, VDmerge.vertexs.size()-2
                                    , edge.cw_predecessor, edge.ccw_predecessor, VDmerge.edges.size()+2, VDmerge.edges.size()+4);
                                    leftDownEdge = new Edge(false, VDmerge.polygons.size()-1, nowTangentLeftGPIndex, edge.end_vertex, VDmerge.vertexs.size()-1
                                    , edge.cw_successor, edge.ccw_successor, VDmerge.edges.size()+4, VDmerge.edges.size()+3);
                                } else {
                                    // endvertex 在上
                                    leftUpEdge = new Edge(false, nowTangentLeftGPIndex, VDmerge.polygons.size()-1, edge.end_vertex, VDmerge.vertexs.size()-2
                                    , edge.cw_successor, edge.ccw_successor, VDmerge.edges.size()+2, VDmerge.edges.size()+4);
                                    leftDownEdge = new Edge(false, VDmerge.polygons.size()-1, nowTangentLeftGPIndex, edge.start_vertex, VDmerge.vertexs.size()-1
                                    , edge.cw_predecessor, edge.ccw_predecessor, VDmerge.edges.size()+4, VDmerge.edges.size()+3);
                                }
                            }
                        }
                    }
                    // TODO: 找到原始的右虛邊
                    {
                        LinkedList<Integer> edgeIndexs = null;

                        edgeIndexs= VDright.edgesAroundPolygon(nowTangentRightGPIndex); // 右圖上切線的多邊形的所有邊
                        for (int edgeIndex : edgeIndexs) {
                            edgeIndex += VDleft.edges.size();
                            Edge edge = VDmerge.edges.get(edgeIndex);
                            if (edge.deleted) {
                                continue;
                            }
                            if (edge.real == false) {
                                // TODO: 目標虛邊
                                edge.deleted = true;
                                Vertex startVertex = VDmerge.vertexs.get(edge.start_vertex);
                                Vertex endVertex = VDmerge.vertexs.get(edge.end_vertex);
                                float[] midpointToStartVertexVector = new float[]{startVertex.x-midpointX, startVertex.y-midpointY};
                                float dotProduct = TwoDPlaneAlgo.dotProduct(HPVectorUp, midpointToStartVertexVector);
                                if (dotProduct > 0) {
                                    // startvertex 在上(HPVectorUp方向)
                                    rightUpEdge = new Edge(false, VDmerge.polygons.size()-1, nowTangentRightGPIndex+VDleft.generatorPoints.size(), edge.start_vertex, VDmerge.vertexs.size()-2
                                    , edge.cw_predecessor, edge.ccw_predecessor, VDmerge.edges.size()-4, VDmerge.edges.size());
                                    rightDownEdge = new Edge(false, nowTangentRightGPIndex+VDleft.generatorPoints.size(), VDmerge.polygons.size()-1, edge.end_vertex, VDmerge.vertexs.size()-1
                                    , edge.cw_successor, edge.ccw_successor, VDmerge.edges.size()+1, VDmerge.edges.size()+4);
                                } else {
                                    // endvertex 在上
                                    rightUpEdge = new Edge(false, VDmerge.polygons.size()-1, nowTangentRightGPIndex+VDleft.generatorPoints.size(), edge.end_vertex, VDmerge.vertexs.size()-2
                                    , edge.cw_successor, edge.ccw_successor, VDmerge.edges.size()-4, VDmerge.edges.size());
                                    rightDownEdge = new Edge(false, nowTangentRightGPIndex+VDleft.generatorPoints.size(), VDmerge.polygons.size()-1, edge.start_vertex, VDmerge.vertexs.size()-1
                                    , edge.cw_predecessor, edge.ccw_predecessor, VDmerge.edges.size()+1, VDmerge.edges.size()+4);
                                }
                            }
                        }
                    }
                    Edge perpendicularEdge = new Edge(true, nowTangentRightGPIndex+VDleft.generatorPoints.size(), nowTangentLeftGPIndex, VDmerge.vertexs.size()-1, VDmerge.vertexs.size()-2
                    , VDmerge.edges.size()+3, VDmerge.edges.size()+1, VDmerge.edges.size(), VDmerge.edges.size()+2);

                    VDmerge.edges.add(leftUpEdge);
                    VDmerge.edges.add(leftDownEdge);
                    VDmerge.edges.add(rightUpEdge);
                    VDmerge.edges.add(rightDownEdge);
                    VDmerge.edges.add(perpendicularEdge);

                    voronoiTaskState.add(VDmerge);
                    return;
                }

            } else if (rightIntersectEdgeIndex==-1) {
                // TODO: case2 只有左圖有交點
                intersectionFirst = new Intersection(leftIntersection[0], leftIntersection[1], Intersection.Side.LEFT, leftIntersectEdgeIndex);

                { // TODO: 左圖交點消點
                    Edge intersectionEdge = VDleft.edges.get(intersectionFirst.edgeIndex);
                    Vertex startVertex = VDmerge.vertexs.get(intersectionEdge.start_vertex);
                    Vertex endVertex = VDmerge.vertexs.get(intersectionEdge.end_vertex);
                    GeneratorPoint leftGP = VDleft.generatorPoints.get(nowTangentLeftGPIndex);
                    GeneratorPoint rightGP = VDright.generatorPoints.get(nowTangentRightGPIndex);
                    deleteVertex(intersectionFirst, leftGP, rightGP, startVertex, endVertex); // 設定被刪的vertex
                }
                leftFirstIntersection = 0;
                System.out.println("交點("+intersectionFirst.x+","+intersectionFirst.y+")");
                HyperPlane.add(intersectionFirst); // 必須在 deleteVertex 後，才知道被刪的vertex

                nowTangentLeftGPIndex = leftNextEdge(nowTangentLeftGPIndex, leftIntersectEdgeIndex, VDleft); // 左邊找下一個生成點做切線
            } else if (leftIntersectEdgeIndex==-1) {
                // TODO: case3 只有右圖有交點
                intersectionFirst = new Intersection(rightIntersection[0], rightIntersection[1], Intersection.Side.RIGHT, rightIntersectEdgeIndex);

                { // TODO: 右圖交點消點
                    Edge intersectionEdge = VDright.edges.get(intersectionFirst.edgeIndex);
                    Vertex startVertex = VDmerge.vertexs.get(intersectionEdge.start_vertex+VDleft.vertexs.size());
                    Vertex endVertex = VDmerge.vertexs.get(intersectionEdge.end_vertex+VDleft.vertexs.size());
                    GeneratorPoint leftGP = VDleft.generatorPoints.get(nowTangentLeftGPIndex);
                    GeneratorPoint rightGP = VDright.generatorPoints.get(nowTangentRightGPIndex);
                    deleteVertex(intersectionFirst, leftGP, rightGP, startVertex, endVertex);
                }
                rightFirstIntersection = 0;
                System.out.println("交點("+intersectionFirst.x+","+intersectionFirst.y+")");
                HyperPlane.add(intersectionFirst); // 必須在 deleteVertex 後，才知道被刪的vertex

                nowTangentRightGPIndex = rightNextEdge(nowTangentRightGPIndex, rightIntersectEdgeIndex, VDright);  // 右邊找下一個生成點做切線
            } else {
                // TODO: case4 左右圖都有交點
                if (leftIntersection[1]>rightIntersection[1]) {
                    // TODO: case 4-1 左圖交點比較高
                    intersectionFirst = new Intersection(leftIntersection[0], leftIntersection[1], Intersection.Side.LEFT, leftIntersectEdgeIndex);

                    { // TODO: 左圖交點消點
                        Edge intersectionEdge = VDleft.edges.get(intersectionFirst.edgeIndex);
                        Vertex startVertex = VDmerge.vertexs.get(intersectionEdge.start_vertex);
                        Vertex endVertex = VDmerge.vertexs.get(intersectionEdge.end_vertex);
                        GeneratorPoint leftGP = VDleft.generatorPoints.get(nowTangentLeftGPIndex);
                        GeneratorPoint rightGP = VDright.generatorPoints.get(nowTangentRightGPIndex);
                        deleteVertex(intersectionFirst, leftGP, rightGP, startVertex, endVertex);
                    }
                    leftFirstIntersection = 0;
                    System.out.println("交點("+intersectionFirst.x+","+intersectionFirst.y+")");
                    HyperPlane.add(intersectionFirst); // 必須在 deleteVertex 後，才知道被刪的vertex

                    nowTangentLeftGPIndex = leftNextEdge(nowTangentLeftGPIndex, leftIntersectEdgeIndex, VDleft); // 左邊找下一個生成點做切線
                } else if (rightIntersection[1]>leftIntersection[1]) {
                    // TODO: case 4-2 右圖交點比較高
                    intersectionFirst = new Intersection(rightIntersection[0], rightIntersection[1], Intersection.Side.RIGHT, rightIntersectEdgeIndex);

                    { // TODO: 右圖交點消點
                        Edge intersectionEdge = VDright.edges.get(intersectionFirst.edgeIndex);
                        Vertex startVertex = VDmerge.vertexs.get(intersectionEdge.start_vertex+VDleft.vertexs.size());
                        Vertex endVertex = VDmerge.vertexs.get(intersectionEdge.end_vertex+VDleft.vertexs.size());
                        GeneratorPoint leftGP = VDleft.generatorPoints.get(nowTangentLeftGPIndex);
                        GeneratorPoint rightGP = VDright.generatorPoints.get(nowTangentRightGPIndex);
                        deleteVertex(intersectionFirst, leftGP, rightGP, startVertex, endVertex);
                    }
                    rightFirstIntersection = 0;
                    System.out.println("交點("+intersectionFirst.x+","+intersectionFirst.y+")");
                    HyperPlane.add(intersectionFirst); // 必須在 deleteVertex 後，才知道被刪的vertex

                    System.out.println("右邊找下一個生成點做切線");
                    nowTangentRightGPIndex = rightNextEdge(nowTangentRightGPIndex, rightIntersectEdgeIndex, VDright);  // 右邊找下一個生成點做切線

                } else {
                    // TODO: case 4-3 左圖右圖交點一樣高
                    intersectionFirst = new Intersection(leftIntersection[0], leftIntersection[1], Intersection.Side.LEFT, leftIntersectEdgeIndex);

                    { // TODO: 左圖交點消點
                        Edge intersectionEdge = VDleft.edges.get(intersectionFirst.edgeIndex);
                        Vertex startVertex = VDmerge.vertexs.get(intersectionEdge.start_vertex);
                        Vertex endVertex = VDmerge.vertexs.get(intersectionEdge.end_vertex);
                        GeneratorPoint leftGP = VDleft.generatorPoints.get(nowTangentLeftGPIndex);
                        GeneratorPoint rightGP = VDright.generatorPoints.get(nowTangentRightGPIndex);
                        deleteVertex(intersectionFirst, leftGP, rightGP, startVertex, endVertex);
                    }
                    leftFirstIntersection = 0;
                    System.out.println("交點("+intersectionFirst.x+","+intersectionFirst.y+")");
                    HyperPlane.add(intersectionFirst); // 必須在 deleteVertex 後，才知道被刪的vertex


                    intersectionFirst = new Intersection(rightIntersection[0], rightIntersection[1], Intersection.Side.RIGHT, rightIntersectEdgeIndex);

                    { // TODO: 右圖交點消點
                        Edge intersectionEdge = VDright.edges.get(intersectionFirst.edgeIndex);
                        Vertex startVertex = VDmerge.vertexs.get(intersectionEdge.start_vertex+VDleft.vertexs.size());
                        Vertex endVertex = VDmerge.vertexs.get(intersectionEdge.end_vertex+VDleft.vertexs.size());
                        GeneratorPoint leftGP = VDleft.generatorPoints.get(nowTangentLeftGPIndex);
                        GeneratorPoint rightGP = VDright.generatorPoints.get(nowTangentRightGPIndex);
                        deleteVertex(intersectionFirst, leftGP, rightGP, startVertex, endVertex);
                    }
                    rightFirstIntersection = 1;
                    System.out.println("交點("+intersectionFirst.x+","+intersectionFirst.y+")");
                    HyperPlane.add(intersectionFirst); // 必須在 deleteVertex 後，才知道被刪的vertex

                    nowTangentLeftGPIndex = leftNextEdge(nowTangentLeftGPIndex, leftIntersectEdgeIndex, VDleft); // 左邊找下一個生成點做切線
                    nowTangentRightGPIndex = rightNextEdge(nowTangentRightGPIndex, rightIntersectEdgeIndex, VDright);  // 右邊找下一個生成點做切線
                }
            }
        }

        if (HyperPlane.getLast().isLeft()) {
            System.out.println("交於左圖");
            Vertex s = VDleft.vertexs.get(VDleft.edges.get(HyperPlane.getLast().edgeIndex).start_vertex);
            Vertex e = VDleft.vertexs.get(VDleft.edges.get(HyperPlane.getLast().edgeIndex).end_vertex);
            System.out.println("有交點的edge:"+s.x+","+s.y+")");
            System.out.println("有交點的edge:"+e.x+","+e.y+")");
            System.out.println("交點:("+HyperPlane.getLast().x+","+HyperPlane.getLast().y+")");
        } else if (HyperPlane.getLast().isRight()) {
            System.out.println("交於右圖");
        }


        // TODO: 一直找下一個交點
        {
            LinkedList<Integer> leftPossibleEdges = new LinkedList<>(); // 左圖有可能與中垂線的邊
            LinkedList<Integer> rightPossibleEdges = new LinkedList<>(); // 右圖有可能與中垂線的邊



            Intersection intersectionLeft = null;
            Intersection intersectionRight = null;
            while (nowTangentLeftGPIndex!=lowerTangentLeftGPIndexInLeftVD || nowTangentRightGPIndex!=lowerTangentRightGPIndexInRightVD) {
//            System.out.println("LowerTangent 左:"+lowerTangent[0]+" 右:"+lowerTangent[1]);
//            System.out.println("現在切線 左:"+tangent[0]+" 右:"+tangent[1]);
                System.out.println("左圖Polygon編號:"+nowTangentLeftGPIndex);
                System.out.println("右圖Polygon編號:"+nowTangentRightGPIndex);

                // TODO: 找下一條中垂線
                // 中垂線出發點，中垂線由剛剛的交點出發
                float[] startPoint = new float[]{HyperPlane.getLast().x, HyperPlane.getLast().y};
                // 中垂線向量
                HPVectorDown = TwoDPlaneAlgo.getNormalVector(
                        VDright.generatorPoints.get(nowTangentRightGPIndex), VDleft.generatorPoints.get(nowTangentLeftGPIndex)
                );
                System.out.println("下一條中垂線的向量 ("+HPVectorDown[0]+","+HPVectorDown[1]+")");

                // TODO: 找有可能與中垂線相交的邊
                leftPossibleEdges.clear();
                rightPossibleEdges.clear();
                // TODO: 找左圖有可能與中垂線相交的邊
                leftPossibleEdges = VDleft.edgesAroundPolygon(nowTangentLeftGPIndex);
//                System.out.println("左圖有可能與中垂線相交的邊數量: "+leftPossibleEdges.size());
                // TODO: 找右圖有可能與中垂線相交的邊
                rightPossibleEdges = VDright.edgesAroundPolygon(nowTangentRightGPIndex);
                System.out.println("右圖有可能與中垂線相交的邊數量: "+rightPossibleEdges.size());

                // TODO: 找最高交點
                intersectionLeft = new Intersection(0, Float.NEGATIVE_INFINITY, Intersection.Side.LEFT, -1);
                intersectionRight = new Intersection(0, Float.NEGATIVE_INFINITY, Intersection.Side.RIGHT, -1);
                // TODO: 左圖找最高交點
                for (int leftEdgeIndex : leftPossibleEdges) {
                    if (HyperPlane.getLast().isLeft() && leftEdgeIndex == HyperPlane.getLast().edgeIndex) {
                        // 出發點不算
                        continue;
                    }
                    Edge leftEdge = VDleft.edges.get(leftEdgeIndex);
                    // TODO: 判斷是不是真的邊
                    if (leftEdge.deleted == true) {
                        continue;
                    }
                    if (leftEdge.real == false) {
                        continue;
                    }
                    System.out.println("左圖的polygon找到一條edge");
                    Vertex startVertex = VDleft.vertexs.get(leftEdge.start_vertex);
                    Vertex endVertex = VDleft.vertexs.get(leftEdge.end_vertex);

                    float[] tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(startPoint, HPVectorDown, startVertex, endVertex);
                    // TODO: 判斷有沒有交點
                    if (tempIntersection == null) { // 沒有交點
                        continue;
                    }
                    // TODO: 上一筆左右都有交點，這一次要忽略
                    if (tempIntersection[0]==HyperPlane.getLast().x && tempIntersection[1]==HyperPlane.getLast().y) {
                        continue;
                    }
                    // TODO: 判斷左圖的此交點有沒有比較高
                    if (tempIntersection[1] > intersectionLeft.y) {
//                    System.out.println("左邊有更新");
                        intersectionLeft.x = tempIntersection[0];
                        intersectionLeft.y = tempIntersection[1];
                        intersectionLeft.setLeft();
                        intersectionLeft.edgeIndex = leftEdgeIndex;
//                    System.out.println("左邊有可能交點，左邊第"+leftEdgeIndex+"條");
//                    System.out.println("("+intersectionLeft.x+","+intersectionLeft.y+")");
                    }
                }
                // TODO: 右圖找最高交點
                for (int rightEdgeIndex : rightPossibleEdges) {
                    if (HyperPlane.getLast().isRight() && rightEdgeIndex == HyperPlane.getLast().edgeIndex) {
                        // 出發點不算
                        continue;
                    }

                    Edge rightEdge = VDright.edges.get(rightEdgeIndex);
                    if (rightEdge.deleted == true) {
                        continue;
                    }
                    if (rightEdge.real == false) {
                        continue;
                    }
                    System.out.println("右圖的polygon找到一條edge");
                    Vertex startVertex = VDright.vertexs.get(rightEdge.start_vertex);
                    Vertex endVertex = VDright.vertexs.get(rightEdge.end_vertex);
                    System.out.println("從("+startVertex.x+","+startVertex.y+")到("+endVertex.x+","+endVertex.y+")");
                    float[] tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(startPoint, HPVectorDown, startVertex, endVertex);
                    // TODO: 判斷有沒有交點
                    if (tempIntersection == null) { // 沒有交點
                        System.out.println("沒有交點");
                        continue;
                    }
                    // TODO: 上一筆左右都有交點，這一次要忽略
                    if (tempIntersection[0]==HyperPlane.getLast().x && tempIntersection[1]==HyperPlane.getLast().y) {
                        continue;
                    }
                    // TODO: 判斷右圖的此交點有沒有比較高
                    if (tempIntersection[1] > intersectionRight.y) {
//                        System.out.println("右邊有更新");
                        intersectionRight.x = tempIntersection[0];
                        intersectionRight.y = tempIntersection[1];
                        intersectionRight.setRight();
                        intersectionRight.edgeIndex = rightEdgeIndex;
                    }
                }


                // TODO: 比較左右誰比較高，放進HP
//                System.out.println(intersectionLeft.x+","+intersectionLeft.y);
//                System.out.println(intersectionRight.x+","+intersectionRight.y);
                if (intersectionLeft.edgeIndex==-1) {
                    System.out.println("左圖找不到交點");
                }
                if (intersectionRight.edgeIndex==-1) {
                    System.out.println("右圖找不到交點");
                }

                if (intersectionLeft.y > intersectionRight.y) {
                    // 左邊先有交點
//                    System.out.println("左邊先有交點");

                    { // TODO: 左圖交點消點
                        Edge intersectionEdge = VDleft.edges.get(intersectionLeft.edgeIndex);
                        Vertex startVertex = VDmerge.vertexs.get(intersectionEdge.start_vertex);
                        Vertex endVertex = VDmerge.vertexs.get(intersectionEdge.end_vertex);
                        GeneratorPoint leftGP = VDleft.generatorPoints.get(nowTangentLeftGPIndex);
                        GeneratorPoint rightGP = VDright.generatorPoints.get(nowTangentRightGPIndex);
                        deleteVertex(intersectionLeft, leftGP, rightGP, startVertex, endVertex);
                    }
                    if (leftFirstIntersection==-1) {
                        leftFirstIntersection = HyperPlane.size();
                    }
                    HyperPlane.add(intersectionLeft); // 必須在 deleteVertex 後，才知道被刪的vertex

                    nowTangentLeftGPIndex = leftNextEdge(nowTangentLeftGPIndex, intersectionLeft.edgeIndex, VDleft);
                } else if (intersectionRight.y > intersectionLeft.y) {
                    // 右邊先有交點
//                    System.out.println("右邊先有交點");

                    { // TODO: 右圖交點消點
                        Edge intersectionEdge = VDright.edges.get(intersectionRight.edgeIndex);
                        Vertex startVertex = VDmerge.vertexs.get(intersectionEdge.start_vertex+VDleft.vertexs.size());
                        Vertex endVertex = VDmerge.vertexs.get(intersectionEdge.end_vertex+VDleft.vertexs.size());
                        GeneratorPoint leftGP = VDleft.generatorPoints.get(nowTangentLeftGPIndex);
                        GeneratorPoint rightGP = VDright.generatorPoints.get(nowTangentRightGPIndex);
                        deleteVertex(intersectionRight, leftGP, rightGP, startVertex, endVertex);
                    }
                    if (rightFirstIntersection == -1) {
                        rightFirstIntersection = HyperPlane.size();
                    }
                    HyperPlane.add(intersectionRight); // 必須在 deleteVertex 後，才知道被刪的vertex

                    nowTangentRightGPIndex = rightNextEdge(nowTangentRightGPIndex, intersectionRight.edgeIndex, VDright);
                } else if (intersectionRight.y == intersectionLeft.y) {
                    // 同時交
//                    System.out.println("同時有交點");
                    { // TODO: 左圖交點消點
                        Edge intersectionEdge = VDleft.edges.get(intersectionLeft.edgeIndex);
                        Vertex startVertex = VDmerge.vertexs.get(intersectionEdge.start_vertex);
                        Vertex endVertex = VDmerge.vertexs.get(intersectionEdge.end_vertex);
                        GeneratorPoint leftGP = VDleft.generatorPoints.get(nowTangentLeftGPIndex);
                        GeneratorPoint rightGP = VDright.generatorPoints.get(nowTangentRightGPIndex);
                        deleteVertex(intersectionLeft, leftGP, rightGP, startVertex, endVertex);
                    }
                    { // TODO: 右圖交點消點
                        Edge intersectionEdge = VDright.edges.get(intersectionRight.edgeIndex);
                        Vertex startVertex = VDmerge.vertexs.get(intersectionEdge.start_vertex+VDleft.vertexs.size());
                        Vertex endVertex = VDmerge.vertexs.get(intersectionEdge.end_vertex+VDleft.vertexs.size());
                        GeneratorPoint leftGP = VDleft.generatorPoints.get(nowTangentLeftGPIndex);
                        GeneratorPoint rightGP = VDright.generatorPoints.get(nowTangentRightGPIndex);
                        deleteVertex(intersectionRight, leftGP, rightGP, startVertex, endVertex);
                    }
                    if (leftFirstIntersection ==-1) {
                        leftFirstIntersection = HyperPlane.size();
                    }
                    HyperPlane.add(intersectionLeft); // 必須在 deleteVertex 後，才知道被刪的vertex
                    nowTangentLeftGPIndex = leftNextEdge(nowTangentLeftGPIndex, intersectionLeft.edgeIndex, VDleft); // 左邊找下一個生成點做切線

                    if (rightFirstIntersection ==-1) {
                        rightFirstIntersection = HyperPlane.size();
                    }
                    HyperPlane.add(intersectionRight); // 必須在 deleteVertex 後，才知道被刪的vertex
                    nowTangentRightGPIndex = rightNextEdge(nowTangentRightGPIndex, intersectionRight.edgeIndex, VDright);  // 右邊找下一個生成點做切線
                }
                System.out.println("交點("+HyperPlane.getLast().x+","+HyperPlane.getLast().y+")");
            }
        }


        // TODO: 已經找完所有交點放在HP
        System.out.println("HP");
        for (Intersection it : HyperPlane) {
            System.out.println("("+it.x+","+it.y+")");
        }

        // TODO: HP建構

        int previousVertexNums = VDmerge.vertexs.size(); // 目前有 0~previoudVertexNums-1
        int previousEdgeNums = VDmerge.edges.size();
        nowTangentLeftGPIndex = upperTangentLeftGPIndexInLeftVD;
        nowTangentRightGPIndex = upperTangentRightGPIndexInRightVD;
        GeneratorPoint GPleftup = VDleft.generatorPoints.get(upperTangentLeftGPIndexInLeftVD);
        System.out.println("左圖上切線的GP: index "+upperTangentLeftGPIndexInLeftVD+" :("+GPleftup.getX()+","+GPleftup.getY()+")");

        // TODO: 建立上無限點
        {
            float normVector[] = TwoDPlaneAlgo.getNormalVector(VDleft.generatorPoints.get(nowTangentLeftGPIndex), VDright.generatorPoints.get(nowTangentRightGPIndex));
            float upperTerminalVertexXY[] = TwoDPlaneAlgo.extendWithVector(HyperPlane.get(0).x, HyperPlane.get(0).y,
                    normVector[0], normVector[1], 10f);
            Vertex upperTerminalVertex = new Vertex(previousEdgeNums, true, upperTerminalVertexXY[0], upperTerminalVertexXY[1]);
            System.out.println("上無限點座標 ("+upperTerminalVertex.x+","+upperTerminalVertex.y+")");
            VDmerge.vertexs.add(upperTerminalVertex);
        }


        // TODO: 上無限點的虛邊更新
        int[] upperTerminal = new int[2]; // 兩個更新的虛邊的index
        {
            // TODO: 找出左右兩個上虛邊
            LinkedList<Integer> edgeIndexs = null;

            edgeIndexs= VDleft.edgesAroundPolygon(upperTangentLeftGPIndexInLeftVD); // 左圖上切線的多邊形的所有邊

            System.out.println("左Polygon的所有邊");
            // 左圖虛邊更新
            for (int edgeIndex : edgeIndexs) {
                System.out.println(edgeIndex+",");
                if (VDmerge.edges.get(edgeIndex).real==false) {
                    upperTerminal[0] = edgeIndex;
                }
            }
            edgeIndexs = VDright.edgesAroundPolygon(upperTangentRightGPIndexInRightVD); // 右圖上切線的多邊形的所有邊
            // 右圖虛邊更新
            for (int edgeIndex : edgeIndexs) {
                edgeIndex += VDleft.edges.size()  ;
                if (VDmerge.edges.get(edgeIndex).real==false) {
                    upperTerminal[1] = edgeIndex;
                }
            }

            // TODO: 更新左圖上虛邊
            {
                Edge edge = VDmerge.edges.get(upperTerminal[0]); // 左邊的上虛邊
                Vertex startVertex = VDmerge.vertexs.get(edge.start_vertex);
                Vertex endVertex = VDmerge.vertexs.get(edge.end_vertex);

                System.out.println("左圖上虛邊index"+upperTerminal[0]);
                System.out.println("從("+startVertex.x+","+startVertex.y+")到("+endVertex.x+","+endVertex.y+")");
                System.out.println("左polygon "+edge.left_polygon);
                System.out.println("右polygon "+edge.right_polygon);
                System.out.println("-------");

                Intersection firstIntersection = HyperPlane.get(leftFirstIntersection); // 左邊第一個交點
                Edge firstEdge = VDmerge.edges.get(firstIntersection.edgeIndex); // 左邊第一個交點的邊
                Vertex firstDeletedVertex = null;
                if (firstIntersection.isDeletedStart()) {
                    firstDeletedVertex = VDmerge.vertexs.get(firstEdge.start_vertex);
                } else if (firstIntersection.isDeletedEnd()){
                    firstDeletedVertex = VDmerge.vertexs.get(firstEdge.end_vertex);
                }


                if (startVertex.x==firstDeletedVertex.x && startVertex.y==firstDeletedVertex.y) {
                    edge.start_vertex = VDmerge.vertexs.size()-1; // 剛剛建HP的上假點
                    edge.cw_predecessor = upperTerminal[1]; // 右邊的虛邊
                    edge.ccw_predecessor = VDmerge.edges.size(); // 待會要建的HP的邊
                } else if (endVertex.x==firstDeletedVertex.x && endVertex.y==firstDeletedVertex.y) {
                    edge.end_vertex = VDmerge.vertexs.size()-1;
                    edge.cw_successor = upperTerminal[1];
                    edge.ccw_successor = VDmerge.edges.size();
                }
            }
            // TODO: 更新右圖上虛邊
            {
                Edge edge = VDmerge.edges.get(upperTerminal[1]); // 右邊的上虛邊
                Vertex startVertex = VDmerge.vertexs.get(edge.start_vertex);
                Vertex endVertex = VDmerge.vertexs.get(edge.end_vertex);

                Intersection firstIntersection = HyperPlane.get(rightFirstIntersection); // 右邊第一個交點
                Edge firstEdge = VDmerge.edges.get(firstIntersection.edgeIndex+VDleft.edges.size()); // 右邊第一個交點的邊
                Vertex firstDeletedVertex = null;
                if (firstIntersection.isDeletedStart()) {
                    firstDeletedVertex = VDmerge.vertexs.get(firstEdge.start_vertex);
                } else if (firstIntersection.isDeletedEnd()){
                    firstDeletedVertex = VDmerge.vertexs.get(firstEdge.end_vertex);
                }
                System.out.println("第一個交點消掉("+firstDeletedVertex.x+","+firstDeletedVertex.y+")");

                if (startVertex.x==firstDeletedVertex.x && startVertex.y==firstDeletedVertex.y) {
                    System.out.println("更新右上虛邊的start vertex ("+startVertex.x+","+startVertex.y+")");
                    edge.start_vertex = VDmerge.vertexs.size()-1; // 剛剛建HP的上假點
                    edge.ccw_predecessor = upperTerminal[0];
                    edge.cw_predecessor = VDmerge.edges.size(); // 待會要建的HP的邊
                } else if (endVertex.x==firstDeletedVertex.x && endVertex.y==firstDeletedVertex.y) {
                    System.out.println("更新右上虛邊的end vertex ("+endVertex.x+","+endVertex.y+")");
                    edge.end_vertex = VDmerge.vertexs.size()-1;
                    edge.ccw_successor = upperTerminal[0];
                    edge.cw_successor = VDmerge.edges.size(); // 待會要建的HP的邊
                }
            }

        }

        int[] upperEdge = upperTerminal;
        // TODO: HP交點建vertex & edge
        {
            nowTangentLeftGPIndex = upperTangentLeftGPIndexInLeftVD;
            nowTangentRightGPIndex = upperTangentRightGPIndexInRightVD;
            for (int i=0; i<HyperPlane.size(); i++) {
                // 建點
                Intersection intersection = HyperPlane.get(i);
                Vertex vertex = new Vertex(VDmerge.edges.size(), false, intersection.x, intersection.y);
//                System.out.println("新的vertex:("+vertex.x+","+vertex.y+")");
                VDmerge.vertexs.add(vertex);
                if (intersection.isLeft()) {
                    int updateEdgeIndex = intersection.edgeIndex;
                    Edge updateEdge = VDmerge.edges.get(updateEdgeIndex);
                    Vertex startVertex = VDmerge.vertexs.get(updateEdge.start_vertex);
                    Vertex endVertex = VDmerge.vertexs.get(updateEdge.end_vertex);
                    if (intersection.isDeletedStart()) {
                        updateEdge.start_vertex = VDmerge.vertexs.size()-1;
                        updateEdge.cw_predecessor = VDmerge.edges.size(); // 待會要建的HP的邊
                        updateEdge.ccw_predecessor = VDmerge.edges.size()+1;
                    } else if (intersection.isDeletedEnd()) {
                        updateEdge.end_vertex = VDmerge.vertexs.size()-1;
                        updateEdge.cw_successor = VDmerge.edges.size(); // 待會要建的HP的邊
                        updateEdge.ccw_successor = VDmerge.edges.size()+1;
                    }

                    Edge edge = new Edge(true, nowTangentRightGPIndex + VDleft.generatorPoints.size(), nowTangentLeftGPIndex, VDmerge.vertexs.size()-1, VDmerge.vertexs.size()-2, VDmerge.edges.size()+1, updateEdgeIndex, upperEdge[0], upperEdge[1]);

                    System.out.println("建立新的edge");
                    Vertex ss = VDmerge.vertexs.get(edge.start_vertex);
                    Vertex ee = VDmerge.vertexs.get(edge.end_vertex);
                    System.out.println("從("+ss.x+","+ss.y+")到("+ee.x+","+ee.y+")");
                    System.out.println("左polygon "+edge.left_polygon);
                    System.out.println("右polygon "+edge.right_polygon);
                    System.out.println("-------");

                    VDmerge.edges.add(edge);
                    upperEdge[0] = updateEdgeIndex;
                    upperEdge[1] = VDmerge.edges.size()-1;
                    nowTangentLeftGPIndex = leftNextEdge(nowTangentLeftGPIndex, intersection.edgeIndex, VDleft);
                } else if (intersection.isRight()) {
                    int updateEdgeIndex = intersection.edgeIndex+VDleft.edges.size();
                    Edge updateEdge = VDmerge.edges.get(updateEdgeIndex);
                    Vertex startVertex = VDmerge.vertexs.get(updateEdge.start_vertex);
                    Vertex endVertex = VDmerge.vertexs.get(updateEdge.end_vertex);

                    if (intersection.isDeletedStart()) {
                        updateEdge.start_vertex = VDmerge.vertexs.size()-1;
                        updateEdge.ccw_predecessor = VDmerge.edges.size(); // 待會要建的HP的邊
                        updateEdge.cw_predecessor = VDmerge.edges.size()+1;
                    } else if (intersection.isDeletedEnd()) {
                        updateEdge.end_vertex = VDmerge.vertexs.size()-1;
                        updateEdge.ccw_successor = VDmerge.edges.size(); // 待會要建的HP的邊
                        updateEdge.cw_successor = VDmerge.edges.size()+1;
                    }

                    Edge edge = new Edge(true, nowTangentRightGPIndex+VDleft.generatorPoints.size(), nowTangentLeftGPIndex, VDmerge.vertexs.size()-1, VDmerge.vertexs.size()-2, updateEdgeIndex, VDmerge.edges.size()+1, upperEdge[0], upperEdge[1]);

                    System.out.println("建立新的edge");
                    Vertex ss = VDmerge.vertexs.get(edge.start_vertex);
                    Vertex ee = VDmerge.vertexs.get(edge.end_vertex);
                    System.out.println("從("+ss.x+","+ss.y+")到("+ee.x+","+ee.y+")");
                    System.out.println("左polygon "+edge.left_polygon);
                    System.out.println("右polygon "+edge.right_polygon);
                    System.out.println("-------");

                    VDmerge.edges.add(edge);
                    upperEdge[0] = VDmerge.edges.size()-1;
                    upperEdge[1] = updateEdgeIndex;
                    nowTangentRightGPIndex = rightNextEdge(nowTangentRightGPIndex, intersection.edgeIndex, VDright);
                }
            }
        }

        // TODO: 建立下無限點
        {
            // TODO: 建點
            float normVector[] = TwoDPlaneAlgo.getNormalVector(VDright.generatorPoints.get(lowerTangentRightGPIndexInRightVD), VDleft.generatorPoints.get(lowerTangentLeftGPIndexInLeftVD));
            float[] lowerTerminalVertexXY = TwoDPlaneAlgo.extendWithVector(HyperPlane.getLast().x, HyperPlane.getLast().y,
                    normVector[0], normVector[1], 10f);
            Vertex lowerTerminalVertex = new Vertex(VDmerge.edges.size(), true, lowerTerminalVertexXY[0], lowerTerminalVertexXY[1]);

            VDmerge.vertexs.add(lowerTerminalVertex);
        }

        // TODO: 下無限點的虛邊更新
        int[] lowerTerminal = new int[2]; // 兩個更新的虛邊的index
        {
            // TODO: 找出左右兩個下虛邊
            LinkedList<Integer> edgeIndexs = null;

            edgeIndexs = VDleft.edgesAroundPolygon(lowerTangentLeftGPIndexInLeftVD); // 左圖下切線的多邊形的所有邊

            // 找左圖虛邊
            for (int edgeIndex : edgeIndexs) {
                Edge edge = VDmerge.edges.get(edgeIndex);
                if (edge.real == false) {
                    lowerTerminal[0] = edgeIndex;
                }
            }
            edgeIndexs = VDright.edgesAroundPolygon(lowerTangentRightGPIndexInRightVD); // 右圖下切線的多邊形的所有邊
            // 找右圖虛邊
            for (int edgeIndex : edgeIndexs) {
                edgeIndex += VDleft.edges.size();
                Edge edge = VDmerge.edges.get(edgeIndex);
                if (edge.real == false) {
                    lowerTerminal[1] = edgeIndex;
                }
            }
            // TODO: 更新左圖下虛邊
            {
                Edge edge = VDmerge.edges.get(lowerTerminal[0]); // 左邊的下虛邊
                Vertex startVertex = VDmerge.vertexs.get(edge.start_vertex);
                Vertex endVertex = VDmerge.vertexs.get(edge.end_vertex);

                if (startVertex.deleted) {
                    edge.start_vertex = VDmerge.vertexs.size()-1; // 剛剛建HP的上假點
                    edge.cw_predecessor = VDmerge.edges.size(); // 待會要建的HP的邊
                    edge.ccw_predecessor = lowerTerminal[1]; // 右邊的虛邊
                } else if (endVertex.deleted) {
                    edge.end_vertex = VDmerge.vertexs.size()-1;
                    edge.cw_successor = VDmerge.edges.size(); // 待會要建的HP的邊
                    edge.ccw_successor = lowerTerminal[1]; // 右邊的虛邊
                }
            }
            // TODO: 更新右圖下虛邊
            {
                Edge edge = VDmerge.edges.get(lowerTerminal[1]); // 右邊的下虛邊
                Vertex startVertex = VDmerge.vertexs.get(edge.start_vertex);
                Vertex endVertex = VDmerge.vertexs.get(edge.end_vertex);

                if (startVertex.deleted) {
                    edge.start_vertex = VDmerge.vertexs.size()-1; // 剛剛建HP的上假點
                    edge.ccw_predecessor = VDmerge.edges.size(); // 待會要建的HP的邊
                    edge.cw_predecessor = lowerTerminal[0]; // 左邊的虛邊
                } else if (endVertex.deleted) {
                    edge.end_vertex = VDmerge.vertexs.size()-1;
                    edge.ccw_successor = VDmerge.edges.size(); // 待會要建的HP的邊
                    edge.cw_successor = lowerTerminal[0]; // 左邊的虛邊
                }
            }

//            System.out.println("test5");
            // TODO: 建邊
            {
//                System.out.println("x:"+VDmerge.vertexs.get(VDmerge.vertexs.size()-1).x);
                Edge edge = new Edge(true, lowerTangentRightGPIndexInRightVD+VDleft.generatorPoints.size(), lowerTangentLeftGPIndexInLeftVD, VDmerge.vertexs.size()-1, VDmerge.vertexs.size()-2
                        , lowerTerminal[1], lowerTerminal[0], upperEdge[0], upperEdge[1]);
                VDmerge.edges.add(edge);
            }

        }

        // TODO: 消邊
        for (Edge edge : VDmerge.edges) {
            if (edge.real) {
                Vertex startVertex = VDmerge.vertexs.get(edge.start_vertex);
                Vertex endVertex = VDmerge.vertexs.get(edge.end_vertex);
                if (startVertex.terminal && endVertex.deleted ||
                        startVertex.deleted && endVertex.terminal) {
                    edge.deleted = true;
                }
            }
        }


//        System.out.println("左圖 left: "+VDleft.convexHull.get(VDleft.convexHull.left).getX()+","+VDleft.convexHull.get(VDleft.convexHull.left).getY());
//        System.out.println("左圖 right: "+VDleft.convexHull.get(VDleft.convexHull.right).getX()+","+VDleft.convexHull.get(VDleft.convexHull.right).getY());
//        System.out.println("右圖 left: "+VDright.convexHull.get(VDright.convexHull.left).getX()+","+VDright.convexHull.get(VDright.convexHull.left).getY());
//        System.out.println("右圖 right: "+VDright.convexHull.get(VDright.convexHull.right).getX()+","+VDright.convexHull.get(VDright.convexHull.right).getY());

        // TODO: 刪掉 兩個vertex有任一個被deleted的edge
        {
            for (Edge edge : VDmerge.edges) {
                if (edge.deleted == false) {
                    Vertex startVertex = VDmerge.vertexs.get(edge.start_vertex);
                    Vertex endVertex = VDmerge.vertexs.get(edge.end_vertex);
                    if (startVertex.deleted || endVertex.deleted) {
                        edge.deleted = true;
                    }
                }
            }

            // TODO: 更新polygon儲存deleted的edge
            for (int polygonIndex=0; polygonIndex<VDmerge.polygons.size()-1; polygonIndex++) {
                Polygon polygon = VDmerge.polygons.get(polygonIndex);
                Edge edge = VDmerge.edges.get(polygon.edge_around_polygon);
                if (edge.deleted == true) {
                    if (polygonIndex >= (VDleft.polygons.size()-1)) {
                        // 右圖的polygon
                        int rightPolygonIndex = polygonIndex-(VDleft.polygons.size()-1);
                        LinkedList<Integer> edges = VDright.edgesAroundPolygon(rightPolygonIndex);
                        for (int edgeIndex : edges) {
                            edgeIndex += VDleft.edges.size();
                            Edge newEdge = VDmerge.edges.get(edgeIndex);
                            if (newEdge.deleted==false && (newEdge.left_polygon==polygonIndex||newEdge.right_polygon==polygonIndex) && newEdge.real==true ) {
                                polygon.edge_around_polygon = edgeIndex;
                                break;
                            }
                        }
                    } else {
                        // 左圖的polygon
                        int leftPolygonIndex = polygonIndex;
                        LinkedList<Integer> edges = VDleft.edgesAroundPolygon(leftPolygonIndex);
                        for (int edgeIndex : edges) {
                            Edge newEdge = VDmerge.edges.get(edgeIndex);
                            if (newEdge.deleted==false && (newEdge.left_polygon==polygonIndex||newEdge.right_polygon==polygonIndex) && newEdge.real==true ) {
                                polygon.edge_around_polygon = edgeIndex;
                                break;
                            }
                        }
                    }
                }
            }
        }

        // merge完成
        voronoiTaskState.add(VDmerge);
        {
            for (int i=0; i<VDmerge.edges.size(); i++) {
                Edge edge = VDmerge.edges.get(i);
                if (edge.deleted) {
                    continue;
                }
                Vertex startVertex = VDmerge.vertexs.get(edge.start_vertex);
                Vertex endVertex = VDmerge.vertexs.get(edge.end_vertex);
                System.out.println("edge "+i);
                System.out.println("從("+startVertex.x+","+startVertex.y+")到("+endVertex.x+","+endVertex.y+")");
                System.out.println("左polygon "+edge.left_polygon);
                System.out.println("右polygon "+edge.right_polygon);
                System.out.println("起點順時針: "+edge.cw_predecessor);
                System.out.println("起點逆時針: "+edge.ccw_predecessor);
                System.out.println("終點順時針: "+edge.cw_successor);
                System.out.println("終點逆時針: "+edge.ccw_successor);
                System.out.println("-------");
            }

            for (int i=0; i<VDmerge.generatorPoints.size(); i++) {
                GeneratorPoint GPt = VDmerge.generatorPoints.get(i);
                System.out.println("polygon "+i);
                System.out.println("polygon "+i+" 的GP("+GPt.getX()+","+GPt.getY()+")");
                System.out.println("polygon "+i+" 儲存的edge: "+VDmerge.polygons.get(i).edge_around_polygon);
                System.out.println("-------");
            }
        }

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

    private static boolean canBuildFullBinaryTree(int gpNum) {
        // 判斷gpNum個生成點能不能剛好補成Full binary tree
        double K = Math.floor(Math.log(gpNum) / Math.log(2));
        double S = K-1;
        double T = Math.pow(2, S) - 1;
        double G = Math.pow(2, K+1) -1;

        return !(gpNum>= G-T+1 && gpNum<= G);
    }

    // 左邊有交點，左邊要換下一個生成點
    private static int leftNextEdge(int nowTangentLeftGP, int leftIntersectEdgeIndex, VoronoiDiagram VDleft) {
//        System.out.println("左邊要換下一個");
        Edge intersectEdge = VDleft.edges.get(leftIntersectEdgeIndex);
        int leftGP = intersectEdge.left_polygon;
        int rightGP = intersectEdge.right_polygon;
        if (nowTangentLeftGP == leftGP) {
            return rightGP;
        } else {
            return leftGP;
        }
    }

    // 右邊有交點，右邊要換下一個生成點
    private static int rightNextEdge(int nowTangentRightGP, int rightIntersectEdgeIndex, VoronoiDiagram VDright) {
//        System.out.println("右邊要換下一個");
        Edge intersectEdge = VDright.edges.get(rightIntersectEdgeIndex);
        int leftGP = intersectEdge.left_polygon;
        int rightGP = intersectEdge.right_polygon;
        if (nowTangentRightGP == leftGP) {
            return rightGP;
        } else {
            return leftGP;
        }
    }

    private static void deleteVertex(Intersection intersection, GeneratorPoint leftGP, GeneratorPoint rightGP, Vertex startVertex, Vertex endVertex) {
        // TODO: 1. 如果刪除 start vertex，回傳true， 2. 如果刪除 end vertex，回傳false
        // 獲取坐標
        double intersectionX = intersection.x;
        double intersectionY = intersection.y;

        // 檢查是否在兩個頂點之間
        boolean betweenX = isBetween(intersectionX, startVertex.x, endVertex.x);
        boolean betweenY = isBetween(intersectionY, startVertex.y, endVertex.y);

        // 如果在兩個頂點之間
        if (betweenX && betweenY) {
            // intersection位於vertex1和vertex2之間
        } else {
            // intersection不在vertex1和vertex2之間
            // 計算到每個頂點的距離
//            System.out.println("intersection不在vertex1和vertex2之間");
            double distanceToVertex1 = distance(intersectionX, intersectionY, startVertex.x, startVertex.y);
            double distanceToVertex2 = distance(intersectionX, intersectionY, endVertex.x, endVertex.y);

            // 判斷哪個頂點離交點更遠
            if (distanceToVertex1 > distanceToVertex2) {
                // vertex1離intersection更遠
                if (endVertex.terminal) {
//                    System.out.println("延伸("+endVertex.x+","+endVertex.y+")");
                    // 計算延伸點
                    float[] extendedPoint = TwoDPlaneAlgo.extendWithVector(intersection.x, intersection.y,
                            intersection.x - endVertex.x, intersection.y - endVertex.y, 10f);
                    // 更新vertex2的位置
                    endVertex.x = extendedPoint[0];
                    endVertex.y = extendedPoint[1];
//                    System.out.println("變成("+endVertex.x+","+endVertex.y+")");
                }
            } else {
                // vertex2離intersection更遠
                if (startVertex.terminal) {
//                    System.out.println("延伸("+startVertex.x+","+startVertex.y+")");
                    // 計算延伸點
                    float[] extendedPoint = TwoDPlaneAlgo.extendWithVector(intersection.x, intersection.y,
                            intersection.x - startVertex.x, intersection.y - startVertex.y, 10f);
                    // 更新vertex2的位置
                    startVertex.x = extendedPoint[0];
                    startVertex.y = extendedPoint[1];
//                    System.out.println("變成("+startVertex.x+","+startVertex.y+")");
                }
            }
        }
        // 已經將vertex1 endVertex 擺在 intersection兩側

        float[] deleteVector = new float[2];

        if (intersection.isLeft()) {
            // 計算從 leftGP 到 rightGP 的向量
            deleteVector[0] = rightGP.getX() - leftGP.getX(); // x 分量
            deleteVector[1] = rightGP.getY() - leftGP.getY(); // y 分量
        } else {
            // 計算從 rightGP 到 leftGP 的向量
            deleteVector[0] = leftGP.getX() - rightGP.getX(); // x 分量
            deleteVector[1] = leftGP.getY() - rightGP.getY(); // y 分量
        }

        float[] intersectionToVertex1Vector = new float[2];

        // 計算從 intersection 到 startVertex 的向量
        intersectionToVertex1Vector[0] = startVertex.x - intersection.x; // x 分量
        intersectionToVertex1Vector[1] = startVertex.y - intersection.y; // y 分量

        // deleteVector 和 intersectionToVertex1Vector 的內積
        float dotProduct = TwoDPlaneAlgo.dotProduct(deleteVector, intersectionToVertex1Vector);

        if (dotProduct > 0) {
            // startVertex 在 要刪除的那一邊
            startVertex.deleted = true;
            intersection.setDeletedStart();
            System.out.println("消掉start");
        } else {
            endVertex.deleted = true;
            intersection.setDeletedEnd();
            System.out.println("消掉end");
        }

    }

    private static boolean isBetween(double value, double end1, double end2) {
        return (value >= Math.min(end1, end2)) && (value <= Math.max(end1, end2));
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    // 移動terminal vertex直到，兩個vertex把intersection夾在中間
    private static void extendTerminalVertex(Vertex intersection, Vertex changeVertex, Vertex terminalVertex) {
        // 定義移動距離
        float moveDistance = 10f;

        // 計算原始方向向量 (從 changeVertex 到 terminalVertex)
        float originalDx = terminalVertex.x - changeVertex.x;
        float originalDy = terminalVertex.y - changeVertex.y;

        // 計算新方向向量 (從 intersection 到 terminalVertex)
        float newDx = terminalVertex.x - intersection.x;
        float newDy = terminalVertex.y - intersection.y;

        float[] newCoordinates = TwoDPlaneAlgo.extendWithVector(intersection.x, intersection.y, originalDx, originalDy, moveDistance);
        terminalVertex.x = newCoordinates[0];
        terminalVertex.y = newCoordinates[1];


    }
}
