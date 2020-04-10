package technite.model;

import com.luciad.datamodel.ILcdDataObject;
import com.luciad.model.transformation.clustering.ILcdClassifier;
import static technite.model.TwitterDataModel.*;

public class TwitterClassifier implements ILcdClassifier {

	public static final String NO_CLASSIFICATION = "";

	@Override
	public String getClassification(Object aObject) {
		if (aObject instanceof ILcdDataObject) {
			ILcdDataObject tweet = (ILcdDataObject) aObject;
			return tweet.getValue(TWITTER_HASHTAG).toString();
		}
		return NO_CLASSIFICATION;
	}

}