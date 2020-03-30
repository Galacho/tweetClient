package technite.model;

import java.util.Collections;

import com.luciad.model.TLcdDataModelDescriptor;

public class TwitterModelDescriptor extends TLcdDataModelDescriptor {

	public TwitterModelDescriptor(String aSourceName) {
		super(aSourceName, "Twitter Tracks", "Twitter API", TwitterDataModel.DATA_MODEL,
				Collections.singleton(TwitterDataModel.TWITTER_TYPE),
				Collections.singleton(TwitterDataModel.TWITTER_TYPE));
	}

}
