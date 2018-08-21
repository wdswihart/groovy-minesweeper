package wdswihart.groovyminesweeper.factories

import wdswihart.groovyminesweeper.models.Cell

interface CellFactory {
    Cell create(int x, int y)
}