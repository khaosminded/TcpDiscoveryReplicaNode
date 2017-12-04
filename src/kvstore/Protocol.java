package kvstore;

/**
 * Protocol constants
 *
 */
public class Protocol {
	static enum Operation{GET,PUT,DEL,STORE,EXIT,DPUT1,DPUT2,DPUTABORT,
           DDEL1,DDEL2,DDELABORT,DPUTINIT,DDELINIT;}
        static  enum TYPE{RKVSTORE,MBPSTORE,CLIENT};
        public static final String ACK_MESSAGE_SUCCESS="SUCCESS";
	public static final String ACK_MESSAGE_ABORT="ABORT";
        public static final String SERVER_ROLE = "ts";
	public static final String CLIENT_ROLE = "tc";
}
