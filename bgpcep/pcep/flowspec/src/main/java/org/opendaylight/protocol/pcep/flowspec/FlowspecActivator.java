/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.flowspec;

import com.google.common.collect.Lists;
import java.util.List;

import org.opendaylight.protocol.pcep.ietf.stateful07.Stateful07LSPIdentifierIpv4TlvParser;
import org.opendaylight.protocol.pcep.ietf.stateful07.Stateful07LspSymbolicNameTlvParser;
import org.opendaylight.protocol.pcep.spi.ObjectRegistry;
import org.opendaylight.protocol.pcep.spi.PCEPExtensionProviderContext;
import org.opendaylight.protocol.pcep.spi.TlvRegistry;
import org.opendaylight.protocol.pcep.spi.VendorInformationTlvRegistry;
import org.opendaylight.protocol.pcep.spi.pojo.AbstractPCEPExtensionProviderActivator;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Flowspec;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.action.object.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.Flow;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.capability.tlv.PceFlowspecCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.LspIdentifiers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.Srp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.symbolic.path.name.tlv.SymbolicPathName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.Open;


public class FlowspecActivator extends AbstractPCEPExtensionProviderActivator {

    @Override
    protected List<AutoCloseable> startImpl(final PCEPExtensionProviderContext context) {
        final List<AutoCloseable> regs = Lists.newArrayList();

        final ObjectRegistry objReg = context.getObjectHandlerRegistry();

        regs.add(context.registerMessageParser(FlowspecMessageParser.TYPE,
                new FlowspecMessageParser(objReg)));
        regs.add(context.registerMessageSerializer(Flowspec.class, new FlowspecMessageParser(objReg)));


         /* Tlvs */
        regs.add(context.registerTlvParser(FlowspecCapabilityTlvParser.TYPE, new FlowspecCapabilityTlvParser()));

        regs.add(context.registerTlvSerializer(PceFlowspecCapability.class, new FlowspecCapabilityTlvParser()));

        /* Objects */

        final TlvRegistry tlvReg = context.getTlvHandlerRegistry();
        final VendorInformationTlvRegistry viTlvRegistry = context.getVendorInformationTlvRegistry();

        final FlowspecFlowObjectParser flowParser = new FlowspecFlowObjectParser(tlvReg, viTlvRegistry);
        final FlowspecFlowObjectV4Parser flowV4Parser = new FlowspecFlowObjectV4Parser(tlvReg, viTlvRegistry);

        regs.add(context.registerObjectParser(FlowspecFlowObjectV4Parser.CLASS, FlowspecFlowObjectV4Parser.TYPE,
                flowV4Parser));
        regs.add(context.registerObjectSerializer(Flow.class, flowParser));

        final FlowspecActionObjectParser actionParser = new FlowspecActionObjectParser(tlvReg, viTlvRegistry);

        regs.add(context.registerObjectParser(FlowspecActionObjectParser.CLASS, FlowspecActionObjectParser.TYPE,
                actionParser));
        regs.add(context.registerObjectSerializer(Action.class, actionParser));

        regs.add(context.registerObjectParser(FlowspecSrpObjectParser.CLASS, FlowspecSrpObjectParser.TYPE,
                new FlowspecSrpObjectParser(tlvReg, viTlvRegistry)));
        regs.add(context.registerObjectSerializer(Srp.class, new FlowspecSrpObjectParser(tlvReg, viTlvRegistry)));

        regs.add(context.registerObjectParser(PcepOpenObjectWithFlowspecTlvParser.CLASS,
                PcepOpenObjectWithFlowspecTlvParser.TYPE, new PcepOpenObjectWithFlowspecTlvParser(tlvReg, viTlvRegistry)));

        regs.add(context.registerObjectSerializer(Open.class,
                new PcepOpenObjectWithFlowspecTlvParser(tlvReg, viTlvRegistry)));
        regs.add(context.registerTlvParser(FlowspecFlowFilterDestinationPrefixTlvParser.TYPE, new FlowspecFlowFilterDestinationPrefixTlvParser()));
        regs.add(context.registerTlvSerializer(DestinationPrefixCase.class, new FlowspecFlowFilterDestinationPrefixTlvParser()));

        regs.add(context.registerTlvParser(FlowspecFlowFilterSourcePrefixTlvParser.TYPE, new FlowspecFlowFilterSourcePrefixTlvParser()));
        regs.add(context.registerTlvSerializer(SourcePrefixCase.class, new FlowspecFlowFilterSourcePrefixTlvParser()));

        regs.add(context.registerTlvParser(FlowspecFlowFilterProtocolIPTlvParser.TYPE, new FlowspecFlowFilterProtocolIPTlvParser()));
        regs.add(context.registerTlvSerializer(ProtocolIpCase.class, new FlowspecFlowFilterProtocolIPTlvParser()));

        regs.add(context.registerTlvParser(FlowspecFlowFilterPortTlvParser.TYPE, new FlowspecFlowFilterPortTlvParser()));
        regs.add(context.registerTlvSerializer(PortCase.class, new FlowspecFlowFilterPortTlvParser()));

        regs.add(context.registerTlvParser(FlowspecFlowFilterDestinationPortTlvParser.TYPE, new FlowspecFlowFilterDestinationPortTlvParser()));
        regs.add(context.registerTlvSerializer(DestinationPortCase.class, new FlowspecFlowFilterDestinationPortTlvParser()));

        regs.add(context.registerTlvParser(FlowspecFlowFilterSourcePortTlvParser.TYPE, new FlowspecFlowFilterSourcePortTlvParser()));
        regs.add(context.registerTlvSerializer(SourcePortCase.class, new FlowspecFlowFilterSourcePortTlvParser()));

        regs.add(context.registerTlvParser(FlowspecFlowFilterICMPcodeTlvParser.TYPE, new FlowspecFlowFilterICMPcodeTlvParser()));
        regs.add(context.registerTlvSerializer(IcmpCodeCase.class, new FlowspecFlowFilterICMPcodeTlvParser()));

        regs.add(context.registerTlvParser(FlowspecFlowFilterICMPtypeTlvParser.TYPE, new FlowspecFlowFilterICMPtypeTlvParser()));
        regs.add(context.registerTlvSerializer(IcmpTypeCase.class, new FlowspecFlowFilterICMPtypeTlvParser()));

        regs.add(context.registerTlvParser(FlowspecFlowFilterDSCPTlvParser.TYPE, new FlowspecFlowFilterDSCPTlvParser()));
        regs.add(context.registerTlvSerializer(DscpCase.class, new FlowspecFlowFilterDSCPTlvParser()));

        regs.add(context.registerTlvParser(FlowspecFlowFilterFragmentTlvParser.TYPE, new FlowspecFlowFilterFragmentTlvParser()));
        regs.add(context.registerTlvSerializer(FragmentCase.class, new FlowspecFlowFilterFragmentTlvParser()));

        regs.add(context.registerTlvParser(FlowspecFlowFilterTCPflagTlvParser.TYPE, new FlowspecFlowFilterTCPflagTlvParser()));
        regs.add(context.registerTlvSerializer(TcpFlagsCase.class, new FlowspecFlowFilterTCPflagTlvParser()));

        regs.add(context.registerTlvParser(FlowspecFlowFilterPacketLengthTlvParser.TYPE, new FlowspecFlowFilterPacketLengthTlvParser()));
        regs.add(context.registerTlvSerializer(PacketLengthCase.class, new FlowspecFlowFilterPacketLengthTlvParser()));

        regs.add(context.registerTlvParser(Stateful07LSPIdentifierIpv4TlvParser.TYPE, new Stateful07LSPIdentifierIpv4TlvParser()));
        regs.add(context.registerTlvSerializer(LspIdentifiers.class, new Stateful07LSPIdentifierIpv4TlvParser()));

        regs.add(context.registerTlvParser(Stateful07LspSymbolicNameTlvParser.TYPE, new Stateful07LspSymbolicNameTlvParser()));
        regs.add(context.registerTlvSerializer(SymbolicPathName.class, new Stateful07LspSymbolicNameTlvParser()));
        return regs;
    }
}
