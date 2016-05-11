/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.flowspec;

import static org.opendaylight.protocol.util.ByteBufWriteUtil.writeUnsignedInt;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.TlvParser;
import org.opendaylight.protocol.pcep.spi.TlvSerializer;
import org.opendaylight.protocol.pcep.spi.TlvUtil;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.capability.tlv.PceFlowspecCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.capability.tlv.PceFlowspecCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;


public class FlowspecCapabilityTlvParser implements TlvParser, TlvSerializer {

    public static final int TYPE = 65290;
    public static final int LENGTH = 32;

    @Override
    public void serializeTlv(final Tlv tlv, final ByteBuf buffer) {
        final long value = 0L;
        Preconditions.checkArgument(tlv instanceof PceFlowspecCapability, "FlowspecCapability is mandatory.");
        final PceFlowspecCapability sct = (PceFlowspecCapability) tlv;
        final ByteBuf body = Unpooled.buffer();
        writeUnsignedInt(value, body);
        TlvUtil.formatTlv(TYPE, body, buffer);
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
        sb.setValue(value);
        return sb.build();
    }

}
