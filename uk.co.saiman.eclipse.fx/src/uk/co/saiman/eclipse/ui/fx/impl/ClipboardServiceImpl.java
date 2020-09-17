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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx.impl;

import static java.nio.channels.Channels.newChannel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.channels.Channels;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
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

  @Override
  public <T> Optional<T> getValue(Clipboard clipboard, DataFormat<T> format) {
    return getCache(clipboard).getData(format).or(() -> deserialize(clipboard, format));
  }

  private <T> Optional<T> deserialize(Clipboard clipboard, DataFormat<T> format) {
    return format
        .getMediaTypes()
        .map(MediaTypes::toDataFormat)
        .distinct()
        .map(clipboard::getContent)
        .filter(Objects::nonNull)
        .flatMap(data -> {
          if (data instanceof String && format instanceof TextFormat<?>) {
            var value = ((TextFormat<T>) format).decodeString((String) data);
            return Stream.of(value);
          }

          if (data instanceof byte[]) {
            ByteArrayInputStream bytes = new ByteArrayInputStream((byte[]) data);
            try {
              var value = format.load(Channels.newChannel(bytes));
              return Stream.of(value);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
          return Stream.empty();
        })
        .findFirst()
        .map(p -> p.data);
  }

  @Override
  public <T> void putValue(ClipboardContent clipboardContent, DataFormat<T> format, T value) {
    Object data;
    Payload<T> payload = new Payload<>(value);
    if (format instanceof TextFormat<?>) {
      data = ((TextFormat<T>) format).encodeString(payload);

    } else {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      try {
        format.save(Channels.newChannel(bytes), payload);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      data = bytes.toByteArray();
    }
    format
        .getMediaTypes()
        .map(MediaTypes::toDataFormat)
        .distinct()
        .forEach(f -> clipboardContent.put(f, data));
  }
}
