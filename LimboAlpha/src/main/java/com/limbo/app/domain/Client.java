package com.limbo.app.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "clients")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class Client {

    @Id
    @Column(name = "CLIENT_ID")
    @GeneratedValue
    private Integer id;

    @Column(name = "NAME", unique = true, nullable = false)
    
    private String name;

    @Column(name = "PVN_NUMBER", unique = true, nullable = false, length = 21)
    private String pvnNumber;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "ISO_COUNTRY")
    private String ISOCountry;
    
    @Column(name = "CITY")
    private String city;     
    
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPvnNumber() {
		return pvnNumber;
	}

	public void setPvnNumber(String pvnNumber) {
		this.pvnNumber = pvnNumber;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getISOCountry() {
		return ISOCountry;
	}

	public void setISOCountry(String ISOCountry) {
		this.ISOCountry = ISOCountry;
	}	

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}