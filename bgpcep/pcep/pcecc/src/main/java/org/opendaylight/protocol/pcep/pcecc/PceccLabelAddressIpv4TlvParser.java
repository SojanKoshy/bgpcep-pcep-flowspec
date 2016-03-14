/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.pcecc;

import static org.opendaylight.protocol.util.ByteBufWriteUtil.writeIpv4Address;
import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.TlvParser;
import org.opendaylight.protocol.pcep.spi.TlvSerializer;
import org.opendaylight.protocol.pcep.spi.TlvUtil;
import org.opendaylight.protocol.util.Ipv4Util;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.Ipv4Case;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.ipv4._case.Ipv4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.address.tlv.address.address.family.ipv4._case.Ipv4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Tlv;



/**
 * Parser for {@link Address}
 */
public class PceccLabelAddressIpv4TlvParser implements TlvParser, TlvSerializer {

    public static final int TYPE = 65289;

    private static final int V4_LENGTH = 4;

    @Override
    public Address parseTlv(final ByteBuf buffer) throws PCEPDeserializerException {
        if (buffer == null) {
            return null;
        }

        Preconditions.checkArgument(buffer.readableBytes() == V4_LENGTH, "Length %s does not match Label Ipv4 tlv length.", buffer.readableBytes());
        final Ipv4Builder builder = new Ipv4Builder();
        builder.setIpv4Address(Ipv4Util.addressForByteBuf(buffer));
        //final Address add = new Ipv4CaseBuilder().setIpv4(builder.build()).build();
        //return new AddressBuilder().setAddress(add.getAddress()).build();
        return null;
    }

    @Override
    public void serializeTlv(final Tlv tlv, final ByteBuf buffer) {
        Preconditions.checkArgument(tlv instanceof Address, "Address is mandatory.");
        final Address address = (Address) tlv;
        final ByteBuf body = Unpooled.buffer();
        final Ipv4 ipv4 = ((Ipv4Case) address.getAddressFamily()).getIpv4();
        writeIpv4Address(ipv4.getIpv4Address(), body);
        TlvUtil.formatTlv(TYPE, body, buffer);
    }
}
