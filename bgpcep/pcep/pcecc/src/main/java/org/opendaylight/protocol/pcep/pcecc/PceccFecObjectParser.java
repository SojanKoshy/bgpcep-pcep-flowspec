/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
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

import org.opendaylight.protocol.pcep.spi.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.Fec;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.FecBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.fec.fec.Ipv4AdjacencyCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.fec.fec.Ipv4NodeIdCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Object;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.ObjectHeader;




/**
 * Parser for {@link Fec}
 */
public class PceccFecObjectParser implements ObjectParser, ObjectSerializer {

    public static final int CLASS = 54; //TODO: Use value same as SVRP

    public static final int TYPE = 1;
    public static final int MIN_SIZE = 8;

    @Override
    public Fec parseObject(final ObjectHeader header, final ByteBuf bytes) throws PCEPDeserializerException {
        Preconditions.checkArgument(bytes != null && bytes.isReadable(), "Array of bytes is mandatory. Can't be null or empty.");
        if (bytes.readableBytes() < MIN_SIZE) {
            throw new PCEPDeserializerException("Wrong length of array of bytes. Passed: " + bytes.readableBytes() + "; Expected: >="
                    + MIN_SIZE + ".");
        }
        final FecBuilder builder = new FecBuilder();

        return builder.build();
    }


    @Override
    public void serializeObject(final Object object, final ByteBuf buffer) {
        Preconditions.checkArgument(object instanceof Fec, "Wrong instance of PCEPObject. Passed %s . Needed Fec Object.", object.getClass());
        final Fec fec = (Fec)object;
        final ByteBuf body = Unpooled.buffer();

        if (fec.getFec() instanceof Ipv4NodeIdCase) {
            final Ipv4NodeIdCase nodeId = (Ipv4NodeIdCase)fec.getFec();
            writeIpv4Address(nodeId.getNodeId(), body);
        }
        else if (fec.getFec() instanceof Ipv4AdjacencyCase)
        {
            final Ipv4AdjacencyCase adjacencyIp = (Ipv4AdjacencyCase) fec.getFec();
            writeIpv4Address(adjacencyIp.getLocalIpAddress(), body);
            writeIpv4Address(adjacencyIp.getRemoteIpAddress(), body);
        }

        ObjectUtil.formatSubobject(TYPE, CLASS, object.isProcessingRule(), object.isIgnore(), body, buffer);
    }

}
