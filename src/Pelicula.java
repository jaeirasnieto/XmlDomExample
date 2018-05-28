import java.util.List;

public class Pelicula {

	private String ip;
	private String langs;
	private String titulo;
	private Short duracion;
	private List<String> generos;
	private List<Reparto> reparto;
	
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}
	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	/**
	 * @return the duracion
	 */
	public Short getDuracion() {
		return duracion;
	}
	/**
	 * @param duracion the duracion to set
	 */
	public void setDuracion(Short duracion) {
		this.duracion = duracion;
	}
	/**
	 * @return the generos
	 */
	public List<String> getGeneros() {
		return generos;
	}
	/**
	 * @param generos the generos to set
	 */
	public void setGeneros(List<String> generos) {
		this.generos = generos;
	}
	/**
	 * @return the reparto
	 */
	public List<Reparto> getReparto() {
		return reparto;
	}
	/**
	 * @param reparto the reparto to set
	 */
	public void setReparto(List<Reparto> reparto) {
		this.reparto = reparto;
	}
	/**
	 * @return the langs
	 */
	public String getLangs() {
		return langs;
	}
	/**
	 * @param langs the langs to set
	 */
	public void setLangs(String langs) {
		this.langs = langs;
	}
	
}
