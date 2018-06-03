import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Archivo { 
	String leido="";
	
	public void escribir ( String texto ){
		File f=new File ("codigopl0.EJE");
		FileWriter w;
		try {
			w = new FileWriter(f);
			w.write(texto);
			w.close();
		} catch (IOException e) {
			System.out.println("No se puedo escribir el archivo");
			e.printStackTrace();
		}
	}
	
	public void leer(){
		 File file = new File("TeTruena.txt");
		    Scanner s;
            try {
				s= new Scanner(file);
				while (s.hasNextLine()) {
	                leido = leido+"\n"+s.nextLine();
	            }
	            s.close();
			} catch (FileNotFoundException e) {
				System.out.println("No existe el archivo");
				e.printStackTrace();
			}
	}
	
}