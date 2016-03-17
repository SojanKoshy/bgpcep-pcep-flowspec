/*
 * Copyright (c) 2016 Huawei Technologies Co., Ltd. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.config.yang.pcep.pcecc.cfg;

import com.google.common.base.Preconditions;
import java.net.InetSocketAddress;
import org.opendaylight.protocol.pcep.PCEPCapability;
import org.opendaylight.protocol.pcep.pcecc.PcepPceccCapability;
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
        final PcepPceccCapability inner = new PcepPceccCapability(getPceccCapable(), getSBit(), getIldbBit());
        return new PceccCapabilityModuleCloseable(inner);
    }

    private static final class PceccCapabilityModuleCloseable implements PCEPCapability, AutoCloseable {
        private final PcepPceccCapability inner;

        public PceccCapabilityModuleCloseable(final PcepPceccCapability inner) {
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
