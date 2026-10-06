package com.winitech.system.domain.authorizationGroup;

import java.util.UUID;

public interface AuthorizationGroupStore {
	AuthorizationGroup store(AuthorizationGroup authorizationGroup);
	AuthorizationGroup modify(AuthorizationGroup authorizationGroup, AuthorizationGroupCommand authorizationGroupCommand);
}
