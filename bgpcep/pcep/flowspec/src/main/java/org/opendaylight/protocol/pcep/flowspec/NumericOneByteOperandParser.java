/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.flowspec;


import io.netty.buffer.ByteBuf;
import java.util.List;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.NumericOneByteValue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.flowspec.rev160422.NumericOperand;


/**
 * Parser class for NumericOneByteValues.
 */
public final class NumericOneByteOperandParser extends AbstractNumericByteOperandParser<NumericOneByteValue, Short> {

    public static final NumericOneByteOperandParser INSTANCE;

    static {
        INSTANCE = new NumericOneByteOperandParser();
    }

    private NumericOneByteOperandParser() {

    }

    /**
     * Serializes Flowspec component type that has maximum of 1B sized value field and numeric operand.
     *
     * @param list        of operands to be serialized
     * @param nlriByteBuf where the operands will be serialized
     */
    @Override
    public <T extends NumericOneByteValue> void serialize(final List<T> list, final ByteBuf nlriByteBuf) {
        for (final T operand : list) {
            super.serialize(operand.getOp(), 1, nlriByteBuf);
            Util.writeShortest(operand.getValue(), nlriByteBuf);
        }
    }

    @Override
    protected <T extends NumericOneByteValue> Short getValue(final T item) {
        return item.getValue();
    }

    @Override
    <T extends NumericOneByteValue> NumericOperand getOp(final T item) {
        return item.getOp();
    }
}
