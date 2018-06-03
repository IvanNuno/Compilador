import java.util.Stack;

public class Analizadores {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////     LEXICO     //////////////////////////////////////////////////////////
	final static int ERR=-1, ACP = 999;
	static String token, lex;
	static int idx=0;
	static int renglon=1;
	public String entrada ="";
	public static String errores ="";
	
	final static int matran[][]= {
    // ESTADO	letra digi del  opa  rel  .     =    _    "    DU	  /	   *    \n   resto
    	/*0*/  {1, 	  2,   5,   8,   11,  5,    9,   1,   6  , ERR,   15,  8,   ERR,  ERR},
    	/*1*/  {1, 	  1,   ACP, ACP, ACP, ERR, ACP,  1,   ACP, ACP,  ACP, ACP,  ACP,  ERR},    			
    	/*2*/  {ACP,  2,   ACP, ACP, ACP, 3,   ACP,  ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},      			
    	/*3*/  {ERR,  4,   ERR, ERR, ERR, ERR, ERR,  ERR, ERR, ACP,  ERR, ERR,  ACP,  ERR}, 
    	/*4*/  {ACP,  4,   ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},
    	/*5*/  {ACP,  ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},
    	/*6*/  {6,    6,   6,   6,   6,   6,   6,    6,   7,   6,     6,   6,    6,    6 },
    	/*7*/  {ACP,  ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},
    	/*8*/  {ACP,  ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},
    	/*9*/  {ACP,  ACP, ACP, ACP, ACP, ACP, 10,   ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},
       /*10*/  {ACP,  ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},
       /*11*/  {ACP,  ACP, ACP, ACP, ACP, ACP, 12,   ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},
       /*12*/  {ACP,  ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},
       /*13*/  {ACP,  ACP, ACP, ACP, ACP, ACP, 14,   ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},   
       /*14*/  {ACP,  ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},
       
       /*15*/  {ACP,  ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,   16,  18,  ACP,  ERR},
       /*16*/  { 16,   16,  16,  16,  16,  16,  16,   16,  16,  16,   16,  16,   17,  16 },
       /*17*/  {ACP,  ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR},
       /*18*/  { 18,   18,  18,  18,  18,  18,  18,   18,  18,  18,   18,  19,   18,  18 },
       /*19*/  {ERR,  ERR, ERR, ERR, ERR, ERR, ERR,  ERR, ERR, ERR,   20, ERR,  ERR,  ERR},
       /*20*/  {ACP,  ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP, ACP,  ACP, ACP,  ACP,  ERR}
	};
	
	final static String reservadas[] = {"para", "mientras", "en", "inicio", "fin", "regresa", "si", "sino", "seleccion",
		                                "caso", "interrumpe", "otro", "lee", "imprime", "imprimenl", "constante", 
		                                "entero", "decimal", "logico", "cadena", "notipo"};
	final static String oplogicos[] = { "no", "y", "o"};
	final static String ctelogicas[] = {"verdadero", "falso"};
	
	
	public static boolean espalRes( String p ) {
		for(int i = 0; i < reservadas.length; i++)
			if( p.equalsIgnoreCase(reservadas[i])) return true;
		return false;
	}
	
	public static boolean esOlog(String p ) {
		for(int i = 0; i < oplogicos.length; i++) 
			if (p.equalsIgnoreCase(oplogicos[i])) return true;		
		return false;
	}
	
	public static boolean esClog(String p ) {
		for(int i = 0; i < ctelogicas.length; i++) 
			if (p.equalsIgnoreCase(ctelogicas[i])) return true;		
		return false;
	}
	
	public static int colCar( char c, int est ) {
		if( Character.isAlphabetic(c) ) return 0;
		if( Character.isDigit(c) ) return 1;
		if( c == '(' || c == ')' || c == '[' || c == ']' || c == ';' || c == ',' || c == ':') return 2;
		if( c == '+' || c == '-' || c == '%' || c == '^' ) return 3;
		if( c == '<' || c == '>' || c == '!' ) return 4;
		if( c == '.' ) return 5;
		if( c == '=' ) return 6;
		if( c == '_' ) return 7;
		if( c == '"' ) return 8;
		if( c == ' ' || c == '\t' ) return 9;
		if( c == '/' ) return 10;
		if( c == '*' ) return 11;
		if( c == '\n' ) return 12;
		if(( c == '?' || c == '#' || c == '$' || c == '&' || c == '\'' || c == '@' || c == '\\' || c == '^' || c == '{' 
				|| c == '}' || c == '~' || c == '¿' || c == '\'' || c == '´') && ( est==6 ) ) return 13;
		errores=errores+"linea "+renglon+"        simbolo ilegal en el lenguaje: "+c+"\n";
		return ERR;
	}	
	
	public String lexer() {
		int estado = 0, estAnt=0;
		String lexema="";
		while( idx < entrada.length() && estado != ERR && estado != ACP ) {
			char c = entrada.charAt(idx++);
			while ( estado == 0 && (c == ' ' || c == '\t' || c == '\n') && idx < entrada.length() ) {if(c == '\n') renglon++; c = entrada.charAt(idx++);}
			int col = colCar( c , estado );
			if( col >= 0 ) {
				estado = matran[estado][col];
				if ( estado != ERR && estado != ACP ) {
					estAnt = estado;
					lexema += c;
					if(c == '\n') renglon++;
				}
			}
			else estado = ERR;
		}
		if( estado == ACP ) idx--;
		if( estado != ERR && estado != ACP) estAnt = estado;
		token="NOTKEN";
		switch( estAnt ) {
			case 1: token="Identi"; 
			        if( espalRes( lexema ) ) token="PalRes";
			        else if( esOlog( lexema )) token = "OpLog";
			        else if( esClog( lexema )) token = "CteLog";
			        break;
			case 2: token="CteEnt"; break;
			case 4: token="CteDec"; break;
			case 5: token="Delimi"; break;
			case 7: token="CteCad"; break;
			case 8: token="OpArit"; break;
			case 9: token="OpAsig"; break;
			case 10: case 11: case 12: case 14: token="OpRela"; break;
			case 15: token="OpArit"; break;
			case 17: token="ComLin"; break;
			case 20: token="ComMul"; break;
		}
		return lexema;
	}
	/////////////////////////////////////////     LEXICO     //////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////     SEMANTICA     /////////////////////////////////////////////////////////
	Stack <String> pila=new Stack <String>();
	Variable[] tab = new Variable[500];
	int indice=0;
	
	String nombre="";
	String clase="";
	String tipo="";
	String dimen[]={"",""};
	int num_dimen=0;
	boolean inicializada=false;
	String ambito="";
	
	final static String cTipo[]= {
		"E=E", "A=A", "D=D", "L=L", "D=E", 
        "E+E", "E+D", "D+E", "D+D", "A+A", "C+C",
        "E-E", "E-D", "D-E", "D-D",
        "E*E", "E*D", "D*E", "D*D",
        "E/E", "E/D", "D/E", "D/D",
        "E%E", "-E", "-D",
        "LyL", "LoL", "noL",
        "E>E", "D>E", "E>D", "D>D",
        "E<E", "D<E", "E<D", "D<D",
        "E>=E", "D>=E", "E>=D", "D>=D",
        "E<=E", "D<=E", "E<=D", "D<=D",
        "E!=E", "D!=E", "E!=D", "D!=D", "A!=A",
        "E==E", "D==E", "E==D", "D==D", "A==A"	
        
	};
	final static String tipoR[]= {
		"", "", "", "", "",
        "E", "D", "D", "D", "A", "C",
        "E", "D", "D", "D",
        "E", "D", "D", "D",
        "D", "D", "D", "D",
        "E", "E", "D",
        "L", "L", "L",
        "L", "L", "L", "L",
        "L", "L", "L", "L",
        "L", "L", "L", "L",
        "L", "L", "L", "L",
        "L", "L", "L", "L", "L",
        "L", "L", "L", "L", "L"	
	};
	
