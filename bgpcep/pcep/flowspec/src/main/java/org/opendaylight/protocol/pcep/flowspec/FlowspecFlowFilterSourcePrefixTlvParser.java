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
import org.opendaylight.protocol.util.ByteArray;
import org.opendaylight.protocol.util.Ipv4Util;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.SourcePrefixCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.SourcePrefixCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;



/**
 * Parser for {@link SourcePrefixCase}
 */
public class FlowspecFlowFilterSourcePrefixTlvParser implements TlvParser, TlvSerializer {

    public static final int TYPE = 65293;

    private static final int V4_LENGTH = 4;

    @Override
    public SourcePrefixCase parseTlv(final ByteBuf buffer) throws PCEPDeserializerException {
        if (buffer == null) {
            return null;
        }

        Preconditions.checkArgument(buffer.readableBytes() == V4_LENGTH, "Length %s does not match Source prefix tlv length.", buffer.readableBytes());
        final short length = buffer.readUnsignedByte();
        final int prefixLenInByte = length / Byte.SIZE + ((length % Byte.SIZE == 0) ? 0 : 1);
        final SourcePrefixCaseBuilder builder = new SourcePrefixCaseBuilder();
        builder.setSourcePrefix(Ipv4Util.prefixForBytes(ByteArray.readBytes(buffer, prefixLenInByte), length));
        return builder.build();
    }

    @Override
    public void serializeTlv(final Tlv tlv, final ByteBuf buffer) {
        Preconditions.checkArgument(tlv instanceof SourcePrefixCase, "Prefix is mandatory.");
        final SourcePrefixCase sourcePrefix = (SourcePrefixCase) tlv;
        final ByteBuf body = Unpooled.buffer();
        body.writeBytes(Ipv4Util.bytesForPrefixBegin(((SourcePrefixCase) tlv).getSourcePrefix()));
        TlvUtil.formatTlv(TYPE, body, buffer);
    }
}
