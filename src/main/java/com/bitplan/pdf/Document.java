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
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

/**
 * A Document
 * wraps a PDF Box PDDocument and makes text and textBlocks accessible
 * @author wf
 *
 */
public class Document {
  String url;
  private List<Page> pages = new ArrayList<Page>();
  
  private String text;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  /**
   * get the current Page
   * 
   * @return the current page
   */
  public Page currentPage() {
    if (getPages().size() == 0)
      return null;
    return getPages().get(getPages().size() - 1);
  }

  public List<Page> getPages() {
    return pages;
  }

  public void setPages(List<Page> pages) {
    this.pages = pages;
  }

  /**
   * add the given page
   * @param page
   */
  public void add(Page page) {
    pages.add(page);
    page.setDocument(this);
    page.setPageNo(pages.size());
  }

  /**
   * search for the given texts
   * @param limit - the limit for the result
   * @param texts
   * @return - the textBlocks that have a hit
   */
  public List<TextBlock> fuzzySearch(int limit,String... texts) {
    List<TextBlock> hits=new ArrayList<TextBlock>();
    List<String> choices = Arrays.asList(texts);
    for (Page page : pages) {
      for (TextBlock textBlock : page.getTextBlocks()) {
        List<ExtractedResult> fsr = FuzzySearch.extractAll(textBlock.getText(),
            choices);
        if (fsr.size() > 0) {
          textBlock.fsr = fsr;
          for (ExtractedResult fuzzy : fsr)
            if (fuzzy.getScore() >= limit) {
              hits.add(textBlock);
              continue;
            }
        }
      }
    }
    return hits;
  }

  /**
   * get my json representation
   * @return - the json representation
   */
  public String asJson() {
    Gson gson=new Gson();
    String json=gson.toJson(this);
    return json;
  }

}
