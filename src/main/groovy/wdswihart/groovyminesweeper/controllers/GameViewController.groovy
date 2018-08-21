package wdswihart.groovyminesweeper.controllers

import com.google.inject.Inject
import groovy.transform.CompileStatic
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import javafx.util.Pair
import wdswihart.groovyminesweeper.models.GameEngine
import wdswihart.groovyminesweeper.models.GameState
import wdswihart.groovyminesweeper.repositories.PropertiesRepository
import wdswihart.groovyminesweeper.repositories.ScoreRepository
import wdswihart.groovyminesweeper.services.NavigationService

@CompileStatic
class GameViewController implements Initializable, Observer {
    @FXML private GridPane mFieldPane
    @FXML private Label mStatusLabel
    @FXML private Label mScoreLabel
    @FXML private Label mTimeLabel
    @FXML private GridPane mControlsBar
    @FXML private Button mBackButton
    @FXML private Button mRestartButton

    @FXML private ImageView mEmojiView
    private Image mSlightlySmilingFace
    private Image mAlarmedFace
    private Image mSmilingFace
    private Image mGrimacingFace
    private Image mDeadFace
    private Image mSunglassesFace
    private Image mHeartEyesFace

    final double BUTTON_TEXT_RATIO = 0.4
    final String DEFAULT_LABEL_COLOR = 'ladder(primary, white 50% , black 49%)'
    final double BUTTON_EXPLOSION_RATIO = 0.7
    final double BUTTON_MINE_RATIO = 0.7
    private boolean mIsFieldSetup = false
    final double CELL_NUMBER_BRIGHTNESS = 215

    private final GameEngine mGameEngine
    private final PropertiesRepository mPropertiesRepository
    private final NavigationService mNavigationService
    private final ScoreRepository mScoreRepository

    @Inject
    GameViewController(GameEngine engine, PropertiesRepository propertiesRepository,
                       NavigationService navigationService, ScoreRepository scoreRepository) {
        mGameEngine = engine
        mPropertiesRepository = propertiesRepository
        mNavigationService = navigationService
        mScoreRepository = scoreRepository

        mGameEngine.addObserverToAll(this)
    }

    @Override
    void initialize(URL location, ResourceBundle resources) {
        mBackButton.setGraphic(new ImageView(new Image(getClass()
                .getResourceAsStream('/images/icons/baseline_arrow_back_white_18dp.png'))))
        mRestartButton.setGraphic(new ImageView(new Image(getClass()
                .getResourceAsStream('/images/icons/baseline_loop_white_18dp.png'))))
        mRestartButton.setOnAction {
            reset()
        }
        mBackButton.setOnAction() {
            mGameEngine.saveGame()
            mNavigationService.navigateBack()
            mIsFieldSetup = false
        }

        mSlightlySmilingFace = new Image(getClass().getResource('/images/emojis/slightly_smiling.png').toExternalForm())
        mAlarmedFace = new Image(getClass().getResource('/images/emojis/alarmed.png').toExternalForm())
        mSmilingFace = new Image(getClass().getResource('/images/emojis/smiling.png').toExternalForm())
        mGrimacingFace = new Image(getClass().getResource('/images/emojis/grimacing.png').toExternalForm())
        mDeadFace = new Image(getClass().getResource('/images/emojis/dead.png').toExternalForm())
        mSunglassesFace = new Image(getClass().getResource('/images/emojis/sunglasses.png').toExternalForm())
        mHeartEyesFace = new Image(getClass().getResource('/images/emojis/heart-eyes.png').toExternalForm())

        mEmojiView.preserveRatio = true
        mEmojiView.image = mSlightlySmilingFace

        setUpFieldPane()
        setInitialLabels()
    }

    private void resetCells() {
        mFieldPane.children.each {
            it.styleClass.remove('cell-revealed')
            it.styleClass.remove('cell-exploded')
            it.styleClass.remove('cell-has-mine')
            it.styleClass.remove('cell-marked')
            (it as Button).graphic = null
        }
    }

    private void setTimeLabel(int time, String color) {
        mTimeLabel.text = "Time: ${time}"
        mTimeLabel.style = "-fx-text-fill: ${color}"
    }

    private void setTimeLabel(int time) {
        setTimeLabel(time, DEFAULT_LABEL_COLOR)
    }

