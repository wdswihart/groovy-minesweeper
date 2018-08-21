package wdswihart.groovyminesweeper.factories

import com.google.inject.assistedinject.Assisted
import wdswihart.groovyminesweeper.models.Cell
import wdswihart.groovyminesweeper.models.Field
import wdswihart.groovyminesweeper.models.GameState

interface ModelFactory {
    Field create(
            @Assisted('fieldWidth') int width,
            @Assisted('fieldHeight') int height,
            @Assisted('fieldMineCount') int mineCount,
            List<List<Cell>> cells)
    GameState create(
            String player,
            @Assisted('gameStateMineCount') int mineCount,
            @Assisted('gameStateWidth') int width,
            @Assisted('gameStateHeight') int height)
    GameState create()
}