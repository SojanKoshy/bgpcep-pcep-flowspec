/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.bgpcep.pcep.topology.provider;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.concurrent.GuardedBy;
import org.opendaylight.controller.config.yang.pcep.topology.provider.PeerCapabilities;
import org.opendaylight.protocol.pcep.PCEPSession;
import org.opendaylight.protocol.pcep.spi.PCEPErrors;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.pcep.sync.optimizations.rev150714.PathComputationClient1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.pcep.sync.optimizations.rev150714.PathComputationClient1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.pcep.sync.optimizations.rev150714.lsp.db.version.tlv.LspDbVersion;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.crabbe.initiated.rev131126.PcinitiateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.crabbe.initiated.rev131126.Srp1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.crabbe.initiated.rev131126.Srp1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.crabbe.initiated.rev131126.Stateful1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.crabbe.initiated.rev131126.pcinitiate.message.PcinitiateMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.crabbe.initiated.rev131126.pcinitiate.message.pcinitiate.message.Requests;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.crabbe.initiated.rev131126.pcinitiate.message.pcinitiate.message.RequestsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Arguments6;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Arguments7;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.FlowspecBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Tlvs5;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.add.or.update.flowspec.input.arguments.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.flow.flowspec.filter.type.FlowspecFilterV4Case;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.flow.flowspec.filter.type.FlowspecFilterV4CaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.message.FlowspecMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.message.flowspec.message.PceFlowspecBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.capability.tlv.PceFlowspecCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.PceFlowspecAddUpdateCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.PceFlowspecDeleteCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.pce.flowspec.add.update._case.PceFlowspecAddUpdateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.pce.flowspec.add.update._case.pce.flowspec.add.update.ActionList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.pce.flowspec.add.update._case.pce.flowspec.add.update.ActionListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.pce.flowspec.delete._case.PceFlowspecDeleteBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Arguments1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Arguments2;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Arguments3;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.OperationalStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Path1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Path1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.PcrptMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.PcupdBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.PlspId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.SrpIdNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.StatefulTlv1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.StatefulTlv1Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.SymbolicPathName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.Tlvs1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.LspIdentifiersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.Ipv4Case;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.Ipv4CaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.identifiers.tlv.lsp.identifiers.address.family.ipv4._case.Ipv4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.Lsp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.LspBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.lsp.TlvsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcerr.pcerr.message.error.type.StatefulCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcerr.pcerr.message.error.type.stateful._case.stateful.Srps;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcrpt.message.pcrpt.message.Reports;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcupd.message.PcupdMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcupd.message.pcupd.message.UpdatesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcupd.message.pcupd.message.updates.PathBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.Srp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.SrpBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.stateful.capability.tlv.Stateful;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.symbolic.path.name.tlv.SymbolicPathNameBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Arguments4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Arguments5;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.PclabelupdBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.Tlvs4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.fec.object.FecBuilder;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pcecc.capability.tlv.PceccCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pclabelupd.message.PclabelupdMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pclabelupd.message.pclabelupd.message.PceLabelUpdatesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Message;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.PcerrMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.EroBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.open.Tlvs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.path.setup.type.tlv.PathSetupType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev150820.Ipv4ExtendedTunnelId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev150820.TunnelId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.AddLspArgs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.EnsureLspOperationalInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.FlowspecArgs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.LabelArgs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.LspId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.Node1;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.OperationResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.PccSyncState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.RemoveLspArgs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.TriggerSyncArgs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.UpdateLspArgs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.PathComputationClient;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.PathComputationClientBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.ReportedLsp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.ReportedLspBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.StatefulTlvBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.reported.lsp.Path;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Stateful07TopologySessionListener extends AbstractTopologySessionListener<SrpIdNumber, PlspId> {
    private static final Logger LOG = LoggerFactory.getLogger(Stateful07TopologySessionListener.class);

    private final AtomicLong requestId = new AtomicLong(1L);

    @GuardedBy("this")
    private final List<PlspId> staleLsps = new ArrayList<>();

    /**
     * Creates a new stateful topology session listener for given server session manager.
     *
     * @param serverSessionManager
     */
    Stateful07TopologySessionListener(final ServerSessionManager serverSessionManager) {
        super(serverSessionManager);
    }

    @Override
    protected void onSessionUp(final PCEPSession session, final PathComputationClientBuilder pccBuilder) {
        final InetAddress peerAddress = session.getRemoteAddress();

        final Tlvs tlvs = session.getRemoteTlvs();
        if (tlvs != null && tlvs.getAugmentation(Tlvs1.class) != null) {
            final Stateful stateful = tlvs.getAugmentation(Tlvs1.class).getStateful();
            if (stateful != null) {
                getSessionListenerState().setPeerCapabilities(getCapabilities(stateful));
                pccBuilder.setReportedLsp(Collections.<ReportedLsp> emptyList());
                if (isSynchronized()) {
                    pccBuilder.setStateSync(PccSyncState.Synchronized);
                } else if (isTriggeredInitialSynchro()) {
                    pccBuilder.setStateSync(PccSyncState.TriggeredInitialSync);
                } else if (isIncrementalSynchro()) {
                    pccBuilder.setStateSync(PccSyncState.IncrementalSync);
                } else {
                    pccBuilder.setStateSync(PccSyncState.InitialResync);
                }
                pccBuilder.setStatefulTlv(new StatefulTlvBuilder().addAugmentation(StatefulTlv1.class,
                    new StatefulTlv1Builder(tlvs.getAugmentation(Tlvs1.class)).build()).build());
            } else {
                LOG.debug("Peer {} does not advertise stateful TLV", peerAddress);
            }

        } else {
            LOG.debug("Peer {} does not advertise stateful TLV", peerAddress);
        }
        if (tlvs != null && tlvs.getAugmentation(Tlvs4.class) != null) {
            final PceccCapability pceccCapability = tlvs.getAugmentation(Tlvs4.class).getPceccCapability();
            if (pceccCapability != null) {
                setPceccCapable(true);
            } else {
                LOG.debug("Peer {} does not advertise PCECC capability TLV", peerAddress);
            }
        } else if (tlvs != null && tlvs.getAugmentation(Tlvs5.class) != null) {
            final PceFlowspecCapability flowspecCapability = tlvs.getAugmentation(Tlvs5.class).getPceFlowspecCapability();
            if (flowspecCapability != null) {
                setFlowspecCapable(true);
            } else {
                LOG.debug("Peer {} does not advertise Flowspec capability TLV", peerAddress);
            }
        }
        else {
            LOG.debug("Peer {} does not advertise PCECC capability TLV", peerAddress);
        }
    }

    /**
     * @param input
     * @return
     */
    @Override
    public synchronized ListenableFuture<OperationResult> triggerSync(final TriggerSyncArgs input) {
        if (isTriggeredInitialSynchro() && !isSynchronized()) {
            return triggerSynchronization(input);
        } else if (getSynchronized() && isTriggeredReSyncEnabled()) {
            Preconditions.checkArgument(input != null && input.getNode() != null, MISSING_XML_TAG);
            if (input.getName() == null) {
                return triggerResyncronization(input);
            } else {
                return triggerLspSyncronization(input);
            }
        }
        return OperationResults.UNSENT.future();
    }

    @Override
    public ListenableFuture<OperationResult> addLabel(LabelArgs input) {
        Preconditions.checkArgument(input != null, MISSING_XML_TAG);
        LOG.trace("AddLabelArgs {}", input);

        // check if the peer is PCECC capable
        if (!isPceccCapable()) {
            return OperationResults.createUnsent(PCEPErrors.CAPABILITY_NOT_SUPPORTED).future();
        }

        final Arguments4 args = input.getArguments().getAugmentation(Arguments4.class);

        if (args == null) {
            LOG.warn("Node {} does not contain mandatory data", input.getNode());
            return OperationResults.UNSENT.future();
        }
        final PceLabelUpdateType labelType = args.getPceLabelUpdateType();

        if (labelType == null) {
            LOG.warn("Node {} does not contain mandatory data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        if (labelType instanceof PceLabelDownloadCase)
        {
            return downloadLabel(input, false);
        } else if (labelType instanceof PceLabelMapCase) {
            return updateLabelMap(input, false);
        } else {
            LOG.warn("Node {} contains unknown label type", input.getNode());
            return OperationResults.UNSENT.future();
        }
    }

    @Override
    public ListenableFuture<OperationResult> removeLabel(LabelArgs input) {
        Preconditions.checkArgument(input != null, MISSING_XML_TAG);
        LOG.trace("RemoveLabelArgs {}", input);

        // check if the peer is PCECC capable
        if (!isPceccCapable()) {
            return OperationResults.createUnsent(PCEPErrors.CAPABILITY_NOT_SUPPORTED).future();
        }

        final Arguments5 args = input.getArguments().getAugmentation(Arguments5.class);

        if (args == null) {
            LOG.warn("Node {} does not contain mandatory data", input.getNode());
            return OperationResults.UNSENT.future();
        }
        final PceLabelUpdateType labelType = args.getPceLabelUpdateType();

        if (labelType == null) {
            LOG.warn("Node {} does not contain mandatory data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        if (labelType instanceof PceLabelDownloadCase)
        {
            return downloadLabel(input, true);
        } else if (labelType instanceof PceLabelMapCase) {
            return updateLabelMap(input, true);
        } else {
            LOG.warn("Node {} contains unknown label type", input.getNode());
            return OperationResults.UNSENT.future();
        }
    }

    private ListenableFuture<OperationResult> downloadLabel(LabelArgs input, boolean removeFlag) {
        Preconditions.checkArgument(input != null, MISSING_XML_TAG);
        LOG.trace("AddLabelArgs {}", input);

        SrpIdNumber srpIdNumber = new SrpIdNumber(1L);
        final PceLabelDownloadBuilder labelDownloadBuilder = new PceLabelDownloadBuilder();
        final PceLabelDownloadCaseBuilder labelDownloadCaseBuilder = new PceLabelDownloadCaseBuilder();
        final PceLabelUpdatesBuilder labelUpdatesBuilder = new PceLabelUpdatesBuilder();

        PceLabelUpdateType labelType;
        if (removeFlag == false)
        {
            final Arguments4 args = input.getArguments().getAugmentation(Arguments4.class);
            labelType = args.getPceLabelUpdateType();
        }
        else
        {
            final Arguments5 args = input.getArguments().getAugmentation(Arguments5.class);
            labelType = args.getPceLabelUpdateType();
        }


        final PceLabelDownloadCase labelDownloadCase = (PceLabelDownloadCase)labelType;
        final PceLabelDownload labelDownload = labelDownloadCase.getPceLabelDownload();

        if (labelDownload.getSrp() == null) {
            LOG.warn("Node {} does not contain SRP data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        final SrpBuilder srpBuilder = new SrpBuilder().addAugmentation(Srp1.class, new Srp1Builder()
                .setRemove(removeFlag).build()).setOperationId(nextRequest()).setProcessingRule(Boolean.TRUE)
                .setTlvs(labelDownload.getSrp().getTlvs()); // Need to test the TLV

        final Srp srp = srpBuilder.build();

        srpIdNumber = srp.getOperationId();
        labelDownloadBuilder.setSrp(srp);

        if (labelDownload.getLsp() == null){
            LOG.warn("Node {} does not contain LSP data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        Ipv4Case ipv4Case = (Ipv4Case) labelDownload.getLsp().getTlvs().getLspIdentifiers().getAddressFamily();

        final TlvsBuilder lspTlvBuilder = new TlvsBuilder().setLspIdentifiers(new LspIdentifiersBuilder()
                .setLspId(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev150820.LspId(
                        labelDownload.getLsp().getTlvs().getLspIdentifiers().getLspId().getValue()))
                .setTunnelId(new TunnelId(
                        labelDownload.getLsp().getTlvs().getLspIdentifiers().getTunnelId().getValue()))
                .setAddressFamily(new Ipv4CaseBuilder().setIpv4(new Ipv4Builder()
                        .setIpv4TunnelEndpointAddress(new Ipv4Address(ipv4Case.getIpv4().getIpv4TunnelEndpointAddress()))
                        .setIpv4TunnelSenderAddress(new Ipv4Address(ipv4Case.getIpv4().getIpv4TunnelSenderAddress()))
                        .setIpv4ExtendedTunnelId(new Ipv4ExtendedTunnelId(ipv4Case.getIpv4().getIpv4ExtendedTunnelId()))
                        .build()).build()).build());

        labelDownloadBuilder.setLsp(new LspBuilder()
                .setPlspId(labelDownload.getLsp().getPlspId())
                .setDelegate(labelDownload.getLsp().isDelegate())
                .setAdministrative(labelDownload.getLsp().isAdministrative())
                .setOperational(labelDownload.getLsp().getOperational())
                .setTlvs(lspTlvBuilder.build()).build());

        if (labelDownload.getLabel() == null){
            LOG.warn("Node {} does not contain Label list data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        final List<Label> labelList = new ArrayList<>();

        for (final Label label : labelDownload.getLabel()) {
            org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder lbl
                    = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.pce.label.update.pce.label.update.type.pce.label.download._case.pce.label.download.LabelBuilder().setLabel(label.getLabel());
            labelList.add(lbl.build());
        }

        labelDownloadBuilder.setLabel(labelList); // FIXME: This will not work need to check
        labelDownloadCaseBuilder.setPceLabelDownload(labelDownloadBuilder.build());
        labelUpdatesBuilder.setPceLabelUpdateType(labelDownloadCaseBuilder.build());

        final PclabelupdMessageBuilder pclabelupdMessageBuilder = new PclabelupdMessageBuilder(MESSAGE_HEADER);
        pclabelupdMessageBuilder.setPceLabelUpdates(Collections.singletonList(labelUpdatesBuilder.build()));
        final Message msg = new PclabelupdBuilder().setPclabelupdMessage(pclabelupdMessageBuilder.build()).build();
        sendLabelMessage(msg, srpIdNumber, null);
        return OperationResults.SUCCESS.future();
    }

    private ListenableFuture<OperationResult> updateLabelMap(LabelArgs input, boolean removeFlag) {
        Preconditions.checkArgument(input != null, MISSING_XML_TAG);
        LOG.trace("LabelArgs {}", input);

        SrpIdNumber srpIdNumber = new SrpIdNumber(1L);
        final PceLabelMapBuilder labelMapBuilder = new PceLabelMapBuilder();
        final PceLabelMapCaseBuilder labelMapCaseBuilder = new PceLabelMapCaseBuilder();
        final PceLabelUpdatesBuilder labelUpdatesBuilder = new PceLabelUpdatesBuilder();

        PceLabelUpdateType labelType;
        if (removeFlag == false)
        {
            final Arguments4 args = input.getArguments().getAugmentation(Arguments4.class);
            labelType = args.getPceLabelUpdateType();
        }
        else
        {
            final Arguments5 args = input.getArguments().getAugmentation(Arguments5.class);
            labelType = args.getPceLabelUpdateType();
        }

        final PceLabelMapCase labelMapCase = (PceLabelMapCase)labelType;
        final PceLabelMap labelMap = labelMapCase.getPceLabelMap();

        if (labelMap.getSrp() == null) {
            LOG.warn("Node {} does not contain SRP data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        // SRP Mandatory in label Upd, since it's present in both label download and label map
        final SrpBuilder srpBuilder = new SrpBuilder().addAugmentation(Srp1.class, new Srp1Builder()
                .setRemove(removeFlag).build()).setOperationId(nextRequest()).setProcessingRule(Boolean.TRUE);

        final Srp srp = srpBuilder.build();

        srpIdNumber = srp.getOperationId();
        labelMapBuilder.setSrp(srp);

        if (labelMap.getLabel() == null){
            LOG.warn("Node {} does not contain Label data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        labelMapBuilder.setLabel(new LabelBuilder().setLabelNum(labelMap.getLabel().getLabelNum()).
                setOutLabel(labelMap.getLabel().isOutLabel()).setTlvs(labelMap.getLabel().getTlvs()).build());

        if (labelMap.getFec() == null){
            LOG.warn("Node {} does not contain Fec data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        labelMapBuilder.setFec(new FecBuilder().setFec(labelMap.getFec().getFec()).build());
        labelMapCaseBuilder.setPceLabelMap(labelMapBuilder.build());
        labelUpdatesBuilder.setPceLabelUpdateType(labelMapCaseBuilder.build());

        final PclabelupdMessageBuilder pclabelupdMessageBuilder = new PclabelupdMessageBuilder(MESSAGE_HEADER);
        pclabelupdMessageBuilder.setPceLabelUpdates(Collections.singletonList(labelUpdatesBuilder.build()));
        final Message msg = new PclabelupdBuilder().setPclabelupdMessage(pclabelupdMessageBuilder.build()).build();
        sendLabelMessage(msg, srpIdNumber, null);
        return OperationResults.SUCCESS.future();
    }


    @Override
    public ListenableFuture<OperationResult> addOrUpdateFlowspec(FlowspecArgs input) {
        Preconditions.checkArgument(input != null, MISSING_XML_TAG);
        LOG.trace("AddFlowspecArgs {}", input);

        // check if the peer is Flowspec capable
        if (!isFlowspecCapable()) {
            return OperationResults.createUnsent(PCEPErrors.CAPABILITY_NOT_SUPPORTED).future();
        }

        final Arguments6 args = input.getArguments().getAugmentation(Arguments6.class);

        if (args == null) {
            LOG.warn("Node {} does not contain mandatory data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        final PceFlowspecAddUpdateCaseBuilder flowspecAddUpdateCase = new PceFlowspecAddUpdateCaseBuilder();
        final PceFlowspecAddUpdateBuilder flowspecAddUpdate = new PceFlowspecAddUpdateBuilder();
        final PceFlowspecBuilder pceFlowspecBuilder = new PceFlowspecBuilder();

        SrpIdNumber srpIdNumber = new SrpIdNumber(1L);

        // SRP Mandatory in Flowspec message
        final SrpBuilder srpBuilder = new SrpBuilder().addAugmentation(Srp1.class, new Srp1Builder()
                .setRemove(false).build()).setOperationId(nextRequest()).setProcessingRule(Boolean.TRUE)
                .addAugmentation(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Srp1.class, new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Srp1Builder()
                        .setSync(args.isSync()).build());

        final Srp srp = srpBuilder.build();

        srpIdNumber = srp.getOperationId();

        flowspecAddUpdate.setSrp(srp);

        if(args.getFlow() == null) {
            LOG.warn("Node {} does not contain Flow data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        final FlowBuilder flowBuilder = new FlowBuilder().setFsId(args.getFlow().getFsId());

        if (args.getFlow().getFlowspecFilterType() instanceof FlowspecFilterV4Case) {

            final FlowspecFilterV4Case flowspecFilterV4Case = (FlowspecFilterV4Case) args.getFlow().getFlowspecFilterType();
            final FlowspecFilterV4CaseBuilder flowspecFilterV4CaseBuilder = new FlowspecFilterV4CaseBuilder();
            flowspecFilterV4CaseBuilder.setTlvs(flowspecFilterV4Case.getTlvs());

            flowBuilder.setFlowspecFilterType(flowspecFilterV4CaseBuilder.build());
        }

        final Flow flow = flowBuilder.build();

        flowspecAddUpdate.setFlow(flow);

        if(args.getAction() == null) {
            LOG.warn("Node {} does not contain Action data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        final List<ActionList> actionList = new ArrayList<>();

        for (final Action action : args.getAction()) {
            ActionListBuilder actionBuilder
                    = new ActionListBuilder().setAction(action.getAction());
            actionList.add(actionBuilder.build());
        }

        flowspecAddUpdate.setActionList(actionList);

        flowspecAddUpdateCase.setPceFlowspecAddUpdate(flowspecAddUpdate.build());
        pceFlowspecBuilder.setPceFlowspecType(flowspecAddUpdateCase.build());

        final FlowspecMessageBuilder flowspecMessageBuilder = new FlowspecMessageBuilder(MESSAGE_HEADER);
        flowspecMessageBuilder.setPceFlowspec(Collections.singletonList(pceFlowspecBuilder.build()));

        final Message msg = new FlowspecBuilder().setFlowspecMessage(flowspecMessageBuilder.build()).build();
        sendLabelMessage(msg, srpIdNumber, null);
        return OperationResults.SUCCESS.future();
    }

    @Override
    public ListenableFuture<OperationResult> removeFlowspec(FlowspecArgs input) {
        Preconditions.checkArgument(input != null, MISSING_XML_TAG);
        LOG.trace("RemoveFlowspecArgs {}", input);

        // check if the peer is Flowspec capable
        if (!isFlowspecCapable()) {
            return OperationResults.createUnsent(PCEPErrors.CAPABILITY_NOT_SUPPORTED).future();
        }

        final Arguments7 args = input.getArguments().getAugmentation(Arguments7.class);

        if (args == null) {
            LOG.warn("Node {} does not contain mandatory data", input.getNode());
            return OperationResults.UNSENT.future();
        }

        final PceFlowspecDeleteCaseBuilder flowspecDeleteCase = new PceFlowspecDeleteCaseBuilder();
        final PceFlowspecDeleteBuilder flowspecDelete = new PceFlowspecDeleteBuilder();
        final PceFlowspecBuilder pceFlowspecBuilder = new PceFlowspecBuilder();

        SrpIdNumber srpIdNumber = new SrpIdNumber(1L);

        // SRP Mandatory in Flowspec
        final SrpBuilder srpBuilder = new SrpBuilder().addAugmentation(Srp1.class, new Srp1Builder()
                .setRemove(true).build()).setOperationId(nextRequest()).setProcessingRule(Boolean.TRUE);

        final Srp srp = srpBuilder.build();

        srpIdNumber = srp.getOperationId();

        flowspecDelete.setSrp(srp);

        final FlowBuilder flowBuilder = new FlowBuilder().setFsId(args.getFsId());

        final Flow flow = flowBuilder.build();

        flowspecDelete.setFlow(flow);

        flowspecDeleteCase.setPceFlowspecDelete(flowspecDelete.build());
        pceFlowspecBuilder.setPceFlowspecType(flowspecDeleteCase.build());

        final FlowspecMessageBuilder flowspecMessageBuilder = new FlowspecMessageBuilder(MESSAGE_HEADER);
        flowspecMessageBuilder.setPceFlowspec(Collections.singletonList(pceFlowspecBuilder.build()));

        final Message msg = new FlowspecBuilder().setFlowspecMessage(flowspecMessageBuilder.build()).build();
        sendLabelMessage(msg, srpIdNumber, null);
        return OperationResults.SUCCESS.future();
    }

    private ListenableFuture<OperationResult> triggerLspSyncronization(final TriggerSyncArgs input) {
        LOG.trace("Trigger Lsp Resynchronization {}", input);

        // Make sure the LSP exists
        final InstanceIdentifier<ReportedLsp> lsp = lspIdentifier(input.getName());
        final ListenableFuture<Optional<ReportedLsp>> f = readOperationalData(lsp);
        if (f == null) {
            return OperationResults.createUnsent(PCEPErrors.LSP_INTERNAL_ERROR).future();
        }
        return Futures.transform(f, new ResyncLspFunction(input));
    }

    private ListenableFuture<OperationResult> triggerResyncronization(final TriggerSyncArgs input) {
        LOG.trace("Trigger Resynchronization {}", input);
        markAllLspAsStale();
        updatePccState(PccSyncState.PcepTriggeredResync);
        final PcupdMessageBuilder pcupdMessageBuilder = new PcupdMessageBuilder(MESSAGE_HEADER);
        final SrpIdNumber srpIdNumber = createUpdateMessageSync(pcupdMessageBuilder);
        final Message msg = new PcupdBuilder().setPcupdMessage(pcupdMessageBuilder.build()).build();
        return sendMessage(msg, srpIdNumber, null);
    }

    private ListenableFuture<OperationResult> triggerSynchronization(final TriggerSyncArgs input) {
        LOG.trace("Trigger Initial Synchronization {}", input);
        final PcupdMessageBuilder pcupdMessageBuilder = new PcupdMessageBuilder(MESSAGE_HEADER);
        final SrpIdNumber srpIdNumber = createUpdateMessageSync(pcupdMessageBuilder);
        final Message msg = new PcupdBuilder().setPcupdMessage(pcupdMessageBuilder.build()).build();
        return sendMessage(msg, srpIdNumber, null);
    }

    private SrpIdNumber createUpdateMessageSync(final PcupdMessageBuilder pcupdMessageBuilder) {
        final UpdatesBuilder updBuilder = new UpdatesBuilder();
        // LSP mandatory in Upd
        final Lsp lsp = new LspBuilder().setPlspId(new PlspId(0L)).setSync(Boolean.TRUE).build();
        // SRP Mandatory in Upd
        final SrpBuilder srpBuilder = new SrpBuilder();
        // not sue whether use 0 instead of nextRequest() or do not insert srp == SRP-ID-number = 0
        srpBuilder.setOperationId(nextRequest());
        final Srp srp = srpBuilder.build();
        //ERO Mandatory in Upd
        final PathBuilder pb = new PathBuilder();
        pb.setEro(new EroBuilder().build());

        updBuilder.setPath(pb.build());
        updBuilder.setLsp(lsp).setSrp(srp).setPath(pb.build());

        pcupdMessageBuilder.setUpdates(Collections.singletonList(updBuilder.build()));
        return srp.getOperationId();
    }

    private void markAllLspAsStale() {
        for (final PlspId plspId : lsps.keySet()) {
            staleLsps.add(plspId);
        }
    }

    private class ResyncLspFunction implements AsyncFunction<Optional<ReportedLsp>, OperationResult>  {

        private final TriggerSyncArgs input;

        public ResyncLspFunction(final TriggerSyncArgs input) {
            this.input = input;
        }

        @Override
        public ListenableFuture<OperationResult> apply(final Optional<ReportedLsp> rep) {
            final Lsp reportedLsp = validateReportedLsp(rep, this.input);
            if (reportedLsp == null || !rep.isPresent()) {
                return OperationResults.createUnsent(PCEPErrors.UNKNOWN_PLSP_ID).future();
            }
            // mark lsp as stale
            final ReportedLsp staleLsp = rep.get();
            if (!staleLsp.getPath().isEmpty()) {
                final Path1 path1 = staleLsp.getPath().get(0).getAugmentation(Path1.class);
                if (path1 != null) {
                    staleLsps.add(path1.getLsp().getPlspId());
                }
            }
            updatePccState(PccSyncState.PcepTriggeredResync);
            // create PCUpd with mandatory objects and LSP object set to 1
            final SrpBuilder srpBuilder = new SrpBuilder();
            srpBuilder.setOperationId(nextRequest());
            srpBuilder.setProcessingRule(Boolean.TRUE);

            final Optional<PathSetupType> maybePST = getPST(rep);
            if (maybePST.isPresent()) {
                srpBuilder.setTlvs(
                    new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.srp.TlvsBuilder()
                        .setPathSetupType(maybePST.get()).build());
            }

            final Srp srp = srpBuilder.build();
            final Lsp lsp = new LspBuilder().setPlspId(reportedLsp.getPlspId()).setSync(Boolean.TRUE).build();

            final Message msg = createPcepUpd(srp,lsp);
            return sendMessage(msg, srp.getOperationId(), null);
        }

        private Message createPcepUpd(final Srp srp, final Lsp lsp) {
            final UpdatesBuilder rb = new UpdatesBuilder();
            rb.setSrp(srp);
            rb.setLsp(lsp);
            final PathBuilder pb = new PathBuilder();
            rb.setPath(pb.build());
            final PcupdMessageBuilder ub = new PcupdMessageBuilder(MESSAGE_HEADER);
            ub.setUpdates(Collections.singletonList(rb.build()));
            return new PcupdBuilder().setPcupdMessage(ub.build()).build();
        }
    }

    private boolean handleErrorMessage(final PcerrMessage message) {
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.pcerr.message.PcerrMessage errMsg = message.getPcerrMessage();
        if (errMsg.getErrorType() instanceof StatefulCase) {
            final StatefulCase stat = (StatefulCase)errMsg.getErrorType();
            for (final Srps srps : stat.getStateful().getSrps()) {
                final SrpIdNumber id = srps.getSrp().getOperationId();
                if (id.getValue() != 0) {
                    final PCEPRequest req = removeRequest(id);
                    if (req != null) {
                        req.done(OperationResults.createFailed(errMsg.getErrors()));
                    } else {
                        LOG.warn("Request ID {} not found in outstanding DB", id);
                    }
                }
            }
        } else {
            LOG.warn("Unhandled PCErr message {}.", errMsg);
            return true;
        }
        return false;
    }

    private boolean isSolicited(final Srp srp, final Lsp lsp, final MessageContext ctx, final ReportedLspBuilder rlb) {
        if (srp == null) {
            return false;
        }
        final SrpIdNumber id = srp.getOperationId();
        if (id.getValue() == 0) {
            return false;
        }
        switch (lsp.getOperational()) {
        case Active:
        case Down:
        case Up:
            if(!isTriggeredSyncInProcess()) {
                final PCEPRequest req = removeRequest(id);
                if (req != null) {
                    LOG.debug("Request {} resulted in LSP operational state {}", id, lsp.getOperational());
                    rlb.setMetadata(req.getMetadata());
                    ctx.resolveRequest(req);
                } else {
                    LOG.warn("Request ID {} not found in outstanding DB", id);
                }
            }
            break;
        case GoingDown:
        case GoingUp:
            final short pathSetupType;
            final PathSetupType pst;
            if (srp != null && srp.getTlvs() != null && srp.getTlvs().getPathSetupType() != null) {
                pst = srp.getTlvs().getPathSetupType();
                pathSetupType = pst.getPst();

                final PCEPRequest req = removeRequest(id);
                if (req != null) {
                    LOG.debug("Request {} resulted in LSP operational state for label case {}", id, lsp.getOperational());
                    rlb.setMetadata(req.getMetadata());
                    ctx.resolveRequest(req);
                }
            }
            break;
        default:
            break;
        }
        return true;
    }

    private boolean manageNextReport(final Reports report, final MessageContext ctx) {
        final Lsp lsp = report.getLsp();
        final PlspId plspid = lsp.getPlspId();
        final Srp srp = report.getSrp();

        if (!lsp.isSync() && (plspid == null || plspid.getValue() == 0)) {
            purgeStaleLsps(ctx);
            if(isTriggeredSyncInProcess()) {
                if (srp == null) {
                    return false;
                }
                final SrpIdNumber id = srp.getOperationId();
                if (id.getValue() == 0) {
                    return false;
                }
                final PCEPRequest req = removeRequest(id);
                ctx.resolveRequest(req);
            }
            stateSynchronizationAchieved(ctx);
            return true;
        }
        final ReportedLspBuilder rlb = new ReportedLspBuilder();
        boolean solicited = false;
        solicited = isSolicited(srp, lsp, ctx, rlb);

        // if remove flag is set in SRP object, remove the tunnel immediately
        if (solicited && srp.getAugmentation(Srp1.class) != null) {
            final Srp1 initiatedSrp = srp.getAugmentation(Srp1.class);
            if (initiatedSrp.isRemove()) {
                super.removeLsp(ctx, plspid);
                return false;
            }
        }
        rlb.setPath(Collections.singletonList(buildPath(report, srp, lsp)));

        String name = lookupLspName(plspid);
        if (lsp.getTlvs() != null && lsp.getTlvs().getSymbolicPathName() != null) {
            name = Charsets.UTF_8.decode(ByteBuffer.wrap(lsp.getTlvs().getSymbolicPathName().getPathName().getValue())).toString();
        }
        //get LspDB from LSP and write it to pcc's node
        final LspDbVersion lspDbVersion = geLspDbVersionTlv(lsp);
        if (lspDbVersion != null) {
            updatePccNode(ctx, new PathComputationClientBuilder().addAugmentation(PathComputationClient1.class,
                    new PathComputationClient1Builder().setLspDbVersion(lspDbVersion).build()).build());
        }
        updateLsp(ctx, plspid, name, rlb, solicited, lsp.isRemove());
        unmarkStaleLsp(plspid);

        LOG.debug("LSP {} updated", lsp);
        return true;
    }

    private static LspDbVersion geLspDbVersionTlv(final Lsp lsp) {
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.lsp.Tlvs tlvs = lsp.getTlvs();
        if (tlvs != null && tlvs.getAugmentation(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.pcep.sync.optimizations.rev150714.Tlvs1.class) != null) {
            return tlvs.getAugmentation(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.pcep.sync.optimizations.rev150714.Tlvs1.class).getLspDbVersion();
        }
        return null;
    }

    private Path buildPath(final Reports report, final Srp srp, final Lsp lsp) {
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.reported.lsp.PathBuilder pb = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.reported.lsp.PathBuilder();
        if (report.getPath() != null) {
            pb.fieldsFrom(report.getPath());
        }
        // LSP is mandatory (if there is none, parser will throw an exception)
        // this is to ensure a path will be created at any rate
        final Path1Builder p1Builder = new Path1Builder();
        p1Builder.setLsp(report.getLsp());
        final PathSetupType pst;
        if (srp != null && srp.getTlvs() != null && srp.getTlvs().getPathSetupType() != null) {
            pst = srp.getTlvs().getPathSetupType();
            p1Builder.setPathSetupType(pst);
        } else {
            pst = null;
        }
        pb.addAugmentation(Path1.class, p1Builder.build());
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.lsp.object.lsp.Tlvs tlvs = report.getLsp().getTlvs();
        if (tlvs != null) {
            if (tlvs.getLspIdentifiers() != null) {
                pb.setLspId(tlvs.getLspIdentifiers().getLspId());
            } else if (!isDefaultPST(pst)) {
                pb.setLspId(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev150820.LspId(lsp.getPlspId().getValue()));
            }
        }
        return pb.build();
    }

    @Override
    protected boolean onMessage(final MessageContext ctx, final Message message) {
        if (message instanceof PcerrMessage) {
            return handleErrorMessage((PcerrMessage) message);
        }
        if (!(message instanceof PcrptMessage)) {
            return true;
        }
        getSessionListenerState().updateLastReceivedRptMsg();
        final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.pcrpt.message.PcrptMessage rpt = ((PcrptMessage) message).getPcrptMessage();
        for (final Reports report : rpt.getReports()) {
            if (!manageNextReport(report, ctx)) {
                return false;
            }
        }
        return false;
    }

    private SrpIdNumber nextRequest() {
        return new SrpIdNumber(this.requestId.getAndIncrement());
    }

    private class AddFunction implements AsyncFunction<Optional<ReportedLsp>, OperationResult>  {

        private final AddLspArgs input;
        private final InstanceIdentifier<ReportedLsp> lsp;

        public AddFunction(final AddLspArgs input, final InstanceIdentifier<ReportedLsp> lsp) {
            this.input = input;
            this.lsp = lsp;
        }

        @Override
        public ListenableFuture<OperationResult> apply(final Optional<ReportedLsp> rep) {
            if (rep.isPresent()) {
                LOG.debug("Node {} already contains lsp {} at {}", this.input.getNode(), this.input.getName(), this.lsp);
                return OperationResults.createUnsent(PCEPErrors.USED_SYMBOLIC_PATH_NAME).future();
            }
            if (!getPeerCapabilities().getInstantiation()) {
                return OperationResults.createUnsent(PCEPErrors.CAPABILITY_NOT_SUPPORTED).future();
            }

            // Build the request
            final RequestsBuilder rb = new RequestsBuilder();
            final Arguments2 args = this.input.getArguments().getAugmentation(Arguments2.class);
            Preconditions.checkArgument(args != null, "Input is missing operational tag.");
            final Lsp inputLsp = args.getLsp();
            Preconditions.checkArgument(inputLsp != null, "Reported LSP does not contain LSP object.");

            rb.fieldsFrom(this.input.getArguments());

            final TlvsBuilder tlvsBuilder;
            if (inputLsp.getTlvs() != null) {
                tlvsBuilder = new TlvsBuilder(inputLsp.getTlvs());
            } else {
                tlvsBuilder = new TlvsBuilder();
            }
            tlvsBuilder.setSymbolicPathName(
                new SymbolicPathNameBuilder().setPathName(new SymbolicPathName(this.input.getName().getBytes(Charsets.UTF_8))).build());

            final SrpBuilder srpBuilder = new SrpBuilder();
            srpBuilder.setOperationId(nextRequest());
            srpBuilder.setProcessingRule(Boolean.TRUE);
            if (!isDefaultPST(args.getPathSetupType())) {
                srpBuilder.setTlvs(
                        new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.srp.TlvsBuilder()
                            .setPathSetupType(args.getPathSetupType()).build());
            }
            rb.setSrp(srpBuilder.build());

            rb.setLsp(new LspBuilder().setAdministrative(inputLsp.isAdministrative()).setDelegate(inputLsp.isDelegate()).setPlspId(
                new PlspId(0L)).setTlvs(tlvsBuilder.build()).build());

            final PcinitiateMessageBuilder ib = new PcinitiateMessageBuilder(MESSAGE_HEADER);
            ib.setRequests(Collections.singletonList(rb.build()));

            // Send the message
            return sendMessage(new PcinitiateBuilder().setPcinitiateMessage(ib.build()).build(), rb.getSrp().getOperationId(),
                this.input.getArguments().getMetadata());
        }
    }

    @Override
    public synchronized ListenableFuture<OperationResult> addLsp(final AddLspArgs input) {
        Preconditions.checkArgument(input != null && input.getName() != null && input.getNode() != null && input.getArguments() != null, MISSING_XML_TAG);
        LOG.trace("AddLspArgs {}", input);
        // Make sure there is no such LSP
        final InstanceIdentifier<ReportedLsp> lsp = lspIdentifier(input.getName());
        final ListenableFuture<Optional<ReportedLsp>> f = readOperationalData(lsp);
        if (f == null) {
            return OperationResults.createUnsent(PCEPErrors.LSP_INTERNAL_ERROR).future();
        }
        return Futures.transform(f, new AddFunction(input, lsp));
    }

    @Override
    public synchronized ListenableFuture<OperationResult> removeLsp(final RemoveLspArgs input) {
        Preconditions.checkArgument(input != null && input.getName() != null && input.getNode() != null, MISSING_XML_TAG);
        LOG.trace("RemoveLspArgs {}", input);
        // Make sure the LSP exists, we need it for PLSP-ID
        final InstanceIdentifier<ReportedLsp> lsp = lspIdentifier(input.getName());
        final ListenableFuture<Optional<ReportedLsp>> f = readOperationalData(lsp);
        if (f == null) {
            return OperationResults.createUnsent(PCEPErrors.LSP_INTERNAL_ERROR).future();
        }
        return Futures.transform(f, new AsyncFunction<Optional<ReportedLsp>, OperationResult>() {
            @Override
            public ListenableFuture<OperationResult> apply(final Optional<ReportedLsp> rep) {
                final Lsp reportedLsp = validateReportedLsp(rep, input);
                if (reportedLsp == null) {
                    return OperationResults.createUnsent(PCEPErrors.UNKNOWN_PLSP_ID).future();
                }
                final PcinitiateMessageBuilder ib = new PcinitiateMessageBuilder(MESSAGE_HEADER);
                final Requests rb = buildRequest(rep, reportedLsp);
                ib.setRequests(Collections.singletonList(rb));
                return sendMessage(new PcinitiateBuilder().setPcinitiateMessage(ib.build()).build(), rb.getSrp().getOperationId(), null);
            }
        });
    }

    private Requests buildRequest(final Optional<ReportedLsp> rep, final Lsp reportedLsp) {
        // Build the request and send it
        final RequestsBuilder rb = new RequestsBuilder();
        final SrpBuilder srpBuilder = new SrpBuilder().addAugmentation(Srp1.class, new Srp1Builder().setRemove(Boolean.TRUE).build()).setOperationId(nextRequest()).setProcessingRule(Boolean.TRUE);
        final Optional<PathSetupType> maybePST = getPST(rep);
        if (maybePST.isPresent()) {
            srpBuilder.setTlvs(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.srp.TlvsBuilder()
                .setPathSetupType(maybePST.get())
                .build());
        }

        rb.setSrp(srpBuilder.build());
        rb.setLsp(new LspBuilder().setRemove(Boolean.FALSE).setPlspId(reportedLsp.getPlspId())
                .setDelegate(reportedLsp.isDelegate()).build());
        return rb.build();
    }

    private class UpdateFunction implements AsyncFunction<Optional<ReportedLsp>, OperationResult>  {

        private final UpdateLspArgs input;

        public UpdateFunction(final UpdateLspArgs input) {
            this.input = input;
        }

        @Override
        public ListenableFuture<OperationResult> apply(final Optional<ReportedLsp> rep) {
            final Lsp reportedLsp = validateReportedLsp(rep, this.input);
            if (reportedLsp == null) {
                return OperationResults.createUnsent(PCEPErrors.UNKNOWN_PLSP_ID).future();
            }
            // create mandatory objects
            final Arguments3 args = this.input.getArguments().getAugmentation(Arguments3.class);
            final SrpBuilder srpBuilder = new SrpBuilder();
            srpBuilder.setOperationId(nextRequest());
            srpBuilder.setProcessingRule(Boolean.TRUE);
            if (args != null && args.getPathSetupType() != null) {
                if (!isDefaultPST(args.getPathSetupType())) {
                    srpBuilder.setTlvs(
                            new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.srp.TlvsBuilder()
                                .setPathSetupType(args.getPathSetupType()).build());
                }
            } else {
                final Optional<PathSetupType> maybePST = getPST(rep);
                if (maybePST.isPresent()) {
                    srpBuilder.setTlvs(
                            new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.srp.TlvsBuilder()
                                .setPathSetupType(maybePST.get()).build());
                }
            }
            final Srp srp = srpBuilder.build();
            final Lsp inputLsp = args.getLsp();
            final Lsp lsp = (inputLsp != null) ?
                new LspBuilder().setPlspId(reportedLsp.getPlspId()).setDelegate((inputLsp.isDelegate() != null) ? inputLsp.isDelegate() : false).setTlvs(inputLsp.getTlvs()).setAdministrative((inputLsp.isAdministrative() != null) ? inputLsp.isAdministrative() : false).build()
                : new LspBuilder().setPlspId(reportedLsp.getPlspId()).build();
            final Message msg = redelegate(reportedLsp.isDelegate(), srp, lsp, this.input);
            return sendMessage(msg, srp.getOperationId(), this.input.getArguments().getMetadata());
        }
    }

    private Message redelegate(final Boolean isDelegate, final Srp srp, final Lsp lsp, final UpdateLspArgs input) {
        // the D bit that was reported decides the type of PCE message sent
        Preconditions.checkNotNull(isDelegate);
        if (isDelegate) {
            // we already have delegation, send update
            final UpdatesBuilder rb = new UpdatesBuilder();
            rb.setSrp(srp);
            rb.setLsp(lsp);
            final PathBuilder pb = new PathBuilder();
            pb.fieldsFrom(input.getArguments());
            rb.setPath(pb.build());
            final PcupdMessageBuilder ub = new PcupdMessageBuilder(MESSAGE_HEADER);
            ub.setUpdates(Collections.singletonList(rb.build()));
            return new PcupdBuilder().setPcupdMessage(ub.build()).build();
        }
        // we want to revoke delegation, different type of message
        // is sent because of specification by Siva
        // this message is also sent, when input delegate bit is set to 0
        // generating an error in PCC
        final List<Requests> reqs = new ArrayList<>();
        reqs.add(new RequestsBuilder().setSrp(srp).setLsp(lsp).build());
        final PcinitiateMessageBuilder ib = new PcinitiateMessageBuilder();
        ib.setRequests(reqs);
        return new PcinitiateBuilder().setPcinitiateMessage(ib.build()).build();
    }

    @Override
    public synchronized ListenableFuture<OperationResult> updateLsp(final UpdateLspArgs input) {
        Preconditions.checkArgument(input != null && input.getName() != null && input.getNode() != null && input.getArguments() != null, MISSING_XML_TAG);
        LOG.trace("UpdateLspArgs {}", input);
        // Make sure the LSP exists
        final InstanceIdentifier<ReportedLsp> lsp = lspIdentifier(input.getName());
        final ListenableFuture<Optional<ReportedLsp>> f = readOperationalData(lsp);
        if (f == null) {
            return OperationResults.createUnsent(PCEPErrors.LSP_INTERNAL_ERROR).future();
        }
        return Futures.transform(f, new UpdateFunction(input));
    }

    @Override
    public synchronized ListenableFuture<OperationResult> ensureLspOperational(final EnsureLspOperationalInput input) {
        Preconditions.checkArgument(input != null && input.getName() != null && input.getNode() != null && input.getArguments() != null, MISSING_XML_TAG);
        final OperationalStatus op;
        final Arguments1 aa = input.getArguments().getAugmentation(Arguments1.class);
        if (aa != null) {
            op = aa.getOperational();
        } else {
            op = null;
        }

        // Make sure the LSP exists
        final InstanceIdentifier<ReportedLsp> lsp = lspIdentifier(input.getName());
        LOG.debug("Checking if LSP {} has operational state {}", lsp, op);
        final ListenableFuture<Optional<ReportedLsp>> f = readOperationalData(lsp);
        if (f == null) {
            return OperationResults.createUnsent(PCEPErrors.LSP_INTERNAL_ERROR).future();
        }
        return listenableFuture(f, input, op);
    }

    private ListenableFuture<OperationResult> listenableFuture(final ListenableFuture<Optional<ReportedLsp>> f, final EnsureLspOperationalInput input, final OperationalStatus op) {
        return Futures.transform(f, new Function<Optional<ReportedLsp>, OperationResult>() {
            @Override
            public OperationResult apply(final Optional<ReportedLsp> rep) {
                if (!rep.isPresent()) {
                    LOG.debug("Node {} does not contain LSP {}", input.getNode(), input.getName());
                    return OperationResults.UNSENT;
                }
                // check if at least one of the paths has the same status as requested
                for (final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.topology.pcep.rev131024.pcep.client.attributes.path.computation.client.reported.lsp.Path p : rep.get().getPath()) {
                    final Path1 p1 = p.getAugmentation(Path1.class);
                    if (p1 == null) {
                        LOG.warn("Node {} LSP {} does not contain data", input.getNode(), input.getName());
                        return OperationResults.UNSENT;
                    }
                    if (op.equals(p1.getLsp().getOperational())) {
                        return OperationResults.SUCCESS;
                    }
                }
                return OperationResults.UNSENT;
            }
        });
    }

    @Override
    protected Lsp validateReportedLsp(final Optional<ReportedLsp> rep,  final LspId input) {
        if (!rep.isPresent()) {
            LOG.debug("Node {} does not contain LSP {}", input.getNode(), input.getName());
            return null;
        }
        // it doesn't matter how many lsps there are in the path list, we only need data that is the same in each path
        final Path1 ra = rep.get().getPath().get(0).getAugmentation(Path1.class);
        Preconditions.checkState(ra != null, "Reported LSP reported null from data-store.");
        final Lsp reportedLsp = ra.getLsp();
        Preconditions.checkState(reportedLsp != null, "Reported LSP does not contain LSP object.");
        return reportedLsp;
    }

    private Optional<PathSetupType> getPST(final Optional<ReportedLsp> rep) {
        if (rep.isPresent()) {
            final Path1 path1 = rep.get().getPath().get(0).getAugmentation(Path1.class);
            if (path1 != null) {
                final PathSetupType pst = path1.getPathSetupType();
                if (!isDefaultPST(pst)) {
                    return Optional.of(pst);
                }
            }
        }
        return Optional.absent();
    }

    private static boolean isDefaultPST(final PathSetupType pst) {
        if (pst != null && pst.getPst() != null && pst.getPst().shortValue() != 0) {
            return false;
        }
        return true;
    }

    private static PeerCapabilities getCapabilities(final Stateful stateful) {
        final PeerCapabilities capa = new PeerCapabilities();
        capa.setStateful(true);
        if (stateful.isLspUpdateCapability() != null) {
            capa.setActive(stateful.isLspUpdateCapability());
        }
        final Stateful1 stateful1 = stateful.getAugmentation(Stateful1.class);
        if (stateful1 != null && stateful1.isInitiation() != null) {
            capa.setInstantiation(stateful1.isInitiation());
        }
        return capa;
    }

    /**
     * Recover lspData and mark any LSPs in the LSP database that were previously reported by the PCC as stale
     * @param lspData
     * @param lsps
     * @param incrementalSynchro
     */
    @Override
    protected synchronized void loadLspData(final Node node, final Map<String, ReportedLsp> lspData, final Map<PlspId, String> lsps, final boolean incrementalSynchro) {
        //load node's lsps from DS
        final PathComputationClient pcc = node.getAugmentation(Node1.class).getPathComputationClient();
        final List<ReportedLsp> reportedLsps = pcc.getReportedLsp();
        for (final ReportedLsp reportedLsp : reportedLsps) {
            final String lspName = reportedLsp.getName();
            lspData.put(lspName, reportedLsp);
            if (!reportedLsp.getPath().isEmpty()) {
                final Path1 path1 = reportedLsp.getPath().get(0).getAugmentation(Path1.class);
                if (path1 != null) {
                    final PlspId plspId = path1.getLsp().getPlspId();
                    if (!incrementalSynchro) {
                        staleLsps.add(plspId);
                    }
                    lsps.put(plspId, lspName);
                }
            }
        }
    }

    /**
     * When the PCC reports an LSP during state synchronization, if the LSP already
     * exists in the LSP database, the PCE MUST update the LSP database and
     * clear the stale marker from the LSP
     * @param plspId
     */
    private synchronized void unmarkStaleLsp(final PlspId plspId) {
        staleLsps.remove(plspId);
    }

    /**
     *  Purge any LSPs from the LSP database that are still marked as stale
     * @param ctx
     */
    private synchronized void purgeStaleLsps(final MessageContext ctx) {
        for (final PlspId plspId : staleLsps) {
            removeLsp(ctx, plspId);
        }
        staleLsps.clear();
    }
}
