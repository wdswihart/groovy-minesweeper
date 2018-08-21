package wdswihart.groovyminesweeper.models

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import com.google.inject.assistedinject.AssistedInject
import groovy.transform.CompileStatic
import wdswihart.groovyminesweeper.Main
import wdswihart.groovyminesweeper.factories.CellFactory

import javax.annotation.Nullable

@CompileStatic
class Field {
    private int mWidth
    private int mHeight
    private int mMineCount
    private List<List<Cell>> mCells

    private final CellFactory mCellFactory

    @AssistedInject
    Field(
            @Assisted('fieldWidth') int width,
            @Assisted('fieldHeight') int height,
            @Assisted('fieldMineCount') int mineCount,
            @Assisted @Nullable List<List<Cell>> cells,
            CellFactory cellFactory) {
        mCellFactory = cellFactory
        mWidth = width
        mHeight = height
        mMineCount = mineCount
        if (cells == null) {
            setUpCells()
            plantMines()
        } else {
            mCells = cells
        }
    }

    private int calcNeighboringMines(int x, int y) {
        def mineCount = 0
        for (int i in -1..1) {
            for (int j in -1..1) {
                if ((x + i >= 0 && x + i < mWidth) && (y + j >= 0 && y + j < mHeight)) {
                    if (mCells[x + i][y + j].getHasMine()) {
                        mineCount++
                    }
                }
            }
        }
        mineCount
    }

    private void setUpCells() {
        mCells = new ArrayList<>()
        for (int i in 0..(mWidth - 1)) {
            mCells << new ArrayList<>()
            for (int j in 0..(mHeight - 1)) {
                mCells[i] << mCellFactory.create(i, j)
            }
        }
    }

    void revealAllMines(boolean shouldExplode) {
        mCells.eachWithIndex { List<Cell> col, int i ->
            col.eachWithIndex { Cell cell, int j ->
                if (mCells[i][j].hasMine) {
                    mCells[i][j].isRevealed = true
                    mCells[i][j].isExploded = shouldExplode
                }
            }
        }
    }

    void plantMines() {
        int count = 0
        while (count < mMineCount) {
            int i = Math.floor(Main.random.nextDouble() * mWidth) as int
            int j = Math.floor(Main.random.nextDouble() * mHeight) as int
            if (!mCells[i][j].getHasMine()) {
                mCells[i][j].hasMine = true
                count++
            }
        }

        mCells.eachWithIndex { List<Cell> col, int i ->
            col.eachWithIndex { Cell cell, int j ->
                if (!mCells[i][j].hasMine) {
                    mCells[i][j].neighboringMinesCount = calcNeighboringMines(i, j)
                }
            }
        }
    }

    boolean isExploded(int x, int y) {
        mCells[x][y].exploded
    }

    boolean isMarked(int x, int y) {
        mCells[x][y].isMarked
    }

    void setIsMarked(int x, int y, value) {
        mCells[x][y].isMarked = value
    }

    boolean isRevealed(int x, int y) {
        mCells[x][y].isRevealed
    }

    void setIsRevealed(int x, int y, value) {
        mCells[x][y].isRevealed = value
    }

    int getNeighboringMinesCount(int x, int y) {
        mCells[x][y].neighboringMinesCount
    }

    int getWidth() {
        mWidth
    }

    int getHeight() {
        mHeight
    }

    int getMineCount() {
        mMineCount
    }

    boolean hasMine(int x, int y) {
        mCells[x][y].getHasMine()
    }

    int getHiddenCellCount() {
        def count = 0
        for (int i in 0..mCells.size() - 1) {
            for (int j in 0..mCells[i].size() - 1) {
                if (!mCells[i][j].isRevealed) {
                    count++
                }
            }
        }
        count
    }

    String toString() {
        def string = new StringBuilder()
        string.append(mWidth).append(',').append(mHeight).append(',').append(mMineCount).append(',')
        mCells.each { col ->
            col.each { cell ->
                string.append(cell.x).append(',')
                        .append(cell.y).append(',')
                        .append(cell.hasMine).append(',')
                        .append(cell.isRevealed).append(',')
                        .append(cell.isMarked).append(',')
                        .append(cell.neighboringMinesCount).append(',')
            }
        }
        string.replace(string.length() - 1, string.length(), '')
        string
    }

    void resetField() {
        setUpCells()
        plantMines()
    }

}



