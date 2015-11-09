package com.example.tsunoda.surfaceviewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AnimationSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    static final long FPS = 20;
    static final long FRAME_TIME = 1000 / FPS;
    static final int BALL_R = 30;

    SurfaceHolder surfaceHolder;
    Thread thread;
    int cx = BALL_R, cy = BALL_R;
    int screen_width, getScreen_height;

    public AnimationSurfaceView(Context context) {
        super(context);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void run() {
        Canvas canvas = null;
        Paint bgPaint = new Paint();
        Paint ballPaint = new Paint();

        // Background
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.WHITE);
        // Ball
        ballPaint.setStyle(Paint.Style.FILL);
        ballPaint.setColor(Color.BLUE);

        long loopCount = 0;
        long waitTime = 0;
        long startTime = System.currentTimeMillis();

        while (thread != null) {
            try {
                loopCount++;
                canvas = surfaceHolder.lockCanvas();

                canvas.drawRect(0, 0, screen_width, getScreen_height, bgPaint);
                canvas.drawCircle(cx++, cy++, BALL_R, ballPaint);

                surfaceHolder.unlockCanvasAndPost(canvas);

                waitTime = (loopCount * FRAME_TIME) - (System.currentTimeMillis() -  startTime);
                if (waitTime > 0) {
                    Thread.sleep(waitTime);
                }
            }
            catch (Exception e) {
            }
        }

    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder,
            int format,
            int width,
            int height) {
        screen_width = width;
        getScreen_height = height;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }
}
