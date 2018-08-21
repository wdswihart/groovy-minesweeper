package wdswihart.groovyminesweeper.controllers

import com.google.inject.Inject
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import wdswihart.groovyminesweeper.repositories.SaveRepository
import wdswihart.groovyminesweeper.repositories.ScoreRepository
import wdswihart.groovyminesweeper.services.NavigationService

class SettingsController implements Initializable {
    @FXML private Button mBackButton
    @FXML private Button mDeleteSavesButton
    @FXML private Button mDeleteScoresButton

    private final NavigationService mNavigationService
    private final SaveRepository mSaveRepository
    private final ScoreRepository mScoreRepository

    @Inject
    SettingsController(NavigationService navigationService, SaveRepository saveRepository,
                       ScoreRepository scoreRepository) {
        mNavigationService = navigationService
        mSaveRepository = saveRepository
        mScoreRepository = scoreRepository
    }

    @Override
    void initialize(URL location, ResourceBundle resources) {
        mBackButton.setOnAction {
            mNavigationService.navigateBack()
        }
        mBackButton.graphic = new ImageView(new Image(getClass()
                .getResourceAsStream('/images/icons/baseline_arrow_back_white_18dp.png')))

        mDeleteSavesButton.setOnAction {
            mSaveRepository.removeAllSaves()
        }

        mDeleteScoresButton.setOnAction {
            mScoreRepository.removeAllScores()
        }
    }
}
