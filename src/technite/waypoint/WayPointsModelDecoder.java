package technite.waypoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
 
import com.luciad.datamodel.ILcdDataObject;
import com.luciad.datamodel.TLcdCoreDataTypes;
import com.luciad.datamodel.TLcdDataModel;
import com.luciad.datamodel.TLcdDataModelBuilder;
import com.luciad.datamodel.TLcdDataType;
import com.luciad.datamodel.TLcdDataTypeBuilder;
import com.luciad.io.TLcdInputStreamFactory;
import com.luciad.model.ILcdDataModelDescriptor;
import com.luciad.model.ILcdModel;
import com.luciad.model.ILcdModelDecoder;
import com.luciad.model.ILcdModelReference;
import com.luciad.model.TLcd2DBoundsIndexedModel;
import com.luciad.model.TLcdDataModelDescriptor;
import com.luciad.reference.TLcdGeodeticReference;
import com.luciad.shape.TLcdShapeDataTypes;
import com.luciad.shape.shape3D.TLcdLonLatHeightPoint;
import com.luciad.util.TLcdHasGeometryAnnotation;
import com.luciad.util.service.LcdService;
 
@LcdService(service = ILcdModelDecoder.class)
public final class WayPointsModelDecoder implements ILcdModelDecoder {
  public static final TLcdDataModel DATA_MODEL;
  public static final TLcdDataType WAYPOINT_TYPE;
 
  static {
    //Create the TLcdDataModel for our waypoints
    TLcdDataModelBuilder dataModelBuilder =
        new TLcdDataModelBuilder("http://www.luciad.com/tutorial/waypoints");
 
    //Create a TLcdDataType for the geometry
    TLcdDataTypeBuilder pointTypeBuilder =
        dataModelBuilder.typeBuilder("PointType")
                        .superType(TLcdShapeDataTypes.SHAPE_TYPE)
                        .primitive(true)
                        .instanceClass(TLcdLonLatHeightPoint.class);
 
    TLcdDataTypeBuilder wayPointTypeBuilder =
        dataModelBuilder.typeBuilder("WayPointType");
    wayPointTypeBuilder.addProperty("identifier", TLcdCoreDataTypes.STRING_TYPE);
    wayPointTypeBuilder.addProperty("location", pointTypeBuilder);
 
    TLcdDataModel dataModel = dataModelBuilder.createDataModel();
    //Specify which property holds the geometry
    TLcdDataType wayPointType = dataModel.getDeclaredType("WayPointType");
    wayPointType.addAnnotation(new TLcdHasGeometryAnnotation(wayPointType.getProperty("location")));
 
    DATA_MODEL = dataModel;
    WAYPOINT_TYPE = wayPointType;
  }
 
  @Override
  public String getDisplayName() {
    return "Way Points";
  }
 
  @Override
  public boolean canDecodeSource(String aSourceName) {
    return aSourceName != null && aSourceName.endsWith(".cwp");
  }
 
  @Override
  public ILcdModel decode(String aSourceName) throws IOException {
	  
	  System.out.println("WayPointsModelDecoder - decode");
    //Sanity check to see whether we can decode the data
    if (!canDecodeSource(aSourceName)) {
      throw new IOException("Cannot decode " + aSourceName);
    }
 
    //Create an empty model
    TLcd2DBoundsIndexedModel model = createEmptyModel();
 
    //With the correct reference
    //The reference of the model expresses how the coordinates of the
    //shapes should be interpreted
    model.setModelReference(createModelReference());
 
    //And a ILcdDataModelDescriptor to expose the data model
    ILcdDataModelDescriptor modelDescriptor = createModelDescriptor(aSourceName);
    model.setModelDescriptor(modelDescriptor);
 
    //Read the file and create the domain objects (=ILcdDataObjects of the WayPointType)
    List<ILcdDataObject> wayPoints = createWayPoints(aSourceName);
 
    //Add them to the model
    model.addElements(new Vector<>(wayPoints), ILcdModel.NO_EVENT);
 
    //Return the created model
    return model;
  }
 
  private TLcd2DBoundsIndexedModel createEmptyModel() {
    return new TLcd2DBoundsIndexedModel();
  }
 
  private ILcdModelReference createModelReference() {
    return new TLcdGeodeticReference();//WGS-84 reference
  }
 
  private ILcdDataModelDescriptor createModelDescriptor(String aSourceName) {
	  System.out.println("WayPointsModelDecoder - createModelDescriptor");
    return new TLcdDataModelDescriptor(aSourceName,
                                       "CWP", //Typename: identifier of the format
                                       "Way Points",//Display name for the model
                                       DATA_MODEL,
                                       Collections.singleton(WAYPOINT_TYPE),//The type(s) of our model elements
                                       DATA_MODEL.getTypes());//All types used in the data model
  }
 
  private List<ILcdDataObject> createWayPoints(String aSourceName) throws IOException {
    List<ILcdDataObject> result = new ArrayList<>();
    System.out.println("WayPointsModelDecoder - createWayPoints");
    //Use TLcdInputStreamFactory to create an InputStream for aSourceName
    //This allows to read files on the class path, absolute file paths, files embedded in a jar, ...
    TLcdInputStreamFactory isf = new TLcdInputStreamFactory();
    try (InputStream is = isf.createInputStream(aSourceName);
         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
 
      String line;
      while ((line = reader.readLine()) != null) {
    	  System.out.println("WayPointsModelDecoder - createWayPoints - "+line);
        //Skip empty lines
        if (line.trim().isEmpty()) {
          continue;
        }
 
        // The first non-empty line contains the way point name.
        String wayPointIdentifier = line;
        //The next line is the coordinates line
        String coordinatesLine = reader.readLine();
 
        if (coordinatesLine == null) {
          throw new IOException("Unexpected end of file encountered.");
        }
 
        String[] coordinates = coordinatesLine.split("\\s+");//split the line based on whitespace
        if (coordinates.length != 3) {
          throw new IOException("Expected <lon lat height>, but found " + coordinatesLine);
        }
 
        //Parse the coordinates and the elevation
        try {
          double lon = Double.parseDouble(coordinates[0]);
          double lat = Double.parseDouble(coordinates[1]);
          double height = Double.parseDouble(coordinates[2]);
          if (lon < -180 || lon > 180 || lat < -90 || lat > 90) {
            throw new NumberFormatException("The longitude and latitude must be in the interval " +
                                            "[-180, 180] and [-90, 90], respectively");
          }
          if (height < 0) {
            throw new NumberFormatException("The altitude of the way point must be positive");
          }
          //Create the waypoint and set the properties
          ILcdDataObject wayPoint = WAYPOINT_TYPE.newInstance();
          System.out.println("WayPointsModelDecoder - createWayPoints - wayPointIdentifier - "+wayPointIdentifier);
          wayPoint.setValue("identifier", wayPointIdentifier);
          wayPoint.setValue("location", new TLcdLonLatHeightPoint(lon, lat, height));
 
          result.add(wayPoint);
        } catch (NumberFormatException ex) {
          throw new IOException(ex);
        }
      }
    }
    return result;
  }
}
