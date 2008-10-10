package jshm.sh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import jshm.GameSeries;
import jshm.exceptions.ClientException;
import jshm.sh.client.HttpForm;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

public class Forum {
	static final Logger LOG = Logger.getLogger(Forum.class.getName());
	
	public static enum PostMode {
		NEW("newtopic", "f"),
		REPLY("reply", "t"),
		EDIT("editpost", "p"),
		DELETE("delete", "p");
		
		public final String value;
		public final String idName;
		
		private PostMode(String value, String idName) {
			this.value = value;
			this.idName = idName;
		}
	}
	
	public static void post(GameSeries series, PostMode mode, int postId, String body) throws Exception {
		post(series, mode, postId, "", body);
	}
	
	public static void post(GameSeries series, PostMode mode, int postId, String subject, String body) throws Exception {
		post(series, mode, postId, subject, body, false, false, true, true);
	}
	
	public static void post(
		GameSeries series,
		PostMode mode,
		int postId,
		String subject, String body,
		boolean disableHtml,
		boolean disableSmilies,
		boolean attachSignature,
		boolean notifyOnReply) throws Exception {
		
		Client.getAuthCookies();
		
		String url = URLs.forum.getPostUrl(series, mode, postId);
		Client.makeHeadRequest(url);
		
		String[] staticData = {
			"subject", subject,
			"message", body,
			"mode", mode.value,
			"p", String.valueOf(postId),
			"sid", Client.getPhpBb2MySqlSid(),
			"post", "Submit"
		};
		
		List<String> data = new ArrayList<String>(Arrays.asList(staticData));
		
		if (disableHtml) {
			data.add("disable_html"); data.add("on");
		}
		
		if (disableSmilies) {
			data.add("disable_smilies"); data.add("on");
		}
		
		if (attachSignature) {
			data.add("attach_sig"); data.add("on");
		}
		
		if (notifyOnReply) {
			data.add("notify"); data.add("on");
		}
		
		new HttpForm((Object) url, data) {
			@Override
			public void afterSubmit(final int response, final HttpClient client, final HttpMethod method) throws Exception {
				String body = method.getResponseBodyAsString();
				method.releaseConnection();
				
				if (body.contains("Your message has been entered successfully.")) {
					LOG.fine("Posted successfully");
				} else {
					int start = -1, end = -1;
					String needle = "<td align=\"center\"><span class=\"gen\">";
					
					start = body.indexOf(needle);
					
					ClientException e = null;
					
					if (start >= 0) {
						start += needle.length();
						needle = "</span></td>";
						end = body.indexOf(needle, start);
					} else {
						needle = "<form ";
						start = body.indexOf(needle);
						needle = "</form>";
						end = body.indexOf("</form>") + needle.length();
						
						e = new ClientException("Unknown error while editing post");
					}
					
					String msg = body.substring(start >= 0 ? start : 0, end >= 1 ? end : body.length());
					
					LOG.finest("editPost() response:");
					LOG.finest(msg);
					
					if (null == e) {
						e = new ClientException(msg);
					}
					
					LOG.throwing("Forum", "editPost", e);
					throw e;
				}
			}
		}.submit();
	}
}
