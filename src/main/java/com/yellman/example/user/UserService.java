package com.yellman.example.user;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
	
  @Autowired
  private UserRepository repository;

  public Optional<User> get(Integer id) {
	  return repository.findById(id);
  }
  
  /*
   * Get all Users ordered ascending by LastName
   */
  public List<User> getAll() {
	  return repository.findByOrderByLastNameAsc();
  }
  
  /**
  * Save User only if User with same first and last name does not exist 
  * @param User user
  * @return User
  * @throws Exception
  */
  public User save(User user) throws Exception {
	  if(repository.findByFirstNameAndLastName(user.getFirstName(), user.getLastName()).isPresent()) {
	  	 throw new Exception ("User with same name exists");
	  }

	  return repository.save(user);
  }
  
  public void delete(Integer id) {
	  repository.deleteById(id);
  }
  
  public Optional<User> getByFirstAndLastName(String firstName, String lastName) {
	  return repository.findByFirstNameAndLastName(firstName, lastName);
  }
  
  
}
