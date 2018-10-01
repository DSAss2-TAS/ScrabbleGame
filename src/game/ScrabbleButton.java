package game;

import java.awt.Insets;

import javax.swing.JButton;

public class ScrabbleButton extends JButton {
	int row, column;

	public ScrabbleButton(int i, int j) {
		super(" ");
		this.row = i;
		this.column = j;
		this.setMargin(new Insets(0, 0, 0, 0));
	}

	public int getRow(){
		return this.row;
	}
	public int getColumn(){
		return this.column;
	}
}
