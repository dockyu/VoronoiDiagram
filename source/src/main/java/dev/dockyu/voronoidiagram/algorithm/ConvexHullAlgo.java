package dev.dockyu.voronoidiagram.algorithm;

import dev.dockyu.voronoidiagram.datastruct.ConvexHull;
import dev.dockyu.voronoidiagram.datastruct.GeneratorPoint;

public class ConvexHullAlgo {

    // 找上切線
    public static int[] getUpperTangent(ConvexHull CHleft, ConvexHull CHright) {

        boolean leftStop = false;
        boolean rightStop = false;

        int leftNow = CHleft.right;
        int leftNext = CHleft.getPreviousIndex(leftNow); // 走一步就是退一步

        int rightNow = CHright.left;
        int rightNext = CHright.getNextIndex(rightNow); // 走一步就是向前一步

        // 左邊升到最高
        while (CHleft.get(leftNow).getX()==CHleft.get(leftNext).getX() && CHleft.get(leftNext).getY()>CHleft.get(leftNow).getY()) {
            leftNow = leftNext;
            leftNext = CHleft.getPreviousIndex(leftNow);
        }
        // 右邊升到最高
        while (CHright.get(rightNow).getX()==CHright.get(rightNext).getX() && CHright.get(rightNext).getY()<CHright.get(rightNow).getY()) {
            rightNow = rightNext;
            rightNext = CHright.getNextIndex(rightNow);
        }

        while(true) {
            // 左邊往回
            if ( TwoDPlaneAlgo.getSlope(CHleft.get(leftNext),CHright.get(rightNow))
                    < TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNow))) {
                // 左邊走一步變好
//                System.out.println("左邊走");
                leftNow = leftNext; // 走一步
                leftNext = CHleft.getPreviousIndex(leftNow); // 走一步
                
                rightStop = false; // 或許走了這步原本停止的右邊又可以繼續走

            } else if ( TwoDPlaneAlgo.getSlope(CHleft.get(leftNext),CHright.get(rightNow))
                    == TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNow)) && isCollinear(CHleft, CHright)) {
                // 左右VoronoiDiagram完全共線
                // 左邊有上升就好
                if (CHleft.get(leftNext).getX()>CHleft.get(leftNow).getX()
                        || (CHleft.get(leftNext).getX()==CHleft.get(leftNow).getX() && CHleft.get(leftNext).getY()>CHleft.get(leftNow).getY())) {
//                    System.out.println("error1");
//                    System.out.println(CHleft.get(leftNext).getY() +">"+ CHleft.get(leftNow).getY());

                    leftNow = leftNext; // 走一步
                    leftNext = CHleft.getPreviousIndex(leftNow); // 走一步

                    rightStop = false; // 或許走了這步原本停止的右邊又可以繼續走
                } else {
                    // 左邊走一步變差
                    leftStop = true; // 左邊停止
                }

            } else {
                // 左邊走一步變差
                leftStop = true; // 左邊停止
            }
            // 右邊往前
            if ( TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNext))
                    > TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNow))) {
                // 右邊走一步變好
                rightNow = rightNext; // 走一步
                rightNext = CHright.getNextIndex(rightNow); // 走一步
//                System.out.println("右邊走");
                leftStop = false; // 或許走了這步原本停止的左邊又可以繼續走
            } else if ( TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNext))
                    == TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNow)) && isCollinear(CHleft, CHright)) {
                if (CHright.get(rightNext).getX()<CHright.get(rightNow).getX()
                        || (CHright.get(rightNext).getX()==CHright.get(rightNow).getX()&&CHright.get(rightNext).getY()<CHright.get(rightNow).getY())) {
                    // 如果斜率一樣
                    // 右邊有上升就好
//                    System.out.println("error2");
                    rightNow = rightNext; // 走一步
                    rightNext = CHright.getNextIndex(rightNow); // 走一步
//                    System.out.println("右邊走");
                    leftStop = false; // 或許走了這步原本停止的左邊又可以繼續走
                } else {
                    // 右邊走一步變差
//                    System.out.println("右邊停");
                    rightStop = true; // 右邊停止
                }

            } else {
                // 右邊走一步變差
//                System.out.println("右邊點"+rightNow);
//                System.out.println("右邊停");
                rightStop = true; // 右邊停止
            }


            if (leftStop && rightStop) {
                // 兩邊都停止
                break; // 跳出
            }
        }
        // 左邊的index 右邊的index
