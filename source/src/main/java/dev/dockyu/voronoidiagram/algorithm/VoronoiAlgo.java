package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.*;

import java.util.Arrays;
import java.util.LinkedList;

public class VoronoiAlgo {

    // 用多個生成點生成初始狀態(queue)
    public static void divide(LinkedList<GeneratorPoint> Points, LinkedList<VoronoiDiagram> voronoiTaskState) {
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

    private static GeneratorPoint[] sortThreeGeneratorPoint(GeneratorPoint point0, GeneratorPoint point1, GeneratorPoint point2) {
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
        return points;
    }

    // 排序三個點為順時針順序
    public static GeneratorPoint[] sortClockwise(GeneratorPoint point0, GeneratorPoint point1, GeneratorPoint point2) {
        float x1 = point0.getX();
        float y1 = point0.getY();
        float x2 = point1.getX();
        float y2 = point1.getY();
        float x3 = point2.getX();
        float y3 = point2.getY();

        float crossProduct = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);

        if (crossProduct > 0) {
            return new GeneratorPoint[] {point0, point2, point1};
        } else {
            return new GeneratorPoint[] {point0, point1, point2};
        }
    }

    // 計算外心
    public static float[] calculateCircumcenter(GeneratorPoint A, GeneratorPoint B, GeneratorPoint C) {
        float x1 = A.getX(), y1 = A.getY();
        float x2 = B.getX(), y2 = B.getY();
        float x3 = C.getX(), y3 = C.getY();

        // 計算三個邊的長度
        float a = (float)Math.sqrt((x2 - x3) * (x2 - x3) + (y2 - y3) * (y2 - y3));
        float b = (float)Math.sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1));
        float c = (float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

        // 計算面積
        float K = 0.5f * Math.abs(x1*(y2-y3) + x2*(y3-y1) + x3*(y1-y2));

        // 計算外心坐標
        float x = ((a*a*(b*b + c*c - a*a) * y1) + (b*b*(c*c + a*a - b*b) * y2) + (c*c*(a*a + b*b - c*c) * y3)) / (4 * K * K);
        float y = ((a*a*(b*b + c*c - a*a) * x1) + (b*b*(c*c + a*a - b*b) * x2) + (c*c*(a*a + b*b - c*c) * x3)) / (4 * K * K);

        float[] circumcenter = new float[2];
        circumcenter[0] = x; // 新的x坐標
        circumcenter[1] = y;  // 新的y坐標

        return circumcenter;
    }

    // start點到end點的法向量
    public static float[] calculateNormalVector(GeneratorPoint start, GeneratorPoint end) {
        float dx = end.getX() - start.getX(); // 計算方向向量的x分量
        float dy = end.getY() - start.getY(); // 計算方向向量的y分量

        // 計算左側法線（逆時針旋轉90度）
        float[] normal = new float[2];
        normal[0] = -dy; // 新的x坐標
        normal[1] = dx;  // 新的y坐標

        return normal;
    }

}
