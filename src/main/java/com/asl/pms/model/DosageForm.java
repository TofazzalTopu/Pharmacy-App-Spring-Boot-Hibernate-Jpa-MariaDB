package com.asl.pms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class DosageForm {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(unique=true, nullable = false)
    private String name;

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable
    @JsonIgnore
    private List<Drug> drugs;

    protected DosageForm() {
        this.drugs = new ArrayList<>();
    }

    public DosageForm(Long id) {
        this();
        this.id = id;
    }

    public DosageForm(String name) {
        this();
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DosageForm that = (DosageForm) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
