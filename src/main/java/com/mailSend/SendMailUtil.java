package com.mailSend;

/**
 * @author pc-zw
 * @describe
 * @date 2016/8/11
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;


public class SendMailUtil {
    /**
     * Message���󽫴洢����ʵ�ʷ��͵ĵ����ʼ���Ϣ��
     * Message������Ϊһ��MimeMessage����������������Ҫ֪��Ӧ��ѡ����һ��SendMailUtil3 session��
     */
    private MimeMessage message;

    /**
     * Session�����SendMailUtil3�е�һ���ʼ��Ự��
     * ÿһ������SendMailUtil3��Ӧ�ó���������һ��Session��������������Session����
     *
     * SendMailUtil3��ҪProperties������һ��session����
     * Ѱ��"mail.smtp.host"    ����ֵ���Ƿ����ʼ�������
     * Ѱ��"mail.smtp.auth"    �����֤��Ŀǰ����ʼ�����������Ҫ��һ��
     */
    private Session session;

    /***
     * �ʼ��Ǽȿ��Ա�����Ҳ���Ա��ܵ���SendMailUtil3ʹ����������ͬ������������������ܣ�Transport �� Store��
     * Transport ������������Ϣ�ģ���Store�������š�������Ľ̳�����ֻ��Ҫ�õ�Transport����
     */
    private Transport transport;

    private String mailHost="";
    private int mailPort;
    private String sender_username="";
    private String sender_password="";
    private String smtp_auth = "";


    private Properties properties = new Properties();
    /*
     * ��ʼ������
     */
    public SendMailUtil(boolean debug) {
        InputStream in = SendMailUtil.class.getClassLoader().getResourceAsStream("config.properties");
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
        session.setDebug(debug);//�������е�����Ϣ
        message = new MimeMessage(session);
    }

    /**
     * �����ʼ�
     *
     * @param subject
     *            �ʼ�����
     * @param sendHtml
     *            �ʼ�����
     * @param receiveUser
     *            �ռ��˵�ַ
     */
    public void doSendHtmlEmail(String subject, String sendHtml,
                                String receiveUser) {
        try {
            // ������
            //InternetAddress from = new InternetAddress(sender_username);
            // ������������÷����˵�Nick name
            InternetAddress from = new InternetAddress(MimeUtility.encodeWord("sky")+" <"+sender_username+">");
            message.setFrom(from);

            // �ռ���
            InternetAddress to = new InternetAddress(receiveUser);
            message.setRecipient(Message.RecipientType.TO, to);//��������CC��BCC
            // ������
            message.setRecipient(Message.RecipientType.CC, new InternetAddress("zhangwu@100.com"));
            // ������
            message.setRecipient(Message.RecipientType.BCC, new InternetAddress("zhangwu@yy.com"));

            // �ʼ�����
            message.setSubject(subject);

            String content = sendHtml.toString();
            // �ʼ�����,Ҳ����ʹ���ı�"text/plain"
            message.setContent(content, "text/html;charset=UTF-8");
            message.setSentDate(new Date());
            // �����ʼ�
            message.saveChanges();

            transport = session.getTransport("smtp");
            // smtp��֤���������������ʼ��������û�������
            transport.connect(mailHost,mailPort, sender_username, sender_password);
            // ����
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
        SendMailUtil se = new SendMailUtil(false);
        se.doSendHtmlEmail("�ʼ�����", "�Ҵ���", "843820873@qq.com");
    }
}
