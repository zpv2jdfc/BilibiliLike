package com.bilibili.common.manager;

import java.util.Date;

public interface TokenManager {
    /**
     * 创建一个token关联上指定用户
     * @param userId 指定用户的id
     * @return 生成的token
     */
    public String createToken(long userId);

    /**
     * 检查token是否有效
     * @param token
     * @return 是否有效
     */
    public boolean checkToken(String token);

    /**
     * 从字符串中解析token
     * @param token
     * @return
     */
    public long getId(String token);

    /**
     * 清除token
     * @param userId 登录用户的id
     */
    public void deleteToken(long userId);
    /**
     * 获取失效日期
     */
    public Date getExpiresDay(String token);
}
