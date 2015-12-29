package com.giddyplanet.embrace.examples.client.canvas;

import com.giddyplanet.embrace.examples.client.Example;
import com.giddyplanet.embrace.webapis.*;

import static com.giddyplanet.embrace.examples.client.Examples.getDocument;

public class CanvasTypedArrayExample implements Example {

    private CanvasRenderingContext2D ctx;

    @Override
    public String getId() {
        return "canvasarray";
    }

    @Override
    public String getTitle() {
        return "Canvas and typed arrays (slow starter)";
    }

    @Override
    public HTMLElement setup() {
        Document document = getDocument();

        HTMLCanvasElement canvas = (HTMLCanvasElement) document.createElement("canvas");
        canvas.width = 400;
        canvas.height = 300;

        ctx = (CanvasRenderingContext2D) canvas.getContext("2d");
        ctx.fillStyle = "#000";
        ctx.fillRect(0, 0, 400, 300);

        return canvas;
    }

    @Override
    public void start() {
        ImageData imageData = ctx.getImageData(0, 0, 400, 300);
        Uint8ClampedArray data = imageData.data;
        // JsInterop cannot access indexed accessors such as data[0]
        // go through DataView instead and use setInt8()
        DataView view = new DataView(data.buffer);

        int idx = 0;
        for (int y = 0; y < 300; y++) {
            for (int x = 0; x < 400; x++) {
                byte r = (byte) (128 + 127 * Math.cos(0.07 * x + Math.sin(0.05 * y)));
                byte g = (byte) (128 + 127 * Math.sin(0.06 * y + Math.sin(0.09 * y)));
                byte b = (byte) (128 + 127 * Math.cos(0.04 * x + Math.sin(0.03 * x)));
                byte a = (byte) 255;
                view.setInt8(idx++, r);
                view.setInt8(idx++, g);
                view.setInt8(idx++, b);
                view.setInt8(idx++, a);
            }
        }

        ctx.putImageData(imageData, 0, 0);
    }
}
