package com.mycompany.myeditor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class PrimaryController {

    @FXML private StackPane vistaCentral;
    @FXML private VBox panelIzquierdo;
    @FXML private Button btnArchivos;
    @FXML private Button btnConfiguracion;
    @FXML private HBox barraEstado;

    private Parent nodoInicio;
    private Parent nodoConfiguracion;
    private Parent nodoEditor;

    @FXML private TextArea txtEditor;
    @FXML private TextArea txtLineas; 
    @FXML private Label lblTabName;
    @FXML private Label lblStatusName;
    @FXML private Label lblStatusState;
    @FXML private Label lblCursor;

    private String rutaArchivoActual = null;
    private boolean inicializado = false;

    private final String ESTILO_ACTIVO = "-fx-background-color: #1e2338; -fx-text-fill: #60a5fa; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 14; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold;";
    private final String ESTILO_INACTIVO = "-fx-background-color: transparent; -fx-text-fill: #9ca3af; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 14; -fx-background-radius: 6; -fx-cursor: hand;";

    private final String CSS_ANTI_ARTEFACTOS = "data:text/css," +
        ".text-area%20%7B%20-fx-background-color%3A%20transparent%20!important%3B%20-fx-background-insets%3A%200%20!important%3B%20-fx-padding%3A%200%20!important%3B%20-fx-box-border%3A%20transparent%20!important%3B%20-fx-focus-color%3A%20transparent%20!important%3B%20-fx-faint-focus-color%3A%20transparent%20!important%3B%20%7D%20" +
        ".text-area%20.scroll-pane%20%7B%20-fx-background-color%3A%20transparent%20!important%3B%20-fx-background-insets%3A%200%20!important%3B%20-fx-padding%3A%200%20!important%3B%20%7D%20" +
        ".text-area%20.scroll-pane%20.viewport%20%7B%20-fx-background-color%3A%20transparent%20!important%3B%20%7D%20" +
        ".text-area%20.scroll-pane%20.content%20%7B%20-fx-padding%3A%206px%208px%20!important%3B%20%7D";

    @FXML
    public void initialize() {
        if (inicializado) return;
        inicializado = true;

        try {
            nodoInicio = cargarVistaFXML("/fxml/inicio.fxml");
            nodoConfiguracion = cargarVistaFXML("/fxml/configuracion.fxml");
            nodoEditor = cargarVistaFXML("/fxml/editor.fxml");

            if (nodoEditor != null) {
                nodoEditor.getStylesheets().add(CSS_ANTI_ARTEFACTOS);
            }

            Platform.runLater(() -> mostrarInicio());

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

    private void actualizarVisibilidadBarraEstado(boolean visible) {
        if (barraEstado != null) {
            barraEstado.setVisible(visible);
            barraEstado.setManaged(visible);
        }
    }

    private void actualizarVisibilidadNavegacion(boolean visible) {
        if (panelIzquierdo != null) {
            panelIzquierdo.setVisible(visible);
            panelIzquierdo.setManaged(visible);
        }
    }

    @SuppressWarnings("unchecked")
    private void actualizarRecientesUI() {
        if (nodoInicio == null) return;
        VBox contenedorRecientes = (VBox) nodoInicio.lookup("#contenedorRecientes");
        if (contenedorRecientes == null) return;

        contenedorRecientes.getChildren().clear();

        Label lblTitulo = new Label("🕒 RECIENTES");
        lblTitulo.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px; -fx-font-weight: bold;");
        contenedorRecientes.getChildren().add(lblTitulo);

        List<String> recientes = GestorArchivos.cargarRecientes();

        if (recientes.isEmpty()) {
            Label lblVacio = new Label("No hay archivos recientes");
            lblVacio.setStyle("-fx-text-fill: #484f58; -fx-font-style: italic;");
            contenedorRecientes.getChildren().add(lblVacio);
        } else {
            for (String ruta : recientes) {
                File f = new File(ruta);

                Button btnReciente = new Button();
                btnReciente.setMaxWidth(Double.MAX_VALUE);
                btnReciente.setStyle("-fx-background-color: #141824; -fx-border-color: #22283a; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12; -fx-cursor: hand; -fx-alignment: BASELINE_LEFT;");

                HBox contenidoBtn = new HBox(10);
                contenidoBtn.setAlignment(Pos.CENTER_LEFT);

                Label icono = new Label("📄");
                icono.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 14px;");

                VBox textos = new VBox(2);
                Label lblNombre = new Label(f.getName());
                lblNombre.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

                Label lblRuta = new Label(f.getAbsolutePath());
                lblRuta.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px;");

                textos.getChildren().addAll(lblNombre, lblRuta);
                contenidoBtn.getChildren().addAll(icono, textos);
                btnReciente.setGraphic(contenidoBtn);

                btnReciente.setOnAction(e -> abrirRecienteDirecto(ruta));

                contenedorRecientes.getChildren().add(btnReciente);
            }
        }
    }

    private void abrirRecienteDirecto(String ruta) {
        Documento doc = GestorArchivos.leerArchivoDirecto(ruta);
        if (doc != null) {
            mostrarEditor();
            rutaArchivoActual = doc.getRuta();
            cargarEstiloOEstablecerPorDefecto(doc.getRuta());
            if (txtEditor != null) txtEditor.setText(doc.getContenido());
            if (lblTabName != null) lblTabName.setText(doc.getNombre());
            if (lblStatusName != null) lblStatusName.setText(doc.getNombre());
            if (lblStatusState != null) lblStatusState.setText("Guardado");
            actualizarLineasYCursor();
        } else {
            actualizarRecientesUI();
        }
    }

    @SuppressWarnings("unchecked")
    private void configurarComportamientoEditor() {
        if (nodoEditor == null) return;

        ComboBox<String> cbFuenteEditor = (ComboBox<String>) nodoEditor.lookup("#cbFuenteEditor");
        ComboBox<String> cbTamanoEditor = (ComboBox<String>) nodoEditor.lookup("#cbTamanoEditor");

        if (cbFuenteEditor != null && cbFuenteEditor.getItems().isEmpty()) {
            cbFuenteEditor.getItems().addAll("JetBrains Mono", "Monospaced", "Consolas", "Courier New", "Arial");
            cbFuenteEditor.setValue("JetBrains Mono");
        }

        if (cbTamanoEditor != null && cbTamanoEditor.getItems().isEmpty()) {
            cbTamanoEditor.getItems().addAll("12 px", "14 px", "16 px", "18 px", "20 px", "24 px");
            cbTamanoEditor.setValue("14 px");
        }

        if (txtEditor != null) {
            txtEditor.scrollTopProperty().addListener((obs, oldVal, newVal) -> {
                if (txtLineas != null) txtLineas.setScrollTop(newVal.doubleValue());
            });

            txtEditor.textProperty().addListener((obs, oldVal, newVal) -> {
                if (lblStatusState != null) lblStatusState.setText("Modificado");
                actualizarLineasYCursor();
            });

            txtEditor.caretPositionProperty().addListener((obs, oldVal, newVal) -> actualizarLineasYCursor());
        }
    }

    @SuppressWarnings("unchecked")
    private void configurarComportamientoConfiguracion() {
        if (nodoConfiguracion == null) return;

        ComboBox<String> fontFamilySelector = (ComboBox<String>) nodoConfiguracion.lookup("#fontFamilySelector");
        ComboBox<String> fontSizeSelector = (ComboBox<String>) nodoConfiguracion.lookup("#fontSizeSelector");
        ComboBox<String> cbAlgoritmoCifrado = (ComboBox<String>) nodoConfiguracion.lookup("#cbAlgoritmoCifrado");
        CheckBox chkLineas = (CheckBox) nodoConfiguracion.lookup("#chkLineas");
        CheckBox chkAjusteLinea = (CheckBox) nodoConfiguracion.lookup("#chkAjusteLinea");

        if (fontFamilySelector != null && fontFamilySelector.getItems().isEmpty()) {
            fontFamilySelector.getItems().addAll("JetBrains Mono", "Monospaced", "Consolas", "Courier New", "Arial");
            fontFamilySelector.setValue("JetBrains Mono");
        }

        if (fontSizeSelector != null && fontSizeSelector.getItems().isEmpty()) {
            fontSizeSelector.getItems().addAll("12 px", "14 px (Por defecto)", "16 px", "18 px", "20 px", "24 px");
            fontSizeSelector.setValue("14 px (Por defecto)");
        }

        if (cbAlgoritmoCifrado != null && cbAlgoritmoCifrado.getItems().isEmpty()) {
            cbAlgoritmoCifrado.getItems().addAll("AES-256 (Con Contraseña)", "Cifrado César (+3)", "Base64 (Ofuscación)");
            cbAlgoritmoCifrado.setValue("AES-256 (Con Contraseña)");
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

    private String obtenerAlgoritmoSeleccionado() {
        if (nodoConfiguracion != null) {
            @SuppressWarnings("unchecked")
            ComboBox<String> cbAlgoritmo = (ComboBox<String>) nodoConfiguracion.lookup("#cbAlgoritmoCifrado");
            if (cbAlgoritmo != null && cbAlgoritmo.getValue() != null) {
                return cbAlgoritmo.getValue();
            }
        }
        return "AES-256 (Con Contraseña)";
    }

    @FXML
    private void cifrarTexto() {
        if (txtEditor == null || txtEditor.getText().isEmpty()) return;

        String algoritmo = obtenerAlgoritmoSeleccionado();
        String textoOriginal = txtEditor.getText();

        try {
            if (algoritmo.contains("AES-256")) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Cifrar contenido (AES-256)");
                dialog.setHeaderText("Introduce una contraseña para cifrar este archivo:");
                dialog.setContentText("Contraseña:");
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent() && !result.get().trim().isEmpty()) {
                    String textoCifrado = GestorCifrado.cifrarAES(textoOriginal, result.get().trim());
                    txtEditor.setText(textoCifrado);
                }
            } else if (algoritmo.contains("Cés")) {
                txtEditor.setText(GestorCifrado.cifrarCesar(textoOriginal, 3));
            } else if (algoritmo.contains("Base64")) {
                txtEditor.setText(GestorCifrado.cifrarBase64(textoOriginal));
            }
        } catch (Exception e) {
            mostrarAlertaError("Error al cifrar", "No se pudo cifrar el contenido: " + e.getMessage());
        }
    }

    @FXML
    private void descifrarTexto() {
        if (txtEditor == null || txtEditor.getText().isEmpty()) return;

        String algoritmo = obtenerAlgoritmoSeleccionado();
        String textoCifrado = txtEditor.getText();

        try {
            if (algoritmo.contains("AES-256")) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Descifrar contenido (AES-256)");
                dialog.setHeaderText("Introduce la contraseña para descifrar el archivo:");
                dialog.setContentText("Contraseña:");
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent() && !result.get().trim().isEmpty()) {
                    String textoDescifrado = GestorCifrado.descifrarAES(textoCifrado, result.get().trim());
                    txtEditor.setText(textoDescifrado);
                }
            } else if (algoritmo.contains("Cés")) {
                txtEditor.setText(GestorCifrado.descifrarCesar(textoCifrado, 3));
            } else if (algoritmo.contains("Base64")) {
                txtEditor.setText(GestorCifrado.descifrarBase64(textoCifrado));
            }
        } catch (Exception e) {
            mostrarAlertaError("Error al descifrar", "Contraseña incorrecta o formato de texto no válido.");
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void mostrarInicio() {
        if (vistaCentral != null && nodoInicio != null) {
            vistaCentral.getChildren().setAll(nodoInicio);
            actualizarEstiloNavegacion(true);
            actualizarVisibilidadBarraEstado(false);
            actualizarVisibilidadNavegacion(true);
            actualizarRecientesUI();
        }
    }

    @FXML
    private void mostrarConfiguracion() {
        if (vistaCentral != null && nodoConfiguracion != null) {
            vistaCentral.getChildren().setAll(nodoConfiguracion);
            configurarComportamientoConfiguracion();
            actualizarEstiloNavegacion(false);
            actualizarVisibilidadBarraEstado(false);
            actualizarVisibilidadNavegacion(true);
        }
    }

    private void mostrarEditor() {
        if (vistaCentral != null && nodoEditor != null) {
            vistaCentral.getChildren().setAll(nodoEditor);
            configurarComportamientoEditor();
            actualizarEstiloNavegacion(true);
            actualizarVisibilidadBarraEstado(true);
            actualizarVisibilidadNavegacion(false);
        }
    }

    @FXML
    private void nuevoDocumento() {
        rutaArchivoActual = null;
        mostrarEditor();
        restablecerTipografiaOriginal();

        if (txtEditor != null) txtEditor.setText("");
        if (lblTabName != null) lblTabName.setText("sin_titulo.txt");
        if (lblStatusName != null) lblStatusName.setText("sin_titulo.txt");
        if (lblStatusState != null) lblStatusState.setText("Modificado");
        actualizarLineasYCursor();
    }

    @FXML
    private void abrirArchivo() {
        Stage stage = (Stage) vistaCentral.getScene().getWindow();
        Documento doc = GestorArchivos.abrirArchivo(stage);
        if (doc != null) {
            mostrarEditor();
            rutaArchivoActual = doc.getRuta();
            cargarEstiloOEstablecerPorDefecto(doc.getRuta());
            if (txtEditor != null) txtEditor.setText(doc.getContenido());
            if (lblTabName != null) lblTabName.setText(doc.getNombre());
            if (lblStatusName != null) lblStatusName.setText(doc.getNombre());
            if (lblStatusState != null) lblStatusState.setText("Guardado");
            actualizarLineasYCursor();
        }
    }

    @FXML
    @SuppressWarnings("unchecked")
    private void guardarArchivo() {
        Stage stage = (Stage) vistaCentral.getScene().getWindow();
        String contenido = (txtEditor != null) ? txtEditor.getText() : "";
        Documento doc = GestorArchivos.guardarArchivo(stage, rutaArchivoActual, contenido);
        if (doc != null) {
            rutaArchivoActual = doc.getRuta();
            if (lblTabName != null) lblTabName.setText(doc.getNombre());
            if (lblStatusName != null) lblStatusName.setText(doc.getNombre());
            if (lblStatusState != null) lblStatusState.setText("Guardado");

            if (nodoEditor != null) {
                ComboBox<String> cbFuenteEditor = (ComboBox<String>) nodoEditor.lookup("#cbFuenteEditor");
                ComboBox<String> cbTamanoEditor = (ComboBox<String>) nodoEditor.lookup("#cbTamanoEditor");
                if (cbFuenteEditor != null && cbTamanoEditor != null) {
                    GestorArchivos.guardarEstiloArchivo(doc.getRuta(), cbFuenteEditor.getValue(), cbTamanoEditor.getValue());
                }
            }
        }
    }

    @FXML
    @SuppressWarnings("unchecked")
    private void aplicarTipografiaEditor() {
        if (nodoEditor == null) return;
        ComboBox<String> cbFuenteEditor = (ComboBox<String>) nodoEditor.lookup("#cbFuenteEditor");
        ComboBox<String> cbTamanoEditor = (ComboBox<String>) nodoEditor.lookup("#cbTamanoEditor");

        if (cbFuenteEditor == null || cbTamanoEditor == null || txtEditor == null || txtLineas == null) return;

        String fuente = cbFuenteEditor.getValue();
        String tamanoBruto = cbTamanoEditor.getValue();

        if (fuente != null && tamanoBruto != null) {
            String tamanoNum = tamanoBruto.replaceAll("[^0-9]", "");
            String baseEditor = "-fx-font-family: '" + fuente + "'; -fx-font-size: " + tamanoNum + "px; -fx-control-inner-background: #171a27; -fx-background-color: #171a27;";
            String baseLineas = "-fx-font-family: '" + fuente + "'; -fx-font-size: " + tamanoNum + "px; -fx-control-inner-background: #121520; -fx-background-color: #121520;";

            txtEditor.setStyle(baseEditor + " -fx-text-fill: #c9d1d9;");
            txtLineas.setStyle(baseLineas + " -fx-text-fill: #484f58; -fx-alignment: TOP_RIGHT;");
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarEstiloOEstablecerPorDefecto(String ruta) {
        String[] estilo = GestorArchivos.cargarEstiloArchivo(ruta);
        if (estilo != null && nodoEditor != null) {
            ComboBox<String> cbFuenteEditor = (ComboBox<String>) nodoEditor.lookup("#cbFuenteEditor");
            ComboBox<String> cbTamanoEditor = (ComboBox<String>) nodoEditor.lookup("#cbTamanoEditor");

            if (cbFuenteEditor != null) cbFuenteEditor.setValue(estilo[0]);
            if (cbTamanoEditor != null) cbTamanoEditor.setValue(estilo[1]);
            aplicarTipografiaEditor();
        } else {
            restablecerTipografiaOriginal();
        }
    }

    @SuppressWarnings("unchecked")
    private void restablecerTipografiaOriginal() {
        if (nodoEditor == null) return;
        ComboBox<String> cbFuenteEditor = (ComboBox<String>) nodoEditor.lookup("#cbFuenteEditor");
        ComboBox<String> cbTamanoEditor = (ComboBox<String>) nodoEditor.lookup("#cbTamanoEditor");

        if (cbFuenteEditor != null) cbFuenteEditor.setValue("JetBrains Mono");
        if (cbTamanoEditor != null) cbTamanoEditor.setValue("14 px");
        aplicarTipografiaEditor();
    }

    @FXML
    @SuppressWarnings("unchecked")
    private void aplicarTipografia() {
        if (nodoConfiguracion == null) return;
        ComboBox<String> fontFamilySelector = (ComboBox<String>) nodoConfiguracion.lookup("#fontFamilySelector");
        ComboBox<String> fontSizeSelector = (ComboBox<String>) nodoConfiguracion.lookup("#fontSizeSelector");

        if (fontFamilySelector == null || fontSizeSelector == null || txtEditor == null || txtLineas == null) return;

        String fuente = fontFamilySelector.getValue();
        String tamanoBruto = fontSizeSelector.getValue();

        if (fuente != null && tamanoBruto != null) {
            String tamanoNum = tamanoBruto.replaceAll("[^0-9]", "");
            String baseEditor = "-fx-font-family: '" + fuente + "'; -fx-font-size: " + tamanoNum + "px; -fx-control-inner-background: #171a27; -fx-background-color: #171a27;";
            String baseLineas = "-fx-font-family: '" + fuente + "'; -fx-font-size: " + tamanoNum + "px; -fx-control-inner-background: #121520; -fx-background-color: #121520;";

            txtEditor.setStyle(baseEditor + " -fx-text-fill: #c9d1d9;");
            txtLineas.setStyle(baseLineas + " -fx-text-fill: #484f58; -fx-alignment: TOP_RIGHT;");
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