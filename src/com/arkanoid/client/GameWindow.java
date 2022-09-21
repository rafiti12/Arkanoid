package com.arkanoid.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  Class that Controls and shows the Game
 */
public class GameWindow {
    private final Canvas canvas;
    private Context2d context;
    private final String canvasHolderId;
    private final int height = Window.getClientHeight();
    private final int width = Window.getClientWidth();
    private final List<Button> buttonList = new ArrayList<>();
    private final List<Tile> tileList = new ArrayList<>();
    // 0,1,2 - tiles; 3 - background; 4 - player; 5 - ball; 6 - game_over; 7 - win;
    private final List<Image> imageList = new ArrayList<>();
    private Button lifesTextButton;
    private Button timeTextButton;
    private Timer mainTimer;
    private Timer timeMeterTimer;
    private int loadedImages = 0;
    private Player player;
    private Ball ball;
    private int lifes = 3;
    private int time = 120; // in seconds
    private final int rows = 3;
    private final int columns = 8;


    public GameWindow(Canvas canvas, String canvasHolderId) {
        this.canvas = canvas;
        this.canvasHolderId = canvasHolderId;
        prepareGame();
    }

    private void prepareImages() {
        Image yellowTileImage = new Image("images/yellow_tile.png");
        Image blueTileImage = new Image("images/blue_tile.png");
        Image redTileImage = new Image("images/red_tile.png");
        Image backgroundImage = new Image("images/background.png");
        Image playerImage = new Image("images/player.png");
        Image ballImage = new Image("images/ball.png");
        Image loseImage = new Image("images/game_over.png");
        Image winImage = new Image("images/win.jpg");
        imageList.add(yellowTileImage);
        imageList.add(blueTileImage);
        imageList.add(redTileImage);
        imageList.add(backgroundImage);
        imageList.add(playerImage);
        imageList.add(ballImage);
        imageList.add(loseImage);
        imageList.add(winImage);
    }

    private void prepareGame() {
        prepareImages();
        // Wait for all images to load before preparing
        for (Image image : imageList) {
            image.setVisible(false);
            RootPanel.get().add(image);
            image.addLoadHandler(event -> {
                loadedImages += 1;
                if(loadedImages == imageList.size()) {
                    createCanvas();
                    createMenu();
                }
            });
        }
    }

    private void createCanvas() {
        // Make canvas fill the browser's window
        canvas.setWidth(width + "px");
        canvas.setHeight(height + "px");
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);

        RootPanel.get(canvasHolderId).add(canvas);
        context = canvas.getContext2d();

        // Paint Background
        context.beginPath();
        context.drawImage(ImageElement.as(imageList.get(3).getElement()), 0, 0, width, height);

        // Paint UI - 100% width 10% height
        context.setFillStyle(CssColor.make("black"));
        int uiHeight = height / 10;
        context.fillRect(0, 0, width, uiHeight);

        int xPos = width / 200;
        int yPos = height / 40;
        int fontSize = height / 20;
        lifesTextButton = new Button("Lifes: " + lifes);
        lifesTextButton.getElement().setClassName("ui");
        lifesTextButton.getElement().getStyle().setLeft(xPos, Style.Unit.PX);
        lifesTextButton.getElement().getStyle().setTop(yPos, Style.Unit.PX);
        lifesTextButton.getElement().getStyle().setFontSize(fontSize, Style.Unit.PX);
        RootPanel.get(canvasHolderId).add(lifesTextButton);

        timeTextButton = new Button("Time: " + time);
        timeTextButton.getElement().setClassName("ui");
        timeTextButton.getElement().getStyle().setTop(yPos, Style.Unit.PX);
        timeTextButton.getElement().getStyle().setFontSize(fontSize, Style.Unit.PX);
        // set width to center text
        int timeButtonWidth = width / 5;
        timeTextButton.getElement().getStyle().setWidth(timeButtonWidth, Style.Unit.PX);
        xPos = width / 2 - timeButtonWidth / 2;
        timeTextButton.getElement().getStyle().setLeft(xPos, Style.Unit.PX);
        RootPanel.get(canvasHolderId).add(timeTextButton);


