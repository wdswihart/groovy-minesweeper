package wdswihart.groovyminesweeper.models

import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.transform.CompileStatic
import javafx.application.Platform
import javafx.util.Pair
import wdswihart.groovyminesweeper.factories.ModelFactory
import wdswihart.groovyminesweeper.factories.ObservableFactory
import wdswihart.groovyminesweeper.repositories.PropertiesRepository
import wdswihart.groovyminesweeper.repositories.SaveRepository
import wdswihart.groovyminesweeper.repositories.ScoreRepository
import wdswihart.groovyminesweeper.utils.ObservableValue


@Singleton
@CompileStatic
class GameEngineImpl implements GameEngine {
    private ObservableValue<GameState> mGameState
    private ObservableValue<Boolean> mIsGameOver
    private ObservableValue<Boolean> mIsGameWon

    private boolean mIsNewGame = true
    private Timer mTimer

    private final PropertiesRepository mPropertiesRepository
    private final ScoreRepository mScoreRepository
    private final SaveRepository mSaveRepository
    private final ObservableFactory mObservableFactory
    private final ModelFactory mModelFactory

    @Inject
    GameEngineImpl(PropertiesRepository propertiesRepository, ScoreRepository scoreRepository,
                   SaveRepository saveRepository, ObservableFactory observableFactory,
                   ModelFactory modelFactory) {
        mPropertiesRepository = propertiesRepository
        mScoreRepository = scoreRepository
        mSaveRepository = saveRepository
        mObservableFactory = observableFactory
        mModelFactory = modelFactory

        mGameState = mObservableFactory.createValue(null)
        mIsGameOver = mObservableFactory.createValue(false)
        mIsGameWon = mObservableFactory.createValue(false)

        startNewGame('', mPropertiesRepository.defaultMineCount, mPropertiesRepository.defaultWidth,
                mPropertiesRepository.defaultHeight)
    }

    @Override
    def addGameOverObserver(Observer o) {
        mIsGameOver.addObserver o
    }

    @Override
    boolean getIsGameOver() {
        mIsGameOver.value
    }

    @Override
    int getScore() {
        mGameState.value.score
    }

    @Override
    def addWinObserver(Observer o) {
        mIsGameWon.addObserver o
    }

    @Override
    boolean getIsGameWon() {
        mIsGameWon.value
    }

    @Override
    int getTime() {
        return mGameState.value.time
    }

    @Override
    def cancelTimer() {
        try {
            mTimer.cancel()
        } catch (Exception ignored) {
            System.out.println 'Timer already canceled.'
        }
    }

    @Override
    def excavate(int x, int y) {
        if (!mGameState.value.field.isMarked(x, y) && !mGameState.value.field.isRevealed(x, y)) {
            setIsRevealed(x, y, true)
            if (mGameState.value.field.hasMine(x, y)) {
                gameOver()
            } else {
                revealSafeNeighbors(x, y)
                increaseScore()
                checkWin()
            }
        }

        if (mIsNewGame) {
            startTimer()
            mIsNewGame = false
        }

    }

    @Override
    def excavateNeighbors(int x, int y) {
        for (int i in -1..1) {
            for (int j in -1..1) {
                if ((x + i >= 0 && x + i < mGameState.value.field.width) && (y + j >= 0 && y + j < mGameState.value.field.width)) {
                    if (!mGameState.value.field.isMarked(x + i, y + j) && !isGameOver) {
                        excavate(x + i, y + j)
                    }
                }
            }
        }
    }

    @Override
    def mark(int x, int y) {
        setIsMarked(x, y, true)
        checkWin()
    }

    @Override
    def unmark(int x, int y) {
        setIsMarked(x, y, false)
    }

    def restart(String player, int mineCount, int width, int height) {
        mIsGameWon.value = false
        mIsGameOver.value = false
        mIsNewGame = true
        mGameState.value = mModelFactory.create(player, mineCount, width, height)
        mGameState.value.score = 0
        mGameState.value.time = 0
        cancelTimer()
    }

    @Override
    def restart() {
        restart(mGameState.value.player, mGameState.value.field.mineCount, mGameState.value.field.width,
                mGameState.value.field.height)
    }

    @Override
    def startNewGame(String player, int mineCount, int width, int height) {
        if (mSaveRepository.hasSave(player, mineCount, width, height)) {
            mIsNewGame = false
            mGameState.value = mSaveRepository.getSave(player, mineCount, width, height)
            startTimer()
        } else {
            restart(player, mineCount, width, height)
        }
    }

    @Override
    def saveGame() {
        if (!mIsNewGame && !isGameOver && !isGameWon) {
            println 'saving game'
            mSaveRepository.save mGameState.value
        } else {
            mSaveRepository.removeSave mSaveRepository.saves.find { it.player == mGameState.value.player &&
                    it.field.mineCount == mGameState.value.field.mineCount &&
                    it.field.width == mGameState.value.field.width && it.field.height == mGameState.value.field.height }
        }
        cancelTimer()
    }

