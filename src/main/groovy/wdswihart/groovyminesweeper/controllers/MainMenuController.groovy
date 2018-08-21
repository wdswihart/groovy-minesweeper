package wdswihart.groovyminesweeper.controllers

import com.google.inject.Inject
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.util.Pair
import wdswihart.groovyminesweeper.models.GameEngine
import wdswihart.groovyminesweeper.models.GameState
import wdswihart.groovyminesweeper.repositories.PropertiesRepository
import wdswihart.groovyminesweeper.repositories.SaveRepository
import wdswihart.groovyminesweeper.repositories.ScoreRepository
import wdswihart.groovyminesweeper.services.NavigationService

class MainMenuController implements Initializable, Observer {
    @FXML private Button mPlayButton
    @FXML private Button mDeleteButton
    @FXML private Button mSettingsButton

    @FXML private TextField mNameField

    @FXML private Spinner<Integer> mMineSpinner
    @FXML private Spinner<Integer> mWidthSpinner
    @FXML private Spinner<Integer> mHeightSpinner

    @FXML private VBox mScoreListVBox
    @FXML private VBox mSaveListVBox
    private Object mSelectedItem
    private Button mSelectedMenuListButton

    private Button mSelectedDifficultyButton = mEasyButton
    @FXML private Button mEasyButton
    @FXML private Button mMediumButton
    @FXML private Button mHardButton
    final double DIFFICULTY_ICON_RATIO = 0.5
    final String PRESSED_CLASS = 'difficulty-button-selected'

    private final PropertiesRepository mPropertiesRepository
    private final NavigationService mNavigationService
    private final GameEngine mGameEngine
    private final ScoreRepository mScoreRepository
    private final SaveRepository mSaveRepository

    @Inject
    MainMenuController(PropertiesRepository propertiesRepository, NavigationService navigationService,
                       GameEngine gameEngine, ScoreRepository scoreRepository, SaveRepository saveRepository) {
        mPropertiesRepository = propertiesRepository
        mNavigationService = navigationService
        mScoreRepository = scoreRepository
        mSaveRepository = saveRepository
        mGameEngine = gameEngine
    }

    @Override
    void initialize(URL location, ResourceBundle resources) {
        mScoreRepository.addScoresObserver(this)
        updateScoreList(mScoreRepository.scores)
        mSaveRepository.addSavesObserver(this)
        updateSaveList(mSaveRepository.saves)

        mNameField.textProperty().addListener new ChangeListener<String>() {
            @Override
            void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                checkSavesForSelection()
            }
        }

