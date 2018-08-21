package wdswihart.groovyminesweeper.models

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import groovy.transform.CompileStatic

@CompileStatic
class Cell {
    private int mX
    private int mY
    private boolean mHasMine = false
    private boolean mIsRevealed = false
    private boolean mIsMarked = false
    private boolean mIsExploded = false
    private int mNeighboringMinesCount = 0

    Cell(int x, int y) {
        mX = x
        mY = y
    }

    int getX() {
        mX
    }

    int getY() {
        mY
    }

    boolean isExploded() {
        mIsExploded
    }

    void setIsExploded(boolean isExploded) {
        mIsExploded = isExploded
    }

    boolean getHasMine() {
        mHasMine
    }

    void setHasMine(boolean value) {
        mHasMine = value
    }

    int getNeighboringMinesCount() {
        mNeighboringMinesCount
    }

    void setNeighboringMinesCount(int count) {
        mNeighboringMinesCount = count
    }

    boolean getIsRevealed() {
        mIsRevealed
    }

    void setIsRevealed(boolean isRevealed) {
        mIsRevealed = isRevealed
    }

    boolean getIsMarked() {
        mIsMarked
    }

    void setIsMarked(boolean isMarked) {
        mIsMarked = isMarked
    }

    boolean getIsExploded() {
        mIsExploded
    }
}
