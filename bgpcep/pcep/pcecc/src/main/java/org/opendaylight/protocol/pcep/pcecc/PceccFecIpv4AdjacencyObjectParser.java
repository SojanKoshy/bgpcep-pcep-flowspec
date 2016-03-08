/*
 * Copyright (c) 2016 Huawei Technologies Co. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.pcecc;

import static org.opendaylight.protocol.util.ByteBufWriteUtil.writeIpv4Address;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.opendaylight.protocol.pcep.spi.ObjectParser;
import org.opendaylight.protocol.pcep.spi.ObjectSerializer;
import org.opendaylight.protocol.pcep.spi.ObjectUtil;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.util.Ipv4Util;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.Fec;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.FecBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.fec.fec.Ipv4AdjacencyCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.fec.fec.Ipv4AdjacencyCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Object;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.ObjectHeader;

/**
 * Parser for {@link Ipv4AdjacencyCase}
 */
public class PceccFecIpv4AdjacencyObjectParser implements ObjectParser, ObjectSerializer {

    public static final int CLASS = 226; //TODO

    public static final int TYPE = 3;
    public static final int MIN_SIZE = 8;

    @Override
    public Fec parseObject(final ObjectHeader header, final ByteBuf buffer) throws PCEPDeserializerException {
        Preconditions.checkArgument(buffer != null && buffer.isReadable(), "Array of bytes is mandatory. Can't be null or empty.");
        if (buffer.readableBytes() < MIN_SIZE) {
            throw new PCEPDeserializerException("Wrong length of array of bytes. Passed: " + buffer.readableBytes() + "; Expected: >="
                    + MIN_SIZE + ".");
        }
        final FecBuilder builder = new FecBuilder();
        builder.setIgnore(header.isIgnore());
        builder.setProcessingRule(header.isProcessingRule());

        final Ipv4AdjacencyCaseBuilder IpAdjacencybuilder = new Ipv4AdjacencyCaseBuilder();
        IpAdjacencybuilder.setLocalIpAddress(Ipv4Util.addressForByteBuf(buffer));
        IpAdjacencybuilder.setRemoteIpAddress(Ipv4Util.addressForByteBuf(buffer));
        builder.setFec(IpAdjacencybuilder.build());
        return builder.build();
    }

    @Override
    public void serializeObject(final Object object, final ByteBuf buffer) {
        Preconditions.checkArgument(object instanceof Ipv4AdjacencyCase, "Wrong instance of PCEPObject. Passed %s . Needed Fec IPv4 Object.", object.getClass());
        final Ipv4AdjacencyCase adjacencyIp = (Ipv4AdjacencyCase) object;
        final ByteBuf body = Unpooled.buffer();

        writeIpv4Address(adjacencyIp.getLocalIpAddress(), body);
        writeIpv4Address(adjacencyIp.getRemoteIpAddress(), body);
        ObjectUtil.formatSubobject(TYPE, CLASS, object.isProcessingRule(), object.isIgnore(), body, buffer);
    }
}
