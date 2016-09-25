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

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jolokia.backend.BackendManager;
import org.jolokia.jvmagent.JvmAgent;
import org.jolokia.jvmagent.handler.JolokiaHttpHandler;

/**
 * Extends JvmAgent by making the base class non-final; protected:
 * (1)constructor
 * (2)premain method
 * (3)JolokiaServer var
 */
public final class Agent extends JvmAgent {

    public static void premain(String agentArgs) {
        JvmAgent.premain(agentArgs);

        try {
            System.out.println("About to load BackendManager");

            Field jolokiaHttpHandlerField = FieldUtils.getDeclaredField(server.getClass(), "jolokiaHttpHandler", true);
            JolokiaHttpHandler jolokiaHttpHandler = (JolokiaHttpHandler)jolokiaHttpHandlerField.get(server);

            Field backendManagerField = FieldUtils.getDeclaredField(jolokiaHttpHandler.getClass(), "backendManager", true);
            BackendManager backendManager = (BackendManager)backendManagerField.get(jolokiaHttpHandler);

            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(new HawkularMetricsRunnable(new HawkularMetricsService(backendManager)), 0, 5, TimeUnit.SECONDS);

            System.out.println("Started HawkularMetricsService ScheduledExecutorService");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}

//agent -> JolokiaServer -> jolokiaHttpHandler -> backendManager