//        System.out.println("右邊上點("+CHright.get(rightNow).getX()+","+CHright.get(rightNow).getY()+")");
        return new int[]{leftNow, rightNow};
    }

    public static int[] getLowerTangent(ConvexHull CHleft, ConvexHull CHright) {
        boolean leftStop = false;
        boolean rightStop = false;

        int leftNow = CHleft.right;
        int leftNext = CHleft.getNextIndex(leftNow); // 走一步就是向前一步

        int rightNow = CHright.left;
        int rightNext = CHright.getPreviousIndex(rightNow); // 走一步就是退一步

        // 左邊升到最高
        while (CHleft.get(leftNow).getX()==CHleft.get(leftNext).getX() && CHleft.get(leftNext).getY()>CHleft.get(leftNow).getY()) {
            leftNow = leftNext;
            leftNext = CHleft.getNextIndex(leftNow);
        }
        // 右邊升到最高
        while (CHright.get(rightNow).getX()==CHright.get(rightNext).getX() && CHright.get(rightNext).getY()<CHright.get(rightNow).getY()) {
            rightNow = rightNext;
            rightNext = CHright.getPreviousIndex(rightNow);
        }

        while(true) {
            // 左邊往前
            if ( TwoDPlaneAlgo.getSlope(CHleft.get(leftNext),CHright.get(rightNow))
                    > TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNow))) {
                // 左邊走一步變好
//                System.out.println("左邊走");
                leftNow = leftNext; // 走一步
                leftNext = CHleft.getNextIndex(leftNow); // 走一步

                rightStop = false;

            } else if ( TwoDPlaneAlgo.getSlope(CHleft.get(leftNext),CHright.get(rightNow))
                    == TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNow)) && isCollinear(CHleft, CHright)) {
                if (CHleft.get(leftNext).getX()<CHleft.get(leftNow).getX()
                        ||(CHleft.get(leftNext).getX()==CHleft.get(leftNow).getX()&&CHleft.get(leftNext).getY()<CHleft.get(leftNow).getY())) {
                    // 左右VoronoiDiagram完全共線
                    // 左邊有下降就好
//                    System.out.println("error3");
                    leftNow = leftNext; // 走一步
                    leftNext = CHleft.getNextIndex(leftNow); // 走一步

                    rightStop = false;
                }else {
                    // 左邊走一步變差
//                System.out.println("左邊停");
//                System.out.println("左邊停在"+CHleft.get(leftNow).getX()+"因為下一個點"+CHleft.get(leftNext).getX()+"斜率沒有變大");
                    leftStop = true; // 左邊停止
                }

            }else {
                // 左邊走一步變差
//                System.out.println("左邊停");
//                System.out.println("左邊停在"+CHleft.get(leftNow).getX()+"因為下一個點"+CHleft.get(leftNext).getX()+"斜率沒有變大");
                leftStop = true; // 左邊停止
            }
            // 右邊往前
            if ( TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNext))
                    < TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNow))) {
                // 右邊走一步變好
                rightNow = rightNext; // 走一步
                rightNext = CHright.getPreviousIndex(rightNow); // 走一步
//                System.out.println("右邊走");

                leftStop = false;
            } else if ( TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNext))
                    == TwoDPlaneAlgo.getSlope(CHleft.get(leftNow),CHright.get(rightNow)) && isCollinear(CHleft, CHright)) {
                if (CHright.get(rightNext).getX()>CHright.get(rightNow).getX()
                        ||(CHright.get(rightNext).getX()==CHright.get(rightNow).getX()&&CHright.get(rightNext).getY()>CHright.get(rightNow).getY())){
                    // 如果都是無限大或無限小
                    // 右邊有下降就好
//                    System.out.println("error4");
                    rightNow = rightNext; // 走一步
                    rightNext = CHright.getPreviousIndex(rightNow); // 走一步
//                System.out.println("右邊走");

                    leftStop = false;
                } else {
                    // 右邊走一步變差
//                System.out.println("右邊停");
                    rightStop = true; // 右邊停止
                }

            } else {
                // 右邊走一步變差
//                System.out.println("右邊停");
                rightStop = true; // 右邊停止
            }


            if (leftStop && rightStop) {
                // 兩邊都停止
                break; // 跳出
            }
        }
