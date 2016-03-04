/*
 * Copyright (c) 2016 Huawei Technologies Co. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.pcecc;

import org.opendaylight.protocol.pcep.impl.tlv.PathSetupTypeTlvParser;

public class PCEccPathSetupTypeTlvParser extends PathSetupTypeTlvParser {

    private static final short BASIC_PCECC_PST = 2;
    private static final short PCECC_SR_TE_PST = 3;
    public PCEccPathSetupTypeTlvParser() {
        super();
        PSTS.add(BASIC_PCECC_PST);
        PSTS.add(PCECC_SR_TE_PST);
    }

}
