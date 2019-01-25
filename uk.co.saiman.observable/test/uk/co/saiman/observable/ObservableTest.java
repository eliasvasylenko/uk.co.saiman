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

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings({ "javadoc", "unchecked" })
@ExtendWith(MockitoExtension.class)
public class ObservableTest {
  @Mock
  Observable<String> upstreamObservable;
  @Mock
  Observer<Object> downstreamObserver;
  Observable<String> downstreamObservable = a -> upstreamObservable.observe(a);

  @Test
  public void thenTest() {
    downstreamObservable.then(m -> {}).observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(MultiplePassthroughObserver.class));
  }

  @Test
  public void thenAfterTest() {
    downstreamObservable.thenAfter(m -> {}).observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(MultiplePassthroughObserver.class));
  }

  @Test
  public void retryingTest() {
    downstreamObservable.retrying().observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(RetryingObserver.class));
  }

  @Test
  public void softReferenceOwnedTest() {
    downstreamObservable.softReference(new Object()).observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(ReferenceOwnedObserver.class));
  }

  @Test
  public void softReferenceTest() {
    downstreamObservable.softReference().observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(ReferenceObserver.class));
  }

  @Test
  public void weakReferenceOwnedTest() {
    downstreamObservable.weakReference(new Object()).observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(ReferenceOwnedObserver.class));
  }

  @Test
  public void weakReferenceTest() {
    downstreamObservable.weakReference().observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(ReferenceObserver.class));
  }

  @Test
  public void executeOnTest() {
    downstreamObservable.executeOn(r -> {}).observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(ExecutorObserver.class));
  }

  @Test
  public void mapTest() {
    downstreamObservable.map(s -> s).observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(MappingObserver.class));
  }

  @Test
  public void filterTest() {
    downstreamObservable.filter(s -> true).observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(FilteringObserver.class));
  }

  @Test
  public void takeWhileTest() {
    downstreamObservable.takeWhile(s -> true).observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(TakeWhileObserver.class));
  }

  @Test
  public void dropWhileTest() {
    downstreamObservable.dropWhile(s -> true).observe(downstreamObserver);

    verify(upstreamObservable).observe(isA(DropWhileObserver.class));
  }
}
