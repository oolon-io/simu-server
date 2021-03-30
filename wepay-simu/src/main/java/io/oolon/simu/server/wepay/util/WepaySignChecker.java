package io.oolon.simu.server.wepay.util;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class WepaySignChecker {
	public void check(HttpHeaders header) {
		//TODO 如果以后检查不通过，通过ThrowException
	}

}
