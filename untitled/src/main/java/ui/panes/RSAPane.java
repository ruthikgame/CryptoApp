package ui.panes;

import cipher.modern.RSA;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigInteger;

public class RSAPane implements CipherPane {

    private final RSA rsa = new RSA();
    private final VBox root = new VBox(10);

    public RSAPane() {
        root.setPadding(new Insets(10));


        TextField pField = new TextField();
        pField.setPromptText("p");

        TextField qField = new TextField();
        qField.setPromptText("q");


        Button randomBtn = new Button("Случайные простые (8 бит)");
        randomBtn.setOnAction(e -> {
            try {
                pField.setText(RSA.randomPrime(8).toString());
                qField.setText(RSA.randomPrime(8).toString());
            } catch (Exception ex) {
                showError("Ошибка генерации простых чисел", ex);
            }
        });


        Button genBtn = new Button("Сгенерировать ключи");


        TextArea message = new TextArea();
        message.setPromptText("Текст / Шифр");
        message.setWrapText(true);


        Button encBtn = new Button("Зашифровать");
        Button decBtn = new Button("Расшифровать");


        Label pubKey = new Label();
        pubKey.setWrapText(true);

        Label privKey = new Label();
        privKey.setWrapText(true);


        genBtn.setOnAction(e -> {
            try {
                BigInteger p = new BigInteger(pField.getText().trim());
                BigInteger q = new BigInteger(qField.getText().trim());

                rsa.generateKeys(p, q);

                pubKey.setText(rsa.publicKey());
                privKey.setText(rsa.privateKey());

            } catch (Exception ex) {
                showError("Ошибка генерации ключей", ex);
            }
        });


        encBtn.setOnAction(e -> {
            try {
                message.setText(rsa.encrypt(message.getText()));
            } catch (Exception ex) {
                showError("Ошибка шифрования", ex);
            }
        });


        decBtn.setOnAction(e -> {
            try {
                message.setText(rsa.decrypt(message.getText()));
            } catch (Exception ex) {
                showError("Ошибка расшифрования", ex);
            }
        });

        // Сборка интерфейса
        root.getChildren().addAll(
                new Label("RSA"),
                new HBox(10, pField, qField),
                randomBtn,
                genBtn,
                new Separator(),
                message,
                new HBox(10, encBtn, decBtn),
                new Separator(),
                new Label("Публичный ключ:"),
                pubKey,
                new Label("Приватный ключ:"),
                privKey
        );
    }


    private void showError(String title, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }

    @Override
    public String getName() {
        return "RSA";
    }

    @Override
    public Parent getRoot() {
        return root;
    }
}
