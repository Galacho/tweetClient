package technite.view;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.luciad.gui.TLcdSymbol;
import com.luciad.model.ILcdDataModelDescriptor;
import com.luciad.model.ILcdModel;
import com.luciad.model.ILcdModelDecoder;
import com.luciad.model.TLcdCompositeModelDecoder;
import com.luciad.model.TLcdDataObjectIndexedAnd2DBoundsIndexedModel;
import com.luciad.model.transformation.TLcdTransformingModelFactory;
import com.luciad.model.transformation.clustering.TLcdClusteringTransformer;
import com.luciad.util.service.TLcdServiceLoader;
import com.luciad.view.lightspeed.TLspContext;
import com.luciad.view.lightspeed.layer.ALspSingleLayerFactory;
import com.luciad.view.lightspeed.layer.ILspLayer;
import com.luciad.view.lightspeed.layer.TLspLayer;
import com.luciad.view.lightspeed.layer.TLspPaintState;
import com.luciad.view.lightspeed.layer.shape.TLspShapeLayerBuilder;
import com.luciad.view.lightspeed.painter.label.style.TLspDataObjectLabelTextProviderStyle;
import com.luciad.view.lightspeed.style.ALspStyle;
import com.luciad.view.lightspeed.style.TLspFillStyle;
import com.luciad.view.lightspeed.style.TLspIconStyle;
import com.luciad.view.lightspeed.style.TLspLineStyle;
import com.luciad.view.lightspeed.style.TLspTextStyle;
import com.luciad.view.lightspeed.style.styler.ALspStyleCollector;
import com.luciad.view.lightspeed.style.styler.ALspStyleTargetProvider;
import com.luciad.view.lightspeed.style.styler.ALspStyler;
import com.luciad.view.lightspeed.style.styler.ILspStyler;
import com.luciad.view.lightspeed.style.styler.TLspStyler;
import com.sun.javafx.binding.SelectBinding.AsObject;

import technite.model.Tweet;
import technite.model.TwitterDataModel;

public class TwitterLayerFactory extends ALspSingleLayerFactory {
	
