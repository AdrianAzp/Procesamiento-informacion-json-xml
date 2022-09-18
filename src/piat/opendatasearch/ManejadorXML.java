package piat.opendatasearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Adrian Azpilicueta Pomar 02573134D
 * 
 */

/*
 * Paso 1: Detectar cuando llega un evento startElement del elemento <concept>
 * para guardar temporalmente el valor del atributo id y anotar que se ha
 * entrado en un <concept>
 * 
 * � Paso 2: esperar la llegada de un evento endElement del elemento </code>. Si
 * se est� dentro de un <concept> y el contenido es el c�digo de la categor�a
 * buscada, almacenar el valor del atributo id guardado en el paso 1 en el
 * ArrayList lConcepts. Anotar que se ha encontrado la categor�a y es del primer
 * nivel
 * 
 * � Paso 3: esperar la llegada del evento endElement de </label>. Si se est�
 * dentro de un <concept>, se ha encontrado la categor�a y se est� en el primer
 * nivel, almacenar el contenido del elemento en el atributo sNombreCategoria
 * 
 * � Paso 4: para poder recoger todas las subcategor�as, mientras est� abierto
 * el <concept> correspondiente a la categor�a buscada, cuando lleguen eventos
 * startElement de <concept> , se deber�n obtener los atributo id de los
 * <concept> y almacenarlos tambi�n en lConcepts. Adem�s habr� que ir
 * incrementando una variable que indique el nivel y decrementarla cuando se
 * salga del <concept> correspondiente hasta llegar a 0, que indicar� que se ha
 * salido del <concept> ra�z.
 */

public class ManejadorXML extends DefaultHandler implements ParserCatalogo {

	private boolean debug = false;
	public static final String ANSI_CYAN = "\u001B[36m";

	private String title; // Cosas de dataset
	private String description;
	private String theme;//

	private String sCodigoConcepto; // codigo a leer
	private String sCodigo; // Codigo de la categoria, su CODE
	private String sNombreCategoria; // Nombre de la categoría su LABEL
	private String sIDConcept; // ID de la categoria su ID
	private String sIDDataset; // ID del dataset
	private HashMap<String, String> auxDatasets; // Hashmap para guardar lo que puede ser util dataset
	private String sIDConceptPedido; // ID que se pide para checkear los catalogos
	private List<String> lConcepts; // Lista con los uris de los elementos <concept> que pertenecen a la categoría
	private Map<String, HashMap<String, String>> hDatasets; // Mapa con información de los dataset que pertenecen a la
															// categoría
	private StringBuilder contenidoElemento;
	private boolean esLabel = false;
	private boolean esConcepto = false;
	private boolean esDataset = false;
	private boolean tengoCategoria = false;
	private boolean comprobacion = false;

	/**
	 * @param sCodigoConcepto código de la categoría a procesar
	 * @throws ParserConfigurationException
	 */
	public ManejadorXML(String sCodigoConcepto) throws SAXException, ParserConfigurationException {
		// TODO
		this.sCodigoConcepto = sCodigoConcepto;
		contenidoElemento = new StringBuilder();
		lConcepts = new ArrayList<String>();
		hDatasets = new HashMap<String, HashMap<String, String>>();
		tengoCategoria = false;
		comprobacion = false;
	}

	// ===========================================================
	// Métodos a implementar de la interfaz ParserCatalogo
	// ===========================================================
	/**
	 * <code><b>getCode</b></code>
	 * 
	 * @return Valor de la cadena del elemento code para ver su codigo
	 */
	public String getCode() {
		return sCodigoConcepto;
	}

	/**
	 * <code><b>getLabel</b></code>
	 * 
	 * @return Valor de la cadena del elemento <code>label</code> del
	 *         <code>concept</code> cuyo elemento <code><b>code</b></code> sea
	 *         <b>igual</b> al criterio a búsqueda. <br>
	 *         null si no se ha encontrado el concept pertinente o no se dispone de
	 *         esta información
	 */
	@Override
	public String getLabel() {
		// TODO
		return sNombreCategoria;
	}

	/**
	 * <code><b>getConcepts</b></code> Devuelve una lista con información de los
	 * <code><b>concepts</b></code> resultantes de la búsqueda. <br>
	 * Cada uno de los elementos de la lista contiene la <code><em>URI</em></code>
	 * del <code>concept</code>
	 * 
	 * <br>
	 * Se considerarán pertinentes el <code><b>concept</b></code> cuyo código sea
	 * igual al criterio de búsqueda y todos sus <code>concept</code> descendientes.
	 * 
	 * @return - List con la <em>URI</em> de los concepts pertinentes. <br>
	 *         - null si no hay concepts pertinentes.
	 * 
	 */
	@Override
	public List<String> getConcepts() {
		// TODO
		return lConcepts;
	}

