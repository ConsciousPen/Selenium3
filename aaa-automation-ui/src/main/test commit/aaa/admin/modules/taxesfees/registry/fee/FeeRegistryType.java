
package aaa.admin.modules.taxesfees.registry.fee;

import aaa.admin.modules.taxesfees.registry.IRegistry;

public enum FeeRegistryType {
    FEE("Fee", new FeeRegistry());

    private String registryType;
    private IRegistry registry;

    FeeRegistryType(String registryType, IRegistry registry) {
        this.registryType = registryType;
        this.registry = registry;
    }

    public IRegistry get() {
        return registry;
    }

    public String getType() {
        return registryType;
    }
}
