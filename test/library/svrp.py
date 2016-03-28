""""Library for Huawei Router Telnet operations for PCECC system test"""
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
import re
import telnetlib

from test.variables import variables as v


class Router:
    def __init__(self, sysname, ip, username, password):
        self.ip = ip
        self.username = username
        self.password = password
        self.log = logging.getLogger(sysname)
        self.login()
        return

    def clean_up(self):
        self.unset_pce()
        self.logout()
        return

    def send_cmd(self, command, prompt=""):
        if prompt:
            self.prompt = prompt

        self.write(command)
        return self.read_until(self.prompt)

    def write(self, command):
        self.tn.write(command + "\n")

    def read_until(self, expect):
        ret = self.tn.read_until(expect)
        self.log.debug(" " + ret + "\n")
        return ret

    def read_all(self):
        ret = self.tn.read_all()
        self.log.debug(" " + ret + "\n")
        return ret

    def login(self):
        self.log.info("Login to router")
        self.tn = telnetlib.Telnet(self.ip, timeout=5)
        self.read_until("Password:")
        self.send_cmd(self.password, ">")
        self.send_cmd("system-view immediate", "]")

    def logout(self):
        self.send_cmd("return", ">")
        self.write("quit")
        self.read_all()
        self.tn.close()
        self.tn = None

    def init_basic_pce(self, param={}):
        self.send_cmd("mpls lsr-id " + param['node_id'])
        self.send_cmd("mpls")
        self.send_cmd("mpls te")
        self.send_cmd("mpls te pce delegate")
        self.send_cmd("mpls ldp")
        self.send_cmd("pce-client")
        self.send_cmd("capability initiated-lsp")
        self.send_cmd("connect-server " + param['pce_server_ip'])
        self.send_cmd("interface " + param['intf'])
        self.send_cmd("ip address " + param['ip'] + ' 24')
        self.send_cmd("mpls")
        self.send_cmd("mpls te")
        self.send_cmd("interface loopback0")
        self.send_cmd("ip address " + param['node_id'] + ' 32')
        self.send_cmd("quit")

    def unset_pce(self, param={}):
#         self.send_cmd("undo mpls")
        self.send_cmd("undo pce-client")

    def check_pce_up(self, param={}):
        ret = self.send_cmd("display pce protocol session")
        return re.search(v.odl_pce_server_ip + "\s*UP", ret)
