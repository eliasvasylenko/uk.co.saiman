/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.comms.provider.
 *
 * uk.co.saiman.comms.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.CommsChannel;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.CommsStream;
import uk.co.saiman.comms.impl.DumpPortService.DumpCommsPortConfiguration;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.Observer;

@Designate(ocd = DumpCommsPortConfiguration.class, factory = true)
@Component(configurationPid = DumpPortService.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class DumpPortService implements CommsPort {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Dump Port Configuration",
      description = "A simple simulation of a serial port which discards all data put in and puts no data out")
  public @interface DumpCommsPortConfiguration {
    @AttributeDefinition(name = "Port Name", description = "The name of the port to provide")
    String name();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.comms.simulation.dump";

  private String name;
  private boolean open;

  @Activate
  void activate(DumpCommsPortConfiguration configuration) {
    this.name = configuration.name();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public void kill() {
    open = false;
  }

  @Override
  public CommsChannel openChannel() {
    if (isOpen()) {
      throw new CommsException("Port already in use " + this);
    }

    return new CommsChannel() {
      private boolean disposed;

      private void checkState() {
        if (disposed)
          throw new CommsException("Port is disposed");
      }

      @Override
      public Disposable observe(Observer<? super CommsChannel> observer) {
        checkState();
        return () -> {};
      }

      @Override
      public int write(ByteBuffer src) throws IOException {
        checkState();
        return src.remaining();
      }

      @Override
      public boolean isOpen() {
        return !disposed;
      }

      @Override
      public int read(ByteBuffer dst) throws IOException {
        checkState();
        return 0;
      }

      @Override
      public void close() {
        disposed = true;
        open = false;
      }

      @Override
      public int bytesAvailable() {
        return 0;
      }
    };
  }

  @Override
  public CommsStream openStream(int packetSize) {
    CommsChannel channel = openChannel();
    return new CommsStream() {
      @Override
      public boolean isOpen() {
        return channel.isOpen();
      }

      @Override
      public int write(ByteBuffer src) throws IOException {
        return channel.write(src);
      }

      @Override
      public Disposable observe(Observer<? super ByteBuffer> observer) {
        return channel.map(o -> (ByteBuffer) null).observe(observer);
      }

      @Override
      public void close() throws IOException {
        channel.close();
      }
    };
  }
}