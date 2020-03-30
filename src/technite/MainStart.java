package technite;

import com.luciad.gui.swing.TLcdOverlayLayout.Location;
import com.luciad.model.ILcdModel;
import com.luciad.util.TLcdNoBoundsException;
import com.luciad.util.TLcdOutOfBoundsException;
import com.luciad.view.lightspeed.TLspAWTView;
import com.luciad.view.lightspeed.TLspViewBuilder;
import com.luciad.view.lightspeed.layer.ILspLayer;
import com.luciad.view.lightspeed.swing.TLspBalloonManager;
import com.luciad.view.lightspeed.util.TLspViewNavigationUtil;
import com.luciad.view.swing.TLcdLayerTree;

import technite.model.TwitterAPIModelDecoder;
import technite.view.BalloonContentProvider;
import technite.view.BalloonViewSelectionListener;
import technite.view.TwitterLayerFactory;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import java.awt.BorderLayout;
import java.io.IOException;

public class MainStart {

	final static TLspAWTView view = TLspViewBuilder.newBuilder().size(800, 600).buildAWTView();

	public static void main(String[] args) {
		SwingUtilities.invokeLater(MainStart::new);
	}

	private MainStart() {

		JFrame frame = new JFrame("Technite Twitter");

		ILspLayer layer = new TwitterLayerFactory().addWorldLayer();

		frame.getContentPane().add(view.getHostComponent(), BorderLayout.CENTER);
		frame.getContentPane().add(new TLcdLayerTree(view), BorderLayout.EAST);

		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		TLspAWTView view = TLspViewBuilder.newBuilder().size(800, 600).buildAWTView();
		frame.add(view.getHostComponent());

		ILcdModel model = null;
		try {
			model = new TwitterAPIModelDecoder("").decode("");
		} catch (IOException aE) {
			aE.printStackTrace();
		}

		frame.pack();
		frame.setVisible(true);

		ILspLayer layerModelTwitter = new TwitterLayerFactory().createLayer(model);
		view.addLayer(layer);
		view.addLayer(layerModelTwitter);

		TLspBalloonManager balloonManager = new TLspBalloonManager(view, view.getOverlayComponent(), Location.NO_LAYOUT,
				new BalloonContentProvider());

		BalloonViewSelectionListener listener = new BalloonViewSelectionListener(view, balloonManager);
		view.addLayeredListener(listener);
		view.addLayerSelectionListener(listener);
		view.getRootNode().addHierarchyPropertyChangeListener(listener);

		try {
			new TLspViewNavigationUtil(view).fit(layer);
		} catch (TLcdNoBoundsException aE) {
			aE.printStackTrace();
		} catch (TLcdOutOfBoundsException aE) {
			aE.printStackTrace();
		}

	}
}
