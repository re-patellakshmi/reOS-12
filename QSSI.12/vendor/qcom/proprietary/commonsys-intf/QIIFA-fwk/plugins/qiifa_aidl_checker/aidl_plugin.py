
#!/usr/bin/python
# -*- coding: utf-8 -*-
#Copyright (c) 2021 Qualcomm Technologies, Inc.
#All Rights Reserved.
#Confidential and Proprietary - Qualcomm Technologies, Inc.

'''
Import standard python modules
'''
import sys,os,json,shutil

'''
Import local utilities
'''
from qiifa_util.util import UtilFunctions, Variables, Constants, Logger

'''
Import plugin_interface base module
'''
from plugin_interface import plugin_interface
sys.dont_write_bytecode = True

LOG_TAG = "aidl_plugin"
module_info_dict ={}
aidl_metadata_dict = {}
plugin_state_warning = False

def load_info_from_JSON_file(path):
    '''
    Parse the JSON file given in the path into a list of dictionaries.
    '''
    info_dict = [] #The json file would always be a list of dictionaries
    filename = os.path.basename(path)
    try:
        info_file_handle = open(path, "r")
        info_dict = json.load(info_file_handle)
        info_file_handle.close()
    except Exception as e:
        reason = "AIDL file is not present in "+path
        if plugin_state_warning:
            UtilFunctions.print_violations_on_stdout(LOG_TAG,filename,load_info_from_JSON_file.__name__,reason,False)
        else:
            UtilFunctions.print_violations_on_stdout(LOG_TAG,filename,load_info_from_JSON_file.__name__,reason)
            sys.exit(1)
    return info_dict

def aidl_checker_main_create(self,
                             flag,
                             arg_create_type,
                             arg_intf_name=None):
    Logger.logStdout.info("Running qiifa golden db generator... \n")
    if not check_plugin_state():
        Logger.logStdout.warning("AIDL Plugin state doen't match supported values. Exiting now.")
        return
    if plugin_disabled():
        Logger.logStdout.warning("AIDL Plugin is disabled. Exiting now.")
        return
    ''' rarely the case'''
    if not flag == "golden":
        Logger.logStdout.info("Unexpected create flag! \n")
        sys.exit(1)
    json_file_data = load_info_from_JSON_file(Constants.aidl_metadata_file_path)
    if len(json_file_data) == 0:
        reason="Error while parsing txt files"
        if plugin_state_warning:
            UtilFunctions.print_violations_on_stdout(LOG_TAG,"QIIFA golden db generator",aidl_checker_main_create.__name__,reason,False)
        else:
            UtilFunctions.print_violations_on_stdout(LOG_TAG,"QIIFA golden db generator",aidl_checker_main_create.__name__,reason)
            sys.exit(1)
    chk_dup_lst(json_file_data,Constants.aidl_metadata_file_path)
    '''
        Generating the /QIIFA_cmd/aidl if the folder does not exist
    '''
    while not os.path.isdir(Constants.qiifa_aidl_db_root):
        genopdir(Constants.qiifa_aidl_db_root)
    #the case where user wants to generate the full cmd
    #not recommended :)
    if arg_intf_name == None and arg_create_type == Constants.AIDL_SUPPORTED_CREATE_ARGS[0]:
        Logger.logStdout.info("Running full aidl cmd generation.. \n")
        Logger.logStdout.warning("Not a recommended operation..\n")
        json_create_for_create(Constants.qiifa_aidl_db_path, json_file_data, "all")
    #at this point it must be a single intf cmd generation
    else:
        Logger.logStdout.info("Running aidl cmd generation with option: " + arg_create_type +", intf name: " + arg_intf_name+"\n")
        json_create_for_create(Constants.qiifa_aidl_db_path, json_file_data, arg_create_type, arg_intf_name)
    Logger.logInternal.info ("Success")

