package wdswihart.groovyminesweeper.repositories

import com.google.inject.Inject
import com.google.inject.Singleton
import javafx.util.Pair
import wdswihart.groovyminesweeper.utils.ObservableList

@Singleton
class ScoreRepositoryImpl implements ScoreRepository {
    private ObservableList<Pair<String, Integer>> mScores = new ObservableList<>([])

    @Inject
    ScoreRepositoryImpl() {
        loadData()
    }

    private void loadData() {
        try {
            new File('data/high_scores.csv').eachLine('UTF-8') {
                def splitLine = it.split(',')
                mScores.data.add new Pair<>(splitLine[0], splitLine[1] as int)
            }
        } catch (FileNotFoundException ignored) {
            System.err.println 'Failed to load high scores file.'
        }
    }

    @Override
    void storeData() {
        try {
            new File('data').mkdir()
            new File('data/high_scores.csv').createNewFile()
            new File('data/high_scores.csv').withWriter('UTF-8') { writer ->
                mScores.data.each {
                    writer.writeLine("${it.key},${it.value}")
                }
            }
        } catch (Exception ignored) {
            System.err.println 'Failed to write to high scores file.'
        }
    }

    @Override
    void addScoresObserver(Observer o) {
        mScores.addObserver o
    }

    @Override
    void addScore(Pair<String, Integer> score) {
        mScores.add score
    }

    @Override
    void removeScore(Pair<String, Integer> score) {
        mScores.remove score
    }

    @Override
    List<Pair<String, Integer>> getScores() {
        new ArrayList<Pair<String, Integer>>(mScores.data)
    }

    @Override
    void removeAllScores() {
        mScores.data = []
    }

    @Override
    boolean isHighScore(Pair<String, Integer> score) {
        if (mScores.data.isEmpty()) {
            true
        } else {
            !mScores.data.any { it.key == score.key && it.value > score.value }
        }
    }
}
