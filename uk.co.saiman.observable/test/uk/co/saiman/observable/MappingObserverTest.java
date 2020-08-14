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
import static org.mockito.Mockito.inOrder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("javadoc")
@ExtendWith(MockitoExtension.class)
public class MappingObserverTest {
  @Mock
  Observation upstreamObservation;

  @Mock
  Observer<String> downstreamObserver;

  @Test
  public void mapMultipleMessagesEventTest() {
    Observer<String> test = new MappingObserver<>(downstreamObserver, s -> s + "!");

    test.onObserve(upstreamObservation);
    test.onNext("one");
    test.onNext("two");
    test.onNext("three");
    test.onNext("four");

    InOrder inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(upstreamObservation);
    inOrder.verify(downstreamObserver).onNext("one!");
    inOrder.verify(downstreamObserver).onNext("two!");
    inOrder.verify(downstreamObserver).onNext("three!");
    inOrder.verify(downstreamObserver).onNext("four!");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void nullMappingTest() {
    assertThrows(NullPointerException.class, () -> new MappingObserver<>(downstreamObserver, null));
  }
}
