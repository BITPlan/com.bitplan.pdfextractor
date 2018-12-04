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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import com.bitplan.javafx.Main;

/**
 * extract pdf text
 * 
 * @author wf
 *
 */
public class PdfExtractor extends Main {
  @Option(name = "-f", aliases = {
      "--overwrite" }, usage = "overwrite\noverwrite existing files")
  protected boolean overwrite = false;

  @Argument
  private List<String> arguments = new ArrayList<String>();

  @Override
  public String getSupportEMail() {
    return "support@bitplan.com";
  }

  @Override
  public String getSupportEMailPreamble() {
    return "Dear PdfExtractor user";
  }

  @Override
  public void work() throws Exception {
    if (showVersion)
      this.showVersion();
    if (showHelp)
      this.showHelp();
    if (this.arguments.size() > 0) {
      for (String pdfFileName : arguments) {
        File pdfFile = new File(pdfFileName);
        extract(pdfFile,overwrite);
      }
    }
  }
  
  /**
   * extract the text for the given pdfFile
   * @param pdfFile
   * @throws Exception
   * @return the text file
   */
  public List<File> extract(File pdfFile, boolean pOverwrite) throws Exception {
    List<File> files=new ArrayList<File>();
    PDDocument document = PDDocument.load(pdfFile);
    PDFTextLocator locator = new PDFTextLocator(pdfFile.toURI().toString());
    locator.setDebug(debug);
    String text = locator.getText(document);
    // create a text file
    String textPath = pdfFile.getAbsolutePath().replace(".pdf", ".txt");
    File textFile = new File(textPath);
    if (!textFile.exists() || pOverwrite) {
      FileUtils.writeStringToFile(textFile, text,"UTF-8");
      files.add(textFile);
    }
    String jsonPath=pdfFile.getAbsolutePath().replace(".pdf", ".json");
    File jsonFile=new File(jsonPath);
    if (!jsonFile.exists() || pOverwrite) {
      String json=locator.getDocument().asJson();
      FileUtils.writeStringToFile(jsonFile, json,"UTF-8");
      files.add(jsonFile);
    }
    return files;
  }

  /**
   * main routine
   * 
   * @param args
   */
  public static void main(String[] args) {
    PdfExtractor extractor = new PdfExtractor();
    extractor.maininstance(args);
    if (!testMode)
      System.exit(exitCode);
  }

}
