/*
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.graphstream.algorithm.myrmex.policy;

import org.graphstream.algorithm.myrmex.AntContext;
import org.graphstream.algorithm.myrmex.AntNode;
import org.graphstream.algorithm.myrmex.AntParams;
import org.graphstream.algorithm.myrmex.Colony;
import org.graphstream.algorithm.myrmex.PopulationPolicy;

public class ProportionalPopulationPolicy implements PopulationPolicy {
	// Attributes

	int lastColony = 0;
	AntContext ctx;
	int antCount;

	// Constructors

	/**
	 * New popol.
	 * 
	 */
	public ProportionalPopulationPolicy() {
	}

	public void init(AntContext ctx) {
		this.ctx = ctx;
	}

	public int getAntCount() {
		return antCount;
	}

	public void step() {
		antCount = 0;
		for (Colony c : ctx.eachColony())
			antCount += c.getAntCount();
	}

	// Commands

	public void nodeAdded(AntNode node) {
		// Allocate / colours

		AntParams params = ctx.getAntParams();

		// if( params.colors > params.antsPerVertex )
		// antco2.listener.error( "cannot allocate enough ants: there are " +
		// params.colors +"
		// colours but only " + params.antsPerVertex + " ants per vertex are
		// allowed", null, true );

		int nClr = ctx.getColonyCount();

		if (nClr == 0)
			return;

		for (Colony color : ctx.eachColony()) {
			int nAntsPerClr = params.antsPerVertex / ctx.getColonyCount();

			if (params.antsPerVertexPerColony > 0)
				nAntsPerClr = params.antsPerVertexPerColony;

			// If there are more ants per node than colonies, we merely divide
			// the number of
			// ants per node by the number of colonies to determine how many
			// ants to put on a node.
			//
			// Else, we must put a little of each colony on different node in
			// order to keep a
			// proportional
			// number of ants and the same number of ants in each colony.

			if (nAntsPerClr <= 0) {
				int beg = lastColony;
				int end = (lastColony + params.antsPerVertex) % nClr;

				if (end < beg) {
					if (color.getIndex() >= beg || color.getIndex() < end)
						addAnt(color, null, node);
				} else {
					if (color.getIndex() >= beg && color.getIndex() < end)
						addAnt(color, null, node);
				}
			} else {
				for (int i = 0; i < nAntsPerClr; ++i)
					addAnt(color, null, node);
			}
		}

		lastColony = (lastColony + params.antsPerVertex) % nClr;
	}

	public void nodeRemoved(AntNode node) {
		AntParams params = ctx.getAntParams();

		for (Colony color : ctx.eachColony()) {
			int nAntsPerClr = params.antsPerVertex / ctx.getColonyCount();

			if (params.antsPerVertexPerColony > 0)
				nAntsPerClr = params.antsPerVertexPerColony;

			removeAnts(color, nAntsPerClr);
		}
	}

	public void colonyAdded(Colony color) {
		int colonyCount = ctx.getColonyCount(); // Actual colour count (counting
												// the
		// new colony).
		int nodeCount = ctx.getNodeCount(); // Actual node count.
		int antCount = ctx.getAntCount(); // Actual ant count.

		if (nodeCount == 0)
			return;

		int averageAntCountPerColony = antCount / colonyCount; // How many ants
																// per colour
																// now.
		int toRemovePerColor = colonyCount > 1 ? averageAntCountPerColony
				/ (colonyCount - 1) : 0;
		// How many ants of
		// each old colour.
		// to remove.

		// Remove ants in each already present colony.

		for (Colony c : ctx.eachColony()) {
			if (c != color)
				removeAnts(c, toRemovePerColor);
		}

		// Add ants in the new colony.

		int toAddPerNode = averageAntCountPerColony / nodeCount;
		int added = 0;

		for (AntNode node : ctx.eachNode()) {
			for (int i = 0; i < toAddPerNode; ++i) {
				addAnt(color, null, node);
				added++;
			}
		}

		// Due to float -> integer conversion we can loose ants (if we must
		// for example allocate 1.2 ants per node (always less than nodeCount).

		int remains = averageAntCountPerColony - added;

		if (remains > 0) {
			for (AntNode node : ctx.eachNode()) {
				if (remains <= 0)
					break;

				addAnt(color, null, node);
				added++;
				remains--;
			}
		}
	}

	public void colonyRemoved(Colony color) {
		antCount -= color.getAntCount();
	}

	protected void addAnt(Colony colony, String id, AntNode node) {
		colony.addAnt(id, node);
		antCount++;
	}

	protected void removeAnts(Colony colony, int count) {
		colony.removeAnts(count);
		antCount -= count;
	}
}