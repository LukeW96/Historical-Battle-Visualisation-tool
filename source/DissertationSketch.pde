import javax.swing.*;
import controlP5.*;
import java.util.LinkedList;
import java.util.Random;

int counter = 0;
LinkedList<PShape> unitList;
LinkedList<Shape> units;
Boolean playing;
Random rng;
int j;
float rotX, rotY;
int closestShape;
color colour1 = color(255,0,0);
color colour2 = color(0,0,255);
boolean highlighted;
boolean buttonClicked;

ControlP5 cp5;
GUI gui;
Time t;
int clickCounter;
void setup()
{
  j = 2;
  size(600,400,P3D);
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

void draw()
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
void mouseDragged()
{
  if(highlighted && !units.isEmpty())
  {
    units.get(closestShape).setPos(closestShape, gui.getCurrentFrame(),mouseX,mouseY);  
  }
}

//click to highlight, delete, etc.
void mouseClicked()
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
boolean getClosestShape()
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
void setFrame(int frame)
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

void interpolationPos(int frame)
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
void addNewShape(int size, String type)
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
void getExistingShapes()
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
void getExistingShapes(int frame)
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