	/**
	 * <code><b>getDatasets</b></code>
	 * 
	 * @return Mapa con información de los <code>dataset</code> resultantes de la
	 *         búsqueda. <br>
	 *         Si no se ha realizado ninguna búsqueda o no hay dataset pertinentes
	 *         devolverá el valor <code>null</code> <br>
	 *         Estructura de cada elemento del map: <br>
	 *         . <b>key</b>: valor del atributo ID del elemento
	 *         <code>dataset</code>con la cadena de la <code><em>URI</em></code>
	 *         <br>
	 *         . <b>value</b>: Mapa con la información a extraer del
	 *         <code>dataset</code>. Cada <code>key</code> tomará los valores
	 *         <em>title</em>, <em>description</em> o <em>theme</em>, y
	 *         <code>value</code> sus correspondientes valores.
	 * 
	 * @return - Map con información de los <code>dataset</code> resultantes de la
	 *         búsqueda. <br>
	 *         - null si no hay datasets pertinentes.
	 */
	@Override
	public Map<String, HashMap<String, String>> getDatasets() {
		// TODO
		return hDatasets;
	}

	// ===========================================================
	// Métodos a implementar de SAX DocumentHandler
	// ===========================================================

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		// TODO
		if (debug) {
			System.out.println(ANSI_CYAN + "Inicio del documento");
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		// TODO
		if (debug) {
			System.out.println(ANSI_CYAN + "Fin del documento");
		}

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		// TODO
		// Miro si el elemento es de etiqueta CONCEPT
		// Si el elemento code coincide lo pillo, no aqui pero eso
		if (qName.equals("concept")) {
			sIDConcept = attributes.getValue("id");
			esConcepto = true;
			if (debug) {
				System.out.println(sIDConcept);
			}
		}
		// Miro si el elemento es un dataset
		if (qName.equals("dataset")) {
			sIDDataset = attributes.getValue("id");
			sIDConcept = "";
			comprobacion = false;
			auxDatasets = new HashMap<String, String>();
			if (debug) {
				System.out.println(sIDDataset);
			}
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		// TODO
		if (qName.equals("code")) { // Pillo el elemento codigo y entro
			sCodigo = contenidoElemento.toString().trim(); // Cojo el valor del codigo
			// Si el codigo coincide con el criterio de busqueda
			// Si el codigo coincide con los concepts descendientes
			if (debug) {
				System.out.println(sCodigo);
			}
			if (sCodigo.equals(sCodigoConcepto) && tengoCategoria == false) { // Si el codigo del elemento es igual al
																				// que nos han pedido en el constructor,
																				// entra
				sIDConceptPedido = sIDConcept;
				lConcepts.add(sIDConcept); // Guarda el valor del ID
				tengoCategoria = true; // He encontrado una categoria
				if (debug) {
					System.out.println(sIDConcept);
				}

			}
			if (tengoCategoria) {
				if (sIDConcept.contains(sIDConceptPedido) && !sIDConcept.equals(sIDConceptPedido)) {
					lConcepts.add(sIDConcept); // Guarda el valor del ID
					if (debug) {
						System.out.println(sIDConcept);
					}
				}
			}
		}

		if (qName.equals("label")) {// Cogemos el label
			if (tengoCategoria == true && sIDConcept.equals(sIDConceptPedido)) {
				sNombreCategoria = contenidoElemento.toString().trim(); // Guarda el valor de la label
				if (debug) {
					System.out.println(sNombreCategoria);
				}
			}
		}

		if (qName.equals("title")) {
			title = contenidoElemento.toString().trim();
			if (debug) {
				System.out.println(title);
			}

		}

		if (qName.equals("description")) {
			description = contenidoElemento.toString().trim();
			if (debug) {
				System.out.println(description);
			}
		}

		if (qName.equals("theme")) {
			theme = contenidoElemento.toString().trim();
			if (debug) {
				System.out.println(theme);
			}
		}

		if (qName.equals("concept")) {
			for (String aux : lConcepts) {
				if (aux.equals(sIDConcept))
					comprobacion = true;
			}
		}
		if (qName.equals("dataset") && comprobacion) {
			auxDatasets.put("title", title);
			auxDatasets.put("description", description);
			auxDatasets.put("theme", theme);
			hDatasets.put(sIDDataset, auxDatasets);
		}

		contenidoElemento.setLength(0);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		// TODO
		contenidoElemento.append(ch, start, length);
	}

	public boolean isDebug() {
		return debug;
	}

}
