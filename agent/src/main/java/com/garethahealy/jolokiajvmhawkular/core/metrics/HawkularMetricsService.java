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
package com.garethahealy.jolokiajvmhawkular.core.metrics;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import com.garethahealy.jolokiajvmhawkular.core.metrics.collectors.HeapMemoryUsageCollector;
import com.garethahealy.jolokiajvmhawkular.core.metrics.collectors.Collector;

import org.jolokia.backend.BackendManager;
import org.jolokia.request.JmxRequest;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HawkularMetricsService {

    private static final Logger LOG = LoggerFactory.getLogger(HawkularMetricsService.class);

    private final BackendManager backendManager;
    private final List<Collector> collectors = Arrays.asList((Collector)new HeapMemoryUsageCollector());

    public HawkularMetricsService(BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    public void run() {
        for (Collector current : collectors) {
            JSONObject response = handleRequest(current.generate());

            current.process(response);
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

        LOG.trace("Response for {} is {}", req, answer);

        return answer;
    }
}
