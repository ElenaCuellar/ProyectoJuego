package com.example.caxidy.proyectojuego;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView=new GameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //!!aqui para guardar las bolas y posiciones en sharedprefs...
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.cerrarJuego();
    }

}
