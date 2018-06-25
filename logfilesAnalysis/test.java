package logfilesAnalysis;

public class test {

	public static void main(String[] args) {
		RegExp rg = new RegExp("USERNAME	[a-zA-Z0-9._-]+");
		System.out.println(rg.getName());
		System.out.println(rg.getExpr());
	}

}
