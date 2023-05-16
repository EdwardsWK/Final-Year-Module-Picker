package view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import model.Module;

public class ReservePanelView extends GridPane {

	private ColumnConstraints column0, column1;
	private ModuleListView unselectedModules, reservedModules;
	private ReserveButtonsView reserveButtons;
	private int reserveCredits;

	public ReservePanelView(int term) {

		this.setHgap(20);
		this.setPadding(new Insets(0, 15, 15, 15));

		unselectedModules = new ModuleListView("Unselected", term, 260);
		reservedModules = new ModuleListView(term);
		reserveButtons = new ReserveButtonsView(term);
		reserveCredits = 0;

		column0 = new ColumnConstraints();
		column0.setHalignment(HPos.CENTER);
		column0.setPercentWidth(50);
		column1 = new ColumnConstraints();
		column1.setHalignment(HPos.CENTER);
		column1.setPercentWidth(50);

		this.getColumnConstraints().addAll(column0, column1);

		this.add(unselectedModules, 0, 0);
		this.add(reservedModules, 1, 0);
		this.add(reserveButtons, 0, 1, 2, 1);
	}

	public ModuleListView getUnselectedModules() {
		return unselectedModules;
	}

	public ModuleListView getReservedModules() {
		return reservedModules;
	}

	public Module getSelectedUnselectedModule() {
		return unselectedModules.getSelectedModule();
	}

	public Module getSelectedReservedModule() {
		return reservedModules.getSelectedModule();
	}

	public ReserveButtonsView getButtons() {
		return reserveButtons;
	}

	public void addUnselectedModule(Module module) {
		unselectedModules.addModule(module);
	}

	public void addReservedModule(Module module) {
		reservedModules.addModule(module);
	}

	public void removeUnselectedModule(Module module) {
		unselectedModules.removeModule(module);
	}

	public void removeReservedModule(Module module) {
		reservedModules.removeModule(module);
	}

	public void incrCredits(int credits) {
		reserveCredits += credits;
	}

	public void decrCredits(int credits) {
		reserveCredits -= credits;
	}

	public int getCredits() {
		return reserveCredits;
	}

	public void clear() {
		unselectedModules.clear();
		reservedModules.clear();
		reserveCredits = 0;
	}

}
