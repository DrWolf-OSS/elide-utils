package it.drwolf.elide.security;

import com.yahoo.elide.core.security.User;
import com.yahoo.elide.core.security.checks.UserCheck;

public class TrueCheck extends UserCheck {
	@Override
	public boolean ok(User user) {
		return true;
	}
}
