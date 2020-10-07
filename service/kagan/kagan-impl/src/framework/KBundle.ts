import {Bundle, BundleState} from "../../../kagan-api/lib/framework/bundle";
import {ServiceReference} from "../../../kagan-api/lib/framework/service_reference";


export class KBundle implements Bundle {

    getBundleId(): number {
        return 0;
    }

    getLocation(): string {
        return "";
    }

    getRegisteredServices(): ServiceReference[] {
        return [];
    }

    getServicesInUse(): ServiceReference[] {
        return [];
    }

    getState(): BundleState {
        return BundleState.ACTIVE;
    }

    start(): void {
    }

    stop(): void {
    }

    uninstall(): void {
    }

    update(): void {
    }

}