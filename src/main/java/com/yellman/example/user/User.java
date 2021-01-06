package com.yellman.example.user;


import javax.persistence.*;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @NonNull
	private String firstName;
    @NonNull
	private String lastName;

}
