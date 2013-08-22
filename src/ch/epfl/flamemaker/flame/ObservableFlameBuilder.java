package ch.epfl.flamemaker.flame;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;

public class ObservableFlameBuilder {
	private Set<Observer> observers;
	private Flame.Builder flameBuilder;
	
	public ObservableFlameBuilder(Flame flame) {
		observers = new HashSet<Observer>();
		this.flameBuilder = new Flame.Builder(flame);
	}
	
	public void addObserver(Observer o) {
		observers.add(o);
	}
	
	public void removeObserver(Observer o) {
		observers.remove(o);
	}
	
	public void notifyObservers() {
		for(Observer o : observers) {
			o.update();
		}
	}
	
	public Flame build() {
		return flameBuilder.build();
	}
	
	public int transformationCount() {
		return flameBuilder.transformationCount();
	}
	
	public void addTransformation(FlameTransformation f) {
		flameBuilder.addTransformation(f);
		notifyObservers();
	}
	
	public void removeTransformation(int index) {
		flameBuilder.removeTransformation(index);
		notifyObservers();
	}
	
	public AffineTransformation affineTransformation(int index) {
		return flameBuilder.affineTransformation(index);
	}
	
	public void setAffineTransformation(int index, AffineTransformation newTransformation) {
		flameBuilder.setAffineTransformation(index, newTransformation);
		notifyObservers();
	}
	
	public double variationWeight(int index, Variation variation) {
		return flameBuilder.variationWeight(index, variation);
	}
	
	public void setVariationWeight(int index, Variation variation, double newWeight) {
		if(flameBuilder.variationWeight(index, variation) != newWeight) {
			flameBuilder.setVariationWeight(index, variation, newWeight);
			notifyObservers();
		}
	}
	
	public List<FlameTransformation> getTransfoListCopy() {
		return flameBuilder.getTransfoListCopy();
	}
	
	public interface Observer {
		public void update();
	}
}
