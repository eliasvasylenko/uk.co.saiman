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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

import java.util.concurrent.Executor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("javadoc")
@ExtendWith(MockitoExtension.class)
public class ExecutorObserverTest {
  interface MockObserver<T> extends Observer<T> {}

  interface MockObservation extends Observation {}

  @Mock
  MockObservation upstreamObservation;

  @Mock
  MockObserver<String> downstreamObserver;

  @Test
  public void messageEventOnInlineExecutorTest() {
    SafeObserver<String> test = new ExecutorObserver<>(downstreamObserver, r -> r.run());

    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();
    test.onNext("message");

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("message");
  }

  @Test
  public void messageEventOnDiscardingExecutorTest() {
    Observer<String> test = new ExecutorObserver<>(downstreamObserver, r -> {});

    test.onObserve(upstreamObservation);
    test.onNext("message");

    var inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void messageEventOnMockedExecutorTest(@Mock Executor executor) {
    Observer<String> test = new ExecutorObserver<>(downstreamObserver, executor);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    var inOrder = inOrder(executor, upstreamObservation, downstreamObserver);
    inOrder.verify(executor, times(2)).execute(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void throwFromOnObserveTest() {
    Throwable throwable = new RuntimeException();

    doThrow(throwable).when(downstreamObserver).onObserve(any());

    Observer<String> test = new ExecutorObserver<>(downstreamObserver, r -> r.run());
    test.onObserve(upstreamObservation);

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onFail(throwable);
  }

  @Test
  public void throwFromOnNextTest() {
    Throwable throwable = new RuntimeException();

    doThrow(throwable).when(downstreamObserver).onNext(any());

    SafeObserver<String> test = new ExecutorObserver<>(downstreamObserver, r -> r.run());

    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();
    test.onNext("message");

    var inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("message");
    inOrder.verify(downstreamObserver).onFail(throwable);
  }

  @Test
  public void nullExecutorTest() {
    assertThrows(
        NullPointerException.class,
        () -> new ExecutorObserver<>(downstreamObserver, null));
  }
}
