package technite.model;

import java.util.Set;

import com.luciad.datamodel.ILcdDataObject;
import com.luciad.model.ILcdModel;
import com.luciad.model.transformation.clustering.ILcdClusterShapeProvider;
import com.luciad.shape.ILcdBounds;
import com.luciad.shape.ILcdPoint;
import com.luciad.shape.ILcdShape;
import com.luciad.shape.shape2D.ILcd2DEditablePoint;
import com.luciad.shape.shape3D.ILcd3DEditablePoint;

//public class TweetClusterShapeProvider implements ILcdClusterShapeProvider {
//
//	@Override
//	public ILcdShape getShape(Set<Object> aComposingElements, ILcdModel aOriginalModel) {
//		ILcdDataObject biggestTweet = null;
//		for (Object element : aComposingElements) {
//			if (element instanceof ILcdDataObject) {
//				ILcdDataObject tweet = (ILcdDataObject) element;
//				if (biggestTweet == null) {
//					biggestTweet = tweet;
//					continue;
//				}
//				long qtd = (long) tweet.getValue("QTD");
//				long biggestQtd = (long) biggestTweet.getValue("QTD");
//				
//				biggestTweet = tweet;
//			}
//		}
//		
//		
//		return (ILcdPoint) ;
//	}
//}