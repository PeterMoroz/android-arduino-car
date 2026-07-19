package io.qbbr.arduinocar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class AreaMapView extends View {
    private List<PointF> robotPath = new ArrayList<>();
    private List<PointF> obstacles = new ArrayList<>();

    private Paint robotPaint;
    private Paint obstaclePaint;

    // Scale: How many pixels represent 1 unit (cm) from Arduino
    private float scale = 5f;
    private static final int MAX_POINTS = 5000; // Prevent memory leaks

    public AreaMapView(Context context) {
        super(context);
        init();
    }

    public AreaMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        robotPaint = new Paint();
        robotPaint.setColor(Color.BLUE);
        robotPaint.setStrokeWidth(4f);
        robotPaint.setStyle(Paint.Style.FILL);

        obstaclePaint = new Paint();
        obstaclePaint.setColor(Color.RED);
        obstaclePaint.setStyle(Paint.Style.FILL);
    }

    public void addRobotPoint(float x, float y) {
        robotPath.add(new PointF(x, y));
        if (robotPath.size() > MAX_POINTS) robotPath.remove(0);
    }

    public void addObstaclePoint(float x, float y) {
        obstacles.add(new PointF(x, y));
        if (obstacles.size() > MAX_POINTS) obstacles.remove(0);
    }

    public void update() {
        invalidate(); // Triggers onDraw
    }

    public void clearMap() {
        robotPath.clear();
        obstacles.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE); // Background

        // Move origin (0,0) to the center of the screen
        canvas.translate(getWidth() / 2f, getHeight() / 2f);

        // Invert Y-axis so positive Y is UP (matching matplotlib behavior)
        canvas.scale(scale, -scale);

        // Draw Obstacles (Red dots)
        float dotRadius = 2f / scale; // Radius in world units
        for (PointF p : obstacles) {
            canvas.drawCircle(p.x, p.y, dotRadius, obstaclePaint);
        }

        // Draw Robot Path (Blue line)
        if (robotPath.size() > 1) {
            for (int i = 1; i < robotPath.size(); i++) {
                PointF p1 = robotPath.get(i - 1);
                PointF p2 = robotPath.get(i);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, robotPaint);
            }
        }

        // Draw current robot position as a larger dot
        if (!robotPath.isEmpty()) {
            PointF current = robotPath.get(robotPath.size() - 1);
            canvas.drawCircle(current.x, current.y, 4f / scale, robotPaint);
        }
    }
}