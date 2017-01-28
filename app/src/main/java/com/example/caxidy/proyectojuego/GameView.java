package com.example.caxidy.proyectojuego;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView {

    protected Bitmap bmp;
    protected SurfaceHolder holder;
    protected HiloGameLoop hiloLoop;
    protected int x = 0;
    protected int velocidadX = 1;

    public GameView(Context context) {
        super(context);

        hiloLoop = new HiloGameLoop(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                hiloLoop.setRunning(false);
                while (retry) {
                    try {
                        hiloLoop.join();
                        retry = false;
                    } catch (InterruptedException e) {}
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                hiloLoop.setRunning(true);
                hiloLoop.start();
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {}
        });
        bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Si x no se sale de la pantalla, se aumenta la posicion. Y si se sale, choca y da la vuelta
        if (x == getWidth() - bmp.getWidth())
            velocidadX = -1;

        if (x == 0)
            velocidadX = 1;

        x += velocidadX;

        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bmp, x, 10, null);
    }
}
