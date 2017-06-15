import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.*;

class Time {

  //solution, can keep track of keyframes within array, and then can check
  //if the xml value exists in said array
  private XML xml;
  public Time()
  {
  }


  public void createFrame(int frame)
  {
    xml = loadXML("xmldoc.XML");
    XML children[] = xml.getChildren("frame"+frame);
    if(children.length == 0)
    {
      xml.addChild("frame"+frame);
      saveXML(xml, "xmldoc.XML");  
    }  
  }  

  

  public int[] readPosAtTime(int unitID, int frame)
  {
    
    int[] posArray = new int[3];
    xml = loadXML("xmldoc.XML");
    XML[] children = xml.getChildren("frame" + frame);
    XML child = children[0].getChild("unit" + unitID);  
    if(child != null)
    {
      XML pos = child.getChild("position");
      
      posArray[0] = pos.getInt("x");
      posArray[1] = pos.getInt("y");
      
      XML count = child.getChild("troopCount");
      posArray[2] = count.getInt("size");
      return posArray;
     
    }
    int[] temp = {0,0,0};
    return temp;
  }

  //store unit type in XML
  public void writeUnitType(int unitID, String unitShape)
  {
    xml = loadXML("xmldoc.XML");
    XML[] children = xml.getChildren("frame1");
    XML child = children[0].getChild("unit" + unitID);
    child.addChild("unitType");
    XML unitType = child.getChild("unitType");
    unitType.setString("type", unitShape);
    
    saveXML(xml, "xmldoc.XML");
  }
  
  public String getUnitType(int unitID)
  {
    xml = loadXML("xmldoc.XML");
    XML[] children = xml.getChildren("frame1");
    XML child = children[0].getChild("unit" + unitID);
    XML unitType = child.getChild("unitType");
    
    String type = unitType.getString("type");
    return type;   
  }
 
 //creates the unit fully, includes all of the required info
  public void createUnit(int unitID,int maxFrame, int x, int y, String unitType, int troopSize, int faction)
  {
    for(int i = 1; i < maxFrame; i++)
    {
      if(checkKeyFrame(i))
      {
        writePos(unitID, i, x,y,troopSize);
      }
    }
    writeUnitType(unitID, unitType);
    setFaction(unitID, faction);
  }
  
  
  public void writePos(int unitID, int frame, int x, int y, int size)
  {

    xml = loadXML("xmldoc.XML");
    XML[] children = xml.getChildren("frame" + frame);
   
    if(children[0].getChild("unit" + unitID) == null)
    {
      children[0].addChild("unit" + unitID);
      XML child = children[0].getChild("unit" + unitID);   
      
     
      child.addChild("troopCount");
      child.addChild("position");
      XML pos = child.getChild("position");
      pos.setInt("x", x);
      pos.setInt("y", y);
      
      XML troop = child.getChild("troopCount");
      troop.setInt("size",size);
    }
    saveXML(xml, "xmldoc.XML");
  }
  
  public void updatePos(int unitID, int frame, int x, int y)
  {
    xml = loadXML("xmldoc.XML");   
    XML[] children = xml.getChildren("frame"+frame);
    XML child = children[0].getChild("unit"+unitID);
    XML pos = child.getChild("position");

    pos.setInt("x", x);
    pos.setInt("y", y);
    saveXML(xml, "xmldoc.XML");
  }
  
  //frame 1 is always a keyframe
  public void firstFrameKeyFrame()
  {
    if(!checkKeyFrame(1))
    {
      xml = loadXML("xmldoc.XML");
      XML children[] = xml.getChildren("frame"+1);
      children[0].addChild("keyframe");
      saveXML(xml, "xmldoc.XML");
    }
  }
  
  public void makeKeyFrame(int frame)
  {
    if(!checkKeyFrame(frame))
    {
      int previousFrame = getPreviousKeyFrame(frame);
      xml = loadXML("xmldoc.XML");
      XML children[] = xml.getChildren("frame"+frame);
      children[0].addChild("keyframe");
      saveXML(xml, "xmldoc.XML");
      savePositionAsPreviousKeyFrame(frame, previousFrame);
    }
  }
  
  //stores all of the potions from last keyframe in current keyframe
  private void savePositionAsPreviousKeyFrame(int frame, int previousFrame)
  {
    int[] posArray = new int[3];
    int i = 0;
    while(checkUnitExists(i, previousFrame))
    {
       posArray = readPosAtTime(i, previousFrame);
       writePos(i, frame, posArray[0], posArray[1], posArray[2]);
       i++;
    }
  }
  
  //checks if the frame is a keyframe
  public boolean checkKeyFrame(int frame)
  { 
    xml = loadXML("xmldoc.XML");
    XML children[] = xml.getChildren("frame"+frame);
    XML child = children[0].getChild("keyframe");
    if(child != null)
    {
       return true;
    }   
    return false;   
  }
  
