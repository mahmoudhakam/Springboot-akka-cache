package com.se.onprem.dto.ws;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class MatchFiltersRequestDTO
{

	private Set<String> matchFilters;
	private Set<String> notMatchFilters;
	private boolean not;

	public MatchFiltersRequestDTO()
	{
		this.matchFilters = new HashSet<>();
		this.notMatchFilters = new HashSet<>();
		this.not = false;
	}

	public MatchFiltersRequestDTO(Set<String> matchFilters, boolean not)
	{
		super();
		this.matchFilters = matchFilters;
		this.not = not;
	}

}
