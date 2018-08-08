package uk.co.saiman.eclipse.ui.fx.impl;

import static java.nio.channels.Channels.newChannel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;

import javafx.scene.input.Clipboard;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.eclipse.ui.fx.ClipboardCache;
import uk.co.saiman.eclipse.ui.fx.ClipboardService;
import uk.co.saiman.eclipse.ui.fx.MediaTypes;

@Creatable
public class ClipboardServiceImpl implements ClipboardService {
  public class ClipboardCacheContainer {
    private final Map<DataFormat<? extends Object>, Optional<? extends Object>> objects;

    public ClipboardCacheContainer() {
      this.objects = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> Optional<T> getData(Clipboard clipboard, DataFormat<T> format) {
      return (Optional<T>) objects.computeIfAbsent(format, f -> loadData(clipboard, format));
    }

    public <T> Optional<T> loadData(Clipboard clipboard, DataFormat<T> format) {
      return format
          .getMediaTypes()
          .map(MediaTypes::toDataFormat)
          .map(dataFormat -> clipboard.getContent(dataFormat))
          .filter(byte[].class::isInstance)
          .findFirst()
          .flatMap(data -> {
            try {
              return Optional
                  .of(format.load(newChannel(new ByteArrayInputStream((byte[]) data))).data);
            } catch (IOException e) {
              return Optional.empty();
            }
          });
    }
  }

  private final Map<SoftReference<Clipboard>, ClipboardCacheContainer> caches;

  @Inject
  public ClipboardServiceImpl() {
    caches = new HashMap<>();
  }

  @Override
  public synchronized ClipboardCache getCache(Clipboard clipboard) {
    ClipboardCacheContainer cacheContainer = null;

    for (Iterator<Entry<SoftReference<Clipboard>, ClipboardCacheContainer>> clipboards = caches
        .entrySet()
        .iterator(); clipboards.hasNext();) {
      Entry<SoftReference<Clipboard>, ClipboardCacheContainer> entry = clipboards.next();

      Clipboard cachedClipboard = entry.getKey().get();
      if (cachedClipboard == null) {
        clipboards.remove();
      } else if (cachedClipboard == clipboard) {
        cacheContainer = entry.getValue();
      }
    }

    if (cacheContainer == null) {
      caches.put(new SoftReference<>(clipboard), cacheContainer = new ClipboardCacheContainer());
    }

    return getCache(clipboard, cacheContainer);
  }

  private ClipboardCache getCache(Clipboard clipboard, ClipboardCacheContainer cacheContainer) {
    return new ClipboardCache() {
      @Override
      public <T> Optional<T> getData(DataFormat<T> format) {
        return cacheContainer.getData(clipboard, format);
      }
    };
  }
}
