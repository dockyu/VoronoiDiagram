package dev.dockyu.voronoidiagram.datastruct;

public class Edge {
    public int right_polygon; // 邊的右邊的polygon
    public int left_polygon; // 邊的左邊的polygon
    public int start_vertex; // 邊的起始點
    public int end_vertex; // 邊的結束點
    public int cw_predecessor; // 邊的起始點順時針旋轉的下一個邊
    public int ccw_predecessor; // 邊的起始點逆時針旋轉的下一個邊
    public int cw_successor; // 邊的結束點順時針旋轉的下一個邊
    public int ccw_successor; // 邊的結束點逆時針旋轉的下一個邊
}
