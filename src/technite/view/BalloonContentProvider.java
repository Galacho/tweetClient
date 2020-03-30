package technite.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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
	    GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add( new JLabel( "Tweets: "+ qtd ),gbc );
        
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add( new JLabel( "Tweet : " + text ) ,gbc);
        


	    return panel; 
	  }
}
