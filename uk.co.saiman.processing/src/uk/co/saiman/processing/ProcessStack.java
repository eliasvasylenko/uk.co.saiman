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
