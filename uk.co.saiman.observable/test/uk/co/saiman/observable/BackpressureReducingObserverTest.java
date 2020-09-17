/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import static org.mockito.Mockito.when;
import static uk.co.saiman.observable.BackpressureReducingObserver.backpressureReducingObserver;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("javadoc")
@ExtendWith(MockitoExtension.class)
public class BackpressureReducingObserverTest {
  @Mock
  Observation upstreamObservation;

  @Mock
  Observer<String> downstreamObserver;

  @Mock
  Supplier<String> identity;

  @Mock
  Function<String, String> initial;

  @Mock
  BiFunction<String, String, String> accumulator;

  @Test
  public void accumulateFromIdentityAndSingleMessage() {
    when(identity.get()).thenReturn("identity");

    Observer<String> test = backpressureReducingObserver(downstreamObserver, identity, accumulator);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    InOrder inOrder = inOrder(downstreamObserver, identity, accumulator);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(identity).get();
    inOrder.verify(accumulator).apply("identity", "message");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void accumulateFromIdentityAndTwoMessages() {
    when(identity.get()).thenReturn("identity");

    Observer<String> test = backpressureReducingObserver(downstreamObserver, identity, accumulator);

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    test.onNext("message2");

    InOrder inOrder = inOrder(downstreamObserver, identity, accumulator);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(identity).get();
    inOrder.verify(accumulator).apply("identity", "message1");
    inOrder.verify(accumulator).apply("identity", "message2");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void accumulateFromIdentityAndSingleMessageThenComplete() {
    when(identity.get()).thenReturn("identity");
    when(accumulator.apply(any(), any())).thenReturn("accumulation");

    Observer<String> test = backpressureReducingObserver(downstreamObserver, identity, accumulator);

    test.onObserve(upstreamObservation);
    test.onNext("message");
    test.onComplete();

    InOrder inOrder = inOrder(downstreamObserver, identity, accumulator);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(identity).get();
    inOrder.verify(accumulator).apply("identity", "message");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void accumulateFromInitialMessageAndSingleMessage() {
    when(initial.apply(any())).thenReturn("initial");
    when(accumulator.apply(any(), any())).thenReturn("accumulation");

    Observer<String> test = backpressureReducingObserver(downstreamObserver, initial, accumulator);

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    test.onNext("message2");

    InOrder inOrder = inOrder(downstreamObserver, initial, accumulator);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(initial).apply("message1");
    inOrder.verify(accumulator).apply("initial", "message2");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void immediatelyComplete() {
    Observer<String> test = backpressureReducingObserver(downstreamObserver, identity, accumulator);

    test.onObserve(upstreamObservation);
    test.onComplete();

    InOrder inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onComplete();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void requestNextFromIdentity() {
    PassthroughObserver<String, String> test = backpressureReducingObserver(
        downstreamObserver,
        identity,
        accumulator);

    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();

    InOrder inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void requestNextFromMissingInitialMessage() {
    PassthroughObserver<String, String> test = backpressureReducingObserver(
        downstreamObserver,
        initial,
        accumulator);

    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();

    InOrder inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void requestNextFromInitialMessage() {
    when(initial.apply(any())).thenReturn("initial");

    PassthroughObserver<String, String> test = backpressureReducingObserver(
        downstreamObserver,
        initial,
        accumulator);

    test.onObserve(upstreamObservation);
    test.onNext("message");
    test.getObservation().requestNext();

    InOrder inOrder = inOrder(downstreamObserver, initial);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(initial).apply("message");
    inOrder.verify(downstreamObserver).onNext("initial");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void nullAccumulaterWithIdentityTest() {
    assertThrows(
        NullPointerException.class,
        () -> backpressureReducingObserver(downstreamObserver, () -> null, null));
  }

  @Test
  public void nullAccumulaterWithInitialTest() {
    assertThrows(
        NullPointerException.class,
        () -> backpressureReducingObserver(downstreamObserver, a -> null, null));
  }

  @Test
  public void nullIdentityTest() {
    assertThrows(
        NullPointerException.class,
        () -> backpressureReducingObserver(
            downstreamObserver,
            (Supplier<String>) null,
            (a, b) -> null));
  }

  @Test
  public void nullInitialTest() {
    assertThrows(
        NullPointerException.class,
        () -> backpressureReducingObserver(
            downstreamObserver,
            (Function<String, String>) null,
            (a, b) -> null));
  }
}
