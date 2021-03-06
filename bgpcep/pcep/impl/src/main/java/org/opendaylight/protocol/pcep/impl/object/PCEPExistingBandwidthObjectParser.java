/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.impl.object;

import static org.opendaylight.protocol.util.ByteBufWriteUtil.writeFloat32;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.opendaylight.protocol.pcep.spi.ObjectParser;
import org.opendaylight.protocol.pcep.spi.ObjectSerializer;
import org.opendaylight.protocol.pcep.spi.ObjectUtil;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.util.ByteArray;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.ObjectHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.reoptimization.bandwidth.object.Bandwidth;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.reoptimization.bandwidth.object.BandwidthBuilder;

/**
 * Parser for Bandwidth
 */
public class PCEPExistingBandwidthObjectParser implements ObjectParser, ObjectSerializer {

    public static final int CLASS = 5;

    public static final int TYPE = 2;

    @Override
    public Bandwidth parseObject(final ObjectHeader header, final ByteBuf bytes) throws PCEPDeserializerException {
        Preconditions.checkArgument(bytes != null && bytes.isReadable(), "Array of bytes is mandatory. Can't be null or empty.");
        if (bytes.readableBytes() != PCEPBandwidthObjectParser.BANDWIDTH_F_LENGTH) {
            throw new PCEPDeserializerException("Wrong length of array of bytes. Passed: " + bytes.readableBytes() + "; Expected: "
                + PCEPBandwidthObjectParser.BANDWIDTH_F_LENGTH + ".");
        }
        final BandwidthBuilder builder = new BandwidthBuilder();
        builder.setIgnore(header.isIgnore());
        builder.setProcessingRule(header.isProcessingRule());
        builder.setBandwidth(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.network.concepts.rev131125.Bandwidth(ByteArray.getAllBytes(bytes)));
        return builder.build();
    }

    @Override
    public void serializeObject(final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Object object, final ByteBuf buffer) {
        Preconditions.checkArgument(object instanceof Bandwidth, "Wrong instance of PCEPObject. Passed " +
            "%s. Needed ReoptimizationBandwidthObject.", object.getClass());
        final ByteBuf body = Unpooled.buffer();
        writeFloat32(((Bandwidth) object).getBandwidth(), body);
        ObjectUtil.formatSubobject(TYPE, CLASS, object.isProcessingRule(), object.isIgnore(), body, buffer);
    }
}
