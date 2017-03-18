package lab2_201_04.uwaterloo.ca.lab2_201_04;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import ca.uwaterloo.sensortoy.LineGraphView;
import lab2_201_04.uwaterloo.ca.lab2_201_04.MainActivity;


public class AccelerometerSensorEventListener implements SensorEventListener {

    private TextView gesture;
    private GameLoopTask newGameLoopTask;

    //Setup filter constant here
    private final float FILTER_CONSTANT = 22.0f;

    //FSM: setup FSM states and Signatures here
    // enum for custom variable types
    enum myState{WAIT, RISE_RIGHT, FALL_RIGHT, FALL_LEFT, RISE_LEFT, RISE_UP, FALL_UP, RISE_DOWN, FALL_DOWN, DETERMINED};
    myState state = myState.WAIT;

    enum mySig{SIG_RIGHT, SIG_LEFT, SIG_UP, SIG_DOWN, SIG_UNKNOWN};
    mySig signature = mySig.SIG_UNKNOWN;

    //FSM: setup threshold constants here
    final float[] THRESH_RIGHT = {0.2f, 1.0f, -1.0f}; // right
    final float[] THRESH_LEFT = {-0.2f, -1.0f, 1.0f}; // left
    final float[] THRESH_UP = {0.5f, 2.5f, 1.0f}; // up
    final float[] THRESH_DOWN ={-0.5f, -2.5f, 1.0f}; // down

    //FSM: Setup FSM sample counter here
    final int SAMPLEDEFAULT = 30;
    int sampleCounter = SAMPLEDEFAULT;

    //100 history readings of 3 axis
    private float[][] historyReading = new float[100][3];

    //FIFO 100-element rotation method
    private void insertHistoryReading(float[] values){

        for(int i = 1; i < 100; i++){
            historyReading[i - 1][0] = historyReading[i][0]; // current x
            historyReading[i - 1][1] = historyReading[i][1]; // current y
            historyReading[i - 1][2] = historyReading[i][2]; // current z
        }

        //UPDATE THIS SECTION for LPF Implementation
        historyReading[99][0] += (values[0] - historyReading[99][0]) / FILTER_CONSTANT; // filter x
        historyReading[99][1] += (values[1] - historyReading[99][1]) / FILTER_CONSTANT; // filter y
        historyReading[99][2] += (values[2] - historyReading[99][2]) / FILTER_CONSTANT; // filter z

        //After filtering the data, call FSM for signature analysis.
        callFSM();

        //Make sure that by the 30th sample, the FSM result is generated.
        if(sampleCounter <= 0){
            // Now based on the FSM call, respective signatures will lead to respective gesture results
            if(state == myState.DETERMINED){
                if(signature == mySig.SIG_LEFT) {
                    gesture.setText("LEFT");
                    newGameLoopTask.setDirection(GameLoopTask.gameDirection.LEFT);
                }
                else if (signature == mySig.SIG_RIGHT) {
                    gesture.setText("RIGHT");
                    newGameLoopTask.setDirection(GameLoopTask.gameDirection.RIGHT);
                }
                else if(signature == mySig.SIG_UP) {
                    gesture.setText("UP");
                    newGameLoopTask.setDirection(GameLoopTask.gameDirection.UP);
                }
                else if(signature == mySig.SIG_DOWN) {
                    gesture.setText("DOWN");
                    newGameLoopTask.setDirection(GameLoopTask.gameDirection.DOWN);
                }
                else
                    gesture.setText("Undetermined");
            }
            else{
                state = myState.WAIT;
                gesture.setText("Undetermined");
            }
            sampleCounter = SAMPLEDEFAULT;
            state = myState.WAIT;
        }
    }