def json_create_for_create(json_db_file_path, metadatadict_lst, arg_type, for_intf_name=None):
    dbdict_lst = load_info_from_JSON_file(json_db_file_path)
    if arg_type == 'all':
        if len(dbdict_lst) == 0:
            Logger.logStdout.info("Running aidl create command with option all for the 1st time \n")
        else:
            Logger.logStdout.info("Running aidl create command with option all \n")
        existintfcount=0
        modintfcount=0
        newintfcount=0
        goldencmdlst=[]
        modintflst=[]
        for meta_dict in metadatadict_lst:
            if meta_dict in dbdict_lst:
                existintfcount += 1
            else:
                newintfcount+=1
                for dbdict in dbdict_lst:
                    if meta_dict[u'name'] == dbdict[u'name']:
                        modintflst.append(str(dbdict[u'name']))
                        modintfcount += 1
                        newintfcount -= 1
                if meta_dict[u'name'] not in modintflst:
                    goldencmdlst.append(str(meta_dict[u'name']))
        with open(json_db_file_path,'w') as jf:
            json.dump(metadatadict_lst, jf, separators=(",", ": "), indent=4,sort_keys=True)

        Logger.logStdout.info("Completed running all option for create aidl command. \n")
        Logger.logStdout.info(str(existintfcount)+" existing interfaces are being re-added \n")
        Logger.logStdout.info(str(modintfcount)+" interfaced where modified. Below is a list of Interfaces which have been modified \n")
        if modintfcount > 0:
            Logger.logStdout.info(modintflst)
        Logger.logStdout.info(str(newintfcount)+" interfaced where added. Below is a list of Interfaces which have been added \n")
        if newintfcount > 0:
            Logger.logStdout.info(goldencmdlst)
    else:
        if for_intf_name==None:
            Logger.logStdout.error("Interface name option needs to be provided \n")
            Logger.logStdout.info("python qiifa_main.py -h \n")
            sys.exit(1)
        if len(dbdict_lst) == 0:
            reason="Please run python qiifa_main.py --create aidl before running this option "+ str(arg_type)
            if plugin_state_warning:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,"QIIFA golden db generator for "+str(arg_type),json_create_for_create.__name__,reason,False)
            else:   
                UtilFunctions.print_violations_on_stdout(LOG_TAG,"QIIFA golden db generator for "+str(arg_type),json_create_for_create.__name__,reason)
                sys.exit(1)
        else:
            Logger.logStdout.info("Running aidl create command with option "+ str(arg_type)+" and for interface "+str(for_intf_name)+"\n")
        chk_dup_lst(dbdict_lst,json_db_file_path)
        if arg_type == Constants.AIDL_SUPPORTED_CREATE_ARGS[1]:
            intffound,founddict = check_intf_avail_metadict(metadatadict_lst,for_intf_name)
            if not intffound:
                reason="Interface name is not found in the aidl file path present in "+os.path.basename(Constants.aidl_metadata_file_path)
                if plugin_state_warning:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,for_intf_name,json_create_for_create.__name__,reason,False)
                else:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,for_intf_name,json_create_for_create.__name__,reason)
                    sys.exit(1)
            count=0
            for dbdict in dbdict_lst:
                if str(dbdict[u'name']) == for_intf_name and founddict != dbdict:
                    intffound=True
                    dbdict_lst[count]=founddict
                count+=1
            if not intffound:
                reason="Interface name is not found"
                if plugin_state_warning:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,for_intf_name,json_create_for_create.__name__,reason,False)
                else:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,for_intf_name,json_create_for_create.__name__,reason)
                    sys.exit(1)
            with open(json_db_file_path,'w') as jf:
                json.dump(dbdict_lst, jf, separators=(",", ": "), indent=4,sort_keys=True)
            Logger.logStdout.info("interface "+str(for_intf_name)+" has been modified. \n")
        elif arg_type == Constants.AIDL_SUPPORTED_CREATE_ARGS[2]:
            intffound,founddict = check_intf_avail_metadict(metadatadict_lst,for_intf_name)
            if not intffound:
                reason="Interface name is not found in the aidl file path present in "+os.path.basename(Constants.aidl_metadata_file_path)
                if plugin_state_warning:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,for_intf_name,json_create_for_create.__name__,reason,False)
                else:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,for_intf_name,json_create_for_create.__name__,reason)
                    sys.exit(1)
            intffound=False
            for dbdict in dbdict_lst:
                if str(dbdict[u'name']) == for_intf_name:
                    intffound=True
                    break
            if intffound:
                reason="Interface name is found"
                if plugin_state_warning:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,for_intf_name,json_create_for_create.__name__,reason,False)
                else:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,for_intf_name,json_create_for_create.__name__,reason)
                    sys.exit(1)
            dbdict_lst.append(founddict)
            with open(json_db_file_path,'w') as jf:
                json.dump(dbdict_lst, jf, separators=(",", ": "), indent=4,sort_keys=True)
            Logger.logStdout.info("interface "+str(for_intf_name)+" has been added.")

def chk_dup_lst(dictlst,path):
    '''
    Find if the list of dictionaries have any duplicates
    and stop running if any
    '''
    if len(dictlst) >0:
        compdictlst=[]
        for dict in dictlst:
            if dict not in compdictlst:
                found_dupl=False
                if len(compdictlst) > 0:
                    for comdict in compdictlst:
                        if comdict[u'name'] == dict[u'name']:
                            found_dupl=True
                if not found_dupl:
                    compdictlst.append(dict)
            else:
                reason="Duplicate interface have been found in the file present in this path "+path+". Please remove duplicates before proceeding"
                if plugin_state_warning:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,dict[u'name'],chk_dup_lst.__name__,reason,False)
                else:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,dict[u'name'],chk_dup_lst.__name__,reason)
                    sys.exit(1)

def check_intf_avail_metadict(metadatadict_lst, intf_name):
    intffound=False
    founddict={}
    for metadata_dict in metadatadict_lst:
        if str(metadata_dict[u'name']) == intf_name:
            intffound=True
            founddict = metadata_dict
    return intffound,founddict

