package ui.panes;

import cipher.modern.RC5;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.Priority;

public class RC5Pane implements CipherPane {

    private final VBox root = new VBox(10);
    private final RC5 rc5 = new RC5();

    public RC5Pane() {
        root.setPadding(new Insets(10));

        TextArea input = new TextArea();
        input.setPromptText("Текст");
        input.setPrefRowCount(5);

        TextField keyField = new TextField();
        keyField.setPromptText("Ключ (Base64 или строка)");

        Button genKey = new Button("Случайный ключ");
        genKey.setOnAction(e -> keyField.setText(rc5.generateRandomKey()));

        HBox keyBox = new HBox(10, keyField, genKey);
        HBox.setHgrow(keyField, Priority.ALWAYS);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setPrefRowCount(5);

        Button enc = new Button("Зашифровать");
        Button dec = new Button("Расшифровать");

        enc.setOnAction(e -> {
            try {
                output.setText(rc5.encrypt(input.getText(), keyField.getText()));
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        dec.setOnAction(e -> {
            try {
                output.setText(rc5.decrypt(input.getText(), keyField.getText()));
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        HBox buttons = new HBox(10, enc, dec);

        root.getChildren().addAll(
                new Label("Входной текст"),
                input,
                new Label("Ключ"),
                keyBox,
                buttons,
                new Label("Результат"),
                output
        );
    }

    @Override
    public Parent getRoot() {
        return root;
    }

    @Override
    public String getName() {
        return "Stream RC5";
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
