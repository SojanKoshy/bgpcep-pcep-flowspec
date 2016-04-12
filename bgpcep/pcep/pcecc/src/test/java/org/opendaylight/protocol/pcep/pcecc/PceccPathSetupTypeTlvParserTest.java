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
import org.junit.Test;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.util.ByteArray;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.path.setup.type.tlv.PathSetupType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.path.setup.type.tlv.PathSetupTypeBuilder;

public class PceccPathSetupTypeTlvParserTest {

 /*
      0                   1                   2                   3
  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
  | Object-Class  |   OT  |Res|P|I|   Object Length (bytes)       |
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
  |                                                               |
  //                        (Object body)                        //
  |                                                               |
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

  PCEP Common Object Header
  PCEP Open Object-Class is 1
  TYPE = 1

  0                   1                   2                   3
  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
  |               Type=[TBD]      |            Length=4           |
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
  |                             Flags                         |G|L|
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

  PCECC Capability TLV
  Type of the TLV is 65287

 */
    private static final byte[] PCECC_TLV_BYTES = {0x0, 0x1C, 0x0, 0x4, 0x0, 0x0, 0x0, 0x0};

    private static final byte[] PCECC_TLV_BYTES_UNSUPPORTED = {0x0, 0x1C, 0x0, 0x4, 0x0, 0x0, 0x0, 0x1};

    /*
    * testPathSetupTypeTlvParser
    * Description :- Test PathSetupType with Pcecc Tlv in PceccPathSetupTypeTlvParser.
    */
    @Test
    public void testPathSetupTypeTlvParser() throws PCEPDeserializerException {
        final PceccPathSetupTypeTlvParser parser = new PceccPathSetupTypeTlvParser();
        final PathSetupType pstTlv = new PathSetupTypeBuilder().setPst((short) 0).build();
        assertEquals(pstTlv, parser.parseTlv(Unpooled.wrappedBuffer(ByteArray.cutBytes(PCECC_TLV_BYTES, 4))));
        final ByteBuf buff = Unpooled.buffer();
        parser.serializeTlv(pstTlv, buff);
        assertArrayEquals(PCECC_TLV_BYTES, ByteArray.getAllBytes(buff));
    }

    /*
    * testUnsupportedPSTParser
    * Description :- Test PceccPathSetupTypeTlvParser with Unsupported values.
    */
    @Test(expected = PCEPDeserializerException.class)
    public void testUnsupportedPSTParser() throws PCEPDeserializerException {
        final PceccPathSetupTypeTlvParser parser = new PceccPathSetupTypeTlvParser();
        parser.parseTlv(Unpooled.wrappedBuffer(ByteArray.cutBytes(PCECC_TLV_BYTES_UNSUPPORTED, 4)));
    }

    /*
    * testUnsupportedPSTSerializer
    * Description :- Test PceccPathSetupTypeTlvParser with illegal Arguments.
    */
    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedPSTSerializer() {
        final PceccPathSetupTypeTlvParser parser = new PceccPathSetupTypeTlvParser();
        final PathSetupType pstTlv = new PathSetupTypeBuilder().setPst((short) 1).build();
        final ByteBuf buff = Unpooled.buffer();
        parser.serializeTlv(pstTlv, buff);
    }
}
