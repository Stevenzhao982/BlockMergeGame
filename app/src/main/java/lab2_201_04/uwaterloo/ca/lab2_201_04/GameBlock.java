package lab2_201_04.uwaterloo.ca.lab2_201_04;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ImageView;

public class GameBlock extends ImageView {

    private final float IMAGE_SCALE = 1f;
    private int myCoordX;
    private int myCoordY;
    private int targetX;
    private int targetY;
    private GameLoopTask.gameDirection targetDirection;

    private final float GameBlock_Acceleration = 4.0f;
    private int myVelocity;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public GameBlock(Context gbCTX, int myCoordX, int myCoordY) {
        super(gbCTX);
        this.myCoordX = myCoordX;
        this.myCoordY = myCoordY;
        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        this.setX(this.myCoordX);
        this.setY(this.myCoordY);
        targetDirection = GameLoopTask.gameDirection.NO_MOVEMENT;
        myVelocity = 0;
    }

    public void setBlockDirection(GameLoopTask.gameDirection thisDir) {
        targetDirection = thisDir;
        Log.d("Gameblock Direction", thisDir.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void move() {
        switch (targetDirection) {
            case UP:

                targetY = 0;
                if (myCoordY > targetY) {
                    if ((myCoordY - myVelocity) <= targetY) {
                        myCoordY = targetY;
                        myVelocity = 0;
                    } else {
                        myCoordY -= myVelocity;
                        myVelocity += GameBlock_Acceleration;
                    }
                }
                break;

            case DOWN:

                targetY = 310;
                if (myCoordY < targetY) {
                    if ((myCoordY + myVelocity) >= targetY) {
                        myCoordY = targetY;
                        myVelocity = 0;
                    } else {
                        myCoordY += myVelocity;
                        myVelocity += GameBlock_Acceleration;
                    }
                }
                break;


            case LEFT:

                targetX = 0;
                if (myCoordX > targetX) {
                    if ((myCoordX - myVelocity) <= targetX) {
                        myCoordX = targetX;
                        myVelocity = 0;
                    } else {
                        myCoordX -= myVelocity;
                        myVelocity += GameBlock_Acceleration;
                    }
                }
                break;

            case RIGHT:

                targetX = 310;
                if (myCoordX < targetX) {
                    if ((myCoordX + myVelocity) >= targetX) {
                        myCoordX = targetX;
                        myVelocity = 0;
                    } else {
                        myCoordX += myVelocity;
                        myVelocity += GameBlock_Acceleration;
                    }
                }
                break;

            default:
                break;
        }

        this.setX(myCoordX);
        this.setY(myCoordY);

        if (myVelocity == 0)
            targetDirection = GameLoopTask.gameDirection.NO_MOVEMENT;

    }
}
