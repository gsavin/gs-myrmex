package org.graphstream.algorithm.myrmex;

import java.util.HashMap;
import java.util.LinkedList;

public class Pheromones {
	/**
	 * Pheromone array.
	 */
	// protected float[] pheromones = new float[1];

	/**
	 * Temporary pheromone array.
	 */
	// protected float[] pheromonesTmp = new float[1];

	/**
	 * Total of all pheromones for all colours after the last commit().
	 */
	protected float pheromonesTotal;

	/**
	 * Set to true if some inter-step value changed.
	 */
	// protected boolean commitNeeded = false;

	protected HashMap<Integer, Pheromone<?>> pheromonesM;
	protected LinkedList<Pheromone<?>> pheromonesToProcess;

	protected Pheromones(int count) {
		// pheromonesTmp = new float[count];
		// pheromones = new float[count];

		// Initialise the pheromones to a very small value to avoid 0.

		pheromonesTotal = 0;
		/*
		 * for (int i = 0; i < count; ++i) { float nb = 0.0001f;
		 * 
		 * pheromonesTmp[i] = nb; pheromones[i] = nb; pheromonesTotal += nb; }
		 */
	}

	public Pheromones(AntContext ctx) {
		// pheromonesTmp = new float[ctx.getColonyCount()];
		// pheromones = new float[ctx.getColonyCount()];

		// Initialise the pheromones to a very small value to avoid 0.

		pheromonesM = new HashMap<Integer, Pheromone<?>>();
		pheromonesToProcess = new LinkedList<Pheromone<?>>();
		pheromonesTotal = 0;
		/*
		 * for (int i = 0; i < ctx.getColonyCount(); ++i) { float nb =
		 * ctx.random().nextFloat() * 0.0001f;
		 * 
		 * pheromonesTmp[i] = nb; pheromones[i] = nb; pheromonesTotal += nb; }
		 */
	}

	/**
	 * switch: Commit all temporary changes to this object. The commit operation
	 * will
	 * <ul>
	 * <li>the pheromones;</li>
	 * </ul>
	 * Commit should only be called at the end of each AntCO� step.
	 */
	public void commit() {
		/*
		 * if (commitNeeded) { int n = pheromones.length;
		 * 
		 * for (int i = 0; i < n; ++i) { float incr = pheromonesTmp[i];
		 * 
		 * pheromones[i] += incr; pheromonesTotal += incr; pheromonesTmp[i] = 0;
		 * }
		 * 
		 * commitNeeded = false; }
		 */
		while (pheromonesToProcess.size() > 0) {
			Pheromone<?> p = pheromonesToProcess.poll();

			if (pheromonesM.containsKey(p.getMarker()))
				pheromonesM.get(p.getMarker()).fusion(p);
			else
				pheromonesM.put(p.getMarker(), p.clone());
		}
	}

	/**
	 * Step method for this edge. Pheromones evaporation is done here.
	 * 
	 * @param ctx
	 *            ants context
	 */
	public void step(AntContext ctx) {
		/*
		 * int n = pheromones.length;
		 * 
		 * if (n > 0) { // Evaporate the pheromones already present on the edge.
		 * 
		 * pheromonesTotal = 0;
		 * 
		 * for (int i = 0; i < n; ++i) { pheromones[i] *=
		 * ctx.getAntParams().rho; pheromonesTotal += pheromones[i]; }
		 * 
		 * // Then, and only then, copy pheromones added by ants at the //
		 * previous // step to the pheromones on the edge.
		 * 
		 * }
		 */

		for (Pheromone<?> p : pheromonesM.values())
			p.evaporate(ctx.getAntParams().rho);

		commit();
	}

	/**
	 * Pheromone value for a given colour.
	 * 
	 * @param color
	 *            Colour index.
	 * @return The pheromone value for a given colour.
	 */
	public Pheromone<?> get(int marker) {
		// if (color >= 0 && pheromones.length > color)
		// return pheromones[color];

		Pheromone<?> p = pheromonesM.get(marker);
		return p == null ? Pheromone.EMPTY : p;
	}

	/**
	 * Pheromone value for all colours.
	 * 
	 * @return The total pheromone value.
	 */
	public float getTotalLoad() {
		float t = 0;

		for (Pheromone<?> p : pheromonesM.values())
			t += p.getLoad();

		return t;
	}

	/**
	 * Set the pheromone value for a given colour. If the given colour is null
	 * all the colours are changed. This directly changes the pheromone, no need
	 * to commit.
	 * 
	 * @param colony
	 *            Colour index or null for all the colours.
	 * @param value
	 *            Value to set.
	 */
	/*
	 * public void set(Colony colony, float value) { if (colony != null) { int
	 * index = colony.getIndex();
	 * 
	 * checkPheromonesArraySizes(index);
	 * 
	 * pheromonesTotal -= pheromones[index]; pheromones[index] = value;
	 * pheromonesTotal += pheromones[index]; } else { Arrays.fill(pheromones,
	 * value); pheromonesTotal = value * pheromones.length; } }
	 */

	/**
	 * Increment the pheromone value for a given colour. If the given colour is
	 * null all the colours are changed. This change is stored in a temporary
	 * buffer, you need to call commit() to make it real.
	 * 
	 * @param colony
	 *            Colour index or null for all the colours.
	 * @param value
	 *            Value to add.
	 */
	/*
	 * public void incr(Colony colony, float value) { commitNeeded = true;
	 * 
	 * if (colony != null) { int index = colony.getIndex();
	 * 
	 * checkPheromonesArraySizes(index); pheromonesTmp[index] += value; } else {
	 * for (int i = 0; i < pheromonesTmp.length; ++i) pheromonesTmp[i] += value;
	 * } }
	 */

	public void drop(Pheromone<?> pheromone) {
		pheromonesToProcess.add(pheromone);
	}

	@SuppressWarnings("unchecked")
	public <T extends Comparable<T>> T getMinimumData() {
		T m = null;

		for (Pheromone<?> p : pheromonesM.values()) {
			T d = (T) p.getData();

			if (d != null)
				m = ( m == null || m.compareTo(d) > 0 ) ? d : m;
		}

		return m;
	}

	/**
	 * Check that the antCountPerColor arrays are large enough.
	 * 
	 * @param index
	 */
	/*
	 * protected void checkPheromonesArraySizes(int index) { if (index >=
	 * pheromones.length) { int n = pheromones.length;
	 * 
	 * pheromonesTmp = Arrays.copyOf(pheromonesTmp, index + 1); pheromones =
	 * Arrays.copyOf(pheromones, index + 1);
	 * 
	 * Arrays.fill(pheromones, n, index + 1, 0.000001f); pheromonesTotal +=
	 * 0.000001f * (index + 1 - n); } }
	 */

	private static class EmptyPheromones extends Pheromones {

		EmptyPheromones() {
			super(0);
		}

		public void step(AntContext ctx) {
		}

		public Pheromone<?> get(int color) {
			return Pheromone.EMPTY;
		}

		public float getTotalLoad() {
			return 0;
		}

		public void drop(Pheromone<?> p) {
		}
		/*
		 * public void set(Colony colony, float value) { }
		 * 
		 * public void incr(Colony colony, float value) { }
		 */
	}

	public static final Pheromones EMPTY = new EmptyPheromones();
}
