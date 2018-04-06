package io.pivotal.atlas;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import reactor.core.publisher.Flux;

import org.springframework.stereotype.Component;

@Component
public class SimulatedRestEndpointCalls {
	private final MeterRegistry registry;

	private final RandomEngine r = new MersenneTwister64(0);
	private final Normal incomingRequests = new Normal(0, 1, r);
	private final Normal duration = new Normal(250, 50, r);
	private final AtomicInteger latencyForThisSecond = new AtomicInteger(duration.nextInt());
	private final Random successFail = new Random();

	public SimulatedRestEndpointCalls(MeterRegistry registry) {
		this.registry = registry;
	}

	@PostConstruct
	public void startSimulation() {
		Flux.interval(Duration.ofSeconds(1))
				.doOnEach(d -> latencyForThisSecond.set(duration.nextInt()))
				.subscribe();

		// the potential for an "incoming request" every 10 ms
		Flux.interval(Duration.ofMillis(10))
				.doOnEach(d -> {
					simulateCall("books", "/api/books", 0.4);
					simulateCall("people", "/api/people", 0.8);
					simulateCall("things", "/api/things", 0.4);
				})
				.subscribe();
	}

	private void simulateCall(String client, String endpoint, double bias) {
		if (incomingRequests.nextDouble() + bias > 0) {
			// pretend the request took some amount of time, such that the time is
			// distributed normally with a mean of 250ms
			Timer.builder("http.client.requests")
					.tag("response", successFail.nextGaussian() > 0.2 ? "200" : "400")
					.tag("uri", endpoint)
					.tag("client", client)
					.register(registry)
					.record(latencyForThisSecond.get(), TimeUnit.MILLISECONDS);
		}
	}
}
