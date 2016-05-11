package org.opendaylight.controller.config.yang.pcep.flowspec.cfg;


import org.opendaylight.protocol.pcep.flowspec.FlowspecActivator;

public class FlowspecModule extends org.opendaylight.controller.config.yang.pcep.flowspec.cfg.AbstractFlowspecModule {
    public FlowspecModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public FlowspecModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.controller.config.yang.pcep.flowspec.cfg.FlowspecModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        return new FlowspecActivator();
    }

}
