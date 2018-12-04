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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;

import com.bitplan.dragtop.DropHandler;
import com.bitplan.pdf.Document;
import com.bitplan.pdf.DocumentDisplay;
import com.bitplan.pdf.PDFDropHandlerPlugin;
import com.bitplan.pdf.PDFTextLocator;
import com.bitplan.pdf.PdfExtractor;
import com.bitplan.pdf.TextBlock;

/**
 * test the extractor
 * 
 * @author wf
 *
 */
public class TestExtractor {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.pdfextractor");
  private static final String US_CONSTITUTION_URL = "https://www.usconstitution.net/const.pdf";;
  public boolean debug = false;

  @Test
  public void testPlugin() {
    // test assignability at compile time
    DropHandler handler = new PDFDropHandlerPlugin.PDFDropHandler();
    // test assignability at run time
    Class<? extends DropHandler> pluginType = handler.getClass();
    Class<DropHandler> type = DropHandler.class;
    assertTrue(type.isAssignableFrom(pluginType));
  }

  @Test
  public void testPDFExtract() throws Exception {
    InputStream pdfStream = new URL(US_CONSTITUTION_URL).openStream();
    PDDocument document = PDDocument.load(pdfStream);
    // https://stackoverflow.com/questions/32978179/using-pdfbox-to-get-location-of-line-of-text
    PDFTextLocator locator = new PDFTextLocator(US_CONSTITUTION_URL);
    locator.setDebug(debug);
    String text = locator.getText(document);
    assertEquals(locator.isDebug() ? 56150 : 48548, text.length());
    assertTrue(text.contains("Congress"));
    if (debug) {
      System.out.println(text);
    }

    Document doc = locator.getDocument();
    int limit = 55;
    List<TextBlock> hits = doc.fuzzySearch(limit, "Tax", "Congress",
        "Amendment");
    if (debug) {
      DocumentDisplay.display(doc, limit);
    }
    assertEquals(146, hits.size());
  }

  @Test
  public void testPDFExtractor() throws Exception {
    PdfExtractor extractor = new PdfExtractor();
    File constitutionPdf = File.createTempFile("USconstitution", ".pdf");
    FileUtils.copyURLToFile(new URL(US_CONSTITUTION_URL), constitutionPdf);
    boolean overwrite=true;
    extractor.extract(constitutionPdf, overwrite);
    File jsonFile=new File(constitutionPdf.getAbsolutePath().replace(".pdf", ".json"));
    assertTrue(jsonFile.exists());
    String json=FileUtils.readFileToString(jsonFile, "UTF-8");
    if (debug)
      LOGGER.log(Level.INFO, json);
    assertTrue(json.contains("{\"x\":"));
  }

}