def genopdir(OPDirPath):
    '''
    Generate the path by creating directories till the end of the path is reached.
    '''
    if not (os.path.isdir(OPDirPath)):
        os.mkdir(OPDirPath)

def enforce_package_naming_convention(aidl_interface_list):
    '''
    Requirement is to enforce pacakage naming convention
    for non-AOSP HAL's.
    '''
    voilation_list = []
    for aidl_interface in aidl_interface_list:
        aidl_interface_name = aidl_interface["name"]
        aidl_interface_type = aidl_interface["types"][0]
        aidl_voilation = True
        for package in Constants.qiifa_package_convention_list:
            if aidl_interface_name.startswith(package) and aidl_interface_type.startswith(package):
                aidl_voilation = False
                break
        if aidl_voilation:
            voilation_list.append(aidl_interface)
    if  voilation_list:
        for interface in voilation_list:
            reason = "Interface should follow package naming convention. Prefixed with :" + str(Constants.qiifa_package_convention_list)
            if plugin_state_warning:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,interface["name"],enforce_package_naming_convention.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,interface["name"],enforce_package_naming_convention.__name__,reason)

def project_path_exists_in_known_list(project_list,project):
    project_found_in_list = False
    for prj in project_list:
        if(project.startswith(prj)):
            project_found_in_list = True
    return project_found_in_list

def idenitify_vendor_defined_aidl_interfaces():
    '''
    Write a logic to identify non-AOSP HAL's.
    Basis of this logic will be :

        1. Package/folder naming convention used by AOSP
        2. Generic folder structure used by AOSP
        3. One time Known interfaces list
    '''
    filtered_aidl_list = []
    ## Iterate through aidl_metadata json file
    ## Filter #1
    for aidl_metadata in aidl_metadata_dict:
        aidl_types = aidl_metadata["types"][0]
        if not aidl_types.startswith("vendor.qti"):
            ## Let;s do a reverse check for vendor folder path before we skip these
            ## We don;t expect these interfaces to be present in vendor paths.
            violation_folder_path_list = ["vendor"]
            project_path = module_info_dict[aidl_metadata["name"]]["path"][0]
            folder_violation = project_path_exists_in_known_list(violation_folder_path_list,project_path)
            if not folder_violation:
                continue
            else:
                #TODO Need to discuss Action if such instance is found
                pass
        else:
            filtered_aidl_list.append(aidl_metadata)

    temp_filter_list=[]
    ## Filter #2
    for aidl_metadata in filtered_aidl_list:
        if aidl_metadata["name"] in Constants.aidl_skipped_intf_lst:
            Logger.logInternal.info("AIDL interface "+aidl_metadata["name"]+" is part of the Skipped list")
            continue
        project_path = module_info_dict[aidl_metadata["name"]]["path"][0]
        found_in_aosp_list = project_path_exists_in_known_list(Constants.qiifa_aidl_known_aosp_project_list, project_path)
        if not found_in_aosp_list:
            temp_filter_list.append(aidl_metadata)

    ## Return list of Interfaces which are considered as non-AOSP
    return temp_filter_list

def enforce_vintf_tag(aidl_interfaces):
    ## If vendor_available is set to true then make sure that stability tag is set to vintf
    voilation_list = []
    for aidl_interface in aidl_interfaces:
        if aidl_interface["vendor_available"] == "true":
            stability_tag = aidl_interface["stability"]
            if stability_tag == "vintf":
                continue
            else:
                voilation_list.append(aidl_interface)
    if  voilation_list:
        for interface in voilation_list:
            reason = "Stability : Vintf is mandatory for all vendor_available : true Aidl Interfaces"
            if plugin_state_warning:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,interface["name"],enforce_vintf_tag.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,interface["name"],enforce_vintf_tag.__name__,reason)

def enforce_repository_path(aidl_interfaces):
    ## Aim is to restrict all aidl_interfaces to couple of projects so that
    ## expert reviewers can ensure proper backward compatibility for these
    ## interfaces.
    voilation_list = [];
    for aidl_interface in aidl_interfaces:
        project_path = module_info_dict[aidl_interface["name"]]["path"][0]
        project_violation = True
        for allowed_path in Constants.qiifa_aidl_allowed_path:
            if(project_path.startswith(allowed_path)):
                project_violation = False
                break
        if project_violation:
            voilation_list.append(aidl_interface)
    if  voilation_list:
        for interface in voilation_list:
            reason = "All AIDL Interfaces should be present in folder paths :" + str(Constants.qiifa_aidl_allowed_path)
            if plugin_state_warning:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,interface["name"],enforce_repository_path.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,interface["name"],enforce_repository_path.__name__,reason)

def initialize_aidl_plugin_globals():
    '''
    This function is meant to initialize global metadata
    which will be used in enforcement.
    Primarily planning to load information from :
	1. aidl_metadata.json (Contains the list of HAL;s)
        2. module_info.json (Module <-> Project mapping)
    '''
    global aidl_metadata_dict
    global module_info_dict
    aidl_metadata_dict = load_info_from_JSON_file(Constants.aidl_metadata_file_path)
    module_info_dict   = load_info_from_JSON_file(Constants.module_info_file_path)

