package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class OverviewSelectionPane extends GridPane {

	private Button btnSaveOverview;
	private ColumnConstraints column0, column1;
	private TextArea txtStudentDetails, txtSelected, txtReserved;
	private HBox detailsBox, saveBox;

	public OverviewSelectionPane() {
		// styling
		this.setVgap(15);
		this.setHgap(20);
		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(30, 25, 25, 25));

		column0 = new ColumnConstraints();
		column0.setHalignment(HPos.CENTER);
		column0.setPercentWidth(50);
		column1 = new ColumnConstraints();
		column1.setHalignment(HPos.CENTER);
		column1.setPercentWidth(50);

		this.getColumnConstraints().addAll(column0, column1);

		txtStudentDetails = new TextArea("Profile will appear here");
		txtSelected = new TextArea("Selected Modules will appear here");
		txtReserved = new TextArea("Reserved modules will appear here");
		btnSaveOverview = new Button("Save Overview");

		//txtStudentDetails.setDisable(true);
		txtStudentDetails.setEditable(false);
		txtSelected.setEditable(false);
		txtReserved.setEditable(false);

		txtStudentDetails.setPrefHeight(100);
		txtSelected.setPrefHeight(200);
		txtReserved.setPrefHeight(200);

		detailsBox = new HBox(txtStudentDetails);
		detailsBox.setAlignment(Pos.CENTER);
		detailsBox.setPadding(new Insets(0, 0, 10, 0));
		saveBox = new HBox(btnSaveOverview);
		saveBox.setAlignment(Pos.CENTER);
		saveBox.setPadding(new Insets(10, 0, 0, 0));

		HBox.setHgrow(txtStudentDetails, Priority.ALWAYS);
		HBox.setHgrow(btnSaveOverview, Priority.ALWAYS);

		this.add(detailsBox, 0, 0, 2, 1);
		this.add(txtSelected, 0, 1);
		this.add(txtReserved, 1, 1);
		this.add(saveBox, 0, 2, 2, 1);
	}

	public TextArea getStudentDetails() {
		return txtStudentDetails;
	}

	public TextArea getSelectedModules() {
		return txtSelected;
	}

	public TextArea getReservedModules() {
		return txtReserved;
	}

	public void setStudentDetails(String details) {
		txtStudentDetails.setText(details);
	}

	public void setSelectedModules(String selected) {
		txtSelected.setText(selected);
	}

	public void setReservedModules(String reserved) {
		txtReserved.setText(reserved);
	}

	public void saveOverviewHandler(EventHandler<ActionEvent> handler) {
		btnSaveOverview.setOnAction(handler);
	}
	
	public void clearAll() {
		txtStudentDetails.clear();
		txtSelected.clear();
		txtReserved.clear();
	}

}
