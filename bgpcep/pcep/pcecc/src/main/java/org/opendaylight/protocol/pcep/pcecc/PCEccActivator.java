/*
 * Copyright (c) 2016 Huawei Technologies Co. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.pcecc;

import com.google.common.collect.Lists;
import java.util.List;

import org.opendaylight.protocol.pcep.spi.ObjectRegistry;
import org.opendaylight.protocol.pcep.spi.PCEPExtensionProviderContext;
import org.opendaylight.protocol.pcep.spi.TlvRegistry;
import org.opendaylight.protocol.pcep.spi.VendorInformationTlvRegistry;
import org.opendaylight.protocol.pcep.spi.pojo.AbstractPCEPExtensionProviderActivator;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Pclabelupd;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.Fec;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.Label;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pcecc.capability.tlv.PceccCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.Open;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.path.setup.type.tlv.PathSetupType;


public class PCEccActivator extends AbstractPCEPExtensionProviderActivator {

    @Override
    protected List<AutoCloseable> startImpl(final PCEPExtensionProviderContext context) {
        final List<AutoCloseable> regs = Lists.newArrayList();

        final ObjectRegistry objReg = context.getObjectHandlerRegistry();
        regs.add(context.registerMessageParser(PCEccLabelUpdateMessageParser.TYPE,
                new PCEccLabelUpdateMessageParser(objReg)));
        regs.add(context.registerMessageSerializer(Pclabelupd.class, new PCEccLabelUpdateMessageParser(objReg)));

        /* Tlvs */

        regs.add(context.registerTlvParser(PCEccCapabilityTlvParser.TYPE, new PCEccCapabilityTlvParser()));
        regs.add(context.registerTlvParser(PCEccPathSetupTypeTlvParser.TYPE, new PCEccPathSetupTypeTlvParser()));

        regs.add(context.registerTlvSerializer(PathSetupType.class, new PCEccPathSetupTypeTlvParser()));
        regs.add(context.registerTlvSerializer(PceccCapability.class, new PCEccCapabilityTlvParser()));

        final PCEccFECIPv4ObjectParser fecV4Parser = new PCEccFECIPv4ObjectParser();
        final PCEccFECIPv4AdjacencyObjectParser fecV4AdjParser = new PCEccFECIPv4AdjacencyObjectParser();
        regs.add(context.registerObjectParser(PCEccFECIPv4ObjectParser.CLASS, PCEccFECIPv4ObjectParser.TYPE, fecV4Parser));
        regs.add(context.registerObjectParser(PCEccFECIPv4AdjacencyObjectParser.CLASS, PCEccFECIPv4AdjacencyObjectParser.TYPE, fecV4AdjParser));
        regs.add(context.registerObjectSerializer(Fec.class, fecV4Parser));
        regs.add(context.registerObjectSerializer(Fec.class, fecV4AdjParser));


        /* Objects */
        final TlvRegistry tlvReg = context.getTlvHandlerRegistry();
        final VendorInformationTlvRegistry viTlvRegistry = context.getVendorInformationTlvRegistry();

        regs.add(context.registerObjectParser(PCEccLabelObjectParser.CLASS, PCEccLabelObjectParser.TYPE,
                new PCEccLabelObjectParser(tlvReg, viTlvRegistry)));
        regs.add(context.registerObjectSerializer(Label.class, new PCEccLabelObjectParser(tlvReg, viTlvRegistry)));



        regs.add(context.registerObjectParser(PcepOpenObjectWithPCEccTlvParser.CLASS,
                PcepOpenObjectWithPCEccTlvParser.TYPE, new PcepOpenObjectWithPCEccTlvParser(tlvReg, viTlvRegistry)));
        regs.add(context.registerObjectSerializer(Open.class, new PcepOpenObjectWithPCEccTlvParser(tlvReg, viTlvRegistry)));

        regs.add(context.registerTlvParser(PCEccLabelAddressIpv4TlvParser.TYPE, new PCEccLabelAddressIpv4TlvParser()));
        regs.add(context.registerTlvSerializer(Address.class, new PCEccLabelAddressIpv4TlvParser()));
        return regs;
    }
}
