package com.se.onprem.dto.uaa;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User
{
	private String username;
	private String password;

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("login")
	private String login;
	@JsonProperty("firstName")
	private String firstName;
	@JsonProperty("lastName")
	private String lastName;
	@JsonProperty("email")
	private String email;
	@JsonProperty("imageUrl")
	private String imageUrl;
	@JsonProperty("activated")
	private Boolean activated;
	@JsonProperty("langKey")
	private String langKey;
	@JsonProperty("createdBy")
	private String createdBy;
	@JsonProperty("createdDate")
	private Date createdDate;
	@JsonProperty("lastModifiedBy")
	private String lastModifiedBy;
	@JsonProperty("lastModifiedDate")
	private Date lastModifiedDate;
	@JsonProperty("authorities")
	private List<String> authorities;
}
