package uk.co.saiman.experiment.procedure;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptyNavigableMap;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.product.Nothing;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

public abstract class Instructions<T extends Instructions<T>> {
  /*
   * We store the experiment hierarchy as flat map. Because experiment paths are
   * comparable, the ordering is equivalent to an alphabetical, depth-first
   * traversal of the path hierarchy.
   */
  private final NavigableMap<ExperimentPath, ExperimentLocation> instructions;
  private final Map<ProductPath, ProductLocation> dependencies;

  Instructions() {
    this(emptyNavigableMap(), emptyMap());
  }

  Instructions(
      NavigableMap<ExperimentPath, ExperimentLocation> instructions,
      Map<ProductPath, ProductLocation> dependencies) {
    this.instructions = instructions;
    this.dependencies = dependencies;
  }

  NavigableMap<ExperimentPath, ExperimentLocation> getInstructions() {
    return instructions;
  }

  Map<ProductPath, ProductLocation> getDependencies() {
    return dependencies;
  }

  public Stream<ExperimentPath> instructions() {
    return instructions.keySet().stream();
  }

  public Stream<ProductPath> dependents(ExperimentPath path) {
    return Optional
        .ofNullable(instructions.get(path))
        .map(l -> dependencyPaths(path, l))
        .orElseGet(Stream::empty);
  }

  private Stream<ProductPath> dependencyPaths(ExperimentPath path, ExperimentLocation location) {
    return Stream
        .concat(
            location.instruction().conductor().productions().map(Production::id),
            location.unknownProducts())
        .map(s -> ProductPath.define(path, s))
        .filter(dependencies::containsKey);
  }

  public Optional<Instruction> instruction(ExperimentPath path) {
    return Optional.ofNullable(instructions.get(path)).map(e -> e.instruction());
  }

  public Stream<ExperimentPath> independentInstructions() {
    return dependencies.get(null).dependencies();
  }

  public Stream<ExperimentPath> dependentInstructions(ProductPath path) {
    return Optional
        .ofNullable(dependencies.get(path))
        .map(d -> d.dependencies())
        .orElseGet(Stream::empty);
  }

  public T withInstruction(ProductPath path, Instruction instruction) {
    return withInstruction(path, -1, instruction);
  }

  public T withInstruction(ProductPath path, long index, Instruction instruction) {
    return withInstruction(Optional.of(path), index, instruction);
  }

  T withInstruction(long index, Instruction instruction) {
    return withInstruction(Optional.empty(), index, instruction);
  }

  private T withInstruction(
      Optional<ProductPath> productPath,
      long index,
      Instruction instruction) {
    var instructions = new TreeMap<>(this.instructions);
    var dependencies = new HashMap<>(this.dependencies);

    addInstruction(instructions, dependencies, productPath, index, requireNonNull(instruction));

    return withInstructions(instructions, dependencies);
  }

  public <U extends Product> T withTemplate(
      Dependency<? extends U> dependency,
      Template<?, U> template) {
    return withTemplate(dependency, -1, template);
  }

  public <U extends Product> T withTemplate(
      Dependency<? extends U> dependency,
      long index,
      Template<?, U> template) {
    return withTemplate(
        Optional.of(validateProductPath(dependency)),
        index,
        requireNonNull(template));
  }

  T withTemplate(long index, Template<?, Nothing> template) {
    return withTemplate(Optional.empty(), index, template);
  }

  private T withTemplate(Optional<ProductPath> productPath, long index, Template<?, ?> template) {
    var instructions = new TreeMap<>(this.instructions);
    var dependencies = new HashMap<>(this.dependencies);

    var experimentPath = productPath
        .map(p -> p.getExperimentPath())
        .orElseGet(ExperimentPath::defineAbsolute)
        .resolve(template.id());

    if (instructions.containsKey(experimentPath)) {
      throw new ProcedureException(
          format(
              "Cannot expand template %s at path %s, instruction already present in %s",
              template,
              experimentPath,
              this));
    }

    addInstruction(
        instructions,
        dependencies,
        productPath,
        index,
        requireNonNull(template.instruction()));

    template
        .getInstructions()
        .entrySet()
        .stream()
        .map(e -> Map.entry(e.getKey(), e.getValue()))
        .forEach(e -> instructions.put(e.getKey(), e.getValue()));
    template
        .getDependencies()
        .entrySet()
        .stream()
        .map(e -> e)
        .forEach(e -> dependencies.put(e.getKey(), e.getValue()));

    return withInstructions(instructions, dependencies);
  }

