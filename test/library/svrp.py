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
    def __init__(self, sysname, ip):
        self.ip = ip
        self.username = v.rtr_username
        self.password = v.rtr_password
        self.log = logging.getLogger(sysname)
        self.login()

    def clean_up(self):
        self.unset_basic_pce()
        self.logout()

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

    def set_basic_pce(self, params=None):
        if params.has_key("pce_server_ip"):
            self.pce_server_ip = params['pce_server_ip'] # Required for auto undo
            self.send_cmd("mpls lsr-id " + params['node_id'])
            self.send_cmd("mpls")
            self.send_cmd("mpls te")
            self.send_cmd("mpls te pce delegate")
            self.send_cmd("mpls ldp")
            self.send_cmd("interface loopback0")
            self.send_cmd("ip address " + params['node_id'] + ' 32')
            self.send_cmd("quit")
            self.send_cmd("pce-client")
            self.send_cmd("capability initiated-lsp")
            self.send_cmd("connect-server " + params['pce_server_ip'])
            self.send_cmd("quit")
            self.send_cmd("quit")

        if params.has_key("intf"):
            self.intf = params['intf']   # Required for auto undo
            self.send_cmd("interface " + params['intf'])
            self.send_cmd("ip address " + params['ip'] + ' 24')
            self.send_cmd("mpls")
            self.send_cmd("mpls te")
            self.send_cmd("quit")

        if params.has_key("intf2"):
            self.intf2 = params['intf2'] # Required for auto undo
            self.send_cmd("interface " + params['intf2'])
            self.send_cmd("ip address " + params['ip2'] + ' 24')
            self.send_cmd("mpls")
            self.send_cmd("mpls te")
            self.send_cmd("quit")

    def unset_basic_pce(self, params=None):
        if params is None:
            # Auto undo
            params = {}
            self._move_attr_to_params(params, 'pce_server_ip', 'intf', 'intf2')

        if params.has_key("pce_server_ip"):
            self.send_cmd("undo mpls")
            self.send_cmd("y")
            self.send_cmd("undo mpls lsr-id")
            self.send_cmd("undo interface loopback0")
            self.send_cmd("pce-client")
            self.send_cmd("undo connect-server " + params['pce_server_ip'])
            self.send_cmd("quit")
            # self.send_cmd("undo pce-client")

        if params.has_key("intf"):
            self.send_cmd("interface " + params['intf'])
            self.send_cmd("undo ip address")
            self.send_cmd("undo mpls")
            self.send_cmd("quit")

        if params.has_key("intf2"):
            self.send_cmd("interface " + params['intf2'])
            self.send_cmd("undo ip address")
            self.send_cmd("undo mpls")
            self.send_cmd("quit")

    def check_pce_up(self, params=None):
        ret = self.send_cmd("display pce protocol session")
        return re.search(params['pce_server_ip'] + "\s*UP", ret)

    def _move_attr_to_params(self, params, *attrs):
        for attr in attrs:
            if hasattr(self, attr):
                params[attr] = self.__dict__[attr]
                delattr(self, attr)
