""""Test case for PCECC CR LSP demo"""
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
    """Setup connections before test execution"""
    print "\nSetup"
    global odl, rt1, rt2
    odl = Odl(v.base_url)
    rt1 = Router('RT1', v.rt1_telnet_ip, v.rt1_username, v.rt1_password)
    rt2 = Router('RT2', v.rt2_telnet_ip, v.rt2_username, v.rt2_password)

def teardown_module(module):
    """Tear down connections and pcep operations after test execution"""
    print "\nTear down"
    odl.post_remove_lsp()
    odl.clean_up()
    rt1.clean_up()
    rt2.clean_up()

def test_pcecc_cr_lsp():
    """Test PCECC CR LSP"""
    print "Test PCECC CR LSP"

    # Configure PCE on ingress router and verify the pcep session
    params = {'node_id': v.rt1_node_id,
              'pce_server_ip': v.odl_pce_server_ip,
              'intf': v.rt1_to_rt2_intf,
              'ip': v.rt1_to_rt2_ip}
    rt1.init_basic_pce(params)
    time.sleep(1)
    assert rt1.check_pce_up()

    # Verify the pcep topology in ODL
    status, resp = odl.get_pcep_topology()
    assert status == 200 and (resp['topology'][0]['node'][0]["node-id"] ==
                              "pcc://" + params['node_id'])


    # Configure PCE on egress router and verify the pcep session
    params = {'node_id': v.rt2_node_id,
              'pce_server_ip': v.odl_pce_server_ip,
              'intf': v.rt2_to_rt1_intf,
              'ip': v.rt2_to_rt1_ip}
    rt2.init_basic_pce(params)
    time.sleep(1)
    assert rt2.check_pce_up()

    # Verify the pcep topology in ODL
    status, resp = odl.get_pcep_topology()
    assert status == 200 and (resp['topology'][0]['node'][1]["node-id"] ==
                              "pcc://" + params['node_id'])

    # Add an LSP to ingress router
    params = {'node_id': v.rt1_node_id,
              'pce_server_ip': v.odl_pce_server_ip}
    status, resp = odl.post_add_lsp(params)
    assert status == 200 and resp['output'] == {}

    # Add label to ingress router
    status, resp = odl.post_add_label(params)
    assert status == 200 and resp['output'] == {}

    # Remove the LSP from ingress router
    resp = odl.post_remove_lsp()