    private void setStatusLabel(String text, String color) {
        mStatusLabel.text = text
        mStatusLabel.style = "-fx-text-fill: ${color}"
    }

    private void setStatusLabel(String text) {
        setStatusLabel(text, DEFAULT_LABEL_COLOR)
    }

    private void setScoreLabel(int score, String color) {
        mScoreLabel.text = "Score: ${score}"
        mScoreLabel.style = "-fx-text-fill: ${color}"
    }

    private void setScoreLabel(int score) {
        setScoreLabel(score, DEFAULT_LABEL_COLOR)
    }

    private void setInitialLabels() {
        setStatusLabel("${mGameEngine.mineCount} mine${mGameEngine.mineCount > 1 ? 's' : ''}," +
                " ${mGameEngine.fieldWidth}x${mGameEngine.fieldHeight}")
        setScoreLabel(0)
        setTimeLabel(0)
    }

    void setUpSizeListeners() {
        ChangeListener listener = { observable, oldValue, newValue ->
            render()
        } as ChangeListener
        mFieldPane.widthProperty().addListener(listener)
        mFieldPane.heightProperty().addListener(listener)
    }

    private ImageView createImageView(String relativeImagePath, ReadOnlyDoubleProperty widthProperty,
                                      ReadOnlyDoubleProperty heightProperty, double ratio) {
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(relativeImagePath)))
        imageView.fitWidthProperty().bind(widthProperty * ratio)
        imageView.fitHeightProperty().bind(heightProperty * ratio)
        imageView.smooth = true
        imageView.preserveRatio = true
        imageView
    }

    private void renderRevealedCell(Button cell) {
        int x = GridPane.getColumnIndex(cell)
        int y = GridPane.getRowIndex(cell)
        if (!cell.styleClass.contains('cell-revealed') && !cell.styleClass.contains('cell-exploded')
                && !cell.styleClass.contains('cell-has-mine')) {
            if (mGameEngine.isExploded(x, y)) {
                cell.styleClass.add('cell-exploded')
                cell.text = ''
                cell.graphic = createImageView('/images/explosion.png',
                        cell.prefWidthProperty(), cell.prefHeightProperty(), BUTTON_EXPLOSION_RATIO)
            } else if (mGameEngine.hasMine(x, y) && !cell.styleClass.contains('cell-has-mine')) {
                if (mGameEngine.isMarked(x, y)) {
                    cell.styleClass.add('cell-marked')
                }
                cell.styleClass.add('cell-has-mine')
                cell.text = ''
                cell.graphic = createImageView('/images/mine.png',
                        cell.prefWidthProperty(), cell.prefHeightProperty(), BUTTON_MINE_RATIO)
            } else {
                cell.styleClass.add('cell-revealed')
                def count = mGameEngine.getNeighboringMineCount(x, y)
                if (count > 0) {
                    cell.text = count as String
                }
                cell.graphic = null
            }
        }
    }

    void render() {
        mFieldPane.children.each {
            def x = GridPane.getColumnIndex(it)
            def y = GridPane.getRowIndex(it)
            if (mGameEngine.isRevealed(x, y)) {
                renderRevealedCell(it as Button)
                def width = (it as Button).prefWidth
                def height = (it as Button).prefHeight
                it.style = "-fx-font-size: " +
                        "${(height > width ? (width * BUTTON_TEXT_RATIO) : (height * BUTTON_TEXT_RATIO))};"
                def count = mGameEngine.getNeighboringMineCount(x, y)
                if (count > 0) {
                    it.style = "${it.style} -fx-text-fill: rgb(${CELL_NUMBER_BRIGHTNESS}, " +
                            "${CELL_NUMBER_BRIGHTNESS / count}, 0); "
                }
            } else if (mGameEngine.isMarked(x, y)) {
                if (!it.styleClass.contains('cell-marked')) {
                    it.styleClass.add('cell-marked')
                    def width = (it as Button).prefWidth
                    def height = (it as Button).prefHeight
                    (it as Button).style =  "-fx-font-size: " +
                            "${(height > width ? (width * BUTTON_TEXT_RATIO) : (height * BUTTON_TEXT_RATIO))}"
                    (it as Button).text = '🚩'
                }
            } else {
                if (it.styleClass.contains('cell-marked')) {
                    it.styleClass.remove('cell-marked')
                }
                (it as Button).text = ''
            }
        }
    }

    private void setEmojiBasedOnCompletion() {
        double percentage = mGameEngine.getHiddenCellPercentage()
        if (percentage > 0.75) {
            mEmojiView.image = mSlightlySmilingFace
        } else if (percentage > 0.25) {
            mEmojiView.image = mSmilingFace
        } else {
            mEmojiView.image = mGrimacingFace
        }
    }

    private void addCellButton(GridPane field, int x, int y) {
        def button = new Button()
        button.setOnMousePressed { event ->
            if (event.button == MouseButton.PRIMARY) {
                mEmojiView.image = mAlarmedFace
            }
        }
        button.setOnMouseReleased { event ->
            if (event.button == MouseButton.PRIMARY) {
                if (!mGameEngine.isRevealed(x, y)) {
                    mGameEngine.excavate(x, y)
                } else if (event.clickCount > 1) {
                    mGameEngine.excavateNeighbors(x, y)
                }
            } else {
                if (mGameEngine.isMarked(x, y)) {
                    mGameEngine.unmark(x, y)
                } else {
                    mGameEngine.mark(x, y)
                }
            }
            render()
        }
        button.prefWidthProperty().bind((mFieldPane.widthProperty().divide(mGameEngine.fieldWidth)))
        button.prefHeightProperty().bind(mFieldPane.heightProperty().divide(mGameEngine.fieldHeight))

        button.styleClass.add('cell')

        field.add(button, x, y)
    }

    private void setUpFieldPane() {
        resetFieldPane()
        for (def i = 0; i < mGameEngine.fieldWidth; i++) {
            def col = new ColumnConstraints()
            col.hgrow = Priority.ALWAYS
            mFieldPane.columnConstraints.add(col)
            for (def j = 0; j < mGameEngine.fieldHeight; j++) {
                def row = new RowConstraints()
                row.vgrow = Priority.ALWAYS
                mFieldPane.rowConstraints.add(row)
                addCellButton(mFieldPane, i, j)
            }
        }
        render()
    }

    private void resetFieldPane() {
        mFieldPane.rowConstraints.removeAll(mFieldPane.rowConstraints)
        mFieldPane.columnConstraints.removeAll(mFieldPane.columnConstraints)
        mFieldPane.children.removeAll(mFieldPane.children)
    }

    private void reset() {
        mIsFieldSetup = false
        mGameEngine.restart()
        mFieldPane.setMouseTransparent(false)
        setInitialLabels()
        resetCells()
        setEmojiBasedOnCompletion()
        render()
    }

    void bindFieldPrefSize(ReadOnlyDoubleProperty widthProperty, ReadOnlyDoubleProperty heightProperty) {
        mFieldPane.prefWidthProperty().bind(widthProperty)
        mFieldPane.prefHeightProperty().bind(heightProperty)
    }

    @Override
    void update(Observable o, Object arg) {
        if (mGameEngine.isGameOver) {
            mFieldPane.setMouseTransparent(true)
            setStatusLabel('You lose.', 'bad-status')
            setScoreLabel(mGameEngine.score, 'bad-status')
            setTimeLabel(mGameEngine.time, 'bad-status')
            mEmojiView.image = mDeadFace
            mIsFieldSetup = false
        } else if (mGameEngine.isGameWon) {
            mFieldPane.setMouseTransparent(true)
            setStatusLabel('You win!', 'good-status')
            setScoreLabel(mGameEngine.score, 'good-status')
            setTimeLabel(mGameEngine.time, 'good-status')
            if (mScoreRepository.isHighScore(new Pair(mGameEngine.player, mGameEngine.score))) {
                mEmojiView.image = mHeartEyesFace
            } else {
                mEmojiView.image = mSunglassesFace
            }
            mIsFieldSetup = false
        } else {
            setScoreLabel(mGameEngine.score)
            setTimeLabel(mGameEngine.time)
            setEmojiBasedOnCompletion()
        }

        if (arg instanceof GameState && !mIsFieldSetup) {
            if (mGameEngine.isNewGame) {
                println 'new game'
                mFieldPane.setMouseTransparent(false)
                setInitialLabels()
                resetCells()
            }
            println 'set up field'
            setUpFieldPane()
            render()
            mIsFieldSetup = true
        }
    }
}
