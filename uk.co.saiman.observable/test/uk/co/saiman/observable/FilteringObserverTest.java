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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("javadoc")
@ExtendWith(MockitoExtension.class)
public class FilteringObserverTest {
  @Mock
  Observation upstreamObservation;

  @Mock
  Observer<String> downstreamObserver;

  @Test
  public void filterFailMessageEventTest() {
    Observer<String> test = new FilteringObserver<>(downstreamObserver, s -> false);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    InOrder inOrder = inOrder(downstreamObserver, upstreamObservation);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(upstreamObservation).requestNext();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void filterPassMessageEventTest() {
    Observer<String> test = new FilteringObserver<>(downstreamObserver, s -> true);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    InOrder inOrder = inOrder(downstreamObserver, upstreamObservation);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("message");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void filterMultipleMessageEventsTest() {
    Observer<String> test = new FilteringObserver<>(downstreamObserver, s -> s.startsWith("t"));

    test.onObserve(upstreamObservation);
    test.onNext("one");
    test.onNext("two");
    test.onNext("three");
    test.onNext("four");

    InOrder inOrder = inOrder(downstreamObserver, upstreamObservation);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(upstreamObservation).requestNext();
    inOrder.verify(downstreamObserver).onNext("two");
    inOrder.verify(downstreamObserver).onNext("three");
    inOrder.verify(upstreamObservation).requestNext();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void nullFilterTest() {
    assertThrows(
        NullPointerException.class,
        () -> new FilteringObserver<>(downstreamObserver, null));
  }
}
