/*
 * #%L
 * GarethHealy :: Jolokia JVM to Hawkular Metrics Agent Embedded
 * %%
 * Copyright (C) 2013 - 2017 Gareth Healy
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
package com.garethahealy.jolokiajvmhawkular.core.metrics.collectors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.garethahealy.jolokiajvmhawkular.core.metrics.HawkularMetricsService;

import org.hawkular.client.core.ClientResponse;
import org.hawkular.client.core.HawkularClient;
import org.hawkular.client.core.jaxrs.Empty;
import org.hawkular.metrics.model.DataPoint;
import org.hawkular.metrics.model.Metric;
import org.hawkular.metrics.model.MetricId;
import org.hawkular.metrics.model.MetricType;
import org.jolokia.config.Configuration;
import org.jolokia.config.ProcessingParameters;
import org.jolokia.request.JmxRequest;
import org.jolokia.request.JmxRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeapMemoryUsageCollector implements Collector {

    private static final Logger LOG = LoggerFactory.getLogger(HeapMemoryUsageCollector.class);

    public JmxRequest generate() {
        ProcessingParameters params = new Configuration().getProcessingParameters(new HashMap<String, String>());
        return JmxRequestFactory.createGetRequest("read/java.lang:type=Memory/HeapMemoryUsage/used", params);
    }

    public void process(HawkularClient client, Map<String, Object> data) {
        /**
         {
         "request": {
         "path": "used",
         "mbean": "java.lang:type=Memory",
         "attribute": "HeapMemoryUsage",
         "type": "read"
         },
         "value": 40107480,
         "timestamp": 1474816047,
         "status": 200
         }
         */

        if (data == null) {
            LOG.error("Data is null for {}", HeapMemoryUsageCollector.class.getCanonicalName());
        } else {
            Double value = Double.valueOf(data.get("value").toString());
            Long timestamp = Long.valueOf(data.get("timestamp").toString());

            Map<String, String> tagsMap = new HashMap<String, String>();

            DataPoint<Double> point = new DataPoint<Double>(timestamp, value, tagsMap);
            List<DataPoint<Double>> points = Arrays.asList(point);

            String name = "Memory/HeapMemoryUsage/used";
            MetricId<Double> id = new MetricId<Double>(HawkularMetricsService.TENANT, MetricType.GAUGE, name);
            Metric<Double> metric = new Metric<Double>(id, tagsMap, null, points);

            ClientResponse<Empty> createResponse = client.metrics()
                .gauge()
                .createGaugeMetric(false, metric);

            LOG.info("createResponse; {} {} {}", createResponse.isSuccess(), createResponse.getStatusCode(), createResponse.getErrorMsg());

            ClientResponse<Empty> addDataResponse = client.metrics()
                .gauge()
                .addGaugeDataForMetric(metric.getId(), points);

            LOG.info("addDataResponse; {} {} {}", addDataResponse.isSuccess(), addDataResponse.getStatusCode(), addDataResponse.getErrorMsg());
        }
    }
}
