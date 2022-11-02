package com.bilibili.service;

import com.bilibili.common.utils.ReturnData;

public interface LogService {
//    default ReturnData createUser(String name){return null;}
    ReturnData createUser(String name, String identitytype, String identifier, String credential);
}
