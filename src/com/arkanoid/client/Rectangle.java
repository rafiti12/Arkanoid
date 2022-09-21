package com.arkanoid.client;

/**
 * Represents rectangle made from 4 points on 2d area
 */
public class Rectangle {
    public final Point topLeft;
    public final Point topRight;
    public final Point bottomLeft;
    public final Point bottomRight;

    public Rectangle(int xPos, int yPos, int width, int height) {
        topLeft = new Point(xPos, yPos);
        topRight = new Point(xPos + width, yPos);
        bottomLeft = new Point(xPos, yPos + height);
        bottomRight = new Point(xPos + width, yPos + height);
    }

    public Rectangle(int xPos, int yPos, int radius) {
        int diameter = radius * 2;
        topLeft = new Point(xPos, yPos);
        topRight = new Point(xPos + diameter, yPos);
        bottomLeft = new Point(xPos, yPos + diameter);
        bottomRight = new Point(xPos + diameter, yPos + diameter);
    }
}
