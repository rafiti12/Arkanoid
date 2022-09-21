package com.arkanoid.client;

import java.util.Random;


/**
 * Represents a single tile on the canvas
 */
public class Tile {
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private int hitsToDestroy;


    public Tile(int xPos, int yPos, int width, int height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        Random rand = new Random();
        hitsToDestroy = rand.nextInt(3) + 1;
    }

    public int gotHit() {
        hitsToDestroy -= 1;
        return hitsToDestroy;
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

    public int getHitsToDestroy() {
        return hitsToDestroy;
    }
}