package dev.dockyu.voronoidiagram.datastruct;

public class GeneratorPoint {
    int x; // x座標
    int y; // y座標

    public GeneratorPoint(int clickedX, int clickedY) {
        this.x = clickedX;
        this.y = clickedY;
    }

    public int getX() { return this.x;}
    public int getY() { return this.y;}
}
