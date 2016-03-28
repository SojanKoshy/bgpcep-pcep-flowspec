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

#ODL RESTconf
base_url = "http://127.0.0.1:8181/restconf"
username = "admin"
password = "admin"

#PCE variables
odl_pce_server_ip = "172.16.0.13"
pcc1_node_id = "3.3.3.5"

#REST Transactions
odl_version = "beryllium"
rest_file_path = cur_dir + "/" + odl_version + "/"

add_lsp_file = rest_file_path + "add_lsp.json"
remove_lsp_file = rest_file_path + "remove_lsp.json"
add_label_file = rest_file_path + "add_label.json"
remove_label_file = rest_file_path + "remove_label.json"
