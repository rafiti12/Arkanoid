package com.arkanoid.client;

/**
 * Representation of the ball image on canvas
 */
public class Ball {
    private int xPos;
    private int yPos;
    private int speed;
    private int angle = 165; // 0 - up, 90 - right, 180 - bottom, 270 - left
    private final int radius;
    private final int rightBoundary;
    private final int topBoundary;


    public Ball(int xPos, int yPos, int radius, int rightBoundary, int topBoundary) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.radius = radius;
        this.rightBoundary = rightBoundary;
        this.topBoundary = topBoundary;
    }

    /**
     * Changes position of the ball depending on its speed and angle.
     * <p>
     * Angle between 0 - 90 increases ball's xPosition and decreases its yPosition
     * Angle between 90 - 180 increases ball's xPosition and its yPosition
     * Angle between 180 - 270 decreases ball's xPosition and increases its yPosition
     * Angle between 270 - 360 decreases ball's xPosition and its yPosition
     */
    public void move() {
        // top-right direction
        if(angle >= 0 && angle <= 90) {
            double alpha = Math.toRadians(angle);
            int xDistance = (int) (speed * Math.sin(alpha));
            int yDistance = (int) (speed * Math.cos(alpha));
            xPos += xDistance;
            yPos -= yDistance;
        }

        // bottom-right direction
        else if (angle >= 90 && angle <= 180) {
            double alpha = Math.toRadians(angle - 90);
            int yDistance = (int) (speed * Math.sin(alpha));
            int xDistance = (int) (speed * Math.cos(alpha));
            xPos += xDistance;
            yPos += yDistance;
        }

        // bottom-left direction
        else if (angle >= 180 && angle <= 270) {
            double alpha = Math.toRadians(angle - 180);
            int xDistance = (int) (speed * Math.sin(alpha));
            int yDistance = (int) (speed * Math.cos(alpha));
            xPos -= xDistance;
            yPos += yDistance;
        }

        // top-left direction
        else if (angle >= 270 && angle <= 360) {
            double alpha = Math.toRadians(angle - 270);
            int yDistance = (int) (speed * Math.sin(alpha));
            int xDistance = (int) (speed * Math.cos(alpha));
            xPos -= xDistance;
            yPos -= yDistance;
        }
    }

    /**
     * Moves the ball out of the player's collision area and changes its angle.
     * <p>
     * Angle can be set between 0-60 if ball's center point is on the right side of the player.
     * The farther ball is from the player's center, the bigger angle is set.
     * <p>
     * Alternatively angle can be set between 300-360 if ball's center point is on the right side of the player.
     * The farther ball is from the player's center, the lower angle is set.
     *
     * @param player    Player object that collides with the ball
     */
    public void playerCollision(Player player) {
        int diameter = radius * 2;
        int playerCenter = player.getxPos() + player.getWidth() / 2;
        int ballCenter = xPos + radius;

        yPos = player.getyPos() - diameter - 1;

        int hitPoint = player.getxPos() + player.getWidth() - ballCenter;
        if(ballCenter >= playerCenter) {
            angle = 60 - 60 * hitPoint / (player.getWidth() / 2);
        }
        else {
            hitPoint -= player.getWidth() / 2;
            angle = 360 - (60 - 60 * ((player.getWidth() / 2) - hitPoint) / (player.getWidth() / 2));
        }
    }

    /**
     * Moves the ball out of the tile's collision area and changes its angle depending on from which side ball hit the tile
     * (determined by what part of the ball collided) and from which side it was coming (determined by its angle).
     *
     * @param t     Tile object that collides with the ball
     */
    public void tileCollision(Tile t) {
        int diameter = radius * 2;

        if(angle >= 0 && angle <= 90) {
            // hit from below
            if(t.getyPos() + t.getHeight() - yPos > xPos + diameter - t.getxPos()){
                xPos = t.getxPos() - diameter - 1;
                angle = 360 - angle;
            }
            // hit from left
            else {
                yPos = t.getyPos() + t.getHeight() + 1;
                angle = 180 - angle;
            }
        }

        else if(angle >= 90 && angle <= 180) {
            // hit from up
            if(yPos + diameter - t.getyPos() > xPos + diameter - t.getxPos()) {
                xPos = t.getxPos() - diameter - 1;
                angle = 360 - angle;

            }
            // hit from left
            else {
                yPos = t.getyPos() - diameter - 1;
                angle = 180 - angle;
            }
        }

        else if(angle >= 180 && angle <= 270) {
            // hit from up
            if(yPos + diameter - t.getyPos() > t.getxPos() + t.getWidth() - xPos) {
                xPos = t.getxPos() + t.getWidth() + 1;
                angle = 360 - angle;
            }
            // hit from right
            else {
                yPos = t.getyPos() - diameter - 1;
                angle = 180 + 360 - angle;
            }
        }

        else {
            // hit from below
            if(t.getyPos() + t.getHeight() - yPos > t.getxPos() + t.getWidth() - xPos) {
                xPos = t.getxPos() + t.getWidth() + 1;
                angle = 360 - angle;
            }
            // hit from right
            else {
                yPos = t.getyPos() + t.getHeight() + 1;
                angle = 180 + 360 - angle;
            }
        }
    }

    /**
     * Checks if ball collides with wall.
     * If it does, moves it outside and changes its angle accordingly.
     *
     * @return  true if ball collided with the wall, false otherwise
     */
    public boolean wallCollision() {
        int diameter = radius * 2;
        if(xPos + diameter >= rightBoundary) {
            xPos = rightBoundary - diameter - 1;
            angle = 360 - angle;
            return true;
        }
        if (xPos <= 0) {
            xPos = 1;
            angle = 360 - angle;
            return true;
        }
        if (yPos <= topBoundary) {
            yPos = topBoundary + 1;
            if(angle >= 0 && angle <= 90) {
                angle = 180 - angle;
            }
            else {
                angle = 180 + 360 - angle;
            }
            return true;
        }

        return false;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int getRadius() {
        return radius;
    }
}
