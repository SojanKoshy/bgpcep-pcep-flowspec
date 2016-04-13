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
#     +-----+ 3           1 +-----+ 5           3 +-----+
#     | RT1 |---------------| RT3 |---------------| RT5 |
#     +-----+               +-----+               +-----+
#  E3/0/2|   4\          2/   4|    \6         4/    |6
#        |      \       /      |      \       /      |
#        |        \   /        |        \   /        |
#        |          x          |          x          |
#        |        /   \        |        /   \        |
#        |      /       \      |      /       \      |
#  E3/0/1|   3/          2\   3|   5/           \3   |5
#     +-----+               +-----+               +-----+
#     | RT2 |---------------| RT4 |---------------| RT6 |
#     +-----+ 4           2 +-----+ 6           4 +-----+

# ODL RESTconf
base_url = "http://127.0.0.1:8181/restconf"
username = "admin"
password = "admin"

# Variables for ODL connection
odl_pce_server_ip = "172.16.0.136"

# Variables for RTR Telnet login
rtr_telnet_ip = ["", "172.16.3.11", "172.16.3.12", "172.16.3.13", "172.16.3.14", "172.16.3.15", "172.16.3.16"]
rtrs = [1, 2, 3, 4, 5, 6]

rtr_username = ""
rtr_password = "Root@123"

# Variables for PCEP topology
auto_tunnel_name = "PceTunnel"

rtr_node_id = ["", "1.1.1.1", "2.2.2.2", "3.3.3.3", "4.4.4.4", "5.5.5.5", "6.6.6.6"]

rtr_intfs   = [["", "------------1", "------------2", "------------3", "------------4", "------------5", "------------6"],
               ["",              "", "Ethernet3/0/2", "Ethernet3/0/3", "Ethernet3/0/4",              "",              ""],
               ["", "Ethernet3/0/1",              "", "Ethernet3/0/3", "Ethernet3/0/4",              "",              ""],
               ["", "Ethernet3/0/1", "Ethernet3/0/2",              "", "Ethernet3/0/4", "Ethernet3/0/5", "Ethernet3/0/6"],
               ["", "Ethernet3/0/1", "Ethernet3/0/2", "Ethernet3/0/3",              "", "Ethernet3/0/5", "Ethernet3/0/6"],
               ["",              "",              "", "Ethernet3/0/3", "Ethernet3/0/4",              "", "Ethernet3/0/6"],
               ["",              "",              "", "Ethernet3/0/3", "Ethernet3/0/4", "Ethernet3/0/5",              ""]]

rtr_ips     = [["", "-------1", "-------2", "-------3", "-------4", "-------5", "-------6"],
               ["",         "", "12.1.1.1", "13.1.1.1", "14.1.1.1",         "",         ""],
               ["", "12.1.1.2",         "", "23.1.1.1", "24.1.1.1",         "",         ""],
               ["", "13.1.1.2", "23.1.1.2",         "", "34.1.1.1", "35.1.1.1", "36.1.1.1"],
               ["", "14.1.1.2", "24.1.1.2", "34.1.1.2",         "", "45.1.1.1", "46.1.1.1"],
               ["",         "",         "", "35.1.1.2", "45.1.1.2",         "", "56.1.1.1"],
               ["",         "",         "", "36.1.1.2", "46.1.1.2", "56.1.1.2",         ""]]

# SVRP label range 5122-9217
rtr_adj_labels = [["", "---1", "---2", "---3", "---4", "---5", "---6"],
                  ["",     "", "7002", "7003", "7004",     "",     ""],
                  ["", "7001",     "", "7003", "7004",     "",     ""],
                  ["", "7001", "7002",     "", "7004", "7005", "7006"],
                  ["", "7001", "7002", "7003",     "", "7005", "7006"],
                  ["",     "",     "", "7003", "7004",     "", "7006"],
                  ["",     "",     "", "7003", "7004", "7005",     ""]]

# SVRP global range 4097-5121
rtr_global_label = ["", "5001", "5002", "5003", "5004", "5005", "5006"]
