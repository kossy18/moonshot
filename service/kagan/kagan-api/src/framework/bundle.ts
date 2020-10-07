import { ServiceReference } from "./service_reference";

export enum BundleState {
  UNINSTALLED = 1,
  INSTALLED = 2,
  RESOLVED = 3,
  STARTING = 4,
  STOPPING = 5,
  ACTIVE = 6,
}

export interface Bundle {
  getState(): BundleState;

  start(): void;

  stop(): void;

  update(): void;

  uninstall(): void;

  getBundleId(): number;

  getLocation(): string;

  getRegisteredServices(): ServiceReference[];

  getServicesInUse(): ServiceReference[];
}
