package com.arkanoid.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;

/**
 * Arkanoid game
 * <p>
 * Implements 1 stage.
 * Number of rows and columns of tiles can be changed in code in GameWindow class.
 * Implements 3 different tiles.
 * Can choose 1 from 5 different difficulties which affects game's speed.
 * Window scales with most browsers and PC screen resolutions.
 */
public class Arkanoid implements EntryPoint {
    public void onModuleLoad() {
        String canvasHolderId = "canvas_holder";
        Canvas canvas = Canvas.createIfSupported();
        if (canvas == null) {
            RootPanel.get(canvasHolderId).add(new Label("Your browser does not support the HTML5 Canvas"));
            return;
        }
        new GameWindow(canvas, canvasHolderId);
    }
}