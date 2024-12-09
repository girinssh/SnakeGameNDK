package kr.ac.cau_embedded.snakegame;

public class ScoreManager {
    private int score = 0;
    private int combo = 0;

    private int bestScore = 0;

    public ScoreManager() {
        reset();
    }

    public void reset(){
        score = 0;
        combo = 0;
    }

    public void resetCombo(){
        combo = 0;
    }
    public void increaseScore(boolean isFoodEaten){
        if(isFoodEaten){
            combo++;
            score += combo;
            if(score > bestScore){
                bestScore = score;
            }
        }
    }

    public int getScore(){
        return score;
    }
    public int getCombo(){
        return combo;
    }

    public int getBestScore(){
        return bestScore;
    }
}
