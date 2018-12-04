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
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import com.bitplan.dragtop.DragItem;
import com.bitplan.dragtop.DropHandler;

/**
 * plugin for PDF drag & drop handling
 * 
 * @author wf
 *
 */
public class PDFDropHandlerPlugin extends Plugin {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.mb");

  /**
   * construct me
   * 
   * @param wrapper
   */
  public PDFDropHandlerPlugin(PluginWrapper wrapper) {
    super(wrapper);
  }

  @Extension
  public static class PDFDropHandler implements DropHandler {

    Consumer<DragItem> handler = dragItem -> {
      if (dragItem.getItem() instanceof File) {
        File pdfFile = (File) dragItem.getItem();
        if (pdfFile.getName().endsWith(".pdf")) {
          PdfExtractor extractor = new PdfExtractor();
          try {
            boolean overwrite=true;
            extractor.extract(pdfFile,overwrite);
          } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
          }
        }
      };
    };

    @Override
    public Consumer<DragItem> getHandler() {
      return handler;
    }

    @Override
    public void setHandler(Consumer<DragItem> handler) {
      this.handler = handler;
    }

  }

}
