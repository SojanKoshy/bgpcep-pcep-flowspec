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

from test.library.odl import Odl
from test.library.svrp import Router
from test.variables import variables as v


def setup_module(module):
    """Setup connections before test execution."""
    print "\nSetup"
    global odl, rt1, rt2, rt3
    odl = Odl(v.base_url)
    rt1 = Router('RT1', v.rt1_telnet_ip)
    rt2 = Router('RT2', v.rt2_telnet_ip)
    rt3 = Router('RT3', v.rt3_telnet_ip)

def teardown_module(module):
    """Tear down connections and pcep operations after test execution."""
    print "\nTear down"
    odl.clean_up()
    rt1.clean_up()
    rt2.clean_up()
    rt3.clean_up()

def test_pcecc_sr_be():
    """Test PCECC SR BE"""
    print "Test PCECC SR BE"

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

    # Download adjacency labels on all PCCs
    download_adj_labels_on_egress()
    download_adj_labels_on_transit()
    download_adj_labels_on_ingress()

    # Initiate PCECC SR-TE LSP on ingress
    add_lsp()

    # Get reported-lsp and store all required fields
    get_reported_lsp()

    # Send PcUpdate Message to ingress
    update_lsp()

    # Verify LSP ping
    verify_lsp_ping()

    # Remove labels
#     remove_labels_on_ingress()
#     remove_labels_on_transit()
#     remove_labels_on_egress()

    # Remove LSP
    remove_lsp()


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
    rt3.wait_for_ospf_peer_full()

def send_label_db_sync_end_to_egress():
    """Send label DB sync end to egress using add-label"""
    params = {'node_id': v.rt3_node_id}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

def send_label_db_sync_end_to_transit():
    """Send label DB sync end to transit using add-label"""
    params = {'node_id': v.rt2_node_id}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

def send_label_db_sync_end_to_ingress():
    """Send label DB sync end to ingress using add-label"""
    params = {'node_id': v.rt1_node_id}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

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

def download_adj_labels_on_egress():
    """Add adjacency labels to egress router."""
    params = {'node_id': v.rt3_node_id,
              'adj_label': v.rt3_to_rt2_adj_label,
              'fec_local_ip': v.rt3_to_rt2_ip,
              'fec_remote_ip': v.rt2_to_rt3_ip}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

    params = {'node_id': v.rt3_node_id,
              'adj_label': v.rt3_to_rt1_adj_label,
              'fec_local_ip': v.rt3_to_rt1_ip,
              'fec_remote_ip': v.rt1_to_rt3_ip}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

def download_adj_labels_on_transit():
    """Add adjacency labels to transit router."""
    params = {'node_id': v.rt2_node_id,
              'adj_label': v.rt2_to_rt1_adj_label,
              'fec_local_ip': v.rt2_to_rt1_ip,
              'fec_remote_ip': v.rt1_to_rt2_ip}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

    params = {'node_id': v.rt2_node_id,
              'adj_label': v.rt2_to_rt3_adj_label,
              'fec_local_ip': v.rt2_to_rt3_ip,
              'fec_remote_ip': v.rt3_to_rt2_ip}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

def download_adj_labels_on_ingress():
    """Add adjacency labels to ingress router."""
    params = {'node_id': v.rt1_node_id,
              'adj_label': v.rt1_to_rt2_adj_label,
              'fec_local_ip': v.rt1_to_rt2_ip,
              'fec_remote_ip': v.rt2_to_rt1_ip}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

    """Add adjacency labels to ingress router."""
    params = {'node_id': v.rt1_node_id,
              'adj_label': v.rt1_to_rt3_adj_label,
              'fec_local_ip': v.rt1_to_rt3_ip,
              'fec_remote_ip': v.rt3_to_rt1_ip}
    status, resp = odl.post_add_label(params)
    assert status == 200
    assert resp['output'] == {}

def add_lsp():
    """Add an LSP to ingress router."""
    params = {'node_id': v.rt1_node_id,
              'source': v.rt1_node_id,
              'destination': v.rt3_node_id,
              'name': v.auto_tunnel_name,
              'sid': 1} #FIXME: SID not used
    status, resp = odl.post_add_lsp(params)
    assert status == 200
    assert resp['output'] == {}

def get_reported_lsp():
    """Verify ingress router in the pcep topology in ODL."""
    params = {'node_id': v.rt1_node_id}
    status, resp = odl.get_pcep_topology()
    assert status == 200

    index = odl.get_matching_index(resp['topology'][0]['node'], 'node-id', "pcc://" + params['node_id'])
    assert index != -1

    node = resp['topology'][0]['node'][index]

    global plsp_id, tunnel_id
    lsp = node["network-topology-pcep:path-computation-client"]["reported-lsp"][0]["path"][0]["odl-pcep-ietf-stateful07:lsp"]
    plsp_id = lsp["plsp-id"]
    tunnel_id = lsp["tlvs"]["lsp-identifiers"]["tunnel-id"]

def update_lsp():
    """Update LSP to ingress router to make it Active."""
    params = {'node_id': v.rt1_node_id,
              'source': v.rt1_node_id,
              'destination': v.rt3_node_id,
              'plsp_id': plsp_id,
              'tunnel_id': tunnel_id,
              'name': v.auto_tunnel_name,
              'sid': 1} #FIXME: SID not used
    status, resp = odl.post_update_lsp(params)
    assert status == 200
    assert resp['output'] == {}

def verify_lsp_ping():
    """Verify ping is successful."""
    params = {'name': v.auto_tunnel_name}
    assert rt1.check_ping(params)

def remove_lsp():
    """Remove the LSP from ingress router."""
    params = {'node_id': v.rt1_node_id,
              'name': v.auto_tunnel_name}
    status, resp = odl.post_remove_lsp(params)
    assert status == 200
    assert resp['output'] == {}