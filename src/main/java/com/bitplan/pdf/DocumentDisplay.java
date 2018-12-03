/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.pdfextractor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.pdf;

import java.util.function.Consumer;
import java.util.logging.Logger;

import com.bitplan.javafx.WaitableApp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class DocumentDisplay extends WaitableApp {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.javafx");
  public static DocumentDisplay instance;
  TabPane tabPane;
  int screenPercent = 80;
  int divX = 5;
  int divY = 5;

  public DocumentDisplay() {
    // LOGGER.log(Level.INFO,"constructor called");
    instance = this;
  }

  @Override
  public void start(Stage primaryStage) {
    super.start(primaryStage);
    primaryStage.setTitle("Displayer");
    tabPane = new TabPane();
    Rectangle2D sceneBounds = super.getSceneBounds(screenPercent, divX, divY);
    primaryStage.setScene(
        new Scene(tabPane, sceneBounds.getWidth(), sceneBounds.getHeight()));
    primaryStage.show();
    stage.setOnCloseRequest(e -> {
      Platform.exit();
      System.exit(0);
    });
  }

  /**
   * launch the given Application
   * 
   * @param args
   * @param wait
   * @throws Exception
   */
  public static void launch(String args[], int wait,
      Consumer<DocumentDisplay> callBack) throws Exception {
    new Thread(() -> Application.launch(DocumentDisplay.class, args)).start();
    System.out.println("application started");
    while (DocumentDisplay.instance == null)
      Thread.sleep(10);
    DocumentDisplay mainApp = DocumentDisplay.instance;
    mainApp.waitOpen();
    if (debug)
      System.out.println("application open");
    Platform.runLater(() -> {
      callBack.accept(mainApp);
    });
    Thread.sleep(wait);
    if (debug)
      System.out.println("application to be closed");
    Platform.runLater(() -> mainApp.stage.close());
    if (debug)
      System.out.println("waiting for close");
    mainApp.waitClose();
    if (debug)
      System.out.println("closed");
  }

  /**
   * display the given document
   * 
   * @param doc
   * @param limit 
   * @throws Exception
   */
  public static void display(Document doc, int limit) throws Exception {
    String args[] = {};
    DocumentDisplay.launch(args, 50000, app -> {
      int pageIndex = 0;
      for (Page page : doc.getPages()) {
        Tab tab = new Tab();
        tab.setText(String.format("#%3d", ++pageIndex));
        app.tabPane.getTabs().add(tab);
        Pane pane = new Pane();

        tab.setContent(pane);
        for (TextBlock textBlock : page.getTextBlocks()) {
          TextField textField = new TextField();
          textField.setEditable(false);
          String style = "-fx-background-color: transparent;";
          if (textBlock.fsr != null) {
            for (ExtractedResult fuzzy : textBlock.fsr)
              if (fuzzy.getScore() >=limit) {
                style += "-fx-text-fill: blue;";
                Tooltip tooltip = new Tooltip();
                tooltip.setText(String.format("%s: %3d%%", fuzzy.getString(),
                    fuzzy.getScore()));
                textField.setTooltip(tooltip);
              }

          }
          textField.setStyle(style);
          textField.textProperty().addListener((ov, prevText, currText) -> {
            // https://stackoverflow.com/a/25643696/1497139
            Text fieldtext = new Text(textBlock.getText());
            fieldtext.setFont(textField.getFont()); // Set the same font, so the
                                                    // size is the same
            double width = fieldtext.getLayoutBounds().getWidth() // This big is
                                                                  // the Text in
                                                                  // the
                                                                  // TextField
                + textField.getPadding().getLeft()
                + textField.getPadding().getRight() // Add the padding of the
                                                    // TextField
                + 16d; // Add some spacing
            textField.setPrefWidth(width); // Set the width
            textField.positionCaret(textField.getCaretPosition()); // If you
                                                                   // remove
                                                                   // this line,
                                                                   // it flashes
                                                                   // a little
                                                                   // bit
          });
          textField.setText(textBlock.getText());
          pane.getChildren().add(textField);
          textField.setLayoutX(textBlock.getX());
          textField.setLayoutY(textBlock.getY());
        }
      }
    });
  }

}
