/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.pcecc;

import static org.opendaylight.protocol.util.ByteBufWriteUtil.writeUnsignedInt;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.List;
import org.opendaylight.protocol.pcep.spi.*;
import org.opendaylight.protocol.util.BitArray;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.LabelNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.Label;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.Tlvs;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.label.TlvsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Object;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.ObjectHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.vendor.information.tlvs.VendorInformationTlv;


/**
 * Parser for {@link Label}
 */
public class PceccLabelObjectParser extends AbstractObjectWithTlvsParser<TlvsBuilder>  {

    public static final int CLASS = 225;

    public static final int TYPE = 1;

    protected static final int FLAGS_SIZE = 16;
    protected static final int SKIP_BYTE = 1;
    protected static final int O_FLAG_OFFSET = 15;
    protected static final int LABEL_SIZE = 20;
    private static final int RESERVED = 2;
    private static final int RESERVED_LABEL = 12;
    private static final int MIN_SIZE =  8;
    protected static final int MPLS_LABEL_OFFSET = 12;
    protected static final int OUT_LABEL_BYTE = 1;

    public PceccLabelObjectParser(final TlvRegistry tlvReg, final VendorInformationTlvRegistry viTlvReg) {
        super(tlvReg, viTlvReg);
    }
    @Override
    public Label parseObject(final ObjectHeader header, final ByteBuf bytes) throws PCEPDeserializerException {
        Preconditions.checkArgument(bytes != null && bytes.isReadable(), "Array of bytes is mandatory. Can't be null or empty.");
        if (bytes.readableBytes() < MIN_SIZE) {
            throw new PCEPDeserializerException("Wrong length of array of bytes. Passed: " + bytes.readableBytes() + "; Expected: >="
                    + MIN_SIZE + ".");
        }
        final LabelBuilder builder = new LabelBuilder();
        builder.setIgnore(header.isIgnore());
        builder.setProcessingRule(header.isProcessingRule());
        bytes.skipBytes(RESERVED + SKIP_BYTE);
        byte outLabel = bytes.readByte();
        if((byte)(outLabel & OUT_LABEL_BYTE) == OUT_LABEL_BYTE){
            builder.setOutLabel(true);
        }else
        {
            builder.setOutLabel(false);
        }
        ByteBuf lableBuff = bytes.readBytes((LABEL_SIZE + RESERVED_LABEL) / Byte.SIZE);
        builder.setLabelNum(new LabelNumber(lableBuff.readUnsignedInt() >> RESERVED_LABEL));
        final TlvsBuilder tlvsBuilder = new TlvsBuilder();
        ByteBuf tlvBytes = bytes.slice();
        parseTlvs(tlvsBuilder, tlvBytes);
        builder.setTlvs(tlvsBuilder.build());
        return builder.build();
    }


    @Override
    public void serializeObject(final Object object, final ByteBuf buffer) {
        Preconditions.checkArgument(object instanceof Label, "Wrong instance of PCEPObject. Passed %s . Needed LabelObject.", object.getClass());
        final Label lbl = (Label) object;
        final ByteBuf body = Unpooled.buffer();
        body.writeZero(RESERVED);
        final BitArray flags = new BitArray(FLAGS_SIZE);
        flags.set(O_FLAG_OFFSET, lbl.isOutLabel());
        flags.toByteBuf(body);
//        body.writeZero(RESERVED_LABEL/Byte.SIZE);
        final LabelNumber LabelNum = lbl.getLabelNum();
        Preconditions.checkArgument(LabelNum != null, "Label Number is mandatory.");
        writeUnsignedInt(LabelNum.getValue() << MPLS_LABEL_OFFSET, body);
        serializeTlvs(lbl.getTlvs(), body);
        ObjectUtil.formatSubobject(TYPE, CLASS, object.isProcessingRule(), object.isIgnore(), body, buffer);
    }



    public void serializeTlvs(final Tlvs tlvs, final ByteBuf body) {
        if (tlvs == null) {
            return;
        }

        if (tlvs.getAddress() != null) {
            serializeTlv(tlvs.getAddress(), body);
        }

        serializeVendorInformationTlvs(tlvs.getVendorInformationTlv(), body);
    }

    @Override
    public void addTlv(final TlvsBuilder builder, final Tlv tlv) {
        if (tlv instanceof Address) {
            builder.setAddress((Address) tlv);
        }
    }

    @Override
    protected final void addVendorInformationTlvs(final TlvsBuilder builder, final List<VendorInformationTlv> tlvs) {
        if (!tlvs.isEmpty()) {
            builder.setVendorInformationTlv(tlvs);
        }
    }
}
