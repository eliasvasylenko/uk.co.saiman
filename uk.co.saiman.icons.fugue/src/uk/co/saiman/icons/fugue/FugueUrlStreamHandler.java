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
        return FugueUrlStreamHandler.class.getResourceAsStream("/" + url.getPath());
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
