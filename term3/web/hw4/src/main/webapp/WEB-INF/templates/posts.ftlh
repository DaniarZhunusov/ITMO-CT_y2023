<#-- @ftlvariable name="user_id" type="java.lang.Long" -->
<#import "./common.ftlh" as c>

<#if user_id?? && user_id?is_number>
    <@c.page>
        <@c.show_posts_user_id user_id "false" "middle"/>
    </@c.page>
<#else>
    <@c.page>Not found any posts by this user_id</@c.page>
</#if>