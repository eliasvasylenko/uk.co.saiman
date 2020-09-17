/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.icons.fugue.
 *
 * uk.co.saiman.icons.fugue is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.icons.fugue is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.icons.fugue;

import static org.osgi.service.url.URLConstants.URL_HANDLER_PROTOCOL;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.url.AbstractURLStreamHandlerService;
import org.osgi.service.url.URLStreamHandlerService;

@Component(service = URLStreamHandlerService.class, property = URL_HANDLER_PROTOCOL + "=fugue")
public class FugueUrlStreamHandler extends AbstractURLStreamHandlerService {
  @Override
  public URLConnection openConnection(URL url) throws IOException {
    return new URLConnection(url) {
      @Override
      public void connect() throws IOException {}

      @Override
      public InputStream getInputStream() throws IOException {
        return FugueUrlStreamHandler.class
            .getResourceAsStream("/uk/co/saiman/icons/fugue/" + url.getPath());
      }

      @Override
      public String getContentType() {
        if (url.getFile().endsWith(".png")) {
          return "image/png";
        } else if (url.getFile().endsWith(".gif")) {
          return "image/gif";
        } else {
          return super.getContentType();
        }
      }
    };
  }
}