        setUpActionButtons()
        setUpDifficultyButtons()
        setUpSpinners()
    }

    private void setUpActionButtons() {
        mDeleteButton.setOnAction {
            if (mSelectedItem instanceof Pair<String, Integer>) {
                mScoreRepository.removeScore(mSelectedItem)
                mScoreListVBox.children.remove(mSelectedMenuListButton)
            } else if (mSelectedItem instanceof GameState) {
                mSaveRepository.removeSave(mSelectedItem)
                mSaveListVBox.children.remove(mSelectedMenuListButton)
            }
            mSelectedItem = null
        }
        mDeleteButton.graphic = new ImageView(new Image(getClass()
                .getResourceAsStream('/images/icons/baseline_delete_forever_white_18dp.png')))

        mSettingsButton.setOnAction {
            mNavigationService.navigateTo(NavigationService.View.SETTINGS)
        }
        mSettingsButton.graphic = new ImageView(new Image(getClass()
                .getResourceAsStream('/images/icons/baseline_settings_white_18dp.png')))

        mPlayButton.setOnAction() {
            mGameEngine.startNewGame(mNameField.text, mMineSpinner.value, mWidthSpinner.value, mHeightSpinner.value)
            mNavigationService.navigateTo(NavigationService.View.GAME)
        }
        mPlayButton.setGraphic(new ImageView(new Image(getClass()
                .getResourceAsStream('/images/icons/baseline_play_arrow_white_18dp.png'))))
    }

    private void setUpDifficultyButtons() {
        ImageView easyImage = new ImageView((new Image(getClass()
                .getResourceAsStream('/images/icons/easy_meter.png'))))
        easyImage.fitWidthProperty().bind(mEasyButton.widthProperty() * DIFFICULTY_ICON_RATIO)
        easyImage.fitHeightProperty().bind(mEasyButton.heightProperty() * DIFFICULTY_ICON_RATIO)
        mEasyButton.graphic = easyImage
        mEasyButton.setOnAction {
            resetButtons()
            setSpinners(mPropertiesRepository.easyMineCount, mPropertiesRepository.easyWidth,
                    mPropertiesRepository.easyHeight)
            mEasyButton.styleClass.add(PRESSED_CLASS)
            mSelectedDifficultyButton = mEasyButton
        }
        mEasyButton.styleClass.add(PRESSED_CLASS)

        ImageView mediumImage = new ImageView(new Image(getClass()
                .getResourceAsStream('/images/icons/medium_meter.png')))
        mediumImage.fitWidthProperty().bind(mMediumButton.widthProperty() * DIFFICULTY_ICON_RATIO)
        mediumImage.fitHeightProperty().bind(mMediumButton.heightProperty() * DIFFICULTY_ICON_RATIO)
        mMediumButton.graphic = mediumImage
        mMediumButton.setOnAction {
            resetButtons()
            setSpinners(mPropertiesRepository.mediumMineCount,
                    mPropertiesRepository.mediumWidth, mPropertiesRepository.mediumHeight)
            mMediumButton.styleClass.add(PRESSED_CLASS)
            mSelectedDifficultyButton = mMediumButton
        }

        ImageView hardImage = new ImageView(new Image(getClass()
                .getResourceAsStream('/images/icons/hard_meter.png')))
        hardImage.fitWidthProperty().bind(mHardButton.widthProperty() * DIFFICULTY_ICON_RATIO)
        hardImage.fitHeightProperty().bind(mHardButton.heightProperty() * DIFFICULTY_ICON_RATIO)
        mHardButton.graphic = hardImage
        mHardButton.setOnAction {
            resetButtons()
            setSpinners(mPropertiesRepository.hardMineCount, mPropertiesRepository.hardWidth,
                    mPropertiesRepository.hardHeight)
            mHardButton.styleClass.add(PRESSED_CLASS)
            mSelectedDifficultyButton = mHardButton
        }
    }

    private void setUpSpinners() {
        def changeListener = new ChangeListener<Integer>() {
            @Override
            void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                mPlayButton.requestFocus()
                resetButtons()
                checkSavesForSelection()
            }
        }

        def scrollEventHandler = { event ->
            if (event.deltaY > 0) {
                (event.source as Spinner<Integer>).valueFactory.setValue mHeightSpinner.value + 1
            } else {
                (event.source as Spinner<Integer>).valueFactory.setValue mHeightSpinner.value - 1
            }
        }

        mHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                mPropertiesRepository.easyHeight, mPropertiesRepository.hardHeight, mPropertiesRepository.defaultHeight))
        mHeightSpinner.setOnScroll scrollEventHandler
        mHeightSpinner.valueProperty().addListener changeListener

        mWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(mPropertiesRepository.easyWidth,
                mPropertiesRepository.hardWidth, mPropertiesRepository.defaultWidth))
        mWidthSpinner.setOnScroll scrollEventHandler
        mWidthSpinner.valueProperty().addListener changeListener

        mMineSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,
                mPropertiesRepository.hardHeight * mPropertiesRepository.hardWidth,
                mPropertiesRepository.defaultMineCount))
        mMineSpinner.setOnScroll scrollEventHandler
        mMineSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                mPlayButton.requestFocus()
                int max = mHeightSpinner.value * mWidthSpinner.value
                if (newValue > max) {
                    mMineSpinner.valueFactory.setValue max
                }
                resetButtons()
                checkSavesForSelection()
            }
        })
    }

    private void checkSavesForSelection() {
        resetSaveButton()
        mSaveListVBox.children.each {
            def splitText = (it as Button).text.split(',')
            def widthAndHeight = splitText[2].trim().split('x')
            if (splitText[0].trim() == mNameField.getText().trim()
                    && splitText[1].trim().replace('mines', '').replace('mine', '') as int
                    == mMineSpinner.value && widthAndHeight[0] as int == mWidthSpinner.value && widthAndHeight[1] as int
                    == mHeightSpinner.value) {
                (it as Button).fire()
            }
        }
    }

    private void resetSaveButton() {
        if (mSelectedItem instanceof GameState && mSelectedMenuListButton != null) {
            mSelectedMenuListButton.styleClass.remove('menu-list-button-selected')
            mSelectedMenuListButton = null
            mSelectedItem = null
        }
    }

    private void resetButtons() {
        mEasyButton.styleClass.remove(PRESSED_CLASS)
        mMediumButton.styleClass.remove(PRESSED_CLASS)
        mHardButton.styleClass.remove(PRESSED_CLASS)
        mSelectedDifficultyButton = null
        resetSaveButton()
    }

    private void setSpinners(int mines, int width, int height) {
        mWidthSpinner.valueFactory.value = width
        mHeightSpinner.valueFactory.value = height
        mMineSpinner.valueFactory.value = mines
    }

    private Button createMenuListButton(String text, Object o) {
        def button = new Button(text)
        button.styleClass.add('menu-list-button')
        button.setMaxWidth(Double.MAX_VALUE)
        button.setOnAction {
            mSelectedItem = o
            if (mSelectedItem instanceof GameState) {
                mNameField.setText(mSelectedItem.player)
                mMineSpinner.valueFactory.setValue mSelectedItem.field.mineCount
                mWidthSpinner.valueFactory.setValue mSelectedItem.field.width
                mHeightSpinner.valueFactory.setValue mSelectedItem.field.height
            }
            button.styleClass.add('menu-list-button-selected')
            if (mSelectedMenuListButton != null) {
                mSelectedMenuListButton.styleClass.remove('menu-list-button-selected')
            }
            mSelectedMenuListButton = button
        }
        if (mSelectedMenuListButton != null && button.text == mSelectedMenuListButton.text) {
            mSelectedMenuListButton = button
            mSelectedItem = o
            button.styleClass.add('menu-list-button-selected')
        }
        button
    }

    def updateSaveList(List<GameState> gameStates) {
        mSaveListVBox.children.removeAll(mSaveListVBox.children)
        gameStates.sort(Comparator.comparing { o -> o.player })
        gameStates.reverse().each {
            mSaveListVBox.children.add(createMenuListButton("${it.player}, ${it.field.mineCount} " +
                                        "mine${it.field.mineCount == 1 ? '' : 's'}, " +
                                        "${it.field.width}x${it.field.height}", it))
        }
    }

    private void updateScoreList(List<Pair<String, Integer>> scores) {
        mScoreListVBox.children.removeAll(mScoreListVBox.children)
        scores.sort(Comparator.comparing { o -> o.value })
        scores.reverse().eachWithIndex { Pair<String, Integer> it, int i ->
            mScoreListVBox.children.add(createMenuListButton("${i + 1 + '. ' + it.key} -- ${it.value}", it))
        }
    }

    @Override
    void update(Observable obs, Object arg) {
        updateScoreList(mScoreRepository.scores)
        updateSaveList(mSaveRepository.saves)
        if (mSaveRepository.saves.any { save -> save.player == mNameField.text &&
                save.field.mineCount == mMineSpinner.value && save.field.width == mWidthSpinner.value &&
                save.field.height == mHeightSpinner.value}) {
            (mSaveListVBox.children.find { child -> (child as Button).text == "${mNameField.text}, ${mMineSpinner.value} " +
                    "mine${mMineSpinner.value == 1 ? '' : 's'}, " +
                    "${mWidthSpinner.value}x${mHeightSpinner.value}"} as Button).fire()
        }
    }
}
