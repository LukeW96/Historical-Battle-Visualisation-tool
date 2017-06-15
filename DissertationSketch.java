import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import javax.swing.*; 
import controlP5.*; 
import java.util.LinkedList; 
import java.util.Random; 
import controlP5.*; 
import javax.swing.*; 
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.ParserConfigurationException; 
import javax.xml.transform.Transformer; 
import javax.xml.transform.TransformerException; 
import javax.xml.transform.TransformerFactory; 
import javax.xml.transform.dom.DOMSource; 
import javax.xml.transform.stream.StreamResult; 
import org.w3c.dom.Document; 
import javax.xml.parsers.SAXParserFactory; 
import javax.xml.parsers.SAXParser; 
import javax.xml.parsers.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class DissertationSketch extends PApplet {






int counter = 0;
LinkedList<PShape> unitList;
LinkedList<Shape> units;
Boolean playing;
Random rng;
int j;
float rotX, rotY;
int closestShape;
int colour1 = color(255,0,0);
int colour2 = color(0,0,255);
boolean highlighted;
boolean buttonClicked;

ControlP5 cp5;
GUI gui;
Time t;
int clickCounter;
public void setup()
{
  j = 2;
  
  rng = new Random();
  playing = false;
  unitList = new LinkedList<PShape>();
  units = new LinkedList<Shape>(); 

  t = new Time();
  t.createFrame(1);
  t.firstFrameKeyFrame();
  
  frameRate(2);
 
  //gui setup
  cp5 = new ControlP5(this);
  gui = new GUI();
  gui.setCP5(cp5);
  gui.setPApplet(this);
  gui.display();  

  for(int i = 1; i < gui.getFrameCount(); i++)
  {
    t.createFrame(i);
  }
  getExistingShapes();
}

public void draw()
{
  clear();
  background(170);
  
  //attempt at making play button work, unsuccessful
   if(playing)
    {
      j++;
      if(j == (gui.getFrameCount() - 1))
      {
        j = 2;
      }
      setFrame(j);
      delay(1000);
    }
    
  for(int i = 0; i < unitList.size(); i++)
  {
    
    PShape temp = unitList.get(i);
    
    int x = units.get(i).getXPos();
    int y = units.get(i).getYPos();
    int scale = units.get(i).getScale(i,gui.getCurrentFrame()); 
    if(units.get(i).getFaction() == 0)
      fill(colour1);
    else
      fill(colour2);
    
    
    if(units.get(i).getType(i).equals("Infantry"))
    {
      temp = createShape(RECT,x,y,scale,scale/2);  
    }
    else if(units.get(i).getType(i).equals("Archer"))
    {
      temp = createShape(ELLIPSE,x,y,30,30);
    }
    //else shape is cavalry
    else
    {
      temp = createShape(TRIANGLE, x,y,x-30,y-30,x+30,y-30);
    }
    
    if(i == closestShape && highlighted)
    {
      stroke(200);
      strokeWeight(5);
    }
    else
    {
      stroke(100);
      strokeWeight(1);
    }
    
    
    shape(temp);
      
    fill(0,0,0);
    
      text(units.get(i).getUnitName(i),x+5,y+10);
      text(units.get(i).getTroopCount(),x+10,y+40);
     // text(units.get(i).getTroopCount(i,gui.getCurrentFrame()),x+10,y+40); 
    
   
  }
}

//drag to move the unit
public void mouseDragged()
{
  if(highlighted && !units.isEmpty())
  {
    units.get(closestShape).setPos(closestShape, gui.getCurrentFrame(),mouseX,mouseY);  
  }
}

//click to highlight, delete, etc.
public void mouseClicked()
{
  if(mouseButton == LEFT)
  {
    print("clicked");
    highlighted ^= true;
    if(highlighted)
    {
      if(!getClosestShape())
      {
        highlighted = false;
      }
    }
    else
    {
      print("no unit nearby");
       if(!units.isEmpty())
       {
            units.get(closestShape).savePosAfterMove(gui.getCurrentFrame());     
       }
    }
  }
}


//stores closest shape in closestShape variable 
//if returns true it's in range, else it's not
public boolean getClosestShape()
{
  
  int x = mouseX;
  int y = mouseY;
  int closest = 0;
  int shapeClosest = 0;
  int difference = 0;
  for(int i = 0; i < unitList.size(); i++)
  {
    difference = abs(x - units.get(i).getXPos());
    difference += abs(y - units.get(i).getYPos());
   
    if(i == 0)
    {
      closest = difference;
      shapeClosest = 0;
    }
    if(closest > difference)
    {
      closest = difference;
      shapeClosest = i;
    }
  }
  
  closestShape = shapeClosest;
  if (closest > 20)
    return false;
  
  return true;
}  

//sets frame to the desired frame, calculates where the units should be
public void setFrame(int frame)
{
  units.clear();
  unitList.clear();
  
  //checks if keyframe, if so get the position
  if(t.checkKeyFrame(frame))
  {
    getExistingShapes();
  }
  
  //if there's a keyframe afterwards, interpolate position
  else if(t.checkNextKeyFrame(frame,gui.getFrameCount()) != 0)
  {
    interpolationPos(frame);  
  }
  
  //else onionskin position
  else
  {
    int previousFrame = t.getPreviousKeyFrame(frame);
    getExistingShapes(previousFrame);
  }
}

public void interpolationPos(int frame)
{
  int previousFrame = t.getPreviousKeyFrame(frame);
  int i = 0;
  if(previousFrame != 0)
  {
    while(t.checkUnitExists(i, previousFrame))
    {
      i++;
    }
    
    for(int counter = 0; counter < i; counter++)
    {
      Shape temp = new Shape(counter);
      units.add(temp);
      PShape p;
      
      int x = t.interpolate(counter,gui.getCurrentFrame(),50)[0];
      int y = t.interpolate(counter,gui.getCurrentFrame(),50)[1];
      temp.movingPos(x,y);
      p = createShape(RECT, x,y,10,10);
      unitList.add(p);
    }
  }
}

//adds new shape to the scene
public void addNewShape(int size, String type)
{
  Shape tempShape = new Shape(counter);
  tempShape.createUnit(counter,gui.getFrameCount(),rng.nextInt(300),rng.nextInt(300),type);
  PShape temp;
  
  if(type.equals("Infantry")){
   temp = createShape(BOX,1);
    }
  else if(type.equals("Cavalry"))
  {
   temp = createShape(SPHERE,1);
  }
  
  else if(type.equals("Archer"))
  {
    temp = createShape(TRIANGLE,1,1,1,1,1,1);
  }
  else
  {
    temp = createShape(BOX,1);
  }
  unitList.add(temp);
  units.add(tempShape);
  
  counter++;
} 

//gets all existing shapes for the frame
public void getExistingShapes()
{
  counter = 0;
  int i = 0;
  while(t.checkUnitExists(i,gui.getCurrentFrame()))
  {
    Shape temp = new Shape(i);
    units.add(temp);
    PShape p;
    temp.firstTimeSetup(i,gui.getCurrentFrame());
    p = createShape(RECT, 
                    temp.getXPos(),
                    temp.getYPos(),
                    10,10);
    unitList.add(p);
    
    i++;
    counter++;
  } 
}

//gets all existing shapes for the specified frame
public void getExistingShapes(int frame)
{
   int i = 0;
   counter = 0;
   while(t.checkUnitExists(i, frame))
   {
      Shape temp = new Shape(i);
      units.add(temp);
      PShape p;
      temp.firstTimeSetup(i,frame);
      p = createShape(RECT, 
                      temp.getXPos(),
                      temp.getYPos(),
                      10,10);
      unitList.add(p);
      
      i++;
      counter++;
    }
}



class GUI implements ControlListener
{
  ControlP5 cp5;
  PApplet p;
  int numberOfFrames;
  String[] items = {"Infantry", "Cavalry", "Archer"};

      
  public GUI()
  {
    numberOfFrames = 50;
  }

  //set max number of frames to desired value
  public void setFrameCount(int numberOfFrames)
  {
    this.numberOfFrames = numberOfFrames; 
  }
  
  //returns max frame value
  public int getFrameCount()
  {
    return numberOfFrames;
  }
  
  public void setCP5(ControlP5 cp5)
  {
    this.cp5 = cp5;
  }
  
  public void setPApplet(PApplet p)
  {
    this.p = p;
  }
  
  //displays all of the ui components, adds listeners to them also
  public void display()
  {
    
    cp5.addButton("Create new unit")
    .setPosition(440,290)
    .setSize(70,70)
    .setId(1)
    .addListener(this);
  
    cp5.addButton("Delete unit")
    .setPosition(520,290)
    .setSize(70,70)
    .setId(2)
    .addListener(this);
    
    cp5.addDropdownList("list")
    .setPosition(440,230)
    .setSize(150,50)
    .addItems(items)
    .setLabel("Preset unit types")
    .setId(3);
 
    cp5.addSlider("Frames")
    .setRange(1,numberOfFrames)
    .setSize(400,20)
    .setLabel("")
    .setPosition(50,380)
    .setNumberOfTickMarks(numberOfFrames)
    .setSliderMode(Slider.FLEXIBLE)
    .setId(4)
    .addListener(this);

    cp5.addButton("KeyFrame")
    .setSize(60,30)
    .setLabel("Make keyframe")
    .setPosition(470, 380)
    .setId(5)
    .addListener(this);
    
    cp5.addButton("Play")
    .setSize(30,20)
    .setPosition(10,380)
    .setLabel("Play")
    .setId(6)
    .addListener(this);
    
    cp5.addButton("ChangeUnitName")
    .setSize(90,20)
    .setLabel("Change unit name")
    .setPosition(430,70)
    .setId(7)
    .addListener(this);
    
    cp5.addButton("ChangeTroopCount")
    .setSize(90,20)
    .setLabel("Change troop count")
    .setPosition(430,100)
    .setId(8)
    .addListener(this);
    
    cp5.addButton("swapFaction")
    .setSize(90,20)
    .setLabel("Swap faction")
    .setPosition(430,130)
    .setId(9)
    .addListener(this);
  }

  public int getCurrentFrame()
  {
    return (int)cp5.getController("Frames").getValue();
  }
    
    
  public void controlEvent(ControlEvent theEvent)
  {
    switch(theEvent.getId())
      {
        case(1):
           int size = 1000;
           String unitType = items[(int)cp5.getController("list").getValue()];
           addNewShape(size,unitType);         
        break;    
        case(4):
        setFrame((int)cp5.getController("Frames").getValue());
        break;
        case(5):
          t.makeKeyFrame((int)cp5.getController("Frames").getValue());
        break;
        case(6):
          playing = true;
          break;
        case(7):
          String name = JOptionPane.showInputDialog("please enter new unit name");
          units.get(closestShape).setUnitName(closestShape,name);
          break;
        case(8):
          String stringValue = JOptionPane.showInputDialog("Please enter new troop count");
          int troopCount = Integer.parseInt(stringValue);
          units.get(closestShape).setTroopCount(closestShape, troopCount);
          break;
        case(9):
          units.get(closestShape).swapFaction(closestShape);
      }  
  }
}










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
  public void writePosition(int frame, String objName, int posX, int posY)
  {
    
  }
  
  
  
  public int readXPosition(int frame, String objName)
  {
    return 1;
  }
  
  public int readYPosition(int frame, String objName)
  {
    return 1;
  }  
}
class Shape {  
  int xPos;
  int yPos;
  int unitSize;
  int faction;
  int unitID;
  String unitName;
  String shapeType;
  PShape shape; 
  PApplet p;  
  Time time;


