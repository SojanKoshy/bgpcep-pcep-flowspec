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

from test.library.common import *  # @UnusedWildImport
from test.library.odl import Odl
from test.library.svrp import Router
from test.variables import variables as v


@tc_fixture
def setup_module(module):
    """Setup connections before test execution."""
    global odl, rt1, rt2, rt3
    odl = Odl(v.base_url)
    rt1 = Router('RT1', v.rt1_telnet_ip)
    rt2 = Router('RT2', v.rt2_telnet_ip)
    rt3 = Router('RT3', v.rt3_telnet_ip)

@tc_fixture
def teardown_module(module):
    """Tear down connections and pcep operations after test execution."""
    odl.clean_up()
    rt1.clean_up()
    rt2.clean_up()
    rt3.clean_up()

@tc
def test_pcecc_sr_be():
    """Test PCECC SR BE"""
    # Create PCEP session with all PCCs
    config_pce_on_ingress()
    config_pce_on_transit()
    config_pce_on_egress()

    # Send Label DB Sync End to all PCCs
    send_label_db_sync_end_to_egress()
    send_label_db_sync_end_to_transit()
    send_label_db_sync_end_to_ingress()

    # Download node labels on all PCCs
    download_node_labels_on_egress()
    download_node_labels_on_transit()
    download_node_labels_on_ingress()

    # Verify LSP IP ping
    verify_lsp_ping()

    # Remove labels
#     remove_labels_on_ingress()
#     remove_labels_on_transit()
#     remove_labels_on_egress()

@tc_step
def config_pce_on_ingress():
    """Configure PCE on ingress router and verify the pcep session."""
    params = {'node_id': v.rt1_node_id,
              'pce_server_ip': v.odl_pce_server_ip,
              'intf': v.rt1_to_rt2_intf,
              'ip': v.rt1_to_rt2_ip,
              'intf2': v.rt1_to_rt3_intf,
              'ip2': v.rt1_to_rt3_ip}
    rt1.set_basic_pce(params)
    assert rt1.check_pce_up(params)

@tc_step
def config_pce_on_transit():
    """Configure PCE on egress router and verify the pcep session."""
    params = {'node_id': v.rt2_node_id,
              'pce_server_ip': v.odl_pce_server_ip,
              'intf': v.rt2_to_rt1_intf,
              'ip': v.rt2_to_rt1_ip,
              'intf2': v.rt2_to_rt3_intf,
              'ip2': v.rt2_to_rt3_ip}
    rt2.set_basic_pce(params)
    assert rt2.check_pce_up(params)

@tc_step
def config_pce_on_egress():
    """Configure PCE on egress router and verify the pcep session."""
    params = {'node_id': v.rt3_node_id,
              'pce_server_ip': v.odl_pce_server_ip,
              'intf': v.rt3_to_rt2_intf,
              'ip': v.rt3_to_rt2_ip,
              'intf2': v.rt3_to_rt1_intf,
              'ip2': v.rt3_to_rt1_ip}
    rt3.set_basic_pce(params)
    assert rt3.check_pce_up(params)

@tc_step
def send_label_db_sync_end_to_egress():
    """Send label DB sync end to egress using add-label"""
    params = {'node_id': v.rt3_node_id}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}
    rt3.wait_for_ospf_peer_full()

@tc_step
def send_label_db_sync_end_to_transit():
    """Send label DB sync end to transit using add-label"""
    params = {'node_id': v.rt2_node_id}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

@tc_step
def send_label_db_sync_end_to_ingress():
    """Send label DB sync end to ingress using add-label"""
    params = {'node_id': v.rt1_node_id}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

@tc_step
def download_node_labels_on_egress():
    """Add node label to egress router."""
    params = {'node_id': v.rt3_node_id,
              'fec_node_id': v.rt3_node_id,
              'node_label': v.rt3_global_label}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

    params = {'node_id': v.rt3_node_id,
              'fec_node_id': v.rt1_node_id,
              'node_label': v.rt1_global_label}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

    params = {'node_id': v.rt3_node_id,
              'fec_node_id': v.rt2_node_id,
              'node_label': v.rt2_global_label}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

@tc_step
def download_node_labels_on_transit():
    """Add node label to transit router."""
    params = {'node_id': v.rt2_node_id,
              'fec_node_id': v.rt2_node_id,
              'node_label': v.rt2_global_label}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

    params = {'node_id': v.rt2_node_id,
              'fec_node_id': v.rt1_node_id,
              'node_label': v.rt1_global_label}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

    params = {'node_id': v.rt2_node_id,
              'fec_node_id': v.rt3_node_id,
              'node_label': v.rt3_global_label}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

@tc_step
def download_node_labels_on_ingress():
    """Add node label to ingress router."""
    params = {'node_id': v.rt1_node_id,
              'fec_node_id': v.rt1_node_id,
              'node_label': v.rt1_global_label}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

    params = {'node_id': v.rt1_node_id,
              'fec_node_id': v.rt2_node_id,
              'node_label': v.rt2_global_label}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

    params = {'node_id': v.rt1_node_id,
              'fec_node_id': v.rt3_node_id,
              'node_label': v.rt3_global_label}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

@tc_step
def verify_lsp_ping():
    """Verify ping is successful."""
    params = {'ip': v.rt3_node_id}
    assert rt1.check_ping(params)
