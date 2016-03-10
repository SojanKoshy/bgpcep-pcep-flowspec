package org.opendaylight.controller.config.yang.pcep.pcecc.cfg;

import com.google.common.base.Preconditions;
import java.net.InetSocketAddress;
import org.opendaylight.protocol.pcep.PCEPCapability;
import org.opendaylight.protocol.pcep.pcecc.PceCCCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.TlvsBuilder;


public class PceccCapabilityModule extends org.opendaylight.controller.config.yang.pcep.pcecc.cfg.AbstractPceccCapabilityModule {
    public PceccCapabilityModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public PceccCapabilityModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.controller.config.yang.pcep.pcecc.cfg.PceccCapabilityModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final PceCCCapability inner = new PceCCCapability(getPceccCapable());
        return new PceccCapabilityModuleCloseable(inner);
    }
    private static final class PceccCapabilityModuleCloseable implements PCEPCapability, AutoCloseable {
        private final PceCCCapability inner;

        public PceccCapabilityModuleCloseable(final PceCCCapability inner) {
            this.inner = Preconditions.checkNotNull(inner);
        }

        @Override
        public void close() {
        }

        @Override
        public void setCapabilityProposal(final InetSocketAddress address, final TlvsBuilder builder) {
            this.inner.setCapabilityProposal(address, builder);
        }

    }

}