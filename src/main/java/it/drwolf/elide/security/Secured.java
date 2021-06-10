package it.drwolf.elide.security;

import java.security.Principal;

public interface Secured<U extends Principal> {
	boolean isReadable(U principal);

	boolean isWritable(U principal);
}
