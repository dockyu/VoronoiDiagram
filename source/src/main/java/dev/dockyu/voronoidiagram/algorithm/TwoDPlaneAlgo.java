package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.GeneratorPoint;
import dev.dockyu.voronoidiagram.datastruct.Intersection;
import dev.dockyu.voronoidiagram.datastruct.Vertex;

public class TwoDPlaneAlgo {
    // 將三個生成點順時鐘排序
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

    // 計算三點外心
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
//        System.out.println("外心座標 (x, y): (" + x + ", " + y + ")");

        float[] circumcenter = new float[2];
        circumcenter[0] = x; // 新的x坐標
        circumcenter[1] = y;  // 新的y坐標

        return circumcenter;
    }

    // 計算法向量
    public static float[] getNormalVector(GeneratorPoint start, GeneratorPoint end) {
        // 計算start點到end點的法向量
        float dx = end.getX() - start.getX(); // 計算方向向量的x分量
        float dy = end.getY() - start.getY(); // 計算方向向量的y分量

        // 計算左側法線（逆時針旋轉90度）
        float[] normal = new float[2];
        normal[0] = -dy; // 新的x坐標
        normal[1] = dx;  // 新的y坐標

        // 處理-0.0的情況
        normal[0] = (normal[0] == -0.0f) ? 0.0f : normal[0];
        normal[1] = (normal[1] == -0.0f) ? 0.0f : normal[1];

        return normal;
    }

    // 點向一個向量方向延伸一個距離
    public static float[] extendWithVector(float x, float y, float dx, float dy, float delta) {
        // (x,y)沿著向量(dx,dy)延伸length的新座標

        // 計算向量的長度
        float length = (float)Math.sqrt(dx * dx + dy * dy);

        // 計算單位向量
        float unitVector_x = dx / length;
        float unitVector_y = dy / length;

        // 計算新的座標
        float new_x = x + unitVector_x * delta;
        float new_y = y + unitVector_y * delta;

        float[] new_coordinate = new float[2];
        new_coordinate[0] = new_x;
        new_coordinate[1] = new_y;

        return new_coordinate;
    }

    // 計算兩點之間的距離
    public static float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    // 點和矩形(畫布)最遠的距離+100，保證可以顯現在畫布上
    public static float maxDistanceWithRectangle(float x, float y, float rect_left_bound, float rect_right_bound, float rect_down_bound, float rect_up_bound) {
        // 點和畫布x方向上最遠的距離
        float maxDistanceX = Math.max(Math.abs(x-rect_left_bound),Math.abs(x-rect_right_bound));
        // 點跟畫布y方向上最遠的距離
        float maxDistanceY = Math.max(Math.abs(y-rect_down_bound),Math.abs(y-rect_up_bound));

        float distance = (float) Math.sqrt(Math.pow(maxDistanceX, 2)+Math.pow(maxDistanceY, 2));

        return distance+100;
    }

    // 判斷兩個封閉線段的交點座標，如果不相交則回傳兩個負無限
    public static float[] intersectionOfTwoClosedLine(float L1startX, float L1startY, float L1endX, float L1endY,
                                                      float L2startX, float L2startY, float L2endX, float L2endY) {
        // 計算向量
        // 線可以表示為Ax+By=C
        // A1=y2−y1、B1=x1−x2B1=x1−x2、C1=A1×x1+B1×y1C1=A1×x1+B1×y1
        float A1 = L1endY - L1startY;
        float B1 = L1startX - L1endX;
        float C1 = A1 * L1startX + B1 * L1startY;

        float A2 = L2endY - L2startY;
        float B2 = L2startX - L2endX;
        float C2 = A2 * L2startX + B2 * L2startY;

        // 計算兩直線的交點
        float det = A1 * B2 - A2 * B1;
        if (det == 0) { // 方向向量方向一樣
            // 兩直線平行或重合
            return new float[] {Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
        } else {
            float x = (B2 * C1 - B1 * C2) / det;
            float y = (A1 * C2 - A2 * C1) / det;

            // 判斷交點是否在線段上
            if ((Math.min(L1startX, L1endX) <= x && x <= Math.max(L1startX, L1endX)) &&
                    (Math.min(L1startY, L1endY) <= y && y <= Math.max(L1startY, L1endY)) &&
                    (Math.min(L2startX, L2endX) <= x && x <= Math.max(L2startX, L2endX)) &&
                    (Math.min(L2startY, L2endY) <= y && y <= Math.max(L2startY, L2endY))) {

                // 檢查並修正 -0.0 的情況
                if (x == -0.0f) x = 0.0f;
                if (y == -0.0f) y = 0.0f;

                return new float[] {x, y};
            } else {
                return new float[] {Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
            }
        }
    }

    // 判斷點是否在矩形內
    public static boolean isPointInsideRectangle(float x, float y, float minX, float maxX, float minY, float maxY) {
        return (x >= minX && x <= maxX && y >= minY && y <= maxY);
    }

    // 算兩個生成點斜率
    public static float getSlope(GeneratorPoint leftGP, GeneratorPoint rightGP) {
        float x1 = leftGP.getX();
        float y1 = leftGP.getY();
        float x2 = rightGP.getX();
        float y2 = rightGP.getY();

        if (x2 == x1) {
            if (y1 > y2) {
                return Float.NEGATIVE_INFINITY;  // 左邊在上，斜率為無窮小
            } else {
                return Float.POSITIVE_INFINITY;  // 左邊在下，斜率為無窮大
            }
        }

        return (y2 - y1) / (x2 - x1);
    }

    // edge和半平面的交點，沒有交點回傳null，HP
    public static float[] isIntersectWithHP(float[] HPpoint, float[] HPvector, Vertex v1, Vertex v2) {

//        System.out.println("點(" + HPpoint[0] + "," + HPpoint[1] + ")向向量(" + HPvector[0] + "," + HPvector[1] + ")延伸");
//        System.out.println("線段 (" + v1.x + "," + v1.y + ")<->(" + v2.x + "," + v2.y + ")");

        // 計算直線方程 Ax + By = C 的係數
        float A1 = HPvector[1];
        float B1 = -HPvector[0];
        float C1 = A1 * HPpoint[0] + B1 * HPpoint[1]; // 使用 HPpoint 計算 C1

        float A2 = v2.y - v1.y;
        float B2 = v1.x - v2.x;
        float C2 = A2 * v1.x + B2 * v1.y; // 使用 v1 計算 C2

        float det = A1 * B2 - A2 * B1; // 行列式
        if (Math.abs(det) < 1e-6) {
//            System.out.println("狀況: 平行");
            return null; // 平行且不重合
        }

        float x = (C1 * B2 - C2 * B1) / det;
        float y = (A1 * C2 - A2 * C1) / det;

        // 檢查交點是否過於偏遠
        float threshold = 1.5E5f;
        float dx = x - HPpoint[0];
        float dy = y - HPpoint[1];
        if (dx * dx + dy * dy > threshold * threshold) {
//            System.out.println("狀況: 交點過於偏遠");
            return null;
        }

        float t1 = (x - HPpoint[0]) * HPvector[0] + (y - HPpoint[1]) * HPvector[1];
        float t2 = (x - v1.x) * (v2.x - v1.x) + (y - v1.y) * (v2.y - v1.y);
        float lengthSquared = (v2.x - v1.x) * (v2.x - v1.x) + (v2.y - v1.y) * (v2.y - v1.y);

        if (t1 < 0) {
//            System.out.println("狀況: 不在半平面的正向延伸方向上");
            return null;
        }

        if (!v1.terminal && !v2.terminal) {
            if (t2 < 0 || t2 > lengthSquared) {
//                System.out.println("狀況: 兩端封閉且不在線段上");
                return null;
            }
        } else if (!v1.terminal) {
            if (t2 < 0) {
//                System.out.println("狀況: 一端封閉（v1）且不在線段上");
                return null;
            }
        } else if (!v2.terminal) {
            if (t2 > lengthSquared) {
//                System.out.println("狀況: 一端封閉（v2）且不在線段上");
                return null;
            }
        }

//        System.out.println("狀況: 有交點");
        return new float[]{x, y};
    }

    public static float dotProduct(float[] vector1, float[] vector2) {
        // 檢查向量長度是否相等
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("向量長度必須相同");
        }

        float dot = 0;
        for (int i = 0; i < vector1.length; i++) {
            dot += vector1[i] * vector2[i];
        }
        return dot;
    }
}
