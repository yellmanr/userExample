package com.yellman.example.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@Slf4j
class UserControllerUnitTest {
	
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService service;

	@Test
	@DisplayName("GET /user")
	@WithMockUser(username = "sam", roles = { "VIEWER" })
	void testGetAll() throws Exception {
	    User alex = new User("Alex", "Bell");

	    doReturn(Lists.newArrayList(alex)).when(service).getAll();

	    mvc.perform(get("/user")
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$", hasSize(1)))
	      .andExpect(jsonPath("$[0].firstName", is(alex.getFirstName())));
	}
	
	@Test
	@DisplayName("GET /user NOT AUTHORIZED")
	void testGetAllNoAuth() throws Exception {
	    mvc.perform(get("/user")
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().isUnauthorized());
	}
	
	@Test
	@DisplayName("GET /user/1")
	@WithMockUser(username = "sam", roles = { "VIEWER" })
	void testGetById() throws Exception {
	    User alex = User.builder()
	    		      .id(1)
	    		      .firstName("Alex")
	    		      .lastName("Bell")
	    		      .build();

	    doReturn(Optional.of(alex)).when(service).get(1);

	    mvc.perform(get("/user/{id}", 1)
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$.id", is(1)))
	      .andExpect(jsonPath("$.firstName", is(alex.getFirstName())))
	      .andExpect(jsonPath("$.lastName", is(alex.getLastName())));
	}
	
	@Test
	@DisplayName("GET /user/1 NOT FOUND")
	@WithMockUser(username = "sam", roles = { "VIEWER" })
	void testGetByIdNotFound() throws Exception {

	    doReturn(Optional.empty()).when(service).get(1);

	    mvc.perform(get("/user/{id}", 1)
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().isNotFound());;
	}
	
	@Test
	@DisplayName("POST /user")
	@WithMockUser(username = "john", roles = { "EDITOR" })
	void testCreate() throws Exception {

		User userToPost = new User("Alex", "Bell");
		User userToReturn = User.builder()
								  .id(1)
								  .firstName("Alex")
								  .lastName("Bell")
								  .build();
		
		log.info("********************************* userToPost" + userToPost);
		log.info("********************************* userToPost" + userToReturn);
		
		doReturn(userToReturn).when(service).save(any());
				
	    mvc.perform(post("/user")
	       .contentType(MediaType.APPLICATION_JSON)
	       .content(asJsonString(userToPost)))
	       .andExpect(status().isCreated())
	       .andExpect(jsonPath("$.id", is(1)))
	       .andExpect(jsonPath("$.firstName", is(userToReturn.getFirstName())))
	       .andExpect(jsonPath("$.lastName", is(userToReturn.getLastName())));
	}
	
	@Test
	@DisplayName("POST /user FORBIDDEN")
	@WithMockUser(username = "sam", roles = { "VIEWER" })
	void testCreateNotEditor() throws Exception {

		User userToPost = new User("Alex", "Bell");
				
	    mvc.perform(post("/user")
	       .contentType(MediaType.APPLICATION_JSON)
	       .content(asJsonString(userToPost)))
	       .andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("POST /user NO LASTNAME")
	@WithMockUser(username = "john", roles = { "EDITOR" })
	void testCreateMissingData() throws Exception {
		
		// Creates JSON to submit user without password
		// Creating User object without first and last name causes error
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("firstName", "Alex");

		doThrow(new Exception ("User with same name exists")).when(service).save(any());
		
	    mvc.perform(post("/user")
	       .contentType(MediaType.APPLICATION_JSON)
	       .content(mapper.writeValueAsString(rootNode)))
	       .andExpect(status().isCreated());
	}
	
	@Test
	@DisplayName("PUT /user/1")
	@WithMockUser(username = "john", roles = { "EDITOR" })
	void testUpdate() throws Exception {

		User userToPut = new User("Alex", "Bell");
		User userToReturn = User.builder()
								  .id(1)
								  .firstName("Alex")
								  .lastName("Bell")
								  .build();
		
		log.info("********************************* userToPost" + userToPut);
		log.info("********************************* userToPost" + userToReturn);
		
		doReturn(userToReturn).when(service).save(any());
				
	    mvc.perform(put("/user/{id}", 1)
	       .contentType(MediaType.APPLICATION_JSON)
	       .content(asJsonString(userToPut)))
	       .andExpect(status().isCreated())
	       .andExpect(jsonPath("$.id", is(1)))
	       .andExpect(jsonPath("$.firstName", is(userToReturn.getFirstName())))
	       .andExpect(jsonPath("$.lastName", is(userToReturn.getLastName())));;
	}

	
    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}