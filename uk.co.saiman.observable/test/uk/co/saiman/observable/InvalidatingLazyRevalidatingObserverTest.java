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

import org.junit.Test;

import mockit.FullVerifications;
import mockit.Injectable;
import mockit.VerificationsInOrder;

@SuppressWarnings("javadoc")
public class InvalidatingLazyRevalidatingObserverTest {
  @Injectable
  Observation upstreamObservation;

  @Injectable
  Observer<Invalidation<String>> downstreamObserver;

  @SuppressWarnings("unchecked")
  @Test
  public void invalidateWithSingleMessage() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onNext((Invalidation<String>) any);
      }
    };
    new FullVerifications() {};
  }

  @SuppressWarnings("unchecked")
  @Test
  public void invalidateWithMultipleMessages() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    test.onNext("message2");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onNext((Invalidation<String>) any);
      }
    };
    new FullVerifications() {};
  }

  @SuppressWarnings("unchecked")
  @Test
  public void invalidateAndRevalidateSingleMessage() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(
        new MultiplePassthroughObserver<Invalidation<String>>(
            downstreamObserver,
            Invalidation::revalidate));

    test.onObserve(upstreamObservation);
    test.onNext("message");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onNext((Invalidation<String>) any);
      }
    };
    new FullVerifications() {};
  }

  @SuppressWarnings("unchecked")
  @Test
  public void invalidateAndRevalidateMultipleMessages() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(
        new MultiplePassthroughObserver<Invalidation<String>>(
            downstreamObserver,
            Invalidation::revalidate));

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    test.onNext("message2");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onNext((Invalidation<String>) any);
        downstreamObserver.onNext((Invalidation<String>) any);
      }
    };
    new FullVerifications() {};
  }

  @SuppressWarnings("unchecked")
  @Test
  public void invalidateWithSingleFailure() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(downstreamObserver);

    test.onObserve(upstreamObservation);
    test.onFail(new Throwable());

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onNext((Invalidation<String>) any);
        downstreamObserver.onFail((Throwable) any);
      }
    };
    new FullVerifications() {};
  }

  @Test(expected = MissingValueException.class)
  public void invalidateAndRevalidateSingleFailure() {
    Observer<String> test = new InvalidatingLazyRevalidatingObserver<>(
        new MultiplePassthroughObserver<Invalidation<String>>(
            downstreamObserver,
            Invalidation::revalidate));

    test.onObserve(upstreamObservation);
    test.onFail(new Throwable());

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onFail((Throwable) any);
      }
    };
    new FullVerifications() {};
  }
}