    @Override
    int getNeighboringMineCount(int x, int y) {
        mGameState.value.field.getNeighboringMinesCount(x, y)
    }

    @Override
    boolean isRevealed(int x, int y) {
        mGameState.value.field.isRevealed(x, y)
    }

    @Override
    boolean isMarked(int x, int y) {
        mGameState.value.field.isMarked(x, y)
    }

    @Override
    boolean hasMine(int x, int y) {
        mGameState.value.field.hasMine(x, y)
    }

    @Override
    boolean isExploded(int x, int y) {
        mGameState.value.field.isExploded(x, y)
    }

    @Override
    int getFieldWidth() {
        mGameState.value.field.width
    }

    @Override
    int getFieldHeight() {
        mGameState.value.field.height
    }

    @Override
    int getMineCount() {
        mGameState.value.field.mineCount
    }

    @Override
    def setPlayer(String player) {
        mGameState.value.player = player
    }

    @Override
    String getPlayer() {
        mGameState.value.player
    }

    @Override
    int getStartTime() {
        mGameState.value.time
    }

    @Override
    boolean getIsNewGame() {
        mIsNewGame
    }

    @Override
    double getHiddenCellPercentage() {
        mGameState.value.field.hiddenCellCount / (mGameState.value.field.width * mGameState.value.field.width)
    }

    @Override
    String getFieldString() {
        mGameState.value.field.toString()
    }

    @Override
    def addObserverToAll(Observer o) {
        addGameStateObserver o
        addWinObserver o
        addGameOverObserver o
    }

    @Override
    def addGameStateObserver(Observer o) {
        mGameState.addObserver o
    }

    private def increaseScore() {
        def (fh, fw, mc) = [mGameState.value.field.width, mGameState.value.field.width, mGameState.value.field.mineCount]
        def (hfh, hfw, hmc) = [mPropertiesRepository.hardHeight, mPropertiesRepository.hardWidth,
                               mPropertiesRepository.hardMineCount]
        def baseScore = 200 * ((mc / fh * fw) / (hmc / hfh * hfw))
        def increase = baseScore / (0.5 / (mGameState.value.time == 0 ? 1 : mGameState.value.time))
        mGameState.value.score = mGameState.value.score + (increase as int)
        mGameState.notifyObserversWithValue()
    }

    private def revealSafeNeighbors(int x, int y) {
        if (!mGameState.value.field.isMarked(x, y)) {
            setIsRevealed(x, y, true)
            setIsMarked(x, y, false)

            if (getNeighboringMineCount(x, y) == 0) {
                for (int i in -1..1) {
                    for (int j in -1..1) {
                        if ((x + i >= 0 && x + i < fieldWidth)
                                && (y + j >= 0 && y + j < fieldHeight)) {
                            if (!isRevealed(x + i, y + j)) {
                                revealSafeNeighbors(x + i, y + j)
                            }
                        }
                    }
                }
            }
        }
    }

    private def startTimer() {
        mTimer = new Timer()
        TimerTask timerTask = new TimerTask() {
            @Override
            void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    void run() {
                        mGameState.value.time++
                        mGameState.notifyObserversWithValue()
                    }
                })
            }
        }
        mTimer.scheduleAtFixedRate(timerTask, 0, 1000)
    }

    def submitScore() {
        if (!mGameState.value.player.isEmpty()) {
            mScoreRepository.addScore new Pair(mGameState.value.player, mGameState.value.score)
        }
    }

    private def gameWon() {
        mIsGameWon.value = true
        mGameState.value.field.revealAllMines false
        cancelTimer()
        submitScore()
    }

    private def gameOver() {
        mGameState.value.field.revealAllMines true
        mIsGameOver.value = true
        cancelTimer()
    }

    private def checkWin() {
        boolean hasWon = true
        for (int i in 0..(mGameState.value.field.width - 1)) {
            for (int j in 0..(mGameState.value.field.width - 1)) {
                if (!mGameState.value.field.isRevealed(i, j) && !mGameState.value.field.isMarked(i, j)) {
                    hasWon = false
                } else if (mGameState.value.field.hasMine(i, j) && !mGameState.value.field.isMarked(i, j)) {
                    hasWon = false
                } else if (mGameState.value.field.isMarked(i, j) && !mGameState.value.field.hasMine(i, j)) {
                    hasWon = false
                }
            }
        }
        if (hasWon) {
            gameWon()
        }
    }

    private def setIsRevealed(int x, int y, boolean value) {
        mGameState.value.field.setIsRevealed(x, y, value)
    }

    private def setIsMarked(int x, int y, boolean value) {
        mGameState.value.field.setIsMarked(x, y, value)
    }
}

