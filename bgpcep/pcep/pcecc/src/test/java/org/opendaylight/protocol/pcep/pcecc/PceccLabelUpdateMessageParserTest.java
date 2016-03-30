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
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.pojo.SimplePCEPExtensionProviderContext;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.OperationalStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.PlspId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.SrpIdNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.LspBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.lsp.TlvsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.SrpBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.LabelNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.PclabelupdBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.AddressBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.Ipv4CaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.ipv4._case.Ipv4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.FecBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.fec.fec.Ipv4NodeIdCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.PceLabelDownloadCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.PceLabelMapCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.PceLabelDownloadBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.Label;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.map._case.PceLabelMapBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pclabelupd.message.PclabelupdMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pclabelupd.message.pclabelupd.message.PceLabelUpdates;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pclabelupd.message.pclabelupd.message.PceLabelUpdatesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Message;




public class PceccLabelUpdateMessageParserTest {
    private static final byte[] PceccLabelMapObjectWithAddressTlvBytes = {
        (byte) 0x20, (byte)0xe2,0x00, 0x2c,
        (byte) 0x21, (byte)0x10,0x00, 0x0c,
        0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x01,
        (byte) 0xe1, 0x10, 0x00, (byte) 0x14,
        0x00, 0x00, 0x00, 0x00,
        (byte) 0x01, 0x38, (byte) 0x90, 0x00,
        (byte)  0xff, 0x09, 0x00, 0x04,
        0x01, 0x01, 0x01, 0x01,
        (byte) 0xe2, 0x10, 0x00, 0x08,
        (byte) 0xff,(byte) 0x90, 0x00, 0x01
    };
    private static final byte[] PceccLabelDownloadWithSingleElementList = {
        (byte) 0x20, (byte)0xe2,0x00, 0x2c,
        (byte) 0x21, (byte)0x10,0x00, 0x0c,
        0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x01,
        (byte) 0x20, (byte)0x10,0x00, 0x08,
        0x00,0x00,0x00,0x00,
        (byte) 0xe1, 0x10, 0x00, (byte) 0x14,
        0x00, 0x00, 0x00, 0x00,
        (byte) 0x01, 0x38, (byte) 0x90, 0x00,
        (byte)  0xff, 0x09, 0x00, 0x04,
        0x01, 0x01, 0x01, 0x01,
    };
    private static final byte[] PceccLabelDownloadWithMultiElementList = {
        (byte) 0x20, (byte)0xe2,0x00, 0x40,
        (byte) 0x21, (byte)0x10,0x00, 0x0c,
        0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x01,
        (byte) 0x20, (byte)0x10,0x00, 0x08,
        0x00,0x00,0x00,0x00,
        (byte) 0xe1, 0x10, 0x00, (byte) 0x14,
        0x00, 0x00, 0x00, 0x00,
        (byte) 0x01, 0x38, (byte) 0x90, 0x00,
        (byte)  0xff, 0x09, 0x00, 0x04,
        0x01, 0x01, 0x01, 0x01,
        (byte) 0xe1, 0x10, 0x00, (byte) 0x14,
        0x00, 0x00, 0x00, 0x01,
        (byte) 0x01, 0x38, (byte) 0x90, 0x00,
        (byte)  0xff, 0x09, 0x00, 0x04,
        0x0e, 0x0a, 0x01, 0x1d,
    };
    private static final byte[] PceccLabelMapObjectWithoutAddressTlvBytes = {
        (byte) 0x20, (byte)0xe2,0x00, 0x24,
        (byte) 0x21, (byte)0x10,0x00, 0x0c,
        0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x01,
        (byte) 0xe1, 0x10, 0x00, (byte) 0x0c,
        0x00, 0x00, 0x00, 0x00,
        (byte) 0x01, 0x38, (byte) 0x90, 0x00,
        (byte) 0xe2, 0x10, 0x00, 0x08,
        (byte) 0xff,(byte) 0x90, 0x00, 0x01
    };
    private SimplePCEPExtensionProviderContext ctx;
    private PceccActivator act;

    @Before
    public void setUp() {
        this.ctx = new SimplePCEPExtensionProviderContext();
        this.act = new PceccActivator();
        this.act.start(this.ctx);

    }

