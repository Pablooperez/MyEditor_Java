package com.mycompany.myeditor;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GestorArchivos {

    private static final String ARCHIVO_RECIENTES = System.getProperty("user.home") + File.separator + ".myeditor_recientes.txt";
    private static final String ARCHIVO_ESTILOS = System.getProperty("user.home") + File.separator + ".myeditor_estilos.properties";
    private static final int MAX_RECIENTES = 5;

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
                guardarReciente(archivoSeleccionado.getAbsolutePath());
                return new Documento(archivoSeleccionado.getAbsolutePath(), archivoSeleccionado.getName(), contenido);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Documento leerArchivoDirecto(String ruta) {
        File archivo = new File(ruta);
        if (archivo.exists()) {
            try {
                String contenido = Files.readString(Paths.get(ruta), StandardCharsets.UTF_8);
                guardarReciente(ruta);
                return new Documento(ruta, archivo.getName(), contenido);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

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
                guardarReciente(archivoAGuardar.getAbsolutePath());
                return new Documento(archivoAGuardar.getAbsolutePath(), archivoAGuardar.getName(), contenido);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<String> cargarRecientes() {
        List<String> recientes = new ArrayList<>();
        Path path = Paths.get(ARCHIVO_RECIENTES);
        if (Files.exists(path)) {
            try {
                List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
                for (String linea : lineas) {
                    linea = linea.trim();
                    if (!linea.isEmpty() && Files.exists(Paths.get(linea)) && !recientes.contains(linea)) {
                        recientes.add(linea);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return recientes;
    }

    public static void guardarReciente(String ruta) {
        if (ruta == null || ruta.isEmpty()) return;

        List<String> recientes = cargarRecientes();
        recientes.remove(ruta);
        recientes.add(0, ruta);

        if (recientes.size() > MAX_RECIENTES) {
            recientes = recientes.subList(0, MAX_RECIENTES);
        }

        try {
            Files.write(Paths.get(ARCHIVO_RECIENTES), recientes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Persistencia de metadatos de formato/fuente por archivo
    public static void guardarEstiloArchivo(String ruta, String fuente, String tamano) {
        if (ruta == null || ruta.isEmpty()) return;
        Properties props = cargarTodosLosEstilos();
        props.setProperty(ruta + ".fuente", fuente);
        props.setProperty(ruta + ".tamano", tamano);
        try (OutputStream out = new FileOutputStream(ARCHIVO_ESTILOS)) {
            props.store(out, "Configuraciones de tipografía por archivo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] cargarEstiloArchivo(String ruta) {
        if (ruta == null || ruta.isEmpty()) return null;
        Properties props = cargarTodosLosEstilos();
        String fuente = props.getProperty(ruta + ".fuente");
        String tamano = props.getProperty(ruta + ".tamano");
        if (fuente != null && tamano != null) {
            return new String[]{fuente, tamano};
        }
        return null;
    }

    private static Properties cargarTodosLosEstilos() {
        Properties props = new Properties();
        File file = new File(ARCHIVO_ESTILOS);
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;
    }
}