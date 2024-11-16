/* Generated By:JavaCC: Do not edit this line. Analizador.java */
public class Analizador implements AnalizadorConstants {

    public static void main(String[] args){
        Analizador analizador = new Analizador(System.in);

        try{
            System.out.println("Iniciando an\u00e1lisis...\n");
            analizador.inicializarArbol();

            System.out.println("An\u00e1lisis exitoso");
        }catch(ParseException e){
            System.out.println("Error de sintaxis: " + e.getMessage());
        }
    }

    class Nodo {
        Token token;
        Nodo izquierdo, derecho, padre;

        public Nodo(Token token){
            this.token = token;
            this.izquierdo = null;
            this.derecho = null;
            this.padre = null;
        }
    }

    class Arbol{
        Nodo raiz;

        public Arbol(){
            raiz = null;
        }

        public Arbol(Nodo raiz){
            this.raiz = raiz;
        }

        void enOrden(){
            System.out.print("\nRecorrido in-orden: ");
            enOrdenRec(raiz);
            System.out.println("\n");
        }

        void enOrdenRec(Nodo raiz){
            if (raiz != null) {
                enOrdenRec(raiz.izquierdo);
                System.out.print(raiz.token.image + " ");
                enOrdenRec(raiz.derecho);
            }
        }

        public void insertar(Arbol arbolIzq, Arbol arbolDer, Nodo operador){
            operador.izquierdo = arbolIzq.raiz;
            operador.derecho = arbolDer.raiz;
            this.raiz = operador;
            //Agregado para padre
            operador.izquierdo.padre = operador;
            operador.derecho.padre = operador;
        }

        public Nodo getRaiz(){
            return raiz;
        }

        public void setRaiz(Nodo raiz){
            this.raiz = raiz;
        }
    }

    class ConversionFNC{
        Arbol arbol;
        //Nodo padre;

        public ConversionFNC(Arbol arbol){
            this.arbol = arbol;
        }

        Nodo obtenerSubArbol(Nodo raiz){
            if (raiz == null) {
                return null;
            }

            Nodo nuevoNodo = new Nodo(new Token(raiz.token.kind,raiz.token.image));
            nuevoNodo.izquierdo = obtenerSubArbol(raiz.izquierdo);
            nuevoNodo.derecho = obtenerSubArbol(raiz.derecho);

            return nuevoNodo;
        }

        void generarFNC(){
            boolean detectaCambios = false;
            //this.padre = this.arbol.raiz;
            System.out.println("Generar FNC");
            do{
                detectaCambios = aplicarConversion(this.arbol.raiz);
            }while(detectaCambios);

            System.out.println();
        }

        boolean aplicarConversion(Nodo raiz){
            boolean detectaCambios = false;
            //this.padre = padre;


            if (raiz != null) {
                System.out.println("PADRE: "+raiz.padre.token.image);

                aplicarConversion(raiz.izquierdo);

                detectaCambios = aplicarSustituciones(raiz);

                aplicarConversion(raiz.derecho);
            }
            return detectaCambios;
        }

        boolean aplicarSustituciones(Nodo raiz){
            boolean detectaCambios = false;

            if(raiz.token.kind == BICONDICIONAL){
                sustituyeBicondicional(raiz);
                detectaCambios = true;
            }else if(raiz.token.kind == CONDICIONAL){
                sustituyeCondicional(raiz);
                detectaCambios = true;
            }else if(raiz.token.kind == NEGACION){
                if(raiz.derecho.token.kind == NEGACION){
                    eliminarNegacion(raiz);
                    System.out.println("x: "+raiz.token.image);
                    //detectaCambios = true;
                }else{
                    //detectaCambios = false;
                }
            }else{
                detectaCambios = false;
            }
            return detectaCambios;
        }

        void sustituyeBicondicional(Nodo raiz){
            Nodo nodoIzqA = obtenerSubArbol(raiz.izquierdo);
            Nodo nodoDerA = obtenerSubArbol(raiz.derecho);

            Nodo nodoIzqB = obtenerSubArbol(raiz.izquierdo);
            Nodo nodoDerB = obtenerSubArbol(raiz.derecho);

            raiz.token = new Token(CONJUNCION, "&");

            raiz.izquierdo.token = new Token(CONDICIONAL, ">");
            raiz.izquierdo.izquierdo = nodoIzqA;
            raiz.izquierdo.derecho = nodoDerA;

            raiz.derecho.token = new Token(CONDICIONAL, ">");
            raiz.derecho.izquierdo = nodoDerB;
            raiz.derecho.derecho = nodoIzqB;
        }

