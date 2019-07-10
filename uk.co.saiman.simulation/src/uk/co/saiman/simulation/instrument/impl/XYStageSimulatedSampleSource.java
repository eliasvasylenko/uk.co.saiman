/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.instrument.impl;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.measurement.Quantities.quantityFormat;
import static uk.co.saiman.measurement.Units.count;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.camera.CameraConnection;
import uk.co.saiman.camera.CameraDevice;
import uk.co.saiman.camera.CameraImage;
import uk.co.saiman.camera.CameraResolution;
import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.PassthroughObserver;
import uk.co.saiman.simulation.instrument.ImageSimulatedSampleSource;
import uk.co.saiman.simulation.instrument.SimulatedSample;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;
import uk.co.saiman.simulation.instrument.SimulatedSampleSource;
import uk.co.saiman.simulation.instrument.impl.XYStageSimulatedSampleSource.XYStageSimulatedSampleSourceConfiguration;

/**
 * A software component to attach to a {@link XYStage two dimensional, linear
 * stage} and {@link SimulatedSampleSource simulate a sample source} using the
 * position of the device to index into a sample image. The stage device itself
 * may or may not be a simulation.
 * <p>
 * The component also simulates a camera view of the stage.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = XYStageSimulatedSampleSourceConfiguration.class, factory = true)
@Component(name = XYStageSimulatedSampleSource.CONFIGURATION_PID, configurationPid = XYStageSimulatedSampleSource.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class XYStageSimulatedSampleSource
    implements ImageSimulatedSampleSource, SimulatedSampleSource, CameraDevice {
  static final String CONFIGURATION_PID = "uk.co.saiman.simulation.samplesource.xystage";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(id = CONFIGURATION_PID, name = "XY Stage Simulated Sample Source Configuration", description = "The configuration for a sample simulation over an two dimensional, linear stage")
  public @interface XYStageSimulatedSampleSourceConfiguration {
    @AttributeDefinition(name = "Stage Device", description = "The OSGi reference filter for the stage")
    String stageDevice_target() default "(objectClass=uk.co.saiman.instrument.stage.XYStage)";

    @AttributeDefinition(name = "Horizontal camera resolution", description = "The horizontal resolution of the simulated camera feed of the sample stage in pixels")
    int cameraResolutionWidth() default 320;

    @AttributeDefinition(name = "Vertical camera resolution", description = "The vertical resolution of the simulated camera feed of the sample stage in pixels")
    int cameraResolutionHeight() default 240;

    @AttributeDefinition(name = "View area width", description = "The width of the visible area of the plate")
    String viewAreaWidth() default "10mm";
  }

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
      return (x == y || x == SIZE - 1 - y) || (x == 0 || y == 0 || x == SIZE - 1 || y == SIZE - 1);
    }

    @Override
    public double getBlue(int x, int y) {
      if (isOnEdgeOrCross(x, y))
        return 0;
      else
        return y / (double) SIZE;
    }
  };

  private final XYStage<?> stageDevice;

  private final Set<CameraConnection> cameraConnections;
  private final HotObservable<CameraImage> imageStream;

  private CameraResolution cameraResolution;
  private double cameraImageZoomX;
  private double cameraImageZoomY;

  private ChemicalComposition redChemical;
  private ChemicalComposition greenChemical;
  private ChemicalComposition blueChemical;
  private SimulatedSampleImage sampleImage;

  @Activate
  public XYStageSimulatedSampleSource(
      @Reference XYStage<?> stageDevice,
      XYStageSimulatedSampleSourceConfiguration configuration) {
    this.stageDevice = stageDevice;
    this.cameraConnections = new HashSet<>();

    this.imageStream = new HotObservable<>();
    this.imageStream
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

    resetSampleImage();

    modify(configuration);
  }

  @Modified
  synchronized void modify(XYStageSimulatedSampleSourceConfiguration configuration) {
    cameraResolution = new CameraResolution() {
      @Override
      public int getWidth() {
        return configuration.cameraResolutionWidth();
      }

      @Override
      public int getHeight() {
        return configuration.cameraResolutionHeight();
      }

      @Override
      public CameraDevice getCameraDevice() {
        return XYStageSimulatedSampleSource.this;
      }

      @Override
      public void selectResolution() {}
    };

    Quantity<Length> sampleImagePixelWidth = stageDevice
        .getUpperBound()
        .getX()
        .subtract(stageDevice.getLowerBound().getX())
        .divide(sampleImage.getWidth());

    Quantity<Length> sampleImagePixelHeight = stageDevice
        .getUpperBound()
        .getY()
        .subtract(stageDevice.getLowerBound().getY())
        .divide(sampleImage.getHeight());

    Quantity<Length> viewAreaWidth = quantityFormat()
        .parse(configuration.viewAreaWidth())
        .asType(Length.class);
    cameraImageZoomX = viewAreaWidth
        .divide(sampleImagePixelWidth)
        .divide(cameraResolution.getWidth())
        .asType(Dimensionless.class)
        .to(count().getUnit())
        .getValue()
        .doubleValue();

    Quantity<Length> viewAreaHeight = viewAreaWidth
        .divide(cameraResolution.getWidth())
        .multiply(cameraResolution.getHeight());
    cameraImageZoomY = viewAreaHeight
        .divide(sampleImagePixelHeight)
        .divide(cameraResolution.getHeight())
        .asType(Dimensionless.class)
        .to(count().getUnit())
        .getValue()
        .doubleValue();
  }

  @Override
  public String getName() {
    return "Simulated Camera";
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

  private double getSampleAreaPosition(
      Function<XYCoordinate<Length>, Quantity<Length>> stageDimension) {
    Quantity<Length> range = stageDimension
        .apply(stageDevice.getUpperBound())
        .subtract(stageDimension.apply(stageDevice.getLowerBound()));

    Quantity<Length> offset = stageDimension
        .apply(stageDevice.samplePosition().get())
        .subtract(stageDimension.apply(stageDevice.getLowerBound()));

    return offset
        .divide(range)
        .asType(Dimensionless.class)
        .to(count().getUnit())
        .getValue()
        .doubleValue();
  }

  int getImageX() {
    return (int) (sampleImage.getWidth() * getSampleAreaPosition(XYCoordinate::getX));
  }

  int getImageY() {
    return (int) (sampleImage.getHeight() * getSampleAreaPosition(XYCoordinate::getY));
  }

  @Override
  public synchronized SimulatedSample getNextSample() {
    int imageX = getImageX();
    int imageY = getImageY();

    Map<ChemicalComposition, Double> sampleChemicals = new HashMap<>();

    if (imageX >= 0
        && imageX < sampleImage.getWidth()
        && imageY >= 0
        && imageY < sampleImage.getHeight()) {
      sampleChemicals.put(redChemical, sampleImage.getRed(imageX, imageY));
      sampleChemicals.put(greenChemical, sampleImage.getGreen(imageX, imageY));
      sampleChemicals.put(blueChemical, sampleImage.getBlue(imageX, imageY));
    }

    return () -> sampleChemicals;
  }

  private CameraImage getImage() {
    return new SimulatedCameraImage(
        sampleImage,
        cameraResolution,
        getImageX(),
        getImageY(),
        cameraImageZoomX,
        cameraImageZoomY);
  }

  @Override
  public CameraConnection openConnection() {
    CameraConnection cameraConnection = new CameraConnection() {
      private boolean disposed;

      @Override
      public CameraResolution getResolution() {
        return cameraResolution;
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
        return XYStageSimulatedSampleSource.this.getImage();
      }

      @Override
      public CameraDevice getDevice() {
        return XYStageSimulatedSampleSource.this;
      }

      @Override
      public CameraResolution[] getAvailableResolutions() {
        return new CameraResolution[] { cameraResolution };
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