    @Test
    public void testPceLabelDownloadWithMultiElementList() throws IOException, PCEPDeserializerException {

        final PceccLabelUpdateMessageParser parser = new PceccLabelUpdateMessageParser(this.ctx.getObjectHandlerRegistry());
        final PclabelupdMessageBuilder builder = new PclabelupdMessageBuilder();
        final List<PceLabelUpdates> updates = Lists.newArrayList();
        final List<Label> labelList = Lists.newArrayList();

        PceLabelDownloadCaseBuilder pceLabelDownloadCaseBuilder = new PceLabelDownloadCaseBuilder();
        PceLabelDownloadBuilder pceLabelDownload = new PceLabelDownloadBuilder();
        final FecBuilder fecBuilder = new FecBuilder();
        fecBuilder.setProcessingRule(false);
        fecBuilder.setIgnore(false);
        fecBuilder.setFec(new Ipv4NodeIdCaseBuilder().setNodeId(new Ipv4Address("255.144.0.1")).build());

        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder tlvBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder();
        AddressBuilder addressBuilder =  new AddressBuilder();
        Ipv4Builder ipv4 =  new Ipv4Builder();
        ipv4.setIpv4Address(new Ipv4Address("1.1.1.1")).build();
        addressBuilder.setAddressFamily(new Ipv4CaseBuilder().setIpv4(ipv4.build()).build());

        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder labelBuilder =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder();
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder label1 =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder();
        label1.setOutLabel(false);
        label1.setIgnore(false);
        label1.setProcessingRule(false);
        label1.setLabelNum(new LabelNumber(5001L));
        label1.setTlvs(tlvBuilder.setAddress(addressBuilder.build()).build());

        labelBuilder.setLabel(label1.build());

        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder tlvBuilder1 = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder();
        AddressBuilder addressBuilder1 =  new AddressBuilder();
        Ipv4Builder ipv41 =  new Ipv4Builder();
        ipv41.setIpv4Address(new Ipv4Address("14.10.1.29")).build();
        addressBuilder1.setAddressFamily(new Ipv4CaseBuilder().setIpv4(ipv41.build()).build());

        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder labelBuilder1 =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder();
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder label2 =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder();
        label2.setOutLabel(true);
        label2.setIgnore(false);
        label2.setProcessingRule(false);
        label2.setLabelNum(new LabelNumber(5001L));
        label2.setTlvs(tlvBuilder1.setAddress(addressBuilder1.build()).build());

        labelBuilder1.setLabel(label2.build());

        labelList.add(labelBuilder.build());
        labelList.add(labelBuilder1.build());
        final SrpBuilder srpBuilder = new SrpBuilder();
        srpBuilder.setIgnore(false);
        srpBuilder.setProcessingRule(false);
        srpBuilder.setOperationId(new SrpIdNumber(1L));

        srpBuilder.setTlvs(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.srp.TlvsBuilder().build());

        final LspBuilder lspBuilder = new LspBuilder();
        lspBuilder.setIgnore(false);
        lspBuilder.setProcessingRule(false);
        lspBuilder.setAdministrative(false);
        lspBuilder.setDelegate(false);
        lspBuilder.setPlspId(new PlspId(0L));
        lspBuilder.setOperational(OperationalStatus.Down);
        lspBuilder.setSync(false);
        lspBuilder.setRemove(false);
        lspBuilder.setTlvs(new TlvsBuilder().build());

        pceLabelDownload.setLabel(labelList).build();
        pceLabelDownload.setSrp(srpBuilder.build()).build();
        pceLabelDownload.setLsp(lspBuilder.build());
        pceLabelDownloadCaseBuilder.setPceLabelDownload(pceLabelDownload.build()).build();

        updates.add(new PceLabelUpdatesBuilder().setPceLabelUpdateType(pceLabelDownloadCaseBuilder.build()).build());
        builder.setPceLabelUpdates(updates);

        ByteBuf result = Unpooled.wrappedBuffer(PceccLabelDownloadWithMultiElementList);

        assertEquals(new PclabelupdBuilder().setPclabelupdMessage(builder.build()).build(), parser.parseMessage(result.slice(4,
                result.readableBytes() - 4), Collections.<Message> emptyList()));
        ByteBuf buf = Unpooled.buffer(result.readableBytes());
        parser.serializeMessage(new PclabelupdBuilder().setPclabelupdMessage(builder.build()).build(), buf);
        assertArrayEquals(result.array(), buf.array());

    }

