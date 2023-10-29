package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.GeneratorPoint;
import dev.dockyu.voronoidiagram.datastruct.VoronoiDiagram;

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
//        else if (pointNum == 2) { // 兩個生成點
//            VoronoiDiagram twoPointVD = createTwoPointVD(Points.get(0), Points.get(1));
//            voronoiTaskState.add(twoPointVD);
//        }else if (pointNum == 3) { // 三個生成點
//            VoronoiDiagram threePointVD = createThreePointVD(Points.get(0), Points.get(1), Points.get(2));
//            voronoiTaskState.add(threePointVD);
//        }
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




    // base case 直接建構 小voronoi diagram
    private static VoronoiDiagram createOnePointVD(GeneratorPoint p1) {
        VoronoiDiagram voronoiDiagram = new VoronoiDiagram();
        // 沒有vertex
        // 一條edge
        // 只有兩個polygon P無限
        // convex hull
        return voronoiDiagram;
    }


    // merge 總共4個點或以上的voronoi diagram
    private static VoronoiDiagram mergeMultiPointVD(VoronoiDiagram VDleft, VoronoiDiagram VDright) {
        VoronoiDiagram VDmerge = new VoronoiDiagram();
        return VDmerge;
    }

    // 建立1個點的voronoi diagram
    private static VoronoiDiagram createTwoPointVD(GeneratorPoint p1, GeneratorPoint p2) {
        VoronoiDiagram voronoiDiagram = new VoronoiDiagram();
        return voronoiDiagram;
    }

    // merge 總共2個點的voronoi diagram
    private static VoronoiDiagram mergeTwoPointVD(VoronoiDiagram VDleft, VoronoiDiagram VDright) {
        VoronoiDiagram VDmerge = new VoronoiDiagram();
        return VDmerge;
    }

    // merge 總共3個點的voronoi diagram
    private static VoronoiDiagram mergeThreePointVD(VoronoiDiagram VDleft, VoronoiDiagram VDright) {
        VoronoiDiagram VDmerge = new VoronoiDiagram();
        return VDmerge;
    }
}
