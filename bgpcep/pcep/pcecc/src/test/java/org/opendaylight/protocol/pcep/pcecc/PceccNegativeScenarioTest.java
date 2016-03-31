/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.pcep.pcecc;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.opendaylight.protocol.pcep.spi.ObjectHeaderImpl;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.TlvRegistry;
import org.opendaylight.protocol.pcep.spi.VendorInformationTlvRegistry;
import org.opendaylight.protocol.pcep.spi.pojo.SimplePCEPExtensionProviderContext;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.message.rev131007.PcerrBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.LabelNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.pcecc.rev160225.label.object.LabelBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.Message;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.OpenBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.pcep.error.object.ErrorObjectBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.pcerr.message.PcerrMessageBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.pcerr.message.pcerr.message.ErrorsBuilder;


public class PceccNegativeScenarioTest {
    private static final byte[] PceccFecAdjacencyObjectBytes = {
        (byte) 0xe2, 0x30, 0x00, 0x1c,
        (byte) 0xfe,(byte) 0x90, 0x00, 0x00,
    };

    private static final byte[] PceccFecObjectBytes = {
        (byte) 0xe2, 0x10, 0x00, 0x08,
        (byte) 0xff,(byte) 0x90,
    };

    private static final byte[] PceccLabelObjectBytes = {
        (byte) 0xe1, 0x10, 0x00, 0x0c,
        0x00, 0x00, 0x00, 0x01,
    };

    private static final byte[] PceccLabelObjectwithAddressTlvBytes = {
        (byte) 0xe1, 0x10, 0x00, (byte) 0x14,
        0x00, 0x00, 0x00, 0x00,
        (byte) 0x01, 0x38, (byte) 0x90, 0x00,
    };

    private static final byte[] openObjectBytes = {
        0x01, 0x10, 0x00, 0x10,
        0x20, 0x1e, 0x78, 0x01,
        /* pcecc-capability-tlv */
        (byte) 0xff, 0x07, 0x00, 0x02,
        0x00, 0x00, 0x00, 0x03
    };

    private static final byte[] PceccLabelUpdateMessageParserError = {
        (byte) 0x20, (byte)0xe2,0x00, 0x20,
        (byte) 0xe1, 0x10, 0x00, (byte) 0x14,
        0x00, 0x00, 0x00, 0x00,
        (byte) 0x01, 0x38, (byte) 0x90, 0x00,
        (byte)  0xff, 0x09, 0x00, 0x04,
        0x01, 0x01, 0x01, 0x01,
        (byte) 0xe2, 0x10, 0x00, 0x08,
        (byte) 0xff,(byte) 0x90, 0x00, 0x01
    };

    private static final byte[] PceccLabelTlvBytes = {
        (byte) 0x20, (byte)0xe2,0x00, 0x2c,
    };

    private TlvRegistry tlvRegistry;
    private VendorInformationTlvRegistry viTlvRegistry;

    private SimplePCEPExtensionProviderContext ctx;
    private PceccActivator act;

    @Before
    public void setUp() {
        this.ctx = new SimplePCEPExtensionProviderContext();
        this.act = new PceccActivator();
        this.act.start(this.ctx);
        this.tlvRegistry = this.ctx.getTlvHandlerRegistry();
        this.viTlvRegistry = this.ctx.getVendorInformationTlvRegistry();
    }

    @Test
    public void testPceccFecIpv4AdjacencyObjectParserHitLengthCheck() throws PCEPDeserializerException {
        try {
            final PceccFecIpv4AdjacencyObjectParser parser =
                    new PceccFecIpv4AdjacencyObjectParser();
            final ByteBuf result = Unpooled.wrappedBuffer(PceccFecAdjacencyObjectBytes);
            parser.parseObject(new ObjectHeaderImpl(false, false), result.slice(4, result.readableBytes() - 4));
            fail();
        } catch (final PCEPDeserializerException e) {
            assertTrue(e.getMessage().contains("Wrong length of array of bytes. Passed: 4; Expected: >=8."));
        }
    }

    @Test
    public void testPceccFecObjectParserHitLengthCheck() throws PCEPDeserializerException {
        try {
            final PceccFecObjectParser parser =
                    new PceccFecObjectParser();
            final ByteBuf result = Unpooled.wrappedBuffer(PceccFecObjectBytes);
            parser.parseObject(new ObjectHeaderImpl(false, false), result.slice(4, result.readableBytes() - 4));
            fail();
        } catch (final PCEPDeserializerException e) {
            assertTrue(e.getMessage().contains("Wrong length of array of bytes. Passed: 2; Expected: >=4."));
        }
    }

    @Test
    public void testPceccFecIpv4ObjectParserHitLengthCheck() throws PCEPDeserializerException {
        try {
            final PceccFecIpv4ObjectParser parser =
                    new PceccFecIpv4ObjectParser();
            final ByteBuf result = Unpooled.wrappedBuffer(PceccFecObjectBytes);
            parser.parseObject(new ObjectHeaderImpl(false, false), result.slice(4, result.readableBytes() - 4));
            fail();
        } catch (final PCEPDeserializerException e) {
            assertTrue(e.getMessage().contains("Wrong length of array of bytes. Passed: 2; Expected: >=4."));
        }
    }


