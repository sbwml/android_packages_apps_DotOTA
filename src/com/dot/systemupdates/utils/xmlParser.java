package com.dot.systemupdates.utils;

import android.os.AsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class xmlParser extends AsyncTask<String, Integer, String[]> {
    NodeList nodeList;
    String[] xmlValues = new String[4];
    @Override
    protected String[] doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(url.openStream()));
            document.getDocumentElement().normalize();
            nodeList = document.getElementsByTagName("ROM");
            Node node = nodeList.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                NodeList versionNL = element.getElementsByTagName("VersionName").item(0).getChildNodes();
                Node versionNode = versionNL.item(0);
                NodeList changelogNL = element.getElementsByTagName("Changelog").item(0).getChildNodes();
                Node changelogNode = changelogNL.item(0);
                NodeList urlNL = element.getElementsByTagName("DirectUrl").item(0).getChildNodes();
                Node urlNode = urlNL.item(0);
                NodeList sizeINL = element.getElementsByTagName("FileSize").item(0).getChildNodes();
                Node sizeNode = sizeINL.item(0);
                xmlValues[0] = versionNode.getNodeValue();
                xmlValues[1] = changelogNode.getNodeValue();
                xmlValues[2] = urlNode.getNodeValue();
                xmlValues[3] = sizeNode.getNodeValue();
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return xmlValues;
    }
}