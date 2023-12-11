#!/usr/bin/python
# -*- coding: utf-8 -*-
# Copyright (c) 2019-2021 Qualcomm Technologies, Inc.
# All Rights Reserved.
# Confidential and Proprietary - Qualcomm Technologies, Inc.


"""This class methods that edit aidl files for specific test cases.

IMPORTANT:
 - The sys.path should contain path to
   vendor/qcom/proprietary/commonsys-intf/QIIFA-fwk/ dir to use json
 - This class must contain an implementation of all the tc_method
   variables found within test_cases.json.
 - tc_kwargs should be the only normal parameter when defining a
   method within this class.
 - Methods can have params that contain default values.
 - Any params that are used should be placed within tc_method_kwargs
   within test_cases.json and retrieved within a try catch statement
   from the tc_kwargs dictionary.
 - The abs_file_path key is added to all tc_kwargs dicts within
   aidl_compatibility.py.
 - The abs_file_path key is the file_path variable within test_cases.json

"""

import sys
import os
import re
sys.path.append('../')
from collections import OrderedDict

import json
from utils import ver_lst_to_dict, ver_dict_to_lst


class AIDLTCMethods(object):
    def __init__(self):
        pass

    ########################## helper functions ###############################
    def check_file_alter(self, altered, msg):
        # Raise an exception if method doesn't edit file
        if not altered:
            raise Exception(msg)

    def tc_kwargs_error(self, msg):
        raise Exception(msg)

    def read_json_file(self, abs_file_path):
        with open(abs_file_path, 'r') as f:
            data = json.load(f)
        return data

    def write_json_file(self, abs_file_path, data):
        with open(abs_file_path, 'w') as outfile:
            json.dump(data, outfile, indent=4)

    ############################# main code ###################################

    def do_nothing(self, tc_kwargs):
        """Don't alter any files."""
        pass

    def modify_aidl_stability(self, tc_kwargs):
        """Traverses the json dictionary and modifies a aidl stability to a given hal."""
        try:
            abs_file_path = tc_kwargs['abs_file_path']
            hal_name = tc_kwargs['hal_name']
        except KeyError:
            self.tc_kwargs_error(msg='tc_kwargs missing abs_file_path, intf_name, or new_intf_name.')

        # Read json from abs_file_path if not passes as argument
        data = self.read_json_file(abs_file_path)

        # Modify 'vintf' to 'intf' in stability for the given hal
        for interface in data:
            if interface['name'] == hal_name:
                interface['stability'] = "intf"

        # Write json file to abs_file_path
        self.write_json_file(abs_file_path, data)
        return True

    def delete_aidl_stability(self, tc_kwargs):
        """Traverses the json dictionary and deletes a aidl stability to a given hal."""
        try:
            abs_file_path = tc_kwargs['abs_file_path']
            hal_name = tc_kwargs['hal_name']
        except KeyError:
            self.tc_kwargs_error(msg='tc_kwargs missing abs_file_path, intf_name, or new_intf_name.')

        # Read json from abs_file_path if not passes as argument
        data = self.read_json_file(abs_file_path)

        # Delete stability for the given hal
        for interface in data:
            if interface['name'] == hal_name:
                del interface['stability']

        # Write json file to abs_file_path
        self.write_json_file(abs_file_path, data)
        return True

    def modify_aidl_hash(self, tc_kwargs):
        """Traverses the json dictionary and modifies a aidl hash to a given hal."""
        try:
            abs_file_path = tc_kwargs['abs_file_path']
            hal_name = tc_kwargs['hal_name']
        except KeyError:
            self.tc_kwargs_error(msg='tc_kwargs missing abs_file_path, intf_name, or new_intf_name.')

        # Read json from abs_file_path if not passes as argument
        data = self.read_json_file(abs_file_path)

        # Modify hash for the given hal
        for interface in data:
            if interface['name'] == hal_name:
                interface['hashes'] = ["sdfsadf","fasdf"]

        # Write json file to abs_file_path
        self.write_json_file(abs_file_path, data)
        return True

    def delete_aidl_hash(self, tc_kwargs):
        """Traverses the json dictionary and deletes a aidl hash to a given hal."""
        try:
            abs_file_path = tc_kwargs['abs_file_path']
            hal_name = tc_kwargs['hal_name']
        except KeyError:
            self.tc_kwargs_error(msg='tc_kwargs missing abs_file_path, intf_name, or new_intf_name.')

        # Read json from abs_file_path if not passes as argument
        data = self.read_json_file(abs_file_path)

        # Delete hash for the given hal
        for interface in data:
            if interface['name'] == hal_name:
                del interface['hashes']

        # Write json file to abs_file_path
        self.write_json_file(abs_file_path, data)
        return True

    def modify_aidl_vendor_available(self, tc_kwargs):
        """Traverses the json dictionary and modifies a aidl vendor_available to a given hal."""
        try:
            abs_file_path = tc_kwargs['abs_file_path']
            hal_name = tc_kwargs['hal_name']
        except KeyError:
            self.tc_kwargs_error(msg='tc_kwargs missing abs_file_path, intf_name, or new_intf_name.')

        # Read json from abs_file_path if not passes as argument
        data = self.read_json_file(abs_file_path)

        # Modify boolean value in vendor_available for the given hal
        for interface in data:
            if interface['name'] == hal_name:
                if interface['vendor_available'] =='false':
                    interface['vendor_available'] = "true"
                else:
                    interface['vendor_available'] = "false"

        # Write json file to abs_file_path
        self.write_json_file(abs_file_path, data)
        return True

    def delete_aidl_vendor_available(self, tc_kwargs):
        """Traverses the json dictionary and deletes a aidl vendor_available to a given hal."""
        try:
            abs_file_path = tc_kwargs['abs_file_path']
            hal_name = tc_kwargs['hal_name']
        except KeyError:
            self.tc_kwargs_error(msg='tc_kwargs missing abs_file_path, intf_name, or new_intf_name.')

        # Read json from abs_file_path if not passes as argument
        data = self.read_json_file(abs_file_path)

        # Delete vendor_available for the given hal
        for interface in data:
            if interface['name'] == hal_name:
                del interface['vendor_available']

        # Write json file to abs_file_path
        self.write_json_file(abs_file_path, data)
        return True

    def modify_aidl_types(self, tc_kwargs):
        """Traverses the json dictionary and modifies a aidl types to a given hal."""
        try:
            abs_file_path = tc_kwargs['abs_file_path']
            hal_name = tc_kwargs['hal_name']
        except KeyError:
            self.tc_kwargs_error(msg='tc_kwargs missing abs_file_path, intf_name, or new_intf_name.')

        # Read json from abs_file_path if not passes as argument
        data = self.read_json_file(abs_file_path)

        # Modify value in types for the given hal
        for interface in data:
            if interface['name'] == hal_name:
                interface['types'] = interface['types'][0] + '_updated'

        # Write json file to abs_file_path
        self.write_json_file(abs_file_path, data)
        return True

    def delete_aidl_types(self, tc_kwargs):
        """Traverses the json dictionary and deletes a aidl types to a given hal."""
        try:
            abs_file_path = tc_kwargs['abs_file_path']
            hal_name = tc_kwargs['hal_name']
        except KeyError:
            self.tc_kwargs_error(msg='tc_kwargs missing abs_file_path, intf_name, or new_intf_name.')

        # Read json from abs_file_path if not passes as argument
        data = self.read_json_file(abs_file_path)

        # Delete types for the given hal
        for interface in data:
            if interface['name'] == hal_name:
                del interface['types']

        # Write json file to abs_file_path
        self.write_json_file(abs_file_path, data)
        return True

    def modify_aidl_name(self, tc_kwargs):
        """Traverses the json dictionary and modifies a aidl name to a given hal."""
        try:
            abs_file_path = tc_kwargs['abs_file_path']
            hal_name = tc_kwargs['hal_name']
        except KeyError:
            self.tc_kwargs_error(msg='tc_kwargs missing abs_file_path, intf_name, or new_intf_name.')

        # Read json from abs_file_path if not passes as argument
        data = self.read_json_file(abs_file_path)

        # Modify value in name for the given hal
        for interface in data:
            if interface['name'] == hal_name:
                interface['name'] = interface['name'] + '_updated'

        # Write json file to abs_file_path
        self.write_json_file(abs_file_path, data)
        return True
