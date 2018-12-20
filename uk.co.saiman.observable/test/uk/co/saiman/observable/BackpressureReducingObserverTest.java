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
import static uk.co.saiman.observable.BackpressureReducingObserver.backpressureReducingObserver;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Injectable;
import mockit.VerificationsInOrder;

@SuppressWarnings("javadoc")
public class BackpressureReducingObserverTest {
  @Injectable
  Observation upstreamObservation;

  @Injectable
  Observer<String> downstreamObserver;

  @Injectable
  Supplier<String> identity;

  @Injectable
  Function<String, String> initial;

  @Injectable
  BiFunction<String, String, String> accumulator;

  @Test
  public void accumulateFromIdentityAndSingleMessage() {
    new Expectations() {
      {
        identity.get();
        result = "identity";
      }
    };

    Observer<String> test = backpressureReducingObserver(downstreamObserver, identity, accumulator);

    test.onObserve(upstreamObservation);
    test.onNext("message");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        identity.get();
        accumulator.apply("identity", "message");
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void accumulateFromIdentityAndTwoMessages() {
    new Expectations() {
      {
        identity.get();
        result = "identity";
      }
    };

    Observer<String> test = backpressureReducingObserver(downstreamObserver, identity, accumulator);

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    test.onNext("message2");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        identity.get();
        accumulator.apply("identity", "message1");
        accumulator.apply("identity", "message2");
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void accumulateFromIdentityAndSingleMessageThenComplete() {
    new Expectations() {
      {
        identity.get();
        result = "identity";
        accumulator.apply(anyString, anyString);
        result = "accumulation";
      }
    };

    Observer<String> test = backpressureReducingObserver(downstreamObserver, identity, accumulator);

    test.onObserve(upstreamObservation);
    test.onNext("message");
    test.onComplete();

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        identity.get();
        accumulator.apply("identity", "message");
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void accumulateFromInitialMessageAndSingleMessage() {
    new Expectations() {
      {
        initial.apply(anyString);
        result = "initial";
        accumulator.apply(anyString, anyString);
        result = "accumulation";
      }
    };

    Observer<String> test = backpressureReducingObserver(downstreamObserver, initial, accumulator);

    test.onObserve(upstreamObservation);
    test.onNext("message1");
    test.onNext("message2");

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        initial.apply("message1");
        accumulator.apply("initial", "message2");
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void immediatelyComplete() {
    Observer<String> test = backpressureReducingObserver(downstreamObserver, identity, accumulator);

    test.onObserve(upstreamObservation);
    test.onComplete();

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        downstreamObserver.onComplete();
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void requestNextFromIdentity() {
    PassthroughObserver<String, String> test = backpressureReducingObserver(
        downstreamObserver,
        identity,
        accumulator);

    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void requestNextFromMissingInitialMessage() {
    PassthroughObserver<String, String> test = backpressureReducingObserver(
        downstreamObserver,
        initial,
        accumulator);

    test.onObserve(upstreamObservation);
    test.getObservation().requestNext();

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
      }
    };
    new FullVerifications() {};
  }

  @Test
  public void requestNextFromInitialMessage() {
    new Expectations() {
      {
        initial.apply(anyString);
        result = "initial";
      }
    };

    PassthroughObserver<String, String> test = backpressureReducingObserver(
        downstreamObserver,
        initial,
        accumulator);

    test.onObserve(upstreamObservation);
    test.onNext("message");
    test.getObservation().requestNext();

    new VerificationsInOrder() {
      {
        downstreamObserver.onObserve((Observation) any);
        initial.apply("message");
        downstreamObserver.onNext("initial");
      }
    };
    new FullVerifications() {};
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
