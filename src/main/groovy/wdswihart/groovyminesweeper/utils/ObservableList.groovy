package wdswihart.groovyminesweeper.utils

class ObservableList<T> extends Observable {
    private List<T> mData

    ObservableList(List<T> data) {
        mData = data
    }

    void notifyObserversWithData() {
        setChanged()
        notifyObservers(mData)
    }

    void add(T item) {
        mData.add(item)
        notifyObserversWithData()
    }

    boolean remove(T item) {
       if (mData.remove(item)) {
            notifyObserversWithData()
            true
        }
        false
    }

    void update(T oldItem, T newItem) {
        if (mData.remove oldItem) {
            mData.add newItem
            notifyObserversWithData()
        }
    }

    void setData(List<T> data) {
        mData = data
        notifyObserversWithData()
    }

    List<T> getData() {
        return mData
    }
}
