package dev.dockyu.voronoidiagram.datastruct;

public class Edge {
    public boolean real; // 是否是真實存在
    public int right_polygon; // 邊的右邊的polygon
    public int left_polygon; // 邊的左邊的polygon
    public int start_vertex; // 邊的起始點
    public int end_vertex; // 邊的結束點
    public int cw_predecessor; // 邊的起始點順時針旋轉的下一個邊
    public int ccw_predecessor; // 邊的起始點逆時針旋轉的下一個邊
    public int cw_successor; // 邊的結束點順時針旋轉的下一個邊
    public int ccw_successor; // 邊的結束點逆時針旋轉的下一個邊

    public Edge(boolean real, int right_polygon, int left_polygon, int start_vertex, int end_vertex,
                int cw_predecessor, int ccw_predecessor, int cw_successor, int ccw_successor) {
        this.real = real;
        this.right_polygon = right_polygon;
        this.left_polygon = left_polygon;
        this.start_vertex = start_vertex;
        this.end_vertex = end_vertex;
        this.cw_predecessor = cw_predecessor;
        this.ccw_predecessor = ccw_predecessor;
        this.cw_successor = cw_successor;
        this.ccw_successor = ccw_successor;
    }
}
