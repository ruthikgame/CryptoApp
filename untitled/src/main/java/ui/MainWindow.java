package ui;

import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ComboBox;
import ui.panes.*;

public class MainWindow extends BorderPane {

    private final ComboBox<CipherPane> selector = new ComboBox<>();

    public MainWindow() {

        selector.getItems().addAll(
                new CaesarPane(),
                new VigenerePane(),
                new RC5Pane(),
                new RC5BlockPane(),
                new RSAPane()

        );

        selector.setCellFactory(cb -> new ListCell<CipherPane>() {
            @Override
            protected void updateItem(CipherPane item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });


        selector.setButtonCell(new ListCell<CipherPane>() {
            @Override
            protected void updateItem(CipherPane item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        selector.getSelectionModel().selectFirst();

        setTop(selector);
        setCenter(selector.getValue().getRoot());

        selector.setOnAction(e ->
                setCenter(selector.getValue().getRoot())
        );
    }
}
