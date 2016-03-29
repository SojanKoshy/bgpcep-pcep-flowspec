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

# Variables for RTR telnet login
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
rt1_to_rt2_ip = "10.1.1.1"

rt2_to_rt1_intf = "Ethernet3/0/0"
rt2_to_rt1_ip = "10.1.1.2"

rt2_to_rt3_intf = "Ethernet3/0/1"
rt2_to_rt3_ip = "11.1.1.2"

rt3_to_rt2_intf = "Ethernet3/0/1"
rt3_to_rt2_ip = "11.1.1.3"

rt1_out_label = "1701"
rt2_in_label = "1702"
rt2_out_label = "1703"
rt3_in_label = "1704"

rt1_global_label = "1501"
rt2_global_label = "1502"
rt3_global_label = "1503"

rt1_adj_label = "1601"
rt2_adj_label = "1602"
rt3_adj_label = "1603"

# REST Transactions
odl_version = "beryllium"
rest_file_path = cur_dir + "/" + odl_version + "/"

add_lsp_file = rest_file_path + "add_lsp.json"
add_lsp_file2 = rest_file_path + "add_lsp.json2"
remove_lsp_file = rest_file_path + "remove_lsp.json"
add_label_file = rest_file_path + "add_label.json"
remove_label_file = rest_file_path + "remove_label.json"
