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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.BitmaskOperand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.TcpFlagsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.TcpFlagsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.tcp.flags._case.TcpFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.tcp.flags._case.TcpFlagsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;



/**
 * Parser for {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.IcmpCodeCase}
 */
public class FlowspecFlowFilterTCPflagTlvParser implements TlvParser, TlvSerializer {

    public static final int TYPE = 65299;

    private static final int V4_LENGTH = 4;

    @Override
    public TcpFlagsCase parseTlv(final ByteBuf buffer) throws PCEPDeserializerException {
        if (buffer == null) {
            return null;
        }

        Preconditions.checkArgument(buffer.readableBytes() == V4_LENGTH, "Length %s does not match TCP Flags tlv length.", buffer.readableBytes());
        final short length = buffer.readUnsignedByte();
        final int prefixLenInByte = length / Byte.SIZE + ((length % Byte.SIZE == 0) ? 0 : 1);
        final TcpFlagsCaseBuilder builder = new TcpFlagsCaseBuilder();

        builder.setTcpFlags(parseFlags(buffer)).build();
        return builder.build();
    }

    private static List<TcpFlags> parseFlags(final ByteBuf nlri) {
        final List<TcpFlags> flags = new ArrayList<>();
        boolean end = false;
        // we can do this as all fields will be rewritten in the cycle
        final TcpFlagsBuilder builder = new TcpFlagsBuilder();
        while (!end) {
            final byte b = nlri.readByte();
            final BitmaskOperand op = BitmaskOperandParser.INSTANCE.parse(b);
            builder.setOp(op);
            final short length = AbstractOperandParser.parseLength(b);
            builder.setValue(ByteArray.bytesToInt(ByteArray.readBytes(nlri, length)));
            end = op.isEndOfList();
            flags.add(builder.build());
        }
        return flags;
    }

    @Override
    public void serializeTlv(final Tlv tlv, final ByteBuf buffer) {
        Preconditions.checkArgument(tlv instanceof TcpFlagsCase, "TCP Flags is mandatory.");
        final TcpFlagsCase flags = (TcpFlagsCase) tlv;
        final ByteBuf body = Unpooled.buffer();
        serializeTcpFlags(flags.getTcpFlags(), body);
        TlvUtil.formatTlv(TYPE, body, buffer);
    }

    protected static final void serializeTcpFlags(final List<TcpFlags> flags, final ByteBuf byteBuf) {
        for (final TcpFlags flag : flags) {
            final ByteBuf flagsBuf = Unpooled.buffer();
            Util.writeShortest(flag.getValue(), flagsBuf);
            BitmaskOperandParser.INSTANCE.serialize(flag.getOp(), flagsBuf.readableBytes(), byteBuf);
            byteBuf.writeBytes(flagsBuf);
        }
    }
}
