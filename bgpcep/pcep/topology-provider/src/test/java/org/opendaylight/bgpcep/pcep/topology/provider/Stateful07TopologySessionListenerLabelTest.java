/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.bgpcep.pcep.topology.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.protocol.pcep.PCEPSession;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.network.topology.rev140113.NetworkTopologyRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.crabbe.initiated.rev131126.Stateful1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.crabbe.initiated.rev131126.Stateful1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.LspIdentifiersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.LspBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.SrpBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.stateful.capability.tlv.StatefulBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.AddressBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.Ipv4CaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.ipv4._case.Ipv4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.FecBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.fec.fec.Ipv4NodeIdCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.PceLabelUpdateType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.PceLabelDownloadCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.PceLabelDownloadCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.PceLabelMapCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.PceLabelMapCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.PceLabelDownload;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.PceLabelDownloadBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.Label;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.map._case.PceLabelMap;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.map._case.PceLabelMapBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pcecc.capability.tlv.PceccCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pclabelupd.message.PclabelupdMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pclabelupd.message.pclabelupd.message.PceLabelUpdates;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.Open;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.OpenBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.TlvsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev150820.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.*;

public class Stateful07TopologySessionListenerLabelTest extends AbstractPCEPSessionTest<Stateful07TopologySessionListenerFactory> {

    private static final String TUNNEL_NAME = "pcecc_" + TEST_ADDRESS + "_tunnel_0";

    private Stateful07TopologySessionListener listener;

