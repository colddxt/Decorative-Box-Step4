/*
* Author    Joseph Newcomer
* Version   1.0 
* History   Updated By      Update Date     Update Reason
*           ==============  ===========     ================================================
*/
public class BoxException extends Exception {
     private String FileName;
     private int line;
     private String text;
     
     public String GetFileName() { return FileName; }
     public int GetLine() { return line; }
     
     /* 
      * The default constructor is required by Java, but will never, ever be used
      * If I can figure out how to declare an abstract superclass, all will be well
      */
     public BoxException() {
         FileName = null;
         line = 0;
         text = null;
     }
     
     public BoxException(String f) {
         FileName = f;
         line = 0;
         text = null;
     } // BoxException(String);
     
     public BoxException(String f, int L) {
         FileName = f;
         line = L;
         text = "";
         } // BoxException(String, int)
         
     public BoxException(String f, int L, String t) {
         FileName = f;
         line = L;
         text = t; 
         } // BoxException(String, int, String);
         
     public String where() {
         if(FileName == null)
             if(text == null)
                 return "";
             else
                 return "";
         
         if(line == 0)
             return FileName;
         
         return FileName + "(" + Integer.toString(line) + "): ";
         } // where
     
     public String why() {
         return text;
         } // why
     
     public String toString() {
         return where() + why();
     } // toString
 } // class BoxException