        void sustituyeCondicional(Nodo raiz){
            raiz.token = new Token(DISYUNCION, "|");

            Nodo nodoIzq = obtenerSubArbol(raiz.izquierdo);
            Nodo nodoDer = obtenerSubArbol(raiz.derecho);

            raiz.izquierdo.token = new Token(NEGACION, "\u00ac");
            raiz.izquierdo.derecho = nodoIzq;

            raiz.derecho = nodoDer;
        }

        void eliminarNegacion(Nodo raiz){
            System.out.println("padre: "+raiz.padre.token.image);
            System.out.println("Raiz1: "+raiz.token.image);
            System.out.println("Raiz2: "+raiz.derecho.token.image);
            System.out.println("Raiz3: "+raiz.derecho.derecho.token.image);
            raiz.padre.derecho = raiz.derecho.derecho;
            System.out.println("Raiz asig: "+raiz.padre.token.image);
        }

        //Busca el nodo VARIABLE sin importar cuantas negaciones tenga antes y retorna un nuevo nodo con la misma información
        Nodo obtenerVariable(Nodo nodo){
            Nodo nodoAux;
            if(nodo.token.kind == NEGACION){
                nodoAux = new Nodo(new Token(NEGACION, "\u00ac"));
                nodoAux.derecho = obtenerVariable(nodo.derecho);
                return nodoAux;
            }else{
                nodoAux = new Nodo(nodo.token);
                return nodoAux;
            }
        }

        //Comprueba si ambos hijos del nodo son variables para que se pueda aplicar la sustitución
        boolean aplicarSustitucion(Nodo nodo){
            if(nodo.izquierdo.token.kind == VARIABLE && nodo.derecho.token.kind == VARIABLE){
                return true;
            }else{
                return false;
            }
        }
    }

  final public void inicializarArbol() throws ParseException {
    Arbol arbol;
    ConversionFNC conversion;
    arbol = condicionales();
                            System.out.println("Fin"); arbol.enOrden(); conversion = new ConversionFNC(arbol); conversion.generarFNC(); arbol.enOrden();
  }

