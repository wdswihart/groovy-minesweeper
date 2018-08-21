package wdswihart.groovyminesweeper.models

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import com.google.inject.assistedinject.AssistedInject
import wdswihart.groovyminesweeper.factories.ModelFactory

class GameState {
    private String mPlayer = ''
    private Field mField = null
    private int mScore = 0
    private int mTime = 0

    private final ModelFactory mModelFactory

    @AssistedInject
    GameState(
            @Assisted String player,
            @Assisted('gameStateMineCount') int mineCount,
            @Assisted('gameStateWidth') int width,
            @Assisted('gameStateHeight') int height,
            ModelFactory modelFactory) {
        mPlayer = player
        mModelFactory = modelFactory
        mField = modelFactory.create(width, height, mineCount, null)
    }

    @AssistedInject
    GameState(ModelFactory modelFactory) {
        this('', 0, 0, 0, modelFactory)
    }

    String getPlayer() {
        mPlayer
    }

    void setPlayer(String player) {
        mPlayer = player
    }

    Field getField() {
        mField
    }

    void setField(Field field) {
        mField = field
    }

    int getScore() {
        mScore
    }

    void setScore(int score) {
        mScore = score
    }

    int getTime() {
        mTime
    }

    void setTime(int time) {
        mTime = time
    }
}
