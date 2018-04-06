# Embedding Atlas into a Spring Boot 2 App

This is just a demonstration of how you can embed a full running Atlas server inside a Spring Boot 2 application. The demo includes a simple static [index.html](https://github.com/jkschneider/atlas-embedded/blob/master/src/main/resources/static/index.html) file that has a series of `<img>` tags that generate charts from the embedded Atlas.

For demonstration alone, it contains a [simulation](https://github.com/jkschneider/atlas-embedded/blob/master/src/main/java/io/pivotal/atlas/SimulatedRestEndpointCalls.java) of semi-frequent outbound HTTP client requests.

Enjoy!