  public Shape(int unitValue)
  {
    faction = 0;
    xPos = 0;
    yPos = 0;
    unitSize = 1000;
    unitID = unitValue;
    unitName = "unit" + unitID;
    time = new Time();
  }
  
  public void createUnit(int unitID, int maxFrame, int x, int y, String unitType)
  {
    int troopSize = 1000;
    int tempfaction = 1;
    time.createUnit(unitID,maxFrame,x,y,unitType,troopSize,tempfaction);
  }

  public void setShapeType(int unitID,String type)
  {
    shapeType = type;
    time.writeUnitType(unitID, type);
  }
  
  public void setTroopCount(int unitID, int size)
  {
    unitSize = size;
    time.setTroopCount(unitID, size);
  }

  public String getType(int unitID)
  {
    return time.getUnitType(unitID);
  }

  public void createPos(int frame, int xPos, int yPos, int size)
  {
    time.writePos(unitID, frame, xPos, yPos, size);
  }
  
  public void storeShapeType(int unitID, String type)
  {
    shapeType = type;
    time.writeUnitType(unitID, type);
  }

  public int getXPos()
  {
    return xPos;
  }

  public int getYPos()
  {
    return yPos;
  }

  public void setPos(int unitValue, int frame, int x, int y)
  {
    xPos = x;
    yPos = y;
    updatePos(unitValue, frame, x, y);
  }
  
