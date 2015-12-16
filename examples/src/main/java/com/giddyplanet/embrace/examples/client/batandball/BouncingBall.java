package com.giddyplanet.embrace.examples.client.batandball;

import com.giddyplanet.embrace.examples.client.Example;
import com.giddyplanet.embrace.webapis.*;
import com.google.gwt.core.client.GWT;

import static com.giddyplanet.embrace.examples.client.Examples.getDocument;
import static com.giddyplanet.embrace.examples.client.Examples.getWindow;

public class BouncingBall implements Example {
    private Document doc;
    private HTMLCanvasElement canvas;

    private float ballX = 100;
    private float ballY = 150;
    private float ballDx = 3;
    private float ballDy = 2;

    private Window win;
    private CanvasRenderingContext2D ctx;

    @Override
    public String getId() {
        return "batandball";
    }

    @Override
    public String getTitle() {
        return "animation with requestAnimationFrame()";
    }

    @Override
    public HTMLElement setup() {
        doc = getDocument();
        win = getWindow();
        canvas = (HTMLCanvasElement) doc.createElement("canvas");
        canvas.width = 500;
        canvas.height = 400;
        ctx = (CanvasRenderingContext2D) canvas.getContext("2d");
        return canvas;
    }

    @Override
    public void start() {
        requestframe();
    }

    private void requestframe() {
        if (canvas.parentNode != null) {
            win.requestAnimationFrame(timestamp -> {
                doc.title = "" + timestamp;
                // todo: how do I get the numeric value of timestamp
                repaint();
            });
        }
    }

    private void repaint() {
        requestframe();

        ballX += ballDx;
        ballY += ballDy;

        if (ballX < 25) {
            ballX = 25;
            ballDx = -ballDx;
        }

        if (ballY < 25) {
            ballY = 25;
            ballDy = -ballDy;
        }

        if (ballX > 475) {
            ballX = 475;
            ballDx = -ballDx;
        }

        if (ballY > 375) {
            ballY = 375;
            ballDy = -ballDy;
        }

        ctx.fillStyle = "#00f";
        ctx.fillRect(0, 0, 500, 400);

        ctx.fillStyle = "#fff";
        ctx.beginPath();
        ctx.arc(ballX, ballY, 25, 0, 2.0 * Math.PI, false);
        ctx.fill();
    }
}
