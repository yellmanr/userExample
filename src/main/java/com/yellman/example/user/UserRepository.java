package com.yellman.example.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	 public Optional<User> findByFirstNameAndLastName(String firstName, String lastName);
	 
	 public List<User> findByOrderByLastNameAsc();
}