package wdswihart.groovyminesweeper.utils

class ObservableValue<T> extends Observable {
    private T mValue

    ObservableValue(T value) {
        mValue = value
    }

    ObservableValue() {
        mValue = null
    }

    void notifyObserversWithValue() {
        setChanged()
        notifyObservers(mValue)
    }

    void setValue(T value) {
        mValue = value
        notifyObserversWithValue()
    }

    T getValue() {
        mValue
    }
}
