package wdswihart.groovyminesweeper

import com.google.inject.Guice
import com.google.inject.Injector
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.stage.Stage
import javafx.stage.StageStyle
import wdswihart.groovyminesweeper.controllers.GameViewController
import wdswihart.groovyminesweeper.controllers.MainWindowController
import wdswihart.groovyminesweeper.models.GameEngine
import wdswihart.groovyminesweeper.models.ModelModule
import wdswihart.groovyminesweeper.repositories.PropertiesRepository
import wdswihart.groovyminesweeper.repositories.RepositoryModule
import wdswihart.groovyminesweeper.repositories.SaveRepository
import wdswihart.groovyminesweeper.repositories.ScoreRepository
import wdswihart.groovyminesweeper.services.NavigationService
import wdswihart.groovyminesweeper.services.ServiceModule
import wdswihart.groovyminesweeper.utils.ResizeUtil

class Main extends Application implements Observer {
    static final Random random = new Random(System.currentTimeMillis())
    private Injector mInjector;

    @Override
    void start(Stage primaryStage) throws Exception {
        mInjector = Guice.createInjector(new RepositoryModule(), new ModelModule(), new ServiceModule())
        NavigationService navigationService = mInjector.getInstance(NavigationService)
        PropertiesRepository propertiesRepo = mInjector.getInstance(PropertiesRepository.class)

        FXMLLoader gameViewLoader = new FXMLLoader(getClass().getResource('/views/GameView.fxml'))
        gameViewLoader.controllerFactory = mInjector::getInstance
        Pane gameView = gameViewLoader.load()
        GameViewController gameViewController = gameViewLoader.getController()
        gameViewController.setUpSizeListeners()
        navigationService.gameView = gameView

        FXMLLoader mainMenuViewLoader = new FXMLLoader(getClass().getResource('/views/MainMenu.fxml'))
        mainMenuViewLoader.controllerFactory = mInjector::getInstance
        Pane mainMenuView = mainMenuViewLoader.load()
        navigationService.mainMenuView = mainMenuView

        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource('/views/Settings.fxml'))
        settingsLoader.controllerFactory = mInjector::getInstance
        Pane settingsView = settingsLoader.load()
        navigationService.settingsView = settingsView

        FXMLLoader mainWindowLoader = new FXMLLoader(getClass().getResource('/views/MainWindow.fxml'))
        mainWindowLoader.controllerFactory = mInjector::getInstance
        Parent root = mainWindowLoader.load()
        MainWindowController mainWindowController = mainWindowLoader.getController()
        mainWindowController.title = propertiesRepo.applicationTitle
        mainWindowController.addContent(mainMenuView)
        mainWindowController.addContent(gameView)
        mainWindowController.addContent(settingsView)
        mainWindowController.window = primaryStage

        gameViewController.bindFieldPrefSize(mainWindowController.contentWidthProperty, mainWindowController.contentHeightProperty)

        primaryStage.minWidth = propertiesRepo.minWidth
        primaryStage.minHeight = propertiesRepo.minHeight

        primaryStage.initStyle(StageStyle.UNDECORATED)
        primaryStage.icons.add(new Image(getClass().getResource("/images/mine.png").toExternalForm()))
        primaryStage.setHeight(propertiesRepo.minHeight)
        primaryStage.setWidth(propertiesRepo.minWidth)
        primaryStage.scene = new Scene(root, primaryStage.width, primaryStage.height)
        ResizeUtil.addResizeListener(primaryStage)
        primaryStage.show()
    }

    @Override
    void stop() {
        mInjector.getInstance(GameEngine).saveGame()
        mInjector.getInstance(ScoreRepository).storeData()
        mInjector.getInstance(SaveRepository).storeData()
    }

    static void main(String... args) {
        launch(Main, args)
    }

    @Override
    void update(Observable o, Object arg) {

    }
}
