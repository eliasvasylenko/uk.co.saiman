package uk.co.saiman.experiment.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import uk.co.saiman.data.resource.Location;
import uk.co.saiman.data.resource.PathLocation;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.impl.ExperimentLocationManager;
import uk.co.saiman.experiment.impl.ExperimentNodeImpl;

public class FileSystemLocationManager implements ExperimentLocationManager {
  private final Path rootPath;

  public FileSystemLocationManager(Path rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public void removeLocation(ExperimentNodeImpl<?, ?> node) throws IOException {
    Files.delete(getPath(node));
  }

  @Override
  public void updateLocation(ExperimentNodeImpl<?, ?> node, String id) throws IOException {
    Path newLocation = resolvePath(getParentPath(node), node.getType(), id);

    if (node.getId() != null) {
      Path oldLocation = getPath(node);

      if (Files.exists(oldLocation)) {
        Files.move(oldLocation, newLocation);
      }
    }
  }

  @Override
  public Location getLocation(ExperimentNodeImpl<?, ?> node) {
    return new PathLocation(getPath(node));
  }

  private Path getPath(ExperimentNode<?, ?> node) {
    return resolvePath(getParentPath(node), node.getType(), node.getId());
  }

  private Path getParentPath(ExperimentNode<?, ?> node) {
    return node.getParent().map(this::getPath).orElse(rootPath);
  }

  private Path resolvePath(Path parentPath, ExperimentType<?, ?> type, String id) {
    if (!(type instanceof ExperimentRoot))
      parentPath = parentPath.resolve(type.getId());
    return parentPath.resolve(id);
  }

  public Path getRootPath() {
    return rootPath;
  }
}
