package uk.co.saiman.comms.copley.impl;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.valueOf;
import static uk.co.saiman.comms.copley.rest.CopleyRestConstants.MEDIA_TYPE;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.InterceptorContext;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsExtension;

@JaxrsExtension
@Component
public class CopleyRestJsonProvider<T> implements WriterInterceptor, ReaderInterceptor {
  @Override
  public void aroundWriteTo(WriterInterceptorContext context)
      throws IOException, WebApplicationException {
    intercept(context);
    context.proceed();
  }

  @Override
  public Object aroundReadFrom(ReaderInterceptorContext context)
      throws IOException, WebApplicationException {
    intercept(context);
    return context.proceed();
  }

  private void intercept(InterceptorContext context) {
    if (context.getMediaType().equals(MEDIA_TYPE)) {
      context.setMediaType(valueOf(APPLICATION_JSON));
    }
  }
}