	String tipo_encontrado="";
	String clase_encontrado="";
	String dimen1_encontrado="";
	String dimen2_encontrado="";
	boolean inic_encontrado=false;
	int dimens_encontrado=0;
	
	public void inic_correcto( String Iden1 ){
		boolean encontrado=false;
		String Iden2=Iden1+"$"+ambito;
		for(int i=0; i<indice; i++){	//buscar con ambito
			if(tab[i].getNombre().equalsIgnoreCase(Iden2)) {
				tab[i].setInicializada(true);
				encontrado=true;
			}
		}
		if(!encontrado){	//buscar sin ambito
			for(int i=0; i<indice; i++)
				if(tab[i].getNombre().equalsIgnoreCase(Iden1)) 
					tab[i].setInicializada(true);
		}
	}
	
	String idenpl0="";
	
	public boolean encontrarIden( String Iden1 ) {
		boolean encontrado=false;
		String Iden2=Iden1+"$"+ambito;
		for(int i=0; i<indice; i++){	//buscar con ambito
			if(tab[i].getNombre().equalsIgnoreCase(Iden2)) {
				tipo_encontrado=tab[i].getTipo();
				clase_encontrado=tab[i].getClase();
				inic_encontrado=tab[i].isInicializada();
				dimens_encontrado=tab[i].getNum_dimen();
				dimen1_encontrado=tab[i].getDimen1();
				dimen2_encontrado=tab[i].getDimen2();
				encontrado=true;
				idenpl0=tab[i].getNombre();
			}
		}
		if(!encontrado){	//buscar sin ambito
			for(int i=0; i<indice; i++){
				if(tab[i].getNombre().equalsIgnoreCase(Iden1)) {
					tipo_encontrado=tab[i].getTipo();
					clase_encontrado=tab[i].getClase();
					inic_encontrado=tab[i].isInicializada();
					dimens_encontrado=tab[i].getNum_dimen();
					dimen1_encontrado=tab[i].getDimen1();
					dimen2_encontrado=tab[i].getDimen2();
					encontrado=true;
					idenpl0=tab[i].getNombre();
				}
			}
		}
		return encontrado;
	}
	
	
	public void agregarvar() {
			boolean repetido=false;
			for(int i=0; i<indice; i++){
				if(tab[i].getNombre().equalsIgnoreCase(nombre)) {
					errorSem("        Variable "+nombre+" ya ha sido declarada anteriormente ");
					repetido=true;
					break;
				}
			}
			if( !repetido ) {
				if( (tipo.equalsIgnoreCase("C") || tipo.equalsIgnoreCase("D") || tipo.equalsIgnoreCase("L") || tipo.equalsIgnoreCase("S")) && dimen[0]!="0" ){
					errorSem("        Variables de tipo "+tipo+" no pueden ser declaradas como arreglos ");
				}
				else{
					tab[indice]=new Variable( nombre, clase, tipo, dimen[0], dimen[1], num_dimen, inicializada );
					indice++;
					//borrarSem();
				}
			}
			repetido=false;
	}
	
	public int asigCorrecta( String cad ) {
		for(int i=0; i<cTipo.length; i++) 
			if( cTipo[i].equalsIgnoreCase(cad)) return i;
		return -1;
	}
	
	public void borrarSem() {
		nombre="";  
		clase="";  
		tipo="";  
		dimen[0]="0";  
		dimen[1]="0";
		num_dimen=0;
		inicializada=false;
		for(int i=0; i<20; i++){
			para_tipo[i]="";
			para_nombre[i]="";
		}
		num_para=0;
		error_prgm=false;
		var_estatuto=false;
		ambito="";
		tipo_encontrado="";
		clase_encontrado="";
		inic_encontrado=false;
		dimen1_encontrado="";
		dimen2_encontrado="";
		dimens_encontrado=0;
	}
	
