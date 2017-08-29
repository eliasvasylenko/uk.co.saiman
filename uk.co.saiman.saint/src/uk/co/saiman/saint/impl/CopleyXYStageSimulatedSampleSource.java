/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.saint.
 *
 * uk.co.saiman.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.saint.impl;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.camera.CameraConnection;
import uk.co.saiman.camera.CameraDevice;
import uk.co.saiman.camera.CameraImage;
import uk.co.saiman.camera.CameraResolution;
import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.mathematics.Interval;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.PassthroughObserver;
import uk.co.saiman.saint.simulation.SaintSimulationProperties;
import uk.co.saiman.simulation.instrument.ImageSimulatedSampleSource;
import uk.co.saiman.simulation.instrument.SimulatedSample;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;
import uk.co.saiman.simulation.instrument.SimulatedSampleSource;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class CopleyXYStageSimulatedSampleSource
    implements ImageSimulatedSampleSource, SimulatedSampleSource, CameraDevice {
  private static final int CAMERA_WIDTH = 640;
  private static final int CAMERA_HEIGHT = 480;

  private final CameraResolution CAMERA_RESOLUTION = new CameraResolution() {
    @Override
    public int getWidth() {
      return CAMERA_WIDTH;
    }

    @Override
    public int getHeight() {
      return CAMERA_HEIGHT;
    }

    @Override
    public CameraDevice getCameraDevice() {
      return CopleyXYStageSimulatedSampleSource.this;
    }

    @Override
    public void selectResolution() {}
  };

  private static final SimulatedSampleImage DEFAULT_SAMPLE_IMAGE = new SimulatedSampleImage() {
    protected static final int SIZE = 100;

    @Override
    public int getWidth() {
      return SIZE;
    }

    @Override
    public int getHeight() {
      return SIZE;
    }

    @Override
    public double getRed(int x, int y) {
      return 0;
    }

    @Override
    public double getGreen(int x, int y) {
      if (isOnEdgeOrCross(x, y))
        return 0;
      else
        return x / (double) SIZE;
    }

    private boolean isOnEdgeOrCross(int x, int y) {
      return (x == y || x == SIZE - y) || (x == 0 || y == 0 || x == SIZE - 1 || y == SIZE - 1);
    }

    @Override
    public double getBlue(int x, int y) {
      if (isOnEdgeOrCross(x, y))
        return 0;
      else
        return y / (double) SIZE;
    }
  };

  @Reference
  CopleyComms copleyComms;
  private CopleyController copleyController;

  private ChemicalComposition redChemical;
  private ChemicalComposition greenChemical;
  private ChemicalComposition blueChemical;
  private SimulatedSampleImage sampleImage;

  private Interval<Integer> stageBoundsX;
  private Interval<Integer> stageBoundsY;

  @Reference
  PropertyLoader propertyLoader;
  private SaintSimulationProperties properties;

  private Set<CameraConnection> cameraConnections;
  private HotObservable<CameraImage> imageStream;

  @Activate
  synchronized void activate() {
    copleyController = copleyComms.openController();
    properties = propertyLoader.getProperties(SaintSimulationProperties.class);

    cameraConnections = new HashSet<>();
    imageStream = new HotObservable<>();
    imageStream
        .executeOn(newSingleThreadExecutor())
        .filter(m -> !cameraConnections.isEmpty())
        .observe(o -> {
          try {
            Thread.sleep(50);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          imageStream.next(getImage());
        });

    resetStageBounds();
    resetSampleImage();
  }

  @Override
  public String getName() {
    return properties.copleyXYStageSimuatedSampleSourceName();
  }

  public void resetStageBounds() {
    Interval<Integer> emptyBounds = Interval.bounded(0, 0);
    setStageBounds(emptyBounds, emptyBounds);
  }

  public synchronized void setStageBounds(
      Interval<Integer> stageBoundsX,
      Interval<Integer> stageBoundsY) {
    this.stageBoundsX = stageBoundsX;
    this.stageBoundsY = stageBoundsY;
  }

  @Override
  public synchronized void setRedChemical(ChemicalComposition redChemical) {
    this.redChemical = redChemical;
  }

  @Override
  public synchronized void setGreenChemical(ChemicalComposition greenChemical) {
    this.greenChemical = greenChemical;
  }

  @Override
  public synchronized void setBlueChemical(ChemicalComposition blueChemical) {
    this.blueChemical = blueChemical;
  }

  @Override
  public synchronized void setSampleImage(SimulatedSampleImage sampleImage) {
    this.sampleImage = sampleImage;
  }

  public synchronized void resetSampleImage() {
    this.sampleImage = DEFAULT_SAMPLE_IMAGE;
  }

  private double getSampleAreaPosition(int axis, Interval<Integer> stageBounds) {
    int actualPosition = copleyController.getActualPosition().get(axis).value;

    if (!stageBounds.contains(actualPosition))
      return -1;

    return actualPosition
        / (double) (stageBounds.getRightEndpoint() - stageBounds.getLeftEndpoint());
  }

  private int getImageX() {
    return (int) (sampleImage.getWidth() * getSampleAreaPosition(0, stageBoundsX));
  }

  private int getImageY() {
    return (int) (sampleImage.getHeight() * getSampleAreaPosition(1, stageBoundsY));
  }

  @Override
  public synchronized SimulatedSample getNextSample() {
    int imageX = getImageX();
    int imageY = getImageY();

    Map<ChemicalComposition, Double> sampleChemicals = new HashMap<>();

    if (imageX >= 0 && imageX < sampleImage.getWidth() && imageY >= 0
        && imageY < sampleImage.getHeight()) {
      sampleChemicals.put(redChemical, sampleImage.getRed(imageX, imageY));
      sampleChemicals.put(greenChemical, sampleImage.getGreen(imageX, imageY));
      sampleChemicals.put(blueChemical, sampleImage.getBlue(imageX, imageY));
    }

    return () -> sampleChemicals;
  }

  private CameraImage getImage() {
    return new CameraImage() {
      private final SimulatedSampleImage image = sampleImage;
      private final int imageX = getImageX();
      private final int imageY = getImageY();
      private final boolean tickTock = isEvenSecond();

      @Override
      public int getWidth() {
        return CAMERA_WIDTH;
      }

      @Override
      public int getHeight() {
        return CAMERA_HEIGHT;
      }

      private double getColor(int x, int y, BiFunction<Integer, Integer, Double> color) {
        x = x + imageX;
        y = y + imageY;

        if (x < image.getWidth() && x >= 0 && y < image.getHeight() && y >= 0)
          return color.apply(x, y);
        else
          return 0.5;
      }

      @Override
      public double getRed(int x, int y) {
        if (tickTock && isInCircle(x, y))
          return 1;
        else
          return getColor(x, y, image::getRed);
      }

      private boolean isInCircle(int x, int y) {
        return sqrt(pow(x - (CAMERA_WIDTH / 2), 2) + pow(y - (CAMERA_HEIGHT / 2), 2)) < 25;
      }

      private boolean isEvenSecond() {
        return (System.currentTimeMillis() / 1000) % 2 == 0;
      }

      @Override
      public double getGreen(int x, int y) {
        return getColor(x, y, image::getGreen);
      }

      @Override
      public double getBlue(int x, int y) {
        return getColor(x, y, image::getBlue);
      }
    };
  }

  @Override
  public CameraConnection openConnection() {
    CameraConnection cameraConnection = new CameraConnection() {
      private boolean disposed;

      @Override
      public CameraResolution getResolution() {
        return CAMERA_RESOLUTION;
      }

      @Override
      public Observable<CameraImage> getImageStream() {
        return o -> imageStream.observe(new PassthroughObserver<CameraImage, CameraImage>(o) {
          @Override
          public void onNext(CameraImage message) {
            if (disposed) {
              getObservation().cancel();
              getDownstreamObserver().onComplete();
            } else {
              getDownstreamObserver().onNext(message);
            }
          }
        });
      }

      @Override
      public CameraImage getImage() {
        return CopleyXYStageSimulatedSampleSource.this.getImage();
      }

      @Override
      public CameraDevice getDevice() {
        return CopleyXYStageSimulatedSampleSource.this;
      }

      @Override
      public CameraResolution[] getAvailableResolutions() {
        return new CameraResolution[] { CAMERA_RESOLUTION };
      }

      @Override
      public void dispose() {
        disposed = true;
      }

      @Override
      public boolean isDisposed() {
        return disposed;
      }
    };

    synchronized (cameraConnections) {
      boolean empty = cameraConnections.isEmpty();
      cameraConnections.add(cameraConnection);
      if (empty)
        imageStream.next(getImage());
    }

    return cameraConnection;
  }
}
