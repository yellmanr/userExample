package com.yellman.example.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;

    @GetMapping("/user")
    @Secured("ROLE_VIEWER")
    public List<User> getAll() {
        return userService.getAll();
    }
    
    @GetMapping("/user/{id}")
    @Secured("ROLE_VIEWER")
    public Optional<User> get(@PathVariable Integer id) {
        return userService.get(id);
    }
    
    @PostMapping("/user")
    @Secured("ROLE_EDITOR")
    public User create(@RequestBody User user) throws Exception {
        return userService.save(user);
    }
	
    @PutMapping("/user/{id}")
    @Secured("ROLE_EDITOR")
    public User update(@RequestBody User user, @PathVariable Integer id) throws Exception {
        return userService.save(user);
    }
    
    @DeleteMapping("/user/{id}")
    @Secured("ROLE_EDITOR")
    public void delete(@PathVariable Integer id) throws Exception {
        userService.delete(id);
    }
}