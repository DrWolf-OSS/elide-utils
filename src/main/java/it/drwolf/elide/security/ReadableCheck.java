package it.drwolf.elide.security;

import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.checks.OperationCheck;

import java.util.Optional;

public class ReadableCheck extends OperationCheck<Secured> {
	@Override
	public boolean ok(Secured object, RequestScope requestScope, Optional<ChangeSpec> changeSpec) {
		return object.isReadable(requestScope.getUser().getPrincipal());
	}
}
