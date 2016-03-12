/*
 * Copyright (c) 2016 Huawei Technologies Co. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.pcecc;

import io.netty.buffer.ByteBuf;


import org.opendaylight.protocol.pcep.spi.TlvRegistry;
import org.opendaylight.protocol.pcep.spi.VendorInformationTlvRegistry;

import org.opendaylight.protocol.pcep.sync.optimizations.SyncOptimizationsOpenObjectParser;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pcecc.capability.tlv.PceccCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.Tlvs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.TlvsBuilder;

public class PcepOpenObjectWithPceccTlvParser extends SyncOptimizationsOpenObjectParser {

    public PcepOpenObjectWithPceccTlvParser(final TlvRegistry tlvReg, final VendorInformationTlvRegistry viTlvReg) {
        super(tlvReg, viTlvReg);
    }

    @Override
    public void addTlv(final TlvsBuilder tbuilder, final Tlv tlv) {
        super.addTlv(tbuilder, tlv);
        final Tlvs1Builder tlvBuilder = new Tlvs1Builder();
        if (tbuilder.getAugmentation(Tlvs1.class) != null) {
            final Tlvs1 tlvs = tbuilder.getAugmentation(Tlvs1.class);
            if (tlvs.getPceccCapability() != null) {
                tlvBuilder.setPceccCapability(tlvs.getPceccCapability());
            }
        }
        if (tlv instanceof PceccCapability) {
            tlvBuilder.setPceccCapability((PceccCapability) tlv);
        }
        tbuilder.addAugmentation(Tlvs1.class, tlvBuilder.build());
    }

    @Override
    public void serializeTlvs(final Tlvs tlvs, final ByteBuf body) {
        if (tlvs == null) {
            return;
        }
        super.serializeTlvs(tlvs, body);
        if (tlvs.getAugmentation(Tlvs1.class) != null) {
            final Tlvs1 spcTlvs = tlvs.getAugmentation(Tlvs1.class);
            if (spcTlvs.getPceccCapability() != null) {
                serializeTlv(spcTlvs.getPceccCapability(), body);
            }
        }
    }
}
