package com.example.caxidy.proyectojuego;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    GameView gameView;
    SensorManager sensorManager;
    Sensor sensorAcel;
    float potencia=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView=new GameView(this);
        setContentView(gameView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        List<Sensor> listaSensores;
        //Obtenemos todos los sensores de tipo acelerometro y seleccionamos el que tenga menos potencia...
        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!listaSensores.isEmpty()) {
            for(Sensor sensor : listaSensores) {
                if(potencia>=sensor.getPower()) {
                    potencia = sensor.getPower();
                    sensorAcel = sensor;
                }
            }
            sensorManager.registerListener(this, sensorAcel,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        else{
            //Se informa de que no hay sensores disponibles
            AlertDialog.Builder alertDialogBu = new AlertDialog.Builder(this);
            alertDialogBu.setTitle(getString(R.string.tituloDiagSensor));
            alertDialogBu.setMessage(getString(R.string.textoDiSensor));
            alertDialogBu.setIcon(R.mipmap.ic_launcher);
            alertDialogBu.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            AlertDialog alertDialog = alertDialogBu.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                //Incrementa el valor de X (pos 0)
                gameView.setVelocidadX((int)event.values[0]*10);
                //Incrementa el valor de Y (pos 1)
                //!!
                //Incrementa el valor de Z (pos 2)
                //!!
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        gameView=new GameView(this);
        setContentView(gameView);

        sensorManager.registerListener(this, sensorAcel, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.cerrarJuego();
        gameView = null;
        System.exit(0);
    }

}
