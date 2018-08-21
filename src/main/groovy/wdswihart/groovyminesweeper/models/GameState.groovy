package wdswihart.groovyminesweeper.models

class GameState {
    private String mPlayer = ''
    private Field mField = null
    private int mScore = 0
    private int mTime = 0

    GameState(String player, int mineCount, int width, int height) {
        mPlayer = player
        mField = new Field(width, height, mineCount, null)
    }

    GameState() {}

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
