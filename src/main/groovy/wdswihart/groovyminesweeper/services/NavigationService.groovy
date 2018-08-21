package wdswihart.groovyminesweeper.services

import javafx.scene.layout.Pane

interface NavigationService {
    enum View {
        MAIN_MENU,
        GAME,
        SETTINGS
    }

    void setMainMenuView(Pane pane)
    void setGameView(Pane pane)
    void setSettingsView(Pane pane)

    void navigateTo(View view)
    void navigateBack()
}
