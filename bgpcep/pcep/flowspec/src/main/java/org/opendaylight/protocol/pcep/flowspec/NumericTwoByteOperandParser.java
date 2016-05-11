/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.flowspec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.NumericOperand;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.NumericTwoByteValue;


/**
 * Parser class for NumericTwoByteValues.
 */
public final class NumericTwoByteOperandParser extends AbstractNumericByteOperandParser<NumericTwoByteValue, Integer> {

    public static final NumericTwoByteOperandParser INSTANCE;

    static {
        INSTANCE = new NumericTwoByteOperandParser();
    }

    private NumericTwoByteOperandParser() {

    }

    /**
     * Serializes Flowspec component type that has maximum of 2B sized value field and numeric operand.
     *
     * @param list of operands to be serialized
     * @param nlriByteBuf where the operands will be serialized
     */
    @Override
    public <T extends NumericTwoByteValue> void serialize(final List<T> list, final ByteBuf nlriByteBuf) {
        for (final T operand : list) {
            final ByteBuf protoBuf = Unpooled.buffer();
            Util.writeShortest(operand.getValue(), protoBuf);
            super.serialize(operand.getOp(), protoBuf.readableBytes(), nlriByteBuf);
            nlriByteBuf.writeBytes(protoBuf);
        }
    }

    @Override
    protected <T extends NumericTwoByteValue> Integer getValue(final T item) {
        return item.getValue();
    }

    @Override
    <T extends NumericTwoByteValue> NumericOperand getOp(final T item) {
        return item.getOp();
    }
}
