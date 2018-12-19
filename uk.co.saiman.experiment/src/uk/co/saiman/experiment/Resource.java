package uk.co.saiman.experiment;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class Resource<T extends AutoCloseable> {
  private final String id;
  private final Supplier<? extends CompletableFuture<? extends T>> supplier;

  public Resource(String id, Supplier<? extends CompletableFuture<? extends T>> supplier) {
    this.id = id;
    this.supplier = supplier;
  }

  public String id() {
    return id;
  }

  public Future<? extends T> request() {
    return supplier.get();
  }

  public T acquire() {
    return supplier.get().join();
  }
}
