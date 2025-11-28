package com.example.backend.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Gestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    public long id;

    @Column
    public Integer ano;
    @OneToMany(
            mappedBy = "gestion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    public List<Semestre> semestres = new ArrayList<>();

    @OneToMany(
            mappedBy = "gestion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    public Set<Modalidad> modalidades = new HashSet<>();

    public void addSemestre(Semestre s) {
        semestres.add(s);
        s.setGestion(this);
    }
    public void removeSemestre(Semestre s) {
        semestres.remove(s);
        s.setGestion(null);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public List<Semestre> getSemestres() {
        return semestres;
    }

    public void setSemestres(List<Semestre> semestres) {
        this.semestres = semestres;
    }

    public Set<Modalidad> getModalidades() {
        return modalidades;
    }

    public void setModalidades(Set<Modalidad> modalidades) {
        this.modalidades = modalidades;
    }
}
