package wdswihart.groovyminesweeper.factories

import wdswihart.groovyminesweeper.models.Cell

class CellFactoryImpl implements CellFactory {
    CellFactoryImpl() {}

    @Override
    Cell create(int x, int y) {
        new Cell(x, y)
    }
}
