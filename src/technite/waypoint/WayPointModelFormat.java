package technite.waypoint;

import java.util.Collections;
 
import com.luciad.lucy.ILcyLucyEnv;
import com.luciad.lucy.format.ALcyFileFormat;
import com.luciad.lucy.gui.customizer.ILcyCustomizerPanel;
import com.luciad.lucy.gui.customizer.ILcyCustomizerPanelFactory;
import com.luciad.lucy.map.ILcyGXYLayerTypeProvider;
import com.luciad.lucy.model.ALcyDefaultModelDescriptorFactory;
import com.luciad.lucy.model.ILcyModelContentType;
import com.luciad.lucy.model.ILcyModelContentTypeProvider;
import com.luciad.lucy.util.properties.ALcyProperties;
import com.luciad.model.ILcdModel;
import com.luciad.model.ILcdModelDecoder;
import com.luciad.model.ILcdModelDescriptor;
import com.luciad.model.ILcdModelEncoder;
import com.luciad.model.ILcdModelFactory;
import com.luciad.model.ILcdModelReference;
import com.luciad.model.TLcd2DBoundsIndexedModel;
import com.luciad.model.TLcdDataModelDescriptor;
import technite.waypoint.WayPointsModelDecoder;
import technite.waypoint.WayPointsModelEncoder;
import com.luciad.reference.TLcdGeodeticReference;
import com.luciad.view.gxy.ILcdGXYLayerFactory;
 
class WayPointModelFormat extends ALcyFileFormat {
  WayPointModelFormat(ILcyLucyEnv aLucyEnv, String aLongPrefix, String aShortPrefix, ALcyProperties aProperties) {
    super(aLucyEnv, aLongPrefix, aShortPrefix, aProperties);
  }
 
  @Override
  protected ILcdModelDecoder[] createModelDecoders() {
    return new ILcdModelDecoder[]{new WayPointsModelDecoder()};
  }
 
  @Override
  protected ILcyModelContentTypeProvider createModelContentTypeProvider() {
    //All our models only contain point data, so we can return a fixed type
    //No need to check the contents of the model
    return aModel -> ILcyModelContentType.POINT;
  }
 
  @Override
  protected ILcyGXYLayerTypeProvider createGXYLayerTypeProvider() {
    return null;
  }
 
  @Override
  protected ILcdGXYLayerFactory createGXYLayerFactory() {
    return null;
  }
 
  @Override
  public boolean isModelOfFormat(ILcdModel aModel) {
    //All the waypoint models created by our model decoder have CWP as type name
    //We assume here that this typename is unique over all supported formats
    return "CWP".equals(aModel.getModelDescriptor().getTypeName());
  }
 
  @Override
  protected ILcdModelEncoder[] createModelEncoders() {
    return new ILcdModelEncoder[]{new WayPointsModelEncoder()};
  }
 
  @Override
  protected ILcyCustomizerPanelFactory[] createDomainObjectCustomizerPanelFactories() {
    return new ILcyCustomizerPanelFactory[]{
        new ILcyCustomizerPanelFactory() {
          @Override
          public boolean canCreateCustomizerPanel(Object aObject) {
            //The TLcyLspSafeGuardFormatWrapper takes care of this
            return true;
          }
 
          @Override
          public ILcyCustomizerPanel createCustomizerPanel(Object aObject) {
            return new WayPointCustomizerPanel(getLucyEnv());
          }
        }
    };
  }
 
  @Override
  protected ALcyDefaultModelDescriptorFactory[] createDefaultModelDescriptorFactories() {
    return new ALcyDefaultModelDescriptorFactory[]{
        new ALcyDefaultModelDescriptorFactory() {
          @Override
          public ILcdModelDescriptor createDefaultModelDescriptor() {
            //Return the same model descriptor as the model decoder is creating
            return new TLcdDataModelDescriptor(null,
                                               "CWP",
                                               "Way Points",
                                               WayPointsModelDecoder.DATA_MODEL,
                                               Collections.singleton(WayPointsModelDecoder.WAYPOINT_TYPE),
                                               WayPointsModelDecoder.DATA_MODEL.getTypes());
          }
        }
    };
  }
 
  @Override
  protected ILcdModelFactory createModelFactory() {
    return new ILcdModelFactory() {
      @Override
      public ILcdModel createModel(ILcdModelDescriptor aModelDescriptor, ILcdModelReference aModelReference) throws IllegalArgumentException {
        //First check whether the model descriptor is one we recognize
        //The TLcySafeGuardFormatWrapper does not help us, because that wrapper can only check whether
        //a model is valid for a format.
        //It has no knowledge of model descriptors
        if (!"CWP".equals(aModelDescriptor.getTypeName())) {
          throw new IllegalArgumentException("Cannot create model for model descriptor [" + aModelDescriptor + "]");
        }
        //Create a model the same was as done in the WayPointsModelDecoder
        return new TLcd2DBoundsIndexedModel(new TLcdGeodeticReference(), aModelDescriptor);
      }
    };
  }
 
 
}