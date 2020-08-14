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

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;

import java.lang.ref.WeakReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("javadoc")
@ExtendWith(MockitoExtension.class)
public class ReferenceOwnedObserverTest {
  @Mock
  Observation upstreamObservation;

  @Mock
  Observer<OwnedMessage<Object, String>> downstreamObserver;

  @Test
  public void weakReferenceTest() {
    assertTimeout(ofSeconds(5), () -> {
      WeakReference<?> reference = new WeakReference<>(new Object());

      while (reference.get() != null) {
        new Object();
        System.gc();
        System.runFinalization();
      }

      assertNull(reference.get());
    });
  }

  private Observer<OwnedMessage<Object, String>> wrapDownstreamObserver() {
    return new Observer<OwnedMessage<Object, String>>() {
      @Override
      public void onObserve(Observation observation) {
        downstreamObserver.onObserve(observation);
      }

      @Override
      public void onNext(OwnedMessage<Object, String> message) {
        String messageContent = message.message();
        downstreamObserver.onNext(new OwnedMessage<Object, String>() {
          @Override
          public Object owner() {
            return null;
          }

          @Override
          public String message() {
            return messageContent;
          }
        });
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
  public void holdWeakOwnedObserverThenMessageTest() {
    Object owner = new Object();

    Observer<OwnedMessage<Object, String>> downstreamObserverWrapper = wrapDownstreamObserver();
    Observer<String> test = ReferenceOwnedObserver.weak(owner, downstreamObserverWrapper);

    test.onObserve(upstreamObservation);
    weakReferenceTest();
    test.onNext("message");

    InOrder inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(upstreamObservation);
    inOrder.verify(downstreamObserver).onNext(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void dropWeakOwnedObserverThenMessageTest() {
    Object owner = new Object();

    Observer<OwnedMessage<Object, String>> downstreamObserverWrapper = wrapDownstreamObserver();
    Observer<String> test = ReferenceOwnedObserver.weak(owner, downstreamObserverWrapper);

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    owner = null;
    weakReferenceTest();
    test.onNext("message2");

    InOrder inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(upstreamObservation);
    inOrder.verify(downstreamObserver).onNext(any());
    inOrder.verify(upstreamObservation).cancel();
    inOrder.verifyNoMoreInteractions();
  }
}
