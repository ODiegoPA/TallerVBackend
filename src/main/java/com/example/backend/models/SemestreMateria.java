package com.example.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class SemestreMateria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="materia_id")
    private Materia materia;

    @ManyToOne
    @JoinColumn(name="semestre_id")
    private Semestre semestre;

    @ManyToOne
    @JoinColumn(name="docente_id")
    private User docente;

    @ManyToOne
    @JoinColumn(name="modalidad_id")
    private Modalidad modalidad;

    @OneToMany(mappedBy = "semestreMateria")
    @JsonBackReference
    private List<Matriculacion> matriculaciones;

    @Column
    private Integer cupos;

    @Column
    private boolean estaActiva;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public void setSemestre(Semestre semestre) {
        this.semestre = semestre;
    }

    public User getDocente() {
        return docente;
    }

    public void setDocente(User docente) {
        this.docente = docente;
    }

    public Integer getCupos() {
        return cupos;
    }

    public void setCupos(Integer cupos) {
        this.cupos = cupos;
    }

    public boolean isEstaActiva() {
        return estaActiva;
    }

    public void setEstaActiva(boolean estaActiva) {
        this.estaActiva = estaActiva;
    }


    public Modalidad getModalidad() {
        return modalidad;
    }

    public void setModalidad(Modalidad modalidad) {
        this.modalidad = modalidad;
    }

    public List<Matriculacion> getMatriculaciones() {
        return matriculaciones;
    }

    public void setMatriculaciones(List<Matriculacion> matriculaciones) {
        this.matriculaciones = matriculaciones;
    }
}
