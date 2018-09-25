package uk.co.saiman.icons;

import static org.osgi.service.url.URLConstants.URL_CONTENT_MIMETYPE;

import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;
import java.net.URLConnection;

import org.osgi.service.component.annotations.Component;

@Component(service = ContentHandler.class, property = URL_CONTENT_MIMETYPE + "=image/png")
public class PngContentHandler extends ContentHandler {
  @Override
  public Icon getContent(URLConnection connection) throws IOException {
    try (InputStream input = connection.getInputStream()) {
      return null;
    }
  }
}