    @Test
    public void testPceLabelDownloadWithSingleElementList() throws IOException, PCEPDeserializerException {

        final PceccLabelUpdateMessageParser parser = new PceccLabelUpdateMessageParser(this.ctx.getObjectHandlerRegistry());
        final PclabelupdMessageBuilder builder = new PclabelupdMessageBuilder();
        final List<PceLabelUpdates> updates = Lists.newArrayList();
        final List<Label> labelList = Lists.newArrayList();

        PceLabelDownloadCaseBuilder pceLabelDownloadCaseBuilder = new PceLabelDownloadCaseBuilder();
        PceLabelDownloadBuilder pceLabelDownload = new PceLabelDownloadBuilder();
        final FecBuilder fecBuilder = new FecBuilder();
        fecBuilder.setProcessingRule(false);
        fecBuilder.setIgnore(false);
        fecBuilder.setFec(new Ipv4NodeIdCaseBuilder().setNodeId(new Ipv4Address("255.144.0.1")).build());

        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder tlvBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder();
        AddressBuilder addressBuilder =  new AddressBuilder();
        Ipv4Builder ipv4 =  new Ipv4Builder();
        ipv4.setIpv4Address(new Ipv4Address("1.1.1.1")).build();
        addressBuilder.setAddressFamily(new Ipv4CaseBuilder().setIpv4(ipv4.build()).build());

        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder labelBuilder =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder();
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder label1 =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder();
        label1.setOutLabel(false);
        label1.setIgnore(false);
        label1.setProcessingRule(false);
        label1.setLabelNum(new LabelNumber(5001L));
        label1.setTlvs(tlvBuilder.setAddress(addressBuilder.build()).build());

        labelBuilder.setLabel(label1.build());

        labelList.add(labelBuilder.build());
        final SrpBuilder srpBuilder = new SrpBuilder();
        srpBuilder.setIgnore(false);
        srpBuilder.setProcessingRule(false);
        srpBuilder.setOperationId(new SrpIdNumber(1L));

        srpBuilder.setTlvs(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.srp.TlvsBuilder().build());

        final LspBuilder lspBuilder = new LspBuilder();
        lspBuilder.setIgnore(false);
        lspBuilder.setProcessingRule(false);
        lspBuilder.setAdministrative(false);
        lspBuilder.setDelegate(false);
        lspBuilder.setPlspId(new PlspId(0L));
        lspBuilder.setOperational(OperationalStatus.Down);
        lspBuilder.setSync(false);
        lspBuilder.setRemove(false);
        lspBuilder.setTlvs(new TlvsBuilder().build());

        pceLabelDownload.setLabel(labelList).build();
        pceLabelDownload.setSrp(srpBuilder.build()).build();
        pceLabelDownload.setLsp(lspBuilder.build());
        pceLabelDownloadCaseBuilder.setPceLabelDownload(pceLabelDownload.build()).build();

        updates.add(new PceLabelUpdatesBuilder().setPceLabelUpdateType(pceLabelDownloadCaseBuilder.build()).build());
        builder.setPceLabelUpdates(updates);

        ByteBuf result = Unpooled.wrappedBuffer(PceccLabelDownloadWithSingleElementList);

        assertEquals(new PclabelupdBuilder().setPclabelupdMessage(builder.build()).build(), parser.parseMessage(result.slice(4,
                result.readableBytes() - 4), Collections.<Message> emptyList()));
        ByteBuf buf = Unpooled.buffer(result.readableBytes());
        parser.serializeMessage(new PclabelupdBuilder().setPclabelupdMessage(builder.build()).build(), buf);
        assertArrayEquals(result.array(), buf.array());

    }