  public T withoutInstruction(ExperimentPath experimentPath) {
    var instructions = new TreeMap<>(this.instructions);
    var dependencies = new HashMap<>(this.dependencies);

    removeInstruction(instructions, dependencies, experimentPath);

    return withInstructions(instructions, dependencies);
  }

  protected abstract T withInstructions(
      NavigableMap<ExperimentPath, ExperimentLocation> instructions,
      Map<ProductPath, ProductLocation> dependencies);

  private ProductPath validateProductPath(Dependency<?> dependency) {
    requireNonNull(dependency);
    var experiment = instructions.get(dependency.getExperimentPath());
    if (experiment == null) {
      throw new ProcedureException(
          format("Cannot resolve dependency %s amongst instructions %s", dependency, this));
    }
    experiment
        .instruction()
        .conductor()
        .production(dependency.getProduction().id())
        .filter(p -> p.equals(dependency.getProduction()))
        .orElseThrow(
            () -> new ProcedureException(
                format(
                    "Cannot resolve dependency %s against instruction %s",
                    dependency,
                    experiment.instruction())));
    return dependency.getProductPath();
  }

  private static void addInstruction(
      TreeMap<ExperimentPath, ExperimentLocation> instructionMap,
      HashMap<ProductPath, ProductLocation> dependencyMap,
      Optional<ProductPath> productPath,
      long index,
      Instruction instruction) {
    var experimentPath = productPath
        .map(ProductPath::getExperimentPath)
        .orElseGet(ExperimentPath::defineAbsolute);

    instructionMap.computeIfPresent(experimentPath, (e, l) -> l.withInstruction(instruction));

    productPath.ifPresent(path -> {
      dependencyMap
          .compute(
              path,
              (p, l) -> Optional
                  .ofNullable(l)
                  .orElseGet(ProductLocation::new)
                  .withDependency(experimentPath));
    });
  }

  private static void removeInstruction(
      TreeMap<ExperimentPath, ExperimentLocation> instructionMap,
      HashMap<ProductPath, ProductLocation> dependencyMap,
      ExperimentPath path) {
    var higherPaths = instructionMap.navigableKeySet().tailSet(path).iterator();
    while (higherPaths.hasNext()) {
      var higherPath = higherPaths.next();
      if (higherPath.relativeTo(path).getAncestorDepth() > 0) {
        break;
      }
      higherPaths.remove();
    }

    // TODO remove instructions with indirect dependencies
    throw new UnsupportedOperationException();
  }

  /*
   * TODO convert to value type & record type
   */
  static class ExperimentLocation {
    private final Instruction instruction;
    private final List<String> unknownProducts;

    private ExperimentLocation(Instruction instruction) {
      this(instruction, List.of());
    }

    private ExperimentLocation(Instruction instruction, List<String> unknownProducts) {
      this.instruction = instruction;
      this.unknownProducts = unknownProducts;
    }

    public Instruction instruction() {
      return instruction;
    }

    public ExperimentLocation withInstruction(Instruction instruction) {
      /*
       * TODO check for existing products which might become unknown, remove existing
       * unknowns which and on the new instruction.
       */

      throw new UnsupportedOperationException();
    }

    public Stream<String> unknownProducts() {
      return unknownProducts.stream();
    }

    public ExperimentLocation withUnknownProducts(Collection<? extends String> products) {
      return new ExperimentLocation(instruction, List.copyOf(products));
    }
  }

  static class ProductLocation {
    private final List<ExperimentPath> dependencies;

    public ProductLocation() {
      dependencies = emptyList();
    }

    public ProductLocation(List<ExperimentPath> dependencies) {
      this.dependencies = dependencies;
    }

    public Stream<ExperimentPath> dependencies() {
      return dependencies.stream();
    }

    public ProductLocation withDependency(ExperimentPath path) {
      List<ExperimentPath> dependencies = new ArrayList<>(this.dependencies.size() + 1);
      dependencies.addAll(this.dependencies);
      dependencies.add(path);
      return new ProductLocation(dependencies);
    }
  }
}
