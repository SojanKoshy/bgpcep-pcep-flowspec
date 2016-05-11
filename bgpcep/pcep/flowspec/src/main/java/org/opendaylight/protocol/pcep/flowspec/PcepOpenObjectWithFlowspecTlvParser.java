/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.flowspec;

import io.netty.buffer.ByteBuf;


import org.opendaylight.protocol.pcep.spi.TlvRegistry;
import org.opendaylight.protocol.pcep.spi.VendorInformationTlvRegistry;

import org.opendaylight.protocol.pcep.sync.optimizations.SyncOptimizationsOpenObjectParser;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Tlvs5;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Tlvs5Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.capability.tlv.PceFlowspecCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.Tlvs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.TlvsBuilder;

public class PcepOpenObjectWithFlowspecTlvParser extends SyncOptimizationsOpenObjectParser {

    public PcepOpenObjectWithFlowspecTlvParser(final TlvRegistry tlvReg, final VendorInformationTlvRegistry viTlvReg) {
        super(tlvReg, viTlvReg);
    }

    @Override
    public void addTlv(final TlvsBuilder tbuilder, final Tlv tlv) {
        super.addTlv(tbuilder, tlv);
        final Tlvs5Builder tlvBuilder = new Tlvs5Builder();
        if (tbuilder.getAugmentation(Tlvs5.class) != null) {
            final Tlvs5 tlvs = tbuilder.getAugmentation(Tlvs5.class);
            if (tlvs.getPceFlowspecCapability() != null) {
                tlvBuilder.setPceFlowspecCapability(tlvs.getPceFlowspecCapability());
            }
        }
        if (tlv instanceof PceFlowspecCapability) {
            tlvBuilder.setPceFlowspecCapability((PceFlowspecCapability) tlv);
        }
        tbuilder.addAugmentation(Tlvs5.class, tlvBuilder.build());
    }

    @Override
    public void serializeTlvs(final Tlvs tlvs, final ByteBuf body) {
        if (tlvs == null) {
            return;
        }
        super.serializeTlvs(tlvs, body);
        if (tlvs.getAugmentation(Tlvs5.class) != null) {
            final Tlvs5 spcTlvs = tlvs.getAugmentation(Tlvs5.class);
            if (spcTlvs.getPceFlowspecCapability() != null) {
                serializeTlv(spcTlvs.getPceFlowspecCapability(), body);
            }
        }
    }
}
