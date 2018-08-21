package wdswihart.groovyminesweeper.repositories

import wdswihart.groovyminesweeper.models.GameState

interface SaveRepository {
    void addSavesObserver(Observer o)
    boolean hasSave(String player, int mineCount, int width, int height)
    GameState getSave(String player, int mineCount, int width, int height)
    boolean removeSave(GameState state)
    void removeAllSaves()
    List<GameState> getSaves()
    void save(GameState state)

    void storeData()
}
