package com.giddyplanet.embrace.examples.client;

import com.giddyplanet.embrace.webapis.CanvasRenderingContext2D;
import com.giddyplanet.embrace.webapis.Document;
import com.giddyplanet.embrace.webapis.HTMLCanvasElement;
import com.giddyplanet.embrace.webapis.Window;
import com.google.gwt.core.client.EntryPoint;

public class Examples implements EntryPoint {

    @Override
    public void onModuleLoad() {
        Document doc = getDocument();

        HTMLCanvasElement canvas = (HTMLCanvasElement) doc.createElement("canvas");
        canvas.setWidth(400);
        canvas.setHeight(300);

        CanvasRenderingContext2D ctx = (CanvasRenderingContext2D) canvas.getContext("2d", new Object[0]);
        ctx.setFillStyle("#008");
        ctx.fillRect(0, 0, 400, 300);

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

        ctx.scale(1, 1.5);
        ctx.beginPath();
        ctx.arc(170, 90, 20, 0, 2.0 * Math.PI, false);
        ctx.moveTo(250, 100);
        ctx.arc(230, 90, 20, 0, 2.0 * Math.PI, false);
        ctx.fill();

        doc.getBody().appendChild(canvas);
    }

    public static native Document getDocument() /*-{
        return $doc;
    }-*/;

    public static native Window getWindow() /*-{
        return $wnd;
    }-*/;

}
