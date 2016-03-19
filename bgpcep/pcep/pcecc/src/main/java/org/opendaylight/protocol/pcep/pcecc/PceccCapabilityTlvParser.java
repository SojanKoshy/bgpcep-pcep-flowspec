/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.pcecc;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.TlvParser;
import org.opendaylight.protocol.pcep.spi.TlvSerializer;
import org.opendaylight.protocol.pcep.spi.TlvUtil;
import org.opendaylight.protocol.util.BitArray;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pcecc.capability.tlv.PceccCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pcecc.capability.tlv.PceccCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;

public class PceccCapabilityTlvParser implements TlvParser, TlvSerializer {

    public static final int TYPE = 65287;

    protected static final int FLAGS_LENGTH = 32;
    protected static final int S_FLAG_OFFSET = 31;
    protected static final int ILDB_FLAG_OFFSET = 30;

    @Override
    public void serializeTlv(final Tlv tlv, final ByteBuf buffer) {
        Preconditions.checkArgument(tlv instanceof PceccCapability, "PceccCapability is mandatory.");
        final PceccCapability sct = (PceccCapability) tlv;

        TlvUtil.formatTlv(TYPE, Unpooled.wrappedBuffer(serializeFlags(sct).array()), buffer);
    }

    protected BitArray serializeFlags(final PceccCapability sct) {
        final BitArray flags = new BitArray(FLAGS_LENGTH);

        flags.set(S_FLAG_OFFSET, sct.isSBit());
        flags.set(ILDB_FLAG_OFFSET, sct.isILDBBit());
        return flags;
    }

    @Override
    public Tlv parseTlv(final ByteBuf buffer) throws PCEPDeserializerException {
        if (buffer == null) {
            return null;
        }
        if (buffer.readableBytes() < FLAGS_LENGTH / Byte.SIZE) {
            throw new PCEPDeserializerException("Wrong length of array of bytes. Passed: " + buffer.readableBytes()
                    + "; Expected: >= " + FLAGS_LENGTH / Byte.SIZE + ".");
        }
        final PceccCapabilityBuilder sb = new PceccCapabilityBuilder();
        parseFlags(sb, buffer);
        return sb.build();
    }

    protected void parseFlags(final PceccCapabilityBuilder sb, final ByteBuf buffer) {
        final BitArray flags = BitArray.valueOf(buffer, FLAGS_LENGTH);
        sb.setSBit(flags.get(S_FLAG_OFFSET));
        sb.setILDBBit(flags.get(ILDB_FLAG_OFFSET));
    }

}
