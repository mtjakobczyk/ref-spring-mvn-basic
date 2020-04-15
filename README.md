# Reference application - Spring, Maven (basic), Docker
This is a minimalistic Spring Boot application (REST API) used to demonstrate a very simple Maven-based software building process that consists of the following phases:
- compiling Java source code
- running Unit Tests
- creating JAR file
- packing the JAR into OpenJDK-based Docker image
- pushing the image to the Container Image Registry (here Docker Hub)

The application alone returns a dynamically generated UUID (`uuid`) in a form of a JSON payload:

    { "generatorName":"uuid-1", "uuid":"5e8bbba3-baef-40ea-8cdf-40d81616d0fa" }
    
The `generatorName` field contains the name of the backend service that can be injected using Spring properties or a plain environment variable `GENERATOR_NAME`. You can do it like this:

    # JAR
    GENERATOR_NAME=uuid-1 exec java -jar basic-spring-1.0-SNAPSHOT.jar pl.mtjakobczyk.apps.reference.App
    # Docker container
    docker run -d -p 8080:8080 -e "GENERATOR_NAME=uuid-1" --name uuid-1 mtjakobczyk/basic-spring:1.0-SNAPSHOT
    

### Initialization
The project tree has been initialized using the following command:

    mvn archetype:generate -DgroupId=pl.mtjakobczyk.apps.reference -DartifactId=basic-spring -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
    
### Structure

    .
    ├── Dockerfile
    ├── pom.xml
    └── src
        ├── main
        │   └── java
        │       └── pl
        │           └── mtjakobczyk
        │               └── apps
        │                   └── reference
        │                       ├── App.java
        │                       ├── controllers
        │                       │   └── UUIDController.java
        │                       └── model
        │                           └── Message.java
        └── test
            └── java
                └── pl
                    └── mtjakobczyk
                        └── apps
                            └── reference
                                └── AppTest.java

### Build Process
Build process is done through Maven (using Spring Boot and Spotify Dockerfile plugins).

#### Unit Testing
There are two unit tests prepared:
- shouldReturnOK() that verifies the REST resource is present under the /uuid path and returns 200 OK
- shouldReturnGeneratorName() that verifies whether the application reads one of the returned values from environment variable

The unit tests rely on Spring Boot Test Framework (including Spring MVC Test components).  

Run tests:

    mvn test
    
Output:

    [INFO] Scanning for projects...
    [INFO] 
    [INFO] -------------< pl.mtjakobczyk.apps.reference:basic-spring >-------------
    [INFO] Building basic-spring 1.0-SNAPSHOT
    [INFO] --------------------------------[ jar ]---------------------------------
    [INFO] 
    [INFO] --- maven-resources-plugin:3.1.0:resources (default-resources) @ basic-spring ---
    ...
    [INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ basic-spring ---
    ...
    [INFO] --- maven-resources-plugin:3.1.0:testResources (default-testResources) @ basic-spring ---
    ...
    [INFO] --- maven-compiler-plugin:3.8.1:testCompile (default-testCompile) @ basic-spring ---
    ...
    [INFO] --- maven-surefire-plugin:2.22.2:test (default-test) @ basic-spring ---
    [INFO] 
    [INFO] -------------------------------------------------------
    [INFO]  T E S T S
    [INFO] -------------------------------------------------------
    [INFO] Running pl.mtjakobczyk.apps.reference.AppTest
    ...
    [INFO] Results:
    [INFO] 
    [INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
    [INFO] 
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time:  5.211 s
    [INFO] Finished at: 2020-04-15T19:00:51+02:00
    [INFO] ------------------------------------------------------------------------
