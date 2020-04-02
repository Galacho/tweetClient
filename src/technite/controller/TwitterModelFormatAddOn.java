package technite.controller;

import com.luciad.lucy.addons.ALcyFormatAddOn;
import com.luciad.lucy.format.ALcyFormat;
import com.luciad.lucy.format.TLcySafeGuardFormatWrapper;
import com.luciad.lucy.util.ALcyTool;

public class TwitterModelFormatAddOn extends ALcyFormatAddOn {

	public TwitterModelFormatAddOn() {
		super(ALcyTool.getLongPrefixWithClassName(TwitterModelFormatAddOn.class),
				ALcyTool.getShortPrefix(TwitterModelFormatAddOn.class));
		
		System.out.println("TwitterModelFormatAddOn");
		
	}

	@Override
	protected ALcyFormat createBaseFormat() {
		System.out.println("createBaseFormat");
		return new TwitterModelFormat(getLucyEnv(), getLongPrefix(), getShortPrefix(), getPreferences());
	}

	@Override
	protected ALcyFormat createFormatWrapper(ALcyFormat aFormat) {
		System.out.println("createFormatWrapper");
		return new TLcySafeGuardFormatWrapper(aFormat);
	}
}
