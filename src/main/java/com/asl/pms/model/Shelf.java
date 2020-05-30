package com.asl.pms.model;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "shelf")
public class Shelf {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	
	@NotNull
	@Column(name="name")
	private String name;
	
	@Column(name="details")
	private String details;
	
	@Column(name="zone")
	private String zone;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,   mappedBy = "shelf")
	@JsonIgnore
	private List<Rack> racks;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}
	
	

}
