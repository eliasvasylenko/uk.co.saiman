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
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.inOrder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.co.saiman.observable.ObservableValue.Change;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("javadoc")
public class ObservablePropertyImplTest {
  @Mock
  Observer<String> downstreamObserver;
  @Mock
  Observer<Change<String>> changeObserver;
  @Captor
  ArgumentCaptor<Change<String>> change;
  @Captor
  ArgumentCaptor<Throwable> failure;

  @Test
  public void getInitialValueTest() {
    ObservableProperty<String> property = new ObservablePropertyImpl<>("initial");

    assertThat(property.get(), equalTo("initial"));
  }

  @Test
  public void getInitialValueMultipleTimesTest() {
    ObservableProperty<String> property = new ObservablePropertyImpl<>("initial");

    assertThat(property.get(), equalTo("initial"));
    assertThat(property.get(), equalTo("initial"));
  }

  @Test
  public void initialValueMessageOnSubscribeTest() {
    ObservableProperty<String> property = new ObservablePropertyImpl<>("initial");

    property.value().observe(downstreamObserver);

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("initial");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void initialValueMessageOnSubscribeMultipleTimesTest() {
    ObservableProperty<String> property = new ObservablePropertyImpl<>("initial");

    property.value().observe(downstreamObserver).cancel();
    property.value().observe(downstreamObserver);

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("initial");
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("initial");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void setValueMessageAfterSubscribeTest() {
    ObservableProperty<String> property = new ObservablePropertyImpl<>("initial");

    property.value().observe(downstreamObserver);
    property.set("message");

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("initial");
    inOrder.verify(downstreamObserver).onNext("message");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void setValueMessageBeforeSubscribeTest() {
    ObservableProperty<String> property = new ObservablePropertyImpl<>("initial");

    property.set("message");
    property.value().observe(downstreamObserver);

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("message");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void setProblemEventAfterSubscribeTest() {
    ObservableProperty<String> property = new ObservablePropertyImpl<>("initial");
    Throwable problem = new Throwable();

    property.value().observe(downstreamObserver);
    property.setProblem(problem);

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onNext("initial");
    inOrder.verify(downstreamObserver).onFail(problem);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void setProblemEventBeforeSubscribeTest() {
    ObservableProperty<String> property = new ObservablePropertyImpl<>("initial");
    Throwable problem = new Throwable();

    property.setProblem(problem);
    property.value().observe(downstreamObserver);

    var inOrder = inOrder(downstreamObserver);
    inOrder.verify(downstreamObserver).onObserve(any());
    inOrder.verify(downstreamObserver).onFail(failure.capture());
    assertThat(failure.getValue(), instanceOf(MissingValueException.class));
    assertThat(((MissingValueException) failure.getValue()).getCause(), equalTo(problem));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void setProblemEventThenGetTest() {
    ObservableProperty<String> property = new ObservablePropertyImpl<>("initial");
    Throwable problem = new Throwable();

    property.setProblem(problem);
    assertThrows(MissingValueException.class, () -> property.get());
  }

  @Test
  public void clearProblemEventThenGetTest() {
    ObservableProperty<String> property = new ObservablePropertyImpl<>("initial");
    Throwable problem = new Throwable();

    property.setProblem(problem);
    property.set("message");
    assertThat(property.get(), equalTo("message"));
  }

  @Test
  public void failWithNullThrowableTest() {
    ObservablePropertyImpl<String> observable = new ObservablePropertyImpl<>("initial");
    observable.value().observe();
    assertThrows(NullPointerException.class, () -> observable.setProblem(null));
  }

  @Test
  public void failWithNullMessageTest() {
    ObservablePropertyImpl<String> observable = new ObservablePropertyImpl<>("initial");
    observable.value().observe();
    assertThrows(NullPointerException.class, () -> observable.set(null));
  }

  @Test
  public void noChangesOnObserveTest() {
    ObservablePropertyImpl<String> observable = new ObservablePropertyImpl<>("initial");
    observable.changes().observe(changeObserver);

    var inOrder = inOrder(changeObserver);
    inOrder.verify(changeObserver).onObserve(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void changeInitialToNextMessageTest() {
    ObservablePropertyImpl<String> observable = new ObservablePropertyImpl<>("initial");
    observable.changes().observe(changeObserver);
    observable.set("message");

    var inOrder = inOrder(changeObserver);
    inOrder.verify(changeObserver).onObserve(any());
    inOrder.verify(changeObserver).onNext(change.capture());
    assertThat(change.getValue().previousValue().get(), equalTo("initial"));
    assertThat(change.getValue().newValue().get(), equalTo("message"));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void changeInitialToProblemTest() {
    ObservablePropertyImpl<String> observable = new ObservablePropertyImpl<>("initial");
    observable.changes().observe(changeObserver);
    observable.setProblem(new Throwable());

    var inOrder = inOrder(changeObserver);
    inOrder.verify(changeObserver).onObserve(any());
    inOrder.verify(changeObserver).onNext(change.capture());
    assertThat(change.getValue().previousValue().get(), equalTo("initial"));
    assertFalse(change.getValue().newValue().isPresent());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void changeInitialToEqualValueTest() {
    ObservablePropertyImpl<String> observable = new ObservablePropertyImpl<>("initial");
    observable.changes().observe(changeObserver);
    observable.set("initial");

    var inOrder = inOrder(changeObserver);
    inOrder.verify(changeObserver).onObserve(any());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void changeProblemToNextMessageTest() {
    ObservablePropertyImpl<String> observable = new ObservablePropertyImpl<>("initial");
    observable.changes().observe(changeObserver);
    observable.setProblem(new Throwable());
    observable.set("message");

    var inOrder = inOrder(changeObserver);
    inOrder.verify(changeObserver).onObserve(any());
    inOrder.verify(changeObserver, calls(1)).onNext(change.capture());

    assertThat(change.getValue().previousValue().get(), equalTo("initial"));
    assertFalse(change.getValue().newValue().isPresent());

    inOrder.verify(changeObserver).onNext(change.capture());
    assertFalse(change.getValue().previousValue().isPresent());
    assertThat(change.getValue().newValue().get(), equalTo("message"));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void changeProblemToProblemTest() {
    ObservablePropertyImpl<String> observable = new ObservablePropertyImpl<>("initial");
    observable.changes().observe(changeObserver);
    observable.setProblem(new Throwable());
    observable.setProblem(new Throwable());

    var inOrder = inOrder(changeObserver);
    inOrder.verify(changeObserver).onObserve(any());

    inOrder.verify(changeObserver, calls(1)).onNext(change.capture());
    assertThat(change.getValue().previousValue().get(), equalTo("initial"));
    assertFalse(change.getValue().newValue().isPresent());

    inOrder.verify(changeObserver).onNext(change.capture());
    assertFalse(change.getValue().previousValue().isPresent());
    assertFalse(change.getValue().newValue().isPresent());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void changeIsImmutableAfterFurtherChange() {
    ObservablePropertyImpl<String> observable = new ObservablePropertyImpl<>("initial");
    observable.changes().observe(changeObserver);
    observable.set("message");
    observable.set("more");

    var inOrder = inOrder(changeObserver);
    inOrder.verify(changeObserver).onObserve(any());
    inOrder.verify(changeObserver, calls(1)).onNext(change.capture());
    assertThat(change.getValue().previousValue().get(), equalTo("initial"));
    assertThat(change.getValue().newValue().get(), equalTo("message"));

    inOrder.verify(changeObserver).onNext(any());
    assertThat(change.getValue().previousValue().get(), equalTo("initial"));
    assertThat(change.getValue().newValue().get(), equalTo("message"));
    inOrder.verifyNoMoreInteractions();
  }
}
