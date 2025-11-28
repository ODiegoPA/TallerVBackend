package com.example.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Matriculacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "alumno_id")
    private User alumno;

    @ManyToOne
    @JoinColumn(name = "semestre_materia_id")
    private SemestreMateria semestreMateria;

    @OneToMany(mappedBy = "matriculacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Nota> notas = new ArrayList<>();

    @Column
    private Integer faltas;

    @Column
    private Boolean estaAprobado;

    @Column
    private Integer notaFinal;

    @Column
    private Boolean estaConsolidado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAlumno() {
        return alumno;
    }

    public void setAlumno(User alumno) {
        this.alumno = alumno;
    }

    public SemestreMateria getSemestreMateria() {
        return semestreMateria;
    }

    public void setSemestreMateria(SemestreMateria semestreMateria) {
        this.semestreMateria = semestreMateria;
    }

    public List<Nota> getNotas() {
        return notas;
    }

    public void setNotas(List<Nota> notas) {
        this.notas = notas;
    }

    public Integer getFaltas() {
        return faltas;
    }

    public void setFaltas(Integer faltas) {
        this.faltas = faltas;
    }

    public Boolean getEstaAprobado() {
        return estaAprobado;
    }

    public void setEstaAprobado(Boolean estaAprobado) {
        this.estaAprobado = estaAprobado;
    }

    public Integer getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Integer notaFinal) {
        this.notaFinal = notaFinal;
    }

    public Boolean getEstaConsolidado() {
        return estaConsolidado;
    }

    public void setEstaConsolidado(Boolean estaConsolidado) {
        this.estaConsolidado = estaConsolidado;
    }
}
