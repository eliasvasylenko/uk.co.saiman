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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("javadoc")
@ExtendWith(MockitoExtension.class)
public class InvalidatingLazyRevalidatingObserverTest {
  @Mock
  Observation upstreamObservation;

  @Mock
  Observer<Invalidation<String>> downstreamObserver;

  @Captor
  ArgumentCaptor<Invalidation<String>> invalidation;

  @Test
  public void invalidateWithSingleMessage() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void invalidateWithMultipleMessages() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    test.onNext("message2");

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void invalidateAndRevalidateSingleMessage() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(
        new MultiplePassthroughObserver<Invalidation<String>>(
            downstreamObserver,
            Invalidation::revalidate));

    test.onObserve(upstreamObservation);
    test.onNext("message");

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void invalidateAndRevalidateMultipleMessages() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(
        new MultiplePassthroughObserver<Invalidation<String>>(
            downstreamObserver,
            Invalidation::revalidate));

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    test.onNext("message2");

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver, times(2)).onNext(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void invalidateWithSingleFailure() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onFail(new Throwable());

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext(any());
    inOrder.verify(downstreamObserver).onFail(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void invalidateAndRevalidateSingleFailure() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(
        new MultiplePassthroughObserver<Invalidation<String>>(
            downstreamObserver,
            Invalidation::revalidate));

    test.onObserve(upstreamObservation);
    assertThrows(MissingValueException.class, () -> test.onFail(new Throwable()));

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext(invalidation.capture());
    assertThrows(MissingValueException.class, invalidation.getValue()::revalidate);
    inOrder.verifyNoMoreInteractions();
  }
}
