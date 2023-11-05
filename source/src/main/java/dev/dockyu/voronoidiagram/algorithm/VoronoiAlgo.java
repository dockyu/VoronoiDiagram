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
        Intersection intersection = null;
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
            intersection = new Intersection(leftIntersection[0], leftIntersection[1], Intersection.Side.LEFT, rightIntersectEdgeIndex);
            HyperPlane.add(intersection);
            leftNextEdge(tangent, leftIntersectEdgeIndex, VDleft); // 左邊找下一個生成點做切線
        } else if (leftIntersectEdgeIndex==-1) {
            // TODO: case3 只有右圖有交點
//            System.out.println("只有右圖有交點");
//            System.out.println("右圖交點: ("+rightIntersection[0]+" "+rightIntersection[1]+")");
            intersection = new Intersection(rightIntersection[0], rightIntersection[1], Intersection.Side.RIGHT, leftIntersectEdgeIndex);
            HyperPlane.add(intersection);
            rightNextEdge(tangent, rightIntersectEdgeIndex, VDright);  // 右邊找下一個生成點做切線
        } else {
            // TODO: case4 左右圖都有交點
            if (leftIntersection[1]>rightIntersection[1]) {
                // TODO: case 4-1 左圖交點比較高
//                System.out.println("左圖交點: ("+leftIntersection[0]+" "+leftIntersection[1]+")");
                intersection = new Intersection(leftIntersection[0], leftIntersection[1], Intersection.Side.LEFT, rightIntersectEdgeIndex);
                HyperPlane.add(intersection);
                leftNextEdge(tangent, leftIntersectEdgeIndex, VDleft); // 左邊找下一個生成點做切線
            } else if (rightIntersection[1]>leftIntersection[1]) {
                // TODO: case 4-2 右圖交點比較高
//                System.out.println("右圖交點: ("+rightIntersection[0]+" "+rightIntersection[1]+")");
                intersection = new Intersection(rightIntersection[0], rightIntersection[1], Intersection.Side.RIGHT, leftIntersectEdgeIndex);
                HyperPlane.add(intersection);
                rightNextEdge(tangent, rightIntersectEdgeIndex, VDright);  // 右邊找下一個生成點做切線
            } else {
                // TODO: case 4-3 左圖右圖交點一樣高
                intersection = new Intersection(leftIntersection[0], leftIntersection[1], Intersection.Side.LEFT, rightIntersectEdgeIndex);
                HyperPlane.add(intersection);
                leftNextEdge(tangent, leftIntersectEdgeIndex, VDleft); // 左邊找下一個生成點做切線
                intersection = new Intersection(rightIntersection[0], rightIntersection[1], Intersection.Side.RIGHT, leftIntersectEdgeIndex);
                HyperPlane.add(intersection);
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



//        System.out.println("左圖 left: "+VDleft.convexHull.get(VDleft.convexHull.left).getX()+","+VDleft.convexHull.get(VDleft.convexHull.left).getY());
//        System.out.println("左圖 right: "+VDleft.convexHull.get(VDleft.convexHull.right).getX()+","+VDleft.convexHull.get(VDleft.convexHull.right).getY());
//        System.out.println("右圖 left: "+VDright.convexHull.get(VDright.convexHull.left).getX()+","+VDright.convexHull.get(VDright.convexHull.left).getY());
//        System.out.println("右圖 right: "+VDright.convexHull.get(VDright.convexHull.right).getX()+","+VDright.convexHull.get(VDright.convexHull.right).getY());

        // TODO: merge generatorPoints
        // 直接合併就好
        VDmerge.generatorPoints.addAll(VDleft.generatorPoints);
        VDmerge.generatorPoints.addAll(VDright.generatorPoints);

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
