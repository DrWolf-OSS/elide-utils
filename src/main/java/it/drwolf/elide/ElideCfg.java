package it.drwolf.elide;

import com.yahoo.elide.Elide;
import com.yahoo.elide.ElideSettingsBuilder;
import com.yahoo.elide.core.audit.Slf4jLogger;
import com.yahoo.elide.core.dictionary.EntityDictionary;
import com.yahoo.elide.core.filter.dialect.RSQLFilterDialect;
import com.yahoo.elide.core.security.checks.Check;
import com.yahoo.elide.datastores.hibernate5.HibernateSessionFactoryStore;
import org.hibernate.SessionFactory;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;


public abstract class ElideCfg {

	public static final String API_VERSION = "1.0";

	private Elide elide;

	@SuppressWarnings("rawtypes")
	@Inject
	public ElideCfg(JPAApi jpaApi) {
		this.elide = jpaApi.withTransaction(entityManager -> {
			SessionFactory sessionFactory = ((org.hibernate.Session) entityManager.getDelegate()).getSessionFactory();
			EntityDictionary dictionary = new EntityDictionary(getChecks());
			return new Elide(new ElideSettingsBuilder(
					new HibernateSessionFactoryStore.Builder(sessionFactory).build()).withAuditLogger(new Slf4jLogger())
					.withEntityDictionary(dictionary)
					.withJoinFilterDialect(new RSQLFilterDialect(dictionary))
					.withSubqueryFilterDialect(new RSQLFilterDialect(dictionary))
					.build());
		});

	}
	protected abstract Map<String, Class<? extends Check>> getChecks();
	public Elide getElide() {
		return this.elide;
	}



}
