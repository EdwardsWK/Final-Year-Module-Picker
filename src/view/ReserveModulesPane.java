package view;

import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public class ReserveModulesPane extends Accordion {

	private TitledPane tp1, tp2;
	private ReservePanelView rpv1, rpv2;

	public ReserveModulesPane() {
		rpv1 = new ReservePanelView(1);
		rpv2 = new ReservePanelView(2);

		tp1 = new TitledPane("Term 1 Modules", rpv1);
		tp2 = new TitledPane("Term 2 Modules", rpv2);

		this.getPanes().addAll(tp1, tp2);
		this.setPadding(new Insets(10));

		this.setExpandedPane(tp1);
	}
	
	public ReservePanelView getRpv1() {
		return rpv1;
	}
	
	public ReservePanelView getRpv2() {
		return rpv2;
	}
	
	public void switchPanel() {
		this.setExpandedPane(tp2);
	}
	
	public void clearAll() {
		rpv1.clear();
		rpv2.clear();
	}
}
