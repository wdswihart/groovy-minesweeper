// Originally created by Alexander Berg.
// https://stackoverflow.com/questions/19455059/allow-user-to-resize-an-undecorated-stage

package wdswihart.groovyminesweeper.utils

import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

class ResizeUtil {
    private static void addListenerDeeply(Node node, EventHandler<MouseEvent> listener) {
        node.addEventHandler(MouseEvent.MOUSE_MOVED, listener)
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener)
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener)
        node.addEventHandler(MouseEvent.MOUSE_EXITED, listener)
        node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, listener)
        if (node instanceof Parent) {
            Parent parent = (Parent) node
            ObservableList<Node> children = parent.getChildrenUnmodifiable()
            for (Node child : children) {
                addListenerDeeply(child, listener)
            }
        }
    }

    static void addResizeListener(Stage stage) {
        ResizeListener resizeListener = new ResizeListener(stage)
        stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener)
        stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener)
        stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener)
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener)
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener)
        ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable()
        for (Node child : children) {
            addListenerDeeply(child, resizeListener)
        }
    }

    static class ResizeListener implements EventHandler<MouseEvent> {
        private Stage stage
        private Cursor cursor = Cursor.DEFAULT
        private int borderY = 4
        private int borderX = 8
        private double startX = 0
        private double startY = 0

        ResizeListener(Stage stage) {
            this.stage = stage
        }

        @Override
        void handle(MouseEvent mouseEvent) {
            EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType()
            Scene scene = stage.getScene()

            double mouseEventX = mouseEvent.getSceneX(),
                   mouseEventY = mouseEvent.getSceneY(),
                   sceneWidth = scene.getWidth(),
                   sceneHeight = scene.getHeight()

            if (MouseEvent.MOUSE_MOVED == mouseEventType) {
                if (mouseEventX < borderX && mouseEventY < borderY) {
                    cursor = Cursor.NW_RESIZE
                } else if (mouseEventX < borderX && mouseEventY > sceneHeight - borderY) {
                    cursor = Cursor.SW_RESIZE
                } else if (mouseEventX > sceneWidth - borderX && mouseEventY < borderY) {
                    cursor = Cursor.NE_RESIZE
                } else if (mouseEventX > sceneWidth - borderX && mouseEventY > sceneHeight - borderY) {
                    cursor = Cursor.SE_RESIZE
                } else if (mouseEventX < borderX) {
                    cursor = Cursor.W_RESIZE
                } else if (mouseEventX > sceneWidth - borderX) {
                    cursor = Cursor.E_RESIZE
                } else if (mouseEventY < borderY) {
                    cursor = Cursor.N_RESIZE
                } else if (mouseEventY > sceneHeight - borderY) {
                    cursor = Cursor.S_RESIZE
                } else {
                    cursor = Cursor.DEFAULT
                }
                scene.setCursor(cursor)
            } else if(MouseEvent.MOUSE_EXITED == mouseEventType || MouseEvent.MOUSE_EXITED_TARGET == mouseEventType){
                scene.setCursor(Cursor.DEFAULT)
            } else if (MouseEvent.MOUSE_PRESSED == mouseEventType) {
                startX = stage.getWidth() - mouseEventX
                startY = stage.getHeight() - mouseEventY
            } else if (MouseEvent.MOUSE_DRAGGED == mouseEventType) {
                if (Cursor.DEFAULT != cursor) {
                    if (Cursor.W_RESIZE != cursor && Cursor.E_RESIZE != cursor) {
                        double minHeight = stage.getMinHeight() > (borderY *2) ? stage.getMinHeight() : (borderY *2)
                        if (Cursor.NW_RESIZE == cursor || Cursor.N_RESIZE == cursor || Cursor.NE_RESIZE == cursor) {
                            if (stage.getHeight() > minHeight || mouseEventY < 0) {
                                stage.setHeight(stage.getY() - mouseEvent.getScreenY() + stage.getHeight())
                                stage.setY(mouseEvent.getScreenY())
                            }
                        } else {
                            if (stage.getHeight() > minHeight || mouseEventY + startY - stage.getHeight() > 0) {
                                stage.setHeight(mouseEventY + startY)
                            }
                        }
                    }

                    if (Cursor.N_RESIZE != cursor && Cursor.S_RESIZE != cursor) {
                        double minWidth = stage.getMinWidth() > (borderY *2) ? stage.getMinWidth() : (borderY *2)
                        if (Cursor.NW_RESIZE == cursor || Cursor.W_RESIZE == cursor || Cursor.SW_RESIZE == cursor) {
                            if (stage.getWidth() > minWidth || mouseEventX < 0) {
                                stage.setWidth(stage.getX() - mouseEvent.getScreenX() + stage.getWidth())
                                stage.setX(mouseEvent.getScreenX())
                            }
                        } else {
                            if (stage.getWidth() > minWidth || mouseEventX + startX - stage.getWidth() > 0) {
                                stage.setWidth(mouseEventX + startX)
                            }
                        }
                    }
                }

            }
        }
    }
}

