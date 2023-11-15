package dev.dockyu.voronoidiagram.datastruct;

// merge時的交點
public class Intersection {
    // 定義一個枚舉型別表示方向
    public enum Side {
        LEFT, RIGHT
    }
    public enum DeletedVertex {
        START, END
    }
    public float x;
    public float y;
    public int edgeIndex;
    private Side side;

    private DeletedVertex deletedVertex;

    // 建構函數，用來初始化屬性
    public Intersection(float x, float y, Side side, int edgeIndex) {
        this.x = x;
        this.y = y;
        this.side = side;
        this.edgeIndex = edgeIndex;
        this.deletedVertex = null;
    }

    // 判斷是否為左
    public boolean isLeft() {
        return side == Side.LEFT;
    }

    public boolean isRight() {
        return side == Side.RIGHT;
    }

    public void setLeft() {
        this.side = Side.LEFT;
    }

    public void setRight() {
        this.side = Side.RIGHT;
    }

    public boolean isDeletedStart() {
        return this.deletedVertex == DeletedVertex.START;
    }

    public boolean isDeletedEnd() {
        return this.deletedVertex == DeletedVertex.END;
    }

    public void setDeletedStart() {
        this.deletedVertex = DeletedVertex.START;
    }

    public void setDeletedEnd() {
        this.deletedVertex = DeletedVertex.END;
    }
}
