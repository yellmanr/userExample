package com.yellman.example.user;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(
		uniqueConstraints=
	        @UniqueConstraint(columnNames={"firstName", "lastName"})
	)
public class User {
	
    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank(message = "First Name cannot be empty")
    @NonNull
	private String firstName;
   
    @NotBlank(message = "Last Name cannot be empty")
    @NonNull
	private String lastName;

}
