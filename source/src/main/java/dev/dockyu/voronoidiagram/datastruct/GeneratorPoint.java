package dev.dockyu.voronoidiagram.datastruct;

public class GeneratorPoint {
    float x; // x座標
    float y; // y座標

    public GeneratorPoint(float clickedX, float clickedY) {
        this.x = clickedX;
        this.y = clickedY;
    }

    public GeneratorPoint(GeneratorPoint other) {
        this.x = other.x;
        this.y = other.y;
    }

    public float getX() { return this.x;}
    public float getY() { return this.y;}
}
