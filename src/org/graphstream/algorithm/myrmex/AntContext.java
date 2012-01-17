/*
 * This file is part of AntCo2.
 * 
 * AntCo2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * AntCo2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with AntCo2.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2009 - 2010
 * 	Antoine Dutot
 * 	Guilhelm Savin
 */
package org.graphstream.algorithm.myrmex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.graphstream.algorithm.myrmex.policy.ProportionalPopulationPolicy;
import org.graphstream.stream.ElementSink;

/**
 * Context of the AntCo2 algorithm. The context is composed of params, an
 * internal graph and colonies.
 * 
 * @author adutot, gsavin
 * 
 */
public class AntContext implements ElementSink {
	/**
	 * Iterator over colonies. This iterator is safe to colonies changes.
	 * 
	 * @author adutot, gsavin
	 * 
	 */
	class ColonyIterator implements Iterator<Colony> {
		/**
		 * Current index.
		 */
		protected int index = 0;

		/**
		 * Has next colonie.
		 */
		public boolean hasNext() {
			for (int i = index; i < colonies.size(); i++)
				if (colonies.get(i) != null)
					return true;

			return false;
		}

		/**
		 * Get next colonie.
		 */
		public Colony next() {
			for (; index < colonies.size(); index++)
				if (colonies.get(index) != null)
					return colonies.get(index++);

			return null;
		}

		/**
		 * Not implemented.
		 */
		public void remove() {
			throw new Error("not implemented");
		}
	}

	/**
	 * Iterable over colonies.
	 * 
	 * @author adutot, gsavin
	 * 
	 */
	class ColonyIterable implements Iterable<Colony> {
		/**
		 * Return a new colony iterator.
		 */
		public Iterator<Colony> iterator() {
			return new ColonyIterator();
		}
	}

	/**
	 * Internal graph model.
	 * 
	 * @see org.graphstream.algorithm.AntGraph.AntCo2Graph
	 */
	protected AntGraph internalGraph;

	private ReentrantLock locked;

	protected LinkedList<AntListener> listeners;

	/**
	 * Algoritm parameters.
	 */
	protected AntParams params;

	/**
	 * Policy used to populate nodes with ants.
	 */
	protected PopulationPolicy populationPolicy;

	/**
	 * List of colonies.
	 * 
	 * @see org.graphstream.algorithm.antco2.Colony
	 */
	protected ArrayList<Colony> colonies;

	/**
	 * Iterable over colonies, used to provide safe iterators.
	 */
	protected ColonyIterable coloniesAsIterable;

	/**
	 * Current time id. Use for the ElementSink implementation.
	 */
	protected long timeId;

	/**
	 * Random instance.
	 */
	protected Random random;

	/**
	 * Number of jumps of the last step.
	 */
	protected int jumps;

	/**
	 * Number of over populated nodes encountered.
	 */
	protected int surpop;

	/**
	 * Number of nodes/edges migrations.
	 */
	protected int migrations;

	/**
	 * Current step.
	 */
	protected int step;

	/**
	 * Jumps count per colony.
	 */
	protected int[] jumpsPerColony;

	/**
	 * Ants count.
	 */
	protected int antCount;

	protected String outputMeasures;

	/**
	 * Default constructor.
	 */
	public AntContext() {
		colonies = new ArrayList<Colony>();
		coloniesAsIterable = new ColonyIterable();
		internalGraph = getDefaultAntGraph();
		params = getDefaultAntParams();
		locked = new ReentrantLock();
		listeners = new LinkedList<AntListener>();
		populationPolicy = getDefaultPopulationPolicy();
		jumpsPerColony = new int[Math.max(1, colonies.size())];

		internalGraph.addElementSink(this);
	}

	public AntGraph getDefaultAntGraph() {
		return new AntGraph(this);
	}

	public PopulationPolicy getDefaultPopulationPolicy() {
		return new ProportionalPopulationPolicy();
	}

	public AntParams getDefaultAntParams() {
		return new AntParams();
	}

	/**
	 * Access to parameters.
	 * 
	 * @return parameters
	 */
	@SuppressWarnings("unchecked")
	public <T extends AntParams> T getAntParams() {
		return (T) params;
	}
	
	public void setAntParams(AntParams params) {
		this.params = params;
	}

	/**
	 * Get the policy used for the ants population.
	 * 
	 * @return the ants population policy
	 */
	public PopulationPolicy getPopulationPolicy() {
		return populationPolicy;
	}

	/**
	 * Get the colonies count.
	 * 
	 * @return colonies count
	 */
	public int getColonyCount() {
		return colonies.size();
	}

	/**
	 * Get the i-th colony.
	 * 
	 * @param i
	 *            index of the colony
	 * @return
	 */
	public Colony getColony(int i) {
		return colonies.get(i);
	}

	/**
	 * Get the current step.
	 * 
	 * @return current step
	 */
	public int getCurrentStep() {
		return step;
	}

	/**
	 * Get the nodes count in the internal graph.
	 * 
	 * @return nodes count
	 */
	public int getNodeCount() {
		return internalGraph.getNodeCount();
	}

