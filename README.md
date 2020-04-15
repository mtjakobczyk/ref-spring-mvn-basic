# Reference application - Spring, Maven (basic), Docker
This is a minimalistic Spring Boot application (REST API) used to demonstrate a very simple Maven-based software building process that consists of the following phases:
- compiling Java source code
- running Unit Tests
- creating JAR file
- packing the JAR into OpenJDK-based Docker image
- pushing the image to the Container Image Registry (here Docker Hub)

### Application
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
Build process is done through Maven (using Spring Boot and Spotify Dockerfile plugins):
- https://docs.spring.io/spring-boot/docs/current/maven-plugin/usage.html
- https://github.com/spotify/dockerfile-maven

#### Unit Testing
There are two unit tests prepared:
- `shouldReturnOK()` that verifies the REST resource is present under the /uuid path and returns 200 OK
- `shouldReturnGeneratorName()` that verifies whether the application reads one of the returned values from environment variable

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

#### Packaging

As a matter of fact, packaging is performed in two stages: 
1. compiled Java classes are collected together and stored in an **executable JAR**
2. **Docker Image** is created: the JAR is added to a OpenJDK-based image and executed

##### Stage 1: JAR
Maven build part

    ...
    [INFO] --- maven-jar-plugin:3.1.2:jar (default-jar) @ basic-spring ---
    [INFO] Building jar: /Users/mjk/git/ref-spring-mvn-basic/service/basic-spring/target/basic-spring-1.0-SNAPSHOT.jar
    ...

Result: JAR

    .
    ├── Dockerfile
    ├── pom.xml
    ├── src
    ...
    └── target
        ├── basic-spring-1.0-SNAPSHOT.jar

##### Stage 2: Docker Image
Maven build part    
    
    ...
    [INFO] --- dockerfile-maven-plugin:1.4.13:build (default) @ basic-spring ---
    ...
    [INFO] Image will be built as mtjakobczyk/basic-spring:1.0-SNAPSHOT
    [INFO] 
    [INFO] Step 1/4 : FROM openjdk:8-jdk-alpine
    ...
    [INFO] Step 2/4 : ARG JAR_FILE=target/*.jar
    ...
    [INFO] Step 3/4 : COPY ${JAR_FILE} app.jar
    ...
    [INFO] Step 4/4 : ENTRYPOINT ["java","-jar","/app.jar"]
    ...
    [INFO] Successfully built fe04154972b4
    [INFO] Successfully tagged mtjakobczyk/basic-spring:1.0-SNAPSHOT
    ...
    [INFO] Successfully built mtjakobczyk/basic-spring:1.0-SNAPSHOT
    ...
    
Result: Docker Image

    REPOSITORY                 TAG                 IMAGE ID            CREATED             SIZE
    mtjakobczyk/basic-spring   1.0-SNAPSHOT        164f86ca9ada        5 seconds ago       135MB
    openjdk                    8-jdk-alpine        a3562aa0b991        11 months ago       105MB

#### Pushing to Docker Image Registry
Pushing image to a remote reigstry consists of two stages:
- tagging the image
- pushing the properly tagged image

##### Tagging
We used a custom property in the `pom.xml` to set the repository name:

    <properties>
        ...
        <dockerfile.maven.plugin.repository>mtjakobczyk</dockerfile.maven.plugin.repository>
    </properties>

We have to tag the image by using a prefix specific to the container registry (for example `registry.hub.docker.com` in case of the Docker Hub). We can override the property using the `-D` parameter:

    mvn dockerfile:tag -Ddockerfile.maven.plugin.repository=registry.hub.docker.com/mtjakobczyk
    
Docker Images:

    REPOSITORY                                         TAG                 IMAGE ID            CREATED             SIZE
    mtjakobczyk/basic-spring                           1.0-SNAPSHOT        164f86ca9ada        11 minutes ago      135MB
    registry.hub.docker.com/mtjakobczyk/basic-spring   1.0-SNAPSHOT        164f86ca9ada        11 minutes ago      135MB
    openjdk                                            8-jdk-alpine        a3562aa0b991        11 months ago       105MB

##### Pushing
To push the image we will use the `dockerfile:push` stage:

    mvn dockerfile:push -Ddockerfile.maven.plugin.repository=registry.hub.docker.com/mtjakobczyk \ 
        -Ddockerfile.username=$REGISTRY_USER -Ddockerfile.password="$REGISTRY_PASS" 
        
Maven build part

    [INFO] --- dockerfile-maven-plugin:1.4.13:push (default-cli) @ basic-spring ---
    [INFO] The push refers to repository [registry.hub.docker.com/mtjakobczyk/basic-spring]
    
    
