import controlP5.*;
import javax.swing.*;

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
  void setFrameCount(int numberOfFrames)
  {
    this.numberOfFrames = numberOfFrames; 
  }
  
  //returns max frame value
  int getFrameCount()
  {
    return numberOfFrames;
  }
  
  void setCP5(ControlP5 cp5)
  {
    this.cp5 = cp5;
  }
  
  void setPApplet(PApplet p)
  {
    this.p = p;
  }
  
  //displays all of the ui components, adds listeners to them also
  void display()
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

  int getCurrentFrame()
  {
    return (int)cp5.getController("Frames").getValue();
  }
    
    
  void controlEvent(ControlEvent theEvent)
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