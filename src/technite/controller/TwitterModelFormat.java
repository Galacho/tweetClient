package technite.controller;

import java.io.IOException;

import com.luciad.gui.swing.TLcdOverlayLayout.Location;
import com.luciad.lucy.ILcyLucyEnv;
import com.luciad.lucy.format.ALcyGeneralFormat;
import com.luciad.lucy.map.ILcyGXYLayerTypeProvider;
import com.luciad.lucy.map.lightspeed.ILcyLspMapComponent;
import com.luciad.lucy.model.ALcyDataSourceHandler;
import com.luciad.lucy.model.ALcyFileTypeDescriptor;
import com.luciad.lucy.model.ILcyModelContentType;
import com.luciad.lucy.model.ILcyModelContentTypeProvider;
import com.luciad.lucy.util.properties.ALcyProperties;
import com.luciad.model.ILcdDataModelDescriptor;
import com.luciad.model.ILcdModel;
import com.luciad.model.ILcdModelDecoder;
import com.luciad.view.gxy.ILcdGXYLayerFactory;
import com.luciad.view.lightspeed.ILspAWTView;
import com.luciad.view.lightspeed.ILspView;
import com.luciad.view.lightspeed.layer.ILspLayer;
import com.luciad.view.lightspeed.swing.TLspBalloonManager;

import technite.model.TwitterAPIModelDecoder;
import technite.model.TwitterDataModel;
import technite.view.BalloonContentProvider;
import technite.view.BalloonViewSelectionListener;
import technite.view.TwitterLayerFactory;

public class TwitterModelFormat extends ALcyGeneralFormat {
	public TwitterModelFormat(ILcyLucyEnv aLucyEnv, String aLongPrefix, String aShortPrefix,
			ALcyProperties aProperties) {
		super(aLucyEnv, aLongPrefix, aShortPrefix, aProperties);
	}

	@Override
	public boolean isModelOfFormat(ILcdModel aModel) {
		return aModel.getModelDescriptor() instanceof ILcdDataModelDescriptor
				&& ((ILcdDataModelDescriptor) aModel.getModelDescriptor())
						.getDataModel() == TwitterDataModel.DATA_MODEL;
	}

	@Override
	protected ILcyModelContentTypeProvider createModelContentTypeProvider() {
		return aModel -> ILcyModelContentType.POINT;
	}

	/**
	 * We don't provide a model decoder. Because we are connecting to a URL, and not
	 * opening a file, we use a data source handler instead.
	 *
	 * @return
	 */
	@Override
	protected ILcdModelDecoder[] createModelDecoders() {
		return new ILcdModelDecoder[0];
	}

	@Override
	protected ALcyFileTypeDescriptor[] createModelDecoderFileTypeDescriptors() {
		return new ALcyFileTypeDescriptor[0];
	}

	@Override
	protected String[][] createDataSourceHandlerFileTypeDescriptorGroups() {
		return new String[][] { new String[] { "" } };
	}

	@Override
	protected ALcyDataSourceHandler[] createDataSourceHandlers() {
		return new ALcyDataSourceHandler[] { new ALcyDataSourceHandler() {
			@Override
			public String getDisplayName() {
				return "Twitter";
			}

			@Override
			public boolean canHandleDataSource(String aS, Object aO) {
				return true;
			}

			@Override
			public void handleDataSource(String aS, Object aO) throws IOException {
				ILcdModel model = new TwitterAPIModelDecoder("").decode(aS);

				if (aO instanceof ILcyLspMapComponent) {
					ILspView view = ((ILcyLspMapComponent) aO).getMainView();
					ILspLayer layer = new TwitterLayerFactory().createLayer(model); 
					view.addLayer(layer); 

					TLspBalloonManager balloonManager = new TLspBalloonManager(view,
							((ILspAWTView) view).getOverlayComponent(), Location.NO_LAYOUT,
							new BalloonContentProvider());

					BalloonViewSelectionListener listener = new BalloonViewSelectionListener(view, balloonManager);
					view.addLayeredListener(listener);
					view.addLayerSelectionListener(listener);
					view.getRootNode().addHierarchyPropertyChangeListener(listener);
				}
			}
		} };
	}

	@Override
	protected ALcyFileTypeDescriptor[] createDataSourceHandlerFileTypeDescriptors() {
		return new ALcyFileTypeDescriptor[] { new ALcyFileTypeDescriptor() {
			@Override
			public String getDisplayName() {
				return "Twitter Technite API";
			}

			@Override
			public boolean includes(String aS) {
				return true;
			}

			@Override
			public String getDefaultExtension() {
				return "";
			}
		} };
	}

	@Override
	protected ILcyGXYLayerTypeProvider createGXYLayerTypeProvider() {
		return null;
	}

	@Override
	protected ILcdGXYLayerFactory createGXYLayerFactory() {
		return null;
	}

}
