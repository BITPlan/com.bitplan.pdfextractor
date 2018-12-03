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

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * extended version of PDFTextStripper that collects locations of text blocks
 * 
 * @author wf
 *
 */
public class PDFTextLocator extends PDFTextStripper {
  private boolean debug = false;

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public Document getDocument() {
    return document;
  }
 
  public void setDocument(Document document) {
    this.document = document;
  }

  private Document document;

  /**
   * construct me
   * 
   * @throws IOException
   */
  public PDFTextLocator() throws IOException {
    super();
  }
  
  @Override 
  public String getText(PDDocument pdDocument) throws IOException {
    setDocument(new Document(pdDocument));
    String text=super.getText(pdDocument);
    getDocument().setText(text);
    return text;
  }

  @Override
  protected void startPage(PDPage page) throws IOException {
    getDocument().add(new Page(page));
    super.startPage(page);
  }

  @Override
  protected void writeLineSeparator() throws IOException {
    getDocument().currentPage().startOfLine = true;
    super.writeLineSeparator();
  }

  @Override
  protected void writeString(String text, List<TextPosition> textPositions)
      throws IOException {
    TextBlock textBlock = new TextBlock(getDocument().currentPage(), text,
        textPositions);
    getDocument().currentPage().getTextBlocks().add(textBlock);
    if (debug)
      if (getDocument().currentPage().startOfLine) {
        writeString(String.format("[^%.0f:%.0f %d]", textBlock.getX(),
            textBlock.getY(), textPositions.size()));
      } else {
        writeString(String.format("[%.0f:%.0f %d]", textBlock.getX(),
            textBlock.getY(), textPositions.size()));
      }

    super.writeString(text, textPositions);
  }

}
