import { Bundle } from "./bundle";

export interface ServiceReference {
  getBundle(): Bundle;

  getProperty(propName: string): {};

  getPropertyKeys(): string[];
}
