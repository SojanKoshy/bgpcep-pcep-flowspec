/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.pcecc;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pcecc.capability.tlv.PceccCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.Tlvs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.TlvsBuilder;


public class PceccCapabilityTest {

    private static final Tlvs EXPECTED_TLVS =
            new TlvsBuilder().addAugmentation(Tlvs4.class, new Tlvs4Builder()
                    .setPceccCapability(new PceccCapabilityBuilder().setSBit(true).setIldbBit(true).build()).build())
                    .build();

    private static final Tlvs EXPECTED_TLVS_FALSE_LBIT =
            new TlvsBuilder().addAugmentation(Tlvs4.class, new Tlvs4Builder()
                    .setPceccCapability(new PceccCapabilityBuilder().setSBit(true).setIldbBit(false).build()).build())
                    .build();

    private static final Tlvs EXPECTED_TLVS_FALSE_SBIT =
            new TlvsBuilder().addAugmentation(Tlvs4.class, new Tlvs4Builder()
                    .setPceccCapability(new PceccCapabilityBuilder().setSBit(false).setIldbBit(true).build()).build())
                    .build();

    private static final Tlvs EXPECTED_TLVS_WITHOUT_PCECC_TLV =
            new TlvsBuilder().build();

    /*
    * testPCEPPceccCapabilityWithPceccCapability
    * Description :- Test PcepPceccCapability with L bit "true" and S bit "true".
    */
    @Test
    public void testPCEPPceccCapabilityWithPceccCapability() {
        final PcepPceccCapability pceccCapability;
        pceccCapability = new PcepPceccCapability(true, true, true);
        Assert.assertTrue(pceccCapability.isPceccCapable());
        final TlvsBuilder builder = new TlvsBuilder();
        pceccCapability.setCapabilityProposal(null, builder);
        Assert.assertEquals(EXPECTED_TLVS, builder.build());
    }

    /*
    * testPCEPPceccCapabilityWithFalseSbit
    * Description :- Test PcepPceccCapability with L bit "true" and S bit "false".
    */
    @Test
    public void testPCEPPceccCapabilityWithFalseSbit() {
        final PcepPceccCapability pceccCapability;
        pceccCapability = new PcepPceccCapability(true, false, true);
        Assert.assertTrue(pceccCapability.isPceccCapable());
        final TlvsBuilder builder = new TlvsBuilder();
        pceccCapability.setCapabilityProposal(null, builder);
        Assert.assertEquals(EXPECTED_TLVS_FALSE_SBIT, builder.build());
    }

    /*
    * testPCEPPceccCapabilityWithFalseLbit
    * Description :- Test PcepPceccCapability with L bit "false" and S bit "true".
    */
    @Test
    public void testPCEPPceccCapabilityWithFalseLbit() {
        final PcepPceccCapability pceccCapability;
        pceccCapability = new PcepPceccCapability(true, true, false);
        Assert.assertTrue(pceccCapability.isPceccCapable());
        Assert.assertTrue(pceccCapability.isSCapable());
        Assert.assertFalse(pceccCapability.isILDBCapable());
        final TlvsBuilder builder = new TlvsBuilder();
        pceccCapability.setCapabilityProposal(null, builder);
        Assert.assertEquals(EXPECTED_TLVS_FALSE_LBIT, builder.build());
    }

    /*
    * testPCEPPceccCapabilityWithoutPceccCapability
    * Description :- Test PcepPceccCapability without Pcecc Tlv".
    */
    @Test
    public void testPCEPPceccCapabilityWithoutPceccCapability() {
        final PcepPceccCapability pceccCapability;
        pceccCapability = new PcepPceccCapability(false, true, true);
        Assert.assertFalse(pceccCapability.isPceccCapable());
        Assert.assertTrue(pceccCapability.isSCapable());
        Assert.assertTrue(pceccCapability.isILDBCapable());
        final TlvsBuilder builder = new TlvsBuilder();
        pceccCapability.setCapabilityProposal(null, builder);
        Assert.assertEquals(EXPECTED_TLVS_WITHOUT_PCECC_TLV, builder.build());
    }

}
