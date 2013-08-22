package ch.epfl.flamemaker.flame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class PresetFlame implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4457511530685761285L;
	private Set<TransfoListObserver> observers;
	private String name;
	private ObservableFlameBuilder flameBuilder;
	private Rectangle frame;
	private Palette pal;
	private JList<String> listTransfos;
	private int selectedTransformationIndex;
	
	public PresetFlame(String name, List<FlameTransformation> flameTransfoList, Rectangle frame, Palette pal) {
		observers = new HashSet<TransfoListObserver>();
		this.name = name;
		flameBuilder = new ObservableFlameBuilder(new Flame(flameTransfoList));
		this.frame = frame;
		this.pal = pal;
		listTransfos = new JList<String>(new TransformationsListModel());
		listTransfos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listTransfos.setVisibleRowCount(3);
		selectedTransformationIndex = 0;
		listTransfos.setSelectedIndex(0);
		listTransfos.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				setSelectedTransformationIndex(listTransfos.getSelectedIndex());
			}
		});
	}
	
	public void addObserver(TransfoListObserver o) {
		observers.add(o);
	}
	
	public void removeObserver(TransfoListObserver o) {
		observers.remove(o);
	}
	
	public void notifyObservers() {
		for(TransfoListObserver o : observers) {
			o.update();
		}
	}
	
	@Override
	public PresetFlame clone() {
		List<FlameTransformation> newList = flameBuilder.getTransfoListCopy();
		return new PresetFlame(name, newList, new Rectangle(frame.center(), frame.width(), frame.height()), pal.clone());
	}
	
	public static PresetFlame newEmptyFlame(String name) {
		List<FlameTransformation> list = new ArrayList<FlameTransformation>();
		list.add(new FlameTransformation(AffineTransformation.IDENTITY, new double[] {1, 0, 0, 0, 0, 0}));
		return new PresetFlame(name, list, new Rectangle(new Point(0, 0), 5, 5), InterpolatedPalette.generateRGBPal());
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public AffineTransformation getSelectedTransformation() {
		return flameBuilder.affineTransformation(selectedTransformationIndex);
	}
	
	public void setSelectedTransformationIndex(int index) {
		checkIndexTransfo(index);
		selectedTransformationIndex = index;
		notifyObservers();
	}
	
	public int getSelectedTransformationIndex() {
		return selectedTransformationIndex;
	}
	
	public void composeSelectedTransformation(AffineTransformation at) {
		AffineTransformation a = flameBuilder.affineTransformation(selectedTransformationIndex);
		flameBuilder.setAffineTransformation(selectedTransformationIndex, at.composeWith(a));
	}
		
	public ObservableFlameBuilder getObservableFlameBuilder() {
		return flameBuilder;
	}
	
	public Rectangle getFrame() {
		return frame;
	}
	
	public Palette getPalette() {
		return pal;
	}
	
	public JList<String> getListTransfos() {
		return listTransfos;
	}
	
	private void checkIndexTransfo(int index) {
		if(index < 0 || index > flameBuilder.transformationCount()-1)
			throw new IllegalArgumentException("L'index n'est pas valide !");
	}
	
	// Demander si il faut parametriser avec String (en 1.7 oui, en 1.6 non)
	@SuppressWarnings("serial")
	public class TransformationsListModel extends AbstractListModel<String> {

		public void addTransformation() {
			flameBuilder.addTransformation(new FlameTransformation(AffineTransformation.IDENTITY, new double[] {1, 0, 0, 0, 0, 0}));
			fireIntervalAdded(this, getSize(), getSize());
		}
		
		public void removeTransformation(int index) {
			checkIndexTransfo(index);
			flameBuilder.removeTransformation(index);
			fireIntervalRemoved(this, index, index);
		}
		
		@Override
		public String getElementAt(int index) {
			checkIndexTransfo(index);
			return "Transformation n° " + (index + 1);
		}

		@Override
		public int getSize() {
			return flameBuilder.transformationCount();
		}
	}
	
	public interface TransfoListObserver {
		public void update();
	}
}
