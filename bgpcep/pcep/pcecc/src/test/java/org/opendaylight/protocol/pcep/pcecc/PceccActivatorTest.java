/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.pcecc;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.protocol.pcep.spi.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.spi.PCEPExtensionProviderContext;
import org.opendaylight.protocol.pcep.spi.pojo.ServiceLoaderPCEPExtensionProviderContext;

/* Test :- Check the registry of all object done in PceccActivator */
public class PceccActivatorTest {
    /*No of object register in PceccActivator*/
    private static final int TOTAL_OBJECT_REGISTERED = 19;
    @Test
    public void testStartImplPCEPExtensionProviderContext() throws PCEPDeserializerException {
        final PceccActivator activator = new PceccActivator();
        final PCEPExtensionProviderContext ctx = ServiceLoaderPCEPExtensionProviderContext.create();
        final List<AutoCloseable> registrations = activator.startImpl(ctx);
        Assert.assertEquals(TOTAL_OBJECT_REGISTERED, registrations.size());
        activator.close();
    }
}
