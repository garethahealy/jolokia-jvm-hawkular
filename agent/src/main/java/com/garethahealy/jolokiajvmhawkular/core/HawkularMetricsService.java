/*
 * #%L
 * GarethHealy :: Jolokia JVM to Hawkular Metrics Agent
 * %%
 * Copyright (C) 2013 - 2016 Gareth Healy
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.garethahealy.jolokiajvmhawkular.core;

import java.io.IOException;
import java.util.HashMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.jolokia.backend.BackendManager;
import org.jolokia.config.Configuration;
import org.jolokia.config.ProcessingParameters;
import org.jolokia.request.JmxRequest;
import org.jolokia.request.JmxRequestFactory;
import org.json.simple.JSONObject;

public class HawkularMetricsService {

    private final BackendManager backendManager;

    public HawkularMetricsService(BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    public void getUsedMemory() {
        ProcessingParameters params = new Configuration().getProcessingParameters(new HashMap<String, String>());
        JmxRequest req = JmxRequestFactory.createGetRequest("read/java.lang:type=Memory/HeapMemoryUsage/used", params);

        handleRequest(req);
    }

    private JSONObject handleRequest(JmxRequest req) {
        JSONObject answer = null;

        try {
            answer = backendManager.handleRequest(req);
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

        System.out.println(answer);

        return answer;
    }
}
