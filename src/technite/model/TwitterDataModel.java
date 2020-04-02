package technite.model;

import com.luciad.datamodel.*;
import com.luciad.shape.ILcdPoint;

public class TwitterDataModel {
	public static final TLcdDataModel DATA_MODEL;

	public static final TLcdDataType TWITTER_TYPE;

	// These are the properties of our objects. They have an ID and a Line number,
	// currently.
	public static final String TWITTER_QTD = "QTD"; 
	public static final String TWITTER_TEXT = "TEXT";
	
	public static final String TWITTER_POINT_LNG = "LNG";
	public static final String TWITTER_POINT_LAT = "LAT";
	
	public static final String TWITTER_IDX = "IDX";
	
	
	public static final String TWITTER_HASHTAG = "HASHTAG";
	

	static {
		//System.out.println("Twitter API");
		TLcdDataModelBuilder builder = new TLcdDataModelBuilder("Twitter Technite API");

		//System.out.println("Tweet");
		TLcdDataTypeBuilder tweet = builder.typeBuilder("Tweet");
		// Register each property.
		tweet.addProperty(TWITTER_QTD, TLcdCoreDataTypes.LONG_TYPE).nullable(false);
		tweet.addProperty(TWITTER_TEXT, TLcdCoreDataTypes.STRING_TYPE).nullable(false);
		tweet.addProperty(TWITTER_HASHTAG, TLcdCoreDataTypes.STRING_TYPE).nullable(false);
		tweet.addProperty(TWITTER_IDX, TLcdCoreDataTypes.STRING_TYPE).nullable(false);
		
		
		tweet.addProperty(TWITTER_POINT_LNG, TLcdCoreDataTypes.DOUBLE_TYPE ).nullable(false);
		tweet.addProperty(TWITTER_POINT_LAT, TLcdCoreDataTypes.DOUBLE_TYPE ).nullable(false);
		

		DATA_MODEL = builder.createDataModel();
		TWITTER_TYPE = DATA_MODEL.getDeclaredType("Tweet");
	}

}
