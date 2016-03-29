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
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.pcep.sync.optimizations.rev150714.Tlvs3;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.pcep.sync.optimizations.rev150714.Tlvs3Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Tlvs1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Tlvs1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.LabelNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.AddressBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.Ipv4CaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.ipv4._case.Ipv4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.FecBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.fec.fec.Ipv4AdjacencyCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.fec.fec.Ipv4NodeIdCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pcecc.capability.tlv.PceccCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.ProtocolVersion;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.OpenBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.TlvsBuilder;

public class PceccObjectParserTest {

    private static final byte[] openObjectBytes = {
        0x01, 0x10, 0x00, 0x10,
        0x20, 0x1e, 0x78, 0x01,
        /* pcecc-capability-tlv */
        (byte) 0xff, 0x07, 0x00, 0x04,
        0x00, 0x00, 0x00, 0x03
    };

    private static final byte[] PceccLabelObjectBytes = {
        (byte) 0xe1, 0x10, 0x00, 0x0c,
        0x00, 0x00, 0x00, 0x01,
        (byte) 0x01, 0x38, (byte) 0x90, 0x00
    };

    private static final byte[] PceccFecObjectBytes = {
        (byte) 0xe2, 0x10, 0x00, 0x08,
        (byte) 0xff,(byte) 0x90, 0x00, 0x01
    };

    private static final byte[] PceccFecAdjacencyObjectBytes = {
        (byte) 0xe2, 0x30, 0x00, 0x0c,
        (byte) 0xfe,(byte) 0x90, 0x00, 0x00,
        (byte) 0xfe,(byte) 0x90, 0x00, 0x00
    };


