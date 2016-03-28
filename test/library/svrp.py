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

import telnetlib
import logging
import re

from test.variables import variables as v

class Router:    
    def __init__(self):
        self.log = logging.getLogger('RT1')
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
        self.tn = telnetlib.Telnet("172.16.3.15", timeout=5)
        self.read_until("Password:")
        self.send_cmd("Root@123", ">")
        self.send_cmd("system-view immediate", "]")
                
    def logout(self):
        self.send_cmd("return", ">")
        self.write("quit")
        self.read_all()
        self.tn.close()
        self.tn = None
        
    def set_pce(self):
        self.send_cmd("pce-client")
        self.send_cmd("connect-server " + v.odl_pce_server_ip)
        
    def unset_pce(self):
        self.send_cmd("pce-client")
        self.send_cmd("undo connect-server " + v.odl_pce_server_ip)
        
    def check_pce_up(self):
        ret = self.send_cmd("display pce protocol session")
        return re.search(v.odl_pce_server_ip + "\s*UP", ret)
        
    