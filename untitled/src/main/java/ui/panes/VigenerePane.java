package ui.panes;

import cipher.classical.VigenereCipher;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class VigenerePane implements CipherPane {

    private final VBox root = new VBox(10);
    private final VigenereCipher cipher = new VigenereCipher();

    public VigenerePane() {
        root.setPadding(new Insets(10));

        TextField keyField = new TextField();
        keyField.setPromptText("Ключевое слово");

        TextArea input = new TextArea();
        input.setPromptText("Введите текст");
        input.setPrefRowCount(5);

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setPrefRowCount(5);

        Button enc = new Button("Зашифровать");
        Button dec = new Button("Расшифровать");

        enc.setOnAction(e -> {
            try {
                output.setText(cipher.encode(input.getText(), keyField.getText()));
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        dec.setOnAction(e -> {
            try {
                output.setText(cipher.decode(input.getText(), keyField.getText()));
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Ключ"),
                keyField,
                new Label("Входной текст"),
                input,
                new HBox(10, enc, dec),
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
        return "Квадрат Виженера";
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
