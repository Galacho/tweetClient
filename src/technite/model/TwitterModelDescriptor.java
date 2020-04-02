package technite.model;

import java.util.Collections;

import com.luciad.model.TLcdDataModelDescriptor;

public class TwitterModelDescriptor extends TLcdDataModelDescriptor {

	public TwitterModelDescriptor(String aSourceName) {
		// aqui define o datamodel e coloca o nome na opcao lateral
		super(aSourceName, "Twitter Tracks", "Twitter Technite API", TwitterDataModel.DATA_MODEL,
				Collections.singleton(TwitterDataModel.TWITTER_TYPE),
				Collections.singleton(TwitterDataModel.TWITTER_TYPE));
	}

}