        // Create Tile objects and paint their images
        int tileWidth = width / columns;
        int tileHeight = height / 20; // 1 row = 5% of the screen
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                xPos = tileWidth * j;
                yPos = uiHeight + tileHeight * i;
                Tile tile = new Tile(xPos, yPos, tileWidth, tileHeight);
                tileList.add(tile);
                ImageElement imgEl = ImageElement.as(imageList.get(tile.getHitsToDestroy() - 1).getElement());
                context.drawImage(imgEl, xPos, yPos, tileWidth, tileHeight);
            }
        }

        // Create Player and paint its image - 12.5% width 2% height
        int playerWidth = width / 8;
        int playerHeight = height / 50;
        xPos = width / 2 - playerWidth / 2; // center of the x-axis
        yPos = height - height / 10;
        int speed = playerWidth / 5;
        player = new Player(xPos, yPos, playerWidth, playerHeight, width, speed);
        context.drawImage(ImageElement.as(imageList.get(4).getElement()), xPos, yPos, playerWidth, playerHeight);

        // Create Ball and paint its image over player
        int ballRadius = height / 50; // 2% of window's height
        xPos = width / 2 - ballRadius; // center of the x-axis
        yPos = height - height / 2;
        ball = new Ball(xPos, yPos, ballRadius, width, uiHeight);
        context.drawImage(ImageElement.as(imageList.get(5).getElement()), xPos, yPos, ballRadius * 2, ballRadius * 2);

        context.closePath();
    }

    private void createMenu() {
        int buttonHeight = height / 8;
        int margin = (height - buttonHeight * 5) / 6; // Space between every button
        List<String> difficultyTextsList = Arrays.asList("VERY EASY", "EASY", "NORMAL", "HARD", "VERY HARD");

        for(int i = 0; i < 5; i++) {
            int yPos = margin * (i + 1) + i * buttonHeight;
            createDifficultyButton(difficultyTextsList.get(i), i + 1, yPos);
        }
    }

    private void createDifficultyButton(String text, int difficulty, int yPos) {
        Button button = new Button(text, (ClickHandler) event -> setDifficulty(difficulty));

        button.getElement().setClassName("menu");
        button.getElement().getStyle().setTop(yPos, Style.Unit.PX);

        int buttonWidth = width / 4; // 25% screen width
        button.getElement().getStyle().setWidth(buttonWidth, Style.Unit.PX);

        int buttonHeight = height / 8; // 12.5% screen height
        button.getElement().getStyle().setHeight(buttonHeight, Style.Unit.PX);

        int fontSize = buttonHeight / 3; // divided by magic number, looks good on most PC resolutions
        button.getElement().getStyle().setFontSize(fontSize, Style.Unit.PX);

        // Place center of the button in the middle of x-axis
        int xPos = width/2 - buttonWidth/2;
        button.getElement().getStyle().setLeft(xPos, Style.Unit.PX);

        buttonList.add(button);
        RootPanel.get(canvasHolderId).add(button);
    }

    private void setDifficulty(int difficulty) {
        // Hide mneu buttons
        for (Button button : buttonList) {
            button.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        }

        // Play start sound
        Audio start = Audio.createIfSupported();
        start.setSrc("sounds/start.mp3");
        start.play();

        // Set ball's speed depending on the difficulty
        int newSpeed = height / 200;
        ball.setSpeed(newSpeed * difficulty);

        setControls();
        startGame();
    }

    private void setControls() {
        // Set Player keyboard controls
        canvas.addKeyDownHandler(event -> {
            if(event.getNativeKeyCode() == KeyCodes.KEY_RIGHT) {
                player.setAcceleration(1);
            }
            else if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) {
                player.setAcceleration(-1);
            }
        });
        canvas.setFocus(true);

        // Set Player mouse controls
        canvas.addMouseMoveHandler(event -> player.setxPos(event.getX() - player.getWidth() / 2));
    }

    private void startGame() {
        // Run game
        mainTimer = new Timer() {
            @Override
            public void run() {
                if(time <= 0) {
                    gameOver();
                    return;
                }
                update();
                if(lifes <= 0 || tileList.size() == 0) {
                    return ;
                }
                render();
            }
        };
        mainTimer.scheduleRepeating(16); // around 60 updates per second

        // Update time
        timeMeterTimer = new Timer() {
            @Override
            public void run() {
                time -= 1;
            }
        };
        timeMeterTimer.scheduleRepeating(1000);
    }

    private void update() {
        player.move();
        player.setAcceleration(0);
        ball.move();
        ballCollisions();
        checkBallMissed();
    }

    private void checkBallMissed() {
        if(ball.getyPos() + ball.getRadius() >= player.getyPos()) {
            lifes -= 1;
            lifesTextButton.setText("Lifes: " + lifes);
            if(lifes == 0) {
                gameOver();
            }
            else {
                playSound("sounds/fail.mp3");
                // reset ball positions
                ball.setxPos(width / 2 - ball.getRadius());
                ball.setyPos(height / 2 - ball.getRadius());
                ball.setAngle(30);
            }
        }
    }

    private void ballCollisions() {
        boolean collided = false;
        Rectangle rectBall = new Rectangle(ball.getxPos(), ball.getyPos(), ball.getRadius());

        // Tiles collision
        for(int i = 0; i < tileList.size(); i++) {
            Tile tile = tileList.get(i);
            Rectangle rectTile = new Rectangle(tile.getxPos(), tile.getyPos(), tile.getWidth(), tile.getHeight());
            if(isOverlapping(rectBall, rectTile)) {
                playSound("sounds/tile_hit.mp3");
                ball.tileCollision(tile);
                collided = true;

                if(tile.gotHit() == 0) {
                    tileList.remove(tile);
                    if(tileList.size() == 0) {
                        levelCompleted();
                    }
                }
                break;
            }
        }

        // Walls collision
        if(ball.wallCollision()) {
            playSound("sounds/wall_hit.mp3");
            collided = true;
        }

        // Player collision
        Rectangle rectPlayer = new Rectangle(player.getxPos(), player.getyPos(), player.getWidth(), player.getHeight());
        if(isOverlapping(rectPlayer, rectBall)) {
            playSound("sounds/wall_hit.mp3");
            ball.playerCollision(player);
            collided = true;
        }


        // If any collision happened - check again
        if(collided) {
            ballCollisions();
        }
    }

    /**
     * Checks if 2 rectangle overlap with each other in 2d area.
     *
     * @param rect1     First rectangle.
     * @param rect2     Second rectangle.
     * @return          True if rectangles overlap, false otherwise.
     */
    public boolean isOverlapping(Rectangle rect1, Rectangle rect2) {
        if (rect1.topRight.y > rect2.bottomLeft.y || rect1.bottomLeft.y < rect2.topRight.y) {
            return false;
        }
        return rect1.topRight.x >= rect2.bottomLeft.x && rect1.bottomLeft.x <= rect2.topRight.x;
    }

    private void render() {
        context.beginPath();

        // Background
        context.drawImage(ImageElement.as(imageList.get(3).getElement()), 0, 0, width, height);

        // UI
        context.setFillStyle(CssColor.make("black"));
        int uiHeight = height / 10;
        context.fillRect(0, 0, width, uiHeight);
        timeTextButton.setText("Time: " + time);
        lifesTextButton.setText("Lifes: " + lifes);

        // Tiles
        int k = 0;
        for(int i = 0; i < tileList.size(); i++) {
            ImageElement imgEl = ImageElement.as(imageList.get(tileList.get(k).getHitsToDestroy() - 1).getElement());
            context.drawImage(imgEl, tileList.get(k).getxPos(), tileList.get(k).getyPos(), tileList.get(k).getWidth(), tileList.get(k).getHeight());
            k += 1;
        }

        // Player
        context.drawImage(ImageElement.as(imageList.get(4).getElement()), player.getxPos(), player.getyPos(), player.getWidth(), player.getHeight());

        // Ball
        context.drawImage(ImageElement.as(imageList.get(5).getElement()), ball.getxPos(), ball.getyPos(), ball.getRadius() * 2, ball.getRadius() * 2);

        context.closePath();
    }

    private void gameOver() {
        // Cancel timers
        timeMeterTimer.cancel();
        mainTimer.cancel();

        // hide UI
        lifesTextButton.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        timeTextButton.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);

        // Draw Game Over screen and play sound
        context.beginPath();
        context.drawImage(ImageElement.as(imageList.get(6).getElement()), 0, 0, width, height);
        context.closePath();
        playSound("sounds/lose.mp3");
    }

    private void levelCompleted() {
        // Cancel timers
        timeMeterTimer.cancel();
        mainTimer.cancel();

        // hide UI
        lifesTextButton.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        timeTextButton.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);

        // Draw screen with congratulations and play sound
        context.beginPath();
        context.drawImage(ImageElement.as(imageList.get(7).getElement()), 0, 0, width, height);
        context.closePath();
        playSound("sounds/win.mp3");
    }

    /**
     * Plays sound specified by the first parameter.
     *
     * @param path      Path to the mp3 file located in the war folder.
     */
    public void playSound(String path) {
        // Play sound
        Audio sound = Audio.createIfSupported();
        sound.setSrc(path);
        sound.play();
    }
}
