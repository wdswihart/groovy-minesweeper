package wdswihart.groovyminesweeper.repositories

import com.google.inject.Inject
import com.google.inject.Singleton
import wdswihart.groovyminesweeper.models.Cell
import wdswihart.groovyminesweeper.models.Field
import wdswihart.groovyminesweeper.models.GameState
import wdswihart.groovyminesweeper.utils.ObservableList

@Singleton
class SaveRepositoryImpl implements SaveRepository {
    private ObservableList<GameState> mGameStates = new ObservableList<>([])

    @Inject
    SaveRepositoryImpl() {
        loadData()
    }

    private static Field getFieldFromString(String s) {
        List<List<Cell>> cells = new ArrayList<>()
        def splitLine = s.split(',')
        3.step splitLine.length - 1, 6, { i ->
            def cell = new Cell(splitLine[i] as int, splitLine[i + 1] as int)
            cell.hasMine = splitLine[i + 2].toBoolean()
            cell.isRevealed = splitLine[i + 3].toBoolean()
            cell.isMarked = splitLine[i + 4].toBoolean()
            cell.neighboringMinesCount = splitLine[i + 5] as int
            if (cells.size() <= cell.x) {
                cells << new ArrayList<>()
            }
            cells[cell.x] << cell
        }
        new Field(splitLine[0] as int, splitLine[1] as int, splitLine[2] as int, cells)
    }

    private void loadData() {
        try {
            new File('data/saved_games.csv').eachLine('UTF-8') { line ->
                def state = new GameState()
                def shouldIgnoreCommas = false
                def buffer = new StringBuilder()
                def i = 0
                line.toCharArray().each { c ->
                    if (c == ',' && !shouldIgnoreCommas && i < 3) {
                        switch(i++) {
                            case 0:
                                state.player = buffer.toString()
                                break
                            case 1:
                                state.score = buffer.toString() as int
                                break
                            case 2:
                                state.time = buffer.toString() as int
                                break
                        }
                        buffer.replace(0, buffer.length(), '')
                    } else if (c == '"') {
                        shouldIgnoreCommas = !shouldIgnoreCommas
                    } else {
                        buffer.append(c)
                    }
                }
                state.field = getFieldFromString(buffer.toString())
                mGameStates.data << state
            }
        } catch (Exception e) {
            System.err.println 'Failed to load saved games file.'
            e.printStackTrace()
        }
    }

    private static String validateString(String s) {
        s = s.replace('"', '')
        if (s.contains(',')) {
            "\"${s}\""
        } else {
            s
        }
    }

    @Override
    void storeData() {
        new File('data').mkdir()
        new File('data/saved_games.csv').createNewFile()
        new File('data/saved_games.csv').withWriter('UTF-8') { writer ->
            mGameStates.data.each {
                writer.write validateString(it.player) + ','
                writer.write it.score + ','
                writer.write it.time + ','
                writer.write it.field.toString()
                writer.newLine()
            }
        }
    }

    @Override
    void addSavesObserver(Observer o) {
        mGameStates.addObserver o
    }

    @Override
    boolean hasSave(String player, int mineCount, int width, int height) {
        mGameStates.data.any { it.player == player && it.field.mineCount == mineCount && it.field.width == width &&
                it.field.height == height }
    }

    @Override
    GameState getSave(String player, int mineCount, int width, int height) {
        mGameStates.data.find { it.player == player && it.field.mineCount == mineCount && it.field.width == width &&
                it.field.height == height }
    }

    @Override
    boolean removeSave(GameState state) {
        mGameStates.remove state
    }

    @Override
    void removeAllSaves() {
        mGameStates.data = []
    }

    @Override
    List<GameState> getSaves() {
        mGameStates.data
    }

    @Override
    void save(GameState state) {
        if (!state.player.isEmpty()) {
            def copy = new GameState()
            copy.player = state.player
            copy.field = state.field
            copy.score = state.score
            copy.time = state.time

            if (mGameStates.data.any { o -> o.player == state.player && o.field.mineCount == state.field.mineCount &&
                    o.field.width == state.field.width && o.field.height == state.field.height}) {
                mGameStates.update(
                        mGameStates.data.find { o -> o.player == state.player &&
                                o.field.mineCount == state.field.mineCount && o.field.width == state.field.width &&
                                o.field.height == state.field.height },
                        copy
                )
            } else {
                mGameStates.add copy
            }
        }
    }
}
