package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Comparator utils class.
 */
@Slf4j
public class ComparatorUtils {

    private ComparatorUtils() {}

    /**
     * Compares to XML Nodes.
     * @param expectedXml The expected node.
     * @param actualXml The actual node.
     * @return The result.
     */
    public static boolean compareXmlNodes(Node expectedXml, Node actualXml) {
        DataValidationUtils.validateNotNull(expectedXml, "expectedXml");
        DataValidationUtils.validateNotNull(actualXml, "actualXml");
        DataValidationUtils.validateNotTheSame(expectedXml, actualXml,
                "expectedXml", "actualXml");

        try {
            if (!expectedXml.getNodeName().equals(actualXml.getNodeName())) {
                logComparatorResult(expectedXml, actualXml, false);
                return false;
            }
            // Compare text content
            if (expectedXml.getNodeType() == Node.TEXT_NODE && actualXml.getNodeType() == Node.TEXT_NODE) {
                if (!expectedXml.getTextContent().trim().equals(actualXml.getTextContent().trim())) {
                    logComparatorResult(expectedXml, actualXml, false);
                    return false;
                }
            }
            // Compare attributes
            NamedNodeMap attributes1 = expectedXml.getAttributes();
            NamedNodeMap attributes2 = actualXml.getAttributes();
            if (attributes1 != null && attributes2 != null) {
                if (attributes1.getLength() != attributes2.getLength()) {
                    logComparatorResult(expectedXml, actualXml, false);
                    return false;
                }
                for (int i = 0; i < attributes1.getLength(); i++) {
                    Node attr1 = attributes1.item(i);
                    Node attr2 = attributes2.getNamedItem(attr1.getNodeName());
                    if (attr2 == null || !attr1.getNodeValue().equals(attr2.getNodeValue())) {
                        logComparatorResult(expectedXml, actualXml, false);
                        return false;
                    }
                }
            } else if (attributes1 != attributes2) {
                // One has attributes, the other does not
                logComparatorResult(expectedXml, actualXml, false);
                return false;
            }
            // Compare child nodes
            Node child1 = expectedXml.getFirstChild();
            Node child2 = actualXml.getFirstChild();
            while (child1 != null && child2 != null) {
                if (!compareXmlNodes(child1, child2)) {
                    logComparatorResult(expectedXml, actualXml, false);
                    return false;
                }
                child1 = child1.getNextSibling();
                child2 = child2.getNextSibling();
            }
            // Ensure both nodes have the same number of children
            boolean result = child1 == null && child2 == null;
            logComparatorResult(expectedXml, actualXml, result);
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot compare expected and actual XML nodes.\nExpected:\n%s\nActual:\n%s",
                    ConverterUtils.xmlToString(expectedXml),
                    ConverterUtils.xmlToString(actualXml)));
        }
    }

    private static void logComparatorResult(Object expected, Object actual, boolean result) {
        log.debug("""
                    Two objects were compared.
                    Expected:
                    {}
                    Actual:
                    {}
                    Result:
                    {}
                    """.stripIndent(),
                expected, actual, result);
    }
}
