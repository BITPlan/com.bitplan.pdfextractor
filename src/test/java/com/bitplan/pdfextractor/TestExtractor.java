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
package com.bitplan.pdfextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;

import com.bitplan.pdf.Document;
import com.bitplan.pdf.DocumentDisplay;
import com.bitplan.pdf.PDFTextLocator;
import com.bitplan.pdf.TextBlock;

/**
 * test the extractor
 * 
 * @author wf
 *
 */
public class TestExtractor {

  public boolean debug = true;

  @Test
  public void testPDFExtract() throws Exception {
    InputStream pdfStream = new URL("https://www.usconstitution.net/const.pdf")
        .openStream();
    PDDocument document = PDDocument.load(pdfStream);
    // https://stackoverflow.com/questions/32978179/using-pdfbox-to-get-location-of-line-of-text
    PDFTextLocator locator = new PDFTextLocator();
    locator.setDebug(debug);
    String text = locator.getText(document);
    assertEquals(locator.isDebug() ? 56150 : 48548, text.length());
    assertTrue(text.contains("Congress"));
    if (debug) {
      System.out.println(text);
    }

    Document doc = locator.getDocument();
    int limit=55;
    List<TextBlock> hits = doc.fuzzySearch(limit,"Tax", "Congress", "Amendment");
    if (debug) {
      DocumentDisplay.display(doc,limit);
    }
    assertEquals(146,hits.size());
  }

}
