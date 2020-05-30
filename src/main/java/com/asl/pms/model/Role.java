package com.asl.pms.model;

import javax.persistence.*;

@Entity(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String name;


    public Role() {
    }


    public void setId(Long id) {
        Id = id;
    }

    public Role(String name) {
        this.name = name;
    }

    public Long getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                '}';
    }
}
