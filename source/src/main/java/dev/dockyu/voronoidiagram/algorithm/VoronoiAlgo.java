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
        while (VDright==null) {
            voronoiTaskState.add(VDleft);
            VDleft = voronoiTaskState.poll();
            VDright = voronoiTaskState.poll();
        }

        VoronoiDiagram VDmerge = new VoronoiDiagram();
        // TODO: merge開始


        // TODO: 求上下切線
        System.out.println("find Tangent");
        int[] upperTangent = ConvexHullAlgo.getUpperTangent(VDleft.convexHull, VDright.convexHull);
        int[] lowerTangent = ConvexHullAlgo.getLowerTangent(VDleft.convexHull, VDright.convexHull); // 中止條件

        // TODO: 生成上下切線的4的點
        GeneratorPoint leftUpperGP = VDleft.generatorPoints.get(upperTangent[0]);
        GeneratorPoint leftLowerGP = VDleft.generatorPoints.get(lowerTangent[0]);
        GeneratorPoint rightUpperGP = VDright.generatorPoints.get(upperTangent[1]);
        GeneratorPoint rightLowerGP = VDright.generatorPoints.get(lowerTangent[1]);
//        System.out.println("左圖上切線的生成點: ("+leftUpperGP.getX()+","+leftUpperGP.getY()+")");
//        System.out.println("右圖下切線的生成點: ("+rightLowerGP.getX()+","+rightLowerGP.getY()+")");


        // TODO: 找上切線的中垂線
        float[] HPpoint = new float[]{ // 初始HP上一點
                (leftUpperGP.getX()+rightUpperGP.getX())/2,
                (leftUpperGP.getY()+rightUpperGP.getY())/2
        };
        float[] HPVectorUp = TwoDPlaneAlgo.getNormalVector(leftUpperGP, rightUpperGP);
        float[] HPVectorDown = TwoDPlaneAlgo.getNormalVector(rightUpperGP, leftUpperGP);

//        System.out.println("upperTangent left: "+VDleft.convexHull.get(upperTangent[0]).getX()+","+VDleft.convexHull.get(upperTangent[0]).getY());
//        System.out.println("upperTangent right: "+VDright.convexHull.get(upperTangent[1]).getX()+","+VDright.convexHull.get(upperTangent[1]).getY());
//        System.out.println("lowerTangent left: "+VDleft.convexHull.get(lowerTangent[0]).getX()+","+VDleft.convexHull.get(lowerTangent[0]).getY());
//        System.out.println("lowerTangent right: "+VDright.convexHull.get(lowerTangent[1]).getX()+","+VDright.convexHull.get(lowerTangent[1]).getY());

        // TODO: 找外圍的所有點
        System.out.println("find terminal vertex");
        LinkedList<Integer> leftTerminalVertexs = VDleft.vertexsAroundPolygon(VDleft.generatorPoints.size()); // 左圖外圍的所有點
        LinkedList<Integer> rightTerminalVertexs = VDright.vertexsAroundPolygon(VDright.generatorPoints.size()); // 右圖外圍的所有點
//        // 左
//        for (int i : leftTerminalVertexs) {
//            System.out.println("gp"+i);
//        }
//        // 右
//        for (int i : rightTerminalVertexs) {
//            System.out.println("gp"+i);
//        }

        // TODO: 找所有無限延伸的邊
        LinkedList<Integer> leftInfinityEdges = new LinkedList<>(); // 左圖無限延伸的邊
        LinkedList<Integer> rightInfinityEdges = new LinkedList<>(); // 右圖無限延伸的邊
        LinkedList<Integer> tempEdges = new LinkedList<>();

        for (int vertex : leftTerminalVertexs) {
            tempEdges = VDleft.edgesAroundVertex(vertex);
            for (int edge : tempEdges) {
                if (VDleft.edges.get(edge).real) {
                    leftInfinityEdges.add(edge);
                }
            }
        }
        for (int vertex : rightTerminalVertexs) {
            tempEdges = VDright.edgesAroundVertex(vertex);
            for (int edge : tempEdges) {
                if (VDright.edges.get(edge).real) {
                    rightInfinityEdges.add(edge);
                }
            }
        }
