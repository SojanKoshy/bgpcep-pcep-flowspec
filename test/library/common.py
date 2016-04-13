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

import sys
import time


global tc_step_counter
tc_step_counter = 0

def tc(func):
    """Decorator for test case functions."""
    assert func.__name__.startswith("test")
    def func_wrapper():
        global tc_step_counter
        tc_step_counter = 0
        sys.stdout.flush()
        print "################################################################################"
        print "##### " + func.__doc__
        print "################################################################################"
        return func()
    return func_wrapper

def tc_step(func):
    """Decorator for test step functions."""
    assert not func.__name__.startswith("test")
    def func_wrapper():
        global tc_step_counter
        tc_step_counter += 1
        sys.stdout.flush()
        print "--------------------------------------------------------------------------------"
        print "# " + str(tc_step_counter) + ". " + func.__doc__
        print "--------------------------------------------------------------------------------"
        return func()
    return func_wrapper

def tc_fixture(func):
    """Decorator for test fixture functions."""
    assert not func.__name__.startswith("test")
    def func_wrapper(module):
        sys.stdout.flush()
        print
        print "--------------------------------------------------------------------------------"
        print "# " + func.__doc__
        print "--------------------------------------------------------------------------------"
        return func(module)
    return func_wrapper

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

def move_attr_to_params(self, param, *attrs):
    """Copies the class attributes to argument and deletes them."""
    for attr in attrs:
        if hasattr(self, attr):
            param[attr] = self.__dict__[attr]
            delattr(self, attr)

def get_matching_index(kvlist, key, values):
    """Returns the index of the matching key value pairs in the given list."""
    for i, kv in enumerate(kvlist):
        if kv.has_key(key):
            if kv[key] == values:
                return i
    return -1

def assert_multi(times=20, wait=3, message=""):
    """Decorator for test check functions with looped check and wait."""
    def check_decorator(func):
        def func_wrapper(self, *args, **kwargs):
            t = 0
            result = False
            while not result and t < times:
                result = func(self, *args, **kwargs)
                if not result:
                    if message:
                        self.log.info(message + " (times = " + str(t) + ")...")
                    t += 1
                    time.sleep(wait)
            return result
        return func_wrapper
    return check_decorator
