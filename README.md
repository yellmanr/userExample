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
* Spring Boot Security Test
* Spring Boot Validation
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

### **Web Validation & Exception Handling** ###

Use javax.validation to validate data coming in to Controller methods.  Three pieces are needed to do this:

#### Indicate Valid RequestBody ####
@Valid annotation to indicate the RequestBody object should be validated

	public ResponseEntity<User> create(@Valid @RequestBody User user) {

#### Annotate attribute constraints ####
javax.validation.constraints annotations in the domain object for data validation in the REST service

	@NotBlank(message = "First Name cannot be empty")
	private String firstName;
   
	@NotBlank(message = "Last Name cannot be empty")
	private String lastName;


#### Attribute Validation Exception Handling ####

Handle MethodArgumentNotValidException.class by mapping to BAD_REQUEST Http.status and providing clean field error list

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
		    String fieldName = ((FieldError) error).getField();
		    String errorMessage = error.getDefaultMessage();
            	    errors.put(fieldName, errorMessage);
        	});
        	return errors;
    	}

This results in a Response Body that looks like this:

	{
	    "firstName": "First Name cannot be empty",
	    "lastName": "Last Name cannot be empty"
	}

#### Custom Exception Handling ####
Annotate custom Exception with @ResponseStatus to map them to HttpStatus responses.  When a service called by a controller throws the custom exception Spring will return the correct HttpStatus.51

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public class DuplicateException extends RuntimeException {

### **Spring Security** ###
Create a WebSecurity configuration with a HttpSecurity configuration and an in-memory AuthenticationManagerBuilder  


#### WebSecurityConfig ####
	
	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled=true)
	public class WebSecurityConfig extends WebSecurityConfigurerAdapter{...}



### HttpSecurity ###
In our case any request requires authentication.  Spring will provide a basic authentication form.

	protected void configure(final HttpSecurity http) throws Exception {
		http
			//  To work with Postman needed this line.
		    .csrf().disable()
		    .antMatcher("/**")
		    .authorizeRequests()
		    .anyRequest().authenticated()
		    .and().httpBasic()
		    ;
	}

### In memory AuthenticationManagerBuilder ###
For quick implementation this demo uses an in-memory   with a VIEWER and EDITOR role and three users.  sam is only a VIEWER.  carlos and john are both VIEWER and EDITOR.

	    @Override
	    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	        auth.inMemoryAuthentication()
	         .passwordEncoder(encoder)
	         .withUser("sam").password(encoder.encode("sam")).roles("VIEWER")
	         .and().withUser("carlos").password(encoder.encode("carlos")).roles("VIEWER", "EDITOR")
	         .and().withUser("john").password(encoder.encode("john")).roles("VIEWER", "EDITOR");
	    }

#### Secure Controller Methods ####
To secure individual methods in the controller add the @Secure annotation with a list of roles. NOTE that the role list must start with "ROLE_" for Spring to make the mapping to the role.
	@Secured("ROLE_EDITOR")

### **Data/Model Object Consistency** ###
Use Lombok to reduce the amount of boilerplate code.  

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

There are a lot of packages involved in testing that are provided by spring-boot-starter-test or -web, including:  

* org.assertj
* org.junit.jupiter
* org.mockito
* org.hamcrest
* com.fasterxml.jackson

#### Repository Integration Testing ####
Create integration tests for custom repository methods.  Use @DataJpaTest to standard setup of a persistence layer with an in-memory H2 database and an JPA EntityManager.

#### Service Unit Testing ####
Create unit tests for service methods with business logic to be tested.  Use @MockBean for repositories to isolate testing business logic. 

#### Controller Testing ####
Create unit test for all controller methods to validate the RequestResponse and HttpStatus.  Use MockMvc to Autowire the Spring MVC processing for your Controller.  Use @MokeBean for services to isolate testing of web layer processing of expected Results and Exceptions.  Use @WithMockUser to work with in-memory web security 

	@ExtendWith(SpringExtension.class)
	@WebMvcTest(UserController.class)
	class UserControllerUnitTest {
		
	    @Autowired
	    private MockMvc mvc;
	
	    @MockBean
	    private UserService service;
		
		@Test
		@DisplayName("GET /user/1")
		@WithMockUser(username = "sam", roles = { "VIEWER" })
		void testGetById() throws Exception {
		    User alex = User.builder()
		    		      .id(1)
		    		      .firstName("Alex")
		    		      .lastName("Bell")
		    		      .build();
	
		    doReturn(alex).when(service).get(1);
	
		    mvc.perform(get("/user/{id}", 1)
		      .contentType(MediaType.APPLICATION_JSON))
		      .andExpect(status().isOk())
		      .andExpect(jsonPath("$.id", is(1)))
		      .andExpect(jsonPath("$.firstName", is(alex.getFirstName())))
		      .andExpect(jsonPath("$.lastName", is(alex.getLastName())));
		}
	}


## Credits

Written by Rachel Yellman, yellmanr@outlook.com

## License

Use as you please.