	public void errorSem( String des ) {
		 	if( !fin_prgm )
		 		errores=errores+"linea "+renglon+"        "+des+"\n";
	}
	///////////////////////////////////////     SEMANTICA     /////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 
    public void borrarVars() {
    	idx=0;
    	token=""; 
    	lex="";
    	renglon=1;
    	errores ="";
    	for(int i=0; i<indice; i++){
    		tab[indice].setNombre("");
    		tab[indice].setTipo("");
    		tab[indice].setClase("");
    		tab[indice].setDimen1("0");
    		tab[indice].setDimen2("0");
    	}
    	indice=0;
    	codigo_pl0="";
    	linea_pl0=1;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////      SINTAXIS     /////////////////////////////////////////////////////////
    public void sig_elemento() {
    	do{
    		lex=lexer();
    		if(token.equals("NOTKEN"))
    			lex=lexer();
    	}while( token.equals("ComLin") || token.equals("ComMul") );
    }
    
    public void error( String des ) {
    	if( !fin_prgm )
    		errores=errores+"linea "+renglon+"        "+des+lex+"\n";
    }
	
	public void lee() {
		boolean errorLeer=true;
		String aux_lee="";
		sig_elemento();
		if( !lex.equals("(") )
			error("        En lee  -  Se esperaba ( y llegó ");
		sig_elemento();
		if( !token.equals("Identi") )
			error("        En lee  -  Se esperaba identificador y llegó ");
		else
			aux_lee=lex;
		sig_elemento();
		if( lex.equals("[") ){
			if( dimens() ){
				if( encontrarIden(aux_lee) ){
					if( dimens_encontrado==0 ){
						errorSem("        La variable "+aux_lee+" no es de tipo arreglo");
					}
					else
					if( contador_dimen<dimens_encontrado ){
						errorSem("        Faltan dimensiones para "+aux_lee);
					}
					else
					if( contador_dimen>dimens_encontrado ){
						errorSem("        Exceso de dimensiones para "+aux_lee);
					}
					else{
						int a=0,b=0,c=0,d=0;
						boolean dimen_correcta=true;
						if(dimens_encontrado==2){
							if( idx_esnumero[0] ){
								a=Integer.parseInt(dimen1_encontrado);
								b=Integer.parseInt(dimen_actual[0]);
								if( a<b ){
									errorSem("        Indice de dimen1 fuera de rango: "+b);
									dimen_correcta=false;
								}
							}
							if( idx_esnumero[1] ){
								c=Integer.parseInt(dimen2_encontrado);
								d=Integer.parseInt(dimen_actual[1]);
								if( c<d ){
									errorSem("        Indice de dimen2 fuera de rango: "+d);
									dimen_correcta=false;
								}
							}
							if( dimen_correcta ){
								pila.push( tipo_encontrado );
								errorLeer=false;
							}
						}
						if(dimens_encontrado==1){
							if( idx_esnumero[0] ){
								a=Integer.parseInt(dimen1_encontrado);
								b=Integer.parseInt(dimen_actual[0]);
								if( a<b ){
									errorSem("        Indice fuera de rango: "+b);
								}
								else{
									pila.push( tipo_encontrado );
									errorLeer=false;
								}
							}
							else{
								pila.push( tipo_encontrado );
								errorLeer=false;
							}
						}
					}		
				}
				else 
					errorSem("        Variable "+aux_lee+" no ha sido declarada");
			}
		}
		else {
			if( encontrarIden(aux_lee) ){
				if( !clase_encontrado.equalsIgnoreCase("C") ){
					if( dimens_encontrado!=0 ){
						errorSem("        Variable "+aux_lee+" debe ser dimensionada: "+"("+dimens_encontrado+")dimensiones ");
					}
					else
						errorLeer=false;
				}
				else
					errorSem("        Variable "+aux_lee+" es constante y no puede ser reasignada");
			}
			else 
				errorSem("        Variable "+aux_lee+" no ha sido declarada");
		}		
		if( !lex.equals(")") )
			error("        En lee  -  Se esperaba ) y llegó ");
		sig_elemento();
		if( !errorLeer )
			inic_correcto( aux_lee );
		gen_cod( "OPR", aux_lee , "19" );
	}
	
	
	public void imprime( boolean nl ) {
		sig_elemento();
		if( !lex.equals("(") )
			error("        En imprime  -  Se esperaba ( y llegó ");
		sig_elemento();
		if( lex.equals(")") )
			sig_elemento();
		else{ 
			expr();
			if( lex.equals(",") ) {
				do{
					sig_elemento();
					expr();
				}while( lex.equals(",") );
			}
			if( !lex.equals(")") )
				error("        En imprime  -  Se esperaba ) y llegó ");
			sig_elemento();
		}
		if( nl ){
			gen_cod( "OPR", "0", "21" );
		}
		else{
			gen_cod( "OPR", "0", "20" );
		}
	}
	
	
	public void mientras() {
		sig_elemento();
		if( !lex.equals("(") )
			error("        En mientras  -  Se esperaba ( y llegó ");
		sig_elemento();
		int auxetiq=linea_pl0;
		expr();
		if( !errorExpr ){
			String elemento=pila.pop();
			if( !elemento.equalsIgnoreCase("L") ){
				errorSem("        Expresion para Mientras debe ser de tipo Logico ");
			}
		}
		if( !lex.equals(")") )
			error("        En mientras  -  Se esperaba ) y llegó ");
		sig_elemento();
		gen_cod( "JMC", "F", generarEtiq() );
		block();
		gen_cod( "JMP", "0", auxetiq+"" );
		resolverEtiq();
	}
	
	
	public void regresa() {
		String tipo_regresa="";
		sig_elemento();
		if( lex.equals(";") ){
			tipo_regresa="N";
			sig_elemento();
		}
		else{
			expr();
			if( !errorExpr )	tipo_regresa=pila.pop();
			if( !lex.equals(";") ){
				error("        En regresa  -  Se esperaba ; y llegó ");
			}
			sig_elemento();
		}
		if( !tipo_regresa.equalsIgnoreCase(tipo_funcion) ){
			errorSem("        Conflicto en regresa, valor de retorno debe de ser: "+tipo_funcion);
		}
		else
			hay_regresa=true;
		gen_cod( "STO", "0", ambito );
	}
	
	
	public void si() {
		sig_elemento();
		if( !lex.equals("(") )
			error("        En si  -  Se esperaba ( y llegó ");
		sig_elemento();
		expr();
		if( !errorExpr ){
			String elemento=pila.pop();
			if( !elemento.equalsIgnoreCase("L") ){
				errorSem("        Expresion para Si debe ser de tipo Logico ");
			}
		}
		if( !lex.equals(")") )
			error("        En si  -  Se esperaba ) y llegó ");
		sig_elemento();
		if( lex.equalsIgnoreCase("Hacer") )
			sig_elemento();
		if( lex.equalsIgnoreCase("inicio") ) {
			block();
			if(!lex.equals(";"))
				error("        En si  -  Se esperaba ; y llegó ");
			sig_elemento();
		}
		else
			unestatuto();		
		
		if( lex.equalsIgnoreCase("sino") ){
			sig_elemento();
			if( lex.equalsIgnoreCase("inicio") ) {
				block();
				if(!lex.equals(";"))
					error("        En sino  -  Se esperaba ; y llegó ");
				sig_elemento();
			}
			else
				unestatuto();		
		}
	}
	
	public void unestatuto() {
		if(lex.equals(";"))
			sig_elemento();
		else{
			if( lex.equalsIgnoreCase("entero") || lex.equalsIgnoreCase("decimal") || lex.equalsIgnoreCase("logico") || lex.equalsIgnoreCase("cadena") ){
				tipo=lex.charAt(0)+"";
				sig_elemento();
				if( !token.equals("Identi") )
					error("        En decvar  -  Se esperaba identificador y llegó ");
				else {
					nombre=lex+"$"+ambito;
				}
				sig_elemento();
				var_estatuto=true;
				vars();
			}
			if( token.equals("Identi") ) {
				aux_estatuto=lex;
				sig_elemento();
					if( lex.equals("(") ){
						aux_termino=aux_estatuto;
						if( lfunc() ){
							errorExpr=true;
							if( encontrarIden(func_llamada) ){
								pila.push( tipo_encontrado );
								errorExpr=false;
							}
							else {
								errorExpr=true;
								errorSem("        Funcion "+func_llamada+" no ha sido declarada");
							}	
						}
					}
					else
						asigna();
				if(!lex.equals(";"))
					error("        En estatuto  -  Se esperaba ; y llegó ");
				sig_elemento();
			}
			if( lex.equalsIgnoreCase("lee") )  {
				lee();
				if(!lex.equals(";"))
					error("        En estatuto  -  Se esperaba ; y llegó ");
				sig_elemento();
			}
			if( lex.equalsIgnoreCase("imprimenl") )  {
				imprime( true );
				if(!lex.equals(";"))
					error("        En estatuto  -  Se esperaba ; y llegó ");
				sig_elemento();
			}
			if( lex.equalsIgnoreCase("imprime") )  {
				imprime( false );
				if(!lex.equals(";"))
					error("        En estatuto  -  Se esperaba ; y llegó ");
				sig_elemento();
			}
			if( lex.equalsIgnoreCase("mientras") )	{
				mientras();
				if(!lex.equals(";"))
					error("        En estatuto  -  Se esperaba ; y llegó ");
				sig_elemento();
			}
			if( lex.equalsIgnoreCase("si") ) {
				si();
			}
			if( lex.equalsIgnoreCase("para") )	{
				para();
			}
			if( lex.equalsIgnoreCase("regresa") ) {
				regresa();
			}
			if( lex.equalsIgnoreCase("seleccion") )	{
				seleccion();
				if(!lex.equals(";"))
					error("        En estatuto  -  Se esperaba ; y llegó ");
				sig_elemento();
			}
		}
	}
	
	
	public void para() {
		sig_elemento();
		if( !token.equals("Identi") )
			error("        En para  -  Se esperaba identificador y llegó ");
		else{
			if( encontrarIden(lex) ){
				if( tipo_encontrado.equalsIgnoreCase("E") ){
					if( inic_encontrado ){
						if( clase_encontrado.equalsIgnoreCase("C") ){
							errorSem("        Variable "+lex+" es constante, no puede ser modificada");
						}
					}
					else
						errorSem("        Variable "+lex+" no ha sido inicializada");
				}
				else
					errorSem("        Variable "+lex+" debe ser tipo entero para ciclos");
			}
			else
				errorSem("        Variable "+lex+" no ha sido declarada");
		}
		sig_elemento();
		if( !lex.equalsIgnoreCase("en") )
			error("        En para  -  Se esperaba en y llegó ");
		sig_elemento();
		expr();
		if( !errorExpr ){
			String elemento=pila.pop();
			if( !elemento.equalsIgnoreCase("E") ){
				errorSem("        Valor a sumar en cada ciclo (en) debe ser Entero ");
			}
		}
		if( !lex.equals(".") )
			error("        En para  -  Se esperaba . entera y llegó ");
		sig_elemento();
		if( !lex.equals(".") )
			error("        En para  -  Se esperaba . entera y llegó ");
		sig_elemento();
		expr();
		if( !errorExpr ){
			String elemento=pila.pop();
			if( !elemento.equalsIgnoreCase("E") ){
				errorSem("        Valor limite en ciclo (. .) debe ser Entero ");
			}
		}
		if( lex.equalsIgnoreCase("inicio") ) {
			block();  
			if(!lex.equals(";"))
				error("        En para  -  Se esperaba ; y llegó ");
			sig_elemento();
		}
		else{
			unestatuto();	
		}		
	}
	
	
	public void seleccion() {
		String nombre="", tiposel="";
		boolean errorSel=false;
		sig_elemento();
		if( !lex.equals("(") )
			error("        En seleccion  -  Se esperaba ( entera y llegó ");
		sig_elemento();
		if( !token.equals("Identi") )
			error("        En seleccion  -  Se esperaba identificador y llegó ");
		else{
			nombre=lex;
			if( encontrarIden(nombre) ){
				if( inic_encontrado )
					tiposel=tipo_encontrado;
				else{
					errorSel=true;
					errorSem("        Variable "+lex+" no ha sido inicializada");
				}
			}
			else{
				errorSel=true;
				errorSem("        Variable "+lex+" no ha sido declarada");
			}
		}
		sig_elemento();
		if( !lex.equals(")") )
			error("        En seleccion  -  Se esperaba ) y llegó ");
		sig_elemento();
		if( !lex.equalsIgnoreCase("inicio") )
			error("        En seleccion  -  Se esperaba inicio y llegó ");
		sig_elemento();
		do{
			if( !lex.equalsIgnoreCase("caso") )
				error("        En seleccion  -  Se esperaba caso y llegó ");
			sig_elemento();
			if( !token.equals("CteEnt") && !token.equals("CteCad") && !token.equals("CteDec"))
				error("        En seleccion  -  Se esperaba cte para caso y llegó ");
			else{
				if( !errorSel ){
					if( !(token.charAt(3)+"").equalsIgnoreCase(tiposel) )
						errorSem("        tipo en caso debe ser "+tiposel);
				}
			}
			sig_elemento();
			if( !lex.equals(":") )
				error("        En seleccion  -  Se esperaba : y llegó ");
			sig_elemento();
			estatuto();
			if(lex.equalsIgnoreCase("interrumpe"))
				sig_elemento();
			if(lex.equalsIgnoreCase(";"))
				sig_elemento();
		}while(lex.equalsIgnoreCase("caso"));
		if(lex.equalsIgnoreCase("otro")){
			sig_elemento();
			if( !lex.equalsIgnoreCase("caso") )
				error("        En seleccion  -  Se esperaba caso y llegó ");
			sig_elemento();
			if( !lex.equals(":") )
				error("        En seleccion  -  Se esperaba : y llegó ");
			sig_elemento();
			estatuto();
		}
		if( !lex.equalsIgnoreCase("fin") )
			error("        En seleccion  -  Se esperaba fin y llegó ");
		sig_elemento();
	}
	
	boolean error_igualacion=false;
	
	public void asigna() {
		error_igualacion=false;
		if( lex.equals("[") ){
			if( dimens() ){
				errorExpr=true;
				if( encontrarIden(aux_estatuto) ){
					if( dimens_encontrado==0 ){
						errorSem("        La variable "+aux_estatuto+" no es de tipo arreglo");
						error_igualacion=true;
					}
					else
					if( contador_dimen<dimens_encontrado ){
						errorSem("        Faltan dimensiones para "+aux_estatuto);
						error_igualacion=true;
					}
					else
					if( contador_dimen>dimens_encontrado ){
						errorSem("        Exceso de dimensiones para "+aux_estatuto);
						error_igualacion=true;
					}
					else{
						int a=0,b=0,c=0,d=0;
						boolean dimen_correcta=true;
						if(dimens_encontrado==2){
							if( idx_esnumero[0] ){
								a=Integer.parseInt(dimen1_encontrado);
								b=Integer.parseInt(dimen_actual[0]);
								if( a<b ){
									errorSem("        Indice de dimen1 fuera de rango: "+b);
									errorExpr=true;
									dimen_correcta=false;
								}
							}
							if( idx_esnumero[1] ){
								c=Integer.parseInt(dimen2_encontrado);
								d=Integer.parseInt(dimen_actual[1]);
								if( c<d ){
									errorSem("        Indice de dimen2 fuera de rango: "+d);
									errorExpr=true;
									dimen_correcta=false;
								}
							}
							if( dimen_correcta ){
								pila.push( tipo_encontrado );
								errorExpr=false;
							}
						}
						if(dimens_encontrado==1){
							if( idx_esnumero[0] ){
								a=Integer.parseInt(dimen1_encontrado);
								b=Integer.parseInt(dimen_actual[0]);
								if( a<b ){
									errorSem("        Indice fuera de rango: "+b);
									errorExpr=true;
								}
								else{
									pila.push( tipo_encontrado );
									errorExpr=false;
								}
							}
							else{
								pila.push( tipo_encontrado );
								errorExpr=false;
							}
						}
					}
				}
				else {
					errorSem("        Variable "+aux_estatuto+" no ha sido declarada");
					error_igualacion=true;
				}	
			}
			else
				error_igualacion=true;
			
			if( !lex.equals("=") ){
				error("        En asigna  -  Se esperaba = y llegó ");
				error_igualacion=true;
			}
			sig_elemento();
			expr(); 
		}		
		else {
			if( encontrarIden(aux_estatuto) ){
				if( !clase_encontrado.equalsIgnoreCase("C") ){
					if( dimens_encontrado!=0 ){
						errorSem("        Variable "+aux_estatuto+" debe ser dimensionada: "+"("+dimens_encontrado+")dimensiones ");
						error_igualacion=true;
					}
					else
						pila.push( tipo_encontrado );
				}
				else{
					errorSem("        Variable "+aux_estatuto+" es constante y no puede ser reasignada");
					error_igualacion=true;
				}
			}
			else {
				errorSem("        Variable "+aux_estatuto+" no ha sido declarada");
				error_igualacion=true;
			}
			if( !lex.equals("=") ){
				error_igualacion=true;
				error("        En asigna  -  Se esperaba = y llegó ");
			}
			sig_elemento();
			expr();
			encontrarIden(aux_estatuto);
			gen_cod( "STO", "0", idenpl0 );
		}		
		String izq="", der="", operacion="";	//meter a pila los 2 lados de la igualacion
		if( !error_igualacion ){
			der=pila.pop();	izq=pila.pop();  operacion="=";
		}
		String aux=izq+operacion+der;
		int idx=asigCorrecta(aux);
		if( idx>=0 ){
			inic_correcto( aux_estatuto );
			pila.push(tipoR[idx]);
		}
		else {
			pila.push("I");
			errorSem("        Conflicto al realizar la asignacion: "+aux);
		}
	}
	
	String aux_estatuto="";
	
	public void estatuto() {
		do{
			if(lex.equals(";"))
				sig_elemento();
			else{
				if( lex.equalsIgnoreCase("entero") || lex.equalsIgnoreCase("decimal") || lex.equalsIgnoreCase("logico") || lex.equalsIgnoreCase("cadena") ){
					tipo=lex.charAt(0)+"";
					sig_elemento();
					if( !token.equals("Identi") )
						error("        En decvar  -  Se esperaba identificador y llegó ");
					else {
						nombre=lex+"$"+ambito;
					}
					sig_elemento();
					var_estatuto=true;
					vars();
				}
				if( token.equals("Identi") ) {
					aux_estatuto=lex;
					sig_elemento();
						if( lex.equals("(") ){
							aux_termino=aux_estatuto;
							if( lfunc() ){
								errorExpr=true;
								if( encontrarIden(func_llamada) ){
									pila.push( tipo_encontrado );
									errorExpr=false;
								}
								else {
									errorExpr=true;
									errorSem("        Funcion "+func_llamada+" no ha sido declarada");
								}	
							}
						}
						else
							asigna();
					if(!lex.equals(";"))
						error("        En estatuto  -  Se esperaba ; y llegó ");
					sig_elemento();
				}
				if( lex.equalsIgnoreCase("lee") )  {
					lee();
					if(!lex.equals(";"))
						error("        En estatuto  -  Se esperaba ; y llegó ");
					sig_elemento();
				}
				if( lex.equalsIgnoreCase("imprimenl") )  {
					imprime( true );
					if(!lex.equals(";"))
						error("        En estatuto  -  Se esperaba ; y llegó ");
					sig_elemento();
				}
				if( lex.equalsIgnoreCase("imprime") )  {
					imprime( false );
					if(!lex.equals(";"))
						error("        En estatuto  -  Se esperaba ; y llegó ");
					sig_elemento();
				}
				if( lex.equalsIgnoreCase("mientras") )	{
					mientras();
					if(!lex.equals(";"))
						error("        En estatuto  -  Se esperaba ; y llegó ");
					sig_elemento();
				}
				if( lex.equalsIgnoreCase("si") ) {
					si();
				}
				if( lex.equalsIgnoreCase("para") )	{
					para();
				}
				if( lex.equalsIgnoreCase("regresa") ) {
					regresa();
				}
				if( lex.equalsIgnoreCase("seleccion") )	{
					seleccion();
					if(!lex.equals(";"))
						error("        En estatuto  -  Se esperaba ; y llegó ");
					sig_elemento();
				}
			}
		}while( lex.equalsIgnoreCase("lee") || lex.equalsIgnoreCase("si") || lex.equalsIgnoreCase("para") || lex.equalsIgnoreCase("imprime")
				|| lex.equalsIgnoreCase("imprimenl") || token.equals("Identi") || lex.equalsIgnoreCase("regresa") || lex.equalsIgnoreCase("mientras")
				|| lex.equalsIgnoreCase("entero") || lex.equalsIgnoreCase("decimal") || lex.equalsIgnoreCase("logico") || lex.equalsIgnoreCase("cadena")
				|| lex.equalsIgnoreCase("seleccion") );
	}
	
	
	public void block() {
		if( !lex.equalsIgnoreCase("inicio") )
			error("        En block  -  Se esperaba inicio y llegó ");
		sig_elemento();
		if( lex.equalsIgnoreCase("fin") )
			sig_elemento();
		else{
			estatuto();
			if( !lex.equalsIgnoreCase("fin") )
 				error("        En block  -  Se esperaba fin y llegó ");
			sig_elemento();
		}
	}
	
	
	public boolean params() {
		boolean correcto=true;
		if(!lex.equalsIgnoreCase("entero") && !lex.equalsIgnoreCase("decimal") && !lex.equalsIgnoreCase("logico") && !lex.equalsIgnoreCase("cadena")) {
			error("        En params  -  Se esperaba una cte y llegó ");	correcto=false;
		}
		else	{	para_tipo[num_para]=lex.charAt(0)+"";	ambito=ambito+"$"+para_tipo[num_para];	}
		sig_elemento();
		if(!token.equals("Identi")) {
			error("        En params  -  Se esperaba un iden y llegó ");	correcto=false;
		}
		else	{	para_nombre[num_para]=lex;	num_para++;	}
		sig_elemento();
		
		while(lex.equals(",")){
			sig_elemento();
			if(!lex.equalsIgnoreCase("entero") && !lex.equalsIgnoreCase("decimal") && !lex.equalsIgnoreCase("logico") && !lex.equalsIgnoreCase("cadena")) {
				error("        En params  -  Se esperaba una cte y llegó ");	correcto=false;
			}
			else	{	para_tipo[num_para]=lex.charAt(0)+"";	ambito=ambito+"$"+para_tipo[num_para];	}
			sig_elemento();
			if(!token.equals("Identi")) {
				error("        En params  -  Se esperaba un iden y llegó ");	correcto=false;
			}
			else	{	para_nombre[num_para]=lex;	num_para++;	}
			sig_elemento();
		}
		return correcto;
	}
	
	String para_tipo[]=new String[20];
	String para_nombre[]=new String[20];
	int num_para=0;
	boolean error_prgm=false;
	boolean var_estatuto=false;
	boolean fin_prgm=false;
	int princ=0;
	
	public void principal() {
        dimen[0]="0";    dimen[1]="0";   clase="F";    num_dimen=0;	   inicializada=false;
        tipo_funcion="N";
		boolean correcto=true;
		ambito=nombre;
		if(uno)	
			tab[indice]=new Variable( "_P", "I", "I", linea_pl0+1+"", "0", 0, false );
		else
			tab[indice]=new Variable( "_P", "I", "I", linea_pl0+"", "0", 0, false );
		indice++;
		if(error_prgm)	
			correcto=false;
		if( !tipo.equalsIgnoreCase("N") )
			errorSem("        Funcion Principal debe ser tipo 'Notipo'");
		sig_elemento();
		if( !lex.equals(")") ) 
			error("        En Principal  -  Se esperaba ) y llegó ");
		nombre=nombre+"$";
		if(correcto)  {
			//agregarvar();
			princ++;
		}
		sig_elemento();
		block();
		if( !lex.equals(";") )
			error("        En Principal  -  Se esperaba cerrar con ; y llegó ");
		sig_elemento();
		borrarSem();
		fin_prgm=true;
	}
	
	String tipo_funcion="";
	String nom_func="";
	boolean hay_regresa=false;
	boolean uno=false;
	
	public void funcs() {
		dimen[0]="0";    dimen[1]="0";   clase="F";    num_dimen=0;	  inicializada=false;
		tipo_funcion=tipo;
		nom_func=nombre;
		boolean correcto=true;
		ambito=nombre;
		uno=true;
		if(error_prgm)	correcto=false;
		sig_elemento();
		if( lex.equals(")") || lex.equalsIgnoreCase("entero") || lex.equalsIgnoreCase("decimal") || lex.equalsIgnoreCase("logico") || lex.equalsIgnoreCase("cadena")){
			if( lex.equals(")") ) {
				nombre=nombre+"$";
				if(correcto)  agregarvar();
				sig_elemento();
				block();
			}
			else{
				if( params() && correcto) {
					nombre=ambito;
					agregarvar();	//declaracion de funcion
					for(int j=0; j<indice; j++){
						if( tab[j].getNombre().equalsIgnoreCase(nombre) ){
							tab[j].setDimen1( linea_pl0+"" );
						}
					}
					for(int i=0; i<num_para; i++) {
						nombre=para_nombre[i]+"$"+ambito;
						tipo=para_tipo[i];
						clase="P";
						dimen[0]="0";    
						dimen[1]="0";
						inicializada=true;
						gen_cod( "STO", "0", nombre );
						agregarvar();	//delaracion de parametros
					}
				}
				if( !lex.equals(")") )
					error("        En funcs  -  Se esperaba cerrar parentesis y llegó ");
				sig_elemento();
				block();
			}
		}
		else{
			error("        En funcs  -  Se esperaba cerrar parentesis y llegó ");
			sig_elemento();
		}
		if( !tipo_funcion.equalsIgnoreCase("N") && !hay_regresa){
			errorSem("        No se encontro ningun regresa para funcion "+nom_func);
		}
		if( !lex.equals(";") )
			error("        En funcs  -  Se esperaba cerrar funcion con ; y llegó ");
		sig_elemento();
		borrarSem();
		tipo_funcion="";
		hay_regresa=false;
	}
	
	
	public void ctes() {
    	String auxTipo="";
    	boolean correcto=true;
		sig_elemento();
		if( !lex.equalsIgnoreCase("entero") && !lex.equalsIgnoreCase("decimal") && !lex.equalsIgnoreCase("logico") && !lex.equalsIgnoreCase("cadena") ) { 
				error("        En ctes  -  Se esperaba un Tipo valido y llego ");
		}  else		tipo=lex.charAt(0)+"";
		auxTipo=tipo;
		do {
			dimen[0]="0";    dimen[1]="0";   clase="C";    inicializada=true;	 num_dimen=0;
			if( lex.equals(",") ) tipo=auxTipo;
			sig_elemento();
			if( !token.equals("Identi") )  {
				error("        En ctes  -  Se esperaba un Identificador y llegó ");		 correcto=false;
			}  else		nombre=lex;
			sig_elemento();
			if( !lex.equals("=") ) {
				error("        En ctes  -  Se esperaba un operador de asignacion y llegó ");		correcto=false;
			}
			sig_elemento();
			if( !token.equals("CteLog") && !token.equals("CteCad") && !token.equals("CteEnt") && !token.equals("CteDec") ) {
					error("        En ctes  -  Se esperaba una Constante y llegó ");		 correcto=false;
			}
			else{
				if( asigCorrecta(auxTipo+"="+token.charAt(3))==-1 )
					errorSem("        Conflicto en asignacion para "+nombre+", imposible asignar "+auxTipo+" = "+token.charAt(3));
			}
			gen_cod( "LIT", lex, "0" );
			gen_cod( "STO", "0", nombre );
			sig_elemento();
			if( correcto )  agregarvar();
			else	borrarSem();
			correcto=true;
		} while( lex.equals(",") );
		if(!lex.equals(";"))
			error("        En ctes  -  Se esperaba un ; y llegó ");	
		sig_elemento();
	}
    
	int contador_dimen=0;
	String dimen_actual[]={"",""};
	boolean idx_esnumero[]=new boolean[10];
	
    public boolean dimens() {
    	boolean correcto=true;
    	contador_dimen=0;
    	dimen_actual[0]="";
    	dimen_actual[1]="";
    	idx_esnumero[0]=true;
    	idx_esnumero[1]=true;
    	do{
    		sig_elemento();
	    	if( !token.equals("Identi") && !token.equals("CteLog") && !token.equals("CteCad") && !token.equals("CteEnt") && !token.equals("CteDec") ) {
	    		error("        En dimens  -  Se esperaba un iden o cte y llegó ");	    correcto=false;   }
	    	else {	
	    		if( !token.equals("CteEnt") && !token.equals("Identi") ){
	    			error("        Indice para dimensiones debe ser Entero o Identificador ");
	    			correcto=false;
	    		}
	    		else{
	    			if( !token.equals("CteEnt") ){
		    			if( !encontrarIden(lex) ){
		    				errorSem("        Variable "+lex+" no ha sido declarada");
		    				correcto=false;
		    			}
		    			else
		    				idx_esnumero[contador_dimen]=false;
	    			}
	    			else{
	    				dimen_actual[contador_dimen]=lex;
	    			}
	    			if(contador_dimen<2)
	    				dimen[contador_dimen]=lex;
	    		}
	    	}
	    	sig_elemento();
	    	if( !lex.equals("]") ) {
	    		error("        En dimens  -  Se esperaba una ] y llegó ");       correcto=false;    }
	    	sig_elemento();
	    	contador_dimen++;
    	}
	    while( lex.equals("[") );
    	if( contador_dimen>2 ) {
    		error("        Exceso de dimensiones, maximo 2 ");		correcto=false;		}
    	else
    		num_dimen=contador_dimen;
    	return correcto;
    }
    
    
	public void vars() {
		String auxTipo="";
		dimen[0]="0";    dimen[1]="0";   num_dimen=0;	inicializada=false;
		if( var_estatuto )	clase="L";
		else	clase="V";
		boolean correcto=true;
		auxTipo=tipo;
		if(error_prgm)	correcto=false;
		if(lex.equals("=")){
			sig_elemento();
			if( !token.equals("CteLog") && !token.equals("CteCad") && !token.equals("CteEnt") && !token.equals("CteDec") ) {
				error("        En vars  -  Se esperaba una cte y llegó ");		correcto=false;		}
			else{
				if( asigCorrecta(auxTipo+"="+token.charAt(3))==-1 )
					errorSem("        Conflicto en asignacion para "+nombre+", imposible asignar "+auxTipo+" = "+token.charAt(3));
				else
					inicializada=true;
			}
			gen_cod( "LIT", lex, "0" );
			gen_cod( "STO", "0", nombre );
			sig_elemento();
		}
		if(lex.equals("[")){
			if( !dimens() )   correcto=false;
			if( lex.equals("=") ){
				sig_elemento();
				if( !token.equals("CteLog") && !token.equals("CteCad") && !token.equals("CteEnt") && !token.equals("CteDec") ) {
					error("        En vars  -  Se esperaba una cte y llegó ");		correcto=false;		}
				else{
					if( asigCorrecta(auxTipo+"="+token.charAt(3))==-1 )
						errorSem("        Conflicto en asignacion para "+nombre+", imposible asignar "+auxTipo+" = "+token.charAt(3));
					else
						inicializada=true;
				}
				gen_cod( "LIT", lex, "0" );
				gen_cod( "STO", "0", nombre );
				sig_elemento();
			}
		}
		if( correcto )  agregarvar();
		else	borrarSem();
		correcto=true;
		if(lex.equals(","))
			do{
				dimen[0]="0";    dimen[1]="0";  clase="V";		num_dimen=0;	inicializada=false;
				tipo=auxTipo;
				sig_elemento();
				if( !token.equals("Identi") ) {
					error("        En vars  -  Se esperaba un iden y llegó ");		correcto=false;		}
				else	nombre=lex;
				sig_elemento();
				if( lex.equals("=") || lex.equals("[")){
					if( lex.equals("[") ){
						if( !dimens() )   correcto=false;
					}
					if( lex.equals("=") ){
						sig_elemento();
						if( !token.equals("CteLog") && !token.equals("CteCad") && !token.equals("CteEnt") && !token.equals("CteDec") ) {
							error("        En vars  -  Se esperaba una cte y llegó ");		correcto=false;		}
						else{
							if( asigCorrecta(auxTipo+"="+token.charAt(3))==-1 )
								errorSem("        Conflicto en asignacion para "+nombre+", imposible asignar "+auxTipo+" = "+token.charAt(3));
							else
								inicializada=true;
						}
						gen_cod( "LIT", lex, "0" );
						gen_cod( "STO", "0", nombre );
						sig_elemento();
					}
				}
				if( correcto )  agregarvar();
				else	borrarSem();
				correcto=true;
			}while(lex.equals(","));
		
		if(!lex.equals(";"))
			error("        En vars  -  Se esperaba cerrar bloque con ; y llegó ");
		sig_elemento();
	}
	
	
	public void prgm() {
		sig_elemento();
		do {
			if( fin_prgm )	   errores=errores+"linea "+renglon+"              Todo codigo despues de la funcion Principal no sera detectado "+"\n";
		    if( lex.equalsIgnoreCase("constante") )     ctes(); 
		    else {
		    	if( !lex.equalsIgnoreCase("entero") && !lex.equalsIgnoreCase("decimal") && !lex.equalsIgnoreCase("logico") &&
					!lex.equalsIgnoreCase("notipo") && !lex.equalsIgnoreCase("cadena")) {
		    			error("        En prgm -  Se espera un tipo y llego ");		error_prgm=true;
		    	}  else		tipo=lex.charAt(0)+"";
		    	sig_elemento();
		    	if( !token.equals( "Identi") ) {
		    		error("        En prgm  -  Se esperaba Identificador y llego ");	error_prgm=true;
		    	}  else		nombre=lex;
		    	sig_elemento();
		    	if( lex.equalsIgnoreCase("(") ) {
		    		if( nombre.equalsIgnoreCase("principal") )
		    			principal();
		    		else
		    			funcs();
		    	}
		    	else 	
		    	    vars();
		    }
		    if( !lex.equalsIgnoreCase("entero") && !lex.equalsIgnoreCase("decimal") && !lex.equalsIgnoreCase("logico") &&
					!lex.equalsIgnoreCase("notipo") && !lex.equalsIgnoreCase("cadena") && !lex.equalsIgnoreCase("constante") && !lex.equals("") )
		    	error("        En prgm  -  Se esperaba un tipo o constante y llego ");
		} while( lex.equalsIgnoreCase("constante") || lex.equalsIgnoreCase("entero") || lex.equalsIgnoreCase("decimal") ||
				 lex.equalsIgnoreCase("logico") || lex.equalsIgnoreCase("notipo") || lex.equalsIgnoreCase("cadena") );
		
		if( princ==0 )	errores="linea "+renglon+"              Funcion principal no encontrada "+"\n"+errores;
		if( princ>1 )	errores="linea "+renglon+"              Funcion principal encontrada en mas de una ocasion "+"\n"+errores;
	}
	
	String func_llamada="";
	boolean error_param=false;
	
	public boolean lfunc()	{
		boolean correcto=true;
		func_llamada="";
		sig_elemento();
		func_llamada=aux_termino;
		gen_cod( "OPR",  "0", "1" );
		gen_cod( "LOD",  generarEtiq(), "0" );
		if( lex.equals(")") ){
			func_llamada=func_llamada+"$";
			sig_elemento();
		}
		else{
			expr();	
            if( !error_param )  func_llamada=func_llamada+"$"+pila.pop();
			while( lex.equals(",") ){
				sig_elemento();
				expr();
				if( !error_param )	func_llamada=func_llamada+"$"+pila.pop();
			}
			if(!lex.equals(")")){
				error("        En lfunc  -  Se esperaba ) y llego  ");
				correcto=false;
			}
			sig_elemento();
		}
		encontrarIden(func_llamada);
		gen_cod( "CAL", func_llamada, dimen1_encontrado );
		resolverEtiq();
		return correcto;
	}
		
	boolean errorExpr=false;
	String aux_termino="";
	
	public void termino() {
		error_param=false;
		if( lex.equals("(") ){
			sig_elemento();
			expr();
			if( !lex.equals(")") )
				error("        En expresion  -  Se esperaba ) y llego  ");
			sig_elemento();
		}
		else
		if( token.equals("CteLog") || token.equals("CteCad") || token.equals("CteEnt") || token.equals("CteDec") ){
			pila.push( token.charAt(3)+"" );
			if( token.equals("CteCad") ){
				boolean coma=false;
				for(int i=0; i<lex.length(); i++){
					if( (lex.charAt(i)+"").equalsIgnoreCase(",") ){
						coma=true;
					}
				}
				if( coma )
					errorSem("        Comas en "+lex+" podrian afectar el funcionamiento del programa ");
			}
			gen_cod( "LIT", lex, "0" );
			sig_elemento();
		}
		else
		if( token.equals("Identi") ){
			boolean entro=false;
			aux_termino=lex;
			sig_elemento();
			if( lex.equals("[") ){
				entro=true;
				if( dimens() ){
					errorExpr=true;
					if( encontrarIden(aux_termino) ){
						if( inic_encontrado ){
							if( dimens_encontrado==0 ){
								errorSem("        La variable "+aux_termino+" no es de tipo arreglo");
								errorExpr=true;
							}
							else
							if( contador_dimen<dimens_encontrado ){
								errorSem("        Faltan dimensiones para "+aux_termino);
								errorExpr=true;
							}
							else
							if( contador_dimen>dimens_encontrado ){
								errorSem("        Exceso de dimensiones para "+aux_termino);
								errorExpr=true;
							}
							else{
								int a=0,b=0,c=0,d=0;
								boolean dimen_correcta=true;
								if(dimens_encontrado==2){
									if( idx_esnumero[0] ){
										a=Integer.parseInt(dimen1_encontrado);
										b=Integer.parseInt(dimen_actual[0]);
										if( a<b ){
											errorSem("        Indice de dimen1 fuera de rango: "+b);
											errorExpr=true;
											dimen_correcta=false;
										}
									}
									if( idx_esnumero[1] ){
										c=Integer.parseInt(dimen2_encontrado);
										d=Integer.parseInt(dimen_actual[1]);
										if( c<d ){
											errorSem("        Indice de dimen2 fuera de rango: "+d);
											errorExpr=true;
											dimen_correcta=false;
										}
									}
									if( dimen_correcta ){
										pila.push( tipo_encontrado );
										errorExpr=false;
									}
								}
								if(dimens_encontrado==1){
									if( idx_esnumero[0] ){
										a=Integer.parseInt(dimen1_encontrado);
										b=Integer.parseInt(dimen_actual[0]);
										if( a<b ){
											errorSem("        Indice fuera de rango: "+b);
											errorExpr=true;
										}
										else{
											pila.push( tipo_encontrado );
											errorExpr=false;
										}
									}
									else{
										pila.push( tipo_encontrado );
										errorExpr=false;
									}
								}
							}		
						}
						else {
							errorExpr=true;
							errorSem("        Variable "+aux_termino+" no ha sido inicializada");
						}
					}
					else {
						errorExpr=true;
						errorSem("        Variable "+aux_termino+" no ha sido declarada");
					}	
				}
				else
					errorExpr=true;
			}
			if( lex.equals("(") ){
				entro=true;
				if( lfunc() ){
					errorExpr=true;
					if( encontrarIden(func_llamada) ){
						pila.push( tipo_encontrado );
						errorExpr=false;
					}
					else {
						errorExpr=true;
						errorSem("        Funcion "+func_llamada+" no ha sido declarada");
					}	
				}
				else
					errorExpr=true;
				gen_cod( "LOD", idenpl0, "0" );
			}
			if( !entro ){
				if( encontrarIden(aux_termino) ){
					if( dimens_encontrado>0 ){
						error_param=true;
						errorExpr=true;
						errorSem("        Variable "+aux_termino+" es dimensionada");
					}
					else{
						if( inic_encontrado )
							pila.push( tipo_encontrado );
						else {
							error_param=true;
							errorExpr=true;
							errorSem("        Variable "+aux_termino+" no ha sido inicializada");
						}
					}
					
				}
				else {
					error_param=true;
					errorExpr=true;
					errorSem("        Variable "+aux_termino+" no ha sido declarada");
				}	
				gen_cod( "LOD", idenpl0, "0" );
			}
		}
		else {
			error("        En expresion  -  Se esperaba un termino y llego  ");
			errorExpr=true;
		}
		if( errorExpr )	error_igualacion=true;
		else	error_igualacion=false;
	}

	
	public void signo() {
		String aux="", operacion="";
		if( lex.equals("-") ) {
			aux=lex;
			operacion=lex;
			sig_elemento();
			
			termino();
			aux=operacion+pila.pop();
			int idx=asigCorrecta(aux);
			if( idx>=0 )
				pila.push(tipoR[idx]);
			else {
				pila.push("I");
				errorSem("        Conflicto al realizar negacion "+operacion+", "+aux);
			}
			aux="";   operacion="";
			gen_cod( "OPR", "0", "7" );
		}
		else
			termino();
	}

	public void expo() {
		String aux="", operacion="";
		boolean usarpila=false;
		do {
			signo();
			aux="";
			if(usarpila){
				String izq="", der="";
				if( !errorExpr ){
					izq=pila.pop();	der=pila.pop();
					aux=izq+operacion+der;
				}
				int idx=asigCorrecta(aux);
				if( idx>=0 )
					pila.push(tipoR[idx]);
				else {
					pila.push("I");
					errorSem("        Conflicto al realizar operacion "+operacion+", "+der+operacion+izq);
				}
				gen_cod( "OPR", "0", "8" );
				operacion="";
				usarpila=false;
			}
			if(aux.equals("^")){
				aux=lex;    operacion=lex;
				sig_elemento();	
				usarpila=true;	
			}	
		} while(aux.equals("^"));
	}

	public void multi() {
		String aux="", operacion="";
		boolean usarpila=false;
		do {
			expo();
			aux="";
			if(usarpila){
				String izq="", der="";
				if( !errorExpr ){
					izq=pila.pop();	der=pila.pop();
					aux=izq+operacion+der;
				}
				int idx=asigCorrecta(aux);
				if( idx>=0 )
					pila.push(tipoR[idx]);
				else {
					pila.push("I");
					errorSem("        Conflicto al realizar operacion "+operacion+", "+der+operacion+izq);
				}
				if( operacion.equals("*") )	gen_cod( "OPR", "0", "4" );
				if( operacion.equals("/") )	gen_cod( "OPR", "0", "5" );
				if( operacion.equals("%") )	gen_cod( "OPR", "0", "2" );
				operacion="";
				usarpila=false;
			}
			if(lex.equals("*") || lex.equals("/") || lex.equals("%")){
				aux=lex;    operacion=lex;
				sig_elemento();	
				usarpila=true;
			}	
		} while(aux.equals("*") || aux.equals("/") || aux.equals("%"));
	}

	public void suma() {
		String aux="", operacion="";
		boolean usarpila=false;
		do {
			multi();
			aux="";
			if(usarpila){
				String izq="", der="";
				if( !errorExpr ){
					der=pila.pop();	izq=pila.pop();
					aux=izq+operacion+der;
				}
				int idx=asigCorrecta(aux);
				if( idx>=0 )
					pila.push(tipoR[idx]);
				else {
					pila.push("I");
					errorSem("        Conflicto al realizar operacion "+operacion+", "+aux);
				}
				if( operacion.equals("+") )	gen_cod( "OPR", "0", "2" );
				if( operacion.equals("-") )	gen_cod( "OPR", "0", "3" );	
				operacion="";
				usarpila=false;
			}
			if(lex.equals("+") || lex.equals("-")){
				aux=lex;    operacion=lex;
				sig_elemento();	
				usarpila=true;
			}	
		} while(aux.equals("+") || aux.equals("-"));
	}

	public void expr() {
		errorExpr=false;
		//pila.removeAllElements();
		String aux="", operacion="";
		boolean usarpila=false;
		do {
			opy();
			aux="";
			if(usarpila){
				String izq="", der="";
				if( !errorExpr ){
					izq=pila.pop();	der=pila.pop();
					aux=izq+operacion+der;
				}
				int idx=asigCorrecta(aux);
				if( idx>=0 )
					pila.push(tipoR[idx]);
				else {
					pila.push("I");
					errorSem("        Conflicto al realizar operacion "+operacion+", "+der+operacion+izq);
				}
				gen_cod( "OPR", "0", "16" );
				operacion="";
				usarpila=false;
			}
			if(lex.equals("o")){
				aux=lex;    operacion=lex;
				sig_elemento();	
				usarpila=true;
			}	
		} while(aux.equals("o"));
	}

	public void opy() {
		String aux="", operacion="";
		boolean usarpila=false;
		do {
			negacion();
			aux="";
			if(usarpila){
				String izq="", der="";
				if( !errorExpr ){
					izq=pila.pop();	der=pila.pop();
					aux=izq+operacion+der;
				}
				int idx=asigCorrecta(aux);
				if( idx>=0 )
					pila.push(tipoR[idx]);
				else {
					pila.push("I");
					errorSem("        Conflicto al realizar operacion "+operacion+", "+der+operacion+izq);
				}
				gen_cod( "OPR", "0", "17" );
				operacion="";
				usarpila=false;
			}
			if(lex.equals("y")){
				aux=lex;    operacion=lex;
				sig_elemento();	
				usarpila=true;
			}	
		} while(aux.equals("y"));
	}

	public void negacion() {
		String aux="", operacion="";
		if( lex.equals("no") ) {
			aux=lex;
			operacion=lex;
			sig_elemento();
			
			oprel();
			aux=operacion+pila.pop();
			int idx=asigCorrecta(aux);
			if( idx>=0 )
				pila.push(tipoR[idx]);
			else {
				pila.push("I");
				errorSem("        Conflicto al realizar negacion "+operacion+", "+aux);
			}
			aux="";   operacion="";
			gen_cod( "OPR", "0", "15" );
		}
		else
			oprel();
	}

	public void oprel() {
		String aux="", operacion="";
		boolean usarpila=false;
		do {
			suma();
			aux="";
			if(usarpila){
				String izq="", der="";
				if( !errorExpr ){
					izq=pila.pop();	der=pila.pop();
					aux=izq+operacion+der;
				}
				int idx=asigCorrecta(aux);
				if( idx>=0 )
					pila.push(tipoR[idx]);
				else {
					pila.push("I");
					errorSem("        Conflicto al realizar operacion "+operacion+", "+der+operacion+izq);
				}
				if( operacion.equals("!=") )	gen_cod( "OPR", "0", "13" );
				if( operacion.equals("==") )	gen_cod( "OPR", "0", "14" );
				if( operacion.equals("<") )		gen_cod( "OPR", "0", "9" );
				if( operacion.equals(">") )		gen_cod( "OPR", "0", "10" );
				if( operacion.equals("<=") )	gen_cod( "OPR", "0", "11" );
				if( operacion.equals("==") )	gen_cod( "OPR", "0", "12" );
				operacion="";
				usarpila=false;
			}
			if(lex.equals("!=") || lex.equals("==") || lex.equals("<") || lex.equals(">") || lex.equals("<=") || lex.equals(">=")){
				aux=lex;    operacion=lex;
				sig_elemento();	
				usarpila=true;
			}	
		} while(aux.equals("!=") || aux.equals("==") || aux.equals("<") || aux.equals(">") || aux.equals("<=") || aux.equals(">="));
	}
    ///////////////////////////////////////      SINTAXIS     /////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////      GEN CODIGO     ///////////////////////////////////////////////////////
	public static String codigo_pl0 ="";
	int linea_pl0=1;
	int idx_etq=1;
	
	public void cod_tabla(){
		String aux_pl0="";
		for(int i=0; i<indice; i++){
			String aux=tab[i].getTipo();
			if( tab[i].getTipo().equalsIgnoreCase("N") )
				aux="V";
			if( tab[i].getTipo().equalsIgnoreCase("D") )
				aux="R";
			aux=tab[i].getTipo().toUpperCase();
			aux_pl0=aux_pl0+tab[i].getNombre()+","+tab[i].getClase()+","+aux+","+tab[i].getDimen1()+","+tab[i].getDimen2()+","+"#"+","+"\n";
		}
		codigo_pl0=aux_pl0+"@"+"\n"+codigo_pl0+linea_pl0+" OPR "+"0"+",0";
	}
	
	public void gen_cod( String mnemo, String dir1, String dir2 ){
		codigo_pl0=codigo_pl0+linea_pl0+" "+mnemo+" "+dir1+","+dir2+"\n";
		linea_pl0++;
	}
	
	String etiq;
	
	public String generarEtiq() {
		etiq="_E"+idx_etq;	
		agregarEtiq( "E"+idx_etq );
		return etiq;
	}
	
	public void resolverEtiq() {
		for(int i=0; i<indice; i++){
			if( tab[i].getNombre().equalsIgnoreCase(etiq) ){
				tab[i].setDimen1( linea_pl0+"" );
			}
		}
		idx_etq++;
	}
	
	public void agregarEtiq( String nombre ) {
		tab[indice]=new Variable( "_"+nombre, "I", "I", linea_pl0+"", "0", 0, false );
		indice++;
    }
	///////////////////////////////////////      GEN CODIGO     ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void scan() {
		borrarVars();
		princ=0;
		fin_prgm=false;
		prgm();
		cod_tabla();
		
		//System.out.println(pila);
		System.out.println(codigo_pl0);
		/*for(int i=0; i<indice; i++){
			System.out.println(tab[i].getNombre()+" , "+tab[i].getClase()+" , "+tab[i].getTipo()+" , "+tab[i].getDimen1()+" , "+tab[i].getDimen2()+" , "+tab[i].getNum_dimen()+" , "+tab[i].isInicializada());
		}*/
		
		/*while (idx < entrada.length() ) {
		   sig_elemento();
		   System.out.println(token+"\t"+lex);
		}*/
	}
	
}
