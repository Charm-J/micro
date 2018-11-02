package com.jeff.api.common;

/**
 * 常量类
 */
public class Const {
    public static final String MSG_OK = "OK";
    public static final String UTF_8 = "UTF-8";
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final long EXPIRED = 1800;//30分钟
    public static final String USER = "User";
    public static final String TOKEN = "Token";
    public static final String T0 = "T0";
    public static final String ONE = "1";

    // 分页
    public static final Integer DEFAULT_PAGENUM = 1;
    public static final Integer DEFAULT_PAGESIZE = 5;


    // redis缓存键
    public class RedisKeys {
        /**
         * 命名空间
         */
        private static final String NAMESPACE = "micro:";
        public static final String TOKEN_NAMESPACE = NAMESPACE + "token:";

        /* 用户详情 String micro:user_info_{#userId} */
        public static final String USER_INFO = "user_info_";
    }

    // 用户类型
    public enum UserType {
        NORMAL(0),  //普通用户
        VIP(1);     //VIP用户
        public int v;

        UserType(int v) {
            this.v = v;
        }

        public int getV() {
            return v;
        }
    }

}
