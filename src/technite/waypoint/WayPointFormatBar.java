package technite.waypoint;

import java.awt.Component;

import javax.swing.JComponent;
 
import samples.lucy.util.*;


import com.luciad.lucy.ILcyLucyEnv;
import com.luciad.lucy.gui.TLcyActionBarManager;
import com.luciad.lucy.gui.TLcyActionBarUtil;
import com.luciad.lucy.gui.TLcyToolBar;
import com.luciad.lucy.gui.formatbar.ALcyFormatBar;
import com.luciad.lucy.map.action.lightspeed.TLcyLspCreateLayerAction;
import com.luciad.lucy.map.lightspeed.TLcyLspMapComponent;
import com.luciad.lucy.model.ALcyDefaultModelDescriptorFactory;
import com.luciad.lucy.util.properties.ALcyProperties;
import com.luciad.view.ILcdLayer;
import com.luciad.view.lightspeed.layer.ILspInteractivePaintableLayer;
 
class WayPointFormatBar extends ALcyFormatBar {
 
  /**
   * The actual Swing component representing the format bar
   */
  private final TLcyToolBar fToolBar = new TLcyToolBar();
  private final WayPointCreateControllerModel fControllerModel;
 
  WayPointFormatBar(TLcyLspMapComponent aLspMapComponent,
                    ALcyProperties aProperties,
                    String aShortPrefix,
                    ALcyDefaultModelDescriptorFactory aDefaultModelDescriptorFactory,
                    ILcyLucyEnv aLucyEnv) {
    putValue(ALcyFormatBar.NAME, "Way Points");
    putValue(ALcyFormatBar.SHORT_DESCRIPTION, "Create way points");
 
    //Allow TLcyActionBarUtil (and other add-ons) to contribute to our tool bar
    TLcyActionBarManager actionBarManager = aLucyEnv.getUserInterfaceManager().getActionBarManager();
    TLcyActionBarUtil.setupAsConfiguredActionBar(fToolBar,
                                                 WayPointFormatBarFactory.TOOLBAR_ID,
                                                 aLspMapComponent,
                                                 aProperties,
                                                 aShortPrefix,
                                                 (JComponent) aLspMapComponent.getComponent(),
                                                 actionBarManager);
 
    //Create and insert a button for the controller
    TLcyLspCreateLayerAction createLayerAction = new TLcyLspCreateLayerAction(aLucyEnv, aLspMapComponent);
    createLayerAction.setDefaultModelDescriptorFactory(aDefaultModelDescriptorFactory);
 
    fControllerModel = new WayPointCreateControllerModel(aLspMapComponent, createLayerAction);
    LayerUtil.insertCreateShapeActiveSettable(aProperties, aShortPrefix, aLucyEnv, aLspMapComponent, fControllerModel);
  }
 
  @Override
  protected void updateForLayer(ILcdLayer aPreviousLayer, ILcdLayer aLayer) {
    fControllerModel.setCurrentLayer((ILspInteractivePaintableLayer) aLayer);
  }
 
  @Override
  public boolean canSetLayer(ILcdLayer aLayer) {
    // TLcyLspSafeGuardFormatWrapper already checks the layer
    return true;
  }
 
  @Override
  public Component getComponent() {
    return fToolBar.getComponent();
  }
}
