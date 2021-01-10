package com.yellman.example.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    public User get(@PathVariable Integer id) {
        return userService.get(id);
    }
    
    @PostMapping("/user")
    @Secured("ROLE_EDITOR")
    public ResponseEntity<User> create(@Valid @RequestBody User user) throws Exception {
        return new ResponseEntity<User>(userService.save(user), HttpStatus.CREATED);
    }
	
    @PutMapping("/user/{id}")
    @Secured("ROLE_EDITOR")
    public User update(@Valid @RequestBody User user, @PathVariable Integer id) throws Exception {
        return userService.save(user);
    }
    
    @DeleteMapping("/user/{id}")
    @Secured("ROLE_EDITOR")
    public void delete(@PathVariable Integer id) throws Exception {
        userService.delete(id);
    }
    
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
    
    
} 