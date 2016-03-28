""""Library for ODL REST operations for PCECC system test"""
# Copyright (c) 2016 Huawei Technologies Co., Ltd. and others.  All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v1.0 which accompanies this distribution,
# and is available at http://www.eclipse.org/legal/epl-v10.html

__author__ = "Sojan Koshy"
__copyright__ = "Copyright(c) 2016 Huawei Technologies Co., Ltd."
__license__ = "Eclipse Public License v1.0"
__email__ = "sojan.koshy@huawei.com"

import httplib
import json
import logging
from restful_lib import Connection
from string import Template

from test.variables import variables as v


class Odl:
    """Connect to ODL and do REST transactions."""
    def __init__(self, base_url):
        """Connect to ODL."""
        self.conn = Connection(base_url, username=v.username,
                      password=v.password)
        self.log = logging.getLogger('ODL')

    def clean_up(self):
        """Clean up ODL configuration."""
        return

    def read_file(self, filename, params={}):
        """Return the file contents after substituting the params if any."""
        f = open(filename, 'r')
        contents = f.read()
        f.close()
        if params:
            contents = Template(contents)
            contents = contents.substitute(params)

        return contents

    def get(self, path):
        """Request REST GET transaction with timeout."""
        return self.timeout(self.request, ('GET', path, {}))

    def post(self, path, body):
        """Request REST POST transaction with timeout."""
        return self.timeout(self.request, ('POST', path, body))

    def put(self, path, body):
        """Request REST PUT transaction with timeout."""
        return self.timeout(self.request, ('PUT', path, body))

    def request(self, operation, path, body):
        """Request REST transactions."""
        self.log.info("%s : %s", operation, path)

        if body != {}:
            self.log.debug("Body : %s", json.dumps(json.loads(body), indent=2))

        headers = {'content-type':'application/json', 'accept':'application/json'}

        if operation == 'GET':
            response = self.conn.request_get(path, args={}, headers=headers)
        elif operation == 'POST':
            response = self.conn.request_post(path, args={}, body=body, headers=headers)
        elif operation == 'PUT':
            response = self.conn.request_put(path, args={}, body=body, headers=headers)

        status = int(response['headers']['status'])

        self.log.info("Response : %s %s", status, httplib.responses[status])

        response_body = json.loads(response['body'])
        self.log.debug("Response Body : %s\n", json.dumps(response_body, indent=2))

        return status, response_body

    def get_pcep_topology(self, params={}):
        """Return the content of PCEP topology response."""
        return self.get("/operational/network-topology:network-topology/topology/pcep-topology")

    def post_add_lsp(self, params={}):
        """Add LSP and return the content of the response."""
        body = self.read_file(v.add_lsp_file, params)
        return self.post("/operations/network-topology-pcep:add-lsp", body)

    def post_remove_lsp(self, params={}):
        """Remove LSP and return the content of the response."""
        body = self.read_file(v.remove_lsp_file, params)
        return self.post("/operations/network-topology-pcep:remove-lsp", body)

    def post_add_label(self, params={}):
        """Add label and return the content of the response."""
        body = self.read_file(v.add_label_file, params)
        return self.post("/operations/network-topology-pcep:add-label", body)

    def post_remove_label(self, params={}):
        """Remove label and return the content of the response."""
        body = self.read_file(v.remove_label_file, params)
        return self.post("/operations/network-topology-pcep:remove-label", body)

    def timeout(self, func, args=(), kwargs={}, timeout_duration=1, default=None):
        """Terminate the function execution when timeout happens."""
        import signal

        class TimeoutError(Exception):
            pass

        def handler(signum, frame):
            raise TimeoutError()

        # set the timeout handler
        signal.signal(signal.SIGALRM, handler)
        signal.alarm(timeout_duration)
        try:
            result = func(*args, **kwargs)
        except TimeoutError:
            self.log.warn("Timeout: %s seconds expired in %s(%s %s)",
                     timeout_duration, func.__name__, args, kwargs)
            result = default
        finally:
            signal.alarm(0)

        return result
