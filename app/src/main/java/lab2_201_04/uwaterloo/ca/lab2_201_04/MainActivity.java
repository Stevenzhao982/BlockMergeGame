package lab2_201_04.uwaterloo.ca.lab2_201_04;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;

import ca.uwaterloo.sensortoy.LineGraphView;

public class MainActivity extends AppCompatActivity {

    // Variable Declarations
    // Note we want some static since we want to work with the same one(s) across all classes
    public SensorManager SM;
    public Sensor AccelerometerSensor;
    public static TextView gesture;
    public static AccelerometerSensorEventListener accListener;
    public RelativeLayout rl;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        rl = (RelativeLayout)findViewById(R.id.layout);
        rl.getLayoutParams().width = 1300;
        rl.getLayoutParams().height = 1300;
        rl.setBackgroundResource(R.drawable.gameboard);

        Timer gameLoop = new Timer();
        GameLoopTask newGameLoopTask = new GameLoopTask(rl,this, getApplicationContext());
        gameLoop.schedule(newGameLoopTask, 50, 50);

        // Create TextViews to Display our values
        gesture = (TextView) findViewById(R.id.textGesture);
        // Create Sensor Manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Create our sensor for gravity compensated accelerometer
        AccelerometerSensor = SM.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        // We connect our previously declared sensor event listeners to our sensor classes and register them to the sensor manager
        accListener = new AccelerometerSensorEventListener(gesture, newGameLoopTask);
        SM.registerListener(accListener ,AccelerometerSensor,SensorManager.SENSOR_DELAY_GAME); // Game for increased graph display speed






    }
}

