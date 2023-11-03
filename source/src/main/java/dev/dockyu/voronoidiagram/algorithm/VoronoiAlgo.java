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
        int[] lowerTangent = ConvexHullAlgo.getLowerTangent(VDleft.convexHull, VDright.convexHull);

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
        float[] HPvector = TwoDPlaneAlgo.getNormalVector(leftUpperGP, rightUpperGP);

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
        LinkedList<Integer> tempEdges;

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
}
