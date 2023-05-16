package view;

import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import model.Module;

public class ModuleListView extends VBox {

	private Label lblTitle;
	private ListView<Module> moduleView;
	private ObservableList<Module> moduleObservable;

	public ModuleListView() {
		ModuleConstruct("Selected Year Long Modules", 35);
	}

	public ModuleListView(int term) {
		this("Reserved", term, 260);
	}

	public ModuleListView(String status, int term) {
		this(status, term, 80);
	}

	public ModuleListView(String status, int term, int size) {
		ModuleConstruct(status + " Term " + term + " Modules", size);
	}

	private void ModuleConstruct(String listLabel, int listHeight) {
		this.setPadding(new Insets(15, 0, 0, 0));
		lblTitle = new Label(listLabel);
		moduleObservable = FXCollections.observableArrayList();
		moduleView = new ListView<>(moduleObservable);
		moduleView.setPrefHeight(listHeight);
		this.setMinHeight(listHeight);
		this.getChildren().addAll(lblTitle, moduleView);
	}

	public Module getSelectedModule() {
		return moduleView.getSelectionModel().getSelectedItem();
	}

	public Collection<Module> getModules() {
		return moduleView.getItems();
	}

	public void addModule(Module module) {
		moduleObservable.add(module);
	}

	public void removeModule(Module module) {
		moduleView.getItems().remove(module);
	}

	public void setModules(Collection<Module> modules) {
		clear();
		modules.forEach(module -> addModule(module));
	}

	public void clear() {
		moduleView.getItems().clear();
	}
}
