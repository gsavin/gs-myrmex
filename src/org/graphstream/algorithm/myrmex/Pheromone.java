package org.graphstream.algorithm.myrmex;

public class Pheromone<T> {
	protected final int marker;
	protected float load;
	protected T data;
	
	public Pheromone(int marker, float load){
		this(marker, load, null);
	}
	
	public Pheromone(int marker, float load, T data){
		this.marker = marker;
		this.load = load;
		this.data = data;
	}
	
	public int getMarker() {
		return marker;
	}
	
	public float getLoad() {
		return load;
	}
	
	public T getData() {
		return data;
	}
	
	public void setLoad(float load) {
		this.load = load;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
	public void fusion(Pheromone<?> pheromone) {
		if(pheromone.marker != marker)
			throw new InvalidMarkerException();
		
		fusionData(pheromone);
		load += pheromone.getLoad();
	}
	
	public void evaporate(float evaporation) {
		this.load *= evaporation;
	}
	
	protected void fusionData(Pheromone<?> pheromone) {
	}
	
	public Pheromone<T> clone() {
		return new Pheromone<T>(marker,load,data);
	}

	private static class EmptyPheromone extends Pheromone<Object> {
		EmptyPheromone() {
			super(0,0);
		}
		
		public float getLoad() {
			return 0;
		}
		
		public Object getData() {
			return null;
		}
	}
	
	public static final Pheromone<Object> EMPTY = new EmptyPheromone();
}
