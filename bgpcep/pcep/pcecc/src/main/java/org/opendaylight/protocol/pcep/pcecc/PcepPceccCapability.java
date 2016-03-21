/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.pcecc;

import java.net.InetSocketAddress;
import org.opendaylight.protocol.pcep.PCEPCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pcecc.capability.tlv.PceccCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.TlvsBuilder;


public class PcepPceccCapability implements PCEPCapability {


    private final boolean isPceccCapable;
    private final boolean isSCapable;
    private final boolean isIldbCapable;

    public PcepPceccCapability(final boolean isPceccCapable, final boolean isSCapable, final boolean isIldbCapable){
        this.isPceccCapable = isPceccCapable;
        this.isSCapable = isSCapable;
        this.isIldbCapable = isIldbCapable;
    }

    @Override
    public void setCapabilityProposal(final InetSocketAddress address, final TlvsBuilder builder) {
        if (this.isPceccCapable) {
            builder.addAugmentation(Tlvs4.class, new Tlvs4Builder().setPceccCapability(new PceccCapabilityBuilder().
                            setSBit(this.isSCapable).setIldbBit(this.isIldbCapable).build()).build());
        }
    }

    public boolean isPceccCapable() {
        return this.isPceccCapable;
    }

    public boolean isSCapable() {
        return this.isSCapable;
    }

    public boolean isILDBCapable() {
        return this.isIldbCapable;
    }

}
