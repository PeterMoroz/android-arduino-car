package io.qbbr.arduinocar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RadarView extends View {

    private Paint pointPaint;
    private Paint fadePaint;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;

    public RadarView(Context context) {
        super(context);
        init();
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        pointPaint = new Paint();
        pointPaint.setColor(Color.GREEN); // Vibrant Green plot points
        pointPaint.setAntiAlias(true);

        fadePaint = new Paint();
        // Alpha 5 out of 255 creates the light fading trailing effect (matches fill(0, 5))
        fadePaint.setColor(Color.argb(5, 0, 0, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Create an off-screen bitmap to persist the drawing
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawColor(Color.BLACK); // Initial background
    }

    public void plotDistances(int left, int centre, int right) {
        plotPoint(135, left);
        plotPoint(90, centre);
        plotPoint(45, right);
    }
    private void plotPoint(float angleDegrees, float distance) {
        // Limit rendering parameters to realistic sensor thresholds
        if (distance >= 2000) return;

        float angleRadians = (float) Math.toRadians(angleDegrees);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // Translate polar coordinates into X/Y canvas coordinates (scaled by 2)
        float x = (float) (centerX + Math.cos(angleRadians) * distance * 2);
        float y = (float) (centerY - Math.sin(angleRadians) * distance * 2);

        if (bitmapCanvas != null) {
            // 1. Apply fading trail effect to the persistent bitmap
            bitmapCanvas.drawRect(0, 0, getWidth(), getHeight(), fadePaint);

            // 2. Draw the new point (radius 4f = diameter 8, matching ellipse(8, 8))
            bitmapCanvas.drawCircle(x, y, 4f, pointPaint);
        }

        // 3. Request the view to redraw itself on the screen
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }
}