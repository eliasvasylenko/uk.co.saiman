package uk.co.saiman.instrument.virtual;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import uk.co.saiman.instrument.Control;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.DeviceStatus;
import uk.co.saiman.observable.Disposable;

public class DeviceDependency<V> {
  private final Device<V> device;
  private final long acquireTimeout;
  private final TimeUnit acquireTimeoutUnit;

  private volatile Control<V> lock;
  private volatile Disposable observation;

  private final Runnable statusUpdated;
  private final Consumer<Control<V>> controllerAcquired;
  private final Runnable controllerReleased;

  public DeviceDependency(
      Device<V> device,
      long time,
      TimeUnit unit,
      Consumer<DeviceDependency<V>> statusUpdated) {
    this.device = device;
    this.acquireTimeout = time;
    this.acquireTimeoutUnit = unit;

    this.statusUpdated = () -> statusUpdated.accept(this);
    this.controllerAcquired = lock -> {};
    this.controllerReleased = () -> {};
  }

  public DeviceDependency(
      Device<V> device,
      long time,
      TimeUnit unit,
      Consumer<DeviceDependency<V>> statusUpdated,
      Consumer<Control<V>> controllerAcquired,
      Runnable controllerReleased) {
    this.device = device;
    this.acquireTimeout = time;
    this.acquireTimeoutUnit = unit;

    this.statusUpdated = () -> statusUpdated.accept(this);
    this.controllerAcquired = controllerAcquired;
    this.controllerReleased = controllerReleased;
  }

  public synchronized void open() {
    if (observation == null) {
      this.observation = device
          .status()
          .value()
          .weakReference(this)
          .observe(status -> status.apply(DeviceDependency::updateStatus));
    }
  }

  public synchronized void close() {
    if (observation != null) {
      observation.cancel();
      observation = null;
      releaseController();
    }
  }

  private synchronized void updateStatus(DeviceStatus status) {
    try {
      switch (status) {
      case AVAILABLE:
        acquireController();
        break;
      case INACCESSIBLE:
        releaseController();
        break;
      case UNAVAILABLE:
        break;
      case DISPOSED:
        close();
        break;
      }
    } finally {
      statusUpdated();
    }
  }

  protected void statusUpdated() {
    statusUpdated.run();
  }

  protected void controllerAcquired(Control<V> lock) {
    controllerAcquired.accept(lock);
  }

  protected void controllerReleased() {
    controllerReleased.run();
  }

  public Device<V> getDevice() {
    return device;
  }

  public synchronized Optional<Control<V>> getLock() {
    return Optional.ofNullable(lock);
  }

  public synchronized Optional<V> getController() {
    return getLock().map(Control::getController);
  }

  private synchronized void acquireController() {
    if (lock == null) {
      try {
        lock = device.acquireControl(acquireTimeout, acquireTimeoutUnit);
      } catch (TimeoutException | InterruptedException e) {
        throw new IllegalStateException(e);
      }
      try {
        controllerAcquired(lock);
      } catch (Exception e) {
        try {
          releaseController();
        } catch (Exception ee) {
          e.addSuppressed(ee);
        }
        throw e;
      }
    }
  }

  private synchronized void releaseController() {
    if (lock != null) {
      try {
        lock.close();
      } catch (Exception e) {
        try {
          lock = null;
          controllerReleased();
        } catch (Exception ee) {
          e.addSuppressed(ee);
        }
        throw e;
      }
      lock = null;
      controllerReleased();
    }
  }
}