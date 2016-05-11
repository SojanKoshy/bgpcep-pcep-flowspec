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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Dscp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.NumericOperand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.DscpCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.DscpCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.dscp._case.Dscps;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.dscp._case.DscpsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;



/**
 * Parser for {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.DscpCase}
 */
public class FlowspecFlowFilterDSCPTlvParser implements TlvParser, TlvSerializer {

    public static final int TYPE = 65301;

    private static final int V4_LENGTH = 4;

    @Override
    public DscpCase parseTlv(final ByteBuf buffer) throws PCEPDeserializerException {
        if (buffer == null) {
            return null;
        }

        Preconditions.checkArgument(buffer.readableBytes() == V4_LENGTH, "Length %s does not match DSCP tlv length.", buffer.readableBytes());
        final short length = buffer.readUnsignedByte();
        final int prefixLenInByte = length / Byte.SIZE + ((length % Byte.SIZE == 0) ? 0 : 1);
        final DscpCaseBuilder builder = new DscpCaseBuilder();

        builder.setDscps(parseDscp(buffer)).build();
        return builder.build();
    }

    private static List<Dscps> parseDscp(final ByteBuf buffer) {
        final List<Dscps> dscps = new ArrayList<>();
        boolean end = false;
        // we can do this as all fields will be rewritten in the cycle
        final DscpsBuilder builder = new DscpsBuilder();
        while (!end) {
            final byte b = buffer.readByte();
            // RFC does not specify operator
            final NumericOperand op = NumericOneByteOperandParser.INSTANCE.parse(b);
            builder.setOp(op);
            builder.setValue(new Dscp(buffer.readUnsignedByte()));
            end = op.isEndOfList();
            dscps.add(builder.build());
        }
        return dscps;
    }

    @Override
    public void serializeTlv(final Tlv tlv, final ByteBuf buffer) {
        Preconditions.checkArgument(tlv instanceof DscpCase, "DSCP is mandatory.");
        final DscpCase dscp = (DscpCase) tlv;
        final ByteBuf body = Unpooled.buffer();
        serializeDscps(dscp.getDscps(), body);
        TlvUtil.formatTlv(TYPE, body, buffer);
    }

    protected static final void serializeDscps(final List<Dscps> dscps, final ByteBuf byteBuf) {
        for (final Dscps dscp : dscps) {
            NumericOneByteOperandParser.INSTANCE.serialize(dscp.getOp(), 1, byteBuf);
            Util.writeShortest(dscp.getValue().getValue(), byteBuf);
        }
    }
}