def plugin_disabled():
    if (Constants.aidl_plugin_state == "disabled"):
        return True

def check_plugin_state():
     global plugin_state_warning
     supported_values = ["disabled","enforced","warning"]
     supported = False
     if(Constants.aidl_plugin_state=="warning"):
         plugin_state_warning = True
     for value in supported_values:
        if(Constants.aidl_plugin_state == value):
             supported = True
             break
     return supported

def func_start_qiifa_aidl_checker(self,
                                  flag,
                                  qssi_path=None,
                                  target_path=None,
                                  q_file_name=None,
                                  t_file_name=None):
    '''
    Enforcements for AIDL:
       1. All package name should start from vendor.qti prefix
       2. If AIDL is defined as vendor_available then stability tag
          should be set to vintf.
    '''
    ### We intend to check AIDL interfaces which are part of commonsys-intf
    ### for qssi and vendor compatibility. All commonsys-intf projects are
    ### part of qssi SI, therefore add a check to run aidl checker while
    ### building QSSI lunch.
    if qssi_path != None and target_path != None:
        if not check_plugin_state():
            Logger.logStdout.warning("AIDL Plugin state doen't match supported values. Exiting now.")
            return
        if plugin_disabled():
            Logger.logStdout.warning("AIDL Plugin is disabled. Exiting now.")
            return
        aidl_iic_checker(flag,qssi_path,target_path,q_file_name,t_file_name)
    else:
        if not check_plugin_state():
            Logger.logStdout.warning("AIDL Plugin state doen't match supported values. Exiting now.")
            return
        if plugin_disabled():
            Logger.logStdout.error("AIDL Plugin is disabled. Exiting now.")
            return
        if Constants.qiifa_out_path_target_value == "qssi":
            initialize_aidl_plugin_globals()
            vendor_defined_interfaces = idenitify_vendor_defined_aidl_interfaces()
            enforce_package_naming_convention(vendor_defined_interfaces)
            enforce_vintf_tag(vendor_defined_interfaces)
            enforce_repository_path(vendor_defined_interfaces)
            aidl_iic_checker(flag,qssi_path,target_path,q_file_name,t_file_name)
        copy_json_to_out()

def aidl_iic_checker(flag,
                    qssi_path=None,
                    target_path=None,
                    q_file_name=None,
                    t_file_name=None):
    if flag == "check":
        if qssi_path != None and target_path != None:
            Logger.logStdout.info("Running image to image compatibility check.....")
            run_img_to_img_checker(qssi_path, target_path, q_file_name, t_file_name)
        else:
            if (UtilFunctions.pathExists(Constants.aidl_metadata_file_path) == False):
                reason="JSON file is not found at"+Constants.aidl_metadata_file_path
                if plugin_state_warning:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,"aidl_iic_checker",json_create_for_create.__name__,reason,False)
                else:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,"aidl_iic_checker",json_create_for_create.__name__,reason)
                    sys.exit(1)
            goldencmddict_lst = load_info_from_JSON_file(Constants.qiifa_aidl_db_path)
            if len(goldencmddict_lst)<=0:
                reason="JSON File has not been generated yet. Please run --create option before running IIC checker"
                if plugin_state_warning:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,"aidl_iic_checker",json_create_for_create.__name__,reason,False)
                    return
                else:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,"aidl_iic_checker",json_create_for_create.__name__,reason)
                    sys.exit(1)
            chk_dup_lst(goldencmddict_lst,Constants.qiifa_aidl_db_path)
            if Constants.qiifa_out_path_target_value == "qssi":
                Logger.logStdout.info("Running QSSI only checker.....")
                run_indi_checker(aidl_metadata_dict,goldencmddict_lst)
            else:
                Logger.logStdout.info("Running Target only checker.....")
                run_indi_checker(aidl_metadata_dict,goldencmddict_lst)
    else:
        Logger.logStdout.error("Unexpected aidl checker flag!")
        sys.exit(1)

