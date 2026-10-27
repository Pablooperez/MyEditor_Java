/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myeditor;

/**
 *
 * @author pablo.perez
 */
public class Documento {
    private String ruta;
    private String nombre;
    private String contenido;

    public Documento(String ruta, String nombre, String contenido) {
        this.ruta = ruta;
        this.nombre = nombre;
        this.contenido = contenido;
    }

    // Getters y Setters
    public String getRuta() { return ruta; }
    public void setRuta(String ruta) { this.ruta = ruta; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
}
