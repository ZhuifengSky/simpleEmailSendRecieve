package com.mailSend;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author
 * @describe
 * @date 2016/8/12
 */
public class RecieveMailUtil {

    private MimeMessage message;
    private Session session;
    private Folder folder;
    private Store store;
    private String mailHost="";
    private int mailPort;
    private String username="";
    private String password="";
    private String mail_auth = "";


    private Properties properties = new Properties();
    /*
     * 初始化方法
     */
    public RecieveMailUtil(boolean debug) {
        InputStream in = SendMailUtil.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(in);
            this.mailHost = properties.getProperty("mail.pop3.host");
            this.mailPort = Integer.parseInt(properties.getProperty("mail.pop3.port"));
            this.username = properties.getProperty("mail.username");
            this.password = properties.getProperty("mail.password");
            this.mail_auth = properties.getProperty("mail.auth");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Authenticator auth = new MyAuthenticator(username, password);
        session = Session.getDefaultInstance(properties, auth);
        session.setDebug(debug);//开启后有调试信息
    }

    /**
     * 收取邮件
     */
    public void recieveEmail() {
        try {
            // Get the store
            store = session.getStore("pop3");
            store.connect(mailHost, username, password);

            /* Get folder */
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // Get directory
            Message message[] = folder.getMessages();

            for (int i=0, n=message.length; i<n; i++) {
                System.out.println(i + ": " + message[i].getSentDate()
                        + "\t" + message[i].getSubject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (folder!=null){
                try {
                    folder.close(false);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            if(store!=null){
                try {
                    // Close connection
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void main(String[] args) {
        RecieveMailUtil se = new RecieveMailUtil(false);
        se.recieveEmail();
    }
}
