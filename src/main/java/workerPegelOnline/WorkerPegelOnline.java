/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package workerPegelOnline;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.state.KeyValueStore;

// Exception imports
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

// new imports
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KGroupedStream;
// import org.apache.kafka.streams.kstream.KTable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * In this example, we implement a simple WordCount program using the high-level Streams DSL
 * that reads from a source topic "streams-plaintext-input", where the values of messages represent lines of text,
 * split each text line into words and then compute the word occurence histogram, write the continuous updated histogram
 * into a topic "streams-wordcount-output" where each record is an updated count of a single word.
 */
public class WorkerPegelOnline {

    public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-workerpegelonline");
        // props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		final StreamsBuilder builder = new StreamsBuilder();

		// builder.stream("streams-plaintext-input").to("streams-pipe-output");

		// #####################################################
		// create source
		KStream<String, String> sourceManip = builder.stream("PegelOnlineData");

		// parse source to distinguish between flooding and no flooding
		KStream<String, Output> floodEvent = sourceManip
				.map((KeyValueMapper<String, String, KeyValue<String, Output>>) (key, value) -> {
					KeyValue<String, Output> result = null;

					PegelOnlineDataPoint manipObj = new PegelOnlineDataPoint();
					manipObj.setName("Muenster");
					manipObj.setLat(51.96236);
					manipObj.setLon(7.62571);
					manipObj.setFlood(250);
					manipObj.setMeas(251);

                    System.out.println(' ');
                    System.out.println(value);

					try {

						JSONObject element = new JSONObject(value);
						String newKey = element.getString("longname");
	
						System.out.println(newKey);
					} catch (JSONException e) {
						// Data Point is discarded.
					}


					Boolean bool = false;
					Point point = new Point();

					System.out.println(manipObj.getLat());

					// // if measured height is smaller than flooding threshold, set to true
					// if (value.getMeas() > value.getFlood()) {
					// 	bool = true;
					// }

					Output output = new Output();
					output.setName(key);
					output.setBool(bool);
					// set lat and lon of region
					output.setLat(manipObj.getLat());
					output.setLon(manipObj.getLon());

					result = result.pair(key, output);
					
					return result;
				});

		// use floodEvent to create String stream
		KStream<String, String> outputStream = floodEvent
				.map((KeyValueMapper<String, Output, KeyValue<String, String>>) (key, value) -> {
					KeyValue<String, String> result = null;

					String outputString = value.getName().toString() + " - " + value.getBool().toString() + " - ["
							+ String.valueOf(value.getLat()) + ", "
							+ String.valueOf(value.getLon()) + "]";

					result = result.pair(key, outputString);
					return result;
				});

		outputStream.to("PegelOnlineOutput");

		// #####################################################

		final Topology topology = builder.build();

		// print topology and show source and sink
		System.out.println(topology.describe());

		final KafkaStreams streams = new KafkaStreams(topology, props);
		final CountDownLatch latch = new CountDownLatch(1);

		// attach shutdown handler to catch control-c
		Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
			@Override
			public void run() {
				streams.close();
				latch.countDown();
			}
		});

		try {
			streams.start();
			latch.await();
		} catch (Throwable e) {
			System.exit(1);
		}
		System.exit(0);
    }

}
