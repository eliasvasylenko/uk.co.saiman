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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("javadoc")
@ExtendWith(MockitoExtension.class)
public class SafeObserverTest {
  interface MockObserver<T> extends Observer<T> {}

  interface MockObservation extends Observation {}

  @Mock
  MockObservation upstreamObservation;

  @Mock
  MockObserver<String> downstreamObserver;

  @Test
  public void sendMessageAfterCancelTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    test.getObservation().cancel();
    test.onNext("message");

    var inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(upstreamObservation).cancel();
    inOrder.verify(downstreamObserver, times(0)).onNext(any());
  }

  @Test
  public void sendMessageAfterCompleteTest() {
    Observer<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    test.onComplete();
    test.onNext("message");

    var inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onComplete();
    inOrder.verify(downstreamObserver, times(0)).onNext(any());
  }

  @Test
  public void sendMessageBeforeObserveTest() {
    Observer<String> test = new SafeObserver<>(downstreamObserver);
    test.onNext("message");

    inOrder(upstreamObservation, downstreamObserver).verifyNoMoreInteractions();
  }

  @Test
  public void throwFromOnObserveTest() {
    Throwable throwable = new RuntimeException();

    doThrow(throwable).when(downstreamObserver).onObserve(any());

    Observer<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);

    var inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onFail(throwable);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void throwFromOnNextTest() {
    Throwable throwable = new RuntimeException();

    doThrow(throwable).when(downstreamObserver).onNext(any());

    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();
    test.onNext("message");

    var inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("message");
    inOrder.verify(downstreamObserver).onFail(throwable);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void sendMessageWithRequestTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();
    test.onNext("message");

    var inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("message");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void sendMessageWithoutRequestTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    test.onNext("message");

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onFail(isA(UnexpectedMessageException.class));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void initialRequestCountTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    assertThat(test.getObservation().getPendingRequestCount(), equalTo(0l));
  }

  @Test
  public void requestCountAfterRequestNextTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();
    assertThat(test.getObservation().getPendingRequestCount(), equalTo(1l));
  }

  @Test
  public void requestCountAfterRequestFulfilledTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();
    test.onNext("message");
    assertThat(test.getObservation().getPendingRequestCount(), equalTo(0l));
  }

  @Test
  public void requestCountAfterUnboundedRequestTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    test.getObservation().requestUnbounded();
    assertThat(test.getObservation().getPendingRequestCount(), equalTo(Long.MAX_VALUE));
  }

  @Test
  public void requestCountAfterUnboundedRequestFulfilledTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    test.getObservation().requestUnbounded();
    test.onNext("message");
    assertThat(test.getObservation().getPendingRequestCount(), equalTo(Long.MAX_VALUE));
  }

  @Test
  public void requestUnboundedPassTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    test.getObservation().requestUnbounded();
    assertTrue(test.getObservation().isRequestUnbounded());
  }

  @Test
  public void requestUnboundedFailTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);
    test.onObserve(upstreamObservation);
    assertFalse(test.getObservation().isRequestUnbounded());
  }

  @Test
  public void useNullObserverTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);

    assertThrows(NullPointerException.class, () -> test.onObserve(null));
  }

  @Test
  public void useObserverNoTimesTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);

    assertThat(test.getObservation(), nullValue());
  }

  @Test
  public void useObserverMoreThanOnceTest() {
    SafeObserver<String> test = new SafeObserver<>(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onObserve(upstreamObservation);

    verify(downstreamObserver, times(1)).onObserve(Mockito.any());
    verify(upstreamObservation).cancel();
  }
}
