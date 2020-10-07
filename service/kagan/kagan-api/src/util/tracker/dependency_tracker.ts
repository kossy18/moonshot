import { DependencyListener } from "./dependency_listener";
import { BundleContext } from "../../framework/bundle_context";

export class DependencyTracker<T> implements DependencyListener<T> {
  constructor(
    private context: BundleContext,
    private clazz: string,
    private readonly listener: DependencyListener<T>
  ) {
    this.clazz = clazz;
    this.context = context;
    this.listener = listener;
  }

  open(): void {
    throw Error();
  }

  close(): void {
    throw Error();
  }

  public getDependency() {
    return undefined;
  }

  public getDependencies() {
    return undefined;
  }

  public dependencyFound(dep: T): void {
    if (this.listener != null) {
      this.listener.dependencyFound(dep);
    }
  }

  public dependencyLost(): void {
    if (this.listener != null) {
      this.listener.dependencyLost();
    }
  }
}
