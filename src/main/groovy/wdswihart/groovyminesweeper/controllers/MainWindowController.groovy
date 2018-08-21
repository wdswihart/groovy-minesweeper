package wdswihart.groovyminesweeper.controllers

import com.google.inject.Inject
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.Window
import wdswihart.groovyminesweeper.repositories.PropertiesRepository

class MainWindowController implements Initializable {
    @FXML private GridPane mTitleBar
    @FXML private Label mTitle
    @FXML private ImageView mIconView
    @FXML private Button mExitButton
    @FXML private Button mMinimizeButton
    @FXML private Button mSizeButton

    @FXML private GridPane mContentPane

    private Window mWindow

    private boolean mIsFullscreen = false
    private double mStoredHeight
    private double mStoredWidth
    private double mStoredX
    private double mStoredY

    private double mLastMouseX
    private double mLastMouseY

    private final PropertiesRepository mPropertiesRepository

    @Inject
    MainWindowController(PropertiesRepository propertiesRepository) { mPropertiesRepository = propertiesRepository }

    @Override
    void initialize(URL location, ResourceBundle resources) {
        try {
            mExitButton.graphic = new ImageView(new Image(
                    getClass().getResourceAsStream("/images/icons/baseline_close_white_18dp.png")
            ))
            mMinimizeButton.graphic = new ImageView(new Image(
                    getClass().getResourceAsStream("/images/icons/baseline_minimize_white_18dp.png")
            ))
            mSizeButton.graphic = new ImageView(new Image(
                    getClass().getResourceAsStream("/images/icons/baseline_fullscreen_white_18dp.png")
            ))
        } catch (Exception e) {
            e.printStackTrace()
        }

        mExitButton.setOnAction {
            mWindow.hide()
        }
        mMinimizeButton.setOnAction {
            (mWindow as Stage).iconified = true
        }
        mSizeButton.setOnAction {
            if (mIsFullscreen) {
                mIsFullscreen = false

                mWindow.setWidth(mStoredWidth)
                mWindow.setHeight(mStoredHeight)
                mWindow.setX(mStoredX)
                mWindow.setY(mStoredY)

                try {
                    mSizeButton.setGraphic(new ImageView(new Image(
                            getClass().getResourceAsStream("/images/icons/baseline_fullscreen_white_18dp.png")
                    )))
                } catch (Exception e) {
                    e.printStackTrace()
                }
            } else {
                mIsFullscreen = true

                mStoredWidth = mWindow.getWidth()
                mStoredHeight = mWindow.getHeight()
                mStoredX = mWindow.getX()
                mStoredY = mWindow.getY()

                Screen screen = Screen.getPrimary()
                mWindow.setWidth(screen.getVisualBounds().getWidth())
                mWindow.setHeight(screen.getVisualBounds().getHeight())
                mWindow.setX(0)
                mWindow.setY(0)

                try {
                    mSizeButton.setGraphic(new ImageView(new Image(
                            getClass().getResourceAsStream("/images/icons/baseline_fullscreen_exit_white_18dp.png")
                    )))
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }
        }

        mTitleBar.setOnMousePressed { event ->
            mLastMouseX = event.getSceneX()
            mLastMouseY = event.getSceneY()
        }
        mTitleBar.setOnMouseDragged { event ->
            if (!checkMouseOnEdges(event)) {
                mWindow.setX(event.getScreenX() - mLastMouseX)
                mWindow.setY(event.getScreenY() - mLastMouseY)
            }
        }

        mIconView.preserveRatio = true
        mIconView.image = new Image(getClass().getResource('/images/mine.png').toExternalForm())
    }

    private boolean checkMouseOnEdges(MouseEvent event) {
        Scene scene = mTitle.getScene()
        int borderX = 8
        int borderY = 4
        return event.getSceneX() < borderX && event.getSceneY() < borderY ||
                event.getSceneX() < borderX && event.getSceneY() > scene.getHeight() - borderY ||
                event.getSceneX() > scene.getWidth() - borderX && event.getSceneY() < borderY ||
                event.getSceneX() > scene.getWidth() - borderX && event.getSceneY() > scene.getHeight() - borderY ||
                event.getSceneX() < borderX ||
                event.getSceneX() > scene.getWidth() - borderX ||
                event.getSceneY() < borderY ||
                event.getSceneY() > scene.getHeight() - borderY
    }

    void setWindow(Window window) {
        mWindow = window
    }

    void setTitle(String title) {
        mTitle.setText(title)
    }

    ReadOnlyDoubleProperty getContentHeightProperty() {
        mContentPane.heightProperty()
    }

    ReadOnlyDoubleProperty getContentWidthProperty() {
        mContentPane.widthProperty()
    }

    void addContent(javafx.scene.Node content) {
        mContentPane.add(content, 0, 1)
    }
}
