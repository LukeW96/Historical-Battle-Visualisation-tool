import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public class Position
{
  public Position()
  {
    try
    { 
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();
    }
    catch(Exception e)
    {}
  }
  void writePosition(int frame, String objName, int posX, int posY)
  {
    
  }
  
  
  
  int readXPosition(int frame, String objName)
  {
    return 1;
  }
  
  int readYPosition(int frame, String objName)
  {
    return 1;
  }  
}