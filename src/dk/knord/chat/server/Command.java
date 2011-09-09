package dk.knord.chat.server;

/**
 * Container class for a command from the client 
 *
 */
public class Command {
	public final String name;
	public String extra = null;
	public String content = null;
	
	/**
	 * Constructor
	 * @param name The name of the command
	 * @param extra Any extra information that the command might contain [e.g. alias, target, source, ...], null otherwise
	 * @param content Any content that the command might have [e.g. message, alias list], null otherwise
	 */
	public Command(String name){
		this.name = name;
	}
}
