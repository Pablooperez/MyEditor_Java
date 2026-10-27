/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myeditor;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
/**
 *
 * @author pablo.perez
 */
public class GestorArchivos {

    // Equivalente a 'dialog:openFile' y 'file:read'
    public static Documento abrirArchivo(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir archivo de texto");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos de texto", "*.txt", "*.md", "*.js", "*.html", "*.css", "*.json")
        );
        
        File archivoSeleccionado = fileChooser.showOpenDialog(stage);
        if (archivoSeleccionado != null) {
            try {
                String contenido = Files.readString(Paths.get(archivoSeleccionado.getAbsolutePath()), StandardCharsets.UTF_8);
                return new Documento(archivoSeleccionado.getAbsolutePath(), archivoSeleccionado.getName(), contenido);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Equivalente parcial a 'file:read' directo para Recientes
    public static Documento leerArchivoDirecto(String ruta) {
        File archivo = new File(ruta);
        if (archivo.exists()) {
            try {
                String contenido = Files.readString(Paths.get(ruta), StandardCharsets.UTF_8);
                return new Documento(ruta, archivo.getName(), contenido);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Equivalente a 'dialog:saveFile'
    public static Documento guardarArchivo(Stage stage, String rutaActual, String contenido) {
        File archivoAGuardar;

        if (rutaActual == null || rutaActual.isEmpty()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar archivo de texto");
            fileChooser.setInitialFileName("sin_titulo.txt");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt", "*.md", "*.js", "*.html", "*.css", "*.json")
            );
            archivoAGuardar = fileChooser.showSaveDialog(stage);
        } else {
            archivoAGuardar = new File(rutaActual);
        }

        if (archivoAGuardar != null) {
            try {
                Files.writeString(Paths.get(archivoAGuardar.getAbsolutePath()), contenido, StandardCharsets.UTF_8);
                return new Documento(archivoAGuardar.getAbsolutePath(), archivoAGuardar.getName(), contenido);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
