package wdswihart.groovyminesweeper.models

interface GameEngine {
    def addObserverToAll(Observer o)
    def addGameStateObserver(Observer o)
    def addGameOverObserver(Observer o)
    boolean getIsGameOver()
    int getScore()
    def addWinObserver(Observer o)
    boolean getIsGameWon()
    int getTime()

    def cancelTimer()
    def excavate(int x, int y)
    def mark(int x, int y)
    def unmark(int x, int y)
    def excavateNeighbors(int x, int y)
    def restart()
    def startNewGame(String player, int mineCount, int width, int height)
    def saveGame()
    
    int getNeighboringMineCount(int x, int y)
    boolean isRevealed(int x, int y)
    boolean isMarked(int x, int y)
    boolean hasMine(int x, int y)
    boolean isExploded(int x, int y)
    double getHiddenCellPercentage()

    String getFieldString()
    int getFieldWidth()
    int getFieldHeight()
    int getMineCount()
    def setPlayer(String player)
    String getPlayer()
    int getStartTime()

    boolean getIsNewGame()
}
