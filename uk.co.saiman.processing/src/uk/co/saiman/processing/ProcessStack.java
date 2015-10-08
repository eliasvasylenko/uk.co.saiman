/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.processing.
 *
 * uk.co.saiman.processing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.processing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.processing;

import java.util.HashSet;
import java.util.Set;

public interface ProcessStack<T, R> {
	void setTarget(T target);

	Processor<T, R> getProcessor();

	Set<ProcessStack<R, ?>> children();

	default R getResult() {
		return getProcessor().process(getTarget());
	}

	T getTarget();

	static <T, R> ProcessStack<T, R> over(Processor<T, R> processor) {
		return new ProcessStack<T, R>() {
			private final Set<ProcessStack<R, ?>> children = new HashSet<>();

			private T target;
			private R result;

			public void setTarget(T target) {
				this.target = target;
				result = getProcessor().process(target);
				for (ProcessStack<R, ?> child : children)
					child.setTarget(result);
			}

			public Processor<T, R> getProcessor() {
				return processor;
			}

			public Set<ProcessStack<R, ?>> children() {
				return children;
			}

			public R getResult() {
				return result;
			}

			public T getTarget() {
				return target;
			}
		};
	}

	static <T, R> ProcessStack<T, R> over(Processor<T, R> processor, T target) {
		ProcessStack<T, R> stack = over(processor);
		stack.setTarget(target);
		return stack;
	}
}
