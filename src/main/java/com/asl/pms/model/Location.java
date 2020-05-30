package com.asl.pms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Location {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String shelf;
    @Getter
    @Setter
    private int rack;

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable
    @JsonIgnore
    private List<Drug> drugs;

    protected Location() {
        this.drugs = new ArrayList<>();
    }

    public Location(String shelf, int rack) {
        this();
        this.shelf = shelf;
        this.rack = rack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return rack == location.rack &&
                Objects.equals(shelf, location.shelf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shelf, rack);
    }
}
