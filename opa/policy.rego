package access

default allow = false

allow {
    input.user.onCall
    input.user.domain == input.resource.domain
}

allow {
    input.user.domain == "A"
    input.requested == "read_write"
    input.user.leaderApproved
}

allow {
    some group
    group := input.user.groups[_]
    resource := data.group_permissions[group][_]
    resource == input.resource.name
}