  public void movingPos(int x, int y)
  {
    xPos = x;
    yPos = y;
  }
  
  public void savePosAfterMove(int frame)
  {
    updatePos(unitID, frame, xPos,yPos);
  }


  public int getScale(int unitValue, int frame)
  {
    return unitSize /20;
  }

  private void updatePos(int unitValue, int frame, int x, int y)
  {
    xPos = x;
    yPos = y;
    time.updatePos(unitValue, frame, x, y);
  }

  public void setUnitName(int unitID, String name)
  {
    unitName = name;
    time.setName(unitID,name);
  }

  public String getUnitName(int unitID)
  {
    return time.getName(unitID);
  }


  public int getTroopCount()
  {
    return unitSize;
    //return time.readPosAtTime(unitValue, frame)[2];
  }

  public int getFaction()
  {
    return faction;
  }

  public void swapFaction(int unitID)
  {
    if (faction == 0)
      faction = 1;
    else
      faction = 0;
   time.setFaction(unitID, faction);
  }

  //initialises all of the necessary instance variables.
  public void firstTimeSetup(int unitID, int frame)
  {
    getPosFromXML(unitID, frame);
    getNameFromXML(unitID);
    getFactionFromXML(unitID);
    getShapeFromXML(unitID);
    getTroopCountFromXML(unitID);
  }

  private void getPosFromXML(int unitID, int frame)
  {
    int[] temp = new int[2];
    temp = time.readPosAtTime(unitID, frame);
    xPos = temp[0];
    yPos = temp[1];
  }
  
  private void getNameFromXML(int unitID)
  {
    unitName = time.getName(unitID);
  }
  
  private void getFactionFromXML(int unitID)
  {
    faction = time.getFaction(unitID);
  }
  
  private void getShapeFromXML(int unitID)
  {
    shapeType = time.getUnitType(unitID);
  }
 
  private void getTroopCountFromXML(int unitID)
  {
    unitSize = time.getTroopCount(unitID);
  }
}




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
  public void settings() {  size(600,400,P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "DissertationSketch" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