    //FSM1:  Implement FSM Method Here
    public void callFSM(){

        float deltaX = historyReading[99][0] - historyReading[98][0]; // These changes are updated every time we exit one of the cases
        float deltaY = historyReading[99][1] - historyReading[98][1]; // Only change in Y and X matter for our 4 way motion so forget Z axis

        switch(state){

            ////////////////////////////////////////////////////////////
            case WAIT:
                sampleCounter = SAMPLEDEFAULT;
                signature = mySig.SIG_UNKNOWN;


                if(deltaX > THRESH_RIGHT[0] && Math.abs(deltaY) < 0.5){ // We have a second condition to account for Y-changes as a left/right
                                                                        // motion change should not exceed a certain amount of Y-change
                    state = myState.RISE_RIGHT; // if the change in x is greater than our first right
                                                // thresh value, we want to begin analyzing its behaviour
                                                // with respect to the right
                }
                else if(deltaX < THRESH_LEFT[0] && Math.abs(deltaY) < 0.5){ // Vice versa here
                    state = myState.FALL_LEFT; // for left, the movement begins with a falling negative slope for x
                }
                else if (deltaY > THRESH_UP[0] && Math.abs(deltaX) < 1){
                    state = myState.RISE_UP;
                }
                else if (deltaY < THRESH_DOWN[0] && Math.abs(deltaX) < 1){
                    state = myState.FALL_DOWN;
                }

                break;
            ////////////////////////////////////////////////////////////


            ///////////////////////////////////////////////////////////
            case RISE_RIGHT:     // When determined rising, we will only change things by this case when...
                if(deltaX <= 0){ // ...we begin falling
                    if(historyReading[99][0] >= THRESH_RIGHT[1]){
                        state = myState.FALL_RIGHT; // state is fall_right -> leads to next case (as
                                                    // right movement involves a positive rise then negative fall)
                    }
                    else{
                        state = myState.DETERMINED;
                    }
                }
                break;

            case FALL_RIGHT:
                if(deltaX >= 0){ // as x is currently decreasing, we will only go into this case when
                                 // it starts increasing positively to reset to zero - when the right movement is finished
                    if (historyReading[99][0] <= THRESH_RIGHT[2]) { // now that it has risen, it is falling, so its current x must be
                                                                    // less/equal to our second threshold value
                        signature = mySig.SIG_RIGHT; //  Then we know it is a movement to the right
                    }
                    state = myState.DETERMINED;
                }
                break;
            ///////////////////////////////////////////////////////////


            ///////////////////////////////////////////////////////////
            case FALL_LEFT:
                if(deltaX >= 0){
                    if(historyReading[99][0] <= THRESH_LEFT[1]){
                        state = myState.RISE_LEFT;
                    }
                    else{
                        state = myState.DETERMINED;
                    }
                }
                break;

            case RISE_LEFT:
                if(deltaX <= 0){
                    if (historyReading[99][0] >= THRESH_LEFT[2]) {
                        signature = mySig.SIG_LEFT;
                    }
                    state = myState.DETERMINED;
                }
                break;
            ///////////////////////////////////////////////////////////



            /////////////////////////////////////////////////////////// UP
            case RISE_UP:     // When determined rising, we will only change things by this case when...
                if(deltaY <= 0){ // ...we begin falling
                    if(historyReading[99][1] >= THRESH_UP[1]){
                        state = myState.FALL_UP; // state is fall_up -> leads to next case (as
                        // up movement involves a positive rise then negative fall)
                    }
                    else{
                        state = myState.DETERMINED;
                    }
                }
                break;

            case FALL_UP: // finished falling - upwards motion
                if(deltaY >= 0){ // as Y is currently decreasing, we will only go into this case when
                                 // it starts increasing positively to reset to zero - when the up movement is finished
                    if (historyReading[99][1] <= THRESH_UP[2]) {
                        signature = mySig.SIG_UP; //  Then we know it is a movement to the right
                    }
                    state = myState.DETERMINED;
                }
                break;
            ///////////////////////////////////////////////////////////



            ///////////////////////////////////////////////////////////
            case FALL_DOWN:
                if(deltaY >= 0){
                    if(historyReading[99][1] <= THRESH_DOWN[1]){
                        state = myState.RISE_DOWN;
                    }
                    else{
                        state = myState.DETERMINED;
                    }
                }
                break;

            case RISE_DOWN:
                if(deltaY <= 0){
                    if (historyReading[99][1] >= THRESH_DOWN[2]) {
                        signature = mySig.SIG_DOWN;
                    }
                    state = myState.DETERMINED;
                }
                break;
            ///////////////////////////////////////////////////////////



            case DETERMINED:
                Log.d("FSM: ", "State: DETERMINED " + signature.toString() + " CURRENT VALUE IS " + historyReading[99][0] + " DELTA X IS " + deltaX + " DELTA Y IS " + deltaY);
                break;

            default:
                state = myState.WAIT;
                break;

        }
        sampleCounter--;
    }


    //Getter method for the history readings
    public float[][] getHistoryReading(){
        return historyReading;
    }

    //constructor
    public AccelerometerSensorEventListener(TextView gesture, GameLoopTask newGameLoopTask) {
        this.newGameLoopTask= newGameLoopTask;
        this.gesture = gesture;
    }

    //required by the SensorEventListener interface
    public void onAccuracyChanged (Sensor sensor, int accuracy) {}

    // When the sensor detects changes, we want these changes to be recorded down on our graph and the movements to be analyzed by insertHistoryReading
    public void onSensorChanged (SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            //insert the new values into the FIFO buffer
            insertHistoryReading(event.values);

            //Update information on the textview
            //Will use this TV for gesture display
            //instanceOutput.setText("The Accelerometer Reading is: \n"
            //        + String.format("(%.2f, %.2f, %.2f)", se.values[0], se.values[1], se.values[2]) + "\n");



        }
    }
}
