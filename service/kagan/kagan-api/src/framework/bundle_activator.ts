import { BundleContext } from "./bundle_context";

export interface BundleActivator {
  start(context: BundleContext): void;

  stop(context: BundleContext): void;
}
