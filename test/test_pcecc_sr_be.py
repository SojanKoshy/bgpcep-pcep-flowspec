""""Test case for PCECC SR BE demo"""
# Copyright (c) 2016 Huawei Technologies Co., Ltd. and others.  All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v1.0 which accompanies this distribution,
# and is available at http://www.eclipse.org/legal/epl-v10.html

__author__ = "Sojan Koshy"
__copyright__ = "Copyright(c) 2016 Huawei Technologies Co., Ltd."
__license__ = "Eclipse Public License v1.0"
__email__ = "sojan.koshy@huawei.com"

import time

from test.library.odl import Odl
from test.library.svrp import Router
from test.variables import variables as v


def setup_module(module):
    """Setup before test case execution"""
    print "\nSetup"
    global odl, rt1
    odl = Odl()
    rt1 = Router()

def teardown_module(module):
    """Tear down after test case execution"""
    print "\nTear down"
    odl.post_remove_lsp()
    odl.clean_up()
    rt1.clean_up()

def test_pcecc_sr_be():
    """Test PCECC SR BE"""
    print "Test PCECC SR BE"
    rt1.set_pce()
    time.sleep(1)
    assert rt1.check_pce_up()

    status, resp = odl.get_pcep_topology()
    assert status == 200 and (resp['topology'][0]['node'][0]["node-id"] ==
                              "pcc://" + v.rt1_node_id)

    params = {'pcc_node_id': v.rt1_node_id}
    status, resp = odl.post_add_lsp(params)
    assert status == 200 and resp['output'] == {}

    status, resp = odl.post_add_label()
    assert status == 200 and resp['output'] == {}

    resp = odl.post_remove_lsp()
