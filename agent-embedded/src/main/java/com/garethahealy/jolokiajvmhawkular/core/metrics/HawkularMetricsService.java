/*
 * #%L
 * GarethHealy :: Jolokia JVM to Hawkular Metrics Agent Embedded
 * %%
 * Copyright (C) 2013 - 2018 Gareth Healy
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
package com.garethahealy.jolokiajvmhawkular.core.metrics;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garethahealy.jolokiajvmhawkular.core.metrics.collectors.Collector;
import com.garethahealy.jolokiajvmhawkular.core.metrics.collectors.HeapMemoryUsageCollector;

import org.hawkular.client.core.HawkularClient;
import org.jolokia.backend.BackendManager;
import org.jolokia.request.JmxRequest;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HawkularMetricsService {

    public static final String TENANT = "cdi-simple-jetty";
    
    private static final Logger LOG = LoggerFactory.getLogger(HawkularMetricsService.class);

    private final BackendManager backendManager;
    private final HawkularClient client;
    private final List<Collector> collectors = Arrays.asList((Collector)new HeapMemoryUsageCollector());
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HawkularMetricsService(BackendManager backendManager, HawkularClient client) {
        this.backendManager = backendManager;
        this.client = client;
    }

    public void run() {
        LOG.info("Running...");

        JavaType mapClazzKeyType = objectMapper.getTypeFactory().constructType(String.class);
        JavaType mapClazzValueType = objectMapper.getTypeFactory().constructType(Object.class);
        JavaType mapClazzType = objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, mapClazzKeyType, mapClazzValueType);

        for (Collector current : collectors) {
            JSONObject response = handleRequest(current.generate());

            try {
                Map<String, Object> data = objectMapper.readValue(response.toJSONString(), mapClazzType);
                current.process(client, data);
            } catch (IOException e) {
                LOG.error("{}", e);
            }
        }
    }

    private JSONObject handleRequest(JmxRequest req) {
        JSONObject answer = null;

        try {
            answer = backendManager.handleRequest(req);
        } catch (IOException e) {
            LOG.error("{}", e);
        } catch (ReflectionException e) {
            LOG.error("{}", e);
        } catch (InstanceNotFoundException e) {
            LOG.error("{}", e);
        } catch (AttributeNotFoundException e) {
            LOG.error("{}", e);
        } catch (MBeanException e) {
            LOG.error("{}", e);
        }

        LOG.info("Response for {} is {}", req, answer);

        return answer;
    }
}
