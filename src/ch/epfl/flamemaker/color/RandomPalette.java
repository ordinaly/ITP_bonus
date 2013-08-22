package ch.epfl.flamemaker.color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
/**
 * RandomPalette se comporte comme InterpolatedPalette, sauf que les couleurs de la {@link InterpolatedPalette liste} <br />
 * Elle possede un attribut <code><b>palette</b></code> de type <code>InterpolatedPalette</code>.
 * @see InterpolatedPalette
 */
public class RandomPalette implements Palette, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2438390480602608092L;
	private InterpolatedPalette palette;
	
	public RandomPalette(int nbColors) {
		if(nbColors < 2)
			throw new IllegalArgumentException("Il faut au moins deux couleurs pour interpoler !");
		ArrayList<Color> colorList = new ArrayList<Color>();
		Random r = new Random();
		for(int i=0; i<nbColors; i++) {
			colorList.add(new Color(r.nextDouble(), r.nextDouble(), r.nextDouble()));
		}
		palette = new InterpolatedPalette(colorList);
	}
	
	private RandomPalette(InterpolatedPalette palette) {
		this.palette = palette;
	}
	
	/**
	 * Retourne la couleur associée a l'index de couleur donne.
	 * @param index l'index de la couleur entre 0 et 1.
	 * @see Color
	 * @see Palette#colorForIndex(double)
	 */
	@Override
	public Color colorForIndex(double index) {
		return palette.colorForIndex(index);
	}
	
	public RandomPalette clone() {
		return new RandomPalette(palette);
	}
}
