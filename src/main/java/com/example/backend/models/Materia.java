package com.example.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    public long id;

    @Column
    public String nombre;

    @OneToMany(mappedBy = "materia")
    @JsonBackReference
    private List<SemestreMateria> semestreMaterias;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<SemestreMateria> getSemestreMaterias() {
        return semestreMaterias;
    }

    public void setSemestreMaterias(List<SemestreMateria> semestreMaterias) {
        this.semestreMaterias = semestreMaterias;
    }
}
