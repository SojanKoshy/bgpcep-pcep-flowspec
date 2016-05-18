/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.flowspec;

import java.net.InetSocketAddress;
import org.opendaylight.protocol.pcep.PCEPCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Tlvs5;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Tlvs5Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.capability.tlv.PceFlowspecCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.TlvsBuilder;


public class FlowspecCapability implements PCEPCapability {


    private final boolean isFlowspecCapable;
    private final boolean isICapable;
    private final boolean isDCapable;


    public FlowspecCapability(final boolean isFlowspecCapable, final boolean isICapable, final boolean isDCapable){

        this.isFlowspecCapable = isFlowspecCapable;
        this.isICapable = isICapable;
        this.isDCapable = isDCapable;
    }

    @Override
    public void setCapabilityProposal(final InetSocketAddress address, final TlvsBuilder builder) {
        if (this.isFlowspecCapable) {
            builder.addAugmentation(Tlvs5.class, new Tlvs5Builder().setPceFlowspecCapability(new PceFlowspecCapabilityBuilder()
                    .setIBit(this.isICapable).setDBit(this.isDCapable).build())
                            .build());
        }
    }

    public boolean isFlowspecCapable() {
        return this.isFlowspecCapable;
    }

    public boolean isICapable() {
        return this.isICapable;
    }

    public boolean isDCapable() {
        return this.isDCapable;
    }

}
