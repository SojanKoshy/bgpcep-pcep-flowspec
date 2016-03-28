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

# PCE variables
odl_pce_server_ip = "172.16.0.13"

rt1_telnet_ip = "172.16.3.15"
rt1_username = ""
rt1_password = "Root@123"

rt2_telnet_ip = "172.16.3.16"
rt2_username = ""
rt2_password = "Root@123"

rt1_node_id = "3.3.3.5"
rt1_to_rt2_intf = "Ethernet3/0/0"
rt1_to_rt2_ip = "10.1.1.1"

rt2_node_id = "3.3.3.6"
rt2_to_rt1_intf = "Ethernet3/0/0"
rt2_to_rt1_ip = "10.1.1.2"

# REST Transactions
odl_version = "beryllium"
rest_file_path = cur_dir + "/" + odl_version + "/"

add_lsp_file = rest_file_path + "add_lsp.json"
remove_lsp_file = rest_file_path + "remove_lsp.json"
add_label_file = rest_file_path + "add_label.json"
remove_label_file = rest_file_path + "remove_label.json"