def run_img_to_img_checker(qssi_path,
                           target_path,
                           q_file_name,
                           t_file_name):
    '''
    Description: This defination runs image to image checker
    Type       : Internal defination
    '''
    qssi_json_cmd_path =''
    target_json_cmd_path = ''
    qssi_json_cmd_path=(os.path.splitext(os.path.join(qssi_path,q_file_name))[0])
    target_json_cmd_path=(os.path.splitext(os.path.join(target_path,t_file_name))[0])
    qssi_lst=[]
    target_lst=[]

    if UtilFunctions.dirExists(qssi_json_cmd_path) and UtilFunctions.dirExists(target_json_cmd_path):
        qssi_lst = img_load_lst_from_path(qssi_json_cmd_path)
        target_lst = img_load_lst_from_path(target_json_cmd_path)

        if len(qssi_lst)<= 0 or len(target_lst)<=0:
            reason="Either target-CMD or QSSI-CMD is empty"
            if plugin_state_warning:
                Logger.logStdout.error(reason)
                UtilFunctions.print_violations_on_stdout(LOG_TAG,"QIIFA CMD Loading for QSSI / Target",run_img_to_img_checker.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,"QIIFA CMD Loading for QSSI / Target",run_img_to_img_checker.__name__,reason)
                sys.exit(1)
        elif len(qssi_lst) != len(target_lst):
            target_not_found_lst=intf_not_found_lst(qssi_lst,target_lst)
            qssi_not_found_lst=intf_not_found_lst(target_lst,qssi_lst)
            Logger.logStdout.error("These number of QSSI QIIFA-CMD interfaces ("+str(len(target_not_found_lst))+") are not present in the Target QIIFA-CMD. The list is below")
            if len(target_not_found_lst)>0:
                Logger.logStdout.error(target_not_found_lst)
            Logger.logStdout.error("These number of Target QIIFA-CMD interfaces ("+str(len(qssi_not_found_lst))+") are not present in the QSSI QIIFA-CMD. The list is below")
            if len(qssi_not_found_lst)>0:
                Logger.logStdout.error(qssi_not_found_lst)
            reason = "Target-CMD and QSSI-CMD are not identical"
            if plugin_state_warning:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,"QIIFA CMD Loading for QSSI / Target",run_img_to_img_checker.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,"QIIFA CMD Loading for QSSI / Target",run_img_to_img_checker.__name__,reason)
                sys.exit(1)
        else:
            for qssi_dict in qssi_lst:
                for target_dict in target_lst:
                    if target_dict[u'name'] == qssi_dict[u'name']:
                        blk_by_blk_chk(target_dict,qssi_dict)
    else:
        Logger.logStdout.error(qssi_json_cmd_path + " or " + target_json_cmd_path + " doesn't exist.")
        sys.exit(1)
    pass

def intf_not_found_lst(main_lst,compare_lst):
    '''Compare the list of dictionaries and if the
        name present in main list is not present in
        compare list. we collect the name of interfaces not
        found and return it as a list  '''
    found_name=False
    comapre_lst_not_found=[]
    for main_dict in main_lst:
        found_name=False
        for compare_dict in compare_lst:
            if compare_dict[u'name'] == main_dict[u'name']:
                found_name=True
        if not found_name:
            comapre_lst_not_found.append(str(main_dict[u'name']))
    return comapre_lst_not_found

def img_load_lst_from_path(path):
    import re
    dict_lst=[]
    try:
        for root, dr, files in os.walk(path):
            for file in files:
                aidl_filename_match = re.search("aidl",file)
                if aidl_filename_match != None:
                    tmp_filepath = os.path.join(path,file)
                    dict_lst=load_info_from_JSON_file(tmp_filepath)
    except Exception as ex:
        Logger.logStdout.error(ex)
        Logger.logStdout.error("No File was found")
    return dict_lst

def run_indi_checker(metadata_dict, goldencmddict_lst):
    """
    This check runs on the Vendor and the Target Individually
    """
    for meta_dict in metadata_dict:
        if meta_dict not in goldencmddict_lst:
            name_present_check = False
            stability_flag_check = False
            type_flag_check = False
            hash_check = False
            vendavail_check = False
            if meta_dict[u'name'] in Constants.aidl_skipped_intf_lst:
                continue
            try:
                intf_skip_chk = skipintf_chk(meta_dict[u'name'])
            except Exception as e:
                Logger.logStdout.warning("AIDL Skip checked needs to be validated for this intf "+meta_dict[u'name'])
                Logger.logStdout.warning(e)
                intf_skip_chk = True
            if intf_skip_chk:
                Logger.logInternal.info("ABI PRESERVED INTF " + meta_dict[u'name'])
                continue
            goldencmd_dictrlst = check_dup_intf_name(goldencmddict_lst,meta_dict[u'name'],True)
            if goldencmd_dictrlst == None:
                reason="AIDL interface doesn't exist in the coresponding QIIFA cmd. please add it to the cmd"
                if plugin_state_warning:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],run_indi_checker.__name__,reason,False)
                else:
                    UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],run_indi_checker.__name__,reason)
                continue
            #at this point, the meta entry must exist in the coresponding
            #cmd which means we can check for its validity and we know exactly
            #to which cmd entry it coresponds from the return of check_dup_intf_name
            elif not isinstance(goldencmd_dictrlst, list):
                #no duplicate for this intf. most of the cases will fall through
                #this case
                if is_intf_modified(meta_dict,goldencmd_dictrlst):
                    blk_by_blk_chk(meta_dict,goldencmd_dictrlst)
            else:
                #duplicates found, we will have to go one by one and
                #find a coresponding match
                if meta_dict not in goldencmd_dictrlst:
                    found_match = False
                    matching_hal = None
                    for golddict in goldencmd_dictrlst:
                        if meta_dict[u'name'] == golddict[u'name']:
                            found_match=True
                            matching_hal = meta_dict
                            break
                    if found_match:
                        if is_intf_modified(meta_dict,goldencmd_dictrlst):
                            blk_by_blk_chk(meta_dict,goldencmd_dictrlst)
                    else:
                        reason="AIDL interface's Mismatch error"
                        if plugin_state_warning:
                            UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],run_indi_checker.__name__,reason,False)
                        else:
                            UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],run_indi_checker.__name__,reason)

