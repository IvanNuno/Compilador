
public class Variable {
	private String nombre;
	private String clase;
	private String tipo;
	private String dimen1;
	private String dimen2;
	private int num_dimen;
	private boolean inicializada;

	public Variable() {
		super();
	}
	
	public Variable( String nombre, String clase, String tipo, String dimen1, String dimen2, int num_dimen, boolean inicializada ) {
		super();
		this.nombre = nombre;
		this.clase = clase;
		this.tipo = tipo;
		this.dimen1 = dimen1;
		this.dimen2 = dimen2;
		this.num_dimen = num_dimen;
		this.inicializada = inicializada;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDimen1() {
		return dimen1;
	}

	public void setDimen1(String dimen1) {
		this.dimen1 = dimen1;
	}

	public String getDimen2() {
		return dimen2;
	}

	public void setDimen2(String dimen2) {
		this.dimen2 = dimen2;
	}
	
	public int getNum_dimen() {
		return num_dimen;
	}

	public void setNum_dimen(int num_dimen) {
		this.num_dimen = num_dimen;
	}

	public boolean isInicializada() {
		return inicializada;
	}

	public void setInicializada(boolean inicializada) {
		this.inicializada = inicializada;
	}

}
