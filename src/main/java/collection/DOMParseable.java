package collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface DOMParseable {
    static Element createTextElement(Document document, String nodeName, String text) {
        Element element = document.createElement(nodeName);
        element.setTextContent(text);
        return element;
    }
    Element parse(Document document);
    void parse(Element element);

}
