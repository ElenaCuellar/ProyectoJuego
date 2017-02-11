package com.example.caxidy.proyectojuego;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView {
    public HiloGameLoop hiloLoop;
    public List<TempSprite> temps = new ArrayList<TempSprite>();
    public List<Bola> bolas = new ArrayList<Bola>();
    private long lastClick;
    private Bitmap bmpExpl;
    private SoundPool sp;
    private int miSonido = 0;
    public MainActivity contexto;
    public boolean finJuego = false;

    public GameView(Context context) {
        super(context);

        contexto = (MainActivity) context;
        //Configuracion del sonido al tocar una bola
        sp = new SoundPool(4, AudioManager.STREAM_MUSIC,0);
        miSonido = sp.load(context,R.raw.pom,1);

        hiloLoop = new HiloGameLoop(this);
        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                if(!finJuego) {
                    hiloLoop.setRunning(false);
                    while (retry) {
                        try {
                            hiloLoop.join();
                            retry = false;
                        } catch (InterruptedException e) {
                        }
                    }
                }
                else
                    finJuego=false;
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                crearSprites();
                hiloLoop.setRunning(true);
                hiloLoop.start();
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {}
        });
        bmpExpl = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
    }

    private void crearSprites() {
        int roja = R.drawable.rojas;
        int azul = R.drawable.azules;
        int verde = R.drawable.verdes;
        int totalBolas = (int)(Math.random()*15+5);
        int random;
        for(int b=1;b<=totalBolas;b++) {
            random = (int)(Math.random()*3+1);
            if(random==3)
                bolas.add(crearSprite(roja));
            else if (random==2)
                bolas.add(crearSprite(azul));
            else
                bolas.add(crearSprite(verde));
        }
    }

    public Bola crearSprite(int res) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), res);
        return new Bola(this,bmp,res);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        for (int i = temps.size() - 1; i >= 0; i--) {
            temps.get(i).onDraw(canvas);
        }
        for (Bola bola : bolas) {
            bola.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (System.currentTimeMillis() - lastClick > 300) {
            lastClick = System.currentTimeMillis();
            float x = event.getX();
            float y = event.getY();
            synchronized (getHolder()) {
                for (int i = bolas.size() - 1; i >= 0; i--) {
                    Bola bola = bolas.get(i);
                    if (bola.isCollition(x,y)) {
                        bolas.remove(bola);
                        temps.add(new TempSprite(temps, this, x, y, bmpExpl));
                        sp.play(miSonido,1,1,1,0,1.0f);

                        //sale un dialog de que has terminado el juego si ya no hay mas bolas
                        if(bolas.size()<=0){
                            AlertDialog.Builder alertDialogBu = new AlertDialog.Builder(contexto);
                            alertDialogBu.setTitle(contexto.getString(R.string.titulodiag));
                            alertDialogBu.setMessage(contexto.getString(R.string.textodiag));
                            alertDialogBu.setIcon(R.mipmap.ic_launcher);
                            alertDialogBu.setPositiveButton(contexto.getString(R.string.nuevoj), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Se vuelve a empezar el juego
                                    crearSprites();
                                }
                            });
                            alertDialogBu.setNegativeButton(contexto.getString(R.string.salir), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finJuego=true;
                                    hiloLoop.setRunning(false);
                                    contexto.finish();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBu.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    public void setVelocidadX(int acelX){
        for (Bola bola : bolas) {
            bola.xSpeed=acelX;
        }
    }

    public void setVelocidadY(int acelY){
        for (Bola bola : bolas) {
            bola.ySpeed=acelY;
        }
    }

    public void cerrarJuego(){
        sp.release();
        if(hiloLoop!=null)
            hiloLoop=null;
    }

}