  final public Arbol condicionales() throws ParseException {
    Arbol arbolIzq, arbolDer;
    Token t;
    arbolIzq = operacionesLogicas();
    label_1:
    while (true) {
      if (jj_2_1(2)) {
        ;
      } else {
        break label_1;
      }
      if (jj_2_2(2)) {
        jj_consume_token(CONDICIONAL);
                          t = token;
        arbolDer = operacionesLogicas();
                                                                        arbolIzq.insertar(arbolIzq, arbolDer, new Nodo(t));
      } else if (jj_2_3(2)) {
        jj_consume_token(BICONDICIONAL);
                          t = token;
        arbolDer = operacionesLogicas();
                                                                        arbolIzq.insertar(arbolIzq, arbolDer, new Nodo(t));
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
         {if (true) return arbolIzq;}
    throw new Error("Missing return statement in function");
  }

  final public Arbol operacionesLogicas() throws ParseException {
    Arbol arbolIzq, arbolDer;
    Token t;
    arbolIzq = procesarNegacion();
    label_2:
    while (true) {
      if (jj_2_4(2)) {
        ;
      } else {
        break label_2;
      }
      if (jj_2_5(2)) {
        jj_consume_token(CONJUNCION);
                       t = token;
        arbolDer = procesarNegacion();
                                                                   arbolIzq.insertar(arbolIzq, arbolDer, new Nodo(t));
      } else if (jj_2_6(2)) {
        jj_consume_token(DISYUNCION);
                       t = token;
        arbolDer = procesarNegacion();
                                                                   arbolIzq.insertar(arbolIzq, arbolDer, new Nodo(t));
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
         {if (true) return arbolIzq;}
    throw new Error("Missing return statement in function");
  }

  final public Arbol procesarNegacion() throws ParseException {
    Arbol arbolIzq = new Arbol();
    Arbol arbolDer;
    if (jj_2_7(2)) {
      jj_consume_token(NEGACION);
        if(arbolIzq.raiz == null){
            arbolIzq.setRaiz(new Nodo(token));
        }else{
            Nodo actual = arbolIzq.raiz;
            while(actual.derecho != null){
                actual = actual.derecho;
            }
            actual.derecho = new Nodo(token);
        }
      arbolDer = procesarNegacion();
    } else if (jj_2_8(2)) {
      arbolDer = procesarVariable();
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
        if(arbolIzq.raiz == null){
            arbolIzq.setRaiz(arbolDer.raiz);
        }
        else{
            Nodo actual = arbolIzq.raiz;
            while(actual.derecho != null){
                actual = actual.derecho;
            }
            actual.derecho = arbolDer.raiz;
        }
        {if (true) return arbolIzq;}
    throw new Error("Missing return statement in function");
  }

  final public Arbol procesarVariable() throws ParseException {
    Arbol arbol;
    if (jj_2_9(2)) {
      jj_consume_token(VARIABLE);
                 arbol = new Arbol(new Nodo(token));
    } else if (jj_2_10(2)) {
      arbol = procesarAgrupaciones();
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
                                                                                          {if (true) return arbol;}
    throw new Error("Missing return statement in function");
  }

  final public Arbol procesarAgrupaciones() throws ParseException {
    Arbol arbol;
    if (jj_2_11(2)) {
      jj_consume_token(PARENTESIS_IZQ);
      arbol = condicionales();
      jj_consume_token(PARENTESIS_DER);
                                                                 {if (true) return arbol;}
    } else if (jj_2_12(2)) {
      jj_consume_token(CORCHETE_IZQ);
      arbol = condicionales();
      jj_consume_token(CORCHETE_DER);
                                                               {if (true) return arbol;}
    } else if (jj_2_13(2)) {
      jj_consume_token(LLAVE_IZQ);
      arbol = condicionales();
      jj_consume_token(LLAVE_DER);
                                                            {if (true) return arbol;}
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  private boolean jj_2_10(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_10(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  private boolean jj_2_11(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_11(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(10, xla); }
  }

  private boolean jj_2_12(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_12(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(11, xla); }
  }

  private boolean jj_2_13(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_13(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(12, xla); }
  }

  private boolean jj_3_7() {
    if (jj_scan_token(NEGACION)) return true;
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3R_4() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_7()) {
    jj_scanpos = xsp;
    if (jj_3_8()) return true;
    }
    return false;
  }

  private boolean jj_3_6() {
    if (jj_scan_token(DISYUNCION)) return true;
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3_5() {
    if (jj_scan_token(CONJUNCION)) return true;
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3_4() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_5()) {
    jj_scanpos = xsp;
    if (jj_3_6()) return true;
    }
    return false;
  }

  private boolean jj_3_13() {
    if (jj_scan_token(LLAVE_IZQ)) return true;
    if (jj_3R_7()) return true;
    return false;
  }

  private boolean jj_3_12() {
    if (jj_scan_token(CORCHETE_IZQ)) return true;
    if (jj_3R_7()) return true;
    return false;
  }

  private boolean jj_3_11() {
    if (jj_scan_token(PARENTESIS_IZQ)) return true;
    if (jj_3R_7()) return true;
    return false;
  }

  private boolean jj_3R_6() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_11()) {
    jj_scanpos = xsp;
    if (jj_3_12()) {
    jj_scanpos = xsp;
    if (jj_3_13()) return true;
    }
    }
    return false;
  }

  private boolean jj_3R_3() {
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3_10() {
    if (jj_3R_6()) return true;
    return false;
  }

  private boolean jj_3_3() {
    if (jj_scan_token(BICONDICIONAL)) return true;
    if (jj_3R_3()) return true;
    return false;
  }

  private boolean jj_3_2() {
    if (jj_scan_token(CONDICIONAL)) return true;
    if (jj_3R_3()) return true;
    return false;
  }

  private boolean jj_3_1() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_2()) {
    jj_scanpos = xsp;
    if (jj_3_3()) return true;
    }
    return false;
  }

  private boolean jj_3_9() {
    if (jj_scan_token(VARIABLE)) return true;
    return false;
  }

  private boolean jj_3R_5() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_9()) {
    jj_scanpos = xsp;
    if (jj_3_10()) return true;
    }
    return false;
  }

  private boolean jj_3R_7() {
    if (jj_3R_3()) return true;
    return false;
  }

  private boolean jj_3_8() {
    if (jj_3R_5()) return true;
    return false;
  }

  /** Generated Token Manager. */
  public AnalizadorTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  /** Whether we are looking ahead. */
  private boolean jj_lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[0];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[13];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Analizador(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Analizador(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new AnalizadorTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Analizador(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new AnalizadorTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Analizador(AnalizadorTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(AnalizadorTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = jj_lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List jj_expentries = new java.util.ArrayList();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Iterator it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.add(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[20];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 0; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 20; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 13; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
            case 8: jj_3_9(); break;
            case 9: jj_3_10(); break;
            case 10: jj_3_11(); break;
            case 11: jj_3_12(); break;
            case 12: jj_3_13(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
