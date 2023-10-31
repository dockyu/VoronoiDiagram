package dev.dockyu.voronoidiagram.datastruct;

public class GeneratorPoint {
    float x; // x座標
    float y; // y座標

    public GeneratorPoint(float clickedX, float clickedY) {
        this.x = clickedX;
        this.y = clickedY;
    }

    public float getX() { return this.x;}
    public float getY() { return this.y;}
}