    private static final byte[] PceccLabelObjectwithAddressTlvBytes = {
        (byte) 0xe1, 0x10, 0x00, (byte) 0x14,
        0x00, 0x00, 0x00, 0x00,
        (byte) 0x01, 0x38, (byte) 0x90, 0x00,
        (byte)  0xff, 0x09, 0x00, 0x04,
        0x01, 0x01, 0x01, 0x01
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


    @Test
    public void testPceccLabelObjectParserWithoutAddressTlv() throws PCEPDeserializerException {
        final PceccLabelObjectParser parser =
                new PceccLabelObjectParser(this.tlvRegistry, this.viTlvRegistry);

        final LabelBuilder builder = new LabelBuilder();
        builder.setProcessingRule(false);
        builder.setIgnore(false);
        builder.setLabelNum(new LabelNumber(5001L));
        builder.setOutLabel(true);
        builder.setTlvs(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder()
                .build());
        final ByteBuf result = Unpooled.wrappedBuffer(PceccLabelObjectBytes);
        assertEquals(builder.build(),
                parser.parseObject(new ObjectHeaderImpl(false, false), result.slice(4, result.readableBytes() - 4)));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeObject(builder.build(), buffer);
        assertArrayEquals(PceccLabelObjectBytes, ByteArray.getAllBytes(buffer));
    }


    @Test
    public void testPceccFecIpv4ObjectParser() throws PCEPDeserializerException {
        final PceccFecIpv4ObjectParser parser =
                new PceccFecIpv4ObjectParser();

        final FecBuilder builder = new FecBuilder();
        builder.setProcessingRule(false);
        builder.setIgnore(false);

        builder.setFec(new Ipv4NodeIdCaseBuilder().setNodeId(new Ipv4Address("255.144.0.1")).build());
        final ByteBuf result = Unpooled.wrappedBuffer(PceccFecObjectBytes);
        assertEquals(builder.build(),
                parser.parseObject(new ObjectHeaderImpl(false, false), result.slice(4, result.readableBytes() - 4)));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeObject(builder.build(), buffer);
        assertArrayEquals(PceccFecObjectBytes, ByteArray.getAllBytes(buffer));
    }

    @Test
    public void testPceccFecObjectParserIpv4NodeId() throws PCEPDeserializerException {
        final PceccFecObjectParser parser =
                new PceccFecObjectParser();

        final FecBuilder builder = new FecBuilder();
        builder.setProcessingRule(false);
        builder.setIgnore(false);
        builder.setFec(new Ipv4NodeIdCaseBuilder().setNodeId(new Ipv4Address("255.144.0.1")).build());
        /*only for parser coverage*/
        final ByteBuf result = Unpooled.wrappedBuffer(PceccFecObjectBytes);
        parser.parseObject(new ObjectHeaderImpl(false, false), result.slice(4, result.readableBytes() - 4));

        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeObject(builder.build(), buffer);
        assertArrayEquals(PceccFecObjectBytes, ByteArray.getAllBytes(buffer));
    }

    @Test
    public void testPceccFecObjectParserIpv4Adjacency() throws PCEPDeserializerException {
        final PceccFecObjectParser parser =
                new PceccFecObjectParser();

        final FecBuilder builder = new FecBuilder();
        builder.setProcessingRule(false);
        builder.setIgnore(false);
        builder.setFec(new Ipv4AdjacencyCaseBuilder().setLocalIpAddress(new Ipv4Address("254.144.0.0"))
                .setRemoteIpAddress(new Ipv4Address("254.144.0.0")).build()).build();

        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeObject(builder.build(), buffer);
        assertArrayEquals(PceccFecAdjacencyObjectBytes, ByteArray.getAllBytes(buffer));
    }

    @Test
    public void testPceccFecIpv4AdjacencyObjectParser() throws PCEPDeserializerException {
        final PceccFecIpv4AdjacencyObjectParser parser =
                new PceccFecIpv4AdjacencyObjectParser();

        final FecBuilder builder = new FecBuilder();
        builder.setProcessingRule(false);
        builder.setIgnore(false);
        builder.setFec(new Ipv4AdjacencyCaseBuilder().setLocalIpAddress(new Ipv4Address("254.144.0.0"))
                .setRemoteIpAddress(new Ipv4Address("254.144.0.0")).build()).build();

        final ByteBuf result = Unpooled.wrappedBuffer(PceccFecAdjacencyObjectBytes);
        assertEquals(builder.build(),
                parser.parseObject(new ObjectHeaderImpl(false, false), result.slice(4, result.readableBytes() - 4)));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeObject(builder.build(), buffer);
        assertArrayEquals(PceccFecAdjacencyObjectBytes, ByteArray.getAllBytes(buffer));
    }

    @Test
    public void testPceccLabelObjectParserWithAddressLabelTLV() throws PCEPDeserializerException {
        final PceccLabelObjectParser parser =
                new PceccLabelObjectParser(this.tlvRegistry, this.viTlvRegistry);

        final LabelBuilder builder = new LabelBuilder();
        builder.setProcessingRule(false);
        builder.setIgnore(false);
        builder.setLabelNum(new LabelNumber(5001L));
        builder.setOutLabel(false);


        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder tlvBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder();

        AddressBuilder addressBuilder =  new AddressBuilder();
        Ipv4Builder ipv4 =  new Ipv4Builder();
        ipv4.setIpv4Address(new Ipv4Address("1.1.1.1")).build();
        addressBuilder.setAddressFamily(new Ipv4CaseBuilder().setIpv4(ipv4.build()).build());

        builder.setTlvs(tlvBuilder.setAddress(addressBuilder.build()).build());

        final ByteBuf result = Unpooled.wrappedBuffer(PceccLabelObjectwithAddressTlvBytes);
        assertEquals(builder.build(),
                parser.parseObject(new ObjectHeaderImpl(false, false), result.slice(4, result.readableBytes() - 4)));
        final ByteBuf buffer = Unpooled.buffer();
        parser.serializeObject(builder.build(), buffer);
        assertArrayEquals(PceccLabelObjectwithAddressTlvBytes, ByteArray.getAllBytes(buffer));
    }

}

