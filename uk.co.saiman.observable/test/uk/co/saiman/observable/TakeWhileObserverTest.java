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
import uk.co.saiman.observable.TakeWhileObserver;

@SuppressWarnings("javadoc")
public class TakeWhileObserverTest {
  @Mocked
  Observation upstreamObservation;

  @Mocked
  Observer<String> downstreamObserver;

  @Test
  public void failTakeFilterMessageEventTest() {
    Observer<String> test = new TakeWhileObserver<>(downstreamObserver, s -> false);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    new FullVerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onComplete();
        upstreamObservation.cancel();
      }
    };
  }

  @Test
  public void passTakeFilterMessageEventTest() {
    Observer<String> test = new TakeWhileObserver<>(downstreamObserver, s -> true);

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
  public void passThenFailTakeFilterMessageEventTest() {
    Observer<String> test = new TakeWhileObserver<>(downstreamObserver, "pass"::equals);

    test.onObserve(upstreamObservation);
    test.onNext("pass");
    test.onNext("fail");

    new FullVerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onNext("pass");
        downstreamObserver.onComplete();
        upstreamObservation.cancel();
      }
    };
  }

  @Test
  public void failThenPassTakeFilterMessageEventTest() {
    Observer<String> test = new TakeWhileObserver<>(downstreamObserver, "pass"::equals);

    test.onObserve(upstreamObservation);
    test.onNext("fail");
    test.onNext("pass");

    new FullVerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onComplete();
        upstreamObservation.cancel();
      }
    };
  }

  @Test(expected = NullPointerException.class)
  public void nullTakeFilterTest() {
    new TakeWhileObserver<>(downstreamObserver, null);
  }
}
