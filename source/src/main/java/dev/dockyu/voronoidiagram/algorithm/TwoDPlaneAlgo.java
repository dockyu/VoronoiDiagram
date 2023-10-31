package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.GeneratorPoint;

public class TwoDPlaneAlgo {
    public static GeneratorPoint[] sortThreePointClockwise(GeneratorPoint gp0, GeneratorPoint gp1, GeneratorPoint gp2) {
        // 將3個生成點按照順時針方向排序
        float x1 = gp0.getX();
        float y1 = gp0.getY();
        float x2 = gp1.getX();
        float y2 = gp1.getY();
        float x3 = gp2.getX();
        float y3 = gp2.getY();

        float crossProduct = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);

        if (crossProduct > 0) {
            return new GeneratorPoint[] {gp0, gp2, gp1};
        } else {
            return new GeneratorPoint[] {gp0, gp1, gp2};
        }
    }

    public static float[] getThreePointCircumcenter(GeneratorPoint A, GeneratorPoint B, GeneratorPoint C) {
        // 創建三個生成點的座標
        float x1 = A.getX(), y1 = A.getY();
        float x2 = B.getX(), y2 = B.getY();
        float x3 = C.getX(), y3 = C.getY();

        // 計算分母D，以確保不會除以零
        float D = 2 * ((x1 - x2) * (y2 - y3) - (y1 - y2) * (x2 - x3));

        // 計算外心的x和y座標
        float x = ((y2 - y3) * (x1 * x1 + y1 * y1 - x2 * x2 - y2 * y2) -
                (y1 - y2) * (x2 * x2 + y2 * y2 - x3 * x3 - y3 * y3)) / D;

        float y = ((x1 - x2) * (x2 * x2 + y2 * y2 - x3 * x3 - y3 * y3) -
                (x2 - x3) * (x1 * x1 + y1 * y1 - x2 * x2 - y2 * y2)) / D;

        // 印出外心座標
        System.out.println("外心座標 (x, y): (" + x + ", " + y + ")");

        float[] circumcenter = new float[2];
        circumcenter[0] = x; // 新的x坐標
        circumcenter[1] = y;  // 新的y坐標

        return circumcenter;
    }

    public static float[] getNormalVector(GeneratorPoint start, GeneratorPoint end) {
        // 計算start點到end點的法向量
        float dx = end.getX() - start.getX(); // 計算方向向量的x分量
        float dy = end.getY() - start.getY(); // 計算方向向量的y分量

        // 計算左側法線（逆時針旋轉90度）
        float[] normal = new float[2];
        normal[0] = -dy; // 新的x坐標
        normal[1] = dx;  // 新的y坐標

        return normal;
    }

    public static float[] getXYExtendVector(float x, float y, float v_x, float v_y, float delta) {
        // (x,y)沿著向量(v_x,v_y)延伸length的新座標

        // 計算向量的長度
        float length = (float)Math.sqrt(v_x * v_x + v_y * v_y);

        // 計算單位向量
        float unitVector_x = v_x / length;
        float unitVector_y = v_y / length;

        // 計算新的座標
        float new_x = x + unitVector_x * delta;
        float new_y = y + unitVector_y * delta;

        float[] new_coordinate = new float[2];
        new_coordinate[0] = new_x;
        new_coordinate[1] = new_y;

        return new_coordinate;
    }

    // 計算兩點之間的距離
    private static float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

}
