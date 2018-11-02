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
 * This file is part of uk.co.saiman.observable.
 *
 * uk.co.saiman.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.observable;

import static org.junit.Assert.assertNull;

import java.lang.ref.WeakReference;

import org.junit.Test;

import mockit.FullVerifications;
import mockit.Injectable;
import mockit.VerificationsInOrder;

@SuppressWarnings("javadoc")
public class ReferenceObserverTest {
  @Injectable
  Observation upstreamObservation;

  @Injectable
  Observer<String> downstreamObserver;

  @Test(timeout = 5000)
  public void weakReferenceTest() {
    WeakReference<?> reference = new WeakReference<>(new Object());

    while (reference.get() != null) {
      new Object();
      System.gc();
      System.runFinalization();
    }

    assertNull(reference.get());
  }

  private Observer<String> wrapDownstreamObserver() {
    return new Observer<String>() {
      @Override
      public void onObserve(Observation observation) {
        downstreamObserver.onObserve(observation);
      }

      @Override
      public void onNext(String message) {
        downstreamObserver.onNext(message);
      }

      @Override
      public void onComplete() {
        downstreamObserver.onComplete();
      }

      @Override
      public void onFail(Throwable t) {
        downstreamObserver.onFail(t);
      }
    };
  }

  @Test(timeout = 5000)
  public void holdWeakObserverThenMessageTest() {
    Observer<String> downstreamObserverWrapper = wrapDownstreamObserver();
    Observer<String> test = ReferenceObserver.weak(downstreamObserverWrapper);

    test.onObserve(upstreamObservation);
    weakReferenceTest();
    test.onNext("message");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve(upstreamObservation);
        downstreamObserver.onNext("message");
      }
    };
    new FullVerifications() {};
  }

  @Test(timeout = 5000)
  public void dropWeakObserverThenMessageTest() {
    Observer<String> downstreamObserverWrapper = wrapDownstreamObserver();
    Observer<String> test = ReferenceObserver.weak(downstreamObserverWrapper);

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    downstreamObserverWrapper = null;
    weakReferenceTest();
    test.onNext("message2");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve(upstreamObservation);
        downstreamObserver.onNext("message1");
        upstreamObservation.cancel();
      }
    };
    new FullVerifications() {};
  }
}
