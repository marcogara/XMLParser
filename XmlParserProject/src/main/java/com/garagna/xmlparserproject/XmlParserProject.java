package com.garagna.xmlparserproject;

import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public class XmlParserProject {

    public static void main(String[] args) {
        try {
            String rootPath = "path/to/your/project"; // Set the root path of your project
            Files.walkFileTree(Paths.get(rootPath), EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if (file.toString().endsWith(".jsp")) {
                                parseJspFile(file.toString());
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                            // Handle the case where file visit failed
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseJspFile(String filePath) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            MyHandler handler = new MyHandler(filePath);
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

        public MyHandler(String currentFileName) {
            this.currentFileName = currentFileName;
        }

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
