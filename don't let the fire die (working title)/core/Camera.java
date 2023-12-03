package core;

public class Camera {
    public double x, y, targetX, targetY, maxDistance, speed;
    public Camera(double x,double y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        maxDistance = 4;
        speed = 0.75d;
    }
    public void move() {
        this.x = this.targetX;
        this.y = this.targetY;
    }
}