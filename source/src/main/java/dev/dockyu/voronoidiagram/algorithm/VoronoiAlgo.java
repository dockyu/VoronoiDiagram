package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.*;

import java.util.LinkedList;

public class VoronoiAlgo {

    // 用多個生成點生成初始狀態(queue)
    public static void divide(LinkedList<GeneratorPoint> Points, LinkedList<VoronoiDiagram> voronoiTaskState) {
//        System.out.println("VoronoiAlgo.java divide()");
        int pointNum = Points.size();

        // Base case，直接做出來
        if (pointNum == 3) {
            VoronoiDiagram threePointVD = VoronoiBaseCase.createThreePointVD(Points.get(0), Points.get(1), Points.get(2));
            voronoiTaskState.add(threePointVD);
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

            divide(leftPoints, voronoiTaskState);
            divide(rightPoints, voronoiTaskState);
        }//else { // pointNum<1，有問題
//            System.out.println("divide 出錯");
//        }

    }

    public static void merge(LinkedList<VoronoiDiagram> voronoiTaskState) {
        System.out.println("merge");
        VoronoiDiagram VDleft = voronoiTaskState.poll();
        VoronoiDiagram VDright = voronoiTaskState.poll();

        // 如果left VD真實位置在右邊就交換
        if (VDleft.generatorPoints.get(0).getX() > VDright.generatorPoints.get(0).getX()) { // VDleft換成左邊
            System.out.println("change left and right");
            VoronoiDiagram temp = VDleft;
            VDleft = VDright;
            VDright = temp;
        }
        VoronoiDiagram VDmerge = new VoronoiDiagram();
        // merge開始

        // 求上下切線
        System.out.println("find Tangent");
        System.out.println("left points");
//        for (GeneratorPoint gp : VDleft.convexHull.hull) {
//            System.out.println(gp.getX()+","+gp.getY());
//        }
        System.out.println("left: "+VDleft.convexHull.hull.get(VDleft.convexHull.left).getX()+","+VDleft.convexHull.hull.get(VDleft.convexHull.left).getY());
        System.out.println("left: "+VDleft.convexHull.hull.get(VDleft.convexHull.right).getX()+","+VDleft.convexHull.hull.get(VDleft.convexHull.right).getY());
        System.out.println("right points");
//        for (GeneratorPoint gp : VDright.convexHull.hull) {
//            System.out.println(gp.getX()+","+gp.getY());
//        }
        System.out.println("right: "+VDright.convexHull.hull.get(VDright.convexHull.left).getX()+","+VDright.convexHull.hull.get(VDright.convexHull.left).getY());
        System.out.println("right: "+VDright.convexHull.hull.get(VDright.convexHull.right).getX()+","+VDright.convexHull.hull.get(VDright.convexHull.right).getY());

        int[] upperTangent = ConvexHullAlgo.getUpperTangent(VDleft.convexHull, VDright.convexHull);
        int[] lowerTangent = ConvexHullAlgo.getLowerTangent(VDleft.convexHull, VDright.convexHull);

        System.out.println("upperTangent: "+VDleft.convexHull.hull.get(upperTangent[0]).getX()+","+VDleft.convexHull.hull.get(upperTangent[0]).getY());
        System.out.println("upperTangent: "+VDright.convexHull.hull.get(upperTangent[1]).getX()+","+VDright.convexHull.hull.get(upperTangent[1]).getY());
        System.out.println("lowerTangent: "+VDleft.convexHull.hull.get(lowerTangent[0]).getX()+","+VDleft.convexHull.hull.get(lowerTangent[0]).getY());
        System.out.println("lowerTangent: "+VDright.convexHull.hull.get(lowerTangent[1]).getX()+","+VDright.convexHull.hull.get(lowerTangent[1]).getY());

        // TODO:找第一個點

        // merge generatorPoints
        System.out.println("merge generator Points");
        VDmerge.generatorPoints.addAll(VDleft.generatorPoints);
        VDmerge.generatorPoints.addAll(VDright.generatorPoints);

        // merge convex hull
        System.out.println("merge convex hull");
        VDmerge.convexHull = ConvexHullAlgo.merge(VDleft.convexHull, VDright.convexHull, upperTangent, lowerTangent);
        System.out.println("after merge points");
        for (GeneratorPoint gp : VDmerge.convexHull.hull) {
            System.out.println(gp.getX()+","+gp.getY());
        }
        
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


}
