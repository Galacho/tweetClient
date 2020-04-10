package technite.view;

import static technite.model.TwitterDataModel.*;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.luciad.datamodel.ILcdDataObject;
import com.luciad.gui.TLcdBoxIcon;
import com.luciad.gui.TLcdSymbol;
import com.luciad.gui.TLcdTextIcon;
import com.luciad.model.ILcdDataModelDescriptor;
import com.luciad.model.ILcdModel;
import com.luciad.model.ILcdModelDecoder;
import com.luciad.model.TLcdCompositeModelDecoder;
import com.luciad.model.transformation.TLcdTransformingModelFactory;
import com.luciad.model.transformation.clustering.TLcdClusteringTransformer;
import com.luciad.util.TLcdInterval;
import com.luciad.util.service.TLcdServiceLoader;
import com.luciad.view.lightspeed.TLspContext;
import com.luciad.view.lightspeed.layer.ALspSingleLayerFactory;
import com.luciad.view.lightspeed.layer.ILspLayer;
import com.luciad.view.lightspeed.layer.TLspLayer;
import com.luciad.view.lightspeed.layer.TLspPaintState;
import com.luciad.view.lightspeed.layer.shape.TLspShapeLayerBuilder;
import com.luciad.view.lightspeed.painter.label.style.TLspDataObjectLabelTextProviderStyle;
import com.luciad.view.lightspeed.style.ALspStyle;
import com.luciad.view.lightspeed.style.ILspWorldElevationStyle;
import com.luciad.view.lightspeed.style.TLspFillStyle;
import com.luciad.view.lightspeed.style.TLspIconStyle;
import com.luciad.view.lightspeed.style.TLspLineStyle;
import com.luciad.view.lightspeed.style.TLspTextStyle;
import com.luciad.view.lightspeed.style.styler.ALspStyleCollector;
import com.luciad.view.lightspeed.style.styler.ALspStyleTargetProvider;
import com.luciad.view.lightspeed.style.styler.ALspStyler;
import com.luciad.view.lightspeed.style.styler.ILspStyler;
import com.luciad.view.lightspeed.style.styler.TLspLabelStyler;
import com.luciad.view.lightspeed.style.styler.TLspStyler;

import samples.common.MapColors;
import samples.common.UIColors;
import technite.model.TwitterClusterAwareStylerWrapper;
import technite.model.TwitterClusterIgnoringLabelStylerWrapper;
import technite.model.Tweet;
import technite.model.TwitterClassifier;
import technite.model.TwitterDataModel;

public class TwitterLayerFactory extends ALspSingleLayerFactory {
	
	
	  // Scale at which labels become visible
	  private static final double SCALE_THRESHOLD_LABELS = 3e-3;

	  @Override
	  public ILspLayer createLayer(ILcdModel aModel) {
			int CLUSTER_SIZE = 100;
			int MIN_CLUSTER_COUNT = 3;
		  
	    TLspIconStyle element = TLspIconStyle.newBuilder()
	                                         .icon(MapColors.createIcon(false))
	                                         .elevationMode(ILspWorldElevationStyle.ElevationMode.ON_TERRAIN)
	                                         .build();
	    TLspIconStyle selectedElement = TLspIconStyle.newBuilder()
	                                                 .icon(MapColors.createIcon(true))
	                                                 .elevationMode(ILspWorldElevationStyle.ElevationMode.ON_TERRAIN)
	                                                 .build();

	    TLspStyler regularStyler = new TLspStyler(element);
	    TLspStyler selectionStyler = new TLspStyler(selectedElement);

	    TLspTextStyle fElementTextStyle = TLspTextStyle.newBuilder()
	                                                   .textColor(UIColors.mid(MapColors.ICON_OUTLINE, Color.WHITE, 0.5))
	                                                   .haloThickness(0)
	                                                   .build();
	    TLspLabelStyler labelStyler = TLspLabelStyler.newBuilder().styles(fElementTextStyle).build();

	    
/////////// ====================================================================
		
		//When zoomed in, cluster the events per country and avoid grouping events
		//happening in different countries in different clusters.
		TLcdClusteringTransformer zoomedInClusteringTransformer =
		    TLcdClusteringTransformer.newBuilder()
		                             .classifier(new TwitterClassifier())
		                             .defaultParameters()
		                             .clusterSize(CLUSTER_SIZE)
		                             .minimumPoints(MIN_CLUSTER_COUNT)
		                             .build()
		                             .build();
		//When zoomed out, all the events can be clustered together.
		//Otherwise, we would end up with overlapping clusters as the countries become rather small
		TLcdClusteringTransformer zoomedOutClusteringTransformer =
		    TLcdClusteringTransformer.newBuilder()
		                             .defaultParameters()
		                             .clusterSize(CLUSTER_SIZE)
		                             .minimumPoints(MIN_CLUSTER_COUNT)
		                             .build()
		                             .build();
		 
		//Switching between the two clustering approaches should happen at a scale 1 : 25 000 000
		double scale = 1.0 / 25000000.0;
		TLcdClusteringTransformer scaleDependentClusteringTransformer =
		    TLcdClusteringTransformer.createMapScaleDependent(
		        new double[]{scale},
		        new TLcdClusteringTransformer[]{zoomedInClusteringTransformer, zoomedOutClusteringTransformer}
		    );
		
		ILcdModel transformingModel = TLcdTransformingModelFactory.createTransformingModel(aModel, scaleDependentClusteringTransformer);
		
		/////////// ====================================================================
	    
	    return TLspShapeLayerBuilder.newBuilder()
	                                .model(transformingModel)
	                                .label("Clustered events")
	                                .bodyStyler(TLspPaintState.REGULAR, new TwitterClusterAwareStylerWrapper(regularStyler, TLspPaintState.REGULAR))
	                                .bodyStyler(TLspPaintState.SELECTED, new TwitterClusterAwareStylerWrapper(selectionStyler, TLspPaintState.SELECTED))
	                                .labelStyler(TLspPaintState.REGULAR, new TwitterClusterIgnoringLabelStylerWrapper(labelStyler))
	                                .labelStyler(TLspPaintState.SELECTED, new TwitterClusterIgnoringLabelStylerWrapper(labelStyler))
	                                .labelScaleRange(new TLcdInterval(SCALE_THRESHOLD_LABELS, Double.MAX_VALUE))
	                                .bodyEditable(true)
	                                .build();
	    
	    
	  }

