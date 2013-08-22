package ch.epfl.flamemaker.color;
/**
 * L'interface Palette modelise les palettes.
 */
public interface Palette {
	/**
	 * Retourne la couleur associee a l'index de couleur donne.
	 * @param index l'index de la couleur entre 0 et 1.
	 * @return la couleur a l'index donne.
	 * @see Color
	 */
	public Color colorForIndex(double index);
	public Palette clone();
}
