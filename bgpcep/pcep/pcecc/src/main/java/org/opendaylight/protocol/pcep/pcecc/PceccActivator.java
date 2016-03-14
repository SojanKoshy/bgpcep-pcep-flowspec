/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
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


public class PceccActivator extends AbstractPCEPExtensionProviderActivator {

    @Override
    protected List<AutoCloseable> startImpl(final PCEPExtensionProviderContext context) {
        final List<AutoCloseable> regs = Lists.newArrayList();

        final ObjectRegistry objReg = context.getObjectHandlerRegistry();
        regs.add(context.registerMessageParser(PceccLabelUpdateMessageParser.TYPE,
                new PceccLabelUpdateMessageParser(objReg)));
        regs.add(context.registerMessageSerializer(Pclabelupd.class, new PceccLabelUpdateMessageParser(objReg)));

        /* Tlvs */
        regs.add(context.registerTlvParser(PceccCapabilityTlvParser.TYPE, new PceccCapabilityTlvParser()));
        regs.add(context.registerTlvParser(PceccPathSetupTypeTlvParser.TYPE, new PceccPathSetupTypeTlvParser()));

        regs.add(context.registerTlvSerializer(PathSetupType.class, new PceccPathSetupTypeTlvParser()));
        regs.add(context.registerTlvSerializer(PceccCapability.class, new PceccCapabilityTlvParser()));

        final PceccFecIpv4ObjectParser fecV4Parser = new PceccFecIpv4ObjectParser();
        final PceccFecIpv4AdjacencyObjectParser fecV4AdjParser = new PceccFecIpv4AdjacencyObjectParser();
        regs.add(context.registerObjectParser(PceccFecIpv4ObjectParser.CLASS, PceccFecIpv4ObjectParser.TYPE, fecV4Parser));
        //regs.add(context.registerObjectParser(PceccFecIpv4AdjacencyObjectParser.CLASS, PceccFecIpv4AdjacencyObjectParser.TYPE, fecV4AdjParser));
        regs.add(context.registerObjectSerializer(Fec.class, fecV4Parser));
        //regs.add(context.registerObjectSerializer(Fec.class, fecV4AdjParser)); // need to check

        /* Objects */
        final TlvRegistry tlvReg = context.getTlvHandlerRegistry();
        final VendorInformationTlvRegistry viTlvRegistry = context.getVendorInformationTlvRegistry();

        regs.add(context.registerObjectParser(PceccLabelObjectParser.CLASS, PceccLabelObjectParser.TYPE,
                new PceccLabelObjectParser(tlvReg, viTlvRegistry)));

        regs.add(context.registerObjectSerializer(Label.class, new PceccLabelObjectParser(tlvReg, viTlvRegistry)));

        regs.add(context.registerObjectParser(PcepOpenObjectWithPceccTlvParser.CLASS,
                PcepOpenObjectWithPceccTlvParser.TYPE, new PcepOpenObjectWithPceccTlvParser(tlvReg, viTlvRegistry)));
        regs.add(context.registerObjectSerializer(Open.class, new PcepOpenObjectWithPceccTlvParser(tlvReg, viTlvRegistry)));

        regs.add(context.registerTlvParser(PceccLabelAddressIpv4TlvParser.TYPE, new PceccLabelAddressIpv4TlvParser()));
        regs.add(context.registerTlvSerializer(Address.class, new PceccLabelAddressIpv4TlvParser()));
        return regs;
    }
}