def blk_by_blk_chk(meta_dict,goldencmd_dictrlst):
    try:
        if meta_dict[u'stability'] != goldencmd_dictrlst[u'stability']:
            reason="AIDL interface's stability check has been modified. Please run --create option before running IIC."
            if plugin_state_warning or Constants.AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return
    except:
        stabilityerror = False
        if "stability" in meta_dict.keys() and "stability" not in goldencmd_dictrlst.keys():
            stabilityerror = True
            reason="AIDL interface's stability check is present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
        elif "stability" not in meta_dict.keys() and "stability" in goldencmd_dictrlst.keys() :
            reason="AIDL interface's stability check is not present in META data but present in QIIFA CMD. Please run --create option before running IIC."
            stabilityerror = True
        else:
            reason="AIDL interface's stability check is not present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
            stabilityerror = True

        if stabilityerror:
            if plugin_state_warning or Constants.AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return
    try:
        if meta_dict[u'types'] != goldencmd_dictrlst[u'types']:
            diffinmeta=[]
            if len(meta_dict[u'types']) > 0 or len(goldencmd_dictrlst[u'types']) > 0:
                metatypeset = set(meta_dict[u'types'])
                goldencmdtypeset = set(goldencmd_dictrlst[u'types'])
                samesets = metatypeset & goldencmdtypeset
                diffinmeta = list(samesets ^ metatypeset)
            Logger.logStdout.error("These are the Types which are different")
            Logger.logStdout.error(diffinmeta)
            reason="AIDL interface's Types has been modified.  please run --create option before running IIC."
            if plugin_state_warning or Constants.AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return
    except:
        typeserror = False
        if "types" in meta_dict.keys() and "types" not in goldencmd_dictrlst.keys():
            typeserror = True
            reason="AIDL interface's types check is present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
        elif "types" not in meta_dict.keys() and "types" in goldencmd_dictrlst.keys() :
            if len(goldencmd_dictrlst[u'types']) > 1:
                reason="AIDL interface's types check is not present in META data but present in QIIFA CMD. Please run --create option before running IIC."
                typeserror = True
        else:
            reason="AIDL interface's types check is not present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
            typeserror = True

        if typeserror:
            if plugin_state_warning or Constants.AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return
    try:
        hashnotfound = False
        if meta_dict[u'hashes'] != goldencmd_dictrlst[u'hashes']:
            diffinmeta=[]
            diffingoldcmd=[]
            if (len(goldencmd_dictrlst[u'hashes']) - len(meta_dict[u'hashes'])) >= 0 and (len(goldencmd_dictrlst[u'hashes']) - len(meta_dict[u'hashes'])) <= 1:
                # If the HASH Mismatch length is 1 and the HASH is present in QIIFA CMD and not present in Meta data
                if (len(goldencmd_dictrlst[u'hashes']) - len(meta_dict[u'hashes'])) == 1:
                    if (len(meta_dict[u'hashes']) > 0):
                        for meta_hashes in meta_dict[u'hashes']:
                            if meta_hashes not in goldencmd_dictrlst[u'hashes']:
                                hashnotfound = True
                elif len(meta_dict[u'hashes']) > 0 or len(goldencmd_dictrlst[u'hashes']) > 0:
                    metahashset = set(meta_dict[u'hashes'])
                    goldencmdhashset = set(goldencmd_dictrlst[u'hashes'])
                    samesets = metahashset & goldencmdhashset
                    diffinmeta = list(samesets ^ metahashset)
                    diffingoldcmd = list(samesets ^ goldencmdhashset)
                    if len(diffinmeta) > 0:
                        hashnotfound = True
            else:
                # If the HASH Mismatch length is not equal to 0 or 1 then there is a definite hash mismatch.
                metahashset = set(meta_dict[u'hashes'])
                goldencmdhashset = set(goldencmd_dictrlst[u'hashes'])
                samesets = metahashset & goldencmdhashset
                diffinmeta = list(samesets ^ metahashset)
                diffingoldcmd = list(samesets ^ goldencmdhashset)
                hashnotfound = True
        if hashnotfound:
            if (len(diffinmeta) > 0):
                Logger.logStdout.error("These are the hashses which are different on the META Side")
                Logger.logStdout.error(diffinmeta)
            if (len(diffingoldcmd) > 0):
                Logger.logStdout.error("These are the hashses which are different on the QIIFA CMD Side")
                Logger.logStdout.error(diffingoldcmd)
            reason="AIDL interface's hashes has been modified. Please run --create option before running IIC."
            if plugin_state_warning or Constants.AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return
    except Exception as ex:
        hashmetaerror = False
        if "hashes" in meta_dict.keys() and "hashes" not in goldencmd_dictrlst.keys():
            hashmetaerror = True
            reason="AIDL interface's hashes is present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
        elif "hashes" not in meta_dict.keys() and "hashes" in goldencmd_dictrlst.keys() :
            if len(goldencmd_dictrlst[u'hashes']) > 1:
                reason="AIDL interface's hashes is not present in META data but present in QIIFA CMD. Please run --create option before running IIC."
                hashmetaerror = True
        else:
            reason="AIDL interface's hashes is not present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
            hashmetaerror = True

        if hashmetaerror:
            if plugin_state_warning or Constants.AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return
    try:
        if meta_dict[u'vendor_available'] != goldencmd_dictrlst[u'vendor_available']:
            reason="AIDL interface's vendor available flag has been modified. Please run --create option before running IIC."
            if plugin_state_warning or Constants.AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                Logger.logInternal.info("ABI VENDOR AVAILABLE TAG Has been modified FOR " + meta_dict[u'name'])
                #UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return
    except Exception as ex:
        vendor_availableerror = False
        if "vendor_available" in meta_dict.keys() and "vendor_available" not in goldencmd_dictrlst.keys():
            vendor_availableerror = True
            reason="AIDL interface's Vendor Available check is present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
        elif "vendor_available" not in meta_dict.keys() and "vendor_available" in goldencmd_dictrlst.keys() :
            reason="AIDL interface's Vendor Available check is not present in META data but present in QIIFA CMD. Please run --create option before running IIC."
            vendor_availableerror = True
        else:
            reason="AIDL interface's Vendor Available check is not present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
            vendor_availableerror = True

        if vendor_availableerror:
            if plugin_state_warning or AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                Logger.logInternal.info("ABI VENDOR AVAILABLE TAG is not available FOR " + meta_dict[u'name'])
                # UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return
    try:
        if meta_dict[u'has_development'] != goldencmd_dictrlst[u'has_development']:
            reason="AIDL interface's vendor available flag has been modified. Please run --create option before running IIC."
            if plugin_state_warning or Constants.AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return
    except Exception as ex:
        has_developmenterror = False
        if "has_development" in meta_dict.keys() and "has_development" not in goldencmd_dictrlst.keys():
            has_developmenterror = True
            reason="AIDL interface's Vendor Available check is present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
        elif "has_development" not in meta_dict.keys() and "has_development" in goldencmd_dictrlst.keys() :
            reason="AIDL interface's Vendor Available check is not present in META data but present in QIIFA CMD. Please run --create option before running IIC."
            has_developmenterror = True
        else:
            reason="AIDL interface's Has developement check is not present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
            has_developmenterror = True

        if has_developmenterror:
            if plugin_state_warning or AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                Logger.logInternal.info("ABI VERSION TAG NOT PRESENT FOR " + meta_dict[u'name'])
