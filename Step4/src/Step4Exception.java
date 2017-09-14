/*
* Author    Xingtian Dong
* Version   1.0 
* History   Updated By      Update Date     Update Reason
*           ==============  ===========     ================================================
*/

public class Step4Exception extends BoxException{
    /***********************************************************************************
     * Exceptions for step4
     ***********************************************************************************/

	public Step4Exception(String f, int L, String s){
		super(f,L,s);
	}
}
	
	class wrongUnitTestException extends Step4Exception{
		public wrongUnitTestException(String f, int L, String s){
			super(f,L,s);
		}
	}
	
	class invalidLineException extends Step4Exception{
		public invalidLineException(String f, int L, String s){
			super(f,L,s);
		}
	}
	
	class invalidInitialPrismException extends Step4Exception{           //Exception in DPrism
		public invalidInitialPrismException(String f, int L, String s){
			super(f,L,s);
		}
	}
	
	class noSuchLineInSurfaceException extends Step4Exception{           //Exception in DSurface
		public noSuchLineInSurfaceException(String f, int L, String s){
			super(f,L,s);
		}
	}

