package technite.view;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.luciad.view.swing.ALcdBalloonDescriptor;
import com.luciad.view.swing.ILcdBalloonContentProvider;

import technite.model.Tweet;



public class BalloonContentProvider implements ILcdBalloonContentProvider {
	  @Override
	  public boolean canGetContent( ALcdBalloonDescriptor aDescriptor ) {
	    return aDescriptor.getObject() instanceof Tweet;
	  }

	  @Override
	  public JComponent getContent( ALcdBalloonDescriptor aDescriptor ) {
		
		Tweet t = ( Tweet ) aDescriptor.getObject();

	    //double length = t.getLength2D( 0, 1 );

		Long qtd = t.getQtd();
		String text = t.getText();
		//System.out.println("qtd:" + qtd);
		
	    JPanel panel = new JPanel(  );
	    panel.add( new JLabel( "Tweets: " ) );
	    panel.add( new JLabel( String.format("%s", qtd) ) );
	    panel.add( new JLabel( "Tweet : " + text ) );
	    panel.add( new JLabel( String.format("%s", text) ) );

	    return panel; 
	  }
}
