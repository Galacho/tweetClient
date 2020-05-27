package technite.waypoint;

import com.luciad.lucy.ILcyLucyEnv;
import com.luciad.lucy.datatransfer.ALcyLayerSelectionTransferHandler;
import com.luciad.lucy.format.lightspeed.ALcyLspStyleFormat;
import com.luciad.lucy.gui.formatbar.ALcyFormatBarFactory;
import com.luciad.lucy.util.properties.ALcyProperties;
import com.luciad.model.ILcdModel;
import com.luciad.util.ILcdFilter;
import com.luciad.view.lightspeed.layer.ALspSingleLayerFactory;
import com.luciad.view.lightspeed.layer.ILspLayer;
import com.luciad.view.lightspeed.layer.ILspLayerFactory;
import com.luciad.view.lightspeed.layer.TLspPaintState;
import com.luciad.view.lightspeed.layer.shape.TLspShapeLayerBuilder;
import com.luciad.view.lightspeed.painter.label.style.TLspDataObjectLabelTextProviderStyle;
import com.luciad.view.lightspeed.style.TLspTextStyle;
import com.luciad.view.lightspeed.style.styler.TLspCustomizableStyler;

public class WayPointFormat extends ALcyLspStyleFormat {
	  public WayPointFormat(ILcyLucyEnv aLucyEnv, String aLongPrefix, String aShortPrefix, ALcyProperties aPreferences, ILcdFilter<ILcdModel> aModelFilter) {
	    super(aLucyEnv, aLongPrefix, aShortPrefix, aPreferences, aModelFilter);
	    System.out.println("WayPointFormat - construtor");
	  }
	 
	  @Override
	  protected ILspLayerFactory createLayerFactoryImpl() {
		  System.out.println("WayPointFormat - createLayerFactoryImpl");
	    return new ALspSingleLayerFactory() {
	      @Override
	      public ILspLayer createLayer(ILcdModel aModel) {
	        //Ensure that the layer is editable
	        //Also make sure the layer has labels
	    	  
	    	  System.out.println("WayPointFormat - createLayerFactoryImpl");
	        return TLspShapeLayerBuilder.newBuilder()
	                                    .model(aModel)
	                                    .bodyEditable(true)
	                                    .labelStyler(TLspPaintState.REGULAR, createLabelStyler())
	                                    .build();
	      }
	 
	      private TLspCustomizableStyler createLabelStyler() {
	        TLspDataObjectLabelTextProviderStyle labelContentsStyle =
	            TLspDataObjectLabelTextProviderStyle.newBuilder()
	                                                .expressions("identifier")
	                                                .build();
	        System.out.println("WayPointFormat - createLabelStyler");
	        return new TLspCustomizableStyler(TLspTextStyle.newBuilder().build(),
	                                          labelContentsStyle);
	      }
	 
	      @Override
	      public boolean canCreateLayers(ILcdModel aModel) {
	        //The TLcyLspSafeGuardFormatWrapper only passes waypoint models
	        //to this factory, so no need to check anything here
	    	  System.out.println("WayPointFormat - canCreateLayers");
	        return true;
	      }
	    };
	  }
	}
