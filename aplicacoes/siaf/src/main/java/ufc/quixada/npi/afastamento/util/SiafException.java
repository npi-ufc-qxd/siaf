package ufc.quixada.npi.afastamento.util;

public class SiafException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private String message;

	public SiafException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
