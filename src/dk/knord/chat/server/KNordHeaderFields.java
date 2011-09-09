package dk.knord.chat.server;

public class KNordHeaderFields {

	public static class Requests {
		public static final String Connect = "CONNECT";
		public static final String Disconnect = "DISCONNECT";
		public static final String Message = "MESSAGE";
		public static final String MessageAll = "MESSAGE ALL";
		public static final String List = "LIST";
		public static final String Unkown = "UNKNOWN";
		public static final String Unsupported = "UNSUPPORTED";
	}

	public static class Responses {
		public static final String Connect = "CONNECT";
		public static final String Disconnect = "DISCONNECT";
		public static final String Message = "MESSAGE";
		public static final String List = "LIST";
		public static final String Unknown= "UNKNOWN";
		public static final String Unsupported = "UNSUPPORTED";
		public static final String NoSuchAlias = "NO SUCH ALIAS";
	}

	public static class KeyWords {
		public static final int All = 0;
		
		public static final String[] keyWords = new String[] { "ALL" };

		public static boolean isKeyWord(String input) {
			input = input.trim();

			for (String s : keyWords) {
				if(s.equals(input))
					return true;
			}
			
			return false;
		}
	}
}