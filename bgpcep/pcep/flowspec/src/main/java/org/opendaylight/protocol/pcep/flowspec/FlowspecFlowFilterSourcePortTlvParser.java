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
import java.util.ArrayList;
import java.util.List;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.TlvParser;
import org.opendaylight.protocol.pcep.spi.TlvSerializer;
import org.opendaylight.protocol.pcep.spi.TlvUtil;
import org.opendaylight.protocol.util.ByteArray;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.NumericOperand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.SourcePortCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.SourcePortCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.source.port._case.SourcePorts;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.source.port._case.SourcePortsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;



/**
 * Parser for {@link SourcePortCase}
 */
public class FlowspecFlowFilterSourcePortTlvParser implements TlvParser, TlvSerializer {

    public static final int TYPE = 65296;
    private static final int V4_LENGTH = 4;

    @Override
    public SourcePortCase parseTlv(final ByteBuf buffer) throws PCEPDeserializerException {
        if (buffer == null) {
            return null;
        }

        Preconditions.checkArgument(buffer.readableBytes() == V4_LENGTH, "Length %s does not match Source Port tlv length.", buffer.readableBytes());
        final short length = buffer.readUnsignedByte();
        final int prefixLenInByte = length / Byte.SIZE + ((length % Byte.SIZE == 0) ? 0 : 1);
        final SourcePortCaseBuilder builder = new SourcePortCaseBuilder();

        builder.setSourcePorts(parseSourcePorts(buffer)).build();
        return builder.build();
    }

    private static List<SourcePorts> parseSourcePorts(final ByteBuf nlri) {
        final List<SourcePorts> ports = new ArrayList<>();
        boolean end = false;
        // we can do this as all fields will be rewritten in the cycle
        final SourcePortsBuilder builder = new SourcePortsBuilder();
        while (!end) {
            final byte b = nlri.readByte();
            final NumericOperand op = NumericOneByteOperandParser.INSTANCE.parse(b);
            builder.setOp(op);
            final short length = AbstractOperandParser.parseLength(b);
            builder.setValue(ByteArray.bytesToInt(ByteArray.readBytes(nlri, length)));
            end = op.isEndOfList();
            ports.add(builder.build());
        }
        return ports;
    }

    @Override
    public void serializeTlv(final Tlv tlv, final ByteBuf buffer) {
        Preconditions.checkArgument(tlv instanceof SourcePortCase, "Port is mandatory.");
        final SourcePortCase port = (SourcePortCase) tlv;
        final ByteBuf body = Unpooled.buffer();
        NumericTwoByteOperandParser.INSTANCE.serialize(port.getSourcePorts(), body);
        TlvUtil.formatTlv(TYPE, body, buffer);
    }
}