    @Test
    public void testPceLabelMapWithAddressTlv() throws IOException, PCEPDeserializerException {

        final PceccLabelUpdateMessageParser parser = new PceccLabelUpdateMessageParser(this.ctx.getObjectHandlerRegistry());
        final PclabelupdMessageBuilder builder = new PclabelupdMessageBuilder();
        final List<PceLabelUpdates> updates = Lists.newArrayList();
        PceLabelMapCaseBuilder pceLabelMapCaseBuilder = new PceLabelMapCaseBuilder();
        PceLabelMapBuilder pceLabelMap = new PceLabelMapBuilder();
        final FecBuilder fecBuilder = new FecBuilder();
        fecBuilder.setProcessingRule(false);
        fecBuilder.setIgnore(false);
        fecBuilder.setFec(new Ipv4NodeIdCaseBuilder().setNodeId(new Ipv4Address("255.144.0.1")).build());

        final LabelBuilder labelBuilder = new LabelBuilder();
        labelBuilder.setProcessingRule(false);
        labelBuilder.setIgnore(false);
        labelBuilder.setLabelNum(new LabelNumber(5001L));
        labelBuilder.setOutLabel(false);

        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder tlvBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder();
        AddressBuilder addressBuilder =  new AddressBuilder();
        Ipv4Builder ipv4 =  new Ipv4Builder();
        ipv4.setIpv4Address(new Ipv4Address("1.1.1.1")).build();
        addressBuilder.setAddressFamily(new Ipv4CaseBuilder().setIpv4(ipv4.build()).build());

        labelBuilder.setTlvs(tlvBuilder.setAddress(addressBuilder.build()).build());

        final SrpBuilder srpBuilder = new SrpBuilder();
        srpBuilder.setIgnore(false);
        srpBuilder.setProcessingRule(false);
        srpBuilder.setOperationId(new SrpIdNumber(1L));

        srpBuilder.setTlvs(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.srp.TlvsBuilder().build());

        pceLabelMap.setFec(fecBuilder.build()).build();
        pceLabelMap.setLabel(labelBuilder.build()).build();
        pceLabelMap.setSrp(srpBuilder.build()).build();
        pceLabelMapCaseBuilder.setPceLabelMap(pceLabelMap.build()).build();

        updates.add(new PceLabelUpdatesBuilder().setPceLabelUpdateType(pceLabelMapCaseBuilder.build()).build());
        builder.setPceLabelUpdates(updates);

        ByteBuf result = Unpooled.wrappedBuffer(PceccLabelMapObjectWithAddressTlvBytes);

        assertEquals(new PclabelupdBuilder().setPclabelupdMessage(builder.build()).build(), parser.parseMessage(result.slice(4,
                result.readableBytes() - 4), Collections.<Message> emptyList()));
        ByteBuf buf = Unpooled.buffer(result.readableBytes());
        parser.serializeMessage(new PclabelupdBuilder().setPclabelupdMessage(builder.build()).build(), buf);
        assertArrayEquals(result.array(), buf.array());

    }

    @Test
    public void testPceLabelMapWithoutAddressTlv() throws IOException, PCEPDeserializerException {

        final PceccLabelUpdateMessageParser parser = new PceccLabelUpdateMessageParser(this.ctx.getObjectHandlerRegistry());
        final PclabelupdMessageBuilder builder = new PclabelupdMessageBuilder();
        final List<PceLabelUpdates> updates = Lists.newArrayList();
        PceLabelMapCaseBuilder pceLabelMapCaseBuilder = new PceLabelMapCaseBuilder();
        PceLabelMapBuilder pceLabelMap = new PceLabelMapBuilder();
        final FecBuilder fecBuilder = new FecBuilder();
        fecBuilder.setProcessingRule(false);
        fecBuilder.setIgnore(false);
        fecBuilder.setFec(new Ipv4NodeIdCaseBuilder().setNodeId(new Ipv4Address("255.144.0.1")).build());

        final LabelBuilder labelBuilder = new LabelBuilder();
        labelBuilder.setProcessingRule(false);
        labelBuilder.setIgnore(false);
        labelBuilder.setLabelNum(new LabelNumber(5001L));
        labelBuilder.setOutLabel(false);
        labelBuilder.setTlvs(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder()
                .build());
        final SrpBuilder srpBuilder = new SrpBuilder();
        srpBuilder.setIgnore(false);
        srpBuilder.setProcessingRule(false);
        srpBuilder.setOperationId(new SrpIdNumber(1L));

        srpBuilder.setTlvs(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.srp.TlvsBuilder().build());

        pceLabelMap.setFec(fecBuilder.build()).build();
        pceLabelMap.setLabel(labelBuilder.build()).build();
        pceLabelMap.setSrp(srpBuilder.build()).build();
        pceLabelMapCaseBuilder.setPceLabelMap(pceLabelMap.build()).build();

        updates.add(new PceLabelUpdatesBuilder().setPceLabelUpdateType(pceLabelMapCaseBuilder.build()).build());
        builder.setPceLabelUpdates(updates);

        ByteBuf result = Unpooled.wrappedBuffer(PceccLabelMapObjectWithoutAddressTlvBytes);

        assertEquals(new PclabelupdBuilder().setPclabelupdMessage(builder.build()).build(), parser.parseMessage(result.slice(4,
                result.readableBytes() - 4), Collections.<Message> emptyList()));
        ByteBuf buf = Unpooled.buffer(result.readableBytes());
        parser.serializeMessage(new PclabelupdBuilder().setPclabelupdMessage(builder.build()).build(), buf);
        assertArrayEquals(result.array(), buf.array());

    }

}
