""""Variables for PCECC system test"""
# Copyright (c) 2016 Huawei Technologies Co., Ltd. and others.  All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v1.0 which accompanies this distribution,
# and is available at http://www.eclipse.org/legal/epl-v10.html

__author__ = "Sojan Koshy"
__copyright__ = "Copyright(c) 2016 Huawei Technologies Co., Ltd."
__license__ = "Eclipse Public License v1.0"
__email__ = "sojan.koshy@huawei.com"

import logging
from os.path import dirname


logging.basicConfig(level=logging.DEBUG)
cur_dir = dirname(__file__)

# TOPOLOGY
#                +-----+
#                | RT1 |
#                +-----+
#        E3/0/0 /       \7
#              /         \
#             /           \
#     E3/0/0 /             \7
#     +-----+               +-----+
#     | RT2 |---------------| RT3 |
#     +-----+ 8           8 +-----+

# ODL RESTconf
base_url = "http://127.0.0.1:8181/restconf"
username = "admin"
password = "admin"

# Variables for ODL connection
odl_pce_server_ip = "172.16.0.13"

# Variables for RTR Telnet login
rt1_telnet_ip = "172.16.3.11"
rt2_telnet_ip = "172.16.3.12"
rt3_telnet_ip = "172.16.3.13"
rtr_username = ""
rtr_password = "Root@123"

rt1_node_id = "1.1.1.1"
rt2_node_id = "2.2.2.2"
rt3_node_id = "3.3.3.3"

rt1_to_rt2_intf = "Ethernet3/0/0"
rt1_to_rt2_ip   = "10.1.1.1"

rt1_to_rt3_intf = "Ethernet3/0/7"
rt1_to_rt3_ip   = "12.1.1.1"

rt2_to_rt1_intf = "Ethernet3/0/0"
rt2_to_rt1_ip   = "10.1.1.2"

rt2_to_rt3_intf = "Ethernet3/0/8"
rt2_to_rt3_ip   = "11.1.1.1"

rt3_to_rt2_intf = "Ethernet3/0/8"
rt3_to_rt2_ip   = "11.1.1.2"

rt3_to_rt1_intf = "Ethernet3/0/7"
rt3_to_rt1_ip   = "12.1.1.2"

# Variables for PCEP topology
auto_tunnel_name = "PceTunnel"

# SVRP label range 5122-9217
rt1_out_label = "6001"
rt2_in_label  = "6001"
rt2_out_label = "6002"
rt3_in_label  = "6002"

rt1_to_rt2_adj_label = "7001"
rt1_to_rt3_adj_label = "7002"
rt2_to_rt1_adj_label = "7001"
rt2_to_rt3_adj_label = "7002"
rt3_to_rt2_adj_label = "7001"
rt3_to_rt1_adj_label = "7002"

# SVRP global range 4097-5121
rt1_global_label = "5001"
rt2_global_label = "5002"
rt3_global_label = "5003"

# REST Transactions
odl_version = "beryllium"
rest_file_path = cur_dir + "/" + odl_version + "/"

add_lsp_file                = rest_file_path + "add_lsp.json"
add_lsp_sr_file             = rest_file_path + "add_lsp_sr.json"
add_lsp_sr2_file            = rest_file_path + "add_lsp_sr2.json"
add_lsp_sr3_file            = rest_file_path + "add_lsp_sr3.json"
update_lsp_file             = rest_file_path + "update_lsp.json"
update_lsp_sr_file          = rest_file_path + "update_lsp_sr.json"
update_lsp_sr2_file         = rest_file_path + "update_lsp_sr2.json"
update_lsp_sr3_file         = rest_file_path + "update_lsp_sr3.json"
remove_lsp_file             = rest_file_path + "remove_lsp.json"
add_label_dwnld_in_out_file = rest_file_path + "add_label_dwnld_in_out.json"
add_label_dwnld_in_file     = rest_file_path + "add_label_dwnld_in.json"
add_label_dwnld_out_file    = rest_file_path + "add_label_dwnld_out.json"
add_label_map_node_file     = rest_file_path + "add_label_map_node.json"
add_label_map_adj_file      = rest_file_path + "add_label_map_adj.json"
add_label_db_sync_end_file  = rest_file_path + "add_label_db_sync_end.json"
remove_label_file           = rest_file_path + "remove_label.json"
