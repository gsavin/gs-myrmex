package org.graphstream.algorithm.myrmex;

public interface PheromoneFactory {
	Pheromone<?> newPheromone(AntContext ctx, Colony colony);
	
	public static class DefaultPheromoneFactory implements PheromoneFactory {
		public Pheromone<?> newPheromone(AntContext ctx, Colony colony) {
			return new Pheromone<Double>(colony.getIndex(),0);
		}
	}
}
