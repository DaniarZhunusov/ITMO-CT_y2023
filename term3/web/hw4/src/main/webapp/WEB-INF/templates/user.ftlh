<#-- @ftlvariable name="user_id" type="java.lang.Long" -->
<#-- @ftlvariable name="handle" type="java.lang.String" -->
<#import "./common.ftlh" as c>

<@c.page>
    <#if handle??>
        <#assign uh = c.findBy(users, "handle", handle)!/>
        <@c.userprofile uh/>
    <#elseif user_id?? && user_id?is_number>
        <#assign ui = c.findBy(users, "id", user_id)!/>
        <@c.userprofile ui/>
    <#else>
        No such user.
    </#if>
</@c.page>

