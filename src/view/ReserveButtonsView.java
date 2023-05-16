package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ReserveButtonsView extends HBox {

	private Label lblTerm;
	private Button btnAdd, btnRemove, btnConfirm;

	public ReserveButtonsView(int term) {

		lblTerm = new Label("Reserve 30 credits worth of term " + term + " modules");
		btnAdd = new Button("Add");
		btnRemove = new Button("Remove");
		btnConfirm = new Button("Confirm");

		btnAdd.setPrefWidth(60);
		btnRemove.setPrefWidth(60);
		btnConfirm.setPrefWidth(60);
		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(15, 0, 0, 0));
		this.setSpacing(10);

		this.getChildren().addAll(lblTerm, btnAdd, btnRemove, btnConfirm);
	}
	
	public void addHandler(EventHandler<ActionEvent> handler) {
        btnAdd.setOnAction(handler);
    }
	
    public void removeHandler(EventHandler<ActionEvent> handler) {
        btnRemove.setOnAction(handler);
    }
    
    public void confirmHandler(EventHandler<ActionEvent> handler) {
        btnConfirm.setOnAction(handler);
    }
}
