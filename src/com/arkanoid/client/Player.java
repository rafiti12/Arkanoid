package com.arkanoid.client;

/**
 * Represents player in a rectangle shape on the canvas.
 */
public class Player {
    private int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private final int speed;
    private int acceleration = 0;
    private final int xBoundary;

    public Player(int xPos, int yPos, int width, int height, int xBoundary, int speed) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.xBoundary = xBoundary;
        this.speed = speed;
    }

    /**
     * Moves the player depending on its speed and acceleration.
     * <p>
     * If player tries to cross left or right boundary, it moves him away.
     */
    public void move() {
        xPos += speed * acceleration;
        if(xPos <= 0) {
            xPos = 0;
        }
        else if (xPos >= xBoundary - width) {
            xPos = xBoundary - width;
        }
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
