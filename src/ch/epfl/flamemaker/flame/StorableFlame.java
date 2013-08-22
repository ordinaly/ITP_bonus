package ch.epfl.flamemaker.flame;

import java.io.Serializable;
import java.util.List;

import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class StorableFlame implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -527455419712802593L;
	private String name;
	private List<FlameTransformation> flameTransfoList;
	private Rectangle frame;
	private Palette pal;
	
	public StorableFlame(PresetFlame flame) {
		name = flame.getName();
		flameTransfoList = flame.getObservableFlameBuilder().getTransfoListCopy();
		frame = flame.getFrame();
		pal = flame.getPalette();
	}
	
	public PresetFlame restoreFlame() {
		return new PresetFlame(name, flameTransfoList, frame, pal);
	}
}
