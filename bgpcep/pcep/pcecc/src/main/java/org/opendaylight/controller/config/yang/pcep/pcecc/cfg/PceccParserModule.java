package org.opendaylight.controller.config.yang.pcep.pcecc.cfg;


import org.opendaylight.protocol.pcep.pcecc.PceccActivator;

public class PceccParserModule extends org.opendaylight.controller.config.yang.pcep.pcecc.cfg.AbstractPceccParserModule {
    public PceccParserModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public PceccParserModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.controller.config.yang.pcep.pcecc.cfg.PceccParserModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        // TODO:implement
        return new PceccActivator();
    }

}
