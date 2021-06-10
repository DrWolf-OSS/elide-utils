package it.drwolf.elide;

import com.fasterxml.jackson.databind.JsonNode;
import com.yahoo.elide.Elide;
import com.yahoo.elide.ElideResponse;
import com.yahoo.elide.core.security.User;
import it.drwolf.base.interfaces.Loggable;
import it.drwolf.jpa.RevisionJPAApi;
import it.drwolf.jwt.JWTUtils;
import it.drwolf.jwt.LoggedIn;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonApi extends Controller implements Loggable {

	private final Elide elide;
	protected Class<? extends Principal> userClass = Principal.class;

	@Inject
	private JWTUtils jwtUtils;
	@Inject
	private RevisionJPAApi jpaApi;

	@Inject
	public JsonApi(ElideCfg elideCfg) {
		this.elide = elideCfg.getElide();
	}

	public Result delete(String path, Http.Request request) {
		this.logMethod(path, request);
		return this.jpaApi.withTransaction(request,
				em -> this.end(this.elide.delete(null, path, null, getUser(request), ElideCfg.API_VERSION)));
	}

	public Result end(ElideResponse response) {
		return response.getBody() == null ?
				Results.noContent() :
				Results.status(response.getResponseCode(), response.getBody()).as("application/json;charset=utf-8");
	}

	public Result get(String path, Http.Request request) {
		MultivaluedMap<String, String> mvm = new MultivaluedHashMap<>();
		for (String k : request.queryString().keySet()) {

			List<String> values = Arrays.asList(request.queryString().get(k))
					.stream()
					.filter(s -> !"".equals(s))
					.collect(Collectors.toList());
			mvm.put(k, values);

		}
		return this.jpaApi.withTransaction(request,
				em -> this.end(this.elide.get(null, path, mvm, this.getUser(request), ElideCfg.API_VERSION)));
	}

	public JsonNode getEntityList(Http.Request request, String entityName, List<? extends Object> ids, String include,
			String sort) {

		MultivaluedMap<String, String> mvm = new MultivaluedHashMap<>();
		if (sort == null) {
			sort = "id";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("filter[").append(entityName).append("]");

		String k = sb.toString();
		sb.setLength(0);
		sb.append("id=in=");
		sb.append("(").append(StringUtils.join(ids, ",")).append(")");
		String v = sb.toString();

		mvm.add(k, v);
		mvm.add("sort", sort);
		mvm.add("include", include);

		return Json.parse(this.elide.get(null, entityName, mvm, this.getUser(request), ElideCfg.API_VERSION).getBody());

	}

	public JsonNode getEntityList(String entityName, String idName, List<? extends Object> ids, String[] include,
			String sort, User user, String pageNumber, String pageSize, String pageTotals) {

		MultivaluedMap<String, String> mvm = new MultivaluedHashMap<>();
		if (sort == null) {
			sort = "id";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("filter[").append(entityName).append("]");
		String k = sb.toString();
		sb.setLength(0);
		sb.append(idName + "=in=");
		if (ids.isEmpty()) {
			ids = Arrays.asList(0);
		}
		sb.append("(").append(StringUtils.join(ids, ",")).append(")");
		String v = sb.toString();

		mvm.add(k, v);
		mvm.add("sort", sort);
		if (include != null && include.length != 0) {
			Stream.of(include).forEach(i -> mvm.add("include", i));
		}
		if (pageSize != null && !pageSize.isEmpty()) {
			mvm.add("page[size]", pageSize);
		}
		if (pageNumber != null && !pageNumber.isEmpty()) {
			mvm.add("page[number]", pageNumber);
		}
		if (pageTotals != null) {
			mvm.add("page[totals]", pageTotals);
		}
		return Json.parse(this.elide.get(null, entityName, mvm, user, ElideCfg.API_VERSION).getBody());

	}

	protected User getUser(Http.Request request) {
		return new User(Json.fromJson(this.jwtUtils.getUser(request), userClass));
	}

	private void logMethod(String path, Http.Request request) {
		this.logger().info("{}: {} {}", this.getUser(request).getName(), request.method(), path);
	}

	public Result patch(String path, Http.Request request) {
		this.logMethod(path, request);
		String header = this.safeGetHeader("Content-type", request);
		String headerAccept = this.safeGetHeader("Accept", request);
		return this.jpaApi.withTransaction(request, em -> this.end(
				this.elide.patch(null, header, headerAccept, path, request.body().asJson().toString(),
						this.getUser(request), ElideCfg.API_VERSION)));

	}

	public Result post(String path, Http.Request request) {
		this.logMethod(path, request);
		return this.jpaApi.withTransaction(request, em -> this.end(
				this.elide.post(null, path, request.body().asJson().toString(), this.getUser(request),
						ElideCfg.API_VERSION)));
	}

	private String safeGetHeader(String key, Http.Request request) {
		return request.header(key).orElse("");
	}
}
