/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.flowspec;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;
import org.opendaylight.protocol.pcep.spi.AbstractMessageParser;
import org.opendaylight.protocol.pcep.spi.MessageUtil;
import org.opendaylight.protocol.pcep.spi.ObjectRegistry;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.PCEPErrors;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Flowspec;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.FlowspecBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.action.object.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flow.object.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.message.FlowspecMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.message.flowspec.message.PceFlowspec;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.message.flowspec.message.PceFlowspecBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.PceFlowspecAddUpdateCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.PceFlowspecAddUpdateCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.PceFlowspecDeleteCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.PceFlowspecDeleteCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.pce.flowspec.add.update._case.PceFlowspecAddUpdate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.pce.flowspec.add.update._case.PceFlowspecAddUpdateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.pce.flowspec.add.update._case.pce.flowspec.add.update.ActionList;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.pce.flowspec.add.update._case.pce.flowspec.add.update.ActionListBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.pce.flowspec.delete._case.PceFlowspecDelete;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.pce.flowspec.pce.flowspec.type.pce.flowspec.delete._case.PceFlowspecDeleteBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.ietf.stateful.rev131222.srp.object.Srp;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Message;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Object;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.rp.object.Rp;

/**
 * Parser for {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.FlowspecMessage}
 */
public class FlowspecMessageParser extends AbstractMessageParser {

    public static final int TYPE = 227;

    public FlowspecMessageParser(final ObjectRegistry registry) {
        super(registry);
    }

    @Override
    public void serializeMessage(final Message message, final ByteBuf out) {
        Preconditions.checkArgument(message instanceof Flowspec,
                "Wrong instance of Message. Passed instance of %s. Need FlowspecMessageParser.", message.getClass());

        final Flowspec msg = (Flowspec) message;
        final ByteBuf buffer = Unpooled.buffer();
        final List<PceFlowspec> flowspeclst = msg.getFlowspecMessage().getPceFlowspec();

        for (final PceFlowspec flowspec : flowspeclst) {
            serializeFlowspec(flowspec, buffer);
        }
        MessageUtil.formatMessage(TYPE, buffer, out);
    }

    protected void serializeFlowspec(final PceFlowspec flowspec, final ByteBuf buffer) {

        //If label download
        if (flowspec.getPceFlowspecType() instanceof PceFlowspecAddUpdateCase) {
            final PceFlowspecAddUpdateCase flowspecAddUpdateCase = (PceFlowspecAddUpdateCase) flowspec.getPceFlowspecType();
            final PceFlowspecAddUpdate flowspecAddUpdate = flowspecAddUpdateCase.getPceFlowspecAddUpdate();

            serializeObject(flowspecAddUpdate.getSrp(), buffer);
            serializeObject(flowspecAddUpdate.getFlow(), buffer);

            if (flowspecAddUpdate.getActionList() != null) {
                for (final ActionList action : flowspecAddUpdate.getActionList()) {
                    serializeObject(action.getAction(), buffer);
                }
            }
        }
        //if label map
        else if (flowspec.getPceFlowspecType() instanceof PceFlowspecDeleteCase) {
            final PceFlowspecDeleteCase flowspecDeleteCase = (PceFlowspecDeleteCase) flowspec.getPceFlowspecType();
            final PceFlowspecDelete flowspecDelete = flowspecDeleteCase.getPceFlowspecDelete();
            serializeObject(flowspecDelete.getSrp(), buffer);
            serializeObject(flowspecDelete.getFlow(), buffer);
        }

    }

    @Override
    protected Message validate(final List<Object> objects, final List<Message> errors) throws PCEPDeserializerException {
        Preconditions.checkArgument(objects != null, "Passed list can't be null.");
        if (objects.isEmpty()) {
            throw new PCEPDeserializerException("PcLabelUpt message cannot be empty.");
        }

        final List<PceFlowspec> flowspeclst = Lists.newArrayList();

        if (!objects.isEmpty()) {
            final PceFlowspec flowspec = getValidUpdates(objects, errors);
            if (flowspec != null) {
                flowspeclst.add(flowspec);
            }else if(errors.isEmpty()== false){
                return errors.get(0);
            }
        }
        if (!objects.isEmpty()) {
            throw new PCEPDeserializerException("Unprocessed Objects: " + objects);
        }

        return new FlowspecBuilder().setFlowspecMessage(new FlowspecMessageBuilder()
                .setPceFlowspec(flowspeclst).build()).build();
    }

    protected PceFlowspec getValidUpdates(final List<Object> objects, final List<Message> errors) {

        boolean isValid = true;

        final PceFlowspecAddUpdateBuilder flowspecAddUpdateBuilder = new PceFlowspecAddUpdateBuilder();
        final PceFlowspecDeleteBuilder flowspecDeleteBuilder = new PceFlowspecDeleteBuilder();

        final PceFlowspecBuilder flowspecBuilder = new PceFlowspecBuilder();

        if (objects.get(0) instanceof Srp) {

            if ((objects.get(1) instanceof Flow) && (objects.get(2) instanceof Action)) {
                flowspecAddUpdateBuilder.setSrp((Srp) objects.get(0));
                objects.remove(0);

                flowspecAddUpdateBuilder.setFlow((Flow) objects.get(0));
                objects.remove(0);

                if (objects.get(0) instanceof Action){
                    final List<ActionList> act = parseAction(objects, errors);
                    if (!act.isEmpty()) {
                        flowspecAddUpdateBuilder.setActionList(act);
                    }
                }

            } else if (objects.size() == 2) {

                flowspecDeleteBuilder.setSrp((Srp) objects.get(0));
                objects.remove(0);

                flowspecDeleteBuilder.setFlow((Flow) objects.get(0));
                objects.remove(0);

            } else {
                //Log error or send error
                isValid = false;
            }
        } else {
            errors.add(createErrorMsg(PCEPErrors.SRP_MISSING, Optional.<Rp>absent())); //to check
            isValid = false;
        }
        if(isValid) {
            if (flowspecAddUpdateBuilder.getSrp() != null) {
                flowspecBuilder.setPceFlowspecType(new PceFlowspecAddUpdateCaseBuilder()
                        .setPceFlowspecAddUpdate(flowspecAddUpdateBuilder.build()).build()).build();
            } else {
                flowspecBuilder.setPceFlowspecType(new PceFlowspecDeleteCaseBuilder()
                        .setPceFlowspecDelete(flowspecDeleteBuilder.build()).build()).build();
            }

            return flowspecBuilder.build();
        }

        return null;
    }

    protected List<ActionList> parseAction(final List<Object> objects, final List<Message> errors) {

        final List<ActionList> actionList = new ArrayList<>();

        int actionProcessed =  0;
        final ActionListBuilder actionBuilder = new ActionListBuilder();

        while (!objects.isEmpty()) {
            if (objects instanceof Action) {
                actionList.add(new ActionListBuilder().setAction((Action)objects).build());
                objects.remove(0);
            }
        }

        return actionList;
    }

}
