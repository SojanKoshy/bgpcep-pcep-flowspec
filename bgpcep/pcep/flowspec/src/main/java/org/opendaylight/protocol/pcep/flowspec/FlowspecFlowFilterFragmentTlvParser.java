/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.flowspec;

import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.TlvParser;
import org.opendaylight.protocol.pcep.spi.TlvSerializer;
import org.opendaylight.protocol.pcep.spi.TlvUtil;
import org.opendaylight.protocol.util.BitArray;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.BitmaskOperand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.Fragment;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.FragmentCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.FragmentCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.fragment._case.Fragments;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.fragment._case.FragmentsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;



/**
 * Parser for {@link org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.flowspec.filter.flowspec.flowspec.type.FragmentCase}
 */
public class FlowspecFlowFilterFragmentTlvParser implements TlvParser, TlvSerializer {

    public static final int TYPE = 65302;

    private static final int V4_LENGTH = 4;

    protected static final int LAST_FRAGMENT = 4;
    protected static final int FIRST_FRAGMENT = 5;
    protected static final int IS_A_FRAGMENT = 6;
    protected static final int DONT_FRAGMENT = 7;

    @Override
    public FragmentCase parseTlv(final ByteBuf buffer) throws PCEPDeserializerException {
        if (buffer == null) {
            return null;
        }

        Preconditions.checkArgument(buffer.readableBytes() == V4_LENGTH, "Length %s does not match Fragment tlv length.", buffer.readableBytes());
        final short length = buffer.readUnsignedByte();
        final int prefixLenInByte = length / Byte.SIZE + ((length % Byte.SIZE == 0) ? 0 : 1);
        final FragmentCaseBuilder builder = new FragmentCaseBuilder();

        builder.setFragments(parseFragment(buffer)).build();
        return builder.build();
    }

    protected final List<Fragments> parseFragment(final ByteBuf buffer) {
        final List<Fragments> fragments = new ArrayList<>();
        boolean end = false;
        // we can do this as all fields will be rewritten in the cycle
        final FragmentsBuilder builder = new FragmentsBuilder();
        while (!end) {
            final byte b = buffer.readByte();
            final BitmaskOperand op = BitmaskOperandParser.INSTANCE.parse(b);
            builder.setOp(op);
            builder.setValue(parseFragment(buffer.readByte()));
            end = op.isEndOfList();
            fragments.add(builder.build());
        }
        return fragments;
    }

    @Override
    public void serializeTlv(final Tlv tlv, final ByteBuf buffer) {
        Preconditions.checkArgument(tlv instanceof FragmentCase, "Fragment is mandatory.");
        final FragmentCase fragment = (FragmentCase) tlv;
        final ByteBuf body = Unpooled.buffer();
        serializeFragments(fragment.getFragments(), body);
        TlvUtil.formatTlv(TYPE, body, buffer);
    }

    private final void serializeFragments(final List<Fragments> fragments, final ByteBuf byteBuf) {
        for (final Fragments fragment : fragments) {
            BitmaskOperandParser.INSTANCE.serialize(fragment.getOp(), 1, byteBuf);
            byteBuf.writeByte(serializeFragment(fragment.getValue()));
        }
    }

    private Fragment parseFragment(final byte fragment) {
        final BitArray bs = BitArray.valueOf(fragment);
        return new Fragment(bs.get(DONT_FRAGMENT), bs.get(FIRST_FRAGMENT), bs.get(IS_A_FRAGMENT), bs.get(LAST_FRAGMENT));
    }

    private byte serializeFragment(final Fragment fragment) {
        final BitArray bs = new BitArray(Byte.SIZE);
        bs.set(DONT_FRAGMENT, fragment.isDoNot());
        bs.set(FIRST_FRAGMENT, fragment.isFirst());
        bs.set(IS_A_FRAGMENT, fragment.isIsA());
        bs.set(LAST_FRAGMENT, fragment.isLast());
        return bs.toByte();
    }
}
