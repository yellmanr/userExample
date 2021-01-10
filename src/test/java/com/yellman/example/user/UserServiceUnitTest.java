package com.yellman.example.user;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.yellman.example.user.exception.DuplicateException;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@Slf4j
class UserServiceUnitTest {
	
    @TestConfiguration
    static class UserServiceTestContextConfiguration {
 
        @Bean
        public UserService userService() {
            return new UserService();
        }
    }

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;


	@Test
	@DisplayName ("Test Find User By Name Success")
	public void testFindByName() {
		User existingUser = new User("Existing", "User");
		
        Mockito.when(userRepository.findByFirstNameAndLastName(existingUser.getFirstName(), existingUser.getLastName()))
        .thenReturn(Optional.of(existingUser));
        
	    User found = userService.getByFirstAndLastName(existingUser.getFirstName(), existingUser.getLastName()).get();
	 
	    assertThat(found.getFirstName())
	      .isEqualTo(existingUser.getFirstName());
	    assertThat(found.getLastName())
	      .isEqualTo(existingUser.getLastName());
	 }

	@Test
	@DisplayName ("Test Save User Success")
	public void testSave() throws Exception {
		User unique = new User("Not", "Existing");	
        
        Mockito.when(userRepository.findByFirstNameAndLastName(unique.getFirstName(), unique.getLastName()))
          .thenReturn(Optional.empty());

		Mockito.when(userRepository.save(Mockito.any(User.class)))
          .thenReturn(unique);
				
		User saved = userService.save(unique);

	    assertThat(saved.getFirstName())
	      .isEqualTo(unique.getFirstName());
	    assertThat(saved.getLastName())
	      .isEqualTo(unique.getLastName());
	}
		
	
	@Test
	@DisplayName ("Test Save User Duplicate Error")
	public void testSaveDuplicateUser() {
		User existingUser = new User("Existing", "User");
		
        Mockito.when(userRepository.findByFirstNameAndLastName(existingUser.getFirstName(), existingUser.getLastName()))
        .thenReturn(Optional.of(existingUser));
		
	    Assertions.assertThrows(DuplicateException.class, () -> {
	    	userService.save(existingUser);
	      });
	}
}