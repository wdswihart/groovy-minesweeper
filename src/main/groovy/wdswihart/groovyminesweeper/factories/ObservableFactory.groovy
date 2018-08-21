package wdswihart.groovyminesweeper.factories

import wdswihart.groovyminesweeper.utils.ObservableList
import wdswihart.groovyminesweeper.utils.ObservableValue

interface ObservableFactory {
    ObservableList createList(List data)
    ObservableValue createValue(Object value)
}