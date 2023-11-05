package dev.dockyu.voronoidiagram.datastruct;

// merge時的交點
public class Intersection {
    // 定義一個枚舉型別表示方向
    public enum Side {
        LEFT, RIGHT
    }
    public float x;
    public float y;
    public int edgeIndex;
    private Side side;

    // 建構函數，用來初始化屬性
    public Intersection(float x, float y, Side side, int edgeIndex) {
        this.x = x;
        this.y = y;
        this.side = side;
        this.edgeIndex = edgeIndex;
    }

    // 判斷是否為左
    public boolean isLeft() {
        return side == Side.LEFT;
    }

    public void setLeft() {
        this.side = Side.LEFT;
    }

    public void setRight() {
        this.side = Side.RIGHT;
    }
}
