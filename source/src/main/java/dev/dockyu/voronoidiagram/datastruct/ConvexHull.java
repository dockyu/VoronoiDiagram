package dev.dockyu.voronoidiagram.datastruct;

public class ConvexHull {
    public CircularLinkedList<GeneratorPoint> hull; // 順時針方向
    public Integer right; // convex hull最右邊的點
    public Integer left; // convex hull最左邊的點

    public ConvexHull() {
        this.hull = new CircularLinkedList<>();
    }

    public GeneratorPoint get(int index) {
        return this.hull.get(index);
    }

    public int getNextIndex(int nowIndex) {
        nowIndex++;
        return nowIndex % this.hull.size();
    }

    public int getPreviousIndex(int nowIndex) {
        nowIndex--;
        if (nowIndex < 0) {
            nowIndex += this.hull.size();  // 確保索引總是正數
        }
        return nowIndex % this.hull.size();
    }

}
