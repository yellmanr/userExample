# Example User API Application #

REST API meeting the following requirements 

Provides individual services for:
1. Adds a User to the list of stored users using a JSON request body. The request should be rejected if the same first name/last name combination is already stored in the system.
2. Returns a single User when requested by ID as JSON
3. Returns a list of all Users as JSON, ordered alphabetically by last name
4. Deletes a User when requested by ID
5. User object includes ID, first name, and, last name

Store user using the h2 embedded database.
Protect endpoints with Spring Security.
Appropriate HTTP status codes

## Install & Run ## 

Prerequisites: 
Install Java and Gradle.  Ensure environment variables are set, including PATH.  Download code baseline from GitHub.


To get dependencies and build java, at the command prompt run:

	gradle build

If you update the project and get unexpect errors you may need to refresh dependencies, at the command prompt run:

	gradle --refresh-dependencies build

To run the Sprint Boot Application, at the command prompt run:

	gradle runBoot


## Usage ## 

This is an API.  You can run in your IDE or using Postman.  A postman collection has been saved at the project root named: Localhost user.postman_collection.json

## Implementation Details ## 

### **Technologies** ###
* Gradle
* Java
* Spring Boot Web
* Spring Boot Data JPA
* Spring Boot Test
* Spring Boot Security
* Spring Boot DevTools
* Lombok
* H2

### **Application Layers** ###

#### Web Layer ####
The web layer handles the HTTP requests, translates the JSON parameter to object, and authenticates the request and transfers it to the business layer. 

#### Business ####
The business layer handles all the business logic. It consists of service classes and uses services provided by persistence layers. It also performs authorization and validation.

#### Persistence ####
The persistence layer contains all the storage logic and translates business objects from and to database rows.  Use Spring Data JPA to define data entities and repositories to store each entity.

Spring Data JPA provides three repository types for relational databases to handle storage logic.  Choose which repository type to use based on its own functionality:

1. CrudRepository provides CRUD functions
2. PagingAndSortingRepository provides methods to do pagination and sort records
3. JpaRepository provides JPA related methods such as flushing the persistence context and delete records in a batch

`JpaRepository` extends `PagingAndSortingRepository` which in turn extends `CrudRepository`; because of this inheritance relationship, the JpaRepository contains the full API of CrudRepository and PagingAndSortingRepository.  

### **Lombok** ###
Use Lombok to reduce the amount of boilerplate code.  Use these annotations: 
`@Data` for domain and DTO classes
`@Slf4J` for logging in all classes

#### @Data ####
Use the @Data annotation to implement all of these annotations: @ToString, @EqualsAndHashCode, @Getter on all fields, @Setter on all non-final fields, and @RequiredArgsConstructor.  

#### Constructors ####
`@RequiredArgsConstructor` must be explicitly included when other constructor annotations are used
`@NoArgsConstructor` is required for deserialization of JSON with Jackson in RestController
`@AllArgsConstructor` is required when using `@Builder`.

#### @Builder ####
Use the `@Builder` annotation to implement the builder pattern as a simple means of flexibly and clearly adding attributes to an Object.  The builder pattern is very convenient for creating objects in test cases and it is very clear for creating Objects with many attributes.

	User alex = User.builder()
	    	.id(1)
	            .firstName("Alex")
	    	.lastName("Bell")
	    	.build();

#### Indicate Not Null ####
Use `@NonNull` on class attributes that must be populated.  This will be used for `@RequiredArgsConstructor`

Use `@NonNull` on method attributes that must be populated.  `Lombok.NonNull` will insert a null check and throw a NullPointerException.

#### Exclude Fields ####
Use Exclude annotations on attributes to exclude from toString, equals, and hashcode methods, or use AccessLevel.NONE attribute to exclude a field having a setter or getter generated, for example:

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private String notNeeded;

#### callSuper ####
Use callSuper annotation attribute to add a call to super methods for toString, equals, and hashcode methods, for example: 

	@ToString(callSuper=true)
	@EqualsAndHashCode(callSuper=true)
	public static class Square extends Shape {...}

### **Logging** ###
Using Spring Boot default logger which is SLF4J with a Logback implementation.  Consider switching to SLF4J with Log4j2 for either of these two reasons: (1) compatibility with other application logging or (2) for potential performance benefits with asynchronous logging.  Further investigation would be needed.

#### Lombok @Slf4j ####
With the @Slf4j annotation at the class level Lombok automatically generates this line:

	Creates private static final org.slf4j.Logger log =
		org.slf4j.LoggerFactory.getLogger(LogExample.class);

Simple Example class:

	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RestController;
	import lombok.extern.slf4j.Slf4j;
 
	@RestController
	@Slf4j
	public class LogController {
	
		@RequestMapping("/log")
		public String index() {
			log.trace("A TRACE Message");
			log.debug("A DEBUG Message");
			log.info("An INFO Message");
			log.warn("A WARN Message");
			log.error("An ERROR Message");
	
			return "Howdy! Check out the Logs to see the output...";
		}
	}

### **Automated Testing** ###

#### Repository Integration Testing ####
Create integration tests for custom repository methods.  Use @DataJpaTest to standard setup of a persistence layer with an in-memory H2 database and an JPA EntityManager.

#### Service Unit Testing ####
Create unit tests for service methods with business logic to be tested.  Use @MockBean for repositories to isolate testing business logic. 

#### Controller Testing ####



## Credits

Written by Rachel Yellman, yellmanr@outlook.com

## License

Use as you please.