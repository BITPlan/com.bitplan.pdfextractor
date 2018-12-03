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

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;

/**
 * Page wrapper
 * 
 * @author wf
 *
 */
public class Page {
  PDPage pdPage;
  Document document;
  
  boolean startOfLine = true;
  private List<TextBlock> textBlocks = new ArrayList<TextBlock>();

  public List<TextBlock> getTextBlocks() {
    return textBlocks;
  }

  public void setTextBlocks(List<TextBlock> textBlocks) {
    this.textBlocks = textBlocks;
  }

  public Page(PDPage pdPage) {
    this.pdPage = pdPage;
  }

  public void setDocument(Document document) {
    this.document=document;
  }
}