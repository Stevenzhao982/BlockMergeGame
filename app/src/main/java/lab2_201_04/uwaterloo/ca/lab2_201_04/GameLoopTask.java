package lab2_201_04.uwaterloo.ca.lab2_201_04;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.TimerTask;

/**
 * Created by Steven on 2017-03-06.
 */


public class GameLoopTask extends TimerTask {

    private RelativeLayout rl;
    private Activity activity;
    private Context context;
    public enum gameDirection {UP,DOWN,LEFT,RIGHT,NO_MOVEMENT};
    public gameDirection currentGameDirection;
    public GameBlock newBlock;


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    GameLoopTask(RelativeLayout rl, Activity activity, Context context)
    {
        this.rl = rl;
        this.activity = activity;
        this.context = context;
        createBlock();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void createBlock(){
        newBlock = new GameBlock(context,0,0);
        rl.addView(newBlock);
    }

    //the Setter Method the AccelerometerHandler will use to dictate the movement of blocks
    public void setDirection(gameDirection newDirection){
        currentGameDirection = newDirection;
        newBlock.setBlockDirection(newDirection);   //GameBlock is aggregated under he GameLoopTask,
        //GameLoopTask produces and manages the actions of
        //game blocks


    }

    @Override
    public void run() {
        this.activity.runOnUiThread(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void run() {

                newBlock.move();
            }
        });
    }


}
