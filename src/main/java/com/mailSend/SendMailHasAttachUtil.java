package com.mailSend;

/**
 * @author pc-zw
 * @describe
 * @date 2016/8/11
 */

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;


public class SendMailHasAttachUtil {

    private MimeMessage message;
    private Session session;
    private Transport transport;
    private String mailHost="";
    private int mailPort;
    private String sender_username="";
    private String sender_password="";
    private String smtp_auth = "";
    private Vector file;


    private Properties properties = new Properties();
    /*
     * 初始化方法
     */
    public SendMailHasAttachUtil(boolean debug) {
        InputStream in = SendMailHasAttachUtil.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(in);
            this.mailHost = properties.getProperty("mail.smtp.host");
            this.mailPort = Integer.parseInt(properties.getProperty("mail.smtp.port"));
            this.sender_username = properties.getProperty("mail.sender.username");
            this.sender_password = properties.getProperty("mail.sender.password");
            this.smtp_auth = properties.getProperty("mail.smtp.auth");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Authenticator auth = new MyAuthenticator(sender_username, sender_password);
        session = Session.getDefaultInstance(properties, auth);
        session.setDebug(debug);//开启后有调试信息
        message = new MimeMessage(session);
    }

    /**
     * 发送邮件
     *
     * @param subject
     *            邮件主题
     * @param sendHtml
     *            邮件内容
     * @param receiveUser
     *            收件人地址
     */
    public void doSendHtmlEmail(String subject, String sendHtml,String fileName,
                                String receiveUser) {
        try {
            // 发件人
            //InternetAddress from = new InternetAddress(sender_username);
            // 下面这个是设置发送人的Nick name
            InternetAddress from = new InternetAddress(MimeUtility.encodeWord("sky")+" <"+sender_username+">");
            message.setFrom(from);
            // 收件人
            InternetAddress to = new InternetAddress(receiveUser);
            message.setRecipient(Message.RecipientType.TO, to);//还可以有CC、BCC
            // 抄送人
            message.setRecipient(Message.RecipientType.CC, new InternetAddress("zhangwu@100.com"));
            // 暗送人
            message.setRecipient(Message.RecipientType.BCC, new InternetAddress("zhangwu@yy.com"));
            // 邮件主题
            message.setSubject(subject);
            String content = sendHtml.toString();
            // 邮件内容,也可以使纯文本"text/plain"
            message.setContent(content, "text/html;charset=UTF-8");
            file = new Vector();
            file.addElement(fileName);
            Multipart mp = new MimeMultipart();
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setContent(content.toString(), "text/html;charset=gb2312");
            mp.addBodyPart(mbp);
            if(!file.isEmpty()){//有附件
                Enumeration efile=file.elements();
                while(efile.hasMoreElements()){
                    mbp=new MimeBodyPart();
                    fileName=efile.nextElement().toString(); //选择出每一个附件名
                    FileDataSource fds=new FileDataSource(fileName); //得到数据源
                    mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart
                    mbp.setFileName(fds.getName());  //得到文件名同样至入BodyPart
                    mp.addBodyPart(mbp);
                }
                file.removeAllElements();
            }
            message.setContent(mp); //Multipart加入到信件
            message.setSentDate(new Date());     //设置信件头的发送日期
            // 保存邮件
            message.saveChanges();


            transport = session.getTransport("smtp");
            // smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(mailHost,mailPort, sender_username, sender_password);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
            //System.out.println("send success!");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(transport!=null){
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SendMailHasAttachUtil se = new SendMailHasAttachUtil(false);
        se.doSendHtmlEmail("邮件主题", "我错了","D:\\hello.txt", "843820873@qq.com");
    }
}
