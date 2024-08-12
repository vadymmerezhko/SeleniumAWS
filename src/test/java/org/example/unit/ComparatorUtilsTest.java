package org.example.unit;

import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ComparatorUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ComparatorUtilsTest {

    private DocumentBuilder documentBuilder;

    @BeforeClass
    public void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        documentBuilder = factory.newDocumentBuilder();
    }

    @Test
    public void testCompareXmlNodes_Positive_SameNodes() throws Exception {
        // Arrange
        String xmlContent = "<root><child attr='value'>Text</child></root>";
        Document doc1 = documentBuilder.parse(new java.io.ByteArrayInputStream(xmlContent.getBytes()));
        Document doc2 = documentBuilder.parse(new java.io.ByteArrayInputStream(xmlContent.getBytes()));

        Node node1 = doc1.getDocumentElement();
        Node node2 = doc2.getDocumentElement();

        // Act
        boolean result = ComparatorUtils.compareXmlNodes(node1, node2);

        // Assert
        Assert.assertTrue(result, "Expected nodes to be equal.");
    }

    @Test
    public void testCompareXmlNodes_Negative_DifferentNodes() throws Exception {
        // Arrange
        String xmlContent1 = "<root><child attr='value'>Text</child></root>";
        String xmlContent2 = "<root><child attr='differentValue'>Text</child></root>";

        Document doc1 = documentBuilder.parse(new java.io.ByteArrayInputStream(xmlContent1.getBytes()));
        Document doc2 = documentBuilder.parse(new java.io.ByteArrayInputStream(xmlContent2.getBytes()));

        Node node1 = doc1.getDocumentElement();
        Node node2 = doc2.getDocumentElement();

        // Act
        boolean result = ComparatorUtils.compareXmlNodes(node1, node2);

        // Assert
        Assert.assertFalse(result, "Expected nodes to be different.");
    }

    @Test
    public void testCompareXmlNodes_Negative_NullExpectedNode() {
        // Arrange
        Node node1 = null;
        Node node2 = documentBuilder.newDocument().createElement("root");

        // Act & Assert
        try {
            ComparatorUtils.compareXmlNodes(node1, node2);
            Assert.fail("Expected an exception to be thrown when expectedXml is null.");
        } catch (SmartRuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("expectedXml"), "Exception message should indicate that the expectedXml is null.");
        }
    }

    @Test
    public void testCompareXmlNodes_Negative_NullActualNode() {
        // Arrange
        Node node1 = documentBuilder.newDocument().createElement("root");
        Node node2 = null;

        // Act & Assert
        try {
            ComparatorUtils.compareXmlNodes(node1, node2);
            Assert.fail("Expected an exception to be thrown when actualXml is null.");
        } catch (SmartRuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("actualXml"), "Exception message should indicate that the actualXml is null.");
        }
    }

    @Test
    public void testCompareXmlNodes_Negative_NodesAreTheSameObject() {
        // Arrange
        Node node = documentBuilder.newDocument().createElement("root");

        // Act & Assert
        try {
            ComparatorUtils.compareXmlNodes(node, node);
            Assert.fail("Expected an exception to be thrown when expectedXml and actualXml are the same object.");
        } catch (SmartRuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("expectedXml"), "Exception message should indicate that the nodes are the same object.");
        }
    }

    @Test
    public void testCompareXmlNodes_Positive_DifferentStructure() throws Exception {
        // Arrange
        String xmlContent1 = "<root><child1>Text</child1></root>";
        String xmlContent2 = "<root><child2>Text</child2></root>";

        Document doc1 = documentBuilder.parse(new java.io.ByteArrayInputStream(xmlContent1.getBytes()));
        Document doc2 = documentBuilder.parse(new java.io.ByteArrayInputStream(xmlContent2.getBytes()));

        Node node1 = doc1.getDocumentElement();
        Node node2 = doc2.getDocumentElement();

        // Act
        boolean result = ComparatorUtils.compareXmlNodes(node1, node2);

        // Assert
        Assert.assertFalse(result, "Expected nodes with different structure to be different.");
    }
}
