package com.example.abac

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AccessControlTest {
    @Test
    fun `oncall engineer gets access to own domain`() {
        val user = User(name = "alice", domain = "payments", onCall = true)
        val resource = Resource(name = "payments_db", domain = "payments")
        val request = AccessRequest(user, resource, Permission.READ_WRITE)
        val abac = OpaAccessControl("opa/policy.rego", "opa/data.json")
        assertTrue(abac.isAccessAllowed(request))
    }

    @Test
    fun `domain A engineer needs leader approval for RW`() {
        val user = User(name = "bob", domain = "A", leaderApproved = true)
        val resource = Resource(name = "domainA_db", domain = "A")
        val request = AccessRequest(user, resource, Permission.READ_WRITE)
        val abac = OpaAccessControl("opa/policy.rego", "opa/data.json")
        assertTrue(abac.isAccessAllowed(request))
    }

    @Test
    fun `access granted via group`() {
        val user = User(name = "carol", domain = "B", groups = setOf("analytics"))
        val resource = Resource(name = "metabase", domain = "analytics")
        val request = AccessRequest(user, resource, Permission.READ)
        val abac = OpaAccessControl("opa/policy.rego", "opa/data.json")
        assertTrue(abac.isAccessAllowed(request))
    }

    @Test
    fun `deny when no policy matches`() {
        val user = User(name = "dave", domain = "B")
        val resource = Resource(name = "metabase", domain = "analytics")
        val request = AccessRequest(user, resource, Permission.READ)
        val abac = OpaAccessControl("opa/policy.rego", "opa/data.json")
        assertFalse(abac.isAccessAllowed(request))
    }
}
