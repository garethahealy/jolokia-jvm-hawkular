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

public class HawkularMetricsRunnable implements Runnable {

    private final HawkularMetricsService service;

    public HawkularMetricsRunnable(HawkularMetricsService service) {
        this.service = service;
    }

    @Override
    public void run() {
        service.run();
    }
}


