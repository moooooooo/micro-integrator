/*
 *Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *WSO2 Inc. licenses this file to you under the Apache License,
 *Version 2.0 (the "License"); you may not use this file except
 *in compliance with the License.
 *You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing,
 *software distributed under the License is distributed on an
 *"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *KIND, either express or implied.  See the License for the
 *specific language governing permissions and limitations
 *under the License.
 */
package org.wso2.carbon.esb.jms.transport.test;

import org.awaitility.Awaitility;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.extensions.servers.jmsserver.client.JMSQueueMessageConsumer;
import org.wso2.carbon.automation.extensions.servers.jmsserver.controller.config.JMSBrokerConfigurationProvider;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.Utils;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;
import org.wso2.esb.integration.common.utils.common.ServerConfigurationManager;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class JMSQueueAsProxyEndpointTestCase extends ESBIntegrationTest {
    private int NUM_OF_MESSAGES = 5;

    @BeforeClass(alwaysRun = true)
    protected void init() throws Exception {
        super.init();
    }

    @Test(groups = {
            "wso2.esb" }, description = "Test sending message to jms endpoint with pox format from proxy service")
    public void testJMSEndpointPox() throws Exception {
        AxisServiceClient client = new AxisServiceClient();

        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(
                JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            consumer.connect("TestQueuePox");
            //sending messages to proxy service
            for (int i = 0; i < NUM_OF_MESSAGES; i++) {
                client.sendRobust(Utils.getStockQuoteRequest("JMS"), getProxyServiceURLHttp("proxyWithJmsEndpointPox"),
                        "getQuote");
            }
            //wait for messages to reach the destination queue
            Awaitility.await().pollInterval(50, TimeUnit.MILLISECONDS).atMost(60, TimeUnit.SECONDS)
                    .until(isMessagesConsumed(consumer));
            for (int i = 0; i < NUM_OF_MESSAGES; i++) {
                String message = consumer.popMessage();
                Assert.assertNotNull(message,
                        "Message not found. message sent by proxy service not reached to the destination Queue");
                Assert.assertEquals(message, "<ns:getQuote xmlns:ns=\"http://services.samples\"><"
                                + "ns:request><ns:symbol>JMS</ns:symbol></ns:request></ns:getQuote>",
                        "Message Contains mismatched");
            }
        } finally {
            consumer.disconnect();
        }

    }

    @Test(groups = {
            "wso2.esb" }, description = "Test sending message to jms endpoint with soap11 format from proxy service")
    public void testJMSEndpointSoap11() throws Exception {
        AxisServiceClient client = new AxisServiceClient();

        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(
                JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            consumer.connect("TestQueueSoap11");
            //sending messages to proxy service
            for (int i = 0; i < NUM_OF_MESSAGES; i++) {
                client.sendRobust(Utils.getStockQuoteRequest("JMS"),
                        getProxyServiceURLHttp("proxyWithJmsEndpointSoap11"), "getQuote");
            }
            //wait for messages to reach the destination queue

            Awaitility.await().pollInterval(50, TimeUnit.MILLISECONDS).atMost(60, TimeUnit.SECONDS)
                    .until(isMessagesConsumed(consumer));
            String expectedOutCome = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns:getQuote xmlns:ns=\"http://services.samples\"><ns:request><ns:symbol>JMS</ns:symbol></ns:request></ns:getQuote></soapenv:Body></soapenv:Envelope>";

            for (int i = 0; i < NUM_OF_MESSAGES; i++) {
                String message = consumer.popMessage();
                Assert.assertNotNull(message,
                        "Message not found. message sent by proxy service not reached to the destination Queue");
                //added contains since <?xml version=\"1.0\" encoding=\"UTF-8\"?> " are replaced with ' in jenkins. <?xml version=\'1.0\' encoding=\'UTF-8\'?>
                Assert.assertTrue(message.contains(expectedOutCome), "Message Contains mismatched");
            }
        } finally {
            consumer.disconnect();
        }
    }

    @Test(groups = {
            "wso2.esb" }, description = "Test sending message to jms endpoint with soap12 format from proxy service")
    public void testJMSEndpointSoap12() throws Exception {
        AxisServiceClient client = new AxisServiceClient();

        JMSQueueMessageConsumer consumer = new JMSQueueMessageConsumer(
                JMSBrokerConfigurationProvider.getInstance().getBrokerConfiguration());
        try {
            consumer.connect("TestQueueSoap12");
            //sending messages to proxy service
            for (int i = 0; i < NUM_OF_MESSAGES; i++) {
                client.sendRobust(Utils.getStockQuoteRequest("JMS"),
                        getProxyServiceURLHttp("proxyWithJmsEndpointSoap12"), "getQuote");
            }
            //wait for messages to reach the destination queue
            Awaitility.await().pollInterval(50, TimeUnit.MILLISECONDS).atMost(60, TimeUnit.SECONDS)
                    .until(isMessagesConsumed(consumer));
            String expectedOutPut = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"><soapenv:Body><ns:getQuote xmlns:ns=\"http://services.samples\"><ns:request><ns:symbol>JMS</ns:symbol></ns:request></ns:getQuote></soapenv:Body></soapenv:Envelope>";
            for (int i = 0; i < NUM_OF_MESSAGES; i++) {
                String message = consumer.popMessage();
                Assert.assertNotNull(message,
                        "Message not found. message sent by proxy service not reached to the destination Queue");
                //added contains since <?xml version=\"1.0\" encoding=\"UTF-8\"?> " are replaced with ' in jenkins. <?xml version=\'1.0\' encoding=\'UTF-8\'?>
                Assert.assertTrue(message.contains(expectedOutPut), "Message Contains mismatched");
            }
        } finally {
            consumer.disconnect();
        }
    }

    private Callable<Boolean> isMessagesConsumed(final JMSQueueMessageConsumer consumer) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return consumer.getMessages().size() == NUM_OF_MESSAGES;
            }
        };
    }
}
