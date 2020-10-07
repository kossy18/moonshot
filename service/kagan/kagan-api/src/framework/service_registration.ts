import { ServiceReference } from "./service_reference";

export interface ServiceRegistration {
  getReferences(): ServiceReference;

  setProperties(prop: {}): void;

  unregister(): void;
}