//        System.out.println("左圖無限延伸的邊");
//        for (int edge : leftInfinityEdges) {
//            System.out.println("e"+edge);
//        }
//        System.out.println("右圖無限延伸的邊");
//        for (int edge : rightInfinityEdges) {
//            System.out.println("e"+edge);
//        }

        // TODO: 判斷無限延伸線是不是會和HP相交
        int leftIntersectEdgeIndex = -1; // 左圖有最高交點的邊
        float[] leftIntersection = new float[]{0, Float.NEGATIVE_INFINITY}; // 左圖最高的交點
        int rightIntersectEdgeIndex = -1; // 右圖有最高交點的邊
        float[] rightIntersection = new float[]{0, Float.NEGATIVE_INFINITY}; // 右圖最高的交點

        float[] tempIntersection; // 暫存交點
        // TODO: 找左邊交點
        for (int edgeIndex : leftInfinityEdges) {
            Edge edge = VDleft.edges.get(edgeIndex); // 目前要判斷的邊
            Vertex startVertex = VDleft.vertexs.get(edge.start_vertex);
            Vertex endVertex = VDleft.vertexs.get(edge.end_vertex);
            // TODO: 判斷是不是真的邊
            if (edge.real == false) { // 是假的邊
                continue;
            }
            // TODO: 判斷有沒有交點
            // 把HP分成兩次求交點
            tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(HPpoint, HPVectorUp, startVertex, endVertex);
            if (tempIntersection == null) { // 與HP向上延伸沒有交點
                tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(HPpoint, HPVectorDown, startVertex, endVertex);
            }
            if (tempIntersection == null) { // 與HP沒有交點
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
        // TODO: 找右邊交點
        for (int edgeIndex : rightInfinityEdges) {
            Edge edge = VDright.edges.get(edgeIndex); // 目前要判斷的邊
            Vertex startVertex = VDright.vertexs.get(edge.start_vertex);
            Vertex endVertex = VDright.vertexs.get(edge.end_vertex);
            // TODO: 判斷是不是真的邊
            if (edge.real == false) { // 是假的邊
                continue;
            }
            // TODO: 判斷有沒有交點
            // 把HP分成兩次求交點
            tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(HPpoint, HPVectorUp, startVertex, endVertex);
            if (tempIntersection == null) { // 與HP向上延伸沒有交點
                tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(HPpoint, HPVectorDown, startVertex, endVertex);
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
        LinkedList<Intersection> HyperPlane = new LinkedList<>();
        Intersection intersectionFirst = null;
        int[] tangent = new int[]{upperTangent[0], upperTangent[1]}; // 下一條要做的切線(生成點跟生成點的切線)
        // tangent[0] 切線左邊生成點的index
        // tangent[1] 切線右邊生成點的index

        // TODO: 找出左圖+右圖最高的交點
        // TODO: 4種情況
        if (leftIntersectEdgeIndex==-1 && rightIntersectEdgeIndex==-1) {
            // TODO: case1 左右圖都沒有交點
//            System.out.println("左右圖都沒有交點");
        } else if (rightIntersectEdgeIndex==-1) {
            // TODO: case2 只有左圖有交點
//            System.out.println("只有左圖有交點");
//            System.out.println("左圖交點: ("+leftIntersection[0]+" "+leftIntersection[1]+")");
            intersectionFirst = new Intersection(leftIntersection[0], leftIntersection[1], Intersection.Side.LEFT, rightIntersectEdgeIndex);
            HyperPlane.add(intersectionFirst);
            leftNextEdge(tangent, leftIntersectEdgeIndex, VDleft); // 左邊找下一個生成點做切線
        } else if (leftIntersectEdgeIndex==-1) {
            // TODO: case3 只有右圖有交點
//            System.out.println("只有右圖有交點");
//            System.out.println("右圖交點: ("+rightIntersection[0]+" "+rightIntersection[1]+")");
            intersectionFirst = new Intersection(rightIntersection[0], rightIntersection[1], Intersection.Side.RIGHT, leftIntersectEdgeIndex);
            HyperPlane.add(intersectionFirst);
            rightNextEdge(tangent, rightIntersectEdgeIndex, VDright);  // 右邊找下一個生成點做切線
        } else {
            // TODO: case4 左右圖都有交點
            if (leftIntersection[1]>rightIntersection[1]) {
                // TODO: case 4-1 左圖交點比較高
//                System.out.println("左圖交點: ("+leftIntersection[0]+" "+leftIntersection[1]+")");
                intersectionFirst = new Intersection(leftIntersection[0], leftIntersection[1], Intersection.Side.LEFT, rightIntersectEdgeIndex);
                HyperPlane.add(intersectionFirst);
                leftNextEdge(tangent, leftIntersectEdgeIndex, VDleft); // 左邊找下一個生成點做切線
            } else if (rightIntersection[1]>leftIntersection[1]) {
                // TODO: case 4-2 右圖交點比較高
//                System.out.println("右圖交點: ("+rightIntersection[0]+" "+rightIntersection[1]+")");
                intersectionFirst = new Intersection(rightIntersection[0], rightIntersection[1], Intersection.Side.RIGHT, leftIntersectEdgeIndex);
                HyperPlane.add(intersectionFirst);
                rightNextEdge(tangent, rightIntersectEdgeIndex, VDright);  // 右邊找下一個生成點做切線
            } else {
                // TODO: case 4-3 左圖右圖交點一樣高
                intersectionFirst = new Intersection(leftIntersection[0], leftIntersection[1], Intersection.Side.LEFT, rightIntersectEdgeIndex);
                HyperPlane.add(intersectionFirst);
                leftNextEdge(tangent, leftIntersectEdgeIndex, VDleft); // 左邊找下一個生成點做切線
                intersectionFirst = new Intersection(rightIntersection[0], rightIntersection[1], Intersection.Side.RIGHT, leftIntersectEdgeIndex);
                HyperPlane.add(intersectionFirst);
                rightNextEdge(tangent, rightIntersectEdgeIndex, VDright);  // 右邊找下一個生成點做切線
//                System.out.println("兩圖同時交點: ("+rightIntersection[0]+" "+rightIntersection[1]+")");
            }
        }

        // TODO: 一直找下一個交點
        LinkedList<Integer> leftPossibleEdges = new LinkedList<>(); // 左圖有可能與中垂線的邊
        LinkedList<Integer> rightPossibleEdges = new LinkedList<>(); // 右圖有可能與中垂線的邊

        Intersection intersectionLeft = null;
        Intersection intersectionRight = null;
        while (tangent[0]!=lowerTangent[0] || tangent[1]!=lowerTangent[1]) {
//            System.out.println("LowerTangent 左:"+lowerTangent[0]+" 右:"+lowerTangent[1]);
//            System.out.println("現在切線 左:"+tangent[0]+" 右:"+tangent[1]);

            // TODO: 找下一條中垂線
            // 中垂線出發點，中垂線由剛剛的交點出發
            HPpoint[0] = HyperPlane.getLast().x;
            HPpoint[1] = HyperPlane.getLast().y;
            // 中垂線向量
            HPVectorDown = TwoDPlaneAlgo.getNormalVector(VDright.generatorPoints.get(tangent[1]), VDleft.generatorPoints.get(tangent[0]));
//            System.out.println("中垂向量 ("+HPVectorDown[0]+","+HPVectorDown[1]+")");

            // TODO: 找有可能與中垂線相交的邊
            leftPossibleEdges.clear();
            rightPossibleEdges.clear();
            tempEdges.clear();
            // TODO: 找左圖有可能與中垂線的邊
            leftPossibleEdges = VDleft.edgesAroundPolygon(tangent[0]);
            // TODO: 找右圖有可能與中垂線的邊
            rightPossibleEdges = VDright.edgesAroundPolygon(tangent[1]);
//            System.out.println("右圖有可能與中垂線的邊數量: "+rightPossibleEdges.size());

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
                if (leftEdge.real == false) {
                    continue;
                }
                Vertex startVertex = VDleft.vertexs.get(leftEdge.start_vertex);
                Vertex endVertex = VDleft.vertexs.get(leftEdge.end_vertex);
                tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(HPpoint, HPVectorDown, startVertex, endVertex);
                // TODO: 判斷有沒有交點
                if (tempIntersection == null) { // 沒有交點
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
                if (!HyperPlane.getLast().isLeft() && rightEdgeIndex == HyperPlane.getLast().edgeIndex) {
                    // 出發點不算
                    continue;
                }

                Edge rightEdge = VDright.edges.get(rightEdgeIndex);
                if (rightEdge.real == false) {
                    continue;
                }


                Vertex startVertex = VDright.vertexs.get(rightEdge.start_vertex);
                Vertex endVertex = VDright.vertexs.get(rightEdge.end_vertex);
//                System.out.println("start: ("+startVertex.x+","+startVertex.y+")");
//                System.out.println("end: ("+endVertex.x+","+endVertex.y+")");
                tempIntersection = TwoDPlaneAlgo.isIntersectWithHP(HPpoint, HPVectorDown, startVertex, endVertex);
                // TODO: 判斷有沒有交點
                if (tempIntersection == null) { // 沒有交點
                    continue;
                }
                // TODO: 判斷右圖的此交點有沒有比較高
                if (tempIntersection[1] > intersectionRight.y) {
                    System.out.println("右邊有更新");
                    intersectionRight.x = tempIntersection[0];
                    intersectionRight.y = tempIntersection[1];
                    intersectionRight.setRight();
                    intersectionRight.edgeIndex = rightEdgeIndex;
                }
            }


            // TODO: 比較左右誰比較高，放進HP
            if (intersectionLeft.y > intersectionRight.y) {
                // 左邊先有交點
                System.out.println("左邊先有交點");
                HyperPlane.add(intersectionLeft);
                leftNextEdge(tangent, intersectionLeft.edgeIndex, VDleft); // 左邊找下一個生成點做切線
            } else if (intersectionRight.y > intersectionLeft.y) {
                // 右邊先有交點
                System.out.println("右邊先有交點");
                HyperPlane.add(intersectionRight);
                rightNextEdge(tangent, intersectionRight.edgeIndex, VDright);  // 右邊找下一個生成點做切線
            } else if (intersectionRight.y == intersectionLeft.y) {
                // 同時交
                System.out.println("同時有交點");
                HyperPlane.add(intersectionLeft);
                leftNextEdge(tangent, leftIntersectEdgeIndex, VDleft); // 左邊找下一個生成點做切線
                HyperPlane.add(intersectionRight);
                rightNextEdge(tangent, rightIntersectEdgeIndex, VDright);  // 右邊找下一個生成點做切線
            }

        }

        // TODO: 已經找完所有交點放在HP
        System.out.println("HP");
        for (Intersection it : HyperPlane) {
            System.out.println("("+it.x+","+it.y+")");
        }

        // TODO: 合併並換Index
        int leftGPNums = VDleft.generatorPoints.size();
        int leftEdgeNums = VDleft.edges.size();
        int leftVertexNums = VDleft.vertexs.size();

        // TODO: 合併 generatorPoints
        // 直接合併就好
        VDmerge.generatorPoints.addAll(VDleft.generatorPoints);
        VDmerge.generatorPoints.addAll(VDright.generatorPoints);

        // TODO: 合併 polygon
        for (Polygon polygon : VDleft.polygons) { // 左
            VDmerge.polygons.add(new Polygon(polygon));
        }
        VDmerge.polygons.removeLast(); // 刪掉左邊最後一個
        for (Polygon polygon : VDright.polygons) { // 右
            Polygon rightPolygon = new Polygon(polygon);
            rightPolygon.edge_around_polygon += leftEdgeNums;
            VDmerge.polygons.add(rightPolygon);
        }

        // TODO: 合併 vertex
        for (Vertex vertex : VDleft.vertexs) {
            VDmerge.vertexs.add(new Vertex(vertex));
        }
        for (Vertex vertex : VDright.vertexs) {
            Vertex rightVertex = new Vertex(vertex);
            rightVertex.edge_around_vertex += leftEdgeNums;
            VDmerge.vertexs.add(rightVertex);
        }

        // TODO: HP消vertex
        for (Intersection intersection : HyperPlane) {
            int deleteEdgeIndex;
            if (intersection.isLeft()) {
                // 左圖的交點
                int edgeIndex = intersection.edgeIndex;
                int startVertexIndex = VDleft.edges.get(edgeIndex).start_vertex;
                int endVertexIndex = VDleft.edges.get(edgeIndex).end_vertex;
                Vertex startVertex = VDmerge.vertexs.get(startVertexIndex);
                Vertex endVertex = VDmerge.vertexs.get(endVertexIndex);
                if (startVertex.terminal && endVertex.terminal) {
                    // 都是假點，消近的
                    double startDistance = Math.pow(startVertex.x-intersection.x, 2) + Math.pow(startVertex.y-intersection.y, 2);
                    double endDistance = Math.pow(endVertex.x-intersection.x, 2) + Math.pow(endVertex.y-intersection.y, 2);
                    if (startDistance<endDistance) {
                        startVertex.deleted = true;
                        System.out.println("左圖消點1");
                    } else {
                        endVertex.deleted = true;
                        System.out.println("左圖消點2");
                    }
                } else if (!startVertex.terminal && !endVertex.terminal) {
                    // 都不是假點，消右邊的
                    if (startVertex.x > endVertex.x) {
                        startVertex.deleted = true;
                        System.out.println("左圖消點3");
                    } else {
                        endVertex.deleted = true;
                        System.out.println("左圖消點4");
                    }

                } else if (startVertex.terminal) {
                    // 只有start是假點，消start
                    System.out.println("左圖消點5");
                    startVertex.deleted = true;
                } else if (endVertex.terminal) {
                    // 只有end是假點，消end
                    System.out.println("左圖消點6");
                    endVertex.deleted = true;
                }
            } else {
                // 右圖的交點
                int edgeIndex = intersection.edgeIndex;
                int startVertexIndex = VDright.edges.get(edgeIndex).start_vertex+leftVertexNums;
                int endVertexIndex = VDright.edges.get(edgeIndex).end_vertex+leftVertexNums;
                Vertex startVertex = VDmerge.vertexs.get(startVertexIndex);
                Vertex endVertex = VDmerge.vertexs.get(endVertexIndex);
                if (startVertex.terminal && endVertex.terminal) {
                    // 都是假點，消近的
                    double startDistance = Math.pow(startVertex.x-intersection.x, 2) + Math.pow(startVertex.y-intersection.y, 2);
                    double endDistance = Math.pow(endVertex.x-intersection.x, 2) + Math.pow(endVertex.y-intersection.y, 2);
                    if (startDistance<endDistance) {
                        startVertex.deleted = true;
                        System.out.println("右圖消點1");
                    } else {
                        endVertex.deleted = true;
                        System.out.println("右圖消點2");
                    }
                } else if (!startVertex.terminal && !endVertex.terminal) {
                    // 都不是假點，消右邊的
                    if (startVertex.x < endVertex.x) {
                        startVertex.deleted = true;
                        System.out.println("右圖消點3");
                    } else {
                        endVertex.deleted = true;
                        System.out.println("右圖消點4");
                    }
                } else if (startVertex.terminal) {
                    // 只有start是假點，消start
                    startVertex.deleted = true;
                    System.out.println("右圖消點5");
                } else if (endVertex.terminal) {
                    // 只有end是假點，消end
                    endVertex.deleted = true;
                    System.out.println("右圖消點6");
                }
            }
        }

        // TODO: 合併 edge
        for (Edge edge : VDleft.edges) {
            Edge leftEdge = new Edge(edge);
            // P無限要換
            if (leftEdge.left_polygon==leftGPNums) {
                leftEdge.left_polygon = VDmerge.polygons.size()-1;
            } else if (leftEdge.right_polygon==leftGPNums) {
                leftEdge.right_polygon = VDmerge.polygons.size()-1;
            }
            VDmerge.edges.add(leftEdge);
        }
        for (Edge edge : VDright.edges) {
            Edge rightEdge = new Edge(edge);
            rightEdge.right_polygon += leftGPNums;
            rightEdge.left_polygon += leftGPNums;
            rightEdge.start_vertex += leftVertexNums;
            rightEdge.end_vertex += leftVertexNums;
            rightEdge.ccw_predecessor += leftEdgeNums;
            rightEdge.cw_predecessor += leftEdgeNums;
            rightEdge.ccw_successor += leftEdgeNums;
            rightEdge.cw_successor += leftEdgeNums;
            VDmerge.edges.add(rightEdge);
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

        // TODO: HP建構

        // TODO: 第一個點
        int previousVertexNums = VDmerge.vertexs.size(); // 目前有 0~previoudVertexNums-1
        int previousEdgeNums = VDmerge.edges.size();
        tangent[0] = upperTangent[0];
        tangent[1] = upperTangent[1];
        float normVector[] = TwoDPlaneAlgo.getNormalVector(VDleft.generatorPoints.get(tangent[0]), VDright.generatorPoints.get(tangent[1]));
        float upperVertexXY[] = TwoDPlaneAlgo.extendWithVector(HyperPlane.get(0).x, HyperPlane.get(0).y,
                normVector[0], normVector[1], 10f);
        Vertex upperVertex = new Vertex(previousEdgeNums, true, upperVertexXY[0], upperVertexXY[1]);
        VDmerge.vertexs.add(upperVertex);
            // TODO: 虛邊更新
        LinkedList<Integer> edgeIndexs = VDleft.edgesAroundPolygon(tangent[0]);
        int[] upperTerminal = new int[2];
        // 左邊
        for (int edgeIndex : edgeIndexs) {
            if (VDmerge.edges.get(edgeIndex).real==false) {
                upperTerminal[0] = edgeIndex;

            }
        }
        edgeIndexs = VDright.edgesAroundPolygon(tangent[1]);
        // 右邊
        for (int edgeIndex : edgeIndexs) {
            edgeIndex += leftEdgeNums;
            if (VDmerge.edges.get(edgeIndex).real==false) {
                upperTerminal[1] = edgeIndex;
            }
        }

        {
            Edge edge = VDmerge.edges.get(upperTerminal[0]); // 左邊的上虛邊
            Vertex startVertex = VDmerge.vertexs.get(edge.start_vertex);
            Vertex endVertex = VDmerge.vertexs.get(edge.end_vertex);

            if (startVertex.deleted) {
                edge.start_vertex = VDmerge.vertexs.size()-1; // 剛剛建HP的上假點
                edge.cw_predecessor = upperTerminal[1]; // 右邊的虛邊
                edge.ccw_predecessor = VDmerge.edges.size(); // 待會要建的HP的邊
            } else if (endVertex.deleted) {
                edge.end_vertex = VDmerge.vertexs.size()-1;
                edge.cw_successor = upperTangent[1];
                edge.ccw_successor = VDmerge.edges.size();
            }

            edge = VDmerge.edges.get(upperTerminal[1]); // 右邊的上虛邊
            startVertex = VDmerge.vertexs.get(edge.start_vertex);
            endVertex = VDmerge.vertexs.get(edge.end_vertex);

            if (startVertex.deleted) {
                edge.start_vertex = VDmerge.vertexs.size()-1; // 剛剛建HP的上假點
                edge.ccw_predecessor = upperTerminal[0];
                edge.cw_predecessor = VDmerge.edges.size(); // 待會要建的HP的邊
            } else if (endVertex.deleted) {
                edge.end_vertex = VDmerge.vertexs.size()-1;
                edge.ccw_successor = upperTerminal[0];
                edge.cw_successor = VDmerge.edges.size(); // 待會要建的HP的邊
            }
        }


        int[] upperEdge = upperTerminal;
        // TODO: HP交點建vertex edge
        {
            tangent[0] = upperTangent[0];
            tangent[1] = upperTangent[1];

            for (int i=0; i<HyperPlane.size(); i++) {
                // 建點
                Intersection intersection = HyperPlane.get(i);
                Vertex vertex = new Vertex(VDmerge.edges.size(), false, intersection.x, intersection.y);
                VDmerge.vertexs.add(vertex);
                if (intersection.isLeft()) {
                    int updateEdgeIndex = intersection.edgeIndex;
                    Edge updateEdge = VDmerge.edges.get(updateEdgeIndex);
                    Vertex startVertex = VDmerge.vertexs.get(updateEdge.start_vertex);
                    Vertex endVertex = VDmerge.vertexs.get(updateEdge.end_vertex);
                    if (startVertex.deleted) {
                        updateEdge.start_vertex = VDmerge.vertexs.size()-1;
                        updateEdge.cw_predecessor = VDmerge.edges.size(); // 待會要建的HP的邊
                        updateEdge.ccw_predecessor = VDmerge.edges.size()+1;
                    } else if (endVertex.deleted) {
                        updateEdge.end_vertex = VDmerge.vertexs.size()-1;
                        updateEdge.cw_successor = VDmerge.edges.size(); // 待會要建的HP的邊
                        updateEdge.ccw_successor = VDmerge.edges.size()+1;
                    }

                    Edge edge = new Edge(true, tangent[1]+leftGPNums, tangent[0], VDmerge.vertexs.size()-1, VDmerge.vertexs.size()-2,
                             updateEdgeIndex, VDmerge.edges.size()+1, upperEdge[0], upperEdge[1]);
                    VDmerge.edges.add(edge);
                    upperEdge[0] = updateEdgeIndex;
                    upperEdge[1] = VDmerge.edges.size()-1;
                    leftNextEdge(tangent, updateEdgeIndex, VDmerge);
                } else {
                    int updateEdgeIndex = intersection.edgeIndex+VDleft.edges.size();
                    Edge updateEdge = VDmerge.edges.get(updateEdgeIndex);
                    Vertex startVertex = VDmerge.vertexs.get(updateEdge.start_vertex);
                    Vertex endVertex = VDmerge.vertexs.get(updateEdge.end_vertex);
                    if (startVertex.deleted) {
                        updateEdge.start_vertex = VDmerge.vertexs.size()-1;
                        updateEdge.ccw_predecessor = VDmerge.edges.size(); // 待會要建的HP的邊
                        updateEdge.cw_predecessor = VDmerge.edges.size()+1;
                    } else if (endVertex.deleted) {
                        updateEdge.end_vertex = VDmerge.vertexs.size()-1;
                        updateEdge.ccw_successor = VDmerge.edges.size(); // 待會要建的HP的邊
                        updateEdge.cw_successor = VDmerge.edges.size()+1;
                    }

                    Edge edge = new Edge(true, tangent[1]+leftGPNums, tangent[0], VDmerge.vertexs.size()-1, VDmerge.vertexs.size()-2,
                            VDmerge.edges.size()+1, updateEdgeIndex, upperEdge[0], upperEdge[1]);
                    VDmerge.edges.add(edge);
                    upperEdge[0] = VDmerge.edges.size()-1;
                    upperEdge[1] = updateEdgeIndex;
                    rightNextEdge(tangent, updateEdgeIndex, VDmerge);
                }
            }
        }

        // TODO: 最後一個點
        {
            // TODO: 建點
            normVector = TwoDPlaneAlgo.getNormalVector(VDright.generatorPoints.get(lowerTangent[1]), VDleft.generatorPoints.get(lowerTangent[0]));
            System.out.println("vector: ("+normVector[0]+","+normVector[1]+")");
            float[] lowerVertexXY = TwoDPlaneAlgo.extendWithVector(HyperPlane.getLast().x, HyperPlane.getLast().y,
                    normVector[0], normVector[1], 10f);
            System.out.println("x:"+lowerVertexXY[0]+",y:"+lowerVertexXY[1]);
            Vertex lowerVertex = new Vertex(VDmerge.edges.size(), true, lowerVertexXY[0], lowerVertexXY[1]);

            VDmerge.vertexs.add(lowerVertex);
//            System.out.println("x:"+VDmerge.vertexs.get(VDmerge.vertexs.size()-1).x);

            // TODO: 找下虛邊
            int[] lowerTerminal = new int[2];
            edgeIndexs = VDmerge.edgesAroundPolygon(tangent[0]);
            for (int edgeIndex : edgeIndexs) {
                Edge edge = VDmerge.edges.get(edgeIndex);
                if (edge.real == false) {
                    lowerTerminal[0] = edgeIndex;
                }
            }
            edgeIndexs = VDmerge.edgesAroundPolygon(tangent[1]);
            for (int edgeIndex : edgeIndexs) {
                Edge edge = VDmerge.edges.get(edgeIndex);
                if (edge.real == false) {
                    lowerTerminal[1] = edgeIndex;
                }
            }
            // 更新下虛邊
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

                edge = VDmerge.edges.get(lowerTerminal[1]); // 右邊的下虛邊
                startVertex = VDmerge.vertexs.get(edge.start_vertex);
                endVertex = VDmerge.vertexs.get(edge.end_vertex);

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

            // TODO: 建邊
            {
                System.out.println("x:"+VDmerge.vertexs.get(VDmerge.vertexs.size()-1).x);
                Edge edge = new Edge(true, lowerTangent[1], lowerTangent[0], VDmerge.vertexs.size()-1, VDmerge.vertexs.size()-2
                , lowerTerminal[1], lowerTerminal[0], upperEdge[0], upperEdge[1]);
                VDmerge.edges.add(edge);
            }




        }




//        System.out.println("左圖 left: "+VDleft.convexHull.get(VDleft.convexHull.left).getX()+","+VDleft.convexHull.get(VDleft.convexHull.left).getY());
//        System.out.println("左圖 right: "+VDleft.convexHull.get(VDleft.convexHull.right).getX()+","+VDleft.convexHull.get(VDleft.convexHull.right).getY());
//        System.out.println("右圖 left: "+VDright.convexHull.get(VDright.convexHull.left).getX()+","+VDright.convexHull.get(VDright.convexHull.left).getY());
//        System.out.println("右圖 right: "+VDright.convexHull.get(VDright.convexHull.right).getX()+","+VDright.convexHull.get(VDright.convexHull.right).getY());



        // TODO: merge convex hull
        // 合併convex hull
        ConvexHullAlgo.merge(VDmerge.convexHull, VDleft.convexHull, VDright.convexHull, upperTangent, lowerTangent);
//        System.out.println("after merge points");
//        for (GeneratorPoint gp : VDmerge.convexHull.hull) {
//            System.out.println(gp.getX()+","+gp.getY());
//        }

        // merge完成
        voronoiTaskState.add(VDmerge);

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
    private static void leftNextEdge(int tangent[], int leftIntersectEdgeIndex, VoronoiDiagram VDleft) {
//        System.out.println("左邊要換下一個");
        tangent[0] = (tangent[0] == VDleft.edges.get(leftIntersectEdgeIndex).left_polygon) ?
                VDleft.edges.get(leftIntersectEdgeIndex).right_polygon :
                VDleft.edges.get(leftIntersectEdgeIndex).left_polygon;
    }

    // 右邊有交點，右邊要換下一個生成點
    private static void rightNextEdge(int tangent[], int rightIntersectEdgeIndex, VoronoiDiagram VDright) {
//        System.out.println("右邊要換下一個");
        tangent[1] = (tangent[1] == VDright.edges.get(rightIntersectEdgeIndex).left_polygon) ?
                VDright.edges.get(rightIntersectEdgeIndex).right_polygon :
                VDright.edges.get(rightIntersectEdgeIndex).left_polygon;
    }
}
