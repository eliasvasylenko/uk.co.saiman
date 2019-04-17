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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("javadoc")
@ExtendWith(MockitoExtension.class)
public class HotObservableTest {
  @Mock
  Observer<String> downstreamObserver;

  @Test
  public void observeTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void isLiveAfterObserveTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.assertLive();
  }

  @Test
  public void startWhenLiveTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    assertThrows(IllegalStateException.class, () -> observable.start());
  }

  @Test
  public void startWhenDeadTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();
    observable.start();
  }

  @Test
  public void isLiveAfterStartTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();
    observable.start();
    observable.assertLive();
  }

  @Test
  public void isLiveAfterInstantiationTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.assertLive();
  }

  @Test
  public void isDeadAfterCompleteTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.complete();
    assertFalse(observable.isLive());
  }

  @Test
  public void isDeadAfterFailTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.fail(new Throwable());
    assertFalse(observable.isLive());
  }

  @Test
  public void messageWhenDeadTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();
    assertThrows(IllegalStateException.class, () -> observable.next("fail"));
  }

  @Test
  public void completeWhenDeadTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();
    assertThrows(IllegalStateException.class, () -> observable.complete());
  }

  @Test
  public void failWhenDeadTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();
    assertThrows(IllegalStateException.class, () -> observable.fail(new Exception()));
  }

  @Test
  public void messageTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.next("message");

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("message");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void completeTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.complete();

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onComplete();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void failTest() {
    Throwable t = new Throwable();

    HotObservable<String> observable = new HotObservable<>();
    observable.observe(downstreamObserver);
    observable.fail(t);

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onFail(t);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void hasObserversTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe();
    assertThat(observable.hasObservers(), equalTo(true));
  }

  @Test
  public void hasNoObserversTest() {
    HotObservable<String> observable = new HotObservable<>();
    assertThat(observable.hasObservers(), equalTo(false));
  }

  @Test
  public void hasNoObserversAfterDiscardingOnlyObserverTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe().cancel();
    assertThat(observable.hasObservers(), equalTo(false));
  }

  @Test
  public void hasObserversAfterDiscardingOneOfTwoObserversTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe();
    observable.observe().cancel();
    assertThat(observable.hasObservers(), equalTo(true));
  }

  @Test
  public void failWithNullThrowableTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe();
    assertThrows(NullPointerException.class, () -> observable.fail(null));
  }

  @Test
  public void failWithNullMessageTest() {
    HotObservable<String> observable = new HotObservable<>();
    observable.observe();
    assertThrows(NullPointerException.class, () -> observable.next(null));
  }
}
