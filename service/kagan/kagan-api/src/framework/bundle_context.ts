import { ServiceRegistration } from "./service_registration";
import { Bundle } from "./bundle";
import { DataFile } from "./data_file";

export interface BundleContext {
  installBundle(location: string): Bundle;

  getBundles(): Bundle[];

  registerService<S>(
    clazz: string | string[],
    service: S,
    props: {}
  ): ServiceRegistration;

  getDataFile(): DataFile;
}
