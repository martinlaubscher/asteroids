package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class HighScoresList {
	private final List<Score> scores;
	private final File file;

	public HighScoresList() {
		scores = new ArrayList<>();
		file = new File("src/resources/scores.txt");
		readScoresFromFile();
	}

	private void readScoresFromFile() {
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String[] parts = scanner.nextLine().split(",");
				String name = parts[0].trim();
				int score = Integer.parseInt(parts[1].trim());
				scores.add(new Score(name, score));
			}
		} catch (IOException e) {
			System.err.println("Error reading scores file");
		}
	}

	public void addScore(Score score) {
		scores.add(score);
		Collections.sort(scores, new ScoreComparator());
		writeScoresToFile();
		if (scores.size() > 10) {
			int endIndex = scores.size() - 1;
			int startIndex = endIndex - 3;
			scores.subList(startIndex, endIndex + 1).clear();
			writeScoresToFile();
		}
	}

	private class ScoreComparator implements Comparator<Score> {
		@Override
		public int compare(Score s1, Score s2) {
			return Integer.compare(s2.getScore(), s1.getScore());
		}
	}

	private void writeScoresToFile() {
		try (FileWriter writer = new FileWriter(file)) {
			for (Score score : scores) {
				writer.write(score.getName() + ", " + score.getScore() + "\n");
			}
		} catch (IOException e) {
			System.err.println("Error writing scores file");
		}
	}

	public List<Score> getTopScores(int numScores) {
		int size = Math.min(numScores, scores.size());
		return new ArrayList<>(scores.subList(0, size));
	}
}
