package wdswihart.groovyminesweeper.services

import com.google.inject.Singleton
import javafx.scene.layout.Pane

@Singleton
class NavigationServiceImpl implements NavigationService {
    private Pane mGameView
    private Pane mMainMenuView
    private Pane mSettingsView

    private Stack<Pane> mNavigationStack = new Stack<>()
    private Pane mCurrent

    @Override
    void setMainMenuView(Pane pane) {
        mMainMenuView = pane
        mCurrent = pane
    }

    @Override
    void setGameView(Pane pane) {
        mGameView = pane
        mGameView.visible = false
    }

    @Override
    void setSettingsView(Pane pane) {
        mSettingsView = pane
        mSettingsView.visible = false
    }

    private void setCurrent(Pane pane) {
        pane.visible = true
        mCurrent.visible = false
        mNavigationStack.push(mCurrent)
        mCurrent = pane
    }

    @Override
    void navigateTo(View view) {
        switch (view) {
            case View.MAIN_MENU:
                setCurrent(mMainMenuView)
                break
            case View.GAME:
                setCurrent(mGameView)
                break
            case View.SETTINGS:
                setCurrent(mSettingsView)
        }
    }

    @Override
    void navigateBack() {
        setCurrent(mNavigationStack.pop())
    }
}
