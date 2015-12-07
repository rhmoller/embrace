package com.giddyplanet.embrace.examples.client;

import com.giddyplanet.embrace.webapis.*;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class Examples implements EntryPoint {

    @Override
    public void onModuleLoad() {
        Document doc = getDocument();

        HTMLCanvasElement canvas = (HTMLCanvasElement) doc.createElement("canvas");
        canvas.setWidth(400);
        canvas.setHeight(300);
        canvas.getStyle().setProperty("imageRendering", "crisp");

        CanvasRenderingContext2D ctx = (CanvasRenderingContext2D) canvas.getContext("2d");
        ctx.setFillStyle("#008");
        ctx.fillRect(0, 0, 400, 300);

        ctx.setImageSmoothingQuality(ImageSmoothingQuality.medium);

        ctx.setFillStyle("#ff0");
        ctx.beginPath();
        ctx.arc(200, 150, 100, 0, 2.0 * Math.PI, false);
        ctx.fill();
        ctx.setLineWidth(4);
        ctx.setStrokeStyle("#000");
        ctx.stroke();

        ctx.beginPath();
        ctx.setLineWidth(4);
        ctx.setFillStyle("#000");
        ctx.arc(200, 150, 80, 0, Math.PI, false);
        ctx.stroke();

        ctx.save();
        ctx.scale(1, 1.5);
        ctx.beginPath();
        ctx.arc(170, 90, 20, 0, 2.0 * Math.PI, false);
        ctx.moveTo(250, 100);
        ctx.arc(230, 90, 20, 0, 2.0 * Math.PI, false);
        ctx.fill();
        ctx.restore();

        ctx.setFillStyle("#fff");
        ctx.fillText("Canvas is an element node: " + (canvas.getNodeType() == Node.ELEMENT_NODE), 10, 20);

        HTMLElement body = doc.getBody();
        body.appendChild(canvas);

        HTMLButtonElement button = (HTMLButtonElement) doc.createElement("button");
        button.setInnerHTML("Click Me");
        button.addEventListener("click", e -> {
            getWindow().alert("Clicked!");
        }, false);

        body.appendChild(button);
    }

    public static native Document getDocument() /*-{
        return $doc;
    }-*/;

    public static native Window getWindow() /*-{
        return $wnd;
    }-*/;

}
