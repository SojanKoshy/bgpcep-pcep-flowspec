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
from test.variables import variables6_rtr as v


@tc_fixture
def setup_module(module):
    """Setup connections before test execution."""
    global odl, rtr
    odl = Odl(v.base_url)

    rtr = [0] # Start storing router objects from index 1
    for i in v.rtrs:
        rtr.append(Router('RT' + str(i), v.rtr_telnet_ip[i]))

@tc_fixture
def teardown_module(module):
    """Tear down connections and pcep operations after test execution."""
    odl.clean_up()
    for i in v.rtrs:
        rtr[i].clean_up()

@tc
def test_pcecc_sr_be():
    """Test PCECC SR BE"""
    # Create PCEP session with all PCCs
    config_pce_on_all_nodes()

    # Send Label DB Sync End to all PCCs
    send_label_db_sync_end_to_all_nodes()

    # Download node labels on all PCCs
    download_node_labels_on_all_nodes()

    # Download adjacency labels on all PCCs
    download_adj_labels_on_all_nodes()

    # Initiate PCECC SR-TE LSP on ingress
    add_lsp()

    # Get reported-lsp and store all required fields
    get_reported_lsp()

    # Send PcUpdate Message to ingress
    update_lsp()

    # Initiate PCECC SR-TE LSP on ingress
    add_lsp2()

    # Get reported-lsp and store all required fields
    get_reported_lsp2()

    # Send PcUpdate Message to ingress
    update_lsp2()

    # Verify LSP ping
    verify_lsp_ping()

    # Verify LSP2 ping
    verify_lsp2_ping()

    # Remove labels
#     remove_labels_on_ingress()
#     remove_labels_on_transit()
#     remove_labels_on_egress()

    # Remove LSP
    remove_lsp()

    # Remove LSP2
    remove_lsp2()
@tc_step
def config_pce_on_all_nodes():
    """Configure PCE on all routers and verify the pcep session."""
    for i in v.rtrs:
        params = {'node_id': v.rtr_node_id[i],
                  'pce_server_ip': v.odl_pce_server_ip,
                  'intfs': v.rtr_intfs[i],
                  'ips': v.rtr_ips[i]}
        rtr[i].set_basic_pce(params)
        assert rtr[i].check_pce_up(params)

    rtr[1].wait_for_ospf_peer_full()

@tc_step
def send_label_db_sync_end_to_all_nodes():
    """Send label DB sync end to all nodes using add-label"""
    for i in v.rtrs:
        params = {'node_id': v.rtr_node_id[i]}
        status, resp = odl.post_add_label(params)
        assert status == 200
        assert resp['output'] == {}

@tc_step
def download_node_labels_on_all_nodes():
    """Add node labels to all nodes."""
    for i in v.rtrs:
        for j in v.rtrs:
            params = {'node_id': v.rtr_node_id[i],
                      'fec_node_id': v.rtr_node_id[j],
                      'node_label': v.rtr_global_label[j]}
            status, resp = odl.post_add_label(params)
            assert status == 200
            assert resp['output'] == {}

@tc_step
def download_adj_labels_on_all_nodes():
    """Add adjacency labels to all nodes."""
    for i in v.rtrs:
        for j in v.rtrs:
            if v.rtr_adj_labels[i][j]:
                params = {'node_id': v.rtr_node_id[i],
                          'adj_label': v.rtr_adj_labels[i][j],
                          'fec_local_ip': v.rtr_ips[i][j],
                          'fec_remote_ip': v.rtr_ips[j][i]}
                status, resp = odl.post_add_label(params)
                assert status == 200
                assert resp['output'] == {}

@tc_step
def add_lsp():
    """Add an LSP to ingress router."""
    params = {'node_id': v.rtr_node_id[1],
              'source': v.rtr_node_id[1],
              'destination': v.rtr_node_id[6],
              'name': v.auto_tunnel_name,
              'sid2': 1}  # FIXME: SID not used
    status, resp = odl.post_add_lsp(params)
    assert status == 200
    assert resp['output'] == {}

