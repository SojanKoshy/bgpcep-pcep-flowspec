package org.opendaylight.controller.config.yang.pcep.flowspec.cfg;

import com.google.common.base.Preconditions;
import java.net.InetSocketAddress;
import org.opendaylight.protocol.pcep.PCEPCapability;
import org.opendaylight.protocol.pcep.flowspec.FlowspecCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.TlvsBuilder;


public class FlowspecCapabilityModule extends org.opendaylight.controller.config.yang.pcep.flowspec.cfg.AbstractFlowspecCapabilityModule {
    public FlowspecCapabilityModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public FlowspecCapabilityModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.controller.config.yang.pcep.flowspec.cfg.FlowspecCapabilityModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final FlowspecCapability inner = new FlowspecCapability(getFlowspecCapable());
        return new FlowspecCapabilityModuleCloseable(inner);
    }
    private static final class FlowspecCapabilityModuleCloseable implements PCEPCapability, AutoCloseable {
        private final FlowspecCapability inner;

        public FlowspecCapabilityModuleCloseable(final FlowspecCapability inner) {
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
