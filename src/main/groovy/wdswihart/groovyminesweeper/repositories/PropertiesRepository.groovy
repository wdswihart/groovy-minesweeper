package wdswihart.groovyminesweeper.repositories

interface PropertiesRepository {
    double getMinWidth()
    double getMinHeight()
    String getApplicationTitle()

    int getEasyMineCount()
    int getEasyWidth()
    int getEasyHeight()

    int getMediumMineCount()
    int getMediumWidth()
    int getMediumHeight()

    int getHardMineCount()
    int getHardWidth()
    int getHardHeight()

    int getDefaultMineCount()
    int getDefaultWidth()
    int getDefaultHeight()
}
