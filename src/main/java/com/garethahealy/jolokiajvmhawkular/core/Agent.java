package com.garethahealy.jolokiajvmhawkular.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jolokia.backend.BackendManager;
import org.jolokia.config.Configuration;
import org.jolokia.config.ProcessingParameters;
import org.jolokia.jvmagent.JvmAgent;
import org.jolokia.jvmagent.JvmAgentConfig;
import org.jolokia.jvmagent.handler.JolokiaHttpHandler;
import org.jolokia.request.JmxRequest;
import org.jolokia.request.JmxRequestFactory;
import org.jolokia.util.RequestType;

/**
 * Extends JvmAgent by making the base class non-final, protected (1)constructor, (2)main methods and (3)JolokiaServer var
 */
public final class Agent extends JvmAgent {

    public static void premain(String agentArgs) {
        premain(agentArgs);

        try {
            Field jolokiaHttpHandlerField = FieldUtils.getDeclaredField(server.getClass(), "jolokiaHttpHandler", true);
            JolokiaHttpHandler jolokiaHttpHandler = (JolokiaHttpHandler)jolokiaHttpHandlerField.get(server);

            Field backendManagerField = FieldUtils.getDeclaredField(jolokiaHttpHandler.getClass(), "backendManager", true);
            BackendManager backendManager = (BackendManager)backendManagerField.get(jolokiaHttpHandler);

            ProcessingParameters params = new Configuration().getProcessingParameters(new HashMap<String,String>());
            JmxRequest req = JmxRequestFactory.createGetRequest("read/java.lang:type=Memory/HeapMemoryUsage/used", params);

            backendManager.handleRequest(req);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        }

    }

    public static void agentmain(String agentArgs) {
        agentmain(agentArgs);
    }

}


//agent -> JolokiaServer -> jolokiaHttpHandler -> backendManager
