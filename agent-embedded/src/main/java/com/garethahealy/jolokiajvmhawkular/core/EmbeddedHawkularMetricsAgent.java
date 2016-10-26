/*
 * #%L
 * GarethHealy :: Jolokia JVM to Hawkular Metrics Agent Embedded
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

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.garethahealy.jolokiajvmhawkular.core.metrics.HawkularMetricsRunnable;
import com.garethahealy.jolokiajvmhawkular.core.metrics.HawkularMetricsService;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hawkular.client.core.HawkularClient;
import org.jolokia.backend.BackendManager;
import org.jolokia.jvmagent.CustomJvmAgent;
import org.jolokia.jvmagent.handler.JolokiaHttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends JvmAgent by making the base class non-final and updating the below to be protected:
 * (1)constructor
 * (2)premain method
 * (3)JolokiaServer var
 */
public final class EmbeddedHawkularMetricsAgent extends CustomJvmAgent {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedHawkularMetricsAgent.class);

    public static void premain(String agentArgs) {
        CustomJvmAgent.premain(agentArgs);

        try {
            LOG.info("About to load BackendManager...");

            Field jolokiaHttpHandlerField = FieldUtils.getDeclaredField(server.getClass(), "jolokiaHttpHandler", true);
            JolokiaHttpHandler jolokiaHttpHandler = (JolokiaHttpHandler)jolokiaHttpHandlerField.get(server);

            Field backendManagerField = FieldUtils.getDeclaredField(jolokiaHttpHandler.getClass(), "backendManager", true);
            BackendManager backendManager = (BackendManager)backendManagerField.get(jolokiaHttpHandler);

            HawkularClient client = getHawkularClient();
            if (client == null) {
                LOG.error("HawkularClient is null. Not starting HawkularMetricsService via ScheduledExecutorService.");
            } else {
                BasicThreadFactory factory = new BasicThreadFactory.Builder()
                    .namingPattern("hawkular-metrics-%d")
                    .daemon(true)
                    .priority(Thread.MAX_PRIORITY)
                    .build();

                ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(factory);
                exec.scheduleAtFixedRate(new HawkularMetricsRunnable(new HawkularMetricsService(backendManager, client)), 15, 15, TimeUnit.SECONDS);

                LOG.info("Started HawkularMetricsService via ScheduledExecutorService.");
            }
        } catch (IllegalAccessException e) {
            LOG.error("{}", e);
        }
    }

    private static HawkularClient getHawkularClient() {
        HawkularClient client = null;
        try {
            client = HawkularClient.builder(HawkularMetricsService.TENANT)
                .uri(new URI("http://192.168.99.100:8080"))
                .basicAuthentication("jdoe", "password")
                .build();
        } catch (URISyntaxException e) {
            LOG.error("{}", e);
        }

        return client;
    }
}
