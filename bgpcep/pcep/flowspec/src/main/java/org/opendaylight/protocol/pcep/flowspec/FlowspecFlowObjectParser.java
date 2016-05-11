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
import java.util.List;


import org.opendaylight.protocol.pcep.spi.AbstractObjectWithTlvsParser;
import org.opendaylight.protocol.pcep.spi.ObjectUtil;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.TlvRegistry;
import org.opendaylight.protocol.pcep.spi.VendorInformationTlvRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.FsIdNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.flow.flowspec.filter.type.FlowspecFilterV4Case;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.flow.flowspec.filter.type.flowspec.filter.v4._case.TlvsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.Flowspec;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.DestinationPortCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.DestinationPrefixCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.DscpCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.FragmentCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.IcmpCodeCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.IcmpTypeCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.PacketLengthCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.PortCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.ProtocolIpCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.SourcePortCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.SourcePrefixCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.TcpFlagsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Object;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.ObjectHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.vendor.information.tlvs.VendorInformationTlv;




/**
 * Parser for {@link Flow}
 */
public class FlowspecFlowObjectParser extends AbstractObjectWithTlvsParser<TlvsBuilder> {

    public static final int CLASS = 228; //TODO: Use value same as SVRP

    public static final int IPV4_CASE_TYPE = 1;
    public static final int IPV6_CASE_TYPE = 2;
    public static final int MIN_SIZE = 4;

    public FlowspecFlowObjectParser(final TlvRegistry tlvReg, final VendorInformationTlvRegistry viTlvReg) {
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
        return builder.build();
    }


    @Override
    public void serializeObject(final Object object, final ByteBuf buffer) {
        Preconditions.checkArgument(object instanceof Flow, "Wrong instance of PCEPObject. Passed %s . Needed Flow Object.", object.getClass());
        final Flow flow = (Flow)object;
        final ByteBuf body = Unpooled.buffer();

        final FsIdNumber fsId = flow.getFsId();
        Preconditions.checkArgument(fsId != null, "Fs-Id is mandatory.");
        writeUnsignedInt(fsId.getValue(), body);



        if (flow.getFlowspecFilterType() instanceof FlowspecFilterV4Case) {
            final FlowspecFilterV4Case flowFilterv4 = (FlowspecFilterV4Case)flow.getFlowspecFilterType();

            final List<Flowspec> flowspeclst = flowFilterv4.getTlvs().getFlowspec();

            for (final Flowspec flowspec : flowspeclst) {
                serializev4Tlvs(flowspec, body);
                serializeTlvs(flowspec, body);
            }
        }
        /*else if (flow.getFlowspecFilterType() instanceof FlowspecFilterV6Case)
        {
            final FlowspecFilterV6Case flowFilterv6 = (FlowspecFilterV6Case)flow.getFlowspecFilterType();

            //TODO
            ObjectUtil.formatSubobject(IPV6_CASE_TYPE, CLASS, object.isProcessingRule(), object.isIgnore(), body, buffer);
        }*/

        ObjectUtil.formatSubobject(IPV4_CASE_TYPE, CLASS, object.isProcessingRule(), object.isIgnore(), body, buffer);

    }


    public void serializev4Tlvs(final Flowspec flowspectlv, final ByteBuf body) {
        if (flowspectlv == null) {
            return;
        }

        if (flowspectlv.getFlowspecType() instanceof DestinationPrefixCase) {
            final DestinationPrefixCase destinationPrefixCase
                    = (DestinationPrefixCase)flowspectlv.getFlowspecType();
            serializeTlv(destinationPrefixCase, body);
        }

        if (flowspectlv.getFlowspecType() instanceof SourcePrefixCase) {
            final SourcePrefixCase sourcePrefixCase
                    = (SourcePrefixCase)flowspectlv.getFlowspecType();
            serializeTlv(sourcePrefixCase, body);
        }

        if (flowspectlv.getFlowspecType() instanceof ProtocolIpCase) {
            final ProtocolIpCase protocolIpCase
                    = (ProtocolIpCase)flowspectlv.getFlowspecType();
            serializeTlv(protocolIpCase, body);
        }

        if (flowspectlv.getFlowspecType() instanceof FragmentCase) {
            final FragmentCase fragmentCase
                    = (FragmentCase)flowspectlv.getFlowspecType();
            serializeTlv(fragmentCase, body);
        }
    }

    public void serializeTlvs(final Flowspec flowspectlv, final ByteBuf body) {
        if (flowspectlv == null) {
            return;
        }

        if (flowspectlv.getFlowspecType() instanceof PortCase) {
            final PortCase portCase
                    = (PortCase)flowspectlv.getFlowspecType();
            serializeTlv(portCase, body);
        }

        if (flowspectlv.getFlowspecType() instanceof DestinationPortCase) {
            final DestinationPortCase destinationPortCase
                    = (DestinationPortCase)flowspectlv.getFlowspecType();
            serializeTlv(destinationPortCase, body);
        }

        if (flowspectlv.getFlowspecType() instanceof SourcePortCase) {
            final SourcePortCase sourcePortCase
                    = (SourcePortCase)flowspectlv.getFlowspecType();
            serializeTlv(sourcePortCase, body);
        }

        if (flowspectlv.getFlowspecType() instanceof IcmpTypeCase) {
            final IcmpTypeCase icmpTypeCase
                    = (IcmpTypeCase)flowspectlv.getFlowspecType();
            serializeTlv(icmpTypeCase, body);
        }

        if (flowspectlv.getFlowspecType() instanceof IcmpCodeCase) {
            final IcmpCodeCase icmpCodeCase
                    = (IcmpCodeCase)flowspectlv.getFlowspecType();
            serializeTlv(icmpCodeCase, body);
        }

        if (flowspectlv.getFlowspecType() instanceof PacketLengthCase) {
            final PacketLengthCase packetLengthCase
                    = (PacketLengthCase)flowspectlv.getFlowspecType();
            serializeTlv(packetLengthCase, body);
        }

        if (flowspectlv.getFlowspecType() instanceof TcpFlagsCase) {
            final TcpFlagsCase tcpFlagsCase
                    = (TcpFlagsCase)flowspectlv.getFlowspecType();
            serializeTlv(tcpFlagsCase, body);
        }

        if (flowspectlv.getFlowspecType() instanceof DscpCase) {
            final DscpCase dscpCase
                    = (DscpCase)flowspectlv.getFlowspecType();
            serializeTlv(dscpCase, body);
        }
    }

    @Override
    protected final void addVendorInformationTlvs(final TlvsBuilder builder, final List<VendorInformationTlv> tlvs) {
        if (!tlvs.isEmpty()) {
            //builder.setVendorInformationTlv(tlvs);
        }
    }
}
