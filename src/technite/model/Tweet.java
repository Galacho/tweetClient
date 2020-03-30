package technite.model;

import static technite.model.TwitterDataModel.*;

import com.luciad.datamodel.ILcdDataObject;
import com.luciad.datamodel.TLcdDataObject;
import com.luciad.datamodel.TLcdDataProperty;
import com.luciad.datamodel.TLcdDataType;
import com.luciad.shape.shape2D.TLcdLonLatPolyline;




public class Tweet extends TLcdLonLatPolyline implements ILcdDataObject {

	private final TLcdDataObject delegate = new TLcdDataObject(TwitterDataModel.TWITTER_TYPE);

	/**
	 * @param QTD  quantidade de Tweets
	 * @param aLon Longitude of the tweet
	 * @param aLat Latitude of the tweet
	 * @param Text Text of tweey
	 */

	public Tweet(long Qtd, double aLon, double aLat, String Text) {
		
		//System.out.println("Qtd: "+Qtd);
		delegate.setValue(TWITTER_QTD, Qtd);
		insert2DPoint(0, aLon, aLat);
		setValue(TWITTER_TEXT, Text);
	}
	
	  public long getQtd() {
		    //System.out.println("TWITTER_QTD: "+TWITTER_QTD);
		    return ( long ) getValue( TWITTER_QTD );
		  }
	  
	  public String getText() {
		  //System.out.println("TWITTER_TEXT: "+TWITTER_TEXT);
		  return (String) getValue ( TWITTER_TEXT );
	  }
	  

	  @Override
	  public TLcdDataType getDataType() {
	    return delegate.getDataType();
	  }

	  @Override
	  public Object getValue( TLcdDataProperty aProperty ) {
	    return delegate.getValue( aProperty );
	  }

	  @Override
	  public Object getValue( String aS ) {
	    return delegate.getValue( aS );
	  }

	  @Override
	  public void setValue( TLcdDataProperty aProperty, Object aO ) {
	    delegate.setValue( aProperty, aO );
	  }

	  @Override
	  public void setValue( String aS, Object aO ) {
	    delegate.setValue( aS, aO );
	  }

	  @Override
	  public boolean hasValue( TLcdDataProperty aProperty ) {
	    return delegate.hasValue( aProperty );
	  }

	  @Override
	  public boolean hasValue( String aS ) {
	    return delegate.hasValue( aS );
	  }

}
