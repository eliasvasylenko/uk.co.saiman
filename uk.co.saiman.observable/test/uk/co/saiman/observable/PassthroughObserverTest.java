/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import org.junit.Test;

import mockit.FullVerificationsInOrder;
import mockit.Mocked;
import uk.co.saiman.observable.Observation;
import uk.co.saiman.observable.Observer;
import uk.co.saiman.observable.PassthroughObserver;

@SuppressWarnings("javadoc")
public class PassthroughObserverTest {
  @Mocked
  Observation upstreamObservation;

  @Mocked
  Observer<String> downstreamObserver;

  protected PassthroughObserver<String, String> createDefaultObserver(
      Observer<String> downstreamObserver) {
    return new PassthroughObserver<String, String>(downstreamObserver) {
      @Override
      public void onNext(String message) {
        getDownstreamObserver().onNext(message);
      }
    };
  }

  @Test
  public void useObserverOnceTest() {
    Observer<String> test = createDefaultObserver(downstreamObserver);

    test.onObserve(upstreamObservation);

    new FullVerificationsInOrder() {
      {
        downstreamObserver.onObserve(upstreamObservation);
      }
    };
  }

  @Test
  public void passSomeMessageEventTest() {
    Observer<String> test = createDefaultObserver(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    new FullVerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onNext("message");
      }
    };
  }

  @Test
  public void passSomeMessagesEventTest() {
    Observer<String> test = createDefaultObserver(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    test.onNext("message2");
    test.onNext("message3");

    new FullVerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onNext("message1");
        downstreamObserver.onNext("message2");
        downstreamObserver.onNext("message3");
      }
    };
  }

  @Test
  public void passCompleteEventTest() {
    Observer<String> test = createDefaultObserver(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onComplete();

    new FullVerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onComplete();
      }
    };
  }

  @Test
  public void passFailEventTest() {
    Observer<String> test = createDefaultObserver(downstreamObserver);

    Throwable t = new Throwable();

    test.onObserve(upstreamObservation);
    test.onFail(t);

    new FullVerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onFail(t);
      }
    };
  }
}
