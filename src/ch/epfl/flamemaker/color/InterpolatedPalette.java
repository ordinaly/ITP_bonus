package ch.epfl.flamemaker.color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * La classe <code>InterpolatedPalette</code> interpole entre plusieurs couleurs. <br />
 * Elle possede un attribut <code><b>colorList</b></code> qui contient la liste des couleurs de la palette.
 * @see Palette
 * @see Color
 */
public class InterpolatedPalette implements Palette, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4733588906933207115L;
	private List<Color> colorList;
	
	/**
	 * Le constructeur de la classe InterpolatedPalette retourne la couleur associee a l'index de couleur donne.
	 * @param colorList la liste des couleurs a interpoler
	 * @throws IllegalArgumentException si la liste contient moins de deux couleurs
	 */
	public InterpolatedPalette(List<Color> colorList) {
		if(colorList.size() < 2)
			throw new IllegalArgumentException("Il faut au moins deux couleurs pour interpoler !");
		this.colorList = new ArrayList<Color>(colorList);
	}
	
	/**
	 *  <p>Retourne la couleur associee a l'index de couleur donne.</p> 
	 *  <p>Retourne la premiere couleur de la liste lorsque l'index vaut 0, la derniere lorsqu'il vaut 1. <br />
	 *  Lorsque l'index pointe entre deux couleurs on les melange en proportion appropriee.</p>
	 * @param index l'index de couleur entre 0 et 1.
	 * @return la couleur <code>Color</code> de la liste associee a l'index donne.
	 * @throws IllegalArgumentException si l'index n'est pas entre 0 et 1.
	 */
	@Override
	public Color colorForIndex(double index) {
		if(index < 0 || index > 1)
			throw new IllegalArgumentException("L'index n'est pas valide !" + '\n' + "index : " + index);
		double listIndex = index*(colorList.size()-1);
		int floorIndex = (int) listIndex; 
		if(floorIndex == listIndex) {
			return colorList.get(floorIndex);
		}
		else {
			double proportion = listIndex - floorIndex;
			return colorList.get(floorIndex+1).mixWith(colorList.get(floorIndex), proportion);
		}
	}

	public static InterpolatedPalette generateRGBPal() {
		List<Color> rgbColorList = new ArrayList<Color>();
		rgbColorList.add(new Color(1, 0, 0));
		rgbColorList.add(new Color(0, 1, 0));
		rgbColorList.add(new Color(0, 0, 1));
		return new InterpolatedPalette(rgbColorList);
	}
	
	public InterpolatedPalette clone() {
		List<Color> newList = new ArrayList<Color>();
		newList.addAll(colorList);
		return new InterpolatedPalette(colorList);
	}
}