#                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return
    try:
        if meta_dict[u'versions'] != goldencmd_dictrlst[u'versions']:
            diffinmeta=[]
            diffingoldcmd=[]
            versionnotfound = False
            if (len(goldencmd_dictrlst[u'versions']) - len(meta_dict[u'versions'])) >= 0 and (len(goldencmd_dictrlst[u'versions']) - len(meta_dict[u'versions'])) <= 1:
                # If the Version Mismatch length is 1 and the version is present in QIIFA CMD and not present in Meta data
                if (len(goldencmd_dictrlst[u'versions']) - len(meta_dict[u'versions'])) == 1:
                    if (len(meta_dict[u'versions']) > 0):
                        for meta_versions in meta_dict[u'versions']:
                            if meta_versions not in goldencmd_dictrlst[u'versions']:
                                versionnotfound = True
                elif len(meta_dict[u'versions']) > 0 or len(goldencmd_dictrlst[u'versions']) > 0:
                    metahashset = set(meta_dict[u'versions'])
                    goldencmdhashset = set(goldencmd_dictrlst[u'versions'])
                    samesets = metahashset & goldencmdhashset
                    diffinmeta = list(samesets ^ metahashset)
                    diffingoldcmd = list(samesets ^ goldencmdhashset)
                    if len(diffinmeta) > 0:
                        versionnotfound = True
            else:
                # If the HASH Mismatch length is not equal to 0 or 1 then there is a definite hash mismatch.
                metahashset = set(meta_dict[u'versions'])
                goldencmdhashset = set(goldencmd_dictrlst[u'versions'])
                samesets = metahashset & goldencmdhashset
                diffinmeta = list(samesets ^ metahashset)
                diffingoldcmd = list(samesets ^ goldencmdhashset)
                versionnotfound = True
        if versionnotfound:
            if (len(diffinmeta) > 0):
                Logger.logStdout.error("These are the hashses which are different on the META Side")
                Logger.logStdout.error(diffinmeta)
            if (len(diffingoldcmd) > 0):
                Logger.logStdout.error("These are the hashses which are different on the QIIFA CMD Side")
                Logger.logStdout.error(diffingoldcmd)
            reason="AIDL interface's versions has been modified. Please run --create option before running IIC."
            if plugin_state_warning or Constants.AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return
    except Exception as ex:
        versionerror = False
        if "versions" in meta_dict.keys() and "versions" not in goldencmd_dictrlst.keys():
            versionerror = True
            reason="AIDL interface's versions is present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
        elif "versions" not in meta_dict.keys() and "versions" in goldencmd_dictrlst.keys() :
            if len(goldencmd_dictrlst[u'versions']) > 1:
                reason="AIDL interface's versions is not present in META data but present in QIIFA CMD. Please run --create option before running IIC."
                versionerror = True
        else:
            reason="AIDL interface's versions is not present in META data but not present in QIIFA CMD. Please run --create option before running IIC."
            versionerror = True

        if versionerror:
            if plugin_state_warning or AIDL_QSSI13_ADDITIONS_DSBLE_CHK:
                Logger.logInternal.info("ABI VERSION TAG NOT PRESENT FOR " + meta_dict[u'name'])
                #UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason,False)
            else:
                UtilFunctions.print_violations_on_stdout(LOG_TAG,meta_dict[u'name'],blk_by_blk_chk.__name__,reason)
            return

