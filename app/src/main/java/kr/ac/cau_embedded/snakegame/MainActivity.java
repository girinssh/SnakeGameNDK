package kr.ac.cau_embedded.snakegame;

import static java.lang.Integer.max;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import kr.ac.cau_embedded.snakegame.databinding.ActivityMainBinding;

import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SnakePanelView mSnakePanelView;
    private SnakeGameManager gm;
    private int left_id;
    private int right_id;
    private int top_id;
    private int bottom_id;

    private TextView timerTextView, scoreTextView, bestScoreTextView, comboTextView;

    // Used to load the 'snakegame' library on application startup.
    static {
//        System.loadLibrary("snakegame");
        System.loadLibrary("MoveButton");
        System.loadLibrary("Apple");
        System.loadLibrary("ScoreLCD");
        System.loadLibrary("Time7Seg");
        System.loadLibrary("Effector");
    }
    public native char getInputFromHW();
    public native void sendTime2HW(int time);
    public native void sendScore2HW(int score);
    public native void sendBestScore2HW(int best_score);
    public native void sendCombo2HW(int combo);
    public native void resetLCD();
    public native void effectGameStart();
    public native void effectGameOver();
    public native void effectEatFood();
    public native void resetApple();
    public native void stopMotor();

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mSnakePanelView = findViewById(R.id.snake_view);
        gm = mSnakePanelView.getGameManager();

        left_id     = R.id.left_btn;
        right_id    = R.id.right_btn;
        top_id       = R.id.top_btn;
        bottom_id     = R.id.bottom_btn;

        scoreTextView = findViewById(R.id.score_value);
        comboTextView = findViewById(R.id.combo_value);
        timerTextView = findViewById(R.id.time_value);
        bestScoreTextView = findViewById(R.id.best_score_value);

        findViewById(R.id.left_btn).setOnClickListener(this);
        findViewById(R.id.right_btn).setOnClickListener(this);
        findViewById(R.id.top_btn).setOnClickListener(this);
        findViewById(R.id.bottom_btn).setOnClickListener(this);
        findViewById(R.id.start_btn).setOnClickListener(this);

        resetLCD();
        sendCombo2HW(0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == left_id)
            gm.setSnakeDirection(GameType.LEFT);
        if(v.getId() == right_id)
            gm.setSnakeDirection(GameType.RIGHT);
        if(v.getId() == top_id)
            gm.setSnakeDirection(GameType.TOP);
        if(v.getId() == bottom_id)
            gm.setSnakeDirection(GameType.BOTTOM);
        if(v.getId() == R.id.start_btn){
            if(gm.resetGame()) {
                effectGameStart();
                resetApple();
                GameMainThread thread = new GameMainThread();
                thread.start();
            }
        }
    }
    private void showMessageDialog() {
        runOnUiThread(()->new AlertDialog.Builder(peekAvailableContext()).setMessage("Game " + "Over!")
                .setCancelable(false)
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gm.resetGame();
                        GameMainThread thread = new GameMainThread();
                        thread.start();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show());
    }
    private class GameMainThread extends Thread {
        int timer = 0;
        final long interval = (1000/gm.getmSpeed());

        @SuppressLint("NewApi")
        @Override
        public void run() {
            int combo_time = 0;
            int combo_cnt_offset = 0;
            timer = 0;
            int cnt = (int) -gm.getmSpeed();
            int oldSnakeLength = gm.getmSnakeLength();
            boolean isLengthIncrease;
            long old = System.currentTimeMillis();
            TimerTask stopMotor = new TimerTask() {
                @Override
                public void run() {
                    stopMotor();
                }
            };

            while (!gm.getmIsEndGame()) {
                checkHWButton();

                if(System.currentTimeMillis() - old >= interval) {
                    cnt++;

                    old = System.currentTimeMillis();
                    gm.moveSnake(gm.getmSnakeDirection());
                    gm.checkCollision();
                    gm.refreshGridSquare();
                    gm.handleSnakeTail();
                    mSnakePanelView.postInvalidate();
//                handleSpeed();

                    // Snake Got Score
                    if (oldSnakeLength < gm.getmSnakeLength()) {
                        isLengthIncrease = true;
                        oldSnakeLength = gm.getmSnakeLength();

                        combo_time = gm.getComboMaxInterval();
                        combo_cnt_offset = cnt;

                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                effectEatFood();
                            }
                        }.start();

                        gm.getScoreManager().increaseScore(isLengthIncrease);
                        sendScore2HW(gm.getScoreManager().getScore());
                        sendCombo2HW(gm.getScoreManager().getCombo());;
                        updateScoreTextView();
                        updateComboTextView();

                        if(gm.getScoreManager().getCombo() % 8 == 0){

                            Timer stopTimer = new Timer();
                            stopTimer.schedule(stopMotor, 1000);
                        }
                    } else {
                        isLengthIncrease = false;

                        combo_time = cnt == combo_cnt_offset ? max(combo_time - 1, 0) : combo_time;

                        if (combo_time == 0) {
                            gm.getScoreManager().resetCombo();
                            sendCombo2HW(gm.getScoreManager().getCombo());
                            updateComboTextView();
                        }
                    }

                    if (cnt % gm.getmSpeed() == 0) {
                        timer++;
                        cnt = 0;

                        sendTime2HW(timer);
                        updateTimerTextView();
                    }
                }
                try {
                    sleep(interval/4);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            doGameOver();
        }

        private void updateComboTextView(){
            runOnUiThread(()->{
                comboTextView.setText(String.valueOf(gm.getScoreManager().getCombo()));
            });
        }
        private void updateScoreTextView(){
            runOnUiThread(()->{
                scoreTextView.setText(String.valueOf(gm.getScoreManager().getScore()));
            });
        }

        private void updateTimerTextView(){
            runOnUiThread(()->{
                        timerTextView.setText(String.valueOf(timer));
                    }
            );
        }
        private void updateBestScoreTextView(){
            runOnUiThread(()->{
                        bestScoreTextView.setText(String.valueOf(gm.getScoreManager().getBestScore()));
                    }
            );
        }
        private void doGameOver(){
            effectGameOver();

            updateBestScoreTextView();
            sendBestScore2HW(gm.getScoreManager().getBestScore());

            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            showMessageDialog();
        }
        private void checkHWButton() {
            char buttonState = getInputFromHW();

            if(buttonState == 10){
                runOnUiThread(()->{
                    Toast.makeText(
                            peekAvailableContext(),
                            "Don't Press Multiple Buttons!!",
                            Toast.LENGTH_SHORT)
                            .show();
                });
            }
            else if(buttonState == 11) {
                runOnUiThread(() -> {
                    Toast.makeText(
                                    peekAvailableContext(),
                                    "Use buttons 2, 4, 6, and 8 only",
                                    Toast.LENGTH_SHORT)
                            .show();
                });
            }
            switch (buttonState){
                case 2:
                    gm.setSnakeDirection(GameType.TOP);
                    break;
                case 4:
                    gm.setSnakeDirection(GameType.LEFT);
                    break;
                case 6:
                    gm.setSnakeDirection(GameType.RIGHT);
                    break;
                case 8:
                    gm.setSnakeDirection(GameType.BOTTOM);
                    break;
                default:
                    break;
            }

        }
    }


}
