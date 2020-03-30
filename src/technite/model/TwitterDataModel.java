package technite.model;

import com.luciad.datamodel.*;

public class TwitterDataModel {
	public static final TLcdDataModel DATA_MODEL;

	public static final TLcdDataType TWITTER_TYPE;

	// These are the properties of our objects. They have an ID and a Line number,
	// currently.
	public static final String TWITTER_QTD = "QTD"; 
	public static final String TWITTER_TEXT = "TEXT";

	static {
		//System.out.println("Twitter API");
		TLcdDataModelBuilder builder = new TLcdDataModelBuilder("Twitter API");

		//System.out.println("Tweet");
		TLcdDataTypeBuilder tweet = builder.typeBuilder("Tweet");
		// Register each property.
		tweet.addProperty(TWITTER_QTD, TLcdCoreDataTypes.LONG_TYPE).nullable(false);
		tweet.addProperty(TWITTER_TEXT, TLcdCoreDataTypes.STRING_TYPE).nullable(false);

		DATA_MODEL = builder.createDataModel();
		TWITTER_TYPE = DATA_MODEL.getDeclaredType("Tweet");
	}

}
