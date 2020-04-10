package technite.model;

import java.util.Set;
import static technite.model.TwitterDataModel.*;

///// https://dev.luciad.com/portal/productDocumentation/LuciadLightspeed/docs/articles/tutorial/model/clustering/model_clustering.html#_configuring_a_tlcdclusteringtransformer

import com.luciad.datamodel.ILcdDataObject;
import com.luciad.model.ILcdModel;
import com.luciad.model.transformation.clustering.ILcdClusterShapeProvider;
import com.luciad.shape.ILcdBounds;
import com.luciad.shape.ILcdPoint;
import com.luciad.shape.ILcdShape;
import com.luciad.shape.shape2D.ILcd2DEditablePoint;
import com.luciad.shape.shape3D.ILcd3DEditablePoint;

public class TweetClusterShapeProvider implements ILcdClusterShapeProvider {

	@Override
	public ILcdShape getShape(Set<Object> aComposingElements, ILcdModel aOriginalModel) {
		ILcdDataObject biggestTweet = null;
		
		System.out.println("Estou aqui ...  TweetClusterShapeProvider ILcdShape getShape ");
		long val=0;
		for (Object element : aComposingElements) {
			if (element instanceof ILcdDataObject) {
				ILcdDataObject tweet = (ILcdDataObject) element;
				
				if (biggestTweet == null) {
					biggestTweet = tweet;
					continue;
				}
				long TweetQtd = (long) tweet.getValue(TWITTER_QTD);
				long biggestTweetQtd = (long) biggestTweet.getValue(TWITTER_QTD);
				val += TweetQtd;
				if (TweetQtd > biggestTweetQtd) {
					biggestTweet = tweet;
				}
			}
		}
		
		System.out.println("Maior valor:"+biggestTweet.getValue(TWITTER_QTD));
		System.out.println("Acumulado : "+ val);
		biggestTweet.setValue(TWITTER_QTD, val);
		System.out.println("Maior tweet : "+biggestTweet.getValue(TWITTER_POSITION));
		
		return (ILcdPoint) biggestTweet.getValue(TWITTER_POSITION);
	}

}