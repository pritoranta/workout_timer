package pr.workout_timer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {

    TextView mainTimer;
    TextView recoveryTimer;
    Button b;

    int mainTime = 30; //initial main timer value
    int recoveryTime = 15; //initial recovery timer value
    Handler timerHandler = new Handler();
    int clock = mainTime; //for counting time
    boolean main = true; //is the main timer active (vs recovery timer)
    boolean active = false; //is the timer ticking
    float previousPointX; //for tracking the drag motion

    // distance to drag on screen for timer to be modified
    final float dragDistance = 20;

    /* Method to be called initially.
     * Looks up UI elements from the layout file and sets touch listeners to them. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //sets the view on the main layout
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //prevents the screen from turning off

        mainTimer = (TextView) findViewById(R.id.textView);
        mainTimer.setText(String.valueOf(mainTime));
        mainTimer.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent m){
                return modTimer(0, m);
            }
        });
        recoveryTimer = (TextView) findViewById(R.id.textView2);
        recoveryTimer.setText(String.valueOf(recoveryTime));
        recoveryTimer.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent m){
                return modTimer(1, m);
            }
        });

        b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!active)
                    start();
                else
                    stop();
            }
        });
    }

    /* Called every second (1000ms) with timerHandler.
     * Depletes time from the currently active timer. */
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run(){
            clock -= 1;
            if(main){
                mainTimer.setText(String.valueOf(clock));
            }
            else{
                recoveryTimer.setText(String.valueOf(clock));
            }
            //if the active timer reaches zero, switches to the other one:
            if(clock<0)
                switchActiveTimer();
            timerHandler.postDelayed(this, 1000); //callback
        }
    };

    /* Starts the main timer and updates app visuals */
    private void start() {
        clock=mainTime;
        active = true;
        b.setText(R.string.timer_running);
        timerHandler.postDelayed(timerRunnable, 1000); //timer called in 1000ms = 1s
    }

    /* Stops the count and updates visuals. Sets the active timer to be the main one. */
    private void stop(){
        timerHandler.removeCallbacks(timerRunnable);
        main=true;
        clock=mainTime;
        active = false;
        updateTimerVisuals();
        b.setText(R.string.timer_stopped);
    }

    /* When the timers are paused,
     * modifies the timer values
     * based on the user touching and dragging on the x-axis */
    private boolean modTimer(int timer, MotionEvent m){
        if(active) //if the timer is running, do nothing
            return true;
        if(m.getAction() == MotionEvent.ACTION_DOWN) { //on the initial touch, update the starting point of the touch
            previousPointX = m.getX();
            return true;
        }
        if(Math.abs(m.getX()-previousPointX)<dragDistance){ //if the touch event is too close to the initial touch, return
            return true;
        }
        int dir; //direction from the previous touch: should we add or subtract seconds from the timer
        if(m.getX()>previousPointX)
            dir = 1;
        else
            dir = -1;
        if(timer == 0){ //which timer was called upon
            mainTime += dir;
            if(mainTime<1)
                mainTime=1;
        }
        else{
            recoveryTime += dir;
            if(recoveryTime<1)
                recoveryTime = 1;
        }
        updateTimerVisuals();
        previousPointX = m.getX();
        return true;
    }

    /* Changes the active timer. */
    private void switchActiveTimer(){
        if(main)
            clock = recoveryTime;
        else
            clock = mainTime;
        main = !main;
        updateTimerVisuals();
    }

    /* Updates the visuals of the timers' UI elements based on the corresponding number values */
    private void updateTimerVisuals(){
        mainTimer.setText(String.valueOf(mainTime));
        recoveryTimer.setText(String.valueOf(recoveryTime));
    }

}