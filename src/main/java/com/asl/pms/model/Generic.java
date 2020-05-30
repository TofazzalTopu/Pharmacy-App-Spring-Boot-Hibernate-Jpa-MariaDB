package com.asl.pms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Generic {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String name;

    @Getter
    @Setter
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable
    @JsonIgnore
    private List<Drug> drugs;

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable
    @JsonIgnore
    private List<Company> companies;

    protected Generic() {
        this.drugs = new ArrayList<>();
        this.companies = new ArrayList<>();
    }

    public Generic(Long id) {
        this();
        this.id = id;
    }

    public Generic(String name) {
        this();
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Generic generic = (Generic) o;
        return Objects.equals(name, generic.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
