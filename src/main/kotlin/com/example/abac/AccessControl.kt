package com.example.abac

enum class Permission { READ, WRITE, READ_WRITE }

data class User(
    val name: String,
    val domain: String,
    val onCall: Boolean = false,
    val leaderApproved: Boolean = false,
    val groups: Set<String> = emptySet()
)

data class Resource(
    val name: String,
    val domain: String
)

data class AccessRequest(
    val user: User,
    val resource: Resource,
    val requested: Permission
)

/**
 * Attribute-based access control system implementing simple policies.
 */
class ABACSystem(
    /**
     * Mapping from group name to resources the group can access.
     * The permission granted equals the requested permission.
     */
    private val groupPermissions: Map<String, Set<String>> = emptyMap()
) {
    /**
     * Evaluates whether a request should be granted.
     */
    fun isAccessAllowed(request: AccessRequest): Boolean {
        val user = request.user
        val resource = request.resource

        // Policy 1: On-call engineer can access resources in their own domain.
        if (user.onCall && user.domain == resource.domain) {
            return true
        }

        // Policy 2: Domain A engineers require leader approval for read-write access.
        if (
            user.domain == "A" &&
            request.requested == Permission.READ_WRITE &&
            user.leaderApproved
        ) {
            return true
        }

        // Policy 3: Group-based permissions.
        for (group in user.groups) {
            val allowedResources = groupPermissions[group] ?: continue
            if (resource.name in allowedResources) {
                return true
            }
        }

        return false
    }
}
