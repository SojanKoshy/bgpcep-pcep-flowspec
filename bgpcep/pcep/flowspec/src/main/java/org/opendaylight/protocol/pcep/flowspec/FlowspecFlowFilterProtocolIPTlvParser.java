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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.NumericOperand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.ProtocolIpCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.ProtocolIpCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.protocol.ip._case.ProtocolIps;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.protocol.ip._case.ProtocolIpsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;



/**
 * Parser for {@link ProtocolIpCase}
 */
public class FlowspecFlowFilterProtocolIPTlvParser implements TlvParser, TlvSerializer {

    public static final int TYPE = 65294;

    private static final int V4_LENGTH = 4;

    @Override
    public ProtocolIpCase parseTlv(final ByteBuf buffer) throws PCEPDeserializerException {
        if (buffer == null) {
            return null;
        }

        Preconditions.checkArgument(buffer.readableBytes() == V4_LENGTH, "Length %s does not match Source prefix tlv length.", buffer.readableBytes());
        final short length = buffer.readUnsignedByte();
        final int prefixLenInByte = length / Byte.SIZE + ((length % Byte.SIZE == 0) ? 0 : 1);
        final ProtocolIpCaseBuilder builder = new ProtocolIpCaseBuilder();


        builder.setProtocolIps(parseProtocolIp(buffer)).build();
        return builder.build();
    }

    private static List<ProtocolIps> parseProtocolIp(final ByteBuf nlri) {
        final List<ProtocolIps> ips = new ArrayList<>();
        boolean end = false;

        // we can do this as all fields will be rewritten in the cycle
        final ProtocolIpsBuilder builder = new ProtocolIpsBuilder();
        while (!end) {
            final byte b = nlri.readByte();
            final NumericOperand op = NumericOneByteOperandParser.INSTANCE.parse(b);
            builder.setOp(op);
            builder.setValue(nlri.readUnsignedByte());
            end = op.isEndOfList();
            ips.add(builder.build());
        }
        return ips;
    }

    @Override
    public void serializeTlv(final Tlv tlv, final ByteBuf buffer) {
        Preconditions.checkArgument(tlv instanceof ProtocolIpCase, "ProtocolIp is mandatory.");
        final ProtocolIpCase protocolIp = (ProtocolIpCase) tlv;
        final ByteBuf body = Unpooled.buffer();
        NumericOneByteOperandParser.INSTANCE.serialize(protocolIp.getProtocolIps(), body);
        TlvUtil.formatTlv(TYPE, body, buffer);
    }
}