	  @Override
	  public boolean canCreateLayers(ILcdModel aModel) {
	    return true;
	  }
	
	
	//@Override
	public ILspLayer createLayer2(ILcdModel aModel) {

		Map<ALspStyleTargetProvider, List<ALspStyle>> styles = new HashMap<>();
		
		int CLUSTER_SIZE = 100;
		int MIN_CLUSTER_COUNT = 3;

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
	          
	        	aObjects.stream().forEach(obj->{
	        		if (obj instanceof ILcdDataObject && obj instanceof Tweet) {
		    			ILcdDataObject t = (ILcdDataObject) obj;
		    			System.out.println( t.getValue(TWITTER_QTD).toString());
		    			
						Color c;
						Long qtd = (Long)t.getValue(TWITTER_QTD);
						
						Integer tam = (int) (qtd * 0.02);
						
						
						
						if (tam > 80) {
							tam = 80;
						}
						
						if (tam< 0) {
							tam = 1;
						}
						
						if (tam < 10) {
							c = Color.CYAN;
						}else if(tam < 20 ) {
							c= Color.YELLOW;
						}else if (tam < 40) {
							c=Color.ORANGE;
						}else if(tam < 60) {
							c=Color.PINK;
						}else {
							c=Color.RED;
						}
						
						TLcdSymbol sybolIcon = new TLcdSymbol(TLcdSymbol.CIRCLE, tam, Color.BLACK, c);
						
						List tit = new ArrayList<String>();
						tit.add(""+qtd);
						
						/* TLcdTextIcon(java.util.List aLines, 
						             	java.awt.Font aFont, 
										java.awt.Color aForeground, 
										int aVerticalSpacing, 
										com.luciad.util.ELcdHorizontalAlignment aHorizontalAlignment, 
										boolean aAntiAliased, 
										int aWidth, 
										int aHeight);
						  */
						
						
						//TLcdTextIcon ico = new TLcdTextIcon( tit, Font.CENTER_BASELINE, Color.YELLOW, 2  ,ELcdHorizontalAlignment.CENTER, false, 2, 2);
						TLcdTextIcon ico = TLcdTextIcon.newBuilder()
								.line(""+qtd)
								.font(new Font("Verdana", Font.PLAIN, 12))
								.foreground(Color.BLACK)
								.build();
						
						
						
						TLcdBoxIcon ico2 = TLcdBoxIcon.newBuilder()
											.fillColor(Color.YELLOW)
											.frameColor(Color.YELLOW)
											.filled(true)
											.icon(sybolIcon)
											.build();
						
						
						
						
						
						TLspIconStyle iconStyle = TLspIconStyle.newBuilder()
																	//.icon(sybolIcon)
								                                    .icon(ico)
								                                    .zOrder(3)
						                                            .build();
						
						
						
						aStyleCollector.object(t).style(iconStyle).submit();
		    			
		    			
		    		}
	        		
	        	});
	        	

	        		
	        		
	        	
	    		
//	        	Enumeration<Tweet> tweets = (Enumeration<Tweet>) aContext.getModel().elements();
//	    		
//	        	while(tweets.hasMoreElements()) {
//	        		try {
//						Tweet t = tweets.nextElement();
//						
//						Color c;
//						Long qtd = t.getQtd();
//						
//						Integer tam = (int) (qtd * 0.02);
//						
//						
//						
//						if (tam > 80) {
//							tam = 80;
//						}
//						
//						if (tam< 0) {
//							tam = 1;
//						}
//						
//						if (tam < 10) {
//							c = Color.CYAN;
//						}else if(tam < 20 ) {
//							c= Color.YELLOW;
//						}else if (tam < 40) {
//							c=Color.ORANGE;
//						}else if(tam < 60) {
//							c=Color.PINK;
//						}else {
//							c=Color.RED;
//						}
//						
//						TLcdSymbol sybolIcon = new TLcdSymbol(TLcdSymbol.FILLED_CIRCLE, tam, Color.WHITE, c);
//						
//						
//						TLspIconStyle iconStyle = TLspIconStyle.newBuilder()
//						                                            .icon(sybolIcon)
//						                                            .zOrder(2)
//						                                            .build();
//						
//						aStyleCollector.object(t).style(iconStyle).submit();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//	        		
//	        	}

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
				try {
					System.out.println(aO.getClass().getName());
					Tweet t = (Tweet) aO;
					aList.add(t.getPoint(t.getPointCount() - 1));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
				
				
//				aCollection.stream().forEach(obj->{
//					System.out.println(obj);
//	        		if (obj instanceof ILcdDataObject && obj instanceof Tweet) {
//	        			System.out.println("Estou aqui");
//	        		}
//				});
				
				aCollector.objects(aCollection).geometry(asPoint).style(iconStyle).submit();
			}
		};
		
//		TLcdClusteringTransformer transformer =
//			    TLcdClusteringTransformer.newBuilder()
//			                             .defaultParameters()
//			                             .clusterSize(CLUSTER_SIZE)
//			                             .minimumPoints(MIN_CLUSTER_COUNT)
//			                             .shapeProvider(new TweetClusterShapeProvider())
//			                             .build()
//			                             .build();
//		
//		ILcdModel transformingModel = TLcdTransformingModelFactory.createTransformingModel(aModel, transformer);

		
		
		
		/////////// ====================================================================
		
