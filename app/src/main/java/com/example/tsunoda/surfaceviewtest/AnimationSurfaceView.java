package com.example.tsunoda.surfaceviewtest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AnimationSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    static final long FPS = 60;
    static final long FRAME_TIME = 1000 / FPS;

    SurfaceHolder surfaceHolder;
    Thread thread;

    private static final int BGWIDTH = 1920;         // 背景画像のリサイズ時に画面サイズがわからないので、やむを得ず定義
    private static final int BGHEIGHT = 1200;        // 背景画像のリサイズ時に画面サイズがわからないので、やむを得ず定義
    private Bitmap backgroundImageRaw;
    private Bitmap backgroundImage;
    private int bgPosX, bgPosY;
    private int bgVel;

    private Bitmap[] droid;
    private int droidPose;
    private int droidPosX, droidPosY;
    private double droidPosOrg, droidPosPhase, droidPosAmp, droidPosPitch;
    private double droidPosEdgeSize;
    private int droidPoseCount;

    public AnimationSurfaceView(Context context) {
        super(context);

        // surace holder取得とコールバック設定
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // リソース取得
        Resources res = this.getContext().getResources();

        // 背景画像の読み込み
        backgroundImageRaw = BitmapFactory.decodeResource(res, R.drawable.bg);
        backgroundImage = Bitmap.createScaledBitmap(backgroundImageRaw, BGWIDTH, BGHEIGHT, false);

        // 位置、速さ(背景)
        bgPosX = 0;
        bgPosY = 0;
        bgVel = 3;

        // droid画像の読み込み
        droid = new Bitmap[4];
        droid[0] = BitmapFactory.decodeResource(res, R.drawable.droid01);
        droid[1] = BitmapFactory.decodeResource(res, R.drawable.droid02);
        droid[2] = BitmapFactory.decodeResource(res, R.drawable.droid03);
        droid[3] = BitmapFactory.decodeResource(res, R.drawable.droid04);
        droidPose = 0;
        droidPoseCount = 0;

        // 位置、上下動
        droidPosOrg = 475;
        droidPosPhase = 0;
        droidPosAmp = 30;
        droidPosPitch = 0.01;
        droidPosEdgeSize = Math.PI / 10;

        droidPosX = BGWIDTH / 2;
        droidPosY = (int)droidPosOrg;
    }

    @Override
    public void run() {
        Canvas canvas = null;
        Paint ballPaint = new Paint();

        // 背景
        Paint backgroudPaint = new Paint();
        // 文字
        Paint textPaint = new Paint();
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLUE);
        // ドロイド君
        Paint droidPaint = new Paint();

        long frameCount = 0;
        long waitTime = 0;
        long startTime = System.currentTimeMillis();

        while (thread != null) {
            try {
                frameCount++;
                update();

                // surfaceロック
                canvas = surfaceHolder.lockCanvas();

                // 背景
                canvas.drawBitmap(backgroundImage, bgPosX, bgPosY, backgroudPaint);
                canvas.drawBitmap(backgroundImage, bgPosX - BGWIDTH, bgPosY, backgroudPaint);

                // 文字
                canvas.drawText("どろいど君 一人旅(SurfaceView版)", this.getWidth() / 2, 150, textPaint);

                // ドロイド君
                canvas.drawBitmap(droid[droidPose], droidPosX, droidPosY, droidPaint);

                // surfaceアンロック
                surfaceHolder.unlockCanvasAndPost(canvas);

                waitTime = (frameCount * FRAME_TIME) - (System.currentTimeMillis() -  startTime);
                if (waitTime > 0) {
                    Thread.sleep(waitTime);
                }
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * 更新処理
     */
    private void update() {
        // 背景処理
        bgPosX += bgVel;
        bgPosX %= BGWIDTH;

        // droid処理
        droidPosPhase += droidPosPitch;
        droidPosPhase %= (Math.PI * 2);
        droidPosY = (int)(droidPosOrg + Math.sin(droidPosPhase) * droidPosAmp);
        if (droidPosPhase > Math.PI / 2 - droidPosEdgeSize && droidPosPhase < Math.PI / 2 + droidPosEdgeSize || droidPosPhase > Math.PI * 3 / 2 - droidPosEdgeSize && droidPosPhase < Math.PI * 3 / 2 + droidPosEdgeSize) {
            droidPose = 2 + (droidPoseCount / 5) % 2;
            droidPoseCount++;
        }
        else if(droidPosPhase < Math.PI) {
            droidPose = 0;
        }
        else {
            droidPose = 1;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
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
