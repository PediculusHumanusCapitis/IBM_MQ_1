
import com.ibm.mq.jms.*;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;


public class MqStub {
    public static void main(String[] args) {
        try{
            MQQueueConnection mqConn;
            MQQueueConnectionFactory mqCF;
            final MQQueueSession mqSession;
            MQQueue mqIn, mqOut;
            MQQueueReceiver mqReceiver;
            MQQueueSender mqSender;

            mqCF = new MQQueueConnectionFactory();
            mqCF.setHostName("localhost");

            mqCF.setPort(1410);

            mqCF.setQueueManager("MQTest");
            mqCF.setChannel("SYSTEM.DEP.SVRCONN");
            mqConn = (MQQueueConnection) mqCF.createConnection();
            mqSession = (MQQueueSession) mqConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

            mqIn = (MQQueue) mqSession.createQueue("Mq.In");
            mqOut = (MQQueue) mqSession.createQueue("Mq.Out");

            mqReceiver = (MQQueueReceiver) mqSession.createReceiver(mqIn);
            mqSender = (MQQueueSender) mqSession.createSender(mqOut);

            MessageListener Listener = new MessageListener(){

                @Override
                public void onMessage(javax.jms.Message message) {
                    System.out.println("Got message!");
                    if(message instanceof TextMessage){
                        try{
                            TextMessage tMsg = (TextMessage) message;
                            String msgText = tMsg.getText();
                            System.out.println("Message: " + msgText);

                            TextMessage newMessage = mqSession.createTextMessage(msgText);
                            mqSender.send(newMessage);
                            System.out.println("Message sent to MQ.OUT");
                            mqSession.commit();
                        }catch (JMSException e){
                            e.printStackTrace();
                        }
                    }
                };

            };
            mqReceiver.setMessageListener(Listener);
            mqConn.start();
            System.out.println("Stub Started");
        }catch (JMSException e){
            e.printStackTrace();
        }
        try{
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
