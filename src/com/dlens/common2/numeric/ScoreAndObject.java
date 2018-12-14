package com.dlens.common2.numeric;

public class ScoreAndObject<T> implements Comparable<ScoreAndObject<T>> {
	private double score;
	private T object;
	public ScoreAndObject(T object, double score) {
		this.object=object;
		this.score = score;
	}
	
	public T getObject() {
		return object;
	}
	
	public double getScore() {
		return score;
	}

	@Override
	public int compareTo(ScoreAndObject<T> other) {
		if (other==null)
			return 1;
		return Double.compare(score, other.score);
	}
	
	
}
