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