package com.yellman.example.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryIntegrationTest {
	
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

	@Test
	public void testfindByFirstNameAndLastName() {
	    // given
	    User alex = new User("Alex", "Bell");
	    entityManager.persist(alex);
	    entityManager.flush();

	    // when
	    User found = userRepository.findByFirstNameAndLastName(alex.getFirstName(), alex.getLastName()).get();

	    // then
	    assertThat(found.getFirstName())
	      .isEqualTo(alex.getFirstName());
	    assertThat(found.getLastName())
	      .isEqualTo(alex.getLastName());
	}


}