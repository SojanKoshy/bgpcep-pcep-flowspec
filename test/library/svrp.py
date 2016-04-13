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

from test.library.common import *  # @UnusedWildImport
from test.variables import variables as v


class Router:
    """Connect to SVRP and do Telnet send and receive operations."""
    def __init__(self, sysname, ip):
        """Connect to SVRP."""
        self.ip = ip
        self.username = v.rtr_username
        self.password = v.rtr_password
        self.log = logging.getLogger(sysname)
        self.login()

    def clean_up(self):
        """Clean up SVRP configuration."""
        self.unset_basic_pce()
        self.logout()

    def send_cmd(self, command, prompt=""):
        """Write a command and read until next prompt."""
        if prompt:
            self.prompt = prompt

        self.write(command)
        return self.read_until(self.prompt)

    def write(self, command):
        """Write a command to Telnet session."""
        self.tn.write(command + "\n")

    def read_until(self, expect):
        """Read buffer on Telnet session."""
        ret = self.tn.read_until(expect)
        self.log.debug(" " + ret + "\n")
        return ret

    def read_all(self):
        """Read buffer on Telnet session before closing."""
        ret = self.tn.read_all()
        self.log.debug(" " + ret + "\n")
        return ret

    def login(self):
        """Login to SVRP with credentials."""
        self.log.info("Login to router")
        self.tn = telnetlib.Telnet(self.ip, timeout=5)
        self.read_until("Password:")
        self.send_cmd(self.password, ">")
        self.send_cmd("system-view immediate", "]")

    def logout(self):
        """Gracefully logout SVRP Telnet session."""
        self.send_cmd("return", ">")
        self.write("quit")
        self.read_all()
        self.tn.close()
        self.tn = None

    def set_basic_pce(self, params=None):
        """Configure the basic commands required for pce session."""
        if params.has_key("pce_server_ip"):
            self.pce_server_ip = params['pce_server_ip']  # Required for auto undo
            self.send_cmd("mpls lsr-id " + params['node_id'])
            self.send_cmd("mpls")
            self.send_cmd("mpls te")
            self.send_cmd("mpls te pce delegate")
            self.send_cmd("mpls ldp")
            self.send_cmd("interface loopback0")
            self.send_cmd("ip address " + params['node_id'] + ' 32')
            self.send_cmd("ospf enable area 1")
            self.send_cmd("quit")
            self.send_cmd("pce-client")
            self.send_cmd("connect-server " + params['pce_server_ip'])
            self.send_cmd("quit")
            self.send_cmd("quit")

        if params.has_key("intf"):
            self.intf = params['intf']  # Required for auto undo
            self.send_cmd("interface " + params['intf'])
            self.send_cmd("ip address " + params['ip'] + ' 24')
            self.send_cmd("mpls")
            self.send_cmd("mpls te")
            self.send_cmd("ospf enable area 1")
            self.send_cmd("ospf timer hello 1")
            self.send_cmd("quit")

        if params.has_key("intf2"):
            self.intf2 = params['intf2']  # Required for auto undo
            self.send_cmd("interface " + params['intf2'])
            self.send_cmd("ip address " + params['ip2'] + ' 24')
            self.send_cmd("mpls")
            self.send_cmd("mpls te")
            self.send_cmd("ospf enable area 1")
            self.send_cmd("ospf timer hello 1")
            self.send_cmd("quit")

        if params.has_key("intfs"):
            self.intfs = params['intfs']  # Required for auto undo
            for i, intf in enumerate(params['intfs']):
                if intf:
                    ip = params['ips'][i]
                    self.send_cmd("interface " + intf)
                    self.send_cmd("ip address " + ip + ' 24')
                    self.send_cmd("mpls")
                    self.send_cmd("mpls te")
                    self.send_cmd("ospf enable area 1")
                    self.send_cmd("ospf timer hello 1")
                    self.send_cmd("quit")

        self.send_cmd("ospf 1 router-id " + params['node_id'])
        self.send_cmd("area 1")
        self.send_cmd("quit")
        self.send_cmd("quit")

    def unset_basic_pce(self, params=None):
        """Undo the configured commands of pce session and also support auto tear down."""
        if params is None:
            # Auto undo
            params = {}
            move_attr_to_params(self, params, 'pce_server_ip', 'intf', 'intf2', 'intfs')

        if params.has_key("pce_server_ip"):
            self.send_cmd("pce-client")
            self.send_cmd("undo connect-server " + params['pce_server_ip'])
            self.send_cmd("quit")
            self.send_cmd("undo pce-client")
            self.send_cmd("undo mpls")
            self.send_cmd("y")
            self.send_cmd("undo mpls lsr-id")
            self.send_cmd("undo interface loopback0")

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

        if params.has_key("intfs"):
            for intf in params['intfs']:
                if intf:
                    self.send_cmd("interface " + intf)
                    self.send_cmd("undo ip address")
                    self.send_cmd("undo mpls")
                    self.send_cmd("quit")

        self.send_cmd("undo ospf 1")
        self.send_cmd("y")

    def wait_for_ospf_peer_full(self, params=None):
        """Wait for OSPF peer to become full. Wait Time = Hello Time * 4."""
        self.log.info("Waiting 12 seconds for OSPF peer FULL...")
        time.sleep(12)

    @assert_multi(message="Waiting for PCEP session to be UP")
    def check_pce_up(self, params=None):
        ret = self.send_cmd("display pce protocol session")
        return re.search(params['pce_server_ip'] + "\s*UP", ret)

    def check_ping(self, params=None):
        self.log.info("Verifying ping...")
        if params.has_key('name'):
            ret = self.send_cmd("ping lsp -c 4 te auto-tunnel " + params['name'])
        elif params.has_key('ip'):
            ret = self.send_cmd("ping lsp -c 4 ip " + params['ip'] + " 32")

        raw_input("Paused!")

        if re.search("Error", ret):
            return False

        return re.search("0 packet\(s\) received", ret) == None
