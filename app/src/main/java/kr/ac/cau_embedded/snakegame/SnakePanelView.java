package kr.ac.cau_embedded.snakegame;

import static java.lang.Integer.max;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakePanelView extends View {
    private final static String TAG = SnakePanelView.class.getSimpleName();
    public static boolean DEBUG = true;
    private Paint mGridPaint = new Paint();
    private Paint mStrokePaint = new Paint();
    private int mRectSize = dp2px(getContext(), 15);

    private SnakeGameManager gameManager;

    public SnakePanelView(Context context) {
        this(context, null);
    }

    public SnakePanelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnakePanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gameManager = new SnakeGameManager();
    }

    public SnakeGameManager getGameManager(){
        return gameManager;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gameManager.setmStartX(w / 2 - gameManager.getmGridSize() * mRectSize / 2);
        gameManager.setmStartY(dp2px(getContext(), 40));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = gameManager.getmStartY() * 2 + gameManager.getmGridSize() * mRectSize;
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        //格子画笔
        mGridPaint.reset();
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStyle(Paint.Style.FILL);
        mGridPaint.setAntiAlias(true);

        mStrokePaint.reset();
        mStrokePaint.setColor(Color.BLACK);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);

        for (int i = 0; i < gameManager.getmGridSize(); i++) {
            for (int j = 0; j < gameManager.getmGridSize(); j++) {
                int left = gameManager.getmStartX() + i * mRectSize;
                int top = gameManager.getmStartY() + j * mRectSize;
                int right = left + mRectSize;
                int bottom = top + mRectSize;
                canvas.drawRect(left, top, right, bottom, mStrokePaint);
                mGridPaint.setColor(gameManager.getmGridSquare().get(i).get(j).getColor());
                canvas.drawRect(left, top, right, bottom, mGridPaint);
            }
        }
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }
}