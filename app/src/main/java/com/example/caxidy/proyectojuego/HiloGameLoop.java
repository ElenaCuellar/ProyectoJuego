package com.example.caxidy.proyectojuego;

import android.graphics.Canvas;

public class HiloGameLoop extends Thread {
    static final long FPS = 10;
    private GameView view;
    private boolean repitiendo = false;

    public HiloGameLoop(GameView view) {
        this.view = view;
    }

    public void setRunning(boolean run) {
        repitiendo = run;
    }

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;

        while (repitiendo) {
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    view.onDraw(c);
                }
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
            //Al total que tiene que tardar le restamos el tiempo sobrante (tiempo actual - tiempo cuando empezó)
            sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            try {
                //Si el tiempo que tenemos que dormir es mayor a 0, dormimos el tiempo necesitado. Si no, dormimos una cantidad mínima
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }
}
