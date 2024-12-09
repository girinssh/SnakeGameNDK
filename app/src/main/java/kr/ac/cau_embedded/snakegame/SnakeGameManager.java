package kr.ac.cau_embedded.snakegame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGameManager {
    private List<List<GridSquare>> mGridSquare = new ArrayList<>();
    private List<GridPosition> mSnakePositions = new ArrayList<>();

    private GridPosition mSnakeHeader;
    private GridPosition mFoodPosition;
    private int mSnakeLength = 3;
    private long mSpeed = 5;
    private int mSnakeDirection = GameType.RIGHT;

    public void setmGridSquare(List<List<GridSquare>> mGridSquare) {
        this.mGridSquare = mGridSquare;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }

    private boolean mIsEndGame = false;
    private int mGridSize = 20;

    private int mStartX, mStartY;
    private int comboMaxInterval = 10;

    private ScoreManager scoreManager;

    SnakeGameManager(){
        List<GridSquare> squares;
        for (int i = 0; i < mGridSize; i++) {
            squares = new ArrayList<>();
            for (int j = 0; j < mGridSize; j++) {
                squares.add(new GridSquare(GameType.GRID));
            }
            mGridSquare.add(squares);
        }
        mSnakeHeader = new GridPosition(mGridSize/2, mGridSize/2);
        mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
        mFoodPosition = new GridPosition(0, 0);
        mIsEndGame = true;
        scoreManager = new ScoreManager();
    }


    public void refreshFood(GridPosition foodPosition) {
        mGridSquare.get(foodPosition.getX()).get(foodPosition.getY()).setType(GameType.FOOD);
    }

    public void setSpeed(long speed) {
        mSpeed = speed;
    }

    public void setGridSize(int gridSize) {
        mGridSize = gridSize;
    }

    public void setSnakeDirection(int snakeDirection) {
        if (mSnakeDirection == GameType.RIGHT && snakeDirection == GameType.LEFT) return;
        if (mSnakeDirection == GameType.LEFT && snakeDirection == GameType.RIGHT) return;
        if (mSnakeDirection == GameType.TOP && snakeDirection == GameType.BOTTOM) return;
        if (mSnakeDirection == GameType.BOTTOM && snakeDirection == GameType.TOP) return;
        mSnakeDirection = snakeDirection;
    }
    public void checkCollision() {
        //检测是否咬到自己
        GridPosition headerPosition = mSnakePositions.get(mSnakePositions.size() - 1);
        for (int i = 0; i < mSnakePositions.size() - 2; i++) {
            GridPosition position = mSnakePositions.get(i);
            if (headerPosition.getX() == position.getX() && headerPosition.getY() == position.getY()) {
                mIsEndGame = true;
                return;
            }
        }

        //判断是否吃到食物
        if (headerPosition.getX() == mFoodPosition.getX()
                && headerPosition.getY() == mFoodPosition.getY()) {
            mSnakeLength++;
            generateFood();
        }
    }

    public boolean resetGame() {
        if (!mIsEndGame) return false;
        for (List<GridSquare> squares : mGridSquare) {
            for (GridSquare square : squares) {
                square.setType(GameType.GRID);
            }
        }
        if (mSnakeHeader != null) {
            mSnakeHeader.setX(mGridSize/2);
            mSnakeHeader.setY(mGridSize/2);
        } else {
            mSnakeHeader = new GridPosition(mGridSize/2, mGridSize/2);//蛇的初始位置
        }
        mSnakePositions.clear();
        mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
        mSnakeLength = 3;//蛇的长度
        mSnakeDirection = GameType.RIGHT;
//        mSpeed = mSpeed;//速度
        if (mFoodPosition != null) {
            mFoodPosition.setX(0);
            mFoodPosition.setY(0);
        } else {
            mFoodPosition = new GridPosition(0, 0);
        }
        refreshFood(mFoodPosition);
        mIsEndGame = false;
        return true;
    }

    //生成food
    private void generateFood() {
        Random random = new Random();
        int foodX = random.nextInt(mGridSize - 1);
        int foodY = random.nextInt(mGridSize - 1);
        for (int i = 0; i < mSnakePositions.size() - 1; i++) {
            if (foodX == mSnakePositions.get(i).getX() && foodY == mSnakePositions.get(i).getY()) {
                //不能生成在蛇身上
                foodX = random.nextInt(mGridSize - 1);
                foodY = random.nextInt(mGridSize - 1);
                //重新循环
                i = 0;
            }
        }
        mFoodPosition.setX(foodX);
        mFoodPosition.setY(foodY);
        refreshFood(mFoodPosition);
    }

    public void moveSnake(int snakeDirection) {
        switch (snakeDirection) {
            case GameType.LEFT:
                if (mSnakeHeader.getX() - 1 < 0) {//边界判断：如果到了最左边 让他穿过屏幕到最右边
                    mSnakeHeader.setX(mGridSize - 1);
                } else {
                    mSnakeHeader.setX(mSnakeHeader.getX() - 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
            case GameType.TOP:
                if (mSnakeHeader.getY() - 1 < 0) {
                    mSnakeHeader.setY(mGridSize - 1);
                } else {
                    mSnakeHeader.setY(mSnakeHeader.getY() - 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
            case GameType.RIGHT:
                if (mSnakeHeader.getX() + 1 >= mGridSize) {
                    mSnakeHeader.setX(0);
                } else {
                    mSnakeHeader.setX(mSnakeHeader.getX() + 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
            case GameType.BOTTOM:
                if (mSnakeHeader.getY() + 1 >= mGridSize) {
                    mSnakeHeader.setY(0);
                } else {
                    mSnakeHeader.setY(mSnakeHeader.getY() + 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
        }
    }

    public void refreshGridSquare() {
        for (GridPosition position : mSnakePositions) {
            mGridSquare.get(position.getX()).get(position.getY()).setType(GameType.SNAKE);
        }
    }

    public void handleSnakeTail() {
        int snakeLength = mSnakeLength;
        for (int i = mSnakePositions.size() - 1; i >= 0; i--) {
            if (snakeLength > 0) {
                snakeLength--;
            } else {
                GridPosition position = mSnakePositions.get(i);
                mGridSquare.get(position.getX()).get(position.getY()).setType(GameType.GRID);
            }
        }
        snakeLength = mSnakeLength;
        for (int i = mSnakePositions.size() - 1; i >= 0; i--) {
            if (snakeLength > 0) {
                snakeLength--;
            } else {
                mSnakePositions.remove(i);
            }
        }
    }
    public List<List<GridSquare>> getmGridSquare() {
        return mGridSquare;
    }

    public List<GridPosition> getmSnakePositions() {
        return mSnakePositions;
    }

    public void setmSnakePositions(List<GridPosition> mSnakePositions) {
        this.mSnakePositions = mSnakePositions;
    }

    public GridPosition getmSnakeHeader() {
        return mSnakeHeader;
    }

    public void setmSnakeHeader(GridPosition mSnakeHeader) {
        this.mSnakeHeader = mSnakeHeader;
    }

    public GridPosition getmFoodPosition() {
        return mFoodPosition;
    }

    public void setmFoodPosition(GridPosition mFoodPosition) {
        this.mFoodPosition = mFoodPosition;
    }

    public int getmSnakeLength() {
        return mSnakeLength;
    }

    public void setmSnakeLength(int mSnakeLength) {
        this.mSnakeLength = mSnakeLength;
    }

    public long getmSpeed() {
        return mSpeed;
    }

    public void setmSpeed(long mSpeed) {
        this.mSpeed = mSpeed;
    }

    public int getmSnakeDirection() {
        return mSnakeDirection;
    }

    public void setmSnakeDirection(int mSnakeDirection) {
        this.mSnakeDirection = mSnakeDirection;
    }

    public boolean getmIsEndGame() {
        return mIsEndGame;
    }

    public void setmIsEndGame(boolean mIsEndGame) {
        this.mIsEndGame = mIsEndGame;
    }

    public int getmGridSize() {
        return mGridSize;
    }

    public void setmGridSize(int mGridSize) {
        this.mGridSize = mGridSize;
    }

    public int getmStartX() {
        return mStartX;
    }

    public void setmStartX(int mStartX) {
        this.mStartX = mStartX;
    }

    public int getmStartY() {
        return mStartY;
    }

    public void setmStartY(int mStartY) {
        this.mStartY = mStartY;
    }

    public int getComboMaxInterval() {
        return comboMaxInterval;
    }

    public void setComboMaxInterval(int comboMaxInterval) {
        this.comboMaxInterval = comboMaxInterval;
    }
}