    @Test
    public void testPceccLabelObjectParserHitLengthCheck() throws PCEPDeserializerException {
        try {
            final PceccLabelObjectParser parser =
                    new PceccLabelObjectParser(this.tlvRegistry, this.viTlvRegistry);
            final ByteBuf result = Unpooled.wrappedBuffer(PceccLabelObjectBytes);
            parser.parseObject(new ObjectHeaderImpl(false, false), result.slice(4, result.readableBytes() - 4));
            fail();
        } catch (final PCEPDeserializerException e) {
            assertTrue(e.getMessage().contains("Wrong length of array of bytes. Passed: 4; Expected: >=8."));
        }
    }

    @Test
    public void testPceccLabelObjectParserHitLabelCheck() throws Exception {
        try {
            final PceccLabelObjectParser parser =
                    new PceccLabelObjectParser(this.tlvRegistry, this.viTlvRegistry);
            final ByteBuf buffer = Unpooled.buffer();
            final LabelBuilder builder = new LabelBuilder();
            parser.serializeObject(builder.build(), buffer);
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("Label Number is mandatory."));
        }
    }

    @Test
    public void testPceccLabelObjectParserHitNullTlvCheck() {
        final PceccLabelObjectParser parser =
                new PceccLabelObjectParser(this.tlvRegistry, this.viTlvRegistry);
        final ByteBuf buffer = Unpooled.buffer();
        final LabelBuilder builder = new LabelBuilder();
        builder.setLabelNum(new LabelNumber(5001L));
        parser.serializeObject(builder.build(), buffer);
    }

    @Test
    public void testPceccOpenObjectParserHitSessionIdCheck() throws Exception {
        try {
            final PcepOpenObjectWithPceccTlvParser parser =
                    new PcepOpenObjectWithPceccTlvParser(this.tlvRegistry, this.viTlvRegistry);
            final ByteBuf buffer = Unpooled.buffer();
            final OpenBuilder builder = new OpenBuilder();
            parser.serializeObject(builder.build(), buffer);
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("SessionId is mandatory."));
        }
    }

    @Test
    public void testPceccOpenObjectParserHitNullTlvCheck() {
        final PcepOpenObjectWithPceccTlvParser parser =
                new PcepOpenObjectWithPceccTlvParser(this.tlvRegistry, this.viTlvRegistry);
        final ByteBuf buffer = Unpooled.buffer();
        final OpenBuilder builder = new OpenBuilder();
        builder.setSessionId((short) 1);
        parser.serializeObject(builder.build(), buffer);
    }

    @Test
    public void testPcepOpenObjectWithLengthCheck() throws PCEPDeserializerException {
        try {
            final PcepOpenObjectWithPceccTlvParser parser =
                    new PcepOpenObjectWithPceccTlvParser(this.tlvRegistry, this.viTlvRegistry);
            final ByteBuf result = Unpooled.wrappedBuffer(openObjectBytes);
            parser.parseObject(new ObjectHeaderImpl(false, false), result.slice(4, result.readableBytes() - 4));
            fail();
        } catch (final PCEPDeserializerException e) {
            assertTrue(e.getMessage().contains("Wrong length of array of bytes. Passed: 2; Expected: >= 4."));
        }
    }

    @Test
    public void testPceccLabelUpdateMessageParser() throws IOException, PCEPDeserializerException {
        try {
            final PceccLabelUpdateMessageParser parser = new PceccLabelUpdateMessageParser(this.ctx.getObjectHandlerRegistry());

            ByteBuf result = Unpooled.wrappedBuffer(PceccLabelTlvBytes);
            parser.parseMessage(result.slice(4,
                    result.readableBytes() - 4), Collections.<Message> emptyList());

            fail();
        } catch (final PCEPDeserializerException e) {
            assertTrue(e.getMessage().contains("PcLabelUpt message cannot be empty."));
        }
    }

    /**
     * Srp Object missing for a path in an LSP Update Request where TE-LSP setup is requested.
     * SRP_MISSING(6, 10)
     */
    @Test
    public void testPceccLabelUpdateMessageParserError() throws IOException, PCEPDeserializerException {
        try(PceccActivator a = new PceccActivator()) {
            a.start(ctx);
            final PceccLabelUpdateMessageParser parser = new PceccLabelUpdateMessageParser(this.ctx.getObjectHandlerRegistry());

            ByteBuf result = Unpooled.wrappedBuffer(PceccLabelUpdateMessageParserError);
            final List<Message> errors = Lists.<Message>newArrayList();

            final PcerrMessageBuilder errMsgBuilder = new PcerrMessageBuilder();
            errMsgBuilder.setErrors(Lists.newArrayList(new ErrorsBuilder().setErrorObject(
                    new ErrorObjectBuilder().setType((short) 6).setValue((short) 10).build()).build()));
            final PcerrBuilder builder = new PcerrBuilder();
            builder.setPcerrMessage(errMsgBuilder.build());
            parser.parseMessage(result.slice(4,
                    result.readableBytes() - 4), errors);
            assertFalse(errors.isEmpty());
            assertEquals(new PcerrBuilder().setPcerrMessage(errMsgBuilder.build()).build(), errors.get(0));
        }
    }

}