	/**
	 * Get the ants count.
	 * 
	 * @return ants count
	 */
	public int getAntCount() {
		return populationPolicy.getAntCount();
	}

	public String getOutputMeasures() {
		return outputMeasures;
	}

	public AntGraph getInternalGraph() {
		return internalGraph;
	}

	public void lock() {
		locked.lock();
	}

	public void unlock() {
		locked.unlock();
	}

	/**
	 * Access to the random object used for random operations.
	 * 
	 * @return random object
	 */
	public Random random() {
		return random;
	}

	/**
	 * Get the source id used for sink operations.
	 * 
	 * @return source id
	 */
	public String sourceId() {
		return internalGraph.getId();
	}

	/**
	 * Get a new time id for sink operations.
	 * 
	 * @return a time id
	 */
	public long timeId() {
		return timeId++;
	}

	public void addAntCo2Listener(AntListener listener) {
		listeners.add(listener);
	}

	public void removeAntCo2Listener(AntListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Add a new colony.
	 * 
	 * @param name
	 *            name of the new colony
	 */
	public void addColony(String name) {
		int index = 0;

		while (index < colonies.size() && colonies.get(index) != null)
			index++;

		Colony colony = Colony.newColony(this, name, index);

		if (index < colonies.size())
			colonies.set(index, colony);
		else
			colonies.add(index, colony);

		populationPolicy.colonyAdded(colony);

		for (AntListener l : listeners)
			l.colonyAdded(colony);
	}

	/**
	 * Remove a colony.
	 * 
	 * @param name
	 *            name of the colony to remove
	 */
	public void removeColony(String name) {
		Colony toRemove = null;

		for (Colony c : coloniesAsIterable)
			if (c.getName().equals(name))
				toRemove = c;

		if (toRemove != null)
			removeColony(toRemove);
	}

	/**
	 * Remove a colony.
	 * 
	 * @param colony
	 *            colony to remove
	 */
	public void removeColony(Colony colony) {
		populationPolicy.colonyRemoved(colony);
		colony.removed();
		colonies.set(colony.getIndex(), null);

		for (AntListener l : listeners)
			l.colonyRemoved(colony);
	}

	/**
	 * Access to colonies in a for-each operation.
	 * 
	 * @return an iterable over colonies
	 */
	public Iterable<Colony> eachColony() {
		return coloniesAsIterable;
	}

	public Iterable<? extends AntNode> eachNode() {
		return internalGraph.<AntNode> getEachNode();
	}

	public Iterable<? extends AntEdge> eachEdge() {
		return internalGraph.<AntEdge> getEachEdge();
	}

	public void init() {
		colonies.clear();

		random = new Random(params.randomSeed);
		populationPolicy = getDefaultPopulationPolicy();
		populationPolicy.init(this);

		jumpsPerColony = new int[1];

		// internalGraph.clear();
		for (AntNode an : internalGraph.<AntNode> getEachNode())
			an.reset();
		
		for(AntEdge ae : internalGraph.<AntEdge>getEachEdge())
			ae.reset();
	}

	public void step() {
		lock();

		for (Colony colony : eachColony())
			colony.commit();

		for (Colony colony : eachColony())
			colony.step();

		for (AntEdge e : internalGraph.<AntEdge> getEachEdge())
			e.step(this);

		for (AntNode n : internalGraph.<AntNode> getEachNode())
			n.step(this);

		populationPolicy.step();
		// System.out.printf("%d ants%n",populationPolicy.getAntCount());

		unlock();

		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).step(this);
	}

	/**
	 * Increments jumps count.
	 * 
	 * @param ant
	 *            the ant which has jumped
	 */
	public void incrJumps(Ant ant) {
		jumps++;

		Colony color = ant.getColony();

		if (color.getIndex() >= jumpsPerColony.length)
			jumpsPerColony = Arrays
					.copyOf(jumpsPerColony, color.getIndex() + 1);

		jumpsPerColony[color.getIndex()]++;
	}

	/**
	 * Increments surpopulation count.
	 */
	public void incrSurpop() {
		surpop++;
	}

	/**
	 * Increments migrations count.
	 */
	public void incrMigrations() {
		migrations++;
	}

	/**
	 * @see org.graphstream.stream.ElementSink
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {

	}

	/**
	 * @see org.graphstream.stream.ElementSink
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {

	}

	/**
	 * @see org.graphstream.stream.ElementSink
	 */
	public void graphCleared(String sourceId, long timeId) {

	}

	/**
	 * @see org.graphstream.stream.ElementSink
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		populationPolicy.nodeAdded((AntNode) internalGraph.getNode(nodeId));
	}

	/**
	 * @see org.graphstream.stream.ElementSink
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		populationPolicy.nodeRemoved((AntNode) internalGraph.getNode(nodeId));
	}

	/**
	 * @see org.graphstream.stream.ElementSink
	 */
	public void stepBegins(String sourceId, long timeId, double step) {

	}
}
