package wdswihart.groovyminesweeper.repositories

import com.google.inject.Singleton

@Singleton
class PropertiesRepositoryImpl implements PropertiesRepository {
    private mMinWidth = 640
    private mMinHeight = 480
    private mApplicationTitle = "Groovy Minesweeper"

    private int mEasyMineCount = 10
    private int mEasyWidth = 8
    private int mEasyHeight = 8

    private int mMediumMineCount = 58 // 23% of cells, up from 15.6%
    private int mMediumWidth = 16
    private int mMediumHeight = 16

    private int mHardMineCount = 144 // 30% of cells, up from 20%
    private int mHardWidth = 30
    private int mHardHeight = 16

    @Override
    double getMinWidth() {
        mMinWidth
    }

    @Override
    double getMinHeight() {
        mMinHeight
    }

    @Override
    String getApplicationTitle() {
        mApplicationTitle
    }

    @Override
    int getEasyMineCount() {
        mEasyMineCount
    }

    @Override
    int getEasyWidth() {
        mEasyWidth
    }

    @Override
    int getEasyHeight() {
        mEasyHeight
    }

    @Override
    int getMediumMineCount() {
        mMediumMineCount
    }

    @Override
    int getMediumWidth() {
        mMediumWidth
    }

    @Override
    int getMediumHeight() {
        mMediumHeight
    }

    @Override
    int getHardMineCount() {
        mHardMineCount
    }

    @Override
    int getHardWidth() {
        mHardWidth
    }

    @Override
    int getHardHeight() {
        mHardHeight
    }

    @Override
    int getDefaultMineCount() {
        mEasyMineCount
    }

    @Override
    int getDefaultWidth() {
        mEasyWidth
    }

    @Override
    int getDefaultHeight() {
        mEasyHeight
    }
}
