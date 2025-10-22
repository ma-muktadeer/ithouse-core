package com.ithouse.core.security;

import com.ithouse.core.security.interfaces.Algorithm;
import com.ithouse.core.security.interfaces.Security;

abstract class AbstractSecurity implements Security {

    private final Algorithm map;

    public AbstractSecurity() {
        map = new AlgorithmProvider();
    }

    public String encrypt(String arg0) throws Exception {
        return SecurityUtils.encrypt(arg0, this.map.arg0());
    }

    public String decrypt(String arg0) throws Exception {
        return SecurityUtils.decrypt(arg0, this.map.arg1());
    }

    public String get(String arg0) throws Exception {
        return arg0.replace("ENC[", "").replace("]", "");
    }

}
