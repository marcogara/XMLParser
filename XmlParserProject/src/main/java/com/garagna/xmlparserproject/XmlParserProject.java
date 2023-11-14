package com.garagna.xmlparserproject;

import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import org.xml.sax.Locator;

public class XmlParserProject {

	private static final String LOG_FILE = "your_file_name.txt";

	public static void main(String[] args) {
		File logFile = new File(LOG_FILE);
		if (logFile.exists())
		{
			logFile.delete();
		}
        for (String projectPath : getProjectPaths()) {
            processProject(projectPath);
        }
    }

    private static String[] getProjectPaths() {
        return new String[]{
                "your\\file\\path\\p1",
                "your\\file\\path\\p2",
                "your\\file\\path\\p3"
        };
    }

	private static void processProject(String rootPath)
	{
		String projectName = Paths.get(rootPath).getFileName().toString();

		try {
			File logFile = new File(LOG_FILE);
            System.setOut(new PrintStream(new FileOutputStream(logFile, true))); // Append to the same file
			System.out.println(projectName);

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
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            // Check if the directory contains "target"
                            if (dir.toString().contains("target")) {
                                return FileVisitResult.SKIP_SUBTREE; // Skip the entire subtree
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

		System.out.println(); // Add a blank line after processing the project
	}

    private static void parseJspFile(String filePath) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            MyHandler handler = new MyHandler(filePath);
            saxParser.parse(new File(filePath), handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MyHandler extends DefaultHandler {
		private Locator locator;
        private String currentFileName;
		private String currentElementName;

        public MyHandler(String currentFileName) {
            this.currentFileName = currentFileName;
        }

		@Override
		public void setDocumentLocator(Locator locator)
		{
			this.locator = locator;
		}

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

			currentElementName = qName;  // Store the qualified name of the current element

			// Check 'text' attribute
			String textAttribute = attributes.getValue("text");
			if (textAttribute != null && !textAttribute.isEmpty() && !textAttribute.startsWith("#")) {
			logIssue("text", textAttribute);
			}

			// Check 'label' attribute
			String labelAttribute = attributes.getValue("label");
			if (labelAttribute != null && !labelAttribute.isEmpty() && !labelAttribute.startsWith("#")) {
			logIssue("label", labelAttribute);
			}
		}

		private void logIssue(String attributeName, String attributeValue) {
		System.out.println("Missing '#' in '" + attributeName + "' attribute at line " + this.locator.getLineNumber()
            + " in element: " + currentElementName + " with value: '" + attributeValue + "' in file: " + currentFileName);
        }
    }
}
