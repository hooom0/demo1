package org.example.demo.uicontroller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.example.demo.modelcontroller.ModelController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Objects;

import static java.lang.Integer.parseInt;

public class HelloController {

    private static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");
    private static final PseudoClass INACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("inactive");

    private GridPane childrenGridPane = null;
    private final String styleBlack = "inactive";
    private final String styleWhite = "active";

    public Circle getIfShiftOrNot() {
        return ifShiftOrNot;
    }

    @FXML
    private Circle ifShiftOrNot;

    public TextField getWidthText() {
        return widthText;
    }

    @FXML
    private TextField widthText;

    public TextField getHighText() {
        return highText;
    }

    @FXML
    private TextField highText;

    Logger logger = LoggerFactory.getLogger(HelloController.class);

    public Button getButtonClear() {
        return buttonClear;
    }

    @FXML
    private Button buttonClear;

    @FXML
    private VBox vBox;

    public Button getButtonRunOnce() {
        return buttonRunOnce;
    }

    @FXML
    private Button buttonRunOnce;
    @SuppressWarnings("checkstyle:VisibilityModifier")
    @FXML
    private GridPane placeholderGridPane;

    public Button getButtonStart() {
        return buttonStart;
    }

    @FXML
    private Button buttonStart;

    public Button getButtonStop() {
        return buttonStop;
    }

    @FXML
    private Button buttonStop;

    @FXML
    private Button buttonRefresh;

    private final ModelController modelcontroller = new ModelController();

    Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                // 更新操作
                modelcontroller.updateGridData();
                updateGridPane();
            })
    );

    Timeline clearNotUse = new Timeline(
            new KeyFrame(Duration.seconds(40), event -> System.gc())
    );


    @FXML
    public void initialize() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        placeholderGridPane.getChildren().add(0, initGridPane());

        widthText.setText(String.valueOf(modelcontroller.getLie()));
        highText.setText(String.valueOf(modelcontroller.getHang()));
        //clearNotUse.play();
    }

    private GridPane initGridPane() {
        modelcontroller.reset();
        GridPane gridPane = new GridPane();
        for (int i = 0; i < modelcontroller.getLie(); i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth((double) 100 / modelcontroller.getLie());  // 每列占总宽度的 33%
            gridPane.getColumnConstraints().add(colConstraints);
        }
        for (int j = 0; j < modelcontroller.getHang(); j++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight((double) 100 / modelcontroller.getHang());  // 每行占总高度的 33%
            gridPane.getRowConstraints().add(rowConstraints);
        }
        for (int i = 0; i < modelcontroller.getLie(); i++) {
            for (int j = 0; j < modelcontroller.getHang(); j++) {
                Pane pane = getPane(i, j);
                gridPane.add(pane, i, j);
            }
        }
        gridPane.setGridLinesVisible(true);
        childrenGridPane = gridPane;
        return gridPane;
    }


    private Pane getPane(int i, int j) {
        Pane pane = new Pane();
        pane.setBackground(Background.EMPTY);
        if (modelcontroller.getGridData(i, j) == 0) {
            pane.getStyleClass().clear();
           pane.getStyleClass().add(0, styleBlack);
        } else {
            pane.getStyleClass().clear();
            pane.getStyleClass().add(0, styleWhite);
        }

        pane.setOnMouseClicked(mouseEvent -> {

            findIndexAndChange(mouseEvent, 0);
            if (Objects.equals(pane.getStyleClass().get(0), styleBlack)) {
                pane.getStyleClass().clear();
                pane.getStyleClass().add(0, styleWhite);
            } else {
                pane.getStyleClass().clear();
                pane.getStyleClass().add(0, styleBlack);
            }
        });

        pane.setOnMouseMoved(mouseEvent -> {
            if (mouseEvent.isShiftDown()) {
                findIndexAndChange(mouseEvent, 1);
                pane.getStyleClass().clear();
                pane.getStyleClass().add(0, styleWhite);
            }
        });
        return pane;
    }

    private void findIndexAndChange(MouseEvent mouseEvent, int flag) {
        Integer xIndex = (int) ((mouseEvent.getSceneX()) / (placeholderGridPane.getWidth() / modelcontroller.getLie()));
        Integer yIndex = (int) ((mouseEvent.getSceneY()) / (placeholderGridPane.getHeight() / modelcontroller.getHang()));

        modelcontroller.setGridData(xIndex, yIndex, flag);
        //logger.info(modelcontroller.getGridData()[xIndex][yIndex] + "");
//        if (flag == 0) {
//            logger.info("点击X像素位置:" + mouseEvent.getSceneX() + ":" + placeholderGridPane.getWidth());
//            logger.info("点击Y像素位置:" + mouseEvent.getSceneY() + ":" + placeholderGridPane.getHeight());
//            logger.info("点击位置：" + xIndex + "," + yIndex);
//        } else {
//            logger.info("画线位置：" + xIndex + "," + yIndex);
//        }

    }

    public void updateGridPane() {

        ObservableList<Node> children = childrenGridPane.getChildren();
        int i = 0, j = -1;

        for (int index = 0; index < children.size(); index++) {
            j = index % modelcontroller.getHang();
            i = index / modelcontroller.getHang();
            if (!modelcontroller.getChange(i, j)) {
                continue;
            }
            Node node = children.get(index);
            if (node instanceof Pane pane) {
                if (modelcontroller.getGridData(i, j) == 0) {
                    pane.getStyleClass().set(0, styleBlack);
                } else {
                    pane.getStyleClass().set(0, styleWhite);
                }
            }
        }
    }


    public void startClick() {
        buttonStart.setDisable(true);
        buttonStop.setDisable(false);
        buttonRefresh.setDisable(true);
        buttonRunOnce.setDisable(true);
        buttonClear.setDisable(true);
        timeline.play();
    }

    public void stopClick() {
        buttonStop.setDisable(true);
        buttonStart.setDisable(false);
        buttonRefresh.setDisable(false);
        buttonRunOnce.setDisable(false);
        buttonClear.setDisable(false);
        timeline.stop();
    }

    public void runOnceClick() {
        Timeline timeline1 = new Timeline(
                new KeyFrame(Duration.seconds(0.001), event -> {
                    modelcontroller.updateGridData();
                    updateGridPane();
                    logger.info("更新一次");
                })
        );
        timeline1.play();
    }

    public void refreshClick() {
        modelcontroller.reset();
        updateGridPane();
        logger.info("刷新初始界面");
    }

    public void clearClick() {
        logger.info("清零");
        modelcontroller.allClear();
        updateGridPane();
    }

    public void shiftPush(KeyEvent keyEvent) {
        if (keyEvent.isShiftDown()) {
            ifShiftOrNot.setFill(Paint.valueOf("BLACK"));
        }
    }

    public void shiftRelease(KeyEvent keyEvent) {
        if (!keyEvent.isShiftDown()) {
            ifShiftOrNot.setFill(Paint.valueOf("WHITE"));
        }
    }

    public void clickChange() {
        //logger.info(String.valueOf(parseInt(highText.getText())));
        modelcontroller.setHang(parseInt(highText.getText()));
        modelcontroller.setLie(parseInt(widthText.getText()));

        placeholderGridPane.getChildren().clear();
        placeholderGridPane.getChildren().add(0, initGridPane());
        updateGridPane();
    }

    public GridPane getplaceholderGridPane() {
        return placeholderGridPane;
    }
}
