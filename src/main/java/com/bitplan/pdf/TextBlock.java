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

import java.util.List;

import org.apache.pdfbox.text.TextPosition;

import me.xdrop.fuzzywuzzy.model.ExtractedResult;

/**
 * TextBlock wrapper
 * 
 * @author wf
 */
public class TextBlock {
  double x;
  double y;
  String text;
  
  transient List<TextPosition> textPositions;
  transient private TextPosition firstPosition;
  transient private Page page;
  transient public List<ExtractedResult> fsr;


  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  /**
   * create a text block
   * 
   * @param page
   * @param text
   * @param textPositions
   */
  public TextBlock(Page page, String text, List<TextPosition> textPositions) {
    this.page = page;
    if (this.page.startOfLine) {
      this.page.startOfLine = false;
    }
    this.setText(text);
    this.textPositions = textPositions;
    firstPosition = textPositions.get(0);
    x=getX();
    y=getY();
  }

  public double getX() {
    return firstPosition.getXDirAdj();
  }

  public double getY() {
    return firstPosition.getY();
  }
}