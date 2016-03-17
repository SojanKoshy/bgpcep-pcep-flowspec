/*
 * Copyright (c) 2016 Huawei Technologies Co. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.pcecc;


import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.config.api.jmx.CommitStatus;
import org.opendaylight.controller.config.manager.impl.AbstractConfigTest;
import org.opendaylight.controller.config.manager.impl.factoriesresolver.HardcodedModuleFactoriesResolver;
import org.opendaylight.controller.config.util.ConfigTransactionJMXClient;
import org.opendaylight.controller.config.yang.pcep.pcecc.cfg.PceccCapabilityModuleFactory;
import org.opendaylight.controller.config.yang.pcep.pcecc.cfg.PceccCapabilityModuleMXBean;
import org.opendaylight.controller.config.yang.pcep.spi.SimplePCEPExtensionProviderContextModuleFactory;
import org.opendaylight.controller.config.yang.pcep.spi.SimplePCEPExtensionProviderContextModuleMXBean;
import org.opendaylight.controller.config.yang.pcep.stateful07.cfg.PCEPStatefulCapabilityModuleMXBean;

import javax.management.InstanceAlreadyExistsException;
import javax.management.ObjectName;
import java.util.List;


public class PceccCapabilityModuleTest extends AbstractConfigTest {

    private static final String FACTORY_NAME = PceccCapabilityModuleFactory.NAME;
    private static final String INSTANCE_NAME = "pcepcc-impl";

    @Before
    public void setUp() throws Exception {
        super.initConfigTransactionManagerImpl(new HardcodedModuleFactoriesResolver(mockedContext, new PceccCapabilityModuleFactory()));
    }


    @Test
    public void testCreateBean() throws Exception {
        final CommitStatus status = createInstance();
        assertBeanCount(1, FACTORY_NAME);
        assertStatus(status, 1, 0, 0);
    }

    @Test
    public void testReusingOldInstance() throws Exception {
        createInstance();
        final ConfigTransactionJMXClient transaction = this.configRegistryClient.createTransaction();
        assertBeanCount(1, FACTORY_NAME);
        final CommitStatus status = transaction.commit();
        assertBeanCount(1, FACTORY_NAME);
        assertStatus(status, 0, 0, 1);
    }

    @Test
    public void testReconfigure() throws Exception {
        createInstance();
        final ConfigTransactionJMXClient transaction = this.configRegistryClient.createTransaction();
        assertBeanCount(1, FACTORY_NAME);
        transaction.newMXBeanProxy(transaction.lookupConfigBean(FACTORY_NAME, INSTANCE_NAME),
                PceccCapabilityModuleMXBean.class);
        final CommitStatus status = transaction.commit();
        assertBeanCount(1, FACTORY_NAME);
        assertStatus(status, 0, 0, 1);
    }

    private CommitStatus createInstance(final Boolean pceccCapable, final Boolean stateful, final Boolean active, final Boolean instant,
                                        final Boolean triggeredInitialSync, final Boolean triggeredResync, final Boolean deltaLspSyncCapability,
                                        final Boolean includeDbVersion) throws Exception {
        final ConfigTransactionJMXClient transaction = this.configRegistryClient.createTransaction();
        createInstance(transaction, pceccCapable, stateful, active, instant, triggeredInitialSync, triggeredResync, deltaLspSyncCapability, includeDbVersion);
        return transaction.commit();
    }

    private CommitStatus createInstance() throws Exception {
        final ConfigTransactionJMXClient transaction = this.configRegistryClient.createTransaction();
        createPCEPCCCapabilityInstance(transaction);
        return transaction.commit();
    }


    public static ObjectName createPCEPCCCapabilityInstance(final ConfigTransactionJMXClient transaction) throws Exception {
        return createInstance(transaction, true, true, true, true, true, true, true, true);
    }

    private static ObjectName createInstance(final ConfigTransactionJMXClient transaction, final Boolean pceccCapable, final Boolean stateful, final Boolean active, final Boolean instant,
                                             final Boolean triggeredInitialSync, final Boolean triggeredResync, final Boolean deltaLspSyncCapability, final Boolean includeDbVersion) throws InstanceAlreadyExistsException {
        final ObjectName nameCreated = transaction.createModule(FACTORY_NAME, INSTANCE_NAME);
        final PceccCapabilityModuleMXBean mxBean = transaction.newMXBeanProxy(nameCreated,
                PceccCapabilityModuleMXBean.class);
        mxBean.setPceccCapable(pceccCapable);
        mxBean.setActive(active);
        mxBean.setInitiated(instant);
        mxBean.setStateful(stateful);
        mxBean.setTriggeredInitialSync(triggeredInitialSync);
        mxBean.setTriggeredResync(triggeredResync);
        mxBean.setDeltaLspSyncCapability(deltaLspSyncCapability);
        mxBean.setIncludeDbVersion(includeDbVersion);
        return nameCreated;
    }

}
