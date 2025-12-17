package ui.panes;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import cipher.classical.CaesarCipher;
public class CaesarPane implements CipherPane {

    private final VBox root;
    private final CaesarCipher cipher = new CaesarCipher();

    public CaesarPane() {
        root = new VBox(10);
        root.setPadding(new Insets(10));

        TextArea inputText = new TextArea();
        inputText.setPromptText("Введите текст");

        TextField shiftField = new TextField();
        shiftField.setPromptText("Сдвиг");

        TextArea outputText = new TextArea();
        outputText.setEditable(false);

        Button enc = new Button("Зашифровать");
        Button dec = new Button("Расшифровать");
        Button brute = new Button("Взломать");

        enc.setOnAction(e -> {
            try {
                cipher.setShift(Integer.parseInt(shiftField.getText()));
                outputText.setText(cipher.encrypt(inputText.getText()));
            } catch (Exception ex) {
                showError("Некорректный сдвиг");
            }
        });

        dec.setOnAction(e -> {
            try {
                cipher.setShift(Integer.parseInt(shiftField.getText()));
                outputText.setText(cipher.decrypt(inputText.getText()));
            } catch (Exception ex) {
                showError("Некорректный сдвиг");
            }
        });

        brute.setOnAction(e ->
                outputText.setText(cipher.bruteForce(inputText.getText()))
        );

        root.getChildren().addAll(
                new Label("Текст"),
                inputText,
                new Label("Сдвиг"),
                shiftField,
                enc, dec, brute,
                new Label("Результат"),
                outputText
        );
    }

    @Override
    public Parent getRoot() {
        return root;
    }

    @Override
    public String getName() {
        return "Шифр Цезаря";
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
