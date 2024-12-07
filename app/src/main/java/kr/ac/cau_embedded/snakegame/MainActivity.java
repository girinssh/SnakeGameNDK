package kr.ac.cau_embedded.snakegame;

import static java.lang.Integer.max;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import kr.ac.cau_embedded.snakegame.databinding.ActivityMainBinding;

import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SnakePanelView mSnakePanelView;
    private SnakeGameManager gm;
    private int left_id;
    private int right_id;
    private int top_id;
    private int bottom_id;

    private TextView timerTextView, scoreTextView, comboTextView;
    private AlertDialog dialog;

    // Used to load the 'snakegame' library on application startup.
    static {
        System.loadLibrary("snakegame");
    }

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

        findViewById(R.id.left_btn).setOnClickListener(this);
        findViewById(R.id.right_btn).setOnClickListener(this);
        findViewById(R.id.top_btn).setOnClickListener(this);
        findViewById(R.id.bottom_btn).setOnClickListener(this);
        findViewById(R.id.start_btn).setOnClickListener(this);

        dialog = new AlertDialog.Builder(MainActivity.this.getApplicationContext()).setMessage("Game " + "Over!")
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
                .create();
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
                GameMainThread thread = new GameMainThread();
                thread.start();
            }
        }
    }
    private void showMessageDialog() {
        runOnUiThread(()->dialog.show());
    }
    private class GameMainThread extends Thread {
        // TODO public native char[] getPushButtonState();
        int timer = 0;
        int combo_time = 0;
        int cnt = 0;
        @Override
        public void run() {
            timer = 0;
            cnt = (int) -gm.getmSpeed();
            int oldSnakeLength = gm.getmSnakeLength();
            boolean isLengthIncrease = false;
            while (!gm.getmIsEndGame()) {
                // TODO: Check Hardware Button Input
                checkHWButton();

                gm.moveSnake(gm.getmSnakeDirection());
                gm.checkCollision();
                gm.refreshGridSquare();
                gm.handleSnakeTail();
                mSnakePanelView.postInvalidate();
                handleSpeed();

                if(oldSnakeLength < gm.getmSnakeLength()){
                    isLengthIncrease = true;
                    oldSnakeLength = gm.getmSnakeLength();
                } else {
                    isLengthIncrease = false;
                }

                // TODO: Update Time and Score
                if(cnt % gm.getmSpeed() == 0){
                    timer++;
                    cnt = 0;
                    // TODO: SEND TIME
                    combo_time = isLengthIncrease ? gm.getComboMaxInterval() : max(combo_time - 1, 0);

                    if(combo_time == 0){
                        gm.getScoreManager().resetCombo();
                    }
                    gm.getScoreManager().increaseScore(isLengthIncrease);

                    //TODO: SEND SCORE and COMBO
                    updateTextView();
                }
            }

            // TODO: Is Game Over
            showMessageDialog();
        }

        private void updateTextView(){
            runOnUiThread(()->{
                        scoreTextView.setText(String.valueOf(gm.getScoreManager().getScore()));
                        timerTextView.setText(String.valueOf(timer));
                        comboTextView.setText(String.valueOf(gm.getScoreManager().getCombo()));
                    }
            );
        }

        private void checkHWButton() {


        }

        private void handleSpeed() {
            try {
                sleep(1000 / gm.getmSpeed());
                cnt++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}