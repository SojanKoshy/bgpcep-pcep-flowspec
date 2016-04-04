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

# ODL RESTconf
base_url = "http://127.0.0.1:8181/restconf"
username = "admin"
password = "admin"

# Variables for RTR Telnet login
rt1_telnet_ip = "172.16.3.15"
rt2_telnet_ip = "172.16.3.16"
rt3_telnet_ip = "172.16.3.14"
rtr_username = ""
rtr_password = "Root@123"

# Variables for PCEP topology
odl_pce_server_ip = "172.16.0.15"

rt1_node_id = "3.3.3.5"
rt2_node_id = "3.3.3.6"
rt3_node_id = "3.3.3.7"

rt1_to_rt2_intf = "Ethernet3/0/0"
rt1_to_rt2_ip   = "10.1.1.1"

rt2_to_rt1_intf = "Ethernet3/0/0"
rt2_to_rt1_ip   = "10.1.1.2"

rt2_to_rt3_intf = "Ethernet3/0/1"
rt2_to_rt3_ip   = "11.1.1.1"

rt3_to_rt2_intf = "Ethernet3/0/1"
rt3_to_rt2_ip   = "11.1.1.2"

# SVRP label range 5122-9217
rt1_out_label = "6001"
rt2_in_label  = "6001"
rt2_out_label = "6002"
rt3_in_label  = "6002"

rt1_adj_label = "7001"
rt2_adj_label = "7002"
rt3_adj_label = "7003"

# SVRP global range 4097-5121
rt1_global_label = "5001"
rt2_global_label = "5002"
rt3_global_label = "5003"

# REST Transactions
odl_version = "beryllium"
rest_file_path = cur_dir + "/" + odl_version + "/"

add_lsp_file                = rest_file_path + "add_lsp.json"
update_lsp_file             = rest_file_path + "update_lsp.json"
remove_lsp_file             = rest_file_path + "remove_lsp.json"
add_label_in_out_file       = rest_file_path + "add_label_in_out.json"
add_label_in_file           = rest_file_path + "add_label_in.json"
add_label_out_file          = rest_file_path + "add_label_out.json"
add_label_db_sync_end_file  = rest_file_path + "add_label_db_sync_end.json"
remove_label_file           = rest_file_path + "remove_label.json"
