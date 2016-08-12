package com.mailSend;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author pc-zw
 * @describe
 * @date 2016/8/12
 */

class MyAuthenticator  extends Authenticator {
    private String strUser;
    private String strPwd;
    public MyAuthenticator(String user, String password) {
        this.strUser = user;
        this.strPwd = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(strUser, strPwd);
    }
}


