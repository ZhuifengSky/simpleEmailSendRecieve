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
     * ��ʼ������
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
    public void doSendHtmlEmail(String subject, String sendHtml,String fileName,
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
            file = new Vector();
            file.addElement(fileName);
            Multipart mp = new MimeMultipart();
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setContent(content.toString(), "text/html;charset=gb2312");
            mp.addBodyPart(mbp);
            if(!file.isEmpty()){//�и���
                Enumeration efile=file.elements();
                while(efile.hasMoreElements()){
                    mbp=new MimeBodyPart();
                    fileName=efile.nextElement().toString(); //ѡ���ÿһ��������
                    FileDataSource fds=new FileDataSource(fileName); //�õ�����Դ
                    mbp.setDataHandler(new DataHandler(fds)); //�õ�������������BodyPart
                    mbp.setFileName(fds.getName());  //�õ��ļ���ͬ������BodyPart
                    mp.addBodyPart(mbp);
                }
                file.removeAllElements();
            }
            message.setContent(mp); //Multipart���뵽�ż�
            message.setSentDate(new Date());     //�����ż�ͷ�ķ�������
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
        SendMailHasAttachUtil se = new SendMailHasAttachUtil(false);
        se.doSendHtmlEmail("�ʼ�����", "�Ҵ���","D:\\hello.txt", "843820873@qq.com");
    }
}