	@Override
	public ILspLayer createLayer(ILcdModel aModel) {

		Map<ALspStyleTargetProvider, List<ALspStyle>> styles = new HashMap<>();

//		Enumeration<Tweet> tweets = (Enumeration<Tweet>) aModel.elements();
//		
//		while(tweets.hasMoreElements()) {
//			Tweet t = tweets.nextElement();
//			System.out.print(""+t.getQtd());
//			System.out.print("  -  "+t.getText());
//			
//		}
		
		
		// We create a style to paint the latest vehicle positions as icons
		TLspIconStyle iconStyle = TLspIconStyle.newBuilder()
				.icon(new TLcdSymbol(TLcdSymbol.FILLED_CIRCLE, 10, Color.BLACK, new Color(253, 253, 170)))
				.zOrder(2)
				.build();
		
		
		
		///// novo
		
	    ILspStyler styler = new ALspStyler() {
	        @Override
	        public void style(Collection<?> aObjects, ALspStyleCollector aStyleCollector, TLspContext aContext) {
	          
	        	
	    		
	        	Enumeration<Tweet> tweets = (Enumeration<Tweet>) aContext.getModel().elements();
	    		
	        	while(tweets.hasMoreElements()) {
	        		Tweet t = tweets.nextElement();
	        		
	        		Color c;
	        		Long qtd = t.getQtd();
	        		
	        		Integer tam = (int) (qtd * 0.02);
	        		
	        		
	        		
	        		if (tam > 50) {
	        			tam = 50;
	        		}
	        		
	        		if (tam< 0) {
	        			tam = 1;
	        		}
	        		
	        		if (tam < 10) {
	        			c = Color.YELLOW;
	        		}else if(tam < 20 ) {
	        			c= Color.ORANGE;
	        		}else if (tam < 30) {
	        			c=Color.CYAN;
	        		}else if(tam < 40) {
	        			c=Color.PINK;
	        		}else {
	        			c=Color.RED;
	        		}
	        		
	        		TLcdSymbol sybolIcon = new TLcdSymbol(TLcdSymbol.FILLED_CIRCLE, tam, Color.WHITE, c);
	        		
	        	    TLspIconStyle iconStyle = TLspIconStyle.newBuilder()
	        	                                                .icon(sybolIcon)
	        	                                                .build();
	        	    aStyleCollector.object(t).style(iconStyle).submit();
	        		
	        	}

	        }
	      };
	    
	    //////////////
	    
	   
	    
	    

		// Two line styles to paint the path of a given track.
		///TLspLineStyle lineStyle = TLspLineStyle.newBuilder().color(new Color(235, 51, 0)).width(5).zOrder(1).build();
		//TLspLineStyle lineStyle2 = TLspLineStyle.newBuilder().color(Color.BLACK).width(7).zOrder(0).build();

		// Each vehicle is modelled as a polyline, that contains its path.
		// This StyleTargetProvider extracts the last point of a tweet, which is its
		// latest position.
		// This way, we can style this point as an Icon.
		
		ALspStyleTargetProvider asPoint = new ALspStyleTargetProvider() {
			@Override
			public void getStyleTargetsSFCT(Object aO, TLspContext aContext, List<Object> aList) {
				Tweet t = (Tweet) aO;
				aList.add(t.getPoint(t.getPointCount() - 1));
			}
		};

		// Regular styler: show tweets as points.
		//TLspStyler regular = new TLspStyler(asPoint, iconStyle);

		// Selected styler: show tweet as point AND as a line, showing their entire
		// path.
		ILspStyler selected = new ALspStyler() {
			@Override
			public void style(Collection<?> aCollection, ALspStyleCollector aCollector, TLspContext aContext) {
				//aCollector.objects(aCollection).styles(lineStyle, lineStyle2).submit();
				
				aCollector.objects(aCollection).geometry(asPoint).style(iconStyle).submit();
			}
		};
		
//		TLcdClusteringTransformer transformer =
//			    TLcdClusteringTransformer.newBuilder()
//			                             .defaultParameters()
//			                             .clusterSize(10000)
//			                             .minimumPoints(2)
//			                             .shapeProvider(new TweetClusterShapeProvider())
//			                             .build()
//			                             .build();
//		
//		ILcdModel transformingModel = TLcdTransformingModelFactory.createTransformingModel(aModel, transformer);

//		return TLspShapeLayerBuilder.newBuilder().model(aModel).bodyStyler(TLspPaintState.REGULAR, styler)
//				.bodyStyler(TLspPaintState.SELECTED, selected).build();
		
		return TLspShapeLayerBuilder.newBuilder().model(aModel).bodyStyler(TLspPaintState.REGULAR, styler)
				.bodyStyler(TLspPaintState.SELECTED, selected).build();
	}

	@Override
	public boolean canCreateLayers(ILcdModel aModel) {
		return aModel.getModelDescriptor() instanceof ILcdDataModelDescriptor
				&& ((ILcdDataModelDescriptor) aModel.getModelDescriptor())
						.getDataModel() == TwitterDataModel.DATA_MODEL;
	}

	private ILcdModel decodeSHPModel(String aSourceName) {
		try {
			TLcdCompositeModelDecoder modelDecoder = new TLcdCompositeModelDecoder(
					TLcdServiceLoader.getInstance(ILcdModelDecoder.class));
			return modelDecoder.decode(aSourceName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public TLspLayer addWorldLayer() {

		ALspStyle labelContentsStyle = TLspDataObjectLabelTextProviderStyle.newBuilder().expressions("COUNTRY").build();

		ALspStyle textStyle = TLspTextStyle.newBuilder().font(Font.decode("SansSerif-PLAIN-12")).textColor(Color.BLACK)
				.haloColor(Color.WHITE).build();

		TLspLayer layer = TLspShapeLayerBuilder.newBuilder().model(decodeSHPModel("Data/Shp/World/world.shp"))
				.bodyStyles(TLspPaintState.REGULAR, createWorldLayerBodyStyles())
				.labelStyles(TLspPaintState.REGULAR, labelContentsStyle, textStyle).selectable(false).build();

		return layer;
	}

	private ALspStyle[] createWorldLayerBodyStyles() {
		TLspFillStyle fillStyle = TLspFillStyle.newBuilder().color(new Color(192, 192, 192, 128)).build();
		TLspLineStyle lineStyle = TLspLineStyle.newBuilder().color(Color.white).build();
		return new ALspStyle[] { lineStyle, fillStyle };
	}

}
