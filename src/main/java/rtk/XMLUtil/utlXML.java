/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtk.XMLUtil;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author vasil
 */
public class utlXML<T> {

    /**
     *
     * @param doc
     * @return
     */
    public static String xmlToString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (IllegalArgumentException | TransformerException ex) {
            return null;
        }
    }

    public static Document stringToXml(String xmlString)  {
        try {
            System.out.println("stringToXml => " + xmlString);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * 
     * @param t
     * @return 
     */
     public  String convertObjectToXml(T t) {
        try {
            JAXBContext context = JAXBContext.newInstance(t.getClass());
            Marshaller marshaller = context.createMarshaller();
            // устанавливаем флаг для читабельного вывода XML в JAXB
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // маршаллинг объекта в строку
            StringWriter sw = new StringWriter();
            marshaller.marshal(t, sw);
            return sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

     /**
      * 
      * @param xml
      * @return 
      */
    public T convertFromXml(String xml) {
        T res = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(res.getClass());

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            StringReader sw = new StringReader(xml);
            //sw.write(xml);
            res = (T) jaxbUnmarshaller.unmarshal(sw);
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    
    
    /**
     *
     * @param date
     * @return
     */
    public static String formatDateForXML(Date date) {
        String res = null;
        try {
            SimpleDateFormat dateFormatYear = new SimpleDateFormat("YYYY-MM-dd'T'HH-mm-ssZZ");
            StringBuilder temp = new StringBuilder();
            temp.append(dateFormatYear.format(date));
            temp.insert(temp.length() - 2, ":");
            res = temp.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
