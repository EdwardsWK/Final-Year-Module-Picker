
package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SelectModulesPane extends GridPane {

	private Button btnReset, btnSubmit;
	private ColumnConstraints column0, column1;
	private ModuleListView term1Selected, term1Unselected, term2Selected, term2Unselected, yearLong;
	private SelectButtonsView term1Btns, term2Btns;
	private SelectCreditsView term1Credits, term2Credits;
	private VBox leftBox, rightBox;

	public SelectModulesPane() {
		
		this.setVgap(15);
		this.setHgap(20);
		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(15));

		column0 = new ColumnConstraints();
		column0.setHalignment(HPos.RIGHT);
		column0.setPercentWidth(50);
		column1 = new ColumnConstraints();
		column1.setHalignment(HPos.LEFT);
		column1.setPercentWidth(50);

		this.getColumnConstraints().addAll(column0, column1);

		term1Unselected = new ModuleListView("Unselected", 1);
		term1Selected = new ModuleListView("Selected", 1);
		term2Unselected = new ModuleListView("Unselected", 2);
		term2Selected = new ModuleListView("Selected", 2);
		yearLong = new ModuleListView();

		btnReset = new Button("Reset");
		btnSubmit = new Button("Submit");
		term1Btns = new SelectButtonsView(1);
		term2Btns = new SelectButtonsView(2);

		term1Credits = new SelectCreditsView(1);
		term2Credits = new SelectCreditsView(2);

		leftBox = new VBox(term1Unselected, term1Btns, term2Unselected, term2Btns);
		rightBox = new VBox(yearLong, term1Selected, term2Selected);

		btnReset.setPrefWidth(60);
		btnSubmit.setPrefWidth(60);
		
		this.add(leftBox, 0, 0);
		this.add(term1Credits, 0, 1);
		this.add(btnReset, 0, 2);
		this.add(rightBox, 1, 0);
		this.add(term2Credits, 1, 1);
		this.add(btnSubmit, 1, 2);
	}

	public void clearAll() {
		term1Unselected.clear();
		term2Unselected.clear();
		term1Selected.clear();
		term2Selected.clear();
		yearLong.clear();
		term1Credits.reset();
		term2Credits.reset();
	}
	
	public ModuleListView getTerm1Unselected() {
		return term1Unselected;
	}
	
	public ModuleListView getTerm1Selected() {
		return term1Selected;
	}
	
	public ModuleListView getTerm2Unselected() {
		return term2Unselected;
	}
	
	public ModuleListView getTerm2Selected() {
		return term2Selected;
	}
	
	public ModuleListView getYearLong() {
		return yearLong;
	}
	
	public SelectCreditsView getTerm1Credits() {
		return term1Credits;
	}
	
	public SelectCreditsView getTerm2Credits() {
		return term2Credits;
	}
	
	public SelectButtonsView getTerm1Buttons() {
		return term1Btns;
	}
	
	public SelectButtonsView getTerm2Buttons() {
		return term2Btns;
	}
	
	public void submitHandler(EventHandler<ActionEvent> handler) {
		btnSubmit.setOnAction(handler);
	}
	
	public void resetHandler(EventHandler<ActionEvent> handler) {
		btnReset.setOnAction(handler);
	}
}