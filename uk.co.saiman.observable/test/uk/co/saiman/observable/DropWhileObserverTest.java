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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("javadoc")
@ExtendWith(MockitoExtension.class)
public class DropWhileObserverTest {
  @Mock
  Observation upstreamObservation;

  @Mock
  Observer<String> downstreamObserver;

  @Test
  public void failSkipFilterMessageEventTest() {
    Observer<String> test = new DropWhileObserver<>(downstreamObserver, s -> false);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("message");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void passSkipFilterMessageEventTest() {
    Observer<String> test = new DropWhileObserver<>(downstreamObserver, s -> true);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    var inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(upstreamObservation).requestNext();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void passThenFailSkipFilterMessageEventTest() {
    Observer<String> test = new DropWhileObserver<>(downstreamObserver, "pass"::equals);

    test.onObserve(upstreamObservation);
    test.onNext("pass");
    test.onNext("fail");

    var inOrder = inOrder(upstreamObservation, downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(upstreamObservation).requestNext();
    inOrder.verify(downstreamObserver).onNext("fail");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void failThenPassSkipFilterMessageEventTest() {
    Observer<String> test = new DropWhileObserver<>(downstreamObserver, "pass"::equals);

    test.onObserve(upstreamObservation);
    test.onNext("fail");
    test.onNext("pass");

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("fail");
    inOrder.verify(downstreamObserver).onNext("pass");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void nullSkipFilterTest() {
    assertThrows(
        NullPointerException.class,
        () -> new DropWhileObserver<>(downstreamObserver, null));
  }
}