@tc_step
def add_lsp2():
    """Add an LSP 2 to ingress router."""
    params = {'node_id': v.rtr_node_id[1],
              'source': v.rtr_node_id[1],
              'destination': v.rtr_node_id[6],
              'name': v.auto_tunnel_name + "2",
              'sid3': 1}  # FIXME: SID not used
    status, resp = odl.post_add_lsp(params)
    assert status == 200
    assert resp['output'] == {}

@tc_step
def get_reported_lsp():
    """Verify ingress router in the pcep topology in ODL."""
    params = {'node_id': v.rtr_node_id[1]}
    status, resp = odl.get_pcep_topology()
    assert status == 200

    index = get_matching_index(resp['topology'][0]['node'], 'node-id', "pcc://" + params['node_id'])
    assert index != -1

    node = resp['topology'][0]['node'][index]

    global plsp_id, tunnel_id
    lsp = node["network-topology-pcep:path-computation-client"]["reported-lsp"][0]["path"][0]["odl-pcep-ietf-stateful07:lsp"]
    plsp_id = lsp["plsp-id"]
    tunnel_id = lsp["tlvs"]["lsp-identifiers"]["tunnel-id"]

@tc_step
def get_reported_lsp2():
    """Verify ingress router lsp2 in the pcep topology in ODL."""
    params = {'node_id': v.rtr_node_id[1]}
    status, resp = odl.get_pcep_topology()
    assert status == 200

    index = get_matching_index(resp['topology'][0]['node'], 'node-id', "pcc://" + params['node_id'])
    assert index != -1

    node = resp['topology'][0]['node'][index]

    global plsp_id, tunnel_id
    lsp = node["network-topology-pcep:path-computation-client"]["reported-lsp"][1]["path"][0]["odl-pcep-ietf-stateful07:lsp"]
    plsp_id = lsp["plsp-id"]
    tunnel_id = lsp["tlvs"]["lsp-identifiers"]["tunnel-id"]

@tc_step
def update_lsp():
    """Update LSP to ingress router to make it Active."""
    params = {'node_id': v.rtr_node_id[1],
              'source': v.rtr_node_id[1],
              'destination': v.rtr_node_id[6],
              'plsp_id': plsp_id,
              'tunnel_id': tunnel_id,
              'name': v.auto_tunnel_name,
              'sid2': 1}  # FIXME: SID not used
    status, resp = odl.post_update_lsp(params)
    assert status == 200
    assert resp['output'] == {}

@tc_step
def update_lsp2():
    """Update LSP 2 to ingress router to make it Active."""
    params = {'node_id': v.rtr_node_id[1],
              'source': v.rtr_node_id[1],
              'destination': v.rtr_node_id[6],
              'plsp_id': plsp_id,
              'tunnel_id': tunnel_id,
              'name': v.auto_tunnel_name + "2",
              'sid3': 1}  # FIXME: SID not used
    status, resp = odl.post_update_lsp(params)
    assert status == 200
    assert resp['output'] == {}

@tc_step
def verify_lsp_ping():
    """Verify ping is successful."""
    params = {'name': v.auto_tunnel_name}
    assert rtr[1].check_ping(params)

@tc_step
def verify_lsp2_ping():
    """Verify ping is successful."""
    params = {'name': v.auto_tunnel_name + "2"}
    assert rtr[1].check_ping(params)

@tc_step
def remove_lsp():
    """Remove the LSP from ingress router."""
    params = {'node_id': v.rtr_node_id[1],
              'name': v.auto_tunnel_name}
    status, resp = odl.post_remove_lsp(params)
    assert status == 200
    assert resp['output'] == {}

@tc_step
def remove_lsp2():
    """Remove the LSP from ingress router."""
    params = {'node_id': v.rtr_node_id[1],
              'name': v.auto_tunnel_name + "2"}
    status, resp = odl.post_remove_lsp(params)
    assert status == 200
    assert resp['output'] == {}