  //check if unit exists in xmldocument
  public boolean checkUnitExists(int unitID, int frame)
  {
    try
    {
      xml = loadXML("xmldoc.XML");
      XML children[] = xml.getChildren("frame"+frame);
      XML child = children[0].getChild("unit" + unitID);
      if(child != null)
      {
        return true;
      }
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      print("Exception Caught");
      return false;
    }
    return false;
  }
  
  
  public void removeUnitAllFrames(int unitID, int maxFrame)
  {
    xml = loadXML("xmldoc.XML");
    while(checkNextKeyFrame(1,maxFrame) != 0)
    {
      int temp = checkNextKeyFrame(1,maxFrame);
      XML children[] = xml.getChildren("frame"+temp);
      XML child = children[0].getChild("unit" + unitID);
      children[0].removeChild(child);
      saveXML(xml, "xmlDoc.XML");
    }
  }
  
  
  public int[] interpolate(int unitID, int frame, int maxFrame)
  {
    int pastFrame = checkPreviousKeyFrame(frame);
    int nextFrame = checkNextKeyFrame(frame, maxFrame);
    //shouldn't ever be 0, because frame 1 is always a keyframe
    int[] temp = new int[2];
    if(pastFrame != 0 && nextFrame != 0)
    {
      int[] pastPos = readPosAtTime(unitID, pastFrame);
      int[] nextPos = readPosAtTime(unitID, nextFrame);
       
      int frameDifference = nextFrame - pastFrame;
      int xDifference = nextPos[0] - pastPos[0];
      int yDifference = nextPos[1] - pastPos[1];
      
      int xChangePerFrame = xDifference/frameDifference;
      int yChangePerFrame = yDifference/frameDifference;
      
      temp[0] = pastPos[0] + (xChangePerFrame * (frame - pastFrame));
      temp[1] = pastPos[1] + (yChangePerFrame * (frame - pastFrame));
    
      return temp;
    }
    return temp;
  }
    
  
  public int getPreviousKeyFrame(int frame)
  {
    for(int i = frame; i > 0; i--)
    { 
      xml = loadXML("xmldoc.XML");
      XML children[] = xml.getChildren("frame"+i);
      XML child = children[0].getChild("keyframe");
      //if not null, it's position is stored and therefore a keyframe
      if(child != null)
      {
         return i; 
      }
    }
    return 0;
  }
  
  //gets value of keyframe previously
  private int checkPreviousKeyFrame(int frame)
  {
    for(int i = frame; i > 0; i--)
    { 
      xml = loadXML("xmldoc.XML");
      XML children[] = xml.getChildren("frame"+i);
      XML child = children[0].getChild("keyframe");
      //if not null, it's position is stored and therefore a keyframe
      if(child != null)
      {
        return i;
      }
    }
      return 0;
  }
  
  public int checkNextKeyFrame(int frame, int maxFrame)
  {
     for(int i = frame; i < maxFrame; i++)
    { 
      xml = loadXML("xmldoc.XML");
      XML children[] = xml.getChildren("frame"+i);
      XML child = children[0].getChild("keyframe");
      //if not null, it's position is stored and therefore a keyframe
      if(child != null)
      {
        return i;
      }
    }
    return 0;
  }
  
  public void setFaction(int unitID, int factionValue)
  {
    xml = loadXML("xmldoc.XML");
    XML children[] = xml.getChildren("frame1");
    XML child = children[0].getChild("unit"+ unitID);
    XML faction = child.getChild("faction");
    if(faction == null)
    {
      child.addChild("faction");
      faction = child.getChild("faction");
    }
    
    faction.setInt("value", factionValue);
    saveXML(xml,"xmldoc.XML");  
  }
  
  public int getFaction(int unitID)
  {
    xml = loadXML("xmldoc.XML");
    XML children[] = xml.getChildren("frame1");
    XML child = children[0].getChild("unit" + unitID);
    XML faction = child.getChild("faction");
    if(faction == null)
    {
      child.addChild("faction");
      faction = child.getChild("faction");
      faction.setInt("value", 1);
      
    }
    return faction.getInt("value");
  }
  
  public void setName(int unitID, String name)
  {
    xml = loadXML("xmldoc.XML");
    XML children[] = xml.getChildren("frame1");
    XML child = children[0].getChild("unit" + unitID);
    XML unitName = child.getChild("name");
    
    if(unitName == null)
    {
      child.addChild("name");
      unitName = child.getChild("name");
    }
    
    unitName.setString("value",name);
    saveXML(xml,"xmldoc.XML");
  }
  
  public String getName(int unitID)
  {
    xml = loadXML("xmldoc.XML");
    XML children[] = xml.getChildren("frame1");
    XML child = children[0].getChild("unit" + unitID);
    XML unitName = child.getChild("name");
    if(unitName == null)
    {
      child.addChild("name");
      XML name = child.getChild("name");
      name.setString("value", "unit" +unitID);
      unitName = child.getChild("name");
    }
    String name = unitName.getString("value");
    
    
    return name;
  
  }
  
  public int getTroopCount(int unitID)
  {
    
    xml = loadXML("xmldoc.XML");
    XML children[] = xml.getChildren("frame1");
    XML child = children[0].getChild("unit" + unitID);
    XML size = child.getChild("troopCount");
   
    if(size == null)
    {
      child.addChild("troopCount");
      size = child.getChild("troopCount");
      size.setInt("size", 100);
      size = child.getChild("name");
    }
    int troopCount = size.getInt("size");
    
    return troopCount;
  
  }
  
  public void setTroopCount(int unitID, int size)
  {
     
    xml = loadXML("xmldoc.XML");
    XML children[] = xml.getChildren("frame1");
    XML child = children[0].getChild("unit" + unitID);
    XML unitName = child.getChild("troopCount");
    
    if(unitName == null)
    {
      child.addChild("troopCount");
      unitName = child.getChild("troopCount");
    }
    
    unitName.setInt("size",size);
    saveXML(xml,"xmldoc.XML");   
  }
}