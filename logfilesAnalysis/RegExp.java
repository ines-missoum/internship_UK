package logfilesAnalysis;

public class RegExp {

	private String name;
	private String expr;

	public RegExp(String regExp) {

		String[] parts = regExp.split("[\t| ]");
		this.name = parts[0];
		this.expr = parts[1];

	}

	public String getName() {
		return name;
	}

	public String getExpr() {
		return expr;
	}

	
}
