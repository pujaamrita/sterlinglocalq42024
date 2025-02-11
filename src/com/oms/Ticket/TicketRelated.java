package com.oms.Ticket;

import org.w3c.dom.Document;
import org.json.JSONObject;
import org.json.XML;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;

import com.yantra.yfs.japi.YFSEnvironment;

public class TicketRelated {

    public Document TicketMethod(YFSEnvironment env, Document doc) {
        try {
            String xmlString = convertDocumentToString(doc);
            
            JSONObject json = XML.toJSONObject(xmlString);

            System.out.println("Converted JSON: \n" + json.toString(4));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc; 
    }

    private String convertDocumentToString(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            
            File xmlFile = new File("/Users/jatlasrigowri/Documents/javacodes/Sterling_oms/src/com/oms/Ticket/example.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            TicketRelated ticketRelated = new TicketRelated();
            ticketRelated.TicketMethod(null, doc); 

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
