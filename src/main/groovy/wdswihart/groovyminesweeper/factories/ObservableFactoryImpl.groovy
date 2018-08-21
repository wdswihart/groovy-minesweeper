package wdswihart.groovyminesweeper.factories

import wdswihart.groovyminesweeper.utils.ObservableList
import wdswihart.groovyminesweeper.utils.ObservableValue

class ObservableFactoryImpl implements ObservableFactory {
    ObservableFactoryImpl() {}

    @Override
    ObservableList createList(List data) {
        new ObservableList(data)
    }

    @Override
    ObservableValue createValue(Object value) {
        new ObservableValue(value)
    }
}
