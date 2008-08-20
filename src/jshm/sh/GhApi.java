package jshm.sh;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.hibernate.Session;
import org.hibernate.Transaction;

import jshm.Score;
import jshm.exceptions.ClientException;
import jshm.gh.GhScore;
import jshm.hibernate.HibernateUtil;
import jshm.sh.client.HttpForm;

public class GhApi {
	static final Logger LOG = Logger.getLogger(GhApi.class.getName());

	static final Pattern ERROR_PATTERN =
		Pattern.compile("^.*<span class=\"error\">(.*?)</span>.*$", Pattern.DOTALL);
	
	public static void submitGhScore(final GhScore score) throws Exception {
		Client.getAuthCookies();
		
		new HttpForm((Object) URLs.gh.getInsertScoreUrl(score),
			"song", String.valueOf(score.getSong().getScoreHeroId()),
			"score", String.valueOf(score.getScore()),
			"stars", score.getRating() != 0 ? String.valueOf(score.getRating()) : "",
			"percent", score.getHitPercent() != 0.0f ? String.valueOf((int) (score.getHitPercent() * 100)) : "",
			"streak", score.getStreak() != 0 ? String.valueOf(score.getStreak()) : "",
			"comment", score.getComment(),
			"link", score.getImageUrl(),
			"videolink", score.getVideoUrl()) {
			
			@Override
			public void afterSubmit(final int response, final HttpClient client, final HttpMethod method) throws Exception {
				// TODO figure out what scorehero does upon receiving invalid data
				
				String body = method.getResponseBodyAsString();
				
				LOG.finest("submitGhScore() result body:");
				LOG.finest("\n" + body);
				method.releaseConnection();
				
				Matcher m = ERROR_PATTERN.matcher(body);
				
				if (m.matches()) {
					Exception e = new ClientException(m.group(1));
					LOG.throwing("GhApi", "submitGhScore", e);
					throw e;
				}
				
				// can't be completely sure about this
//				if (body.contains("Your score has been submitted"))
				
				score.setStatus(Score.Status.SUBMITTED);
				score.setSubmissionDate(new java.util.Date());
				
				Session sess = null;
				Transaction tx = null;
				
				try {
					sess = HibernateUtil.getCurrentSession();
					tx = sess.beginTransaction();
					sess.update(score);
					sess.getTransaction().commit();
				} catch (Exception e) {
					if (null != tx) tx.rollback();
					LOG.throwing("GhApi", "submitGhScore", e);
					throw e;
				} finally {
					if (sess.isOpen()) sess.close();
				}
			}
		}.submit();
	}
}
