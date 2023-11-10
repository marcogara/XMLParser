

package com.garagna.xmlparserproject;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

public class XmlParserProject {

    public static void main(String[] args) {
        try {
            String filePath = "path/to/your/file.xml";
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            MyHandler handler = new MyHandler();
            saxParser.parse(new File(filePath), handler);
            handler.printResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MyHandler extends DefaultHandler {
        private int lineNumber = 0;
        private String currentFileName;
        private boolean foundIssue = false;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            lineNumber++;
            if ("ui:label".equals(qName)) {
                String textAttribute = attributes.getValue("text");
                if (textAttribute != null && !textAttribute.isEmpty() && !textAttribute.contains("#")) {
                    foundIssue = true;
                    System.out.println("Missing '#' in 'text' attribute at line " + lineNumber + " in file: " + currentFileName);
                    // You can log this information instead of printing to the console
                }
            }
        }

        @Override
        public void startDocument() throws SAXException {
            currentFileName = ""; // Initialize the currentFileName
        }

        @Override
        public void endDocument() throws SAXException {
            if (!foundIssue) {
                System.out.println("ok");
            }
        }

        public void printResult() {
            if (foundIssue) {
                System.out.println("Issues found in file: " + currentFileName);
            }
        }
    }
}


