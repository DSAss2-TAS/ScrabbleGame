package game;

import javax.swing.JTextField;

public class Player {
	private String name;
	private int score;
	private JTextField scoret;
	private JTextField turnt;

	public Player(String name) {
		this.name = name;
		this.score = 0;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public void setScoreField(JTextField scoret) {
		this.scoret = scoret;
	}

	public void setTurnField(JTextField turnt) {
		this.turnt = turnt;
	}

	public void addScore(int changedScore) {
		score += changedScore;
		scoret.setText(String.valueOf(score));
	}

	public void showTurnField() {
		turnt.setText("Turn Here!");
		;
	}
}
