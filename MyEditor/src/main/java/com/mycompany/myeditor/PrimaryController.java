package com.mycompany.myeditor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class PrimaryController {

    @FXML private StackPane vistaCentral;
    @FXML private Button btnArchivos;
    @FXML private Button btnConfiguracion;

    private Parent nodoInicio;
    private Parent nodoConfiguracion;
    private Parent nodoEditor;

    @FXML private TextArea txtEditor;
    @FXML private TextArea txtLineas; 
    @FXML private Label lblTabName;
    @FXML private Label lblCursor;

    @FXML private ComboBox<String> fontFamilySelector;
    @FXML private ComboBox<String> fontSizeSelector;
    @FXML private CheckBox chkLineas;
    @FXML private CheckBox chkAjusteLinea;

    private String rutaArchivoActual = null;
    private boolean inicializado = false;

    // Estilos CSS para los botones activo e inactivo
    private final String ESTILO_ACTIVO = "-fx-background-color: #21262d; -fx-text-fill: white; -fx-alignment: BASELINE_LEFT; -fx-padding: 10; -fx-cursor: hand;";
    private final String ESTILO_INACTIVO = "-fx-background-color: transparent; -fx-text-fill: #8b949e; -fx-alignment: BASELINE_LEFT; -fx-padding: 10; -fx-cursor: hand;";

    @FXML
    public void initialize() {
        if (inicializado) {
            return;
        }
        inicializado = true;

        try {
            nodoInicio = cargarVistaFXML("/fxml/inicio.fxml");
            nodoConfiguracion = cargarVistaFXML("/fxml/configuracion.fxml");
            nodoEditor = cargarVistaFXML("/fxml/editor.fxml");

            if (vistaCentral != null && nodoInicio != null) {
                vistaCentral.getChildren().setAll(nodoInicio);
                actualizarEstiloNavegacion(true);
            }
        } catch (IOException e) {
            System.err.println("Error cargando la estructura modular FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Parent cargarVistaFXML(String rutaClasspath) throws IOException {
        URL resource = getClass().getResource(rutaClasspath);
        if (resource == null) {
            throw new IOException("No se encontró el recurso FXML en el classpath: " + rutaClasspath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setController(this);
        return loader.load();
    }

    private void actualizarEstiloNavegacion(boolean enArchivos) {
        if (btnArchivos != null && btnConfiguracion != null) {
            if (enArchivos) {
                btnArchivos.setStyle(ESTILO_ACTIVO);
                btnConfiguracion.setStyle(ESTILO_INACTIVO);
            } else {
                btnArchivos.setStyle(ESTILO_INACTIVO);
                btnConfiguracion.setStyle(ESTILO_ACTIVO);
            }
        }
    }

    private void configurarComportamientoEditor() {
        if (txtEditor == null) return;

        txtEditor.scrollTopProperty().addListener((obs, oldVal, newVal) -> {
            if (txtLineas != null) txtLineas.setScrollTop(newVal.doubleValue());
        });

        txtEditor.textProperty().addListener((obs, oldVal, newVal) -> actualizarLineasYCursor());
        txtEditor.caretPositionProperty().addListener((obs, oldVal, newVal) -> actualizarLineasYCursor());
    }

    private void configurarComportamientoConfiguracion() {
        if (fontFamilySelector != null && fontFamilySelector.getItems().isEmpty()) {
            fontFamilySelector.getItems().addAll("JetBrains Mono", "Monospaced", "Consolas", "Courier New", "Arial");
            fontFamilySelector.setValue("JetBrains Mono");
        }

        if (fontSizeSelector != null && fontSizeSelector.getItems().isEmpty()) {
            fontSizeSelector.getItems().addAll("12px", "14px", "16px", "18px", "20px", "24px");
            fontSizeSelector.setValue("14px");
        }

        if (chkLineas != null) {
            chkLineas.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (txtLineas != null) {
                    txtLineas.setVisible(newVal);
                    txtLineas.setManaged(newVal);
                }
            });
        }
        
        if (chkAjusteLinea != null) {
            chkAjusteLinea.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (txtEditor != null) txtEditor.setWrapText(newVal);
            });
        }
    }

    @FXML
    private void mostrarInicio() {
        if (vistaCentral != null && nodoInicio != null) {
            vistaCentral.getChildren().setAll(nodoInicio);
            actualizarEstiloNavegacion(true);
        }
    }

    @FXML
    private void mostrarConfiguracion() {
        if (vistaCentral != null && nodoConfiguracion != null) {
            vistaCentral.getChildren().setAll(nodoConfiguracion);
            configurarComportamientoConfiguracion();
            actualizarEstiloNavegacion(false);
        }
    }

    private void mostrarEditor() {
        if (vistaCentral != null && nodoEditor != null) {
            vistaCentral.getChildren().setAll(nodoEditor);
            configurarComportamientoEditor();
            actualizarEstiloNavegacion(true);
        }
    }

    @FXML
    private void nuevoDocumento() {
        rutaArchivoActual = null;
        mostrarEditor();
        
        if (txtEditor != null) txtEditor.setText("");
        if (lblTabName != null) lblTabName.setText("sin_titulo.txt");
        actualizarLineasYCursor();
    }

    @FXML
    private void abrirArchivo() {
        Stage stage = (Stage) vistaCentral.getScene().getWindow();
        Documento doc = GestorArchivos.abrirArchivo(stage);
        if (doc != null) {
            mostrarEditor();
            rutaArchivoActual = doc.getRuta();
            if (txtEditor != null) txtEditor.setText(doc.getContenido());
            if (lblTabName != null) lblTabName.setText(doc.getNombre());
            actualizarLineasYCursor();
        }
    }

    @FXML
    private void guardarArchivo() {
        Stage stage = (Stage) vistaCentral.getScene().getWindow();
        String contenido = (txtEditor != null) ? txtEditor.getText() : "";
        Documento doc = GestorArchivos.guardarArchivo(stage, rutaArchivoActual, contenido);
        if (doc != null) {
            rutaArchivoActual = doc.getRuta();
            if (lblTabName != null) lblTabName.setText(doc.getNombre());
        }
    }

    @FXML
    private void aplicarTipografia() {
        if (fontFamilySelector == null || fontSizeSelector == null || txtEditor == null || txtLineas == null) return;
        
        String fuente = fontFamilySelector.getValue();
        String tamano = fontSizeSelector.getValue();
        if (fuente != null && tamano != null) {
            String fontStyle = "-fx-font-family: '" + fuente + "'; -fx-font-size: " + tamano + ";";
            txtEditor.setStyle(fontStyle + " -fx-text-fill: #c9d1d9;");
            txtLineas.setStyle(fontStyle + " -fx-text-fill: #484f58; -fx-alignment: TOP_RIGHT;");
        }
    }

    private void actualizarLineasYCursor() {
        if (txtEditor == null || txtLineas == null) return;

        String texto = txtEditor.getText();
        int totalLineas = texto.split("\n", -1).length;
        
        StringBuilder numeros = new StringBuilder();
        for (int i = 1; i <= totalLineas; i++) {
            numeros.append(i).append("\n");
        }
        txtLineas.setText(numeros.toString());

        int posicionCaret = Math.min(txtEditor.getCaretPosition(), texto.length());
        String textoHastaCursor = texto.substring(0, posicionCaret);
        String[] lineas = textoHastaCursor.split("\n", -1);
        
        int numeroLinea = lineas.length;
        int numeroColumna = lineas[lineas.length - 1].length() + 1;

        if (lblCursor != null) {
            lblCursor.setText("Línea " + numeroLinea + ", Columna " + numeroColumna);
        }
    }
}