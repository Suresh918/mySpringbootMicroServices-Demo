package com.example.mirai.projectname.releasepackageservice.zecn.service;

import java.io.StringWriter;

import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.projectname.releasepackageservice.zecn.model.MySqlEcr;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${mirai.libraries.jms.url:}')")
public class ReleasePackageZecnService implements ZecnServiceInterface {

    private final JmsTemplate jmsTemplate;

    private String serializeToXml(MySqlEcr mySQLEcr) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(MySqlEcr.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(mySQLEcr, sw);
        return sw.toString();
    }

    public void processAndSendMessage(String ecnId,String title,String state) {
        String[] ecnIdArray = ecnId!=null?ecnId.split("-"):null;
        String summary = title!=null?title.substring(0, Math.min(title.length(), 120)):null;
        MySqlEcr mySQLEcr = new MySqlEcr();
        mySQLEcr.setId(ecnIdArray[1]!=null?ecnIdArray[1]:null);
        mySQLEcr.setSummary(summary);
        mySQLEcr.setEcrstate(state);
        String xmlResponse = null;
        try {
            xmlResponse = this.serializeToXml(mySQLEcr);
        } catch (JAXBException e) {
            log.error("Unable to create Zecn message: " + e.getMessage());
            throw new InternalAssertionException("Not able to create Zecn message");
        }
        sendMessage(xmlResponse);
    }

    public void sendMessage(String message) {
        jmsTemplate.convertAndSend("example.BIN_BPM_MC_RELEASEPACKAGE.ECN",
                message, new MessagePostProcessor() {
                    @Override
                    public javax.jms.Message
                    postProcessMessage(javax.jms.Message message)  {
                        try {
                            message.setStringProperty("type", "UPDATE-SAP-CHANGES");
                            message.setStringProperty("status", "SUCCESS");
                            message.setStringProperty("entity", "com.example.mirai.projectname.releasepackageservice.releasepackage.model.Releasepackage");
                            message.setStringProperty("payload", "com.example.mirai.projectname.releasepackageservice.releasepackage.MySQLEcr");
                        } catch (JMSException e) {
                            log.error("Unable to send Zecn message: " + e.getMessage());
                            throw new InternalAssertionException("Not able to send Zecn message");
                        }
                        return message;
                    }
                });
    }
}