def is_intf_modified(meta_dict, goldencmd_dict):
    '''
    Compare the meta dictionary and the golden golden cmd dictionary
    and return true if something has changed
    '''
    if goldencmd_dict != meta_dict:
        return True
    return False

def check_dup_intf_name(goldenlist, intf_name, find_dup):
    dup_intf_lst=[]
    for goldendict in goldenlist:
        if goldendict[u'name'] == intf_name:
            if find_dup:
                dup_intf_lst.append(goldendict)
            else:
                return goldendict
    '''
    Check if Interface is present
    '''
    if len(dup_intf_lst) == 0:
        return None
    elif len(dup_intf_lst) == 1:
        return dup_intf_lst[0]
    return dup_intf_lst

def copy_json_to_out():
    for f in os.listdir(Constants.qiifa_aidl_db_root):
        if f.endswith(".json"):
            src = Constants.qiifa_aidl_db_root + f
            dest = Constants.qiifa_current_cmd_dir + "/" + f
            Logger.logStdout.info("src : "+src)
            Logger.logStdout.info("dest: "+dest)
            src_lst= load_info_from_JSON_file(src)
            try:
                if UtilFunctions.pathExists(dest):
                    os.remove(dest)
                with open(dest,"w") as json_file:                    
                    json.dump(src_lst, json_file,separators=(",", ": "), indent=4,sort_keys=True)
            except Exception as ex:
                Logger.logStdout.error(ex)
                Logger.logStdout.error("Couldn't copy json to out.")

def skipintf_chk(intf_name):
    string_concatenate = ''
    for words in intf_name.split('.'):
        string_concatenate += words
        if string_concatenate in Constants.AIDL_ALLWED_INTF_PREF:
            return False
        string_concatenate += '.'
    return True

class qiifa_aidl:
    def __init__(self):
         pass
    start_qiifa_aidl_checker = func_start_qiifa_aidl_checker
'''
plugin class implementation
plugin class is derived from plugin_interface class
Name of plugin class MUST be MyPlugin
'''
class MyPlugin(plugin_interface):
    def __init__(self):
        pass

    def register(self):
        return Constants.AIDL_SUPPORTED_CREATE_ARGS

    def config(self, QIIFA_type=None, libsPath=None, CMDPath=None):
        pass

    def generateGoldenCMD(self, libsPath=None, storagePath=None, create_args_lst=None):
        '''
        the assumption here is that if create_args_lst is empty, then this was called under the
        circumstance where --create arg was called with "all" option; so it should behave as if
        --create was called with "aidl" option.
        '''
        if create_args_lst is None:
            aidl_checker_main_create(self, "golden", Constants.AIDL_SUPPORTED_CREATE_ARGS[0])
        #Same behavior as above. Ignore everything in create_args_lst but the first one.
        elif create_args_lst[0] == Constants.AIDL_SUPPORTED_CREATE_ARGS[0]:
            aidl_checker_main_create(self, "golden", Constants.AIDL_SUPPORTED_CREATE_ARGS[0])
        #In this case create_args_lst[1] will have the particular intf name
        elif create_args_lst[0] in Constants.AIDL_SUPPORTED_CREATE_ARGS and len(create_args_lst)==2:
            aidl_checker_main_create(self, "golden", create_args_lst[0], create_args_lst[1])
        else:
            Logger.logStdout.info("Invalid --create argument options")
            Logger.logStdout.info("python qiifa_main.py -h")
            sys.exit()

    def IIC(self, **kwargs):
        if kwargs:
            kwargs.update({"flag":"check"})
            func_start_qiifa_aidl_checker(self,**kwargs)
        else:
            func_start_qiifa_aidl_checker(self,"check")
