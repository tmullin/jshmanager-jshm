package jshm.sh;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.hibernate.Session;
import org.hibernate.Transaction;

import jshm.Part;
import jshm.Score;
import jshm.exceptions.ClientException;
import jshm.gh.GhScore;
import jshm.hibernate.HibernateUtil;
import jshm.rb.RbScore;
import jshm.sh.client.HttpForm;

public class Api {
	static final Logger LOG = Logger.getLogger(Api.class.getName());

	static final Pattern ERROR_PATTERN =
		Pattern.compile("^.*<span class=\"error\">(.*?)</span>.*$", Pattern.DOTALL);
	
	
	public static void submitScore(final Score score) throws Exception {
		if (score instanceof GhScore) {
			submitGhScore((GhScore) score);
		} else if (score instanceof RbScore) {
			submitRbScore((RbScore) score);
		} else {
			throw new IllegalArgumentException("invalid score");
		}
	}

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
				// TODO more vigorous error handing?
				
				String body = method.getResponseBodyAsString();
				
//				LOG.finest("submitGhScore() result body:");
//				LOG.finest("\n" + body);
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
					LOG.throwing("Api", "submitGhScore", e);
					throw e;
				} finally {
					if (sess.isOpen()) sess.close();
				}
			}
		}.submit();
	}
	
	public static void submitRbScore(final RbScore score) throws Exception {
		Client.getAuthCookies();
		
		String[] staticData = new String[] {
			"song", String.valueOf(score.getSong().getScoreHeroId()),
			"game", String.valueOf(score.getGame().scoreHeroId),
			"platform", String.valueOf(RbPlatform.getId(score.getGame().platform)),
			"group", String.valueOf(score.getGroup().id),
			"score", String.valueOf(score.getScore()),
			"rating", score.getRating() != 0 ? String.valueOf(score.getRating()) : "",
			"comment", score.getComment(),
			"link", score.getImageUrl(),
			"videolink", score.getVideoUrl()	
		};
		
		List<String> data = new ArrayList<String>(Arrays.asList(staticData));
		
		// TODO check on xxxUser being required or not
		for (Part p : score.getParts()) {
			final String istr = p.getInstrument().toShortString();
			data.add(istr + "Diff");
			data.add(String.valueOf(p.getDifficulty().scoreHeroId));
			data.add(istr + "Name");
			data.add(p.getPerformer());
			data.add(istr + "Percent");
			data.add(p.getHitPercent() != 0.0f ? String.valueOf((int) (p.getHitPercent() * 100)) : "");
			data.add(istr + "Streak");
			data.add(p.getStreak() != 0 ? String.valueOf(p.getStreak()) : "");
		}
		
		new HttpForm((Object) URLs.rb.getInsertScoreUrl(score), data.toArray(new String[] {})) {
			@Override
			public void afterSubmit(final int response, final HttpClient client, final HttpMethod method) throws Exception {
				// TODO more vigorous error handing?
				
				String body = method.getResponseBodyAsString();
				
//				LOG.finest("submitGhScore() result body:");
//				LOG.finest("\n" + body);
				method.releaseConnection();
				
				Matcher m = ERROR_PATTERN.matcher(body);
				
				if (m.matches()) {
					Exception e = new ClientException(m.group(1));
					LOG.throwing("Api", "submitRbScore", e);
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
					tx.commit();
				} catch (Exception e) {
					if (null != tx) tx.rollback();
					LOG.throwing("Api", "submitGhScore", e);
					throw e;
				} finally {
					if (sess.isOpen()) sess.close();
				}
			}
		}.submit();
	}
}