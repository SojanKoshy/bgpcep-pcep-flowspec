/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.pcecc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.protocol.pcep.spi.ObjectHeaderImpl;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.TlvRegistry;
import org.opendaylight.protocol.pcep.spi.VendorInformationTlvRegistry;
import org.opendaylight.protocol.pcep.spi.pojo.SimplePCEPExtensionProviderContext;
import org.opendaylight.protocol.util.ByteArray;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.pcep.sync.optimizations.rev150714.Tlvs3;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.pcep.sync.optimizations.rev150714.Tlvs3Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Tlvs1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Tlvs1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pcecc.capability.tlv.PceccCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.ProtocolVersion;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.OpenBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.TlvsBuilder;

public class PcepOpenObjectWithPceccTlvParserTest {

    private static final byte[] openObjectBytes = {
        0x01, 0x10, 0x00, 0x10,
        0x20, 0x1e, 0x78, 0x01,
        /* pcecc-capability-tlv */
        (byte) 0xff, 0x07, 0x00, 0x04,
        0x00, 0x00, 0x00, 0x03
    };

    private TlvRegistry tlvRegistry;
    private VendorInformationTlvRegistry viTlvRegistry;

    private SimplePCEPExtensionProviderContext ctx;
    private PceccActivator act;

    @Before
    public void setUp() {
        this.ctx = new SimplePCEPExtensionProviderContext();
        this.act = new PceccActivator();
        this.act.start(this.ctx);
        this.tlvRegistry = this.ctx.getTlvHandlerRegistry();
        this.viTlvRegistry = this.ctx.getVendorInformationTlvRegistry();
    }

    @Test
    public void testOpenObjectWithPceccTlv() throws PCEPDeserializerException {
        final PcepOpenObjectWithPceccTlvParser parser =
                new PcepOpenObjectWithPceccTlvParser(this.tlvRegistry, this.viTlvRegistry);

        final OpenBuilder builder = new OpenBuilder();
        builder.setProcessingRule(false);
        builder.setIgnore(false);
        builder.setVersion(new ProtocolVersion((short) 1));
        builder.setKeepalive((short) 30);
        builder.setDeadTimer((short) 120);
        builder.setSessionId((short) 1);


        final Tlvs4Builder pceccTlv = new Tlvs4Builder();
        pceccTlv.setPceccCapability(new PceccCapabilityBuilder().setIldbBit(true).setSBit(true).build()).build();
        final Tlvs1Builder statBuilder = new Tlvs1Builder();
        final Tlvs3Builder syncOptBuilder = new Tlvs3Builder();
        builder.setTlvs(new TlvsBuilder()
                .addAugmentation(Tlvs1.class, statBuilder.build())
                .addAugmentation(Tlvs3.class, syncOptBuilder.build())
                .addAugmentation(Tlvs4.class, pceccTlv.build())
                .build());


        final ByteBuf result = Unpooled.wrappedBuffer(openObjectBytes);
        assertEquals(builder.build(),
                parser.parseObject(new ObjectHeaderImpl(false, false), result.slice(4, result.readableBytes() - 4)));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeObject(builder.build(), buffer);
        assertArrayEquals(openObjectBytes, ByteArray.getAllBytes(buffer));
    }

}

