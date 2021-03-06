/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.flowspec;

import io.netty.buffer.ByteBuf;
import org.opendaylight.protocol.pcep.ietf.initiated00.CInitiated00SrpObjectParser;
import org.opendaylight.protocol.pcep.spi.TlvRegistry;
import org.opendaylight.protocol.pcep.spi.VendorInformationTlvRegistry;
import org.opendaylight.protocol.util.BitArray;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Srp1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Srp1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.Srp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.SrpBuilder;

/**
 * Parser for {@link Srp}
 */
public class FlowspecSrpObjectParser extends CInitiated00SrpObjectParser {

    private static final int REMOVE_FLAG = 31;
    private static final int SYNC_FLAG = 30;

    public FlowspecSrpObjectParser(final TlvRegistry tlvReg, final VendorInformationTlvRegistry viTlvReg) {
        super(tlvReg, viTlvReg);
    }

    @Override
    protected void parseFlags(final SrpBuilder builder, final ByteBuf bytes) {
        final BitArray flags = BitArray.valueOf(bytes, FLAGS_SIZE);
        builder.addAugmentation(Srp1.class, new Srp1Builder().setSync(flags.get(SYNC_FLAG)).build());
    }

    @Override
    protected void serializeFlags(final Srp srp, final ByteBuf body) {
        final BitArray flags = new BitArray(FLAGS_SIZE);

        if (srp.getAugmentation(Srp1.class) != null) {
            flags.set(SYNC_FLAG, srp.getAugmentation(Srp1.class).isSync());
        }

        if (srp.getAugmentation(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.crabbe.initiated.rev131126.Srp1.class) != null) {
            flags.set(REMOVE_FLAG, srp.getAugmentation(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.crabbe.initiated.rev131126.Srp1.class).isRemove());
        }

        flags.toByteBuf(body);
    }
}
