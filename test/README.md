Copyright (c) 2016 Huawei Technologies Co., Ltd. All rights reserved.

This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html

DIRECTORY ORGANIZATION
======================
test/library: PCECC test related ODL, Router and other  libraries

test/variables: PCECC related variables for test

HOW RUN TEST
============
In order to run the test it's required to have Python 2.7+ and pytest, python-rest-client libraries.

To auto install the dependent libraties and run test:

1) Run ODL and routers with basic config for PCE test
2) Run "python setup.py pytest" in test folder for installing dependencies and to execute test cases
3) Run "py.test" in test folder to execute test cases

Steps:

    # After start ODL and routers with basic config for PCE test

    cd bgpcep-pcecc/test
    python setup.py pytest
    py.test