		//When zoomed in, cluster the events per country and avoid grouping events
		//happening in different countries in different clusters.
		TLcdClusteringTransformer zoomedInClusteringTransformer =
		    TLcdClusteringTransformer.newBuilder()
		                             .classifier(new TwitterClassifier())
		                             .defaultParameters()
		                             .clusterSize(CLUSTER_SIZE)
		                             .minimumPoints(MIN_CLUSTER_COUNT)
		                             .build()
		                             .build();
		//When zoomed out, all the events can be clustered together.
		//Otherwise, we would end up with overlapping clusters as the countries become rather small
		TLcdClusteringTransformer zoomedOutClusteringTransformer =
		    TLcdClusteringTransformer.newBuilder()
		                             .defaultParameters()
		                             .clusterSize(CLUSTER_SIZE)
		                             .minimumPoints(MIN_CLUSTER_COUNT)
		                             .build()
		                             .build();
		 
		//Switching between the two clustering approaches should happen at a scale 1 : 25 000 000
		double scale = 1.0 / 25000000.0;
		TLcdClusteringTransformer scaleDependentClusteringTransformer =
		    TLcdClusteringTransformer.createMapScaleDependent(
		        new double[]{scale},
		        new TLcdClusteringTransformer[]{zoomedInClusteringTransformer, zoomedOutClusteringTransformer}
		    );
		
		ILcdModel transformingModel = TLcdTransformingModelFactory.createTransformingModel(aModel, scaleDependentClusteringTransformer);
		
		/////////// ====================================================================
		
		
		
		
		return TLspShapeLayerBuilder.newBuilder().model(transformingModel).bodyStyler(TLspPaintState.REGULAR, styler)
				.bodyStyler(TLspPaintState.SELECTED, selected).build();
		
//		return TLspShapeLayerBuilder.newBuilder().model(aModel).bodyStyler(TLspPaintState.REGULAR, styler)
//				.bodyStyler(TLspPaintState.SELECTED, selected).build();
	}

	//@Override
	public boolean canCreateLayers2(ILcdModel aModel) {
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
