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

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.protocol.pcep.spi.AbstractObjectWithTlvsParser;
import org.opendaylight.protocol.pcep.spi.ObjectUtil;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.TlvRegistry;
import org.opendaylight.protocol.pcep.spi.VendorInformationTlvRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.FsIdNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.flow.flowspec.filter.type.FlowspecFilterV4CaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.flow.flowspec.filter.type.flowspec.filter.v4._case.TlvsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.Flowspec;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.FlowspecType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.DestinationPrefixCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Object;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.ObjectHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.vendor.information.tlvs.VendorInformationTlv;


/**
 * Parser for {@link Flow}
 */
public class FlowspecFlowObjectV4Parser extends AbstractObjectWithTlvsParser<TlvsBuilder> {

    public static final int CLASS = 228;

    public static final int TYPE = 1;
    public static final int MIN_SIZE = 4;

    public static List<Flowspec> flowspeclst = new ArrayList<>();

    public FlowspecFlowObjectV4Parser(final TlvRegistry tlvReg, final VendorInformationTlvRegistry viTlvReg) {
        super(tlvReg, viTlvReg);
    }
    @Override
    public Flow parseObject(final ObjectHeader header, final ByteBuf bytes) throws PCEPDeserializerException {
        Preconditions.checkArgument(bytes != null && bytes.isReadable(), "Array of bytes is mandatory. Can't be null or empty.");
        if (bytes.readableBytes() < MIN_SIZE) {
            throw new PCEPDeserializerException("Wrong length of array of bytes. Passed: " + bytes.readableBytes() + "; Expected: >="
                    + MIN_SIZE + ".");
        }
        final FlowBuilder builder = new FlowBuilder();

        builder.setIgnore(header.isIgnore());
        builder.setProcessingRule(header.isProcessingRule());
        builder.setFsId(new FsIdNumber(bytes.readUnsignedInt()));

        final FlowspecFilterV4CaseBuilder flowspecFilterv4builder = new FlowspecFilterV4CaseBuilder();

        final TlvsBuilder tlvsBuilder = new TlvsBuilder();
        parseTlvs(tlvsBuilder, bytes.slice());
        flowspecFilterv4builder.setTlvs(tlvsBuilder.build());
        builder.setFlowspecFilterType(flowspecFilterv4builder.build());
        return builder.build();
    }

    @Override
    public void addTlv(final TlvsBuilder builder, final Tlv tlv) {

        //final list<flowspec> flowspec =


        if (tlv instanceof DestinationPrefixCase) {
            final FlowspecType flowspecType= (FlowspecType) tlv ;
            flowspeclst.add((Flowspec)flowspecType);
            builder.setFlowspec(flowspeclst);

        }
    }

    @Override
    public void serializeObject(final Object object, final ByteBuf buffer) {
        Preconditions.checkArgument(object instanceof Flow, "Wrong instance of PCEPObject. Passed %s . Needed Flow Object.", object.getClass());
        final Flow flow = (Flow)object;
        final ByteBuf body = Unpooled.buffer();

        final FsIdNumber fsId = flow.getFsId();
        Preconditions.checkArgument(fsId != null, "Fs-Id is mandatory.");
        writeUnsignedInt(fsId.getValue(), body);

        ObjectUtil.formatSubobject(TYPE, CLASS, object.isProcessingRule(), object.isIgnore(), body, buffer);

    }


    @Override
    protected final void addVendorInformationTlvs(final TlvsBuilder builder, final List<VendorInformationTlv> tlvs) {
        if (!tlvs.isEmpty()) {
            //builder.setVendorInformationTlv(tlvs);
        }
    }

}
