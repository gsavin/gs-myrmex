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

import java.util.Random;
import org.graphstream.algorithm.DefineParameter;

/**
 * Parameters of the antco2 algorithm.
 * 
 * @author adutot, gsavin
 * 
 */
public class AntParams {
	public static enum DropOn {
		NODES(true,false),
		EDGES(false,true),
		NODES_AND_EDGES(true,true)
		;
		public final boolean onNodes;
		public final boolean onEdges;
		
		DropOn(boolean on, boolean oe) {
			this.onNodes = on;
			this.onEdges = oe;
		}
	}
	
	/**
	 * Name of the ants colony class.
	 */
	@DefineParameter(name = "ant.params.colonySpecies")
	protected String colonySpecies;

	/**
	 * Seed of the random instance.
	 */
	@DefineParameter(name = "ant.params.randomSeed")
	protected long randomSeed;

	/**
	 * Pheromone persistence factor [0..1]. Evaporation is ( 1 - rho ).
	 */
	@DefineParameter(name = "ant.params.evaporation", min = 0, max = 1)
	protected float rho = -1f;

	/**
	 * Output debugging messages?.
	 */
	@DefineParameter(name = "ant.params.debug")
	public boolean debug = false;

	/**
	 * Exponent for edge pheromone values as perceived by ants [0,n[.
	 */
	@DefineParameter(name = "ant.params.alpha", min = 0)
	public float alpha = -1f;

	/**
	 * Exponent for edge weights values as perceived by ants [0,n[.
	 */
	@DefineParameter(name = "ant.params.beta", min = 0)
	public float beta = -1f;

	/**
	 * How to implement jumping (jumping occurs for various reasons, most
	 * notably, agoraphoby). If 0, the ant never jump, if 1, the ant jumps
	 * randomly on the current CR, if larger the ant flees without any pheromone
	 * or edge weight consideration of this number of vertices.
	 */
	@DefineParameter(name = "ant.params.jump")
	public int jump = -1;

	/**
	 * Threshold for the number of ants on a vertex above which the vertex is
	 * considered over populated [antsPerVertex,n[.
	 */
	@DefineParameter(name = "ant.params.overPopulated")
	public int overPopulated = -1;

	/**
	 * Minimal percentage of pheromone of the ant colour that must be present
	 * for the ant to keep a normal behaviour. Under this threshold, the ant
	 * becomes agoraphobic and jumps.
	 */
	@DefineParameter(name = "ant.params.agoraphobia")
	public float agoraphobia = -1f;

	/**
	 * Quantity of pheromone dropped by one ant at each edge traversal ]0,n[.
	 * Note that this number is not used actually since all ants consider
	 * pheromones using ratios, never direct quantitative values.
	 */
	//@DefineParameter(name = "ant.params.pheromoneDrop")
	//public float pheromoneDrop = Float.NaN;

	/**
	 * Number of ants per vertex [multiple of colours]. This is the number of
	 * ants created when a vertex appears or deleted when a vertex disappears.
	 * This number may be modified by a CR power factor.
	 */
	@DefineParameter(name = "ant.params.antsPerVertex", min = 0)
	public int antsPerVertex = -1;

	/**
	 * Number of ants to create per vertex for each colour.
	 */
	@DefineParameter(name = "ant.params.antsPerVertexPerColony", min = 0)
	public int antsPerVertexPerColony = -1;

	@DefineParameter(name = "ant.params.globalFilePrefix")
	protected String globalFilePrefix = "";

	@DefineParameter(name = "ant.params.measuresOutput")
	protected boolean measuresOutput = false;

	@DefineParameter(name = "ant.params.computedMeasures")
	protected String computedMeasures = "";

	@DefineParameter(name = "ant.params.outputMeasuresPath")
	protected String outputMeasuresPath = "%prefix%measures.dat";

	@DefineParameter(name = "ant.params.dropOn")
	protected DropOn dropPheromoneOn = DropOn.EDGES;
	
	public AntParams() {
		defaults();
	}

	/**
	 * Set all parameters to their default values.
	 */
	public void defaults() {
		randomSeed = System.currentTimeMillis();
		rho = 0.86f;
		//pheromoneDrop = 0.1f;
		alpha = 1.0f;
		beta = 3.0f;
		antsPerVertex = 8;
		antsPerVertexPerColony = 8;
		overPopulated = 16;
		agoraphobia = 0.2f;
		jump = 1;
		dropPheromoneOn = DropOn.EDGES;
	}

	public void randomize() {
		randomSeed = System.currentTimeMillis();

		Random random = new Random(randomSeed);
		
		rho = 1 - random.nextFloat() * 0.25f;
		//pheromoneDrop = random.nextFloat() * 0.25f + 0.01f;
		alpha = random.nextFloat() * 5;
		beta = random.nextFloat() * 5;
	}

	public boolean isMeasuresOutput() {
		return measuresOutput;
	}

	public String getComputedMeasures() {
		return computedMeasures;
	}

	public String getOutputMeasuresPath() {
		return formatPath(outputMeasuresPath);
	}
	
	public DropOn getDropPheromoneOn() {
		return dropPheromoneOn;
	}

	protected String formatPath(String path) {
		if (path.contains("%prefix%")) {
			path = path.replace("%prefix%", globalFilePrefix);
		}

		return path;
	}
}
