package wdswihart.groovyminesweeper.repositories

import javafx.util.Pair

interface ScoreRepository {
    void addScoresObserver(Observer o)
    void storeData()

    void addScore(Pair<String, Integer> highScore)
    void removeScore(Pair<String, Integer> highScore)
    List<Pair<String, Integer>> getScores()
    void removeAllScores()
    boolean isHighScore(Pair<String, Integer> score)
}