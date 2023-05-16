package view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SelectCreditsView extends HBox {
	
	private int credits;
	private Label lblTerm;
	private TextField txtCredits;
	
	public SelectCreditsView(int term){
		
		this.credits = 0;
		lblTerm = new Label("Current Term " + term + " Credits: ");
		txtCredits = new TextField(String.valueOf(this.credits));
		
		txtCredits.setPrefWidth(35);
		this.setAlignment(Pos.CENTER);
		
		this.getChildren().addAll(lblTerm, txtCredits);
	}
	
	public int getCredits() {
		return this.credits;
	}
	
	public void incrCredits(int credits) {
		this.credits += credits;
		update();
	}
	
	public void decrCredits(int credits) {
		this.credits -= credits;
		update();
	}
	
	public void reset() {
		this.credits = 0;
		update();
	}
	
	public void update() {
		txtCredits.setText(String.valueOf(this.credits));
	}
	
}