    private PCEPSession session;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.listener = (Stateful07TopologySessionListener) getSessionListener();
        this.session = getPCEPSession(getLocalPref(), getRemotePref());
    }

    @Test
    public void testAddDownloadLabel() throws Exception {
        this.listener.onSessionUp(this.session);

        // add-label
        this.topologyRpcs.addLabel(createAddLabelDownloadInput());
        assertEquals(1, this.receivedMsgs.size());
        assertTrue(this.receivedMsgs.get(0) instanceof Pclabelupd);
        final Pclabelupd pclabelupd = (Pclabelupd) this.receivedMsgs.get(0);
        final PceLabelUpdates req = pclabelupd.getPclabelupdMessage().getPceLabelUpdates().get(0);
        final PceLabelUpdateType pceLabelUpdateType = req.getPceLabelUpdateType();
        final PceLabelDownloadCase labelDownloadCase = (PceLabelDownloadCase)pceLabelUpdateType;
        final PceLabelDownload labelDownload = labelDownloadCase.getPceLabelDownload();

        AddLabelInput addLabelInput = createAddLabelDownloadInput();
        Arguments4 arguments = addLabelInput.getArguments().getAugmentation(Arguments4.class);
        final PceLabelUpdateType pceLabelUpdateTypeIn =arguments.getPceLabelUpdateType();
        final PceLabelDownloadCase labelDownloadCaseIn = (PceLabelDownloadCase)pceLabelUpdateTypeIn;
        final PceLabelDownload labelDownloadIn = labelDownloadCaseIn.getPceLabelDownload();
        assertEquals(labelDownloadIn.getLabel(), labelDownload.getLabel());
        assertEquals(labelDownloadIn.getSrp().getOperationId(), labelDownload.getSrp().getOperationId());
        assertEquals(labelDownloadIn.getLsp(), labelDownload.getLsp());

    }

    @Test
    public void testAddUpdateLabelMap() throws Exception {
        this.listener.onSessionUp(this.session);

        // add-label
        this.topologyRpcs.addLabel(createAddLabelMapInput());
        assertEquals(1, this.receivedMsgs.size());
        assertTrue(this.receivedMsgs.get(0) instanceof Pclabelupd);
        final Pclabelupd pclabelupd = (Pclabelupd) this.receivedMsgs.get(0);
        final PceLabelUpdates req = pclabelupd.getPclabelupdMessage().getPceLabelUpdates().get(0);
        final PceLabelUpdateType pceLabelUpdateType = req.getPceLabelUpdateType();
        final PceLabelMapCase labelMapCase = (PceLabelMapCase)pceLabelUpdateType;
        final PceLabelMap labelMap = labelMapCase.getPceLabelMap();
        AddLabelInput addLabelInput = createAddLabelMapInput();
        Arguments4 arguments = addLabelInput.getArguments().getAugmentation(Arguments4.class);
        final PceLabelUpdateType pceLabelUpdateTypeIn =arguments.getPceLabelUpdateType();
        final PceLabelMapCase labelMapCaseIn = (PceLabelMapCase)pceLabelUpdateTypeIn;
        final PceLabelMap labelMapIn = labelMapCaseIn.getPceLabelMap();
        assertEquals(labelMapIn.getLabel(), labelMap.getLabel());
        assertEquals(labelMapIn.getSrp().getOperationId(), labelMap.getSrp().getOperationId());
        assertEquals(labelMapIn.getFec(), labelMap.getFec());

    }

    @Test
    public void testRemoveDownloadLabel() throws Exception {
        this.listener.onSessionUp(this.session);

        // remove-label
        this.topologyRpcs.removeLabel(createRemoveDownloadLabelInput());
        assertEquals(1, this.receivedMsgs.size());
        assertTrue(this.receivedMsgs.get(0) instanceof Pclabelupd);
        final Pclabelupd pclabelupd = (Pclabelupd) this.receivedMsgs.get(0);
        final PceLabelUpdates req = pclabelupd.getPclabelupdMessage().getPceLabelUpdates().get(0);
        final PceLabelUpdateType pceLabelUpdateType = req.getPceLabelUpdateType();
        final PceLabelDownloadCase labelDownloadCase = (PceLabelDownloadCase)pceLabelUpdateType;
        final PceLabelDownload labelDownload = labelDownloadCase.getPceLabelDownload();
        RemoveLabelInput removeLabelInput = createRemoveDownloadLabelInput();
        Arguments5 arguments = removeLabelInput.getArguments().getAugmentation(Arguments5.class);
        final PceLabelUpdateType pceLabelUpdateTypeIn =arguments.getPceLabelUpdateType();
        final PceLabelDownloadCase labelDownloadCaseIn = (PceLabelDownloadCase)pceLabelUpdateTypeIn;
        final PceLabelDownload labelDownloadIn = labelDownloadCaseIn.getPceLabelDownload();
        assertEquals(labelDownloadIn.getLabel(), labelDownload.getLabel());
        assertEquals(labelDownloadIn.getSrp().getOperationId(), labelDownload.getSrp().getOperationId());
        assertEquals(labelDownloadIn.getLsp(), labelDownload.getLsp());

    }

    @Test
    public void testRemoveMapLabel() throws Exception {
        this.listener.onSessionUp(this.session);
        // remove-label
        this.topologyRpcs.removeLabel(createRemoveMapLabelInput());
        assertEquals(1, this.receivedMsgs.size());
        assertTrue(this.receivedMsgs.get(0) instanceof Pclabelupd);
        final Pclabelupd pclabelupd = (Pclabelupd) this.receivedMsgs.get(0);
        final PceLabelUpdates req = pclabelupd.getPclabelupdMessage().getPceLabelUpdates().get(0);
        final PceLabelUpdateType pceLabelUpdateType = req.getPceLabelUpdateType();
        final PceLabelMapCase labelMapCase = (PceLabelMapCase)pceLabelUpdateType;
        final PceLabelMap labelMap = labelMapCase.getPceLabelMap();

        RemoveLabelInput removeLabelInput = createRemoveMapLabelInput();
        Arguments5 arguments = removeLabelInput.getArguments().getAugmentation(Arguments5.class);
        final PceLabelUpdateType pceLabelUpdateTypeIn =arguments.getPceLabelUpdateType();
        final PceLabelMapCase labelMapCaseIn = (PceLabelMapCase)pceLabelUpdateTypeIn;
        final PceLabelMap labelMapIn = labelMapCaseIn.getPceLabelMap();
        assertEquals(labelMapIn.getLabel(), labelMap.getLabel());
        assertEquals(labelMapIn.getSrp().getOperationId(), labelMap.getSrp().getOperationId());
        assertEquals(labelMapIn.getFec(), labelMap.getFec());

    }


    private RemoveLabelInput createRemoveDownloadLabelInput() {
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.label.args.ArgumentsBuilder argsBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.label.args.ArgumentsBuilder();
        final List<Label> labelList = Lists.newArrayList();
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.Ipv4CaseBuilder ipv4Builder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.Ipv4CaseBuilder();
        ipv4Builder.setIpv4(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.ipv4._case.Ipv4Builder().setIpv4ExtendedTunnelId(new Ipv4ExtendedTunnelId(new Ipv4Address(TEST_ADDRESS))).setIpv4TunnelEndpointAddress(new Ipv4Address(TEST_ADDRESS)).setIpv4TunnelSenderAddress(new Ipv4Address(TEST_ADDRESS)).build());

        PceLabelDownloadCaseBuilder pceLabelDownloadCaseBuilder = new PceLabelDownloadCaseBuilder();
        PceLabelDownloadBuilder pceLabelDownload = new PceLabelDownloadBuilder();
        final FecBuilder fecBuilder = new FecBuilder();
        fecBuilder.setProcessingRule(false);
        fecBuilder.setIgnore(false);
        fecBuilder.setFec(new Ipv4NodeIdCaseBuilder().setNodeId(new Ipv4Address(TEST_ADDRESS)).build());

        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder tlvBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder();
        AddressBuilder addressBuilder =  new AddressBuilder();
        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.ipv4._case.Ipv4Builder ipv4 =  new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.ipv4._case.Ipv4Builder();
        ipv4.setIpv4Address(new Ipv4Address(TEST_ADDRESS)).build();
        addressBuilder.setAddressFamily(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.Ipv4CaseBuilder().setIpv4(ipv4.build()).build());

        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder labelBuilder =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder();
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder label =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder();
        label.setOutLabel(false);
        label.setIgnore(false);
        label.setProcessingRule(false);
        label.setLabelNum(new LabelNumber(5001L));
        label.setTlvs(tlvBuilder.setAddress(addressBuilder.build()).build());
        labelBuilder.setLabel(label.build());

        labelList.add(labelBuilder.build());
        final SrpBuilder srpBuilder = new SrpBuilder();
        srpBuilder.setProcessingRule(true);
        srpBuilder.setOperationId(new SrpIdNumber(1L));
        srpBuilder.setTlvs(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.srp.TlvsBuilder().build());

        final LspBuilder lspBuilder = new LspBuilder();
        lspBuilder.setAdministrative(false);
        lspBuilder.setDelegate(false);
        lspBuilder.setPlspId(new PlspId(0L));
        lspBuilder.setOperational(OperationalStatus.Down);
        lspBuilder.setTlvs(
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.lsp.TlvsBuilder()
                        .setLspIdentifiers(new LspIdentifiersBuilder().setAddressFamily(ipv4Builder.build())
                                .setTunnelId(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev150820.TunnelId(1))
                                .setLspId(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev150820.LspId(1L)).build()).build());

        pceLabelDownload.setLabel(labelList).build();
        pceLabelDownload.setSrp(srpBuilder.build()).build();
        pceLabelDownload.setLsp(lspBuilder.build());
        pceLabelDownloadCaseBuilder.setPceLabelDownload(pceLabelDownload.build()).build();
        argsBuilder.addAugmentation(Arguments5.class, new Arguments5Builder().setPceLabelUpdateType(pceLabelDownloadCaseBuilder.build()).build());
        return new RemoveLabelInputBuilder().setArguments(argsBuilder.build()).setNetworkTopologyRef(new NetworkTopologyRef(TOPO_IID)).setNode(NODE_ID).build();

    }

    private RemoveLabelInput createRemoveMapLabelInput() {
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.label.args.ArgumentsBuilder argsBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.label.args.ArgumentsBuilder();
        final PclabelupdMessageBuilder builder = new PclabelupdMessageBuilder();
        final List<PceLabelUpdates> updates = Lists.newArrayList();
        PceLabelMapCaseBuilder pceLabelMapCaseBuilder = new PceLabelMapCaseBuilder();
        PceLabelMapBuilder pceLabelMap = new PceLabelMapBuilder();
        final FecBuilder fecBuilder = new FecBuilder();
        fecBuilder.setFec(new Ipv4NodeIdCaseBuilder().setNodeId(new Ipv4Address(TEST_ADDRESS)).build());

        final LabelBuilder labelBuilder = new LabelBuilder();
        labelBuilder.setLabelNum(new LabelNumber(5001L));
        labelBuilder.setOutLabel(false);

        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder tlvBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder();
        AddressBuilder addressBuilder =  new AddressBuilder();
        Ipv4Builder ipv4 =  new Ipv4Builder();
        ipv4.setIpv4Address(new Ipv4Address(TEST_ADDRESS)).build();
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
        argsBuilder.addAugmentation(Arguments5.class, new Arguments5Builder().setPceLabelUpdateType(pceLabelMapCaseBuilder.build()).build());
        return new RemoveLabelInputBuilder().setArguments(argsBuilder.build()).setNetworkTopologyRef(new NetworkTopologyRef(TOPO_IID)).setNode(NODE_ID).build();
    }
    @Override
    protected Open getLocalPref() {
        return new OpenBuilder(super.getLocalPref()).setTlvs(new TlvsBuilder()
                .addAugmentation(Tlvs1.class, new Tlvs1Builder().setStateful(new StatefulBuilder()
                .addAugmentation(Stateful1.class, new Stateful1Builder().setInitiation(Boolean.TRUE).build())
                .addAugmentation(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.pcep.sync.optimizations.rev150714.Stateful1.class, new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.pcep.sync.optimizations.rev150714.Stateful1Builder().setTriggeredInitialSync(Boolean.TRUE).build()).build()).build())
                .addAugmentation(Tlvs4.class, new Tlvs4Builder().setPceccCapability(new PceccCapabilityBuilder().setIldbBit(true).setSBit(true).build()).build())
                .build()).build();
    }

    @Override
    protected Open getRemotePref() {
        return getLocalPref();
    }


    private AddLabelInput createAddLabelDownloadInput() {
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.label.args.ArgumentsBuilder argsBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.label.args.ArgumentsBuilder();
        final List<Label> labelList = Lists.newArrayList();
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.Ipv4CaseBuilder ipv4Builder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.Ipv4CaseBuilder();
        ipv4Builder.setIpv4(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.ipv4._case.Ipv4Builder().setIpv4ExtendedTunnelId(new Ipv4ExtendedTunnelId(new Ipv4Address(TEST_ADDRESS))).setIpv4TunnelEndpointAddress(new Ipv4Address(TEST_ADDRESS)).setIpv4TunnelSenderAddress(new Ipv4Address(TEST_ADDRESS)).build());

        PceLabelDownloadCaseBuilder pceLabelDownloadCaseBuilder = new PceLabelDownloadCaseBuilder();
        PceLabelDownloadBuilder pceLabelDownload = new PceLabelDownloadBuilder();
        final FecBuilder fecBuilder = new FecBuilder();
        fecBuilder.setProcessingRule(false);
        fecBuilder.setIgnore(false);
        fecBuilder.setFec(new Ipv4NodeIdCaseBuilder().setNodeId(new Ipv4Address(TEST_ADDRESS)).build());

        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder tlvBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder();
        AddressBuilder addressBuilder =  new AddressBuilder();
        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.ipv4._case.Ipv4Builder ipv4 =  new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.ipv4._case.Ipv4Builder();
        ipv4.setIpv4Address(new Ipv4Address(TEST_ADDRESS)).build();
        addressBuilder.setAddressFamily(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.Ipv4CaseBuilder().setIpv4(ipv4.build()).build());

        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder labelBuilder =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder();
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder label =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder();
        label.setOutLabel(false);
        label.setIgnore(false);
        label.setProcessingRule(false);
        label.setLabelNum(new LabelNumber(5001L));
        label.setTlvs(tlvBuilder.setAddress(addressBuilder.build()).build());
        labelBuilder.setLabel(label.build());
        labelList.add(labelBuilder.build());
        final SrpBuilder srpBuilder = new SrpBuilder();
        srpBuilder.setProcessingRule(true);
        srpBuilder.setOperationId(new SrpIdNumber(1L));
        srpBuilder.setTlvs(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.srp.TlvsBuilder().build());

        final LspBuilder lspBuilder = new LspBuilder();
        lspBuilder.setAdministrative(false);
        lspBuilder.setDelegate(false);
        lspBuilder.setPlspId(new PlspId(0L));
        lspBuilder.setOperational(OperationalStatus.Down);
        lspBuilder.setTlvs(
                        new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.lsp.TlvsBuilder()
                                .setLspIdentifiers(new LspIdentifiersBuilder().setAddressFamily(ipv4Builder.build())
                                        .setTunnelId(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev150820.TunnelId(1))
                                        .setLspId(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev150820.LspId(1L)).build()).build());

        pceLabelDownload.setLabel(labelList).build();
        pceLabelDownload.setSrp(srpBuilder.build()).build();
        pceLabelDownload.setLsp(lspBuilder.build());
        pceLabelDownloadCaseBuilder.setPceLabelDownload(pceLabelDownload.build()).build();
        argsBuilder.addAugmentation(Arguments4.class, new Arguments4Builder().setPceLabelUpdateType(pceLabelDownloadCaseBuilder.build()).build());
        return new AddLabelInputBuilder().setArguments(argsBuilder.build()).setNetworkTopologyRef(new NetworkTopologyRef(TOPO_IID)).setNode(NODE_ID).build();
    }


    private AddLabelInput createAddLabelMapInput() {
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.label.args.ArgumentsBuilder argsBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.label.args.ArgumentsBuilder();
        final PclabelupdMessageBuilder builder = new PclabelupdMessageBuilder();
        final List<PceLabelUpdates> updates = Lists.newArrayList();
        PceLabelMapCaseBuilder pceLabelMapCaseBuilder = new PceLabelMapCaseBuilder();
        PceLabelMapBuilder pceLabelMap = new PceLabelMapBuilder();
        final FecBuilder fecBuilder = new FecBuilder();
        fecBuilder.setFec(new Ipv4NodeIdCaseBuilder().setNodeId(new Ipv4Address(TEST_ADDRESS)).build());

        final LabelBuilder labelBuilder = new LabelBuilder();
        labelBuilder.setLabelNum(new LabelNumber(5001L));
        labelBuilder.setOutLabel(false);

        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder tlvBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder();
        AddressBuilder addressBuilder =  new AddressBuilder();
        Ipv4Builder ipv4 =  new Ipv4Builder();
        ipv4.setIpv4Address(new Ipv4Address(TEST_ADDRESS)).build();
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
        argsBuilder.addAugmentation(Arguments4.class, new Arguments4Builder().setPceLabelUpdateType(pceLabelMapCaseBuilder.build()).build());
        return new AddLabelInputBuilder().setArguments(argsBuilder.build()).setNetworkTopologyRef(new NetworkTopologyRef(TOPO_IID)).setNode(NODE_ID).build();
    }

}
