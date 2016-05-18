/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.flowspec;


import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.TlvParser;
import org.opendaylight.protocol.pcep.spi.TlvSerializer;
import org.opendaylight.protocol.pcep.spi.TlvUtil;
import org.opendaylight.protocol.util.BitArray;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.capability.tlv.PceFlowspecCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.capability.tlv.PceFlowspecCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;


public class FlowspecCapabilityTlvParser implements TlvParser, TlvSerializer {

    public static final int TYPE = 65290;
    public static final int LENGTH = 32;
    protected static final int I_FLAG_OFFSET = 31;
    protected static final int D_FLAG_OFFSET = 30;

    @Override
    public void serializeTlv(final Tlv tlv, final ByteBuf buffer) {
        final BitArray flags = new BitArray(LENGTH);

        Preconditions.checkArgument(tlv instanceof PceFlowspecCapability, "FlowspecCapability is mandatory.");
        final PceFlowspecCapability sct = (PceFlowspecCapability) tlv;
        TlvUtil.formatTlv(TYPE, Unpooled.wrappedBuffer(serializeFlags(sct).array()), buffer);
    }

    protected BitArray serializeFlags(final PceFlowspecCapability sct) {
        final BitArray flags = new BitArray(LENGTH);

        flags.set(I_FLAG_OFFSET, sct.isIBit());
        flags.set(D_FLAG_OFFSET, sct.isDBit());
        return flags;
    }

    @Override
    public Tlv parseTlv(final ByteBuf buffer) throws PCEPDeserializerException {
        final long value = 0L;
        if (buffer == null) {
            return null;
        }

        if (buffer.readableBytes() < LENGTH / Byte.SIZE) {
            throw new PCEPDeserializerException("Wrong length of array of bytes. Passed: " + buffer.readableBytes()
                    + "; Expected: >= " + LENGTH / Byte.SIZE + ".");
        }

        final PceFlowspecCapabilityBuilder sb = new PceFlowspecCapabilityBuilder();
        parseFlags(sb, buffer);
        return sb.build();
    }

    protected void parseFlags(final PceFlowspecCapabilityBuilder sb, final ByteBuf buffer) {
        final BitArray flags = BitArray.valueOf(buffer, LENGTH);
        sb.setIBit(flags.get(I_FLAG_OFFSET));
        sb.setDBit(flags.get(D_FLAG_OFFSET));
    }

}
