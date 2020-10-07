export interface DependencyListener<T> {
  dependencyFound(dep: T): void;

  dependencyLost(): void;
}
