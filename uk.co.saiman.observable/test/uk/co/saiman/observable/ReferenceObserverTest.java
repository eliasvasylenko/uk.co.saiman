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

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.inOrder;

import java.lang.ref.WeakReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("javadoc")
@ExtendWith(MockitoExtension.class)
public class ReferenceObserverTest {
  @Mock
  Observation upstreamObservation;

  @Mock
  Observer<String> downstreamObserver;

  @Test
  public void weakReferenceTest() {
    Assertions.assertTimeout(ofMillis(5000), () -> {
      WeakReference<?> reference = new WeakReference<>(new Object());

      while (reference.get() != null) {
        new Object();
        System.gc();
        System.runFinalization();
      }

      assertNull(reference.get());
    });
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

  @Test
  public void holdWeakObserverThenMessageTest() {
    Observer<String> downstreamObserverWrapper = wrapDownstreamObserver();
    Observer<String> test = ReferenceObserver.weak(downstreamObserverWrapper);

    test.onObserve(upstreamObservation);
    weakReferenceTest();
    test.onNext("message");

    var inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(upstreamObservation);
    inOrder.verify(downstreamObserver).onNext("message");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void dropWeakObserverThenMessageTest() {
    Observer<String> downstreamObserverWrapper = wrapDownstreamObserver();
    Observer<String> test = ReferenceObserver.weak(downstreamObserverWrapper);

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    downstreamObserverWrapper = null;
    weakReferenceTest();
    test.onNext("message2");

    var inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(upstreamObservation);
    inOrder.verify(downstreamObserver).onNext("message1");
    inOrder.verify(upstreamObservation).cancel();
    inOrder.verifyNoMoreInteractions();
  }
}
