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

import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Graph;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.thread.ThreadProxyPipe;

import org.graphstream.algorithm.DefineParameter;
import org.graphstream.algorithm.Parameter;
import org.graphstream.algorithm.MissingParameterException;
import org.graphstream.algorithm.InvalidParameterException;
import static org.graphstream.algorithm.Parameter.parameter;
import static org.graphstream.algorithm.Parameter.processParameters;

public abstract class AntAlgorithm extends SinkAdapter implements
		DynamicAlgorithm {
	protected AntContext context;
	protected ThreadProxyPipe proxy;

	@DefineParameter(name = "graph", optional = false)
	protected Graph registeredGraph;

	public AntAlgorithm() {
		this.context = getDefaultContext();
	}
	
	public AntAlgorithm(AntContext ctx){
		this.context = ctx;
	}

	public AntContext getDefaultContext() {
		return new AntContext();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends AntContext> T getContext() {
		return (T) context;
	}

	public Graph getRegisteredGraph() {
		return registeredGraph;
	}

	public void init(Parameter... params) {
		if (registeredGraph != null) {
			registeredGraph.removeSink(this);
		}
		
		try {
			processParameters(new Object[] { this, context.getAntParams() },
					params);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (MissingParameterException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		registeredGraph.addAttributeSink(this);

		if (proxy == null) {
			proxy = new ThreadProxyPipe(registeredGraph);
			proxy.addSink(context.internalGraph);
		}

		context.init();
	}

	public void init(Graph graph) {
		init(parameter("graph", graph));
	}

	public void compute() {
		proxy.pump();
		context.step();
		proxy.pump();
	}

	public void terminate() {
		if (registeredGraph != null)
			registeredGraph.removeSink(proxy);

		proxy.removeSink(context.internalGraph);
	}
}
