import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DomMovies {

	private static String URL_GENERIC = "http://gssi.det.uvigo.es/users/agil/public_html/SINT/17-18/mml2001.xml";
	private static final String outputEncoding = "UTF-8";
	private static PrintWriter out;
    private static int indent = 0;
    private static final String basicIndent = " ";
    
    private static Set<String> documentosProcesados = new HashSet<String>();
    
    private static List<String> langs = new ArrayList<String>();
    private static List<Actor> acts = new ArrayList<Actor>();
    
    
    static {
    	out = new PrintWriter(System.out);
    }
    
    DomMovies(PrintWriter out) {
        this.out = out;
    }
	
    private static Document cargarDocumento(String url) {
    	
    	Document documento = null;
    	
    	if (!documentosProcesados.contains(url)) {
        	
//        	if (!url.startsWith("http")) {
//        		url = "http://..." + url;
//        	}
        	
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	
        	try {
    			
    			DocumentBuilder db = dbf.newDocumentBuilder();
    			OutputStreamWriter errorWriter = new OutputStreamWriter(System.err,
                        outputEncoding);
    			db.setErrorHandler(new MyErrorHandler (new PrintWriter(errorWriter, true)));
    			documento = db.parse(new URL(URL_GENERIC).openStream());
    			documentosProcesados.add(url);
    		} catch (ParserConfigurationException | SAXException | IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
    	}

    	return documento;
    }
    
	public static void main(String[] args) {
		
		Document doc = cargarDocumento(URL_GENERIC);
		
		echo(doc.getFirstChild());
		System.out.println("Resultados:");
		//buscarPorNodoYAtributo(doc, "Pais", "lang", langs);
		//getC2Langs();
		List<Document> docs = new ArrayList<>();
		docs.add(doc);
		try {
			getMovies(docs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static List<Movies> getMovies(List<Document> docs) throws Exception {
		List<Movies> moviesList = new ArrayList<Movies>();
		
		for(Document doc: docs) {
			NodeList nodesMovies = doc.getElementsByTagName("Movies");
			for(int i = 0; i < nodesMovies.getLength(); i++) {
				Node nodeMovie = nodesMovies.item(i);
				Short anio = getAnioMovies(nodeMovie);
				List<Pais> paises = getPaisesMovie(nodeMovie);
				Movies movies = new Movies();
				movies.setAnio(anio);
				movies.setPaises(paises);
				moviesList.add(movies);
			}
		
		}
		
		return moviesList;
	}
	
	private static Short getAnioMovies(Node nodeMovie) throws Exception {
		NodeList nodos = nodeMovie.getChildNodes();
		for(int i = 0; i < nodos.getLength(); i++) {
			if (nodos.item(i).getNodeName().equals("Anio")) {
				return Short.valueOf(nodos.item(i).getFirstChild().getNodeValue());
			}
		}
		throw new Exception("Error recuperando anio");
	}
	
	private static List<Pais> getPaisesMovie(Node nodeMovie) throws Exception {
		List<Pais> paises = new ArrayList<Pais>();
		NodeList nodos = nodeMovie.getChildNodes();
		for(int i = 0; i < nodos.getLength(); i++) {
			if (nodos.item(i).getNodeName().equals("Pais")) {
				NamedNodeMap atributos = nodos.item(i).getAttributes();
				String nombrePais = atributos.getNamedItem("pais").getNodeValue();
				String lang = atributos.getNamedItem("lang").getNodeValue();
				List<Pelicula> peliculas = getPeliculas(nodos.item(i));
				Pais pais = new Pais();
				pais.setPais(nombrePais);
				pais.setLang(lang);
				pais.setPeliculas(peliculas);
				paises.add(pais);
			}
		}
		return paises;
	}
	
	private static List<Pelicula> getPeliculas(Node nodePais) throws Exception {
		List<Pelicula> peliculas = new ArrayList<Pelicula>();
		NodeList nodos = nodePais.getChildNodes();
		for(int i = 0; i < nodos.getLength(); i++) {
			if (nodos.item(i).getNodeName().equals("Pelicula")) {
				NamedNodeMap atributos = nodos.item(i).getAttributes();
				String ip = atributos.getNamedItem("ip").getNodeValue();
				String langs = atributos.getNamedItem("langs").getNodeValue();
				String titulo = getTituloPelicula(nodos.item(i));
				List<String> generos = getGeneros(nodos.item(i));
				Short duracion = getDuracion(nodos.item(i));
				List<Reparto> reparto = getReparto(nodos.item(i));
				Pelicula pelicula = new Pelicula();
				pelicula.setIp(ip);
				pelicula.setLangs(langs);
				pelicula.setTitulo(titulo);
				pelicula.setGeneros(generos);
				pelicula.setDuracion(duracion);
				pelicula.setReparto(reparto);
				peliculas.add(pelicula);
			}
		}
		return peliculas;
	}
	
	private static String getTituloPelicula(Node nodePelicula) throws Exception {
		NodeList nodos = nodePelicula.getChildNodes();
		for(int i = 0; i < nodos.getLength(); i++) {
			if (nodos.item(i).getNodeName().equals("Titulo")) {
				return nodos.item(i).getFirstChild().getNodeValue();
			}
		}
		throw new Exception("Error recuperando titulo pelicula");
		
	}
	
	private static List<String> getGeneros(Node nodePelicula) throws Exception {
		List<String> generos = new ArrayList<String>();
		NodeList nodos = nodePelicula.getChildNodes();
		for(int i = 0; i < nodos.getLength(); i++) {
			if (nodos.item(i).getNodeName().equals("Generos")) {
				NodeList nodosGenero = nodos.item(i).getChildNodes();
				for (int j = 0; j < nodosGenero.getLength(); j++) {
					if (nodosGenero.item(j).getNodeName().equals("Genero")) {
						generos.add(nodosGenero.item(j).getFirstChild().getNodeValue());
					}
				}
			}
		}
		return generos;
		
	}
	
	private static Short getDuracion(Node nodePelicula) throws Exception {
		NodeList nodos = nodePelicula.getChildNodes();
		for(int i = 0; i < nodos.getLength(); i++) {
			if (nodos.item(i).getNodeName().equals("Duracion")) {
				return Short.valueOf(nodos.item(i).getFirstChild().getNodeValue());
			}
		}
		throw new Exception("Error recuperando la duracion de la pelicula");
	}
	
	private static List<Reparto> getReparto(Node nodePelicula) throws Exception {
		List<Reparto> repartoList = new ArrayList<Reparto>();
		NodeList nodos = nodePelicula.getChildNodes();
		for(int i = 0; i < nodos.getLength(); i++) {
			if (nodos.item(i).getNodeName().equals("Reparto")) {
				String nombreReparto = getNombreReparto(nodos.item(i));
				String personajeReparto = getPersonajeReparto(nodos.item(i));
				String oscar = getOscar(nodos.item(i));
				String ciudad = getCiudadReparto(nodos.item(i));
				OtraPelicula otraPelicula = getOtraPelicula(nodos.item(i));
				Reparto reparto = new Reparto();
				reparto.setNombre(nombreReparto);
				reparto.setPersonaje(personajeReparto);
				reparto.setOscar(oscar);
				reparto.setCiudadNatal(ciudad);
				reparto.setOtraPelicula(otraPelicula);
				repartoList.add(reparto);
			}
		}
		return repartoList;
		
	}
	
	private static String getNombreReparto(Node nodeReparto) throws Exception {
		NodeList nodos = nodeReparto.getChildNodes();
		for(int i = 0; i < nodos.getLength(); i++) {
			if (nodos.item(i).getNodeName().equals("Nombre")) {
				return nodos.item(i).getFirstChild().getNodeValue();
			}
		}
		throw new Exception("Error recuperando nombre reparto");
	}
	
	private static String getPersonajeReparto(Node nodeReparto) throws Exception {
		NodeList nodos = nodeReparto.getChildNodes();
		for(int i = 0; i < nodos.getLength(); i++) {
			if (nodos.item(i).getNodeName().equals("Personaje")) {
				return nodos.item(i).getFirstChild().getNodeValue();
			}
		}
		throw new Exception("Error recuperando personaje reparto");
	}
	
	private static String getOscar(Node nodeReparto) throws Exception {
		NodeList nodos = nodeReparto.getChildNodes();
		for(int i = 0; i < nodos.getLength(); i++) {
			if (nodos.item(i).getNodeName().equals("Oscar")) {
				return nodos.item(i).getFirstChild().getNodeValue();
			}
		}
		return null;
	}
	
	private static String getCiudadReparto(Node nodeReparto) {
		String ciudad = "";
		NodeList nodos = nodeReparto.getChildNodes();
		for (int i = 0; i < nodos.getLength(); i++) {
			if(nodos.item(i).getNodeType() == Node.TEXT_NODE) {
				ciudad += nodos.item(i).getNodeValue();
			}
		}
		return ciudad.trim();
	}
	
	private static OtraPelicula getOtraPelicula(Node nodeReparto) throws Exception {
		OtraPelicula otraPelicula = null;
		NodeList nodos = nodeReparto.getChildNodes();
		for(int i = 0; i < nodos.getLength(); i++) {
			if (nodos.item(i).getNodeName().equals("OtraPelicula")) {
				otraPelicula = new OtraPelicula();
				NamedNodeMap atributos = nodos.item(i).getAttributes();
				Short anio = Short.valueOf(atributos.getNamedItem("anio").getNodeValue());
				String titulo = getTituloOtraPelicula(nodos.item(i));
				Reparto reparto = new Reparto();
				otraPelicula.setAnio(anio);
				otraPelicula.setTitulo(titulo);
			}
		}
		return otraPelicula;
		
	}
	
	
	private static class MyErrorHandler implements ErrorHandler {
	     
	    private PrintWriter out;

	    MyErrorHandler(PrintWriter out) {
	        this.out = out;
	    }

	    private String getParseExceptionInfo(SAXParseException spe) {
	        String systemId = spe.getSystemId();
	        if (systemId == null) {
	            systemId = "null";
	        }

	        String info = "URI=" + systemId + " Line=" + spe.getLineNumber() +
	                      ": " + spe.getMessage();
	        return info;
	    }

	    public void warning(SAXParseException spe) throws SAXException {
	        out.println("Warning: " + getParseExceptionInfo(spe));
	    }
	        
	    public void error(SAXParseException spe) throws SAXException {
	        String message = "Error: " + getParseExceptionInfo(spe);
	        throw new SAXException(message);
	    }

	    public void fatalError(SAXParseException spe) throws SAXException {
	        String message = "Fatal Error: " + getParseExceptionInfo(spe);
	        throw new SAXException(message);
	    }
	}

	private static void printlnCommon(Node n) {
	    out.print(" nodeName=\"" + n.getNodeName() + "\"");

	    String val = n.getNamespaceURI();
	    if (val != null) {
	        out.print(" uri=\"" + val + "\"");
	    }

	    val = n.getPrefix();

	    if (val != null) {
	        out.print(" pre=\"" + val + "\"");
	    }

	    val = n.getLocalName();
	    if (val != null) {
	        out.print(" local=\"" + val + "\"");
	    }

	    val = n.getNodeValue();
	    if (val != null) {
	        out.print(" nodeValue=");
	        if (val.trim().equals("")) {
	            // Whitespace
	            out.print("[WS]");
	        }
	        else {
	            out.print("\"" + n.getNodeValue() + "\"");
	        }
	    }
	    out.println();
	}
	
	private static void outputIndentation() {
	    for (int i = 0; i < indent; i++) {
	        out.print(basicIndent);
	    }
	}
	
	
	private static void buscarPorNodoYAtributo(Node nodo, String nameElementNode, String nameAttribute, List<String> valores) {
		int tipoNodo = nodo.getNodeType();
		switch(tipoNodo) {
			case Node.ATTRIBUTE_NODE:
				if(nodo.getNodeName().equals(nameAttribute)) {
					valores.add(nodo.getNodeValue());
				}
				break;
			case Node.ELEMENT_NODE:
				if (nodo.getNodeName().equals("MML")) {
					Document nuevoDocumento = cargarDocumento(nodo.getFirstChild().getNodeValue());
					if (nuevoDocumento != null) {
						buscarPorNodoYAtributo(nuevoDocumento.getFirstChild(), nameElementNode, nameAttribute, valores);
					}
				}
	            NamedNodeMap atributos = nodo.getAttributes();
	            for (int i = 0; i < atributos.getLength(); i++) {
	                Node atributo = atributos.item(i);
	                buscarPorNodoYAtributo(atributo, nameElementNode, nameAttribute, valores);
	            }
	            break;
		}
		
		NodeList hijos = nodo.getChildNodes();
		for (int i = 0; i < hijos.getLength(); i++) {
			buscarPorNodoYAtributo(hijos.item(i), nameElementNode, nameAttribute, valores);
		}
	}
	
	private static List<String> getC2Langs() {
		// TODO eliminar los duplicados y ordenar descendentemente
		return langs;
	}
	
	private static List<Actor> getC2Acts(String plang) {
		// TODO eliminar los duplicados y ordenar descendentemente
		return acts;
	}
	
	private static void echo(Node n) {
	    outputIndentation();
	    int type = n.getNodeType();

	    switch (type) {
	        case Node.ATTRIBUTE_NODE:
	            out.print("ATTR:");
	            printlnCommon(n);
	            break;

	        case Node.CDATA_SECTION_NODE:
	            out.print("CDATA:");
	            printlnCommon(n);
	            break;

	        case Node.COMMENT_NODE:
	            out.print("COMM:");
	            printlnCommon(n);
	            break;

	        case Node.DOCUMENT_FRAGMENT_NODE:
	            out.print("DOC_FRAG:");
	            printlnCommon(n);
	            break;

	        case Node.DOCUMENT_NODE:
	            out.print("DOC:");
	            printlnCommon(n);
	            break;

	        case Node.DOCUMENT_TYPE_NODE:
	            out.print("DOC_TYPE:");
	            printlnCommon(n);
	            NamedNodeMap nodeMap = ((DocumentType)n).getEntities();
	            indent += 2;
	            for (int i = 0; i < nodeMap.getLength(); i++) {
	                Entity entity = (Entity)nodeMap.item(i);
	                echo(entity);
	            }
	            indent -= 2;
	            break;

	        case Node.ELEMENT_NODE:
	            out.print("ELEM:");
	            printlnCommon(n);

	            NamedNodeMap atts = n.getAttributes();
	            indent += 2;
	            for (int i = 0; i < atts.getLength(); i++) {
	                Node att = atts.item(i);
	                echo(att);
	            }
	            indent -= 2;
	            break;

	        case Node.ENTITY_NODE:
	            out.print("ENT:");
	            printlnCommon(n);
	            break;

	        case Node.ENTITY_REFERENCE_NODE:
	            out.print("ENT_REF:");
	            printlnCommon(n);
	            break;

	        case Node.NOTATION_NODE:
	            out.print("NOTATION:");
	            printlnCommon(n);
	            break;

	        case Node.PROCESSING_INSTRUCTION_NODE:
	            out.print("PROC_INST:");
	            printlnCommon(n);
	            break;

	        case Node.TEXT_NODE:
	            out.print("TEXT:");
	            printlnCommon(n);
	            break;

	        default:
	            out.print("UNSUPPORTED NODE: " + type);
	            printlnCommon(n);
	            break;
	    }

	    indent++;
	    for (Node child = n.getFirstChild(); child != null;
	         child = child.getNextSibling()) {
	        echo(child);
	    }
	    indent--;
	}
	
}
