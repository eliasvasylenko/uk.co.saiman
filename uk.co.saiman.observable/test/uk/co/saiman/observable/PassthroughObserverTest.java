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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("javadoc")
@ExtendWith(MockitoExtension.class)
public class PassthroughObserverTest {
  @Mock
  Observation upstreamObservation;

  @Mock
  Observer<String> downstreamObserver;

  protected PassthroughObserver<String, String> createDefaultObserver(
      Observer<String> downstreamObserver) {
    return new PassthroughObserver<String, String>(downstreamObserver) {
      @Override
      public void onNext(String message) {
        getDownstreamObserver().onNext(message);
      }
    };
  }

  @Test
  public void useObserverOnceTest() {
    Observer<String> test = createDefaultObserver(downstreamObserver);

    test.onObserve(upstreamObservation);

    InOrder inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(upstreamObservation);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void passSomeMessageEventTest() {
    Observer<String> test = createDefaultObserver(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    InOrder inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("message");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void passSomeMessagesEventTest() {
    Observer<String> test = createDefaultObserver(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    test.onNext("message2");
    test.onNext("message3");

    InOrder inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("message1");
    inOrder.verify(downstreamObserver).onNext("message2");
    inOrder.verify(downstreamObserver).onNext("message3");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void passCompleteEventTest() {
    Observer<String> test = createDefaultObserver(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onComplete();

    InOrder inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onComplete();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void passFailEventTest() {
    Observer<String> test = createDefaultObserver(downstreamObserver);

    Throwable t = new Throwable();

    test.onObserve(upstreamObservation);
    test.onFail(t);

    InOrder inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onFail(t);
    inOrder.verifyNoMoreInteractions();
  }
}
