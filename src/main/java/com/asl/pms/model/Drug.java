package com.asl.pms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Drug {

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Setter
	@Column(nullable = false)
	private String name;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
//	@Transient
	private Generic generic;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	//@Transient
	private DosageForm dosageForm;

	@Getter
	@Setter
	private String strength;

	@Getter
	@Setter
	@Column(name ="pack_size")
	private String uom;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	//@Transient
	private Company company;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	//@Transient
	private Location location;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore	
	private Rack rack;

	@Getter
	@Setter
	private double price;

	@Getter
	@Setter
	private int safetyMargin;


	public Drug() {
	}

	public Drug(Generic generic, DosageForm dosageForm) {
		this.generic = generic;
		this.dosageForm = dosageForm;
	}

	public Drug(String name, Generic generic, DosageForm dosageForm, String strength, Company company, double price) {
		this(generic, dosageForm);
		this.name = name;
		this.strength = strength;
		this.company = company;
		this.price = price;
	}

	public Drug(String name, Generic generic, DosageForm dosageForm, String strength, Company company,
			Location location, double price) {
		this(name, generic, dosageForm, strength, company, price);
		this.location = location;
		
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Drug drug = (Drug) o;
		return Objects.equals(name, drug.name) && Objects.equals(strength, drug.strength);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, strength);
	}

	@Override
	public String toString() {
		return "Drug [id=" + id + ", name=" + name 
				+ ", strength=" + strength + ", uom=" + uom + ", location=" + location
				+ ", rack=" + rack + ", price=" + price + ", safetyMargin=" + safetyMargin + "]";
	}


	
}
