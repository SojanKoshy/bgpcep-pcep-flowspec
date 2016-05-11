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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.DestinationPrefixCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.DestinationPrefixCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;



/**
 * Parser for {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.DestinationPrefixCase}
 */
public class FlowspecFlowFilterDestinationPrefixTlvParser implements TlvParser, TlvSerializer {

    public static final int TYPE = 65291;

    private static final int V4_LENGTH = 4;

    @Override
    public DestinationPrefixCase parseTlv(final ByteBuf buffer) throws PCEPDeserializerException {
        if (buffer == null) {
            return null;
        }

        Preconditions.checkArgument(buffer.readableBytes() == V4_LENGTH, "Length %s does not match Destination prefix tlv length.", buffer.readableBytes());
        final short length = buffer.readUnsignedByte();
        final int prefixLenInByte = length / Byte.SIZE + ((length % Byte.SIZE == 0) ? 0 : 1);
        final DestinationPrefixCaseBuilder builder = new DestinationPrefixCaseBuilder();
        builder.setDestinationPrefix(Ipv4Util.prefixForBytes(ByteArray.readBytes(buffer, prefixLenInByte), length));
        return builder.build();
    }

    @Override
    public void serializeTlv(final Tlv tlv, final ByteBuf buffer) {
        Preconditions.checkArgument(tlv instanceof DestinationPrefixCase, "Prefix is mandatory.");
        final DestinationPrefixCase destinationPrefix = (DestinationPrefixCase) tlv;
        final ByteBuf body = Unpooled.buffer();
        body.writeBytes(Ipv4Util.bytesForPrefixBegin(((DestinationPrefixCase) tlv).getDestinationPrefix()));
        TlvUtil.formatTlv(TYPE, body, buffer);
    }
}