//        System.out.println("右邊下點("+CHright.get(rightNow).getX()+","+CHright.get(rightNow).getY()+")");
        // 左邊的index 右邊的index
        return new int[]{leftNow, rightNow};
    }

    public static void merge(ConvexHull CHmerge, ConvexHull CHleft, ConvexHull CHright, int[] upperTangent, int[] lowerTangent) {

        // 從右圖上切線的點開始，按順時針方向遍歷點，直到下切線的點

        int nowIndex;
        int count = -1; // 計數，紀錄目前是CHmerge第幾個點

        nowIndex = upperTangent[1]; // 右邊的上切點

        while(true) {
            CHmerge.hull.addLast(CHright.get(nowIndex));  // 將點加入新的凸包
            count++; // 紀錄

            if (nowIndex==CHright.right) {
                // 找到right
                CHmerge.right = count;
            }

            if (nowIndex==lowerTangent[1]) {
                // 現在是右邊的下切點，且已經放入CHmerge
                break;
            }

            // 下一個
            nowIndex = CHright.getNextIndex(nowIndex);
        }

        nowIndex = lowerTangent[0]; // 左邊的下切點

        while(true) {
            CHmerge.hull.addLast(CHleft.get(nowIndex));  // 將點加入新的凸包
            count++; // 紀錄

            if (nowIndex==CHleft.left) {
                // 找到left
                CHmerge.left = count;
            }

            if (nowIndex==upperTangent[0]) {
                // 現在是左邊的上切點，且已經放入CHmerge
                break;
            }

            // 下一個
            nowIndex = CHleft.getNextIndex(nowIndex);
        }

        // TODO: 判斷是否共線
        if (isCollinear(CHleft, CHright)) {
            CHmerge.setCollinear();
        }

    }

    private static boolean isCollinear(ConvexHull CHleft, ConvexHull CHright) {
        // TODO: 判斷是否所有點共線
        GeneratorPoint leftGPInCHleft = CHleft.hull.get(CHleft.left);
        GeneratorPoint rightGPInCHleft = CHleft.hull.get(CHleft.right);
        GeneratorPoint leftGPInCHright = CHright.hull.get(CHright.left);
        GeneratorPoint rightGPInCHright = CHright.hull.get(CHright.right);
        if (CHleft.collinear && CHright.collinear
                && (leftGPInCHleft.getY()-rightGPInCHleft.getY())*(leftGPInCHright.getX()-rightGPInCHright.getX()) == (leftGPInCHright.getY()-rightGPInCHright.getY())*(leftGPInCHleft.getX()-rightGPInCHleft.getX())
                && (rightGPInCHleft.getY()-leftGPInCHright.getY())*(leftGPInCHleft.getX()-rightGPInCHleft.getX()) == (leftGPInCHleft.getY()-rightGPInCHleft.getY())*(rightGPInCHleft.getX()-leftGPInCHright.getX())) {
            return true;
        }
        return false;
    